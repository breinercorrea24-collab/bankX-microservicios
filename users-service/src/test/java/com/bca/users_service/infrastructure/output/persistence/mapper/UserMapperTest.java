package com.bca.users_service.infrastructure.output.persistence.mapper;

import com.bca.users_service.domain.model.User;
import com.bca.users_service.infrastructure.output.persistence.entity.UserDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    @DisplayName("toDocument should copy every field from domain to document")
    void toDocumentCopiesAllFields() {
        User domain = new User(
                "usr-1",
                "DNI",
                "12345678",
                "+51987654321",
                "imei",
                "user@bankx.com",
                "ACTIVE"
        );

        UserDocument document = UserMapper.toDocument(domain);

        assertEquals(domain.getId(), document.getId());
        assertEquals(domain.getTypeDocument(), document.getTypeDocument());
        assertEquals(domain.getNumberDocument(), document.getNumberDocument());
        assertEquals(domain.getNroPhone(), document.getNroPhone());
        assertEquals(domain.getImeiPhone(), document.getImeiPhone());
        assertEquals(domain.getEmail(), document.getEmail());
        assertEquals(domain.getStatus(), document.getStatus());
    }

    @Test
    @DisplayName("toDomain should copy every field from document to domain")
    void toDomainCopiesAllFields() {
        UserDocument document = new UserDocument(
                "usr-2",
                "PASSPORT",
                "P123",
                "+51900000000",
                "imei2",
                "another@bankx.com",
                "INACTIVE"
        );

        User domain = UserMapper.toDomain(document);

        assertEquals(document.getId(), domain.getId());
        assertEquals(document.getTypeDocument(), domain.getTypeDocument());
        assertEquals(document.getNumberDocument(), domain.getNumberDocument());
        assertEquals(document.getNroPhone(), domain.getNroPhone());
        assertEquals(document.getImeiPhone(), domain.getImeiPhone());
        assertEquals(document.getEmail(), domain.getEmail());
        assertEquals(document.getStatus(), domain.getStatus());
    }
}
