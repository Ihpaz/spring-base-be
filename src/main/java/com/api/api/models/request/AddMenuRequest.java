package com.api.api.models.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private Integer parentId;
    @NotBlank
    private String path;
    @NotBlank
    private String icon;
    @NotBlank
    private Integer priority;

}

