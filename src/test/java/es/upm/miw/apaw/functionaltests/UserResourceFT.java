package es.upm.miw.apaw.functionaltests;

import es.upm.miw.apaw.config.SeederForDev;
import es.upm.miw.apaw.infrastructure.data.daos.UserRepository;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import es.upm.miw.apaw.resources.UserResource;
import es.upm.miw.apaw.resources.dtos.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserResourceFT {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + this.port)
                .build();
    }

    @Test
    void testFindAll() {
        this.restTestClient.get()
                .uri(UserResource.USERS)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .extracting(UserDto::getMobile)
                        .contains(
                                SeederForDev.C_0.getMobile(),
                                SeederForDev.ADMIN.getMobile(),
                                SeederForDev.MANAGER.getMobile(),
                                SeederForDev.OPERATOR.getMobile()
                        ));
    }

    @Test
    void testFindByMobile() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.MANAGER.getMobile())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile, UserDto::getFirstName)
                        .containsExactly(SeederForDev.MANAGER.getMobile(), SeederForDev.MANAGER.getFirstName()));
    }

    @Test
    void testCreate() {
        String mobile = "699999997";
        String firstName = "ResourceUser";
        UserDto userDto = UserDto.builder()
                .mobile(mobile)
                .firstName(firstName)
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", mobile)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile, UserDto::getFirstName)
                        .containsExactly(mobile, firstName));
    }

    @Test
    void testCreateWithExistingMobile() {
        UserDto userDto = UserDto.builder()
                .mobile(SeederForDev.MANAGER.getMobile())
                .firstName("DuplicatedMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        String mobile = "699999995";
        User user = User.builder()
                .id(id)
                .mobile(mobile)
                .firstName("DeletedResourceUser")
                .role(Role.CUSTOMER)
                .active(true)
                .build();

        this.userRepository.save(user);

        this.restTestClient.delete()
                .uri(UserResource.USERS + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", mobile)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users).isEmpty());
    }

    @Test
    void testReadById() {
        this.restTestClient.get()
                .uri("/" + SeederForDev.C_0.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertThat(user)
                        .extracting(UserDto::getMobile, UserDto::getFirstName, UserDto::getFamilyName, UserDto::getEmail, UserDto::getAddress, UserDto::getRole, UserDto::getActive)
                        .containsExactly(SeederForDev.C_0.getMobile(), SeederForDev.C_0.getFirstName(), SeederForDev.C_0.getFamilyName(), SeederForDev.C_0.getEmail(), SeederForDev.C_0.getAddress(), SeederForDev.C_0.getRole(), SeederForDev.C_0.getActive()));
    }

    @Test
    void testReadByIdNotFound() {
        this.restTestClient.get()
                .uri("/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadByMobile() {
        this.restTestClient.get()
                .uri("/" + SeederForDev.MANAGER.getMobile())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertThat(user)
                        .extracting(UserDto::getMobile, UserDto::getFirstName, UserDto::getRole)
                        .containsExactly(SeederForDev.MANAGER.getMobile(), SeederForDev.MANAGER.getFirstName(), SeederForDev.MANAGER.getRole()));
    }

    @Test
    void testReadByShortMobile() {
        this.restTestClient.get()
                .uri("/" + SeederForDev.ADMIN_6.getMobile())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertThat(user)
                        .extracting(UserDto::getMobile, UserDto::getFirstName)
                        .containsExactly(SeederForDev.ADMIN_6.getMobile(), SeederForDev.ADMIN_6.getFirstName()));
    }

    @Test
    void testReadByMobileNotFound() {
        this.restTestClient.get()
                .uri("/699999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindByMobileNotFound() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", "699999999")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users).isEmpty());
    }

    @Test
    void testFindByActive() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("active", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .extracting(UserDto::getMobile)
                        .contains(SeederForDev.C_0.getMobile(), SeederForDev.MANAGER.getMobile()));
    }

    @Test
    void testFindByMobileAndActive() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.MANAGER.getMobile())
                        .queryParam("active", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile)
                        .isEqualTo(SeederForDev.MANAGER.getMobile()));
    }

    @Test
    void testFindByMobileAndActiveNotFound() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.MANAGER.getMobile())
                        .queryParam("active", false)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users).isEmpty());
    }

    @Test
    void testFindByBillable() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("billable", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .extracting(UserDto::getMobile)
                        .contains(SeederForDev.C_0.getMobile())
                        .doesNotContain(SeederForDev.C_6.getMobile()));
    }

    @Test
    void testFindByNotBillable() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("billable", false)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .extracting(UserDto::getMobile)
                        .contains(SeederForDev.C_6.getMobile())
                        .doesNotContain(SeederForDev.C_0.getMobile()));
    }

    @Test
    void testFindByMobileActiveAndBillable() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.C_0.getMobile())
                        .queryParam("active", true)
                        .queryParam("billable", true)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile)
                        .isEqualTo(SeederForDev.C_0.getMobile()));
    }

    @Test
    void testFindByMobileActiveAndBillableNotFound() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.C_0.getMobile())
                        .queryParam("active", true)
                        .queryParam("billable", false)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users).isEmpty());
    }

    @Test
    void testFindReturnsSummary() {
        this.restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(UserResource.USERS)
                        .queryParam("mobile", SeederForDev.C_0.getMobile())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto[].class)
                .value(users -> assertThat(users)
                        .singleElement()
                        .extracting(UserDto::getMobile, UserDto::getEmail, UserDto::getAddress, UserDto::getRole)
                        .containsExactly(SeederForDev.C_0.getMobile(), SeederForDev.C_0.getEmail(), null, null));
    }

    @Test
    void testCreateWithoutMobile() {
        UserDto userDto = UserDto.builder()
                .firstName("InvalidMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithEmptyMobile() {
        UserDto userDto = UserDto.builder()
                .mobile("")
                .firstName("InvalidMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithBlankMobile() {
        UserDto userDto = UserDto.builder()
                .mobile(" ")
                .firstName("InvalidMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithInvalidMobile() {
        UserDto userDto = UserDto.builder()
                .mobile("invalid")
                .firstName("InvalidMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithInvalidMobileLength() {
        UserDto userDto = UserDto.builder()
                .mobile("123")
                .firstName("InvalidMobile")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithoutFirstName() {
        UserDto userDto = UserDto.builder()
                .mobile("699999994")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithEmptyFirstName() {
        UserDto userDto = UserDto.builder()
                .mobile("699999994")
                .firstName("")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithBlankFirstName() {
        UserDto userDto = UserDto.builder()
                .mobile("699999994")
                .firstName(" ")
                .build();

        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithMalformedJson() {
        this.restTestClient.post()
                .uri(UserResource.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"mobile\":")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testDeleteNotFound() {
        this.restTestClient.delete()
                .uri(UserResource.USERS + "/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

}
