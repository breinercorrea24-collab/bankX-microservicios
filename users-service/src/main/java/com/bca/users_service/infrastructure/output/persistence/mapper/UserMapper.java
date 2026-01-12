package com.bca.users_service.infrastructure.output.persistence.mapper;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.infrastructure.output.persistence.entity.UserDocument;

public class UserMapper {

    public static UserDocument toDocument(User user) {
        return new UserDocument(
            user.getId(),
            user.getTypeDocument(),
            user.getNumberDocument(),
            user.getNroPhone(),
            user.getImeiPhone(),
            user.getEmail(),
            user.getStatus()
        );
    }

    public static User toDomain(UserDocument document) {
        return new User(
            document.getId(),
            document.getTypeDocument(),
            document.getNumberDocument(),
            document.getNroPhone(),
            document.getImeiPhone(),
            document.getEmail(),
            document.getStatus()
        );
    }
}
