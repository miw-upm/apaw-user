package es.upm.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class ApplicationTest {
    @Test
    void shouldRunMainMethodWithoutExceptions() {
        assertThatCode(() -> Application.main(new String[]{}))
                .doesNotThrowAnyException();
    }
}
