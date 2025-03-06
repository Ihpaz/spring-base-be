package com.api.api.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AddRoleRequest {

    @NotBlank
    private String role;

    private List<RoleMenuRequest> roleMenu;


}

