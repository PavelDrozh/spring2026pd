INSERT INTO GENRES (GENRE_ID, GENRE)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Триллер'),
       (4, 'Документальный'),
       (5, 'Боевик')
ON CONFLICT (GENRE_ID) DO UPDATE
    SET GENRE = EXCLUDED.GENRE;

SELECT setval('genres_genre_id_seq', (SELECT MAX(genre_id) FROM genres));
