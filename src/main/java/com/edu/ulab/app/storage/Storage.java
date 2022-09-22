package com.edu.ulab.app.storage;

//TODO создать хранилище в котором будут содержаться данные
// сделать абстракции через которые можно будет производить операции с хранилищем
// продумать логику поиска и сохранения
// продумать возможные ошибки
// учесть, что при сохранеии юзера или книги, должен генерироваться идентификатор
// продумать что у узера может быть много книг и нужно создать эту связь
// так же учесть, что методы хранилища принимают другой тип данных - учесть это в абстракции

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@Slf4j
public class Storage implements BookRepository, UserRepository {
    private final ReadWriteLock bookReadWriteLock = new ReentrantReadWriteLock();

    private final ReadWriteLock userReadWriteLock = new ReentrantReadWriteLock();

    private final Map<Long, BookEntity> books = new HashMap<>();

    private final Map<Long, UserEntity> users = new HashMap<>();

    @Override
    public BookEntity insertBook(BookEntity bookEntity) {
        try {
            bookReadWriteLock.writeLock().lock();
            books.putIfAbsent(bookEntity.getId(), bookEntity);
        } finally {
            bookReadWriteLock.writeLock().unlock();
        }
        return bookEntity;
    }

    @Override
    public BookEntity findBookById(long bookEntityId) {
        Optional<BookEntity> bookEntity;
        try {
            bookReadWriteLock.readLock().lock();
            bookEntity = books.values().stream()
                    .filter(book -> book.getId().equals(bookEntityId))
                    .findFirst();
        } finally {
            bookReadWriteLock.readLock().unlock();
        }
        return bookEntity.orElse(null);
    }

    @Override
    public void deleteBookById(long bookEntityId) {
        try {
            bookReadWriteLock.writeLock().lock();
            books.remove(bookEntityId);
        } finally {
            bookReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public BookEntity updateBook(BookEntity bookEntity) {
        BookEntity newBook;
        try {
            bookReadWriteLock.writeLock().lock();

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
            bookReadWriteLock.writeLock().unlock();
        }
        return newBook;
    }

    @Override
    public List<BookEntity> findBookByUserId(long userId) {
        List<BookEntity> bookEntities;
        try {
            bookReadWriteLock.readLock().lock();

            bookEntities = books.values().stream()
                    .filter(Objects::nonNull)
                    .filter(book -> book.getUserId().equals(userId))
                    .toList();
        } finally {
            bookReadWriteLock.readLock().unlock();
        }
        return bookEntities;
    }

    @Override
    public void deleteBookByUserId(long userId) {
        try {
            bookReadWriteLock.writeLock().lock();
            books.forEach((key, value) -> {
                if (Objects.equals(value.getUserId(), userId)) {
                    books.remove(key);
                }
            });
        } finally {
            bookReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        try {
            userReadWriteLock.writeLock().lock();
            users.putIfAbsent(userEntity.getId(), userEntity);
        } finally {
            userReadWriteLock.writeLock().unlock();
        }
        return userEntity;
    }

    @Override
    public UserEntity getUserById(long userEntityId) {
        Optional<UserEntity> userEntity;
        try {
            userReadWriteLock.readLock().lock();

            userEntity = users.values().stream()
                    .filter(book -> book.getId().equals(userEntityId))
                    .findFirst();
        } finally {
            userReadWriteLock.readLock().unlock();
        }
        return userEntity.orElse(null);
    }

    @Override
    public void deleteUserById(long userEntityId) {
        try {
            userReadWriteLock.writeLock().lock();

            users.remove(userEntityId);
        } finally {
            userReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        UserEntity newUser;
        Optional<UserEntity> user;

        try {
            userReadWriteLock.writeLock().lock();
            newUser = UserEntity.builder()
                    .age(userEntity.getAge())
                    .bookList(userEntity.getBookList())
                    .fullName(userEntity.getFullName())
                    .id(userEntity.getId())
                    .title(userEntity.getTitle())
                    .build();

            users.remove(userEntity.getId());
            user = Optional.ofNullable(createUser(newUser));
        } finally {
            userReadWriteLock.writeLock().unlock();
        }
        return user.orElse(null);
    }

    @Override
    public UserEntity getUserByName(String name) {
        Optional<UserEntity> user = Optional.empty();
        try {
            userReadWriteLock.readLock().lock();
            for (Map.Entry<Long, UserEntity> longUserEntityEntry : users.entrySet()) {
                if (Objects.equals(longUserEntityEntry.getValue().getFullName(), name)) {
                    user = Optional.ofNullable(longUserEntityEntry.getValue());
                }
            }
        } finally {
            userReadWriteLock.readLock().unlock();
        }
        return user.orElse(null);
    }
}
