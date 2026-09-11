package es.upm.miw.apaw.services;

import es.upm.miw.apaw.config.SeederForDev;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import es.upm.miw.apaw.services.criteria.UserFindCriteria;
import es.upm.miw.apaw.services.exceptions.ClientBusinessException;
import es.upm.miw.apaw.services.exceptions.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Test
    void testCreate() {
        User user = User.builder()
                .mobile("699999998")
                .firstName("NewUser")
                .role(Role.CUSTOMER)
                .active(true)
                .build();

        this.userService.create(user);

        assertThat(this.userService.find(new UserFindCriteria(null, user.getMobile())).toList())
                .singleElement()
                .satisfies(created -> {
                    assertThat(created.getId()).isNotNull();
                    assertThat(created.getMobile()).isEqualTo(user.getMobile());
                    assertThat(created.getFirstName()).isEqualTo(user.getFirstName());
                    assertThat(created.getRole()).isEqualTo(Role.CUSTOMER);
                    assertThat(created.getPassword()).isNotBlank();
                    assertThat(created.getRegistrationDate()).isEqualTo(LocalDate.now());
                });
    }

    @Test
    void testCreateWithExistingMobile() {
        User user = User.builder()
                .mobile(SeederForDev.MANAGER.getMobile())
                .firstName("DuplicatedMobile")
                .role(Role.CUSTOMER)
                .active(true)
                .build();
        assertThatThrownBy(() -> this.userService.create(user))
                .isInstanceOf(ClientBusinessException.class);
    }

    @Test
    void testDelete() {
        User user = User.builder()
                .mobile("699999996")
                .firstName("DeletedUser")
                .role(Role.CUSTOMER)
                .active(true)
                .build();

        this.userService.create(user);

        this.userService.delete(user.getId());

        assertThat(this.userService.find(new UserFindCriteria(null, user.getMobile())).toList())
                .isEmpty();
    }

    @Test
    void testFindAll() {
        assertThat(this.userService.find(new UserFindCriteria()).map(User::getMobile).toList())
                .contains(
                        SeederForDev.C_0.getMobile(),
                        SeederForDev.ADMIN.getMobile(),
                        SeederForDev.MANAGER.getMobile(),
                        SeederForDev.OPERATOR.getMobile()
                );
    }

    @Test
    void testFindByMobileFound() {
        List<User> users = this.userService.find(new UserFindCriteria(null, SeederForDev.MANAGER.getMobile())).toList();

        assertThat(users).singleElement()
                .extracting(User::getId, User::getMobile, User::getRole)
                .containsExactly(SeederForDev.MANAGER.getId(), SeederForDev.MANAGER.getMobile(), Role.MANAGER);
    }

    @Test
    void testFindByMobileNotFound() {
        assertThat(this.userService.find(new UserFindCriteria(null, "699999999")).toList())
                .isEmpty();
    }

    @Test
    void testFindByMobileAndActiveFound() {
        assertThat(this.userService.find(new UserFindCriteria(true, SeederForDev.MANAGER.getMobile())).toList())
                .singleElement()
                .extracting(User::getId)
                .isEqualTo(SeederForDev.MANAGER.getId());
    }

    @Test
    void testFindByMobileAndActiveNotFound() {
        assertThat(this.userService.find(new UserFindCriteria(false, SeederForDev.MANAGER.getMobile())).toList())
                .isEmpty();
    }
}
