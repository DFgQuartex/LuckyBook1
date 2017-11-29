package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "root")
@Namespace(reference = "http://spsr.ru/webapi/Info/GetCities/1.0")
@Order(elements = {"Params", "GetCities"})
public class GetCitiesRequestBody {

    @Element(name = "Params")
    @Namespace(reference = "http://spsr.ru/webapi/WA/1.0", prefix = "p")
    private GetCitiesParams mParams;

    @Element(name = "GetCities")
    private GetCities mGetCities;

    public GetCitiesRequestBody() {
        mParams = new GetCitiesParams();
    }

    public void setGetCities(GetCities getCities) {
        mGetCities = getCities;
    }

    public static class GetCities {

        @Attribute(name = "CityName")
        private String mCityName;

        @Attribute(name = "CountryName")
        private String mCountryName;

        public String getCityName() {
            return mCityName;
        }

        public void setCityName(String cityName) {
            mCityName = cityName;
        }

        public String getCountryName() {
            return mCountryName;
        }

        public void setCountryName(String countryName) {
            mCountryName = countryName;
        }
    }

    public static class GetCitiesParams extends Params {

        @Attribute(name = "Name", empty = "WAGetCities")
        private String mName;

        @Override
        public String getName() {
            return mName;
        }
    }
}
