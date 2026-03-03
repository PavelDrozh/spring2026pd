package org.example.util;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.example.util.LocalizationServiceImpl.BOOKS_PARSE_LOCAL_DATE_ERR;

@AllArgsConstructor
public class UsersCommandGetter {

    private final LocalizedIOservice ioService;

    public long getIdFromUser (String errMessage) {
        long answer = 0;
        boolean notNumber = true;
        while (notNumber) {
            try {
                answer = Long.parseLong(getNotEmptyString());
                notNumber = false;
            } catch (NumberFormatException e) {
                ioService.printMessage(errMessage);
            }
        }
        return answer;
    }

    public String getNotEmptyString() {
        String answer = "";
        boolean notString = true;
        while (notString) {
            answer = ioService.readString();
            if(!answer.isEmpty()) {
                notString = false;
            }
        }
        return answer;
    }

    public LocalDate getLocalDateFromUser() {
        LocalDate answer = null;
        boolean notDate = true;
        while (notDate) {
            try {
                answer = LocalDate.parse(getNotEmptyString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                notDate = false;
            } catch (DateTimeParseException e) {
                ioService.printMessage(BOOKS_PARSE_LOCAL_DATE_ERR);
            }
        }
        return answer;
    }

}
