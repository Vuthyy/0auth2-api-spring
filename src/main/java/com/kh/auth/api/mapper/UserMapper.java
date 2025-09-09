package com.kh.auth.api.mapper;

import com.kh.auth.api.dto.response.UserResponse;
import com.kh.auth.api.dto.response.UserResponseDto;
import com.kh.auth.api.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserResponseDto to(UserResponse user);

    UserResponse toResponse(UserEntity userEntity);
}
