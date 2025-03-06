package com.api.api.models.responses;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleMenuResponse {
    private String uuid;
    private boolean isShow;
    private boolean isDeleted;
    private boolean isCreated;
    private boolean isUpdated;
    private String menuName;
    private String menuIcon;
    private String menuPath;
}

