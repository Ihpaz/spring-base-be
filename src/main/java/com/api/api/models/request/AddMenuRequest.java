package com.api.api.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddMenuRequest {

    @NotBlank
    private String name;
    private Integer parentId;
    @NotBlank
    private String path;
    @NotBlank
    private String icon;
    @NotNull
    @Positive
    private Integer priority;

}

