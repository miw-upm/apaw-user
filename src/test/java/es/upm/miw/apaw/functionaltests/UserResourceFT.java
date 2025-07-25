package es.upm.miw.apaw.functionaltests;

import es.upm.miw.apaw.resources.dtos.UserDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static es.upm.miw.apaw.resources.UserResource.*;
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
        assertThat(response.getBody().getMobile()).isEqualTo("666000660");
        assertThat(response.getBody().getFirstName()).isEqualTo("user0");
    }

    @Test
    void testReadByMobile() {
        String url = this.baseUrl + MOBILE + MOBILE_ID.replace("{mobile}", "666000661");
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMobile()).isEqualTo("666000661");
        assertThat(response.getBody().getFirstName()).isEqualTo("user1");
    }

    @Test
    void testUpdateByMobile() {
        String url = this.baseUrl + MOBILE + MOBILE_ID.replace("{mobile}", "666000660");
        UserDto userDto = restTemplate.getForEntity(url, UserDto.class).getBody();
        assertThat(userDto).isNotNull();
        String oldName = userDto.getFirstName();
        userDto.setFirstName("update");
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("update");
        userDto.setFirstName(oldName);
        request = new HttpEntity<>(userDto, headers);
        restTemplate.exchange(url, HttpMethod.PUT, request, UserDto.class);
    }

    @Test
    void testReadUserNotFound() {
        String url = this.baseUrl + ID_ID.replace("{id}", "aaaaaaaa-bbbb-cccc-dddd-eeeeffff9999");
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateConflictWithMobile() {
        UserDto userDto = UserDto.builder().mobile("666000660").firstName("daemon").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, this.headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(this.baseUrl, HttpMethod.POST, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreate() {
        UserDto userDto = UserDto.builder().mobile("666000666").firstName("new").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, this.headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(this.baseUrl, HttpMethod.POST, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testCreateConflictWithEmail() {
        UserDto userDto = UserDto.builder().mobile("666666666").firstName("daemon").email("user0@gmail.com").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, this.headers);
        ResponseEntity<UserDto> response = restTemplate.exchange(this.baseUrl, HttpMethod.POST, request, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateConflictWithIdentity() {
        UserDto userDto = UserDto.builder().mobile("666666666").firstName("daemon").identity("66666600D").build();
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
    void testFind() {
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(baseUrl + "?mobile=666000660", UserDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        UserDto first = response.getBody()[0];
        assertThat(first.getFirstName()).isNotNull();
        assertThat(first.getAddress()).isNull();
    }

    @Test
    void testFindWithProjection() {
        String url = baseUrl + "?projection=true&mobile=666000660";
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(url, UserDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        UserDto first = response.getBody()[0];
        assertThat(first.getFirstName()).isNotNull();
        assertThat(first.getAddress()).isNotNull();
    }

}
