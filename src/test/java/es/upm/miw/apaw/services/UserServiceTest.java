package es.upm.miw.apaw.services;

import es.upm.miw.apaw.data.entities.User;
import es.upm.miw.apaw.services.exceptions.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserConflictByEmail() {
        User userDto = User.builder().id(UUID.randomUUID()).mobile("000000002").firstName("k").email("c1@gmail.com").build();
        assertThrows(ConflictException.class, () -> this.userService.create(userDto));
    }

    @Test
    void testCreateConflictByDni() {
        User userDto = User.builder().id(UUID.randomUUID()).mobile("000000003").firstName("k").identity("66666604T").build();
        assertThrows(ConflictException.class, () -> this.userService.create(userDto));
    }

    @Test
    void testUpdateUser() {
        User oldUser = userService.readByMobile("666666002");
        oldUser.setMobile("666666666");
        this.userService.updateByMobile("666666002", oldUser);
        User user = userService.readByMobile("666666666");
        assertThat(user)
                .isNotNull()
                .extracting(User::getFirstName)
                .isEqualTo(oldUser.getFirstName());
        oldUser.setMobile("666666002");
        this.userService.updateByMobile("666666666", oldUser);
    }

}
