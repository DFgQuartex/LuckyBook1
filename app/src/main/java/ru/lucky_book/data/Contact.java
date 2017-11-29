
package ru.lucky_book.data;


import com.google.gson.annotations.SerializedName;

public class Contact {

    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private String phone;
    @SerializedName("birthday_at_millis")
    private String birthdayAtMillis;

    public Contact(String name, String surname, String patronymic, String email, String phone, String birthdayAtMillis) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.phone = phone;
        this.birthdayAtMillis = birthdayAtMillis;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname The surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return The patronymic
     */
    public String getPatronymic() {
        return patronymic;
    }

    /**
     * @param patronymic The patronymic
     */
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The birthdayAtMillis
     */
    public String getBirthdayAtMillis() {
        return birthdayAtMillis;
    }

    /**
     * @param birthdayAtMillis The birthday_at_millis
     */
    public void setBirthdayAtMillis(String birthdayAtMillis) {
        this.birthdayAtMillis = birthdayAtMillis;
    }

}
