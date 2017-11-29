package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "root")
public class CostResponseBody {

    @ElementList(entry = "Tariff", inline = true)
    private List<Tariff> mTariffs;

    public List<Tariff> getTariffs() {
        return mTariffs;
    }

    public void setTariffs(List<Tariff> tariffs) {
        mTariffs = tariffs;
    }

    public static class Tariff {

        @Element(name = "TariffType")
        private String mTariffType;

        @Element(name = "Total_Dost")
        private double mTotalCost;

        @Element(name = "DP")
        private String mDeliveryTime;

        @Element(name = "Total_DopUsl")
        private String mTotalDopUsl;

        @Element(name = "Insurance")
        private String mInsurance;

        @Element(name = "worth")
        private String mWorth;

        public double getTotalCost() {
            return mTotalCost;
        }

        public void setTotalCost(double totalCost) {
            mTotalCost = totalCost;
        }

        public String getDeliveryTime() {
            return mDeliveryTime;
        }

        public void setDeliveryTime(String deliveryTime) {
            mDeliveryTime = deliveryTime;
        }

        public String getTariffType() {
            return mTariffType;
        }

        public void setTariffType(String tariffType) {
            mTariffType = tariffType;
        }

        public String getTotalDopUsl() {
            return mTotalDopUsl;
        }

        public void setTotalDopUsl(String totalDopUsl) {
            mTotalDopUsl = totalDopUsl;
        }

        public String getInsurance() {
            return mInsurance;
        }

        public void setInsurance(String insurance) {
            mInsurance = insurance;
        }

        public String getWorth() {
            return mWorth;
        }

        public void setWorth(String worth) {
            mWorth = worth;
        }
    }
}
