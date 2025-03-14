package com.api.api.models.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserRequest {

    private String role;

    private String username;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
