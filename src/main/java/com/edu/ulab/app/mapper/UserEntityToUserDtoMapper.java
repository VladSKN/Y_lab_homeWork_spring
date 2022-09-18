package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityToUserDtoMapper {


    UserDto userEntityToUserDto(UserEntity userEntity);

    @Mapping(target = "bookList", expression = "java(new java.util.ArrayList())")
    UserEntity userDtoToUserEntity(UserDto userDto);
}
