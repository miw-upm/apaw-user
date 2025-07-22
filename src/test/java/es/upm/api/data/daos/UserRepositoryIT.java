package es.upm.api.data.daos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByMobile() {
        assertThat(this.userRepository.findByMobile("6")).isPresent();
    }

    @Test
    void testFindByMobileAndFirstNameAndFamilyName() {
        assertThat(this.userRepository.findByMobileAndFirstNameAndFamilyNameNullSafe(
                "1", null, null))
                .anyMatch(user -> "666666001" .equals(user.getMobile()));
    }

}
