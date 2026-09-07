package br.com.ecociente.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest 
class CalendarioApplicationTests {

    @Test
    void applicationClassExists() {
        assertThat(CalendarioApplication.class).isNotNull();
    }
}