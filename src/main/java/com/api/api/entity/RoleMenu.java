package com.api.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role_menu")
public class RoleMenu extends AuditMetaData {
    @Id
    private int id;

    private String uuid;

    private boolean is_created;
    private boolean is_updated;
    private boolean is_deleted;
    private boolean is_show;

    @ManyToOne
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "menu-id",referencedColumnName = "id")
    private Menu menu;

//    @Embedded
//    private AuditMetaData auditMetadata;

}
