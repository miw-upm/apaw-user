package es.upm.api.functionaltests;

import es.upm.api.resources.dtos.UserDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static es.upm.api.resources.UserResource.*;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserResourceFT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String baseUrl;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + USERS;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testReadUser() {
        String url = this.baseUrl + ID_ID.replace("{id}", "aaaaaaaa-bbbb-cccc-dddd-eeeeffff0000");
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMobile()).isEqualTo("6");
        assertThat(response.getBody().getFirstName()).isEqualTo("admin");
    }

    @Test
    void testReadByMobile() {
        String url = this.baseUrl + MOBILE + MOBILE_ID.replace("{mobile}", "66");
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMobile()).isEqualTo("66");
        assertThat(response.getBody().getFirstName()).isEqualTo("customer");
    }

    @Test
    void testReadUserNotFound() {
        String url = this.baseUrl + ID_ID.replace("{id}", "aaaaaaaa-bbbb-cccc-dddd-eeeeffff9999");
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateConflict() {
        UserDto userDto = UserDto.builder().mobile("666666001").firstName("daemon").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, this.headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(this.baseUrl, HttpMethod.POST, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateUserWithoutNumber() {
        UserDto userDto = UserDto.builder().mobile(null).firstName("daemon").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, this.headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(this.baseUrl, HttpMethod.POST, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testFindDoesNotContainNull() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).doesNotContain("null");
        log.debug("json: {}", response.getBody());
    }

    @Test
    void testFindAll() {
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(baseUrl, UserDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }

}
