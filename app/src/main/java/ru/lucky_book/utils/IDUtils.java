package ru.lucky_book.utils;

import java.util.UUID;

/**
 * Created by demafayz on 24.08.16.
 */
public class IDUtils {

    public static String idGenerate() {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        return id;
    }
}
