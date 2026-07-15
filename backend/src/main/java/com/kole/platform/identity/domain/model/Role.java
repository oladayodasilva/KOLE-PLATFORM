package com.kole.platform.identity.domain.model;

import com.kole.platform.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "roles",
    schema = "identity"
)
public class Role extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(
        name = "name",
        nullable = false,
        unique = true,
        length = 60
    )
    private RoleName name;

    @Column(
        name = "description",
        length = 255
    )
    private String description;

    protected Role() {
    }

    public Role(
        RoleName name,
        String description
    ) {
        this.name = name;
        this.description = description;
    }

    public RoleName getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}