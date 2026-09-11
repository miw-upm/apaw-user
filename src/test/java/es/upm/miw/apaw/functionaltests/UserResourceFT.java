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
                .expectStatus().isBadRequest();
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
}
