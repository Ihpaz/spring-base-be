package com.api.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends AuditMetaData implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private int id;

    private String uuid;

    private String username;

    private String password;

    private String avatar;

    private String email;

    private String name;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // You can return roles/authorities here if needed@ManyToOne
    }

    @ManyToOne()
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;

//    @Embedded
//    private AuditMetaData auditMetadata;

}
