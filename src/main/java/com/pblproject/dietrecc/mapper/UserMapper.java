package com.pblproject.dietrecc.mapper;

import com.pblproject.dietrecc.dto.UserDTO;
import com.pblproject.dietrecc.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface UserMapper {

    public UserDTO toDto(User user);

    void updateUserFromDto(UserDTO dto, @MappingTarget User user);

}
