package com.webgara.module.user.mapper;

import com.webgara.module.user.dto.UpdateProfileRequest;
import com.webgara.module.user.dto.UserProfileResponse;
import com.webgara.module.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "staffInfo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "address.coordinates", ignore = true)
    void updateUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);

    UserProfileResponse toProfileResponse(User user);

    UserProfileResponse.AddressDto toAddressDto(User.Address address);
}
