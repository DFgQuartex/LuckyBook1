package ru.lucky_book.features.order_screen;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.rm.rmswitch.RMSwitch;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.xiaochen.progressroundbutton.AnimDownloadProgressButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.app.Preferences;
import ru.lucky_book.data.Address;
import ru.lucky_book.data.Contact;
import ru.lucky_book.data.Order;
import ru.lucky_book.data.OrderId;
import ru.lucky_book.data.SuccessOrderResponse;
import ru.lucky_book.data.evenbus.UploadEvent;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.entities.OrderData;
import ru.lucky_book.entities.spsr.CostResponseBody;
import ru.lucky_book.features.preview_screen.PreviewActivity;
import ru.lucky_book.features.upload.UploadService;
import ru.lucky_book.network.repository.ServiceRepository;
import ru.lucky_book.network.robospice.CreateInvoiceSpiceTask;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.spice.LuckyUncachedSpiceService;
import ru.lucky_book.utils.AppUtils;
import ru.lucky_book.utils.ConnectionUtils;
import ru.lucky_book.utils.NumberUtils;
import ru.lucky_book.utils.UiUtils;
import ru.lucky_book.utils.pay.YandexMoney;
import ru.luckybook.data.DeliveryPriceSpsr;
import ru.yandex.money.android.PaymentActivity;

public class OrderActivity extends AppCompatActivity implements DialogInterface.OnKeyListener, PromocodeDialog.OnSubmitClickListener, ServiceRepository.OnLoadListener, DatePickerDialog.OnDateSetListener, OrderView {

    public static final int ACCESS_AGE = 10;
    private static final int DEFAULT_ZIP_LENGTH = 6;

