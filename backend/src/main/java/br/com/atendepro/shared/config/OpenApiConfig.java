package br.com.atendepro.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI atendeProOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AtendePro API")
                        .description("API do AtendePro SaaS profissional multiempresa e multiárea.")
                        .version("0.0.1")
                        .license(new License().name("Proprietary")));
    }
}
