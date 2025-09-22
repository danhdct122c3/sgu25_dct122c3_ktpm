package fpl.sd.backend.repository;

import fpl.sd.backend.constant.RoleConstants;
import fpl.sd.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoles(RoleConstants.Role roleName);
}
