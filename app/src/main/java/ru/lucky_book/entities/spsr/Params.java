package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;

public abstract class Params {

    @Attribute(name = "Ver", empty = "1.0")
    private String mVersion;

    public abstract String getName();

    public String getVersion() {
        return mVersion;
    }
}
