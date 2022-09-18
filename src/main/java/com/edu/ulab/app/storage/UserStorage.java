package com.edu.ulab.app.storage;

//TODO создать хранилище в котором будут содержаться данные
// сделать абстракции через которые можно будет производить операции с хранилищем
// продумать логику поиска и сохранения
// продумать возможные ошибки
// учесть, что при сохранеии юзера или книги, должен генерироваться идентификатор
// продумать что у узера может быть много книг и нужно создать эту связь
// так же учесть, что методы хранилища принимают другой тип данных - учесть это в абстракции

import com.edu.ulab.app.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class UserStorage implements UserRepository {

    private final Map<Long, UserEntity> users = new HashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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
