package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.BookEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository {

    BookEntity insertBook(BookEntity bookEntity);

    BookEntity findBookById(long id);

    void deleteBookById(long id);

    BookEntity updateBook(BookEntity bookEntity);

    List<BookEntity> findBookByUserId(long userId);

    void deleteBookByUserId(long userId);
}
