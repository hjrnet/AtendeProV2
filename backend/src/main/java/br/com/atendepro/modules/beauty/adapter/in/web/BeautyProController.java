package br.com.atendepro.modules.beauty.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.beauty.application.command.AtualizarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarProntuarioBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.CriarFichaEsteticaBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarProtocolosBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarClientesBeautyProCommand;
import br.com.atendepro.modules.beauty.application.command.ListarFichasEsteticasBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.in.AtualizarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarProntuarioBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarVisaoBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarProtocoloBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.CriarFichaEsteticaBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.DetalharProtocoloBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarClientesBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarFichasEsteticasBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.ListarProtocolosBeautyProUseCase;
import br.com.atendepro.modules.beauty.application.port.in.RegistrarSessaoProtocoloBeautyProUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/beauty-pro")
@Profile("!test")
public class BeautyProController {

    private final ConsultarVisaoBeautyProUseCase consultarVisaoBeautyProUseCase;
    private final ListarClientesBeautyProUseCase listarClientesBeautyProUseCase;
    private final ConsultarProntuarioBeautyProUseCase consultarProntuarioBeautyProUseCase;
    private final CriarFichaEsteticaBeautyProUseCase criarFichaEsteticaBeautyProUseCase;
    private final AtualizarFichaEsteticaBeautyProUseCase atualizarFichaEsteticaBeautyProUseCase;
    private final ListarFichasEsteticasBeautyProUseCase listarFichasEsteticasBeautyProUseCase;
    private final CriarProtocoloBeautyProUseCase criarProtocoloBeautyProUseCase;
    private final ListarProtocolosBeautyProUseCase listarProtocolosBeautyProUseCase;
    private final DetalharProtocoloBeautyProUseCase detalharProtocoloBeautyProUseCase;
    private final RegistrarSessaoProtocoloBeautyProUseCase registrarSessaoProtocoloBeautyProUseCase;

    public BeautyProController(
            ConsultarVisaoBeautyProUseCase consultarVisaoBeautyProUseCase,
            ListarClientesBeautyProUseCase listarClientesBeautyProUseCase,
            ConsultarProntuarioBeautyProUseCase consultarProntuarioBeautyProUseCase,
            CriarFichaEsteticaBeautyProUseCase criarFichaEsteticaBeautyProUseCase,
            AtualizarFichaEsteticaBeautyProUseCase atualizarFichaEsteticaBeautyProUseCase,
            ListarFichasEsteticasBeautyProUseCase listarFichasEsteticasBeautyProUseCase,
            CriarProtocoloBeautyProUseCase criarProtocoloBeautyProUseCase,
            ListarProtocolosBeautyProUseCase listarProtocolosBeautyProUseCase,
            DetalharProtocoloBeautyProUseCase detalharProtocoloBeautyProUseCase,
            RegistrarSessaoProtocoloBeautyProUseCase registrarSessaoProtocoloBeautyProUseCase
    ) {
        this.consultarVisaoBeautyProUseCase = consultarVisaoBeautyProUseCase;
        this.listarClientesBeautyProUseCase = listarClientesBeautyProUseCase;
        this.consultarProntuarioBeautyProUseCase = consultarProntuarioBeautyProUseCase;
        this.criarFichaEsteticaBeautyProUseCase = criarFichaEsteticaBeautyProUseCase;
        this.atualizarFichaEsteticaBeautyProUseCase = atualizarFichaEsteticaBeautyProUseCase;
        this.listarFichasEsteticasBeautyProUseCase = listarFichasEsteticasBeautyProUseCase;
        this.criarProtocoloBeautyProUseCase = criarProtocoloBeautyProUseCase;
        this.listarProtocolosBeautyProUseCase = listarProtocolosBeautyProUseCase;
        this.detalharProtocoloBeautyProUseCase = detalharProtocoloBeautyProUseCase;
        this.registrarSessaoProtocoloBeautyProUseCase = registrarSessaoProtocoloBeautyProUseCase;
    }

