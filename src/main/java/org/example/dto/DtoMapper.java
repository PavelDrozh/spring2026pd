package org.example.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Comment;
import org.example.model.Genre;

@Mapper
public interface DtoMapper {

    DtoMapper INSTANCE = Mappers.getMapper(DtoMapper.class);

    BookDto toBookDto(Book book);

    @Mapping(target = "bookId", source = "book.id")
    CommentDto toCommentDto(Comment comment);

    GenreDto toGenreDto(Genre genre);

    AuthorDto toAuthorDto(Author author);
}
