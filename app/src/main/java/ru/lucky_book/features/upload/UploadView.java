package ru.lucky_book.features.upload;

import ru.lucky_book.data.SuccessOrderResponse;
import ru.lucky_book.features.base.MvpView;

/**
 * Created by DemaWork on 21.04.2017.
 */

public interface UploadView extends MvpView {
    void resultSendOrderLink(SuccessOrderResponse successOrder);
}
