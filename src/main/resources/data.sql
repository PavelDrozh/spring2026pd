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

-- Синхронизация последовательностей после вставки данных
SELECT setval('genres_genre_id_seq', (SELECT MAX(genre_id) FROM genres));
SELECT setval('authors_author_id_seq', (SELECT MAX(author_id) FROM authors));
SELECT setval('books_book_id_seq', (SELECT MAX(book_id) FROM books));
/*
MERGE INTO GENRES AS g
USING (
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Триллер'),
           (4, 'Документальный'),
           (5, 'Боевик')
) AS new_data(GENRE_ID, NAME)
ON g.GENRE_ID = new_data.GENRE_ID AND g.NAME = new_data.NAME
WHEN MATCHED THEN UPDATE SET GENRE_ID = new_data.GENRE_ID, NAME = new_data.NAME
WHEN NOT MATCHED THEN INSERT (GENRE_ID, NAME) VALUES (new_data.GENRE_ID, new_data.NAME);

MERGE INTO AUTHORS AS g
USING (
    VALUES (1, 'G', 'G', '2000-01-01'),
           (2, 'A', 'A', '2000-01-01'),
           (3, 'B', 'B', '2000-01-01'),
           (4, 'R', 'R', '2000-01-01'),
           (5, 'N', 'N', '2000-01-01')
) AS new_data(AUTHOR_ID, NAME, SURNAME, BIRTHDAY)
ON g.AUTHOR_ID = new_data.AUTHOR_ID
WHEN MATCHED THEN UPDATE SET AUTHOR_ID = new_data.AUTHOR_ID, NAME = new_data.NAME,
                             SURNAME = new_data.SURNAME, BIRTHDAY = new_data.BIRTHDAY
WHEN NOT MATCHED THEN INSERT (AUTHOR_ID, NAME, SURNAME, BIRTHDAY)
                      VALUES (new_data.AUTHOR_ID, new_data.NAME, new_data.SURNAME, new_data.BIRTHDAY);
*/