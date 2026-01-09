package com.bca.users_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String typeDocument;
    private String numberDocument;
    private String nroPhone;
    private String imeiPhone;
    private String email;
    private String status;
}