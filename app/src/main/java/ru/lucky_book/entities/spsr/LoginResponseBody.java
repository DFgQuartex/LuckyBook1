package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name = "root")
@Order(elements = {"Result", "Login"})
public class LoginResponseBody {

    @Element(name = "Result")
    private Result mResult;

    @Element(name = "Login")
    private Login mLogin;

    public Login getLogin() {
        return mLogin;
    }

    public void setResult(Result result) {
        mResult = result;
    }

    public void setLogin(Login login) {
        mLogin = login;
    }

    public static class Login {

        @Attribute(name = "SID")
        private String mSid;

        @Attribute(name = "IsAdmin")
        private boolean mAdmin;

        public String getSid() {
            return mSid;
        }

        public boolean isAdmin() {
            return mAdmin;
        }

        public void setSid(String sid) {
            mSid = sid;
        }

        public void setAdmin(boolean admin) {
            mAdmin = admin;
        }
    }
}
