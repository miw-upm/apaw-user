package es.upm.miw.apaw.functionaltests;

import es.upm.miw.apaw.resources.dtos.UserDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static es.upm.miw.apaw.resources.UserResource.*;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class UserResourceFT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testReadUser() {
        webTestClient.get()
                .uri(USERS + ID_ID, "aaaaaaaa-bbbb-cccc-dddd-eeeeffff0000")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getMobile()).isEqualTo("666000660");
                    assertThat(user.getFirstName()).isEqualTo("user0");
                });
    }

    @Test
    void testReadByMobile() {
        webTestClient.get()
                .uri(USERS + MOBILE + MOBILE_ID, "666000661")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getMobile()).isEqualTo("666000661");
                    assertThat(user.getFirstName()).isEqualTo("user1");
                });
    }

    @Test
    void testUpdateByMobile() {
        String mobile = "666000660";
        UserDto userDto = webTestClient.get()
                .uri(USERS + MOBILE + MOBILE_ID, mobile)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(userDto).isNotNull();
        String oldName = userDto.getFirstName();

        userDto.setFirstName("update");
        webTestClient.put()
                .uri(USERS + MOBILE + MOBILE_ID, mobile)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(updated -> assertThat(updated.getFirstName()).isEqualTo("update"));

        userDto.setFirstName(oldName);
        webTestClient.put()
                .uri(USERS + MOBILE + MOBILE_ID, mobile)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testReadUserNotFound() {
        webTestClient.get()
                .uri(USERS + ID_ID, "aaaaaaaa-bbbb-cccc-dddd-eeeeffff9999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateConflictWithMobile() {
        UserDto userDto = UserDto.builder().mobile("666000660").firstName("daemon").build();

        webTestClient.post()
                .uri(USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreate() {
        UserDto userDto = UserDto.builder().mobile("666000666").firstName("new").build();

        webTestClient.post()
                .uri(USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateConflictWithEmail() {
        UserDto userDto = UserDto.builder()
                .mobile("666666666")
                .firstName("daemon")
                .email("user0@gmail.com")
                .build();

        webTestClient.post()
                .uri(USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateConflictWithIdentity() {
        UserDto userDto = UserDto.builder()
                .mobile("666666666")
                .firstName("daemon")
                .identity("66666600D")
                .build();

        webTestClient.post()
                .uri(USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateUserWithoutNumber() {
        UserDto userDto = UserDto.builder()
                .mobile(null)
                .firstName("daemon")
                .build();

        webTestClient.post()
                .uri(USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testFind() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USERS)
                        .queryParam("mobile", "666000660")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> {
                    assertThat(users).isNotNull().isNotEmpty();
                    UserDto first = users.getFirst();
                    assertThat(first.getFirstName()).isNotNull();
                    assertThat(first.getAddress()).isNull();
                });
    }

    @Test
    void testFindWithProjection() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(USERS)
                        .queryParam("projection", "true")
                        .queryParam("mobile", "666000660")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> {
                    assertThat(users).isNotNull().isNotEmpty();
                    UserDto first = users.getFirst();
                    assertThat(first.getFirstName()).isNotNull();
                    assertThat(first.getAddress()).isNotNull();
                });
    }
}
