package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "root")
@Namespace(reference = "http://spsr.ru/webapi/usermanagment/login/1.0")
@Order(elements = {"Params", "Login"})
public class LoginRequestBody {

    @Element(name = "Params")
    @Namespace(reference = "http://spsr.ru/webapi/WA/1.0", prefix = "p")
    private LoginParams mParams;

    @Element(name = "Login")
    private Login mLogin;

    public LoginRequestBody() {
        mParams = new LoginParams();
        mLogin = new Login();
    }

    public static class Login {

        @Attribute(name = "Login", empty = "Aprint")
        private String mLogin;

        @Attribute(name = "Pass", empty = "N0!123")
        private String mPassword;

        @Attribute(name = "UserAgent", empty = "LuckyBook")
        private String mUserAgent;
    }

    public static class LoginParams extends Params {

        @Attribute(name = "Name", empty = "WALogin")
        private String mName;

        @Override
        public String getName() {
            return mName;
        }
    }
}
