package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "root")
public class InvoiceResponseBody {

    @Element(name = "Result")
    private Result mResult;

    @Element(name = "Invoice")
    private Invoice mInvoice;

    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }

    public Invoice getInvoice() {
        return mInvoice;
    }

    public void setInvoice(Invoice invoice) {
        mInvoice = invoice;
    }

    public static class Invoice {

        @Attribute(name = "Status")
        private String mStatus;

        @Attribute(name = "GCNumber")
        private String mGCNumber;

        @Attribute(name = "InvoiceNumber")
        private String mInvoiceNumber;

        @Attribute(name = "Barcodes")
        private String mBarcodes;

        @Attribute(name = "ClientBarcodes")
        private String mClientBarcodes;

        public String getStatus() {
            return mStatus;
        }

        public void setStatus(String status) {
            mStatus = status;
        }

        public String getGCNumber() {
            return mGCNumber;
        }

        public void setGCNumber(String GCNumber) {
            mGCNumber = GCNumber;
        }

        public String getInvoiceNumber() {
            return mInvoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            mInvoiceNumber = invoiceNumber;
        }

        public String getBarcodes() {
            return mBarcodes;
        }

        public void setBarcodes(String barcodes) {
            mBarcodes = barcodes;
        }

        public String getClientBarcodes() {
            return mClientBarcodes;
        }

        public void setClientBarcodes(String clientBarcodes) {
            mClientBarcodes = clientBarcodes;
        }
    }

}
