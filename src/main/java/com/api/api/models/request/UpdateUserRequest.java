package com.api.api.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {


    @Size(max = 100)
    private String password;

    @Size(max = 100)
    private String username;

    @Size(max = 100)
    private String name;

    @Email
    @Size(max = 100)
    private String email;
}
