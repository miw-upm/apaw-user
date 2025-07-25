package es.upm.miw.apaw.data.daos;

import es.upm.miw.apaw.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByMobile(String mobile);

    boolean existsByMobile(String mobile);

    boolean existsByEmail(String email);

    boolean existsByIdentity(String identity);

    @Query("""
                select u from User u where
                (coalesce(?1, '') = '' or u.mobile like concat('%', ?1, '%')) and
                (coalesce(?2, '') = '' or lower(u.firstName) like lower(concat('%', ?2, '%'))) and
                (coalesce(?3, '') = '' or lower(u.familyName) like lower(concat('%', ?3, '%')))
            """)
    List<User> findByMobileAndFirstNameAndFamilyNameNullSafe(
            String mobile, String firstName, String familyName);
}
