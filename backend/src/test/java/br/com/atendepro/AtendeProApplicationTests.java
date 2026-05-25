package br.com.atendepro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class AtendeProApplicationTests {

    @Test
    void deveCarregarContextoDaAplicacao() {
    }
}
