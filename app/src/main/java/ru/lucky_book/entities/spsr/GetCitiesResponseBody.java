package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "root")
@Order(elements = {"Result", "City"})
public class GetCitiesResponseBody {

    @ElementList(name = "City", entry = "Cities")
    private List<City> mCities;

    @Element(name = "Result")
    private Result mResult;

    public List<City> getCities() {
        return mCities;
    }

    public Result getResult() {
        return mResult;
    }

    public void setCities(List<City> cities) {
        mCities = cities;
    }

    public void setResult(Result result) {
        mResult = result;
    }

    public static class City {

        @Attribute(name = "City_ID")
        private int mCityId;

        @Attribute(name = "City_owner_ID")
        private int mCityOwnerId;

        @Attribute(name = "CityName")
        private String mName;

        @Attribute(name = "RegionName")
        private String mRegionName;

        @Attribute(name = "Region_ID")
        private int mRegionId;

        @Attribute(name = "Region_Owner_ID")
        private int mRegionOwnerId;

        @Attribute(name = "Country_ID")
        private int mCountryId;

        @Attribute(name = "Country_Owner_ID")
        private int mCountryOwnerId;

        @Attribute(name = "COD")
        private int mCode;

        @Attribute(name = "DepId")
        private int mDepId;

        @Attribute(name = "DepOwnerId")
        private int mDepOwnerId;

        public int getCityId() {
            return mCityId;
        }

        public int getCityOwnerId() {
            return mCityOwnerId;
        }

        public void setCityId(int cityId) {
            mCityId = cityId;
        }

        public void setCityOwnerId(int cityOwnerId) {
            mCityOwnerId = cityOwnerId;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getRegionName() {
            return mRegionName;
        }

        public void setRegionName(String regionName) {
            mRegionName = regionName;
        }

        public int getRegionId() {
            return mRegionId;
        }

        public void setRegionId(int regionId) {
            mRegionId = regionId;
        }

        public int getRegionOwnerId() {
            return mRegionOwnerId;
        }

        public void setRegionOwnerId(int regionOwnerId) {
            mRegionOwnerId = regionOwnerId;
        }

        public int getCountryId() {
            return mCountryId;
        }

        public void setCountryId(int countryId) {
            mCountryId = countryId;
        }

        public int getCountryOwnerId() {
            return mCountryOwnerId;
        }

        public void setCountryOwnerId(int countryOwnerId) {
            mCountryOwnerId = countryOwnerId;
        }

        public int getCode() {
            return mCode;
        }

        public void setCode(int code) {
            mCode = code;
        }

        public int getDepId() {
            return mDepId;
        }

        public void setDepId(int depId) {
            mDepId = depId;
        }

        public int getDepOwnerId() {
            return mDepOwnerId;
        }

        public void setDepOwnerId(int depOwnerId) {
            mDepOwnerId = depOwnerId;
        }
    }

}
