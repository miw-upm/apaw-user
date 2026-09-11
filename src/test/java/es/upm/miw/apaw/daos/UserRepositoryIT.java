package es.upm.miw.apaw.daos;

import es.upm.miw.apaw.config.SeederForDev;
import es.upm.miw.apaw.infrastructure.data.daos.UserRepository;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByMobileFound() {
        assertThat(this.userRepository.findByMobile(SeederForDev.MANAGER.getMobile()))
                .isPresent()
                .get()
                .extracting(User::getId, User::getMobile, User::getRole)
                .containsExactly(SeederForDev.MANAGER.getId(), SeederForDev.MANAGER.getMobile(), Role.MANAGER);
    }

    @Test
    void testFindByMobileNotFound() {
        assertThat(this.userRepository.findByMobile("699999999")).isEmpty();
    }

    @Test
    void testFindByRoleIn() {
        assertThat(this.userRepository.findByRoleIn(List.of(Role.ADMIN, Role.MANAGER)))
                .extracting(User::getMobile)
                .containsExactlyInAnyOrder(
                        SeederForDev.ADMIN_6.getMobile(),
                        SeederForDev.ADMIN.getMobile(),
                        SeederForDev.MANAGER.getMobile()
                );
    }
}