    @GetMapping("/visao")
    public ResponseEntity<VisaoBeautyProResponse> consultarVisaoBeautyPro(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(VisaoBeautyProResponse.de(
                consultarVisaoBeautyProUseCase.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(empresaId))
        ));
    }

    @GetMapping("/clientes")
    public ResponseEntity<ClientesBeautyProResponse> listarClientesBeautyPro(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String busca
    ) {
        return ResponseEntity.ok(ClientesBeautyProResponse.de(
                listarClientesBeautyProUseCase.listarClientesBeautyPro(new ListarClientesBeautyProCommand(empresaId, busca))
        ));
    }

    @GetMapping("/clientes/{clienteId}/prontuario")
    public ResponseEntity<ProntuarioBeautyProResponse> consultarProntuarioBeautyPro(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return consultarProntuarioBeautyProUseCase
                .consultarProntuarioBeautyPro(new ConsultarProntuarioBeautyProCommand(empresaId, clienteId))
                .map(ProntuarioBeautyProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/clientes/{clienteId}/fichas-esteticas")
    public ResponseEntity<FichaEsteticaBeautyProResponse> criarFichaEstetica(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody SalvarFichaEsteticaBeautyProRequest request
    ) {
        FichaEsteticaBeautyProResponse response = FichaEsteticaBeautyProResponse.de(
                criarFichaEsteticaBeautyProUseCase.criarFichaEstetica(request.paraCriacaoCommand(empresaId, clienteId))
        );
        return ResponseEntity.created(URI.create("/api/beauty-pro/clientes/" + clienteId + "/fichas-esteticas/" + response.id()))
                .body(response);
    }

    @PutMapping("/clientes/{clienteId}/fichas-esteticas/{fichaId}")
    public ResponseEntity<FichaEsteticaBeautyProResponse> atualizarFichaEstetica(
            @PathVariable UUID clienteId,
            @PathVariable UUID fichaId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody SalvarFichaEsteticaBeautyProRequest request
    ) {
        return atualizarFichaEsteticaBeautyProUseCase
                .atualizarFichaEstetica(request.paraAtualizacaoCommand(empresaId, clienteId, fichaId))
                .map(FichaEsteticaBeautyProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/clientes/{clienteId}/fichas-esteticas")
    public ResponseEntity<FichasEsteticasBeautyProResponse> listarFichasEsteticas(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(FichasEsteticasBeautyProResponse.de(
                listarFichasEsteticasBeautyProUseCase.listarFichasEsteticas(
                        new ListarFichasEsteticasBeautyProCommand(empresaId, clienteId)
                )
        ));
    }

    @PostMapping("/clientes/{clienteId}/protocolos")
    public ResponseEntity<ProtocoloBeautyProResponse> criarProtocolo(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody CriarProtocoloBeautyProRequest request
    ) {
        ProtocoloBeautyProResponse response = ProtocoloBeautyProResponse.de(
                criarProtocoloBeautyProUseCase.criarProtocolo(request.paraCommand(empresaId, clienteId))
        );
        return ResponseEntity.created(URI.create("/api/beauty-pro/clientes/" + clienteId + "/protocolos/" + response.id()))
                .body(response);
    }

    @GetMapping("/clientes/{clienteId}/protocolos")
    public ResponseEntity<ProtocolosBeautyProResponse> listarProtocolos(
            @PathVariable UUID clienteId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(ProtocolosBeautyProResponse.de(
                listarProtocolosBeautyProUseCase.listarProtocolos(new ListarProtocolosBeautyProCommand(empresaId, clienteId))
        ));
    }

    @GetMapping("/clientes/{clienteId}/protocolos/{protocoloId}")
    public ResponseEntity<ProtocoloBeautyProResponse> detalharProtocolo(
            @PathVariable UUID clienteId,
            @PathVariable UUID protocoloId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return detalharProtocoloBeautyProUseCase
                .detalharProtocolo(new br.com.atendepro.modules.beauty.application.command.DetalharProtocoloBeautyProCommand(empresaId, clienteId, protocoloId))
                .map(ProtocoloBeautyProResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/clientes/{clienteId}/protocolos/{protocoloId}/sessoes")
    public ResponseEntity<SessaoProtocoloBeautyProResponse> registrarSessao(
            @PathVariable UUID clienteId,
            @PathVariable UUID protocoloId,
            @RequestParam(required = false) UUID empresaId,
            @Valid @RequestBody RegistrarSessaoProtocoloBeautyProRequest request
    ) {
        return registrarSessaoProtocoloBeautyProUseCase
                .registrarSessao(request.paraCommand(empresaId, clienteId, protocoloId))
                .map(response -> ResponseEntity.created(URI.create("/api/beauty-pro/clientes/" + clienteId + "/protocolos/" + protocoloId + "/sessoes/" + response.id()))
                        .body(SessaoProtocoloBeautyProResponse.de(response)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
