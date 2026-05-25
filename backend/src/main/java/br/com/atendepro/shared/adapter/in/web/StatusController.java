package br.com.atendepro.shared.adapter.in.web;

import java.time.OffsetDateTime;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public StatusResponse consultarStatus() {
        return new StatusResponse("AtendePro Backend", "ONLINE", OffsetDateTime.now());
    }

    public record StatusResponse(String aplicacao, String status, OffsetDateTime verificadoEm) {
    }
}
