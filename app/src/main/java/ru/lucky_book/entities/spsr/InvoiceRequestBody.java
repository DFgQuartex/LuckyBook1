package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "root")
@Namespace(reference = "http://spsr.ru/webapi/xmlconverter/1.3")
@Order(elements = {"Params", "Login", "XmlConverter"})
public class InvoiceRequestBody {

    @Element(name = "Params")
    @Namespace(reference = "http://spsr.ru/webapi/WA/1.0")
    private InvoiceParams mParams;

    @Element(name = "Login")
    private Login mLogin;

    @Element(name = "XmlConverter")
    private XmlConverter mXmlConverter;

    public InvoiceRequestBody() {
        mParams = new InvoiceParams();
    }

    public InvoiceParams getParams() {
        return mParams;
    }

    public void setParams(InvoiceParams params) {
        mParams = params;
    }

    public Login getLogin() {
        return mLogin;
    }

    public void setLogin(Login login) {
        mLogin = login;
    }

    public XmlConverter getXmlConverter() {
        return mXmlConverter;
    }

    public void setXmlConverter(XmlConverter xmlConverter) {
        mXmlConverter = xmlConverter;
    }

    public static class InvoiceParams {

        @Attribute(name = "Name", empty = "WAXmlConverter")
        private String mName;

        @Attribute(name = "Ver", empty = "1.3")
        private String mVersion;

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getVersion() {
            return mVersion;
        }

        public void setVersion(String version) {
            mVersion = version;
        }
    }

    public static class Login {

        @Attribute(name = "SID")
        private String mSid;

        public String getSid() {
            return mSid;
        }

        public void setSid(String sid) {
            mSid = sid;
        }
    }

    public static class XmlConverter {

        @Element(name = "GeneralInfo")
        private GeneralInfo mGeneralInfo;

        public GeneralInfo getGeneralInfo() {
            return mGeneralInfo;
        }

        public void setGeneralInfo(GeneralInfo generalInfo) {
            mGeneralInfo = generalInfo;
        }

        public static class GeneralInfo {

            @Attribute(name = "ContractNumber", empty = "1620082621")
            private String mContractNumber;

            @ElementList(entry = "Invoice", inline = true)
            private List<Invoice> mInvoices;

            public List<Invoice> getInvoices() {
                return mInvoices;
            }

            public void setInvoices(List<Invoice> invoices) {
                mInvoices = invoices;
            }

            public String getContractNumber() {
                return mContractNumber;
            }

            public void setContractNumber(String contractNumber) {
                mContractNumber = contractNumber;
            }

            @Order(elements = {"Shipper", "Receiver", "Pieces"})
            public static class Invoice {

                @Attribute(name = "Action", empty = "N")
                private String mAction;

                @Attribute(name = "PickUpType", empty = "C")
                private String mPickupType;

                @Attribute(name = "ProductCode", empty = "ZebOn")
                private String mProductCode;

                @Attribute(name = "PiecesCount", empty = "1")
                private Integer mPiecesCount;

                @Attribute(name = "InsuranceType", empty = "INS")
                private String mInsuranceType;

                @Attribute(name = "InsuranceSum", empty = "0.00")
                private Double mInsuranceSum;

                @Element(name = "Shipper")
                private Shipper mShipper;

                @Element(name = "Receiver")
                private Receiver mReceiver;

                public AdditionalServices getAdditionalServices() {
                    return mAdditionalServices;
                }

                public void setAdditionalServices(AdditionalServices additionalServices) {
                    mAdditionalServices = additionalServices;
                }

                public Sms getSms() {
                    return mSms;
                }

                public void setSms(Sms sms) {
                    mSms = sms;
                }

                @Element(name = "AdditionalServices")
                private AdditionalServices mAdditionalServices;
                @Element(name = "SMS")
                private Sms mSms;

                @org.simpleframework.xml.Path("Pieces")
                @ElementList(entry = "Piece", inline = true)
                private List<Piece> mPieces;

                public String getAction() {
                    return mAction;
                }

                public void setAction(String action) {
                    mAction = action;
                }

                public String getPickupType() {
                    return mPickupType;
                }

                public void setPickupType(String pickupType) {
                    mPickupType = pickupType;
                }

                public String getProductCode() {
                    return mProductCode;
                }

                public void setProductCode(String productCode) {
                    mProductCode = productCode;
                }

                public Integer getPiecesCount() {
                    return mPiecesCount;
                }

                public void setPiecesCount(Integer piecesCount) {
                    mPiecesCount = piecesCount;
                }

                public String getInsuranceType() {
                    return mInsuranceType;
                }

                public void setInsuranceType(String insuranceType) {
                    mInsuranceType = insuranceType;
                }

                public Double getInsuranceSum() {
                    return mInsuranceSum;
                }

                public void setInsuranceSum(Double insuranceSum) {
                    mInsuranceSum = insuranceSum;
                }

                public Shipper getShipper() {
                    return mShipper;
                }

                public void setShipper(Shipper shipper) {
                    mShipper = shipper;
                }

                public Receiver getReceiver() {
                    return mReceiver;
                }

                public void setReceiver(Receiver receiver) {
                    mReceiver = receiver;
                }

                public List<Piece> getPieces() {
                    return mPieces;
                }

                public void setPieces(List<Piece> pieces) {
                    mPieces = pieces;
                }

                public static class Receiver extends Shipper {

                    @Attribute(name = "Phone")
                    private String mPhone;

                    @Attribute(name = "Email")
                    private String mEmail;

                    public String getPhone() {
                        return mPhone;
                    }

                    public void setPhone(String phone) {
                        mPhone = phone;
                    }

                    public String getEmail() {
                        return mEmail;
                    }

                    public void setEmail(String email) {
                        mEmail = email;
                    }
                }

                public static class AdditionalServices {
                    public int getCheckContents() {
                        return mCheckContents;
                    }

                    public void setCheckContents(int checkContents) {
                        this.mCheckContents = checkContents;
                    }

                    @Attribute(name = "CheckContents")
                    private int mCheckContents;
                }

                public static class Sms {
                    @Attribute(name = "SMStoReceiver")
                    private int mSmsToReceiver;

                    public String getPhoneNumber() {
                        return phoneNumber;
                    }

                    public void setPhoneNumber(String phoneNumber) {
                        this.phoneNumber = phoneNumber;
                    }

                    public int getSmsToReceiver() {
                        return mSmsToReceiver;
                    }

                    public void setSmsToReceiver(int smsToReceiver) {
                        mSmsToReceiver = smsToReceiver;
                    }

                    @Attribute(name = "SMSNumberReceiver")
                    private String phoneNumber;
                }

                public static class Piece {

                    @Attribute(name = "Description", empty = "15")
                    private Integer mDescription;

                    public Integer getDescription() {
                        return mDescription;
                    }

                    public void setDescription(Integer description) {
                        mDescription = description;
                    }
                }
            }
        }
    }

}
