package com.kole.platform.identity.domain.repository;

import com.kole.platform.identity.domain.model.Role;
import com.kole.platform.identity.domain.model.RoleName;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository
    extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
}