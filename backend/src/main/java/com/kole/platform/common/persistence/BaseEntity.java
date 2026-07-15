package com.kole.platform.common.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(
        name = "id",
        nullable = false,
        updatable = false
    )
    private UUID id;

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null
            || getClass() != object.getClass()) {
            return false;
        }

        BaseEntity that = (BaseEntity) object;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}