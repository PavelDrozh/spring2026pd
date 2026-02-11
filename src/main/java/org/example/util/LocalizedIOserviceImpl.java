package org.example.util;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LocalizedIOserviceImpl implements LocalizedIOservice {
    private final LocalizationService localizationService;
    private final IOService ioService;


    @Override
    public void printMessage(String key, Object ...args) {
        ioService.outputString(localizationService.getMessage(key,args));
    }

    @Override
    public void printString(String string) {
        ioService.outputString(string);
    }

    @Override
    public String readString() {
        return ioService.readString();
    }
}
