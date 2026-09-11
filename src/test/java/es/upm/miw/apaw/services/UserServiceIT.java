package es.upm.miw.apaw.services;

import es.upm.miw.apaw.config.SeederForDev;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import es.upm.miw.apaw.services.criteria.UserFindCriteria;
import es.upm.miw.apaw.services.exceptions.ClientBusinessException;
import es.upm.miw.apaw.services.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @Test
    void testCreateWithDefaultValues() {
        User user = User.builder()
                .mobile("699999993")
                .firstName("DefaultUser")
                .build();

        this.userService.create(user);

        assertThat(this.userService.find(new UserFindCriteria(null, user.getMobile())).toList())
                .singleElement()
                .satisfies(created -> {
                    assertThat(created.getRole()).isEqualTo(Role.CUSTOMER);
                    assertThat(created.getActive()).isTrue();
                    assertThat(created.getPassword()).isNotBlank();
                });
    }

    @Test
    void testCreateWithProvidedValues() {
        User user = User.builder()
                .mobile("699999992")
                .firstName("ProvidedUser")
                .password("provided-password")
                .role(Role.MANAGER)
                .active(false)
                .build();

        this.userService.create(user);

        assertThat(this.userService.find(new UserFindCriteria(null, user.getMobile())).toList())
                .singleElement()
                .extracting(User::getPassword, User::getRole, User::getActive)
                .containsExactly("provided-password", Role.MANAGER, false);
    }

    @Test
    void testReadFound() {
        User user = this.userService.read(SeederForDev.C_0.getId());

        assertThat(user)
                .extracting(User::getId, User::getMobile, User::getFirstName, User::getRole)
                .containsExactly(SeederForDev.C_0.getId(), SeederForDev.C_0.getMobile(),
                        SeederForDev.C_0.getFirstName(), SeederForDev.C_0.getRole());
    }

    @Test
    void testReadNotFound() {
        assertThatThrownBy(() -> this.userService.read(UUID.randomUUID()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testReadByMobileFound() {
        User user = this.userService.readByMobile(SeederForDev.MANAGER.getMobile());

        assertThat(user)
                .extracting(User::getId, User::getMobile, User::getRole)
                .containsExactly(SeederForDev.MANAGER.getId(), SeederForDev.MANAGER.getMobile(), Role.MANAGER);
    }

    @Test
    void testReadByMobileNotFound() {
        assertThatThrownBy(() -> this.userService.readByMobile("699999999"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindByActive() {
        assertThat(this.userService.find(new UserFindCriteria(true, null)).map(User::getMobile).toList())
                .contains(SeederForDev.C_0.getMobile(), SeederForDev.MANAGER.getMobile());
    }

    @Test
    void testFindByInactive() {
        User user = User.builder()
                .mobile("699999991")
                .firstName("InactiveUser")
                .active(false)
                .build();

        this.userService.create(user);

        assertThat(this.userService.find(new UserFindCriteria(false, null)).map(User::getMobile).toList())
                .contains(user.getMobile())
                .doesNotContain(SeederForDev.C_0.getMobile(), SeederForDev.MANAGER.getMobile());
    }

    @Test
    void testFindByBillable() {
        assertThat(this.userService.find(new UserFindCriteria(null, null, true)).map(User::getMobile).toList())
                .contains(SeederForDev.C_0.getMobile(), SeederForDev.MANAGER.getMobile())
                .doesNotContain(SeederForDev.C_6.getMobile());
    }

    @Test
    void testFindByNotBillable() {
        assertThat(this.userService.find(new UserFindCriteria(null, null, false)).map(User::getMobile).toList())
                .contains(SeederForDev.C_6.getMobile())
                .doesNotContain(SeederForDev.C_0.getMobile(), SeederForDev.MANAGER.getMobile());
    }

    @Test
    void testFindByMobileActiveAndBillableFound() {
        assertThat(this.userService.find(new UserFindCriteria(true, SeederForDev.C_0.getMobile(), true)).toList())
                .singleElement()
                .extracting(User::getId)
                .isEqualTo(SeederForDev.C_0.getId());
    }

    @Test
    void testFindByMobileActiveAndBillableNotFound() {
        assertThat(this.userService.find(new UserFindCriteria(true, SeederForDev.C_6.getMobile(), true)).toList())
                .isEmpty();
    }

    @Test
    void testFindByMobileActiveAndNotBillableFound() {
        assertThat(this.userService.find(new UserFindCriteria(true, SeederForDev.C_6.getMobile(), false)).toList())
                .singleElement()
                .extracting(User::getId)
                .isEqualTo(SeederForDev.C_6.getId());
    }

    @Test
    void testFindByMobileActiveAndNotBillableNotFound() {
        assertThat(this.userService.find(new UserFindCriteria(true, SeederForDev.C_0.getMobile(), false)).toList())
                .isEmpty();
    }

    @Test
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();

        this.userService.delete(id);

        assertThatThrownBy(() -> this.userService.read(id))
                .isInstanceOf(NotFoundException.class);
    }
}
