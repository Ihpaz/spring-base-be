package com.api.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private int id;

    private String uuid;

    private String name;

    private String path;

    private String icon;

    private int priority;

    @ManyToOne
    @JoinColumn(name = "parent_id")  // This column stores the parent menu reference
    private Menu parent;

    @OneToMany(mappedBy = "menu")
    private List<RoleMenu> roleMenu;

}
