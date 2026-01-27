package org.example.util;

import org.example.config.LocaleProvider;
import org.springframework.context.MessageSource;

public class LocalizationServiceImpl implements LocalizationService {

    public static final String BOOKS_TOTAL = "books.total";
    public static final String BOOKS_NAME = "books.name";
    public static final String BOOKS_ID = "books.id";
    public static final String BOOKS_AUTHOR = "books.author";
    public static final String BOOKS_GENRE = "books.genre";
    public static final String BOOKS_RELEASE_DATE = "books.release";
    public static final String BOOKS_DESCRIPTION = "books.description";
    public static final String BOOKS_FIND_BY_ID_REQ = "books.find.by.id";
    public static final String BOOKS_FIND_BY_ID_ERR = "books.find.by.id.err";
    public static final String BOOKS_PARSE_LOCAL_DATE_ERR = "books.parse.date.err";
    public static final String BOOKS_FIND_BY_NOT_FOUND = "books.find.by.id.not";
    public static final String BOOKS_CREATE_NAME = "books.create.name";
    public static final String BOOKS_CREATE_DESCRIPTION = "books.create.description";
    public static final String BOOKS_CREATE_RELEASE = "books.create.release";
    public static final String BOOKS_CREATE_GENRE = "books.create.genre";
    public static final String BOOKS_CREATE_AUTHOR = "books.create.author";
    public static final String BOOKS_UPDATE_NAME = "books.update.name";
    public static final String BOOKS_UPDATE_DESCRIPTION = "books.update.description";
    public static final String BOOKS_UPDATE_RELEASE = "books.update.release";
    public static final String BOOKS_UPDATE_GENRE = "books.update.genre";
    public static final String BOOKS_UPDATE_AUTHOR = "books.update.author";
    public static final String BOOKS_UPDATE_START = "books.update.start";
    public static final String BOOKS_NOT_FOUND = "books.not.found";
    public static final String BOOKS_DELETE_START = "books.delete.start";

    public static final String GENRES_NOT_FOUND = "genres.not.found";
    public static final String GENRES_PARSE_ID_ERR = "genres.find.by.id.err";
    public static final String AUTHORS_NOT_FOUND = "authors.not.found";
    public static final String AUTHORS_PARSE_ID_ERR = "authors.find.by.id.err";

    private final LocaleProvider localeProvider;
    private final MessageSource messageSource;

    public LocalizationServiceImpl(LocaleProvider localeProvider, MessageSource messageSource) {
        this.localeProvider = localeProvider;
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, localeProvider.getCurrent());
    }
}