    private SpiceManager mSpiceManager = new LuckySpiceManager(LuckyUncachedSpiceService.class);
    boolean doubleBackToExitPressedOnce = false;
    private ViewHolder mViewHolder;
    private MaterialDialog mProgressDialog;
    private boolean mPromoCodeActivated;
    private RealmAlbum mRealmAlbum;
    private String mOldCity;
    private OrderData mOrderData;
    private int mAlbumTotalCost;
    private double mTotalCost;
    private DatePickerDialog mDatePickerDialog;
    private DecimalFormat mFormat = new DecimalFormat("#.##");
    CostResponseBody.Tariff mTariff = new CostResponseBody.Tariff();
    private PaymentButtonClickListener mClickListener = new PaymentButtonClickListener();
    private CreateInvoiceListener mCreateInvoiceListener = new CreateInvoiceListener();
    OrderPresenter mPresenter;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private boolean isCancel;
    private boolean isCancelCalculateCost;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.payment_and_delivery);
        mPresenter = new OrderPresenter();
        mPresenter.attachView(this);
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        collectData();
        createViewHolder();
        fillViewHolder();
        generateOrderId();
        startAlbumUploading();
    }

    private void generateOrderId() {
        if (Preferences.OrderData.getOrderId(this, mRealmAlbum.getId()) == Preferences.OrderData.DEFAULT_ORDER_ID)
            mPresenter.getNewIdForOrder();
    }

    @Override
    protected void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
        }
        EventBus.getDefault().register(this);
        MainApplication.currentScreen = MainApplication.Screen.ЗаполнениеДанных;
        ((MainApplication)getApplication()).info();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        collectData();
        if (mRealmAlbum.getStatusUpload() != null && (mRealmAlbum.getStatusUpload().contains(UploadEvent.STATUS_SEND_LINK_DONE) ||
                mRealmAlbum.getStatusUpload().contains(UploadEvent.STATUS_UPLOAD_DONE))) {
            mViewHolder.mUploadProgressButton.setState(AnimDownloadProgressButton.NORMAL);
            mViewHolder.mUploadProgressButton.setIdleText(getString(R.string.upload_done));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private boolean mIsSomethingInBackground = false;

    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    private void collectData() {
        Intent intent = getIntent();
        if (intent != null) {
            mRealmAlbum = DBHelper.findAlbumById(intent.getStringExtra(PreviewActivity.EXTRA_ALBUM_ID));
            mAlbumTotalCost = intent.getIntExtra(PreviewActivity.EXTRA_COST, 0);
            mTotalCost = mAlbumTotalCost;
            mPromoCodeActivated = mRealmAlbum.getPromoCode() != null && !mRealmAlbum.getPromoCode().isEmpty();
        }
    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.mDeliveryWaySwitch = (RMSwitch) findViewById(R.id.delivery_way);
        mViewHolder.mRussiaPostTextView = (TextView) findViewById(R.id.russia_post);
        mViewHolder.mSpsrPostTextView = (TextView) findViewById(R.id.spsr_post);
        mViewHolder.mCityEditText = (EditText) findViewById(R.id.city);
        mViewHolder.mStreetEditText = (EditText) findViewById(R.id.street);
        mViewHolder.mHouseEditText = (EditText) findViewById(R.id.house);
        mViewHolder.mFlatEditText = (EditText) findViewById(R.id.flat);
        mViewHolder.mZipEditText = (EditText) findViewById(R.id.zip);
        mViewHolder.mNameEditText = (EditText) findViewById(R.id.name);
        mViewHolder.mSurnameEditText = (EditText) findViewById(R.id.surname);
        mViewHolder.mPatronymicEditText = (EditText) findViewById(R.id.patronymic);
        mViewHolder.mBirthdayEditText = (EditText) findViewById(R.id.birthday);
        mViewHolder.mEmailEditText = (EditText) findViewById(R.id.email);
        mViewHolder.mPhoneEditText = (MaskedEditText) findViewById(R.id.phone);
        mViewHolder.mTotalCostTextView = (TextView) findViewById(R.id.total_cost);
        mViewHolder.mCostDetailTextView = (TextView) findViewById(R.id.cost_detail);
        mViewHolder.mUploadProgressButton = (AnimDownloadProgressButton) findViewById(R.id.upload_progress);
        mViewHolder.mPayByCardButton = (AnimDownloadProgressButton) findViewById(R.id.pay_by_card);
        mViewHolder.mCityLabelTextView = (TextView) findViewById(R.id.city_label);
        mViewHolder.mStreetLabelTextView = (TextView) findViewById(R.id.street_label);
        mViewHolder.mHouseLabelTextView = (TextView) findViewById(R.id.house_label);
        mViewHolder.mFlatLabelTextView = (TextView) findViewById(R.id.flat_label);
        mViewHolder.mZipLabelTextView = (TextView) findViewById(R.id.zip_label);
        mViewHolder.mNameLabelTextView = (TextView) findViewById(R.id.name_label);
        mViewHolder.mSurnameLabelTextView = (TextView) findViewById(R.id.surname_label);
        mViewHolder.mPatronymicLabelTextView = (TextView) findViewById(R.id.patronymic_label);
        mViewHolder.mBirthdayLabelTextView = (TextView) findViewById(R.id.birthday_label);
        mViewHolder.mEmailLabelTextView = (TextView) findViewById(R.id.email_label);
        mViewHolder.mPhoneLabelTextView = (TextView) findViewById(R.id.phone_label);
        mViewHolder.mProgressBar = (ProgressBar) findViewById(R.id.progressBarSpsr);
    }


    private void fillViewHolder() {
        fillViews();
        mViewHolder.mRussiaPostTextView.setText(Html.fromHtml(getString(R.string.russia_post)));
        mViewHolder.mSpsrPostTextView.setText(R.string.not_delivery_spsr);
        if (mOldCity == null || !TextUtils.equals(mOldCity, mViewHolder.mCityEditText.getText().toString())) {
            startCostCalculating();
        }
        mViewHolder.mStreetEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.STREET));
        mViewHolder.mCityEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.CITY));
        mViewHolder.mHouseEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.HOUSE));
        mViewHolder.mFlatEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.FLAT));
        mViewHolder.mZipEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.ZIP));
        mViewHolder.mNameEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.NAME));
        mViewHolder.mSurnameEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.SURNAME));
        mViewHolder.mPatronymicEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.PATRONYMIC));
        mViewHolder.mPhoneEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.PHONE));
        mViewHolder.mEmailEditText.setOnFocusChangeListener(new PreferencesSaveListener(this, Preferences.OrderData.EMAIL));
        mViewHolder.mCityLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mCityEditText));
        mViewHolder.mStreetLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mStreetEditText));
        mViewHolder.mHouseLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mHouseEditText));
        mViewHolder.mFlatLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mFlatEditText));
        mViewHolder.mZipLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mZipEditText));
        mViewHolder.mNameLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mNameEditText));
        mViewHolder.mSurnameLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mSurnameEditText));
        mViewHolder.mPatronymicLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mPatronymicEditText));
        mViewHolder.mBirthdayLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mBirthdayEditText));
        mViewHolder.mEmailLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mEmailEditText));
        mViewHolder.mPhoneLabelTextView.setOnClickListener(new TextViewFocusClickListener(mViewHolder.mPhoneEditText));
        if (Preferences.Payment.isPaid(this, mRealmAlbum.getId()))
            mViewHolder.mPayByCardButton.setProgressText(getString(R.string.ordered_text), 100);
        mViewHolder.mBirthdayEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                mDatePickerDialog = DatePickerDialog.newInstance(OrderActivity.this, year, month, day);
                mDatePickerDialog.show(getSupportFragmentManager(), null);
                v.getBackground().setColorFilter(ContextCompat.getColor(OrderActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            } else {
                Preferences.OrderData.setProperty(OrderActivity.this, Preferences.OrderData.BIRTHDAY, TextUtils.isDigitsOnly(mViewHolder.mBirthdayEditText.getText()) ? null : mViewHolder.mBirthdayEditText.getText().toString());
            }
        });
        processTariff(false);

        mViewHolder.mDeliveryWaySwitch.addSwitchObserver(isChecked -> {
            processTariff(isChecked);
            if (isChecked && mTariff == null) {
                Toast.makeText(OrderActivity.this, R.string.city_not_found_error, Toast.LENGTH_SHORT).show();
                mViewHolder.mDeliveryWaySwitch.setChecked(false);
                return;
            }
        });
        mViewHolder.mCityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mViewHolder.mDeliveryWaySwitch.setEnabled(charSequence.length() != 0);
                if (charSequence.length() == 0)
                    mViewHolder.mDeliveryWaySwitch.setChecked(false);
                if (mOldCity == null || !TextUtils.equals(mOldCity, mViewHolder.mCityEditText.getText().toString())) {
                    startCostCalculating();
                } else {
                    mOldCity = TextUtils.isEmpty(mViewHolder.mCityEditText.getText()) ? "" : mViewHolder.mCityEditText.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mViewHolder.mPayByCardButton.setOnClickListener(mClickListener);

    }

    private void fillViews() {
        String city = Preferences.OrderData.getProperty(this, Preferences.OrderData.CITY);
        String street = Preferences.OrderData.getProperty(this, Preferences.OrderData.STREET);
        String house = Preferences.OrderData.getProperty(this, Preferences.OrderData.HOUSE);
        String flat = Preferences.OrderData.getProperty(this, Preferences.OrderData.FLAT);
        String zip = Preferences.OrderData.getProperty(this, Preferences.OrderData.ZIP);
        String name = Preferences.OrderData.getProperty(this, Preferences.OrderData.NAME);
        String surname = Preferences.OrderData.getProperty(this, Preferences.OrderData.SURNAME);
        String patronymic = Preferences.OrderData.getProperty(this, Preferences.OrderData.PATRONYMIC);
        String birthday = Preferences.OrderData.getProperty(this, Preferences.OrderData.BIRTHDAY);
        String phone = Preferences.OrderData.getProperty(this, Preferences.OrderData.PHONE);
        String email = Preferences.OrderData.getProperty(this, Preferences.OrderData.EMAIL);
        String promoCode = Preferences.OrderData.getProperty(this, mRealmAlbum.getId());
        mViewHolder.mCityEditText.setText(TextUtils.isEmpty(city) ? "" : city);
        mViewHolder.mStreetEditText.setText(TextUtils.isEmpty(street) ? "" : street);
        mViewHolder.mHouseEditText.setText(TextUtils.isEmpty(house) ? "" : house);
        mViewHolder.mFlatEditText.setText(TextUtils.isEmpty(flat) ? "" : flat);
        mViewHolder.mZipEditText.setText(TextUtils.isEmpty(zip) ? "" : zip);
        mViewHolder.mNameEditText.setText(TextUtils.isEmpty(name) ? "" : name);
        mViewHolder.mSurnameEditText.setText(TextUtils.isEmpty(surname) ? "" : surname);
        mViewHolder.mPatronymicEditText.setText(TextUtils.isEmpty(patronymic) ? "" : patronymic);
        mViewHolder.mBirthdayEditText.setText(TextUtils.isEmpty(birthday) ? "" : birthday);
        mViewHolder.mPhoneEditText.setMaskedText(TextUtils.isEmpty(phone) ? "" : phone);
        mViewHolder.mEmailEditText.setText(TextUtils.isEmpty(email) ? "" : email);
        if (city == null) {
            mViewHolder.mDeliveryWaySwitch.setEnabled(false);
        }
    }

    private synchronized void setIsSomethingInBackground(boolean isEnabled) {
        mIsSomethingInBackground = isEnabled;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSubmitClick(String code) {
        if (!TextUtils.isEmpty(code)) {
            mProgressDialog = UiUtils.showProgress(this);
            ServiceRepository repository = ServiceRepository.getInstance(this);
            repository.setListener(this);
            repository.sendPromoCode(code);
        }
    }

    @Override
    public void onLoad(String code) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        Preferences.OrderData.setProperty(this, mRealmAlbum.getId(), code);
    }

    @Override
    public void onFail(int state) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (state == ServiceRepository.STATE_INCORRECT) {
            UiUtils.showErrorDialog(this, R.string.promo_code_incorrect_error);
        } else {
            UiUtils.showErrorDialog(this, R.string.promo_code_connection_error);
        }
    }

    public void sendSuccessfulOrder() {
        sendSuccessfulOrder(null, false);
    }

    public void sendSuccessfulOrder(@Nullable String invoiceDelivery, boolean preOrder) {
        try {
            if (Preferences.OrderData.getOrderId(this, mRealmAlbum.getId()) == Preferences.OrderData.DEFAULT_ORDER_ID)
                return;
            OrderData orderData = createOrder();
            Address address = new Address(orderData.getAddress().getCity(),
                    orderData.getAddress().getStreet(),
                    orderData.getAddress().getHouse(),
                    orderData.getAddress().getFlat(),
                    String.valueOf(orderData.getAddress().getZip()));
            Contact contact = new Contact(orderData.getContact().getName(),
                    orderData.getContact().getSurname(),
                    orderData.getContact().getPatronymic(),
                    orderData.getContact().getEmail(),
                    orderData.getContact().getPhone(),
                    String.valueOf(orderData.getContact().getBirthday()));
            Order order = new Order(address, contact);
            order.setDeliveryId(invoiceDelivery);
            order.setAppId(AppUtils.getDeviceID(this));
            order.setOs(getString(R.string.os_text));
            order.setPhoneModel(AppUtils.getDeviceName());
            order.setSkinId(mRealmAlbum.getCoverId());
            order.setCommonId(Preferences.OrderData.getOrderId(this, mRealmAlbum.getId()));
            if (mRealmAlbum.getPayTransaction() != null)
                order.setPayTransaction(mRealmAlbum.getPayTransaction());
            if (mTariff != null) {
                order.setDeliveryPrice(Math.ceil(mTariff.getTotalCost() * 1.04));
            }
            order.setDeliveryType(mTariff != null && mTariff.getTotalCost() != 0 ? 1 : 0);
            order.setPrice(mTotalCost);
            if (preOrder)
                mPresenter.sendPreOrder(order, mRealmAlbum.getPromoCode());
            else
                mPresenter.sendSuccessfulOrder(order, mRealmAlbum.getPromoCode());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == YandexMoney.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String id_invoice = data.getStringExtra(PaymentActivity.EXTRA_INVOICE_ID);
                DBHelper.updatePaymentStatus(mRealmAlbum.getId(), RealmAlbum.STATUS_PAYMENT_DONE,
                        b -> {
                            mRealmAlbum = DBHelper.findAlbumById(getIntent().getStringExtra(PreviewActivity.EXTRA_ALBUM_ID));
                            mRealmAlbum = DBHelper.updateTransaction(mRealmAlbum.getId(), id_invoice);
                            processTariff(mViewHolder.mDeliveryWaySwitch.isChecked());
                            tryToSendOrder();
                        }, this);
            } else {
                Toast.makeText(this, R.string.yandex_money_payment_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeButtonEnabled(boolean enabled) {
    }

    private void startAlbumUploading() {
        if (mRealmAlbum.getStatusUpload() == null || mRealmAlbum.getStatusUpload().contains(UploadEvent.STATUS_UPLOAD_NONE)) {
            mViewHolder.mUploadProgressButton.setState(AnimDownloadProgressButton.DOWNLOADING);
            mViewHolder.mUploadProgressButton.setProgressText(getString(R.string.text_in_queue), 0);
            DBHelper.updateUploadStatus(mRealmAlbum.getId(), UploadEvent.STATUS_UPLOAD_STACK, this, isClear -> {
                mRealmAlbum = DBHelper.findAlbumById(mRealmAlbum.getId());
                Intent intent = new Intent(this, UploadService.class);
                startService(intent);
            });

        } else if (mRealmAlbum.getStatusUpload().contains(UploadEvent.STATUS_UPLOAD_STACK)) {
            mViewHolder.mUploadProgressButton.setState(AnimDownloadProgressButton.DOWNLOADING);
            mViewHolder.mUploadProgressButton.setProgressText(getString(R.string.text_in_queue), 0);
        }
    }

    private void showCancelBackgroundDialog(MaterialDialog.SingleButtonCallback onOkClick) {
        UiUtils.showMessageDialog(this, R.string.attention, R.string.need_to_cancel_background_task_before, R.string.stop, onOkClick);
    }

    private void startSendOrderData() {
        try {
            mProgressDialog = UiUtils.showProgress(this);
            mOrderData = createOrder();
            if (mViewHolder.mDeliveryWaySwitch.isChecked()) {
                mSpiceManager.execute(new CreateInvoiceSpiceTask(OrderActivity.this, mOrderData), mCreateInvoiceListener);
            } else {
                sendSuccessfulOrder();
            }
        } catch (ParseException e) {
            Toast.makeText(OrderActivity.this, R.string.parse_date_error, Toast.LENGTH_LONG).show();
            disableProgress();
        }
    }


    private void tryToSendOrder() {
        Error error = allFieldsFilled();
        if (error == null) {
            if (ConnectionUtils.connectedToNetwork(OrderActivity.this)) {
                startSendOrderData();
            } else {
                UiUtils.showOrderAlertDialog(OrderActivity.this, R.string.order_alert_error_network_content,
                        (dialog, which) -> startSendOrderData(), (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
            }
        }
    }

    private void showError(Error error) {
        View view = findViewById(error.getResourceId());
        view.getBackground().setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
    }

    private void changeEnabled(boolean enabled) {
        mViewHolder.mDeliveryWaySwitch.setEnabled(enabled);
        mViewHolder.mCityEditText.setEnabled(enabled);
        mViewHolder.mStreetEditText.setEnabled(enabled);
        mViewHolder.mHouseEditText.setEnabled(enabled);
        mViewHolder.mFlatEditText.setEnabled(enabled);
        mViewHolder.mZipEditText.setEnabled(enabled);
        mViewHolder.mNameEditText.setEnabled(enabled);
        mViewHolder.mSurnameEditText.setEnabled(enabled);
        mViewHolder.mPatronymicEditText.setEnabled(enabled);
        mViewHolder.mBirthdayEditText.setEnabled(enabled);
        mViewHolder.mPhoneEditText.setEnabled(enabled);
        mViewHolder.mEmailEditText.setEnabled(enabled);
        mViewHolder.mCityLabelTextView.setEnabled(enabled);
        mViewHolder.mStreetLabelTextView.setEnabled(enabled);
        mViewHolder.mHouseLabelTextView.setEnabled(enabled);
        mViewHolder.mFlatLabelTextView.setEnabled(enabled);
        mViewHolder.mZipLabelTextView.setEnabled(enabled);
        mViewHolder.mNameLabelTextView.setEnabled(enabled);
        mViewHolder.mSurnameLabelTextView.setEnabled(enabled);
        mViewHolder.mPatronymicLabelTextView.setEnabled(enabled);
        mViewHolder.mBirthdayLabelTextView.setEnabled(enabled);
        mViewHolder.mEmailLabelTextView.setEnabled(enabled);
        mViewHolder.mPhoneLabelTextView.setEnabled(enabled);
    }

    private OrderData createOrder() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        OrderData orderData = new OrderData();
        OrderData.Address address = new OrderData.Address();
        OrderData.Contact contact = new OrderData.Contact();

        address.setCity(mViewHolder.mCityEditText.getText().toString());
        address.setStreet(mViewHolder.mStreetEditText.getText().toString());
        address.setHouse(mViewHolder.mHouseEditText.getText().toString());
        address.setFlat(mViewHolder.mFlatEditText.getText().toString());
        address.setZip(Integer.parseInt(mViewHolder.mZipEditText.getText().toString()));

        contact.setName(mViewHolder.mNameEditText.getText().toString());
        contact.setSurname(mViewHolder.mSurnameEditText.getText().toString());
        contact.setPatronymic(mViewHolder.mPatronymicEditText.getText().toString());
        contact.setEmail(mViewHolder.mEmailEditText.getText().toString());
        contact.setPhone(String.format("7%s", mViewHolder.mPhoneEditText.getUnmaskedText().toString()));
        Date birthday = sdf.parse(mViewHolder.mBirthdayEditText.getText().toString());
        contact.setBirthday(birthday.getTime());

        orderData.setAddress(address);
        orderData.setContact(contact);
        orderData.setAmount(mTotalCost);
        String promoCode = Preferences.OrderData.getProperty(this, mRealmAlbum.getId());
        if (promoCode != null) {
            orderData.setPromoCode(promoCode);
        }
        if (mRealmAlbum.getFullSizePath() != null) {
            orderData.setLink(mRealmAlbum.getFullSizePath());
        }
        return orderData;
    }

    private void startCostCalculating() {
        mTariff = null;
        if (!TextUtils.isEmpty(mViewHolder.mCityEditText.getText())) {
            //   mProgressDialog = UiUtils.showProgress(OrderActivity.this, R.string.spsr_cost_calculating_progress);
            stateDeliveryCost(true);
            mPresenter.findCity(mViewHolder.mCityEditText.getText().toString());
        } else {
            stateDeliveryCost(false);
        }
    }

    private void stateDeliveryCost(boolean isLoad) {
        isCancelCalculateCost = !isLoad;
        mViewHolder.mDeliveryWaySwitch.setEnabled(mTariff != null);
        mViewHolder.mProgressBar.setVisibility(isLoad ? View.VISIBLE : View.INVISIBLE);
        mViewHolder.mSpsrPostTextView.setVisibility(!isLoad ? View.VISIBLE : View.INVISIBLE);
        if (mTariff != null) {
            mViewHolder.mSpsrPostTextView.setText(getString(R.string.price_pattern, String.valueOf((int) Math.ceil(mTariff.getTotalCost() * 1.04))));
        } else
            mViewHolder.mSpsrPostTextView.setText(getString(R.string.price_pattern, String.valueOf("--")));
        processTariff(mViewHolder.mDeliveryWaySwitch.isChecked());
    }

    private Error allFieldsFilled() {
        if (TextUtils.isEmpty(mViewHolder.mCityEditText.getText())) {
            return Error.CITY;
        }
        if (TextUtils.isEmpty(mViewHolder.mStreetEditText.getText())) {
            return Error.STREET;
        }
        if (TextUtils.isEmpty(mViewHolder.mHouseEditText.getText())) {
            return Error.HOUSE;
        }
        if (TextUtils.isEmpty(mViewHolder.mFlatEditText.getText())) {
            return Error.FLAT;
        }
        if (TextUtils.isEmpty(mViewHolder.mNameEditText.getText())) {
            return Error.NAME;
        }
        if (TextUtils.isEmpty(mViewHolder.mSurnameEditText.getText())) {
            return Error.SURNAME;
        }
        if (TextUtils.isEmpty(mViewHolder.mPatronymicEditText.getText())) {
            return Error.PATRONYMIC;
        }
        if (TextUtils.isEmpty(mViewHolder.mBirthdayEditText.getText())) {
            return Error.BIRTHDAY;
        }
        if (TextUtils.isEmpty(mViewHolder.mEmailEditText.getText())) {
            return Error.EMAIL;
        }
        if (TextUtils.isEmpty(mViewHolder.mPhoneEditText.getText())) {
            return Error.PHONE;
        }
        if (mViewHolder.mZipEditText.getText().toString().length() < DEFAULT_ZIP_LENGTH) {
            return Error.ZIP;
        }
        mViewHolder.mEmailEditText.setText(mViewHolder.mEmailEditText.getText().toString().replace(" ", ""));
        if (!emailMatch()) {
            return Error.EMAIL;
        }
        if (!dateOfBirthMatch()) {
            return Error.BIRTHDAY;
        }
        return null;
    }

    private boolean dateOfBirthMatch() {
        String birthdayText = mViewHolder.mBirthdayEditText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        try {
            Date birthDay = sdf.parse(birthdayText);
            calendar.setTime(birthDay);
            int birthdayYear = calendar.get(Calendar.YEAR);
            if (currentYear - birthdayYear < ACCESS_AGE) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
        Matcher matcher = pattern.matcher(birthdayText);
        return matcher.matches();
    }

    private boolean emailMatch() {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
        Matcher matcher = pattern.matcher(mViewHolder.mEmailEditText.getText().toString());
        return matcher.matches();
    }

    private void goToReady() {
        Intent intent = new Intent(this, OrderReadyActivity.class);
        intent.putExtra(OrderReadyActivity.EXTRA_ALBUM_ID, mRealmAlbum.getId());
        startActivity(intent);
        finish();
    }

    private void processTariff(boolean isSpsrCheked) {
        boolean paid = mRealmAlbum.getStatusPayment() != null;
        if (paid) {
            changeEnabled(false);
            if (mRealmAlbum.getStatusPayment().contains(RealmAlbum.STATUS_PAYMENT_SEND_SERVER)) {
                mViewHolder.mPayByCardButton.setIdleText(R.string.ordered_text);
                mProgressDialog = UiUtils.showProgress(this, R.string.text_progress_album_uploading, this);
            } else
                mViewHolder.mPayByCardButton.setIdleText(R.string.pay_text);
            return;
        }
        String costDetail = null;
        double totalCostDelivery = 0;
        if (mTariff != null && isSpsrCheked)
            totalCostDelivery = Math.ceil(mTariff.getTotalCost() * 1.04);
        if (mAlbumTotalCost == 0 && totalCostDelivery == 0) {
            costDetail = getString(R.string.cost_detail_with_promocode_russia_post_delivery_formatted);
        } else if (mAlbumTotalCost == 0 && totalCostDelivery != 0) {
            costDetail = getString(R.string.cost_detail_with_promocode_srsr_delivery_formatted, mFormat.format(totalCostDelivery));
        } else if (mAlbumTotalCost != 0 && totalCostDelivery != 0) {
            costDetail = getString(R.string.cost_detail_formatted, mAlbumTotalCost, mFormat.format(totalCostDelivery));
        } else if (mAlbumTotalCost != 0 && totalCostDelivery == 0) {
            costDetail = getString(R.string.cost_detail_free_delivery_formatted, mAlbumTotalCost);
        }
        mTotalCost = totalCostDelivery + mAlbumTotalCost;
        if (mTotalCost == 0) {
            mViewHolder.mTotalCostTextView.setText(getString(R.string.total_cost_free));
            mViewHolder.mCostDetailTextView.setVisibility(View.INVISIBLE);
            mViewHolder.mPayByCardButton.setIdleText(R.string.deliver);
        } else {
            mViewHolder.mPayByCardButton.setIdleText(R.string.pay_by_card);
            mViewHolder.mCostDetailTextView.setVisibility(View.VISIBLE);
            mViewHolder.mCostDetailTextView.setText(costDetail);
            mViewHolder.mTotalCostTextView.setText(getString(R.string.total_cost, (int) mTotalCost));
        }
    }

    private void disableProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        hideDatePicker();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        mViewHolder.mBirthdayEditText.setText(sdf.format(calendar.getTime()));
    }


    private void hideDatePicker() {
        if (mDatePickerDialog != null && !mDatePickerDialog.isAdded()) {
            mDatePickerDialog.dismiss();
            mDatePickerDialog = null;
        }
    }

    @Override
    public void resultSendOrder(SuccessOrderResponse successOrder) {
        Preferences.Payment.setPaid(this, mRealmAlbum.getId(), true);
        processTariff(mViewHolder.mDeliveryWaySwitch.isChecked());
        DBHelper.updatePaymentStatus(mRealmAlbum.getId(), RealmAlbum.STATUS_PAYMENT_SEND_SERVER, b -> {
            if (b || successOrder.isOrder()) {
                goToReady();
                finish();
            } else {
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                mProgressDialog = UiUtils.showProgress(this, R.string.text_progress_album_uploading, this);
            }
        }, this);

    }

    @Override
    public void resultGenerateOrderId(OrderId orderId) {
        Preferences.OrderData.setOrderId(this, mRealmAlbum.getId(), orderId.getId());
    }

    @Override
    public void showDeliveryPrice(DeliveryPriceSpsr deliveryPriceSpsr) {
        if (deliveryPriceSpsr.getError() == null) {
            mTariff = new CostResponseBody.Tariff();
            mTariff.setTotalCost(deliveryPriceSpsr.getPrice());
        }
        stateDeliveryCost(false);
        disableProgress();
    }

    @Override
    public void resultSendPreOrder(SuccessOrderResponse successOrderResponse) {
        disableProgress();
        String name = mViewHolder.mNameEditText.getText().toString();
        String surname = mViewHolder.mSurnameEditText.getText().toString();
        String patronymic = mViewHolder.mPatronymicEditText.getText().toString();
        String city = mViewHolder.mCityEditText.getText().toString();
        String street = mViewHolder.mStreetEditText.getText().toString();
        String house = mViewHolder.mHouseEditText.getText().toString();
        String flat = mViewHolder.mFlatEditText.getText().toString();
        String zip = mViewHolder.mZipEditText.getText().toString();
        String fullName = String.format("%s %s %s", surname, name, patronymic);
        String fullAddress = getString(R.string.full_address_tempalte, zip, city, street, house, flat);
        String email = mViewHolder.mEmailEditText.getText().toString();
        new YandexMoney(OrderActivity.this, mTotalCost, fullName, fullAddress, email).pay();
    }

    @Override
    public void showError(Throwable throwable) {
        throwable.printStackTrace();
        stateDeliveryCost(false);
        disableProgress();
        if(TextUtils.equals(throwable.getMessage(),OrderPresenter.PAYMENT_EXCEPTION)){
            UiUtils.showErrorDialog(this,R.string.text_please_again);
        }
    }


    @Override
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP &&
                !keyEvent.isCanceled()) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.text_click_double_click, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
        return false;
    }

    private class ViewHolder {
        ProgressBar mProgressBar;
        RMSwitch mDeliveryWaySwitch;
        TextView mRussiaPostTextView;
        TextView mSpsrPostTextView;
        EditText mCityEditText;
        EditText mStreetEditText;
        EditText mHouseEditText;
        EditText mFlatEditText;
        EditText mZipEditText;
        EditText mNameEditText;
        EditText mSurnameEditText;
        EditText mPatronymicEditText;
        EditText mBirthdayEditText;
        EditText mEmailEditText;
        MaskedEditText mPhoneEditText;
        TextView mTotalCostTextView;
        TextView mCostDetailTextView;
        AnimDownloadProgressButton mUploadProgressButton;
        AnimDownloadProgressButton mPayByCardButton;
        TextView mCityLabelTextView;
        TextView mStreetLabelTextView;
        TextView mHouseLabelTextView;
        TextView mFlatLabelTextView;
        TextView mZipLabelTextView;
        TextView mNameLabelTextView;
        TextView mSurnameLabelTextView;
        TextView mPatronymicLabelTextView;
        TextView mBirthdayLabelTextView;
        TextView mEmailLabelTextView;
        TextView mPhoneLabelTextView;
    }

    private class PaymentButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (Preferences.Payment.isPaid(OrderActivity.this, mRealmAlbum.getId()))
                return;
            if (!isCancelCalculateCost) {
                Toast.makeText(OrderActivity.this, R.string.text_wait_for_download_information, Toast.LENGTH_SHORT).show();
                return;
            }
            if (mTotalCost == 0) {
                {
                    sendSuccessfulOrder();
                    return;
                }
            }

            Error error = allFieldsFilled();
            if (error == null) {
                mProgressDialog = UiUtils.showProgress(OrderActivity.this);
                sendSuccessfulOrder(null, true);
            } else {
                showError(error);
                Toast.makeText(OrderActivity.this, R.string.fields_not_filled_error, Toast.LENGTH_LONG).show();
            }

        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadEvent event) {
        if (event.getId().contains(mRealmAlbum.getId())) {
            if (event.getStatus().contains(UploadEvent.STATUS_UPLOAD_PROCESSING)) {
                mViewHolder.mUploadProgressButton.setState(AnimDownloadProgressButton.DOWNLOADING);
                switch (event.getType()) {
                    case UploadEvent.TYPE_UPLOAD: {
                        float percent = 100 * (event.getProgress() / event.getFileSize());
                        String message = getString(R.string.uploading_progress,
                                mFormat.format(event.getProgress() / NumberUtils.SizeInBytes.MiB),
                                mFormat.format(event.getFileSize() / (double) NumberUtils.SizeInBytes.MiB));
                        mViewHolder.mUploadProgressButton.setProgressText(message, percent);
                        break;
                    }
                    case UploadEvent.TYPE_PDF_CREATE: {
                        mViewHolder.mUploadProgressButton.setProgressText(getString(R.string.progress_album_creating), event.getProgress());
                        break;
                    }
                }
            } else if (event.getStatus().contains(UploadEvent.STATUS_UPLOAD_DONE)) {
                if (event.getType() == UploadEvent.TYPE_UPLOAD) {
                    mViewHolder.mUploadProgressButton.toProgress(100);
                    mViewHolder.mUploadProgressButton.setState(AnimDownloadProgressButton.NORMAL);
                    EventBus.getDefault().removeStickyEvent(event);
                } else if (event.getType() == UploadEvent.TYPE_ORDER_DONE) {
                    if (event.getProgress() == 1) {
                        EventBus.getDefault().removeStickyEvent(event);
                        goToReady();
                        finish();
                    }
                }
            }
        }
    }


    private class CreateInvoiceListener implements RequestListener<String> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            changeEnabled(true);
            disableProgress();
            setIsSomethingInBackground(false);
            if (!(spiceException instanceof RequestCancelledException)) {
                UiUtils.showErrorDialog(OrderActivity.this, spiceException.getMessage());
            }
        }

        @Override
        public void onRequestSuccess(String result) {
            if (!TextUtils.isEmpty(result)) {
                sendSuccessfulOrder(result, false);
            } else {
                disableProgress();
                Toast.makeText(OrderActivity.this, R.string.create_invoice_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class TextViewFocusClickListener implements View.OnClickListener {

        private View mFocusableView;

        public TextViewFocusClickListener(View focusableView) {
            mFocusableView = focusableView;
        }

        @Override
        public void onClick(View v) {
            mFocusableView.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mFocusableView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private enum Error {

        CITY(R.id.city),
        STREET(R.id.street),
        HOUSE(R.id.house),
        FLAT(R.id.flat),
        ZIP(R.id.zip),
        NAME(R.id.name),
        SURNAME(R.id.surname),
        PATRONYMIC(R.id.patronymic),
        BIRTHDAY(R.id.birthday),
        PHONE(R.id.phone),
        EMAIL(R.id.email);

        @IdRes
        private int mResourceId;

        Error(int resourceId) {
            mResourceId = resourceId;
        }

        public int getResourceId() {
            return mResourceId;
        }
    }
}
