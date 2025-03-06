package com.api.api.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RoleMenuRequest {
    private String menuId;
    private boolean is_created;
    private boolean is_updated;
    private boolean is_deleted;
    private boolean is_show;
    ;
}
