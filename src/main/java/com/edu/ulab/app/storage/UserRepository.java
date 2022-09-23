package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    UserEntity createUser(UserEntity userEntity);

    UserEntity getUserById(long id);

    void deleteUserById(long id);

    UserEntity updateUser(UserEntity userEntity);

    UserEntity getUserByName(String name);
}
