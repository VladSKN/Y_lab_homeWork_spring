package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.BookEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO создать хранилище в котором будут содержаться данные
// сделать абстракции через которые можно будет производить операции с хранилищем
// продумать логику поиска и сохранения
// продумать возможные ошибки
// учесть, что при сохранеии юзера или книги, должен генерироваться идентификатор
// продумать что у узера может быть много книг и нужно создать эту связь
// так же учесть, что методы хранилища принимают другой тип данных - учесть это в абстракции

@Component
public class BookStorage implements BookRepository {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Map<Long, BookEntity> books = new HashMap<>();

    @Override
    public BookEntity insertBook(BookEntity bookEntity) {
        try {
            readWriteLock.writeLock().lock();
            books.putIfAbsent(bookEntity.getId(), bookEntity);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return bookEntity;
    }

    @Override
    public BookEntity findBookById(long bookEntityId) {
        Optional<BookEntity> bookEntity;
        try {
            readWriteLock.readLock().lock();
            bookEntity = books.values().stream()
                    .filter(book -> book.getId().equals(bookEntityId))
                    .findFirst();
        } finally {
            readWriteLock.readLock().unlock();
        }
        return bookEntity.orElse(null);
    }

    @Override
    public void deleteBookById(long bookEntityId) {
        try {
            readWriteLock.writeLock().lock();
            books.remove(bookEntityId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public BookEntity updateBook(BookEntity bookEntity) {
        BookEntity newBook;
        try {
            readWriteLock.writeLock().lock();

            newBook = BookEntity.builder()
                    .author(bookEntity.getAuthor())
                    .id(bookEntity.getId())
                    .pageCount(bookEntity.getPageCount())
                    .title(bookEntity.getTitle())
                    .userId(bookEntity.getUserId())
                    .build();

            deleteBookById(bookEntity.getId());
            insertBook(newBook);

        } finally {
            readWriteLock.writeLock().unlock();
        }
        return newBook;
    }

    @Override
    public List<BookEntity> findBookByUserId(long userId) {
        List<BookEntity> bookEntities;
        try {
            readWriteLock.readLock().lock();

            bookEntities = books.values().stream()
                    .filter(Objects::nonNull)
                    .filter(book -> book.getUserId().equals(userId))
                    .toList();
        } finally {
            readWriteLock.readLock().unlock();
        }
        return bookEntities;
    }

    @Override
    public void deleteBookByUserId(long userId) {
        try {
            readWriteLock.writeLock().lock();
            books.forEach((key, value) -> {
                if (Objects.equals(value.getUserId(), userId)) {
                    books.remove(key);
                }
            });
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
