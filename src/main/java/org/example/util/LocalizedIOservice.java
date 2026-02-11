package org.example.util;

public interface LocalizedIOservice {

    void printMessage(String key, Object ...args);
    void printString(String string);
    String readString();
}
