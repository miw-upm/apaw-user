package es.upm.miw.apaw.infrastructure.data.daos;

import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByMobile(String mobile);

    Optional<User> findByMobileAndActive(String mobile, Boolean active);

    List<User> findByActive(Boolean active);

    List<User> findByRoleIn(Collection<Role> roles);

    boolean existsByMobile(String mobile);
}
