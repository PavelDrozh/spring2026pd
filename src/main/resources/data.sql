INSERT INTO GENRES (GENRE_ID, GENRE)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Триллер'),
       (4, 'Документальный'),
       (5, 'Боевик')
ON CONFLICT (GENRE_ID) DO UPDATE
    SET GENRE = EXCLUDED.GENRE;

INSERT INTO AUTHORS (AUTHOR_ID, NAME, SURNAME, BIRTHDAY)
VALUES (1, 'G', 'G', '2000-01-01'),
       (2, 'A', 'A', '2000-01-01'),
       (3, 'B', 'B', '2000-01-01'),
       (4, 'R', 'R', '2000-01-01'),
       (5, 'N', 'N', '2000-01-01')
ON CONFLICT (AUTHOR_ID) DO UPDATE
    SET NAME = EXCLUDED.NAME,
        SURNAME = EXCLUDED.SURNAME,
        BIRTHDAY = EXCLUDED.BIRTHDAY;

INSERT INTO BOOKS (BOOK_ID, GENRE_ID, AUTHOR_ID, NAME, DESCRIPTION, RELEASE_DATE)
VALUES (1, 1, 1, 'book G', 'description G', '2000-01-01'),
       (2, 2, 2, 'book A', 'description A', '2000-01-01'),
       (3, 3, 3, 'book B', 'description B', '2000-01-01'),
       (4, 4, 4, 'book R', 'description R', '2000-01-01'),
       (5, 5, 5, 'book N', 'description N', '2000-01-01')
ON CONFLICT (BOOK_ID) DO UPDATE
    SET NAME = EXCLUDED.NAME,
        GENRE_ID = EXCLUDED.GENRE_ID,
        AUTHOR_ID = EXCLUDED.AUTHOR_ID,
        RELEASE_DATE = EXCLUDED.RELEASE_DATE,
        DESCRIPTION = EXCLUDED.DESCRIPTION;

INSERT INTO COMMENTS (COMMENT_ID, COMMENT, BOOK_ID)
VALUES (1, 'first comment', 1),
       (2, 'second comment', 2),
       (3, 'third comment', 3),
       (4, 'fourth comment', 4),
       (5, 'fifth comment', 5)
    ON CONFLICT (COMMENT_ID) DO UPDATE
                                 SET COMMENT_ID = EXCLUDED.COMMENT_ID,
                                    COMMENT = EXCLUDED.COMMENT,
                                     BOOK_ID = EXCLUDED.BOOK_ID;

INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, ENABLED, ROLE)
VALUES (1, 'admin', '{noop}admin', true, 'ROLE_ADMIN'),
       (2, 'user', '{noop}password', true, 'ROLE_USER')
ON CONFLICT (USER_ID) DO UPDATE
    SET USERNAME = EXCLUDED.USERNAME,
        PASSWORD = EXCLUDED.PASSWORD,
        ENABLED = EXCLUDED.ENABLED,
        ROLE = EXCLUDED.ROLE;

-- Синхронизация последовательностей после вставки данных
SELECT setval('genres_genre_id_seq', (SELECT MAX(genre_id) FROM genres));
SELECT setval('authors_author_id_seq', (SELECT MAX(author_id) FROM authors));
SELECT setval('books_book_id_seq', (SELECT MAX(book_id) FROM books));
SELECT setval('comments_comment_id_seq', (SELECT MAX(comment_id) FROM comments));
SELECT setval('users_user_id_seq', (SELECT MAX(user_id) FROM users));
