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
public class MenuRequest {


    private String name;
    private Integer parentId;
    @NotNull
    private Integer page;

    @NotNull
    private Integer size;

}

