package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
public class Storage implements BookRepository, UserRepository {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Map<Long, BookEntity> books = new ConcurrentHashMap<>();

    private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();

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
        Optional<BookEntity> newBook;
        try {
            readWriteLock.writeLock().lock();

            newBook = Optional.ofNullable(BookEntity.builder()
                    .author(bookEntity.getAuthor())
                    .id(bookEntity.getId())
                    .pageCount(bookEntity.getPageCount())
                    .title(bookEntity.getTitle())
                    .user(bookEntity.getUser())
                    .userId(bookEntity.getUserId())
                    .build());

            deleteBookById(bookEntity.getId());
            insertBook(newBook.orElse(null));

        } finally {
            readWriteLock.writeLock().unlock();
        }
        return newBook.orElse(null);
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
        books.forEach((key, value) -> {
            if (Objects.equals(value.getUserId(), userId)) {
                books.remove(key);
            }
        });
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        try {
            readWriteLock.writeLock().lock();
            users.putIfAbsent(userEntity.getId(), userEntity);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return userEntity;
    }

    @Override
    public UserEntity getUserById(long userEntityId) {
        Optional<UserEntity> userEntity;
        try {
            readWriteLock.readLock().lock();

            userEntity = users.values().stream()
                    .filter(book -> book.getId().equals(userEntityId))
                    .findFirst();
        } finally {
            readWriteLock.readLock().unlock();
        }
        return userEntity.orElse(null);
    }

    @Override
    public void deleteUserById(long userEntityId) {
        try {
            readWriteLock.writeLock().lock();

            users.remove(userEntityId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        UserEntity newUser;
        Optional<UserEntity> user;

        try {
            readWriteLock.writeLock().lock();
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
            readWriteLock.writeLock().unlock();
        }
        return user.orElse(null);
    }

    @Override
    public UserEntity getUserByName(String name) {
        Optional<UserEntity> user = Optional.empty();
        try {
            readWriteLock.readLock().lock();
            for (Map.Entry<Long, UserEntity> longUserEntityEntry : users.entrySet()) {
                if (Objects.equals(longUserEntityEntry.getValue().getFullName(), name)) {
                    user = Optional.ofNullable(longUserEntityEntry.getValue());
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return user.orElse(null);
    }
}
