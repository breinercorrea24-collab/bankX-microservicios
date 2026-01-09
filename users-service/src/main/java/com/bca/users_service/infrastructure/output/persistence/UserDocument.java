package com.bca.users_service.infrastructure.output.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    @Id
    private String id;
    private String typeDocument;
    private String numberDocument;
    private String nroPhone;
    private String imeiPhone;
    private String email;
    private String status;
}