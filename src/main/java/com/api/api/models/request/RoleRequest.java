package com.api.api.models.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleRequest {

    private String role;
    private List<RoleMenuRequest> roleMenu;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
}
