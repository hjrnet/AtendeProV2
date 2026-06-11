package br.com.atendepro.modules.demo.adapter.in.bootstrap;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.atendepro.modules.auth.application.port.out.CriptografarSenhaPort;

@Component
@Profile("local")
@Order(20)
public class DadosDemoLocalRunner implements ApplicationRunner {

    private static final String SENHA_DEMO = "AtendePro@123";
    private static final String PREFIXO_UUID = "atendepro-demo-local:";

    private final JdbcTemplate jdbcTemplate;
    private final CriptografarSenhaPort criptografarSenhaPort;
    private final Clock clock;

    public DadosDemoLocalRunner(
            JdbcTemplate jdbcTemplate,
            CriptografarSenhaPort criptografarSenhaPort,
            Clock clock
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.criptografarSenhaPort = criptografarSenhaPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        popularDadosDemo();
    }

    @Transactional
    public void popularDadosDemo() {
        ContextoDemo contexto = new ContextoDemo(Instant.now(clock), LocalDate.now(clock));
        Map<String, UUID> empresas = popularEmpresas();
        Map<String, UUID> planos = popularPlanos();
        popularAssinaturas(empresas, planos);
        Map<String, UUID> usuarios = popularUsuarios(empresas);
        popularClientes(empresas, contexto);
        Map<String, UUID> servicos = popularServicos(empresas);
        popularCustos(empresas);
        popularEstoque(empresas, contexto);
        popularEquipamentos(empresas, contexto);
        popularAgenda(empresas, usuarios, contexto);
        popularSpaces(empresas, contexto);
        popularSimulacoes(empresas, servicos, contexto);
    }

    private Map<String, UUID> popularEmpresas() {
        List<EmpresaDemo> empresas = List.of(
                new EmpresaDemo("NUTRI", "Clínica Nutri Vida", "Clínica Nutri Vida Ltda",
                        "DEMO-NUTRI-2026", "contato.nutri@atendepro.local", "(21) 3000-1001"),
                new EmpresaDemo("BEAUTY", "Studio Aesthetic Premium", "Studio Aesthetic Premium Ltda",
                        "DEMO-BEAUTY-2026", "contato.beauty@atendepro.local", "(21) 3000-1002"),
                new EmpresaDemo("BIOMED", "Clínica Biomed Glow", "Clínica Biomed Glow Ltda",
                        "DEMO-BIOMED-2026", "contato.biomed@atendepro.local", "(21) 3000-1003"),
                new EmpresaDemo("FISIO", "Movimento Fisio Center", "Movimento Fisio Center Ltda",
                        "DEMO-FISIO-2026", "contato.fisio@atendepro.local", "(21) 3000-1004"),
                new EmpresaDemo("SPACES", "Espaço Compartilhado Pro", "Espaço Compartilhado Pro Ltda",
                        "DEMO-SPACES-2026", "contato.spaces@atendepro.local", "(21) 3000-1005"),
                new EmpresaDemo("SALAO", "Salão Bella Forma", "Salão Bella Forma Ltda",
                        "DEMO-SALAO-2026", "contato.salao@atendepro.local", "(21) 3000-1006"),
                new EmpresaDemo("ESTUDANTE", "Conta Estudante Demo", "Conta Estudante Demo",
                        "DEMO-ESTUDANTE-2026", "estudante.demo@atendepro.local", "(21) 3000-1007")
        );

        Map<String, UUID> ids = new LinkedHashMap<>();
        for (EmpresaDemo empresa : empresas) {
            jdbcTemplate.update(
                    """
                    insert into empresas (
                        id, nome_fantasia, razao_social, documento, email, telefone, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (documento) do update
                    set nome_fantasia = excluded.nome_fantasia,
                        razao_social = excluded.razao_social,
                        email = excluded.email,
                        telefone = excluded.telefone,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("empresa:" + empresa.codigo()),
                    empresa.nomeFantasia(),
                    empresa.razaoSocial(),
                    empresa.documento(),
                    empresa.email(),
                    empresa.telefone()
            );
            ids.put(empresa.codigo(), buscarIdEmpresaPorDocumento(empresa.documento()));
        }
        return ids;
    }

    private Map<String, UUID> popularPlanos() {
        List<PlanoDemo> planos = List.of(
                new PlanoDemo("ESTUDANTE", "Estudante", "Plano acadêmico para estudos e primeiros atendimentos supervisionados.",
                        "29.90", 1, 30, 1, true, "Documento para fins acadêmicos - Plano Estudante AtendePro",
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda", "procedimentos", "documentos")),
                new PlanoDemo("START", "Start", "Plano inicial para profissionais independentes.",
                        "79.90", 2, 100, 1, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda", "procedimentos")),
                new PlanoDemo("CARE", "Care", "Plano profissional para gestão operacional ampliada.",
                        "119.90", 5, 500, 5, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "documentos", "suporte")),
                new PlanoDemo("NUTRI_PRO", "Nutri Pro", "Plano vertical para nutricionistas e clínicas de nutrição.",
                        "149.90", 5, 1000, 5, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "documentos", "nutri-pro")),
                new PlanoDemo("BEAUTY_PRO", "Beauty Pro", "Plano vertical para estética, beleza e salões.",
                        "149.90", 5, 1000, 5, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "estoque", "equipamentos", "beauty-pro")),
                new PlanoDemo("BIOMED_PRO", "Biomed Pro", "Plano vertical para biomedicina estética.",
                        "179.90", 5, 1000, 5, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "estoque", "equipamentos", "biomed-pro")),
                new PlanoDemo("FISIO_PRO", "Fisio Pro", "Plano vertical para fisioterapia e reabilitação.",
                        "149.90", 5, 1000, 5, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "documentos", "fisio-pro")),
                new PlanoDemo("BUSINESS", "Business", "Plano para clínicas, equipes e operações em crescimento.",
                        "249.90", 15, 5000, 15, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "estoque", "equipamentos", "documentos", "suporte")),
                new PlanoDemo("SPACES", "Spaces", "Plano para espaços compartilhados e sublocação profissional.",
                        "299.90", 20, 5000, 30, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "sublocacao", "spaces", "suporte")),
                new PlanoDemo("PREMIUM", "Premium", "Plano completo para operações profissionais maduras.",
                        "499.90", 50, 100000, 50, false, null,
                        modulos("tenant-empresa", "usuarios-permissoes", "dashboard", "clientes", "agenda",
                                "procedimentos", "custos", "precificacao", "estoque", "equipamentos", "documentos",
                                "sublocacao", "nutri-pro", "beauty-pro", "biomed-pro", "fisio-pro", "spaces", "suporte"))
        );

        Map<String, UUID> ids = new LinkedHashMap<>();
        for (PlanoDemo plano : planos) {
            jdbcTemplate.update(
                    """
                    insert into planos (
                        id, codigo, nome, descricao, valor_mensal, limite_usuarios, limite_clientes,
                        limite_profissionais, estudante, marca_dagua_academica, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (codigo) do update
                    set nome = excluded.nome,
                        descricao = excluded.descricao,
                        valor_mensal = excluded.valor_mensal,
                        limite_usuarios = excluded.limite_usuarios,
                        limite_clientes = excluded.limite_clientes,
                        limite_profissionais = excluded.limite_profissionais,
                        estudante = excluded.estudante,
                        marca_dagua_academica = excluded.marca_dagua_academica,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("plano:" + plano.codigo()),
                    plano.codigo(),
                    plano.nome(),
                    plano.descricao(),
                    dinheiro(plano.valorMensal()),
                    plano.limiteUsuarios(),
                    plano.limiteClientes(),
                    plano.limiteProfissionais(),
                    plano.estudante(),
                    plano.marcaDaguaAcademica()
            );
            UUID planoId = buscarIdPlanoPorCodigo(plano.codigo());
            ids.put(plano.codigo(), planoId);
            for (String modulo : plano.modulos()) {
                jdbcTemplate.update(
                        """
                        insert into plano_modulos (plano_id, modulo)
                        values (?, ?)
                        on conflict (plano_id, modulo) do nothing
                        """,
                        planoId,
                        modulo
                );
            }
        }
        return ids;
    }

    private void popularAssinaturas(Map<String, UUID> empresas, Map<String, UUID> planos) {
        List<AssinaturaDemo> assinaturas = List.of(
                new AssinaturaDemo("NUTRI", "NUTRI_PRO"),
                new AssinaturaDemo("BEAUTY", "BEAUTY_PRO"),
                new AssinaturaDemo("BIOMED", "BIOMED_PRO"),
                new AssinaturaDemo("FISIO", "FISIO_PRO"),
                new AssinaturaDemo("SPACES", "SPACES"),
                new AssinaturaDemo("SALAO", "BUSINESS"),
                new AssinaturaDemo("ESTUDANTE", "ESTUDANTE")
        );
        for (AssinaturaDemo assinatura : assinaturas) {
            jdbcTemplate.update(
                    """
                    insert into assinaturas (
                        id, empresa_id, plano_id, status, iniciado_em, cancelado_em, bloqueado_em, criado_em, atualizado_em
                    )
                    values (?, ?, ?, 'ATIVA', now() - interval '15 days', null, null, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        plano_id = excluded.plano_id,
                        status = 'ATIVA',
                        cancelado_em = null,
                        bloqueado_em = null,
                        atualizado_em = now()
                    """,
                    uuidDemo("assinatura:" + assinatura.empresaCodigo()),
                    empresas.get(assinatura.empresaCodigo()),
                    planos.get(assinatura.planoCodigo())
            );
        }
    }

    private Map<String, UUID> popularUsuarios(Map<String, UUID> empresas) {
        List<UsuarioDemo> usuarios = List.of(
                new UsuarioDemo("ADMIN", "Administrador AtendePro", "admin@atendepro.local", null,
                        List.of("SUPER_ADMIN")),
                new UsuarioDemo("KAROL", "Karol Nutricionista Demo", "karol.nutri@atendepro.local", "NUTRI",
                        List.of("EMPRESA_ADMIN", "PROFISSIONAL")),
                new UsuarioDemo("ANA", "Ana Esteticista Demo", "ana.estetica@atendepro.local", "BEAUTY",
                        List.of("EMPRESA_ADMIN", "PROFISSIONAL")),
                new UsuarioDemo("BIANCA", "Bianca Biomédica Demo", "bianca.biomed@atendepro.local", "BIOMED",
                        List.of("EMPRESA_ADMIN", "PROFISSIONAL")),
                new UsuarioDemo("FELIPE", "Felipe Fisioterapeuta Demo", "felipe.fisio@atendepro.local", "FISIO",
                        List.of("EMPRESA_ADMIN", "PROFISSIONAL")),
                new UsuarioDemo("PAULA", "Paula Gestora de Espaços", "paula.spaces@atendepro.local", "SPACES",
                        List.of("EMPRESA_ADMIN")),
                new UsuarioDemo("ESTUDANTE", "Estudante Demo", "estudante@atendepro.local", "ESTUDANTE",
                        List.of("ESTUDANTE", "PROFISSIONAL"))
        );

        Map<String, UUID> ids = new LinkedHashMap<>();
        String senhaHash = criptografarSenhaPort.criptografarSenha(SENHA_DEMO);
        for (UsuarioDemo usuario : usuarios) {
            UUID empresaId = usuario.empresaCodigo() == null ? null : empresas.get(usuario.empresaCodigo());
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(
                        """
                        insert into auth_usuarios (
                            id, empresa_id, nome, email, senha_hash, perfis, ativo, criado_em, atualizado_em
                        )
                        values (?, ?, ?, ?, ?, ?, true, now(), now())
                        on conflict (email) do update
                        set empresa_id = excluded.empresa_id,
                            nome = excluded.nome,
                            senha_hash = excluded.senha_hash,
                            perfis = excluded.perfis,
                            ativo = true,
                            atualizado_em = now()
                        """
                );
                statement.setObject(1, uuidDemo("usuario:" + usuario.codigo()));
                statement.setObject(2, empresaId);
                statement.setString(3, usuario.nome());
                statement.setString(4, usuario.email());
                statement.setString(5, senhaHash);
                statement.setArray(6, connection.createArrayOf("text", usuario.perfis().toArray(String[]::new)));
                return statement;
            });
            ids.put(usuario.codigo(), buscarIdUsuarioPorEmail(usuario.email()));
        }
        return ids;
    }

    private void popularClientes(Map<String, UUID> empresas, ContextoDemo contexto) {
        int contador = 1;
        List<ClienteDemo> clientes = List.of(
                cliente("NUTRI", "Mariana Silva", "PACIENTE", "NUTRI", "1989-04-15"),
                cliente("NUTRI", "Ana Victoria", "PACIENTE", "NUTRI", "1996-07-22"),
                cliente("NUTRI", "Camila Oliveira", "PACIENTE", "NUTRI", "1984-11-03"),
                cliente("NUTRI", "João Pedro Santos", "PACIENTE", "NUTRI", "1991-01-18"),
                cliente("NUTRI", "Vanessa Miranda", "PACIENTE", "NUTRI", "1978-09-09"),
                cliente("BEAUTY", "Juliana Costa", "CLIENTE", "BEAUTY", "1990-02-11"),
                cliente("BEAUTY", "Renata Almeida", "CLIENTE", "BEAUTY", "1987-06-29"),
                cliente("BEAUTY", "Bruna Martins", "CLIENTE", "BEAUTY", "1994-10-06"),
                cliente("BEAUTY", "Larissa Souza", "CLIENTE", "BEAUTY", "1992-08-17"),
                cliente("BEAUTY", "Patrícia Ramos", "CLIENTE", "BEAUTY", "1981-12-27"),
                cliente("BIOMED", "Amanda Rocha", "CLIENTE_PACIENTE", "BIOMED", "1988-03-05"),
                cliente("BIOMED", "Letícia Castro", "CLIENTE_PACIENTE", "BIOMED", "1993-05-14"),
                cliente("BIOMED", "Fernanda Lima", "CLIENTE_PACIENTE", "BIOMED", "1985-01-25"),
                cliente("BIOMED", "Marcela Nunes", "CLIENTE_PACIENTE", "BIOMED", "1998-07-30"),
                cliente("FISIO", "Carlos Henrique", "PACIENTE", "FISIO", "1976-04-08"),
                cliente("FISIO", "Pedro Almeida", "PACIENTE", "FISIO", "1999-02-19"),
                cliente("FISIO", "Helena Duarte", "PACIENTE", "FISIO", "1969-11-13"),
                cliente("FISIO", "Beatriz Campos", "PACIENTE", "FISIO", "1986-09-21"),
                cliente("SALAO", "Carla Mendes", "CLIENTE", "BEAUTY", "1983-05-02"),
                cliente("SALAO", "Nicole Ferreira", "CLIENTE", "BEAUTY", "1997-12-01"),
                cliente("SALAO", "Aline Barbosa", "CLIENTE", "BEAUTY", "1991-08-24"),
                cliente("SPACES", "Dra. Juliana R.", "CLIENTE", "SPACES", "1980-06-10"),
                cliente("SPACES", "Profissional Parceiro 1", "CLIENTE", "SPACES", "1982-10-19"),
                cliente("SPACES", "Profissional Parceiro 2", "CLIENTE", "SPACES", "1990-03-16")
        );

        for (ClienteDemo cliente : clientes) {
            String chave = "%03d".formatted(contador++);
            jdbcTemplate.update(
                    """
                    insert into clientes_pacientes (
                        id, empresa_id, nome, tipo, area, documento, email, telefone,
                        data_nascimento, observacoes, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, null, ?, ?, ?, ?, true, ?, ?)
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        nome = excluded.nome,
                        tipo = excluded.tipo,
                        area = excluded.area,
                        email = excluded.email,
                        telefone = excluded.telefone,
                        data_nascimento = excluded.data_nascimento,
                        observacoes = excluded.observacoes,
                        ativo = true,
                        atualizado_em = excluded.atualizado_em
                    """,
                    uuidDemo("cliente:" + chave + ":" + cliente.empresaCodigo() + ":" + cliente.nome()),
                    empresas.get(cliente.empresaCodigo()),
                    cliente.nome(),
                    cliente.tipo(),
                    cliente.area(),
                    emailDemo(cliente.nome()),
                    "(21) 9%04d-%04d".formatted(8000 + contador, 1000 + contador),
                    LocalDate.parse(cliente.dataNascimento()),
                    "Cadastro fictício para apresentação local/demo.",
                    Timestamp.from(contexto.agora()),
                    Timestamp.from(contexto.agora())
            );
        }
    }

    private Map<String, UUID> popularServicos(Map<String, UUID> empresas) {
        List<ServicoDemo> servicos = List.of(
                servico("NUTRI", "Consulta nutricional inicial", "NUTRI", 60, "180.00"),
                servico("NUTRI", "Consulta de retorno", "NUTRI", 45, "120.00"),
                servico("NUTRI", "Avaliação com bioimpedância", "NUTRI", 45, "160.00"),
                servico("NUTRI", "Plano alimentar personalizado", "NUTRI", 90, "230.00"),
                servico("NUTRI", "Pacote de acompanhamento mensal", "NUTRI", 120, "420.00"),
                servico("NUTRI", "Acompanhamento de emagrecimento", "NUTRI", 60, "220.00"),
                servico("NUTRI", "Acompanhamento de performance", "NUTRI", 60, "260.00"),
                servico("NUTRI", "Consulta online", "NUTRI", 50, "150.00"),
                servico("BEAUTY", "Limpeza de pele", "BEAUTY", 70, "170.00"),
                servico("BEAUTY", "Limpeza de pele premium", "BEAUTY", 90, "220.00"),
                servico("BEAUTY", "Peeling químico superficial", "BEAUTY", 50, "260.00"),
                servico("BEAUTY", "Peeling clareador", "BEAUTY", 60, "280.00"),
                servico("BEAUTY", "Microagulhamento facial", "BEAUTY", 70, "450.00"),
                servico("BEAUTY", "Radiofrequência facial", "BEAUTY", 50, "210.00"),
                servico("BEAUTY", "Radiofrequência corporal", "BEAUTY", 60, "260.00"),
                servico("BEAUTY", "Drenagem linfática", "BEAUTY", 60, "130.00"),
                servico("BEAUTY", "Massagem modeladora", "BEAUTY", 60, "150.00"),
                servico("BEAUTY", "Massagem relaxante", "BEAUTY", 60, "120.00"),
                servico("BEAUTY", "Protocolo corporal redutor", "BEAUTY", 90, "320.00"),
                servico("BEAUTY", "Protocolo para acne", "BEAUTY", 60, "240.00"),
                servico("BEAUTY", "Protocolo para manchas", "BEAUTY", 60, "260.00"),
                servico("BEAUTY", "Terapia capilar", "BEAUTY", 50, "180.00"),
                servico("BEAUTY", "Design de sobrancelhas", "BEAUTY", 40, "75.00"),
                servico("BEAUTY", "Extensão de cílios", "BEAUTY", 120, "230.00"),
                servico("BEAUTY", "Lash lifting", "BEAUTY", 70, "160.00"),
                servico("BEAUTY", "Brow lamination", "BEAUTY", 60, "150.00"),
                servico("BIOMED", "Avaliação estética biomédica", "BIOMED", 60, "240.00"),
                servico("BIOMED", "Microagulhamento biomédico", "BIOMED", 70, "480.00"),
                servico("BIOMED", "Peeling biomédico estético", "BIOMED", 60, "360.00"),
                servico("BIOMED", "Bioestimulador", "BIOMED", 75, "950.00"),
                servico("BIOMED", "Laser estético", "BIOMED", 60, "420.00"),
                servico("BIOMED", "Intradermoterapia", "BIOMED", 80, "680.00"),
                servico("BIOMED", "Protocolo capilar", "BIOMED", 60, "180.00"),
                servico("BIOMED", "Procedimento corporal estético", "BIOMED", 80, "520.00"),
                servico("BIOMED", "Harmonização facial simulada", "BIOMED", 120, "1800.00"),
                servico("FISIO", "Avaliação fisioterapêutica", "FISIO", 60, "180.00"),
                servico("FISIO", "Sessão de fisioterapia ortopédica", "FISIO", 50, "150.00"),
                servico("FISIO", "Sessão de fisioterapia desportiva", "FISIO", 50, "170.00"),
                servico("FISIO", "RPG", "FISIO", 60, "190.00"),
                servico("FISIO", "Pilates clínico", "FISIO", 55, "160.00"),
                servico("FISIO", "Terapia manual", "FISIO", 50, "150.00"),
                servico("FISIO", "Liberação miofascial", "FISIO", 45, "140.00"),
                servico("FISIO", "Drenagem pós-operatória", "FISIO", 60, "180.00"),
                servico("FISIO", "Fisioterapia domiciliar", "FISIO", 70, "160.00"),
                servico("FISIO", "Reabilitação funcional", "FISIO", 60, "140.00"),
                servico("SPACES", "Sala estética por hora", "SPACES", 60, "60.00"),
                servico("SPACES", "Sala de atendimento nutricional por hora", "SPACES", 60, "70.00"),
                servico("SPACES", "Sala de fisioterapia por hora", "SPACES", 60, "80.00"),
                servico("SPACES", "Cadeira de salão por turno", "SPACES", 240, "160.00"),
                servico("SPACES", "Cabine estética por diária", "SPACES", 480, "210.00"),
                servico("SPACES", "Uso de equipamento por sessão", "SPACES", 60, "90.00"),
                servico("SPACES", "Consultório premium por período", "SPACES", 240, "320.00"),
                servico("SALAO", "Corte feminino", "BEAUTY", 45, "90.00"),
                servico("SALAO", "Corte masculino", "BEAUTY", 30, "55.00"),
                servico("SALAO", "Escova", "BEAUTY", 45, "70.00"),
                servico("SALAO", "Hidratação capilar", "BEAUTY", 60, "110.00"),
                servico("SALAO", "Coloração", "BEAUTY", 120, "120.00"),
                servico("SALAO", "Luzes/mechas", "BEAUTY", 180, "260.00"),
                servico("SALAO", "Progressiva", "BEAUTY", 180, "280.00"),
                servico("SALAO", "Manicure", "BEAUTY", 40, "40.00"),
                servico("SALAO", "Pedicure", "BEAUTY", 45, "45.00"),
                servico("SALAO", "Alongamento de unhas", "BEAUTY", 120, "170.00"),
                servico("SALAO", "Maquiagem social", "BEAUTY", 90, "180.00")
        );

        Map<String, UUID> ids = new LinkedHashMap<>();
        for (ServicoDemo servico : servicos) {
            UUID servicoId = uuidDemo("servico:" + servico.empresaCodigo() + ":" + servico.nome());
            jdbcTemplate.update(
                    """
                    insert into servicos_procedimentos (
                        id, empresa_id, nome, descricao, area, duracao_minutos, preco_base, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        nome = excluded.nome,
                        descricao = excluded.descricao,
                        area = excluded.area,
                        duracao_minutos = excluded.duracao_minutos,
                        preco_base = excluded.preco_base,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    servicoId,
                    empresas.get(servico.empresaCodigo()),
                    servico.nome(),
                    "Serviço fictício local/demo para apresentação profissional.",
                    servico.area(),
                    servico.duracaoMinutos(),
                    dinheiro(servico.precoBase())
            );
            ids.put(chaveServico(servico.empresaCodigo(), servico.nome()), servicoId);
        }
        return ids;
    }

    private void popularCustos(Map<String, UUID> empresas) {
        int contador = 1;
        for (Map.Entry<String, UUID> empresa : empresas.entrySet()) {
            if ("ESTUDANTE".equals(empresa.getKey())) {
                continue;
            }
            String chave = "%03d".formatted(contador++);
            jdbcTemplate.update(
                    """
                    insert into custos_gerais (
                        id, empresa_id, descricao, tipo, categoria, valor, competencia, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, 'FIXO', 'Estrutura', ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        descricao = excluded.descricao,
                        tipo = excluded.tipo,
                        categoria = excluded.categoria,
                        valor = excluded.valor,
                        competencia = excluded.competencia,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("custo-geral:" + chave + ":" + empresa.getKey()),
                    empresa.getValue(),
                    "Aluguel e estrutura demo",
                    dinheiro(valorCustoEstrutura(empresa.getKey())),
                    YearMonth.now(clock).toString()
            );
            jdbcTemplate.update(
                    """
                    insert into custos_alimentacao_transporte (
                        id, empresa_id, profissional_id, descricao, tipo, periodicidade, valor, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, null, ?, 'TRANSPORTE', 'MENSAL', ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        descricao = excluded.descricao,
                        tipo = excluded.tipo,
                        periodicidade = excluded.periodicidade,
                        valor = excluded.valor,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("custo-transporte:" + chave + ":" + empresa.getKey()),
                    empresa.getValue(),
                    "Deslocamentos profissionais demo",
                    dinheiro("450.00")
            );
        }
    }

    private void popularEstoque(Map<String, UUID> empresas, ContextoDemo contexto) {
        List<ProdutoDemo> produtos = List.of(
                produto("NUTRI", "Kit avaliação nutricional", "Nutrição", "UN", "4.000", "28.00", "6.000", 18),
                produto("NUTRI", "Impresso de orientação", "Materiais", "UN", "20.000", "0.80", "15.000", 60),
                produto("BEAUTY", "Sérum facial", "Cosméticos", "UN", "2.000", "54.00", "4.000", 21),
                produto("BEAUTY", "Máscara calmante", "Cosméticos", "UN", "3.000", "38.00", "4.000", 24),
                produto("BEAUTY", "Ácido mandélico", "Cosméticos", "UN", "1.000", "72.00", "2.000", 17),
                produto("BEAUTY", "Neutralizante pós-peeling", "Cosméticos Beauty", "UN", "1.000", "41.00", "2.000", 6),
                produto("BEAUTY", "Protetor pós-procedimento", "Cosméticos Beauty", "UN", "5.000", "29.00", "3.000", 31),
                produto("BEAUTY", "Espátula descartável facial", "Descartáveis Beauty", "UN", "8.000", "0.65", "20.000", 180),
                produto("BEAUTY", "Henna sobrancelhas castanho", "Produtos sobrancelhas", "UN", "1.000", "24.00", "2.000", -3),
                produto("BIOMED", "Ponteira descartável", "Descartáveis", "UN", "8.000", "12.00", "12.000", 29),
                produto("BIOMED", "Ácido glicólico", "Cosméticos", "UN", "2.000", "85.00", "2.000", 20),
                produto("FISIO", "Elástico terapêutico", "Terapêuticos", "UN", "5.000", "18.00", "8.000", 35),
                produto("FISIO", "Fita cinesiológica", "Terapêuticos", "UN", "3.000", "26.00", "5.000", 22),
                produto("SPACES", "Lençol descartável", "Descartáveis", "UN", "12.000", "1.10", "20.000", 14),
                produto("SALAO", "Tintura", "Produtos capilares", "UN", "4.000", "32.00", "8.000", 19),
                produto("SALAO", "Oxidante", "Produtos capilares", "UN", "5.000", "19.00", "6.000", 27)
        );

        for (ProdutoDemo produto : produtos) {
            jdbcTemplate.update(
                    """
                    insert into estoque_produtos (
                        id, empresa_id, nome, categoria, lote, validade, unidade, quantidade_atual,
                        custo_unitario, estoque_minimo, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        nome = excluded.nome,
                        categoria = excluded.categoria,
                        lote = excluded.lote,
                        validade = excluded.validade,
                        unidade = excluded.unidade,
                        quantidade_atual = excluded.quantidade_atual,
                        custo_unitario = excluded.custo_unitario,
                        estoque_minimo = excluded.estoque_minimo,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("estoque:" + produto.empresaCodigo() + ":" + produto.nome()),
                    empresas.get(produto.empresaCodigo()),
                    produto.nome(),
                    produto.categoria(),
                    "DEMO-" + produto.empresaCodigo(),
                    contexto.hoje().plusDays(produto.diasParaValidade()),
                    produto.unidade(),
                    dinheiro(produto.quantidadeAtual()),
                    dinheiro(produto.custoUnitario()),
                    dinheiro(produto.estoqueMinimo())
            );
        }
    }

    private void popularEquipamentos(Map<String, UUID> empresas, ContextoDemo contexto) {
        List<EquipamentoDemo> equipamentos = List.of(
                equipamento("NUTRI", "Balança de bioimpedância", "Avaliação", "DemoCare", "BIO-900", "3200.00"),
                equipamento("BEAUTY", "Radiofrequência facial", "Estética", "DemoTech", "RF-Premium", "7800.00"),
                equipamento("BIOMED", "Laser estético", "Biomedicina", "DemoLaser", "LZ-Clinic", "22000.00"),
                equipamento("FISIO", "Maca clínica articulada", "Fisioterapia", "DemoFisio", "MC-Pro", "5400.00"),
                equipamento("SPACES", "Equipamento compartilhado multifuncional", "Spaces", "DemoSpace", "SP-Multi", "9800.00"),
                equipamento("SALAO", "Lavatorio profissional", "Salão", "DemoBeauty", "LV-Prime", "4200.00")
        );
        for (EquipamentoDemo equipamento : equipamentos) {
            jdbcTemplate.update(
                    """
                    insert into equipamentos (
                        id, empresa_id, nome, categoria, marca, modelo, numero_serie,
                        valor_aquisicao, data_aquisicao, vida_util_meses, proxima_manutencao_em,
                        descricao_manutencao, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, 60, ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        nome = excluded.nome,
                        categoria = excluded.categoria,
                        marca = excluded.marca,
                        modelo = excluded.modelo,
                        numero_serie = excluded.numero_serie,
                        valor_aquisicao = excluded.valor_aquisicao,
                        data_aquisicao = excluded.data_aquisicao,
                        vida_util_meses = excluded.vida_util_meses,
                        proxima_manutencao_em = excluded.proxima_manutencao_em,
                        descricao_manutencao = excluded.descricao_manutencao,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    uuidDemo("equipamento:" + equipamento.empresaCodigo() + ":" + equipamento.nome()),
                    empresas.get(equipamento.empresaCodigo()),
                    equipamento.nome(),
                    equipamento.categoria(),
                    equipamento.marca(),
                    equipamento.modelo(),
                    "SERIE-" + equipamento.empresaCodigo() + "-DEMO",
                    dinheiro(equipamento.valorAquisicao()),
                    contexto.hoje().minusMonths(14),
                    contexto.hoje().plusDays(23),
                    "Manutenção preventiva demo nos próximos 30 dias."
            );
        }
    }

    private void popularAgenda(Map<String, UUID> empresas, Map<String, UUID> usuarios, ContextoDemo contexto) {
        List<AgendaDemo> compromissos = List.of(
                agenda("NUTRI", "KAROL", "Karol Nutricionista Demo", "Consultório Nutri 1", "ATENDIMENTO", "CONFIRMADO", 0, 9, 60),
                agenda("NUTRI", "KAROL", "Karol Nutricionista Demo", "Consultório Nutri 1", "RETORNO", "AGENDADO", 3, 14, 45),
                agenda("BEAUTY", "ANA", "Ana Esteticista Demo", "Sala Facial", "ATENDIMENTO", "AGENDADO", 0, 10, 90),
                agenda("BIOMED", "BIANCA", "Bianca Biomédica Demo", "Sala Procedimentos", "AVALIACAO", "CONFIRMADO", 1, 11, 60),
                agenda("FISIO", "FELIPE", "Felipe Fisioterapeuta Demo", "Sala Fisio 2", "ATENDIMENTO", "AGENDADO", 0, 15, 50),
                agenda("SPACES", "PAULA", "Paula Gestora de Espaços", "Sala Premium", "OUTRO", "CONFIRMADO", 2, 13, 120),
                agenda("SALAO", "ANA", "Ana Esteticista Demo", "Cadeira 3", "ATENDIMENTO", "AGENDADO", 4, 16, 45)
        );
        for (AgendaDemo agenda : compromissos) {
            Instant inicio = inicio(contexto.hoje().plusDays(agenda.dias()), agenda.hora());
            Instant fim = inicio.plusSeconds(agenda.duracaoMinutos() * 60L);
            jdbcTemplate.update(
                    """
                    insert into agenda_compromissos (
                        id, empresa_id, cliente_paciente_id, profissional_id, profissional_nome, sala,
                        tipo, status, inicio, fim, observacoes, criado_em, atualizado_em
                    )
                    values (?, ?, null, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        profissional_id = excluded.profissional_id,
                        profissional_nome = excluded.profissional_nome,
                        sala = excluded.sala,
                        tipo = excluded.tipo,
                        status = excluded.status,
                        inicio = excluded.inicio,
                        fim = excluded.fim,
                        observacoes = excluded.observacoes,
                        atualizado_em = now()
                    """,
                    uuidDemo("agenda:" + agenda.empresaCodigo() + ":" + agenda.dias() + ":" + agenda.hora()),
                    empresas.get(agenda.empresaCodigo()),
                    usuarios.get(agenda.usuarioCodigo()),
                    agenda.profissionalNome(),
                    agenda.sala(),
                    agenda.tipo(),
                    agenda.status(),
                    Timestamp.from(inicio),
                    Timestamp.from(fim),
                    "Agenda fictícia local/demo."
            );
        }
    }

    private void popularSpaces(Map<String, UUID> empresas, ContextoDemo contexto) {
        UUID empresaSpaces = empresas.get("SPACES");
        List<RecursoSpacesDemo> recursos = List.of(
                new RecursoSpacesDemo("SALA_PREMIUM", "Sala premium multiprofissional", "SALA", "Sala equipada para atendimentos de saúde e estética.", 3, "2º andar"),
                new RecursoSpacesDemo("CADEIRA_SALAO", "Cadeira de salão por turno", "CADEIRA", "Cadeira profissional com espelho e bancada.", 1, "Área beleza"),
                new RecursoSpacesDemo("CABINE_ESTETICA", "Cabine estética privativa", "CABINE", "Cabine com maca, apoio e iluminação.", 2, "Ala estética"),
                new RecursoSpacesDemo("EQUIPAMENTO_SHARED", "Equipamento por sessão", "EQUIPAMENTO", "Equipamento compartilhado para profissionais parceiros.", 1, "Sala técnica")
        );

        Map<String, UUID> recursoIds = new LinkedHashMap<>();
        for (RecursoSpacesDemo recurso : recursos) {
            UUID id = uuidDemo("spaces-recurso:" + recurso.codigo());
            recursoIds.put(recurso.codigo(), id);
            jdbcTemplate.update(
                    """
                    insert into spaces_recursos (
                        id, empresa_id, nome, tipo, descricao, capacidade_pessoas, localizacao, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        nome = excluded.nome,
                        tipo = excluded.tipo,
                        descricao = excluded.descricao,
                        capacidade_pessoas = excluded.capacidade_pessoas,
                        localizacao = excluded.localizacao,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    id,
                    empresaSpaces,
                    recurso.nome(),
                    recurso.tipo(),
                    recurso.descricao(),
                    recurso.capacidadePessoas(),
                    recurso.localizacao()
            );
        }

        List<PacoteSpacesDemo> pacotes = List.of(
                new PacoteSpacesDemo("HORA_SALA", "SALA_PREMIUM", "Sala premium por hora", "HORA", "1.00", "70.00", "0.00"),
                new PacoteSpacesDemo("TURNO_CADEIRA", "CADEIRA_SALAO", "Cadeira de salão por turno", "TURNO", "4.00", "160.00", "0.00"),
                new PacoteSpacesDemo("DIARIA_CABINE", "CABINE_ESTETICA", "Cabine estética por diária", "DIARIA", "8.00", "260.00", "0.00"),
                new PacoteSpacesDemo("EQUIPAMENTO_SESSAO", "EQUIPAMENTO_SHARED", "Uso de equipamento por sessão", "HORA", "1.00", "90.00", "10.00")
        );
        Map<String, UUID> pacoteIds = new LinkedHashMap<>();
        for (PacoteSpacesDemo pacote : pacotes) {
            UUID id = uuidDemo("spaces-pacote:" + pacote.codigo());
            pacoteIds.put(pacote.codigo(), id);
            jdbcTemplate.update(
                    """
                    insert into spaces_pacotes_sublocacao (
                        id, empresa_id, recurso_id, nome, tipo, descricao, duracao_horas, valor_fixo,
                        percentual_receita, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, true, now(), now())
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        recurso_id = excluded.recurso_id,
                        nome = excluded.nome,
                        tipo = excluded.tipo,
                        descricao = excluded.descricao,
                        duracao_horas = excluded.duracao_horas,
                        valor_fixo = excluded.valor_fixo,
                        percentual_receita = excluded.percentual_receita,
                        ativo = true,
                        atualizado_em = now()
                    """,
                    id,
                    empresaSpaces,
                    recursoIds.get(pacote.recursoCodigo()),
                    pacote.nome(),
                    pacote.tipo(),
                    "Pacote fictício local/demo para sublocação.",
                    dinheiro(pacote.duracaoHoras()),
                    dinheiro(pacote.valorFixo()),
                    dinheiro(pacote.percentualReceita())
            );
        }

        Instant inicio = inicio(contexto.hoje().plusDays(1), 9);
        jdbcTemplate.update(
                """
                insert into spaces_ocupacoes (
                    id, empresa_id, recurso_id, pacote_id, nome_parceiro, inicio_em, fim_em,
                    status, observacao, criado_em, atualizado_em
                )
                values (?, ?, ?, ?, ?, ?, ?, 'CONFIRMADA', ?, now(), now())
                on conflict (id) do update
                set empresa_id = excluded.empresa_id,
                    recurso_id = excluded.recurso_id,
                    pacote_id = excluded.pacote_id,
                    nome_parceiro = excluded.nome_parceiro,
                    inicio_em = excluded.inicio_em,
                    fim_em = excluded.fim_em,
                    status = excluded.status,
                    observacao = excluded.observacao,
                    atualizado_em = now()
                """,
                uuidDemo("spaces-ocupacao:demo-principal"),
                empresaSpaces,
                recursoIds.get("SALA_PREMIUM"),
                pacoteIds.get("HORA_SALA"),
                "Dra. Juliana R.",
                Timestamp.from(inicio),
                Timestamp.from(inicio.plusSeconds(2 * 60 * 60L)),
                "Ocupação demo para validação visual de agenda Spaces."
        );
    }

    private void popularSimulacoes(Map<String, UUID> empresas, Map<String, UUID> servicos, ContextoDemo contexto) {
        List<SimulacaoDemo> simulacoes = List.of(
                simulacao("BEAUTY", "Limpeza de pele premium", 90, "45.00", "40.00", "25.00", "0.00", "0.00", "5.00", "30.00", "220.00", "110.00", "157.14", "110.00", "50.00", "SAUDAVEL", 15),
                simulacao("NUTRI", "Consulta nutricional inicial", 60, "8.00", "35.00", "35.00", "8.00", "5.00", "4.00", "30.00", "180.00", "95.00", "135.71", "85.00", "47.22", "SAUDAVEL", 14),
                simulacao("SPACES", "Sala estética por hora", 60, "2.00", "35.00", "0.00", "0.00", "0.00", "5.00", "30.00", "60.00", "42.00", "60.00", "18.00", "30.00", "SAUDAVEL", 13),
                simulacao("SALAO", "Corte feminino", 45, "8.00", "18.00", "7.00", "0.00", "0.00", "2.00", "30.00", "90.00", "35.00", "50.00", "55.00", "61.11", "SAUDAVEL", 12),
                simulacao("BEAUTY", "Drenagem linfática", 60, "15.00", "30.00", "28.00", "0.00", "2.00", "5.00", "30.00", "130.00", "80.00", "114.29", "50.00", "38.46", "SAUDAVEL", 11),
                simulacao("BEAUTY", "Massagem relaxante", 60, "16.00", "35.00", "45.00", "0.00", "4.00", "5.00", "30.00", "120.00", "105.00", "150.00", "15.00", "12.50", "MARGEM_BAIXA", 10),
                simulacao("FISIO", "Fisioterapia domiciliar", 70, "12.00", "25.00", "70.00", "25.00", "8.00", "5.00", "30.00", "160.00", "145.00", "207.14", "15.00", "9.38", "MARGEM_BAIXA", 9),
                simulacao("BIOMED", "Bioestimulador", 75, "560.00", "45.00", "70.00", "10.00", "5.00", "30.00", "30.00", "950.00", "720.00", "1028.57", "230.00", "24.21", "MARGEM_BAIXA", 8),
                simulacao("SPACES", "Sala de atendimento nutricional por hora", 60, "3.00", "45.00", "0.00", "0.00", "0.00", "8.00", "30.00", "70.00", "56.00", "80.00", "14.00", "20.00", "MARGEM_BAIXA", 7),
                simulacao("NUTRI", "Plano alimentar personalizado", 90, "12.00", "36.00", "55.00", "5.00", "5.00", "5.00", "30.00", "130.00", "118.00", "168.57", "12.00", "9.23", "MARGEM_BAIXA", 6),
                simulacao("BIOMED", "Avaliação estética biomédica", 60, "95.00", "60.00", "85.00", "20.00", "5.00", "10.00", "30.00", "240.00", "275.00", "392.86", "-35.00", "-14.58", "PREJUIZO", 5),
                simulacao("BIOMED", "Protocolo capilar", 60, "110.00", "35.00", "50.00", "5.00", "5.00", "5.00", "30.00", "180.00", "210.00", "300.00", "-30.00", "-16.67", "PREJUIZO", 4),
                simulacao("FISIO", "Reabilitação funcional", 60, "18.00", "45.00", "72.00", "12.00", "3.00", "5.00", "30.00", "140.00", "155.00", "221.43", "-15.00", "-10.71", "PREJUIZO", 3),
                simulacao("SPACES", "Cabine estética por diária", 480, "15.00", "220.00", "0.00", "0.00", "0.00", "25.00", "30.00", "210.00", "260.00", "371.43", "-50.00", "-23.81", "PREJUIZO", 2),
                simulacao("SALAO", "Coloração", 120, "74.00", "28.00", "35.00", "0.00", "3.00", "5.00", "30.00", "120.00", "145.00", "207.14", "-25.00", "-20.83", "PREJUIZO", 1)
        );

        for (SimulacaoDemo simulacao : simulacoes) {
            UUID empresaId = empresas.get(simulacao.empresaCodigo());
            UUID servicoId = Objects.requireNonNull(
                    servicos.get(chaveServico(simulacao.empresaCodigo(), simulacao.nomeProcedimento())),
                    "servico demo nao encontrado: " + simulacao.nomeProcedimento()
            );
            Instant atualizadoEm = contexto.agora().minusSeconds(simulacao.diasAtras() * 24L * 60L * 60L);
            jdbcTemplate.update(
                    """
                    insert into precificacao_simulacoes (
                        id, empresa_id, servico_procedimento_id, nome_procedimento, duracao_minutos,
                        custo_insumos, custo_sala_por_hora, valor_hora_profissional, custo_deslocamento,
                        custo_alimentacao, taxas, margem_desejada_percentual, preco_venda, custo_total,
                        preco_minimo, preco_recomendado, lucro_estimado, margem_real_percentual,
                        status_margem, ativo, criado_em, atualizado_em
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, ?, ?)
                    on conflict (id) do update
                    set empresa_id = excluded.empresa_id,
                        servico_procedimento_id = excluded.servico_procedimento_id,
                        nome_procedimento = excluded.nome_procedimento,
                        duracao_minutos = excluded.duracao_minutos,
                        custo_insumos = excluded.custo_insumos,
                        custo_sala_por_hora = excluded.custo_sala_por_hora,
                        valor_hora_profissional = excluded.valor_hora_profissional,
                        custo_deslocamento = excluded.custo_deslocamento,
                        custo_alimentacao = excluded.custo_alimentacao,
                        taxas = excluded.taxas,
                        margem_desejada_percentual = excluded.margem_desejada_percentual,
                        preco_venda = excluded.preco_venda,
                        custo_total = excluded.custo_total,
                        preco_minimo = excluded.preco_minimo,
                        preco_recomendado = excluded.preco_recomendado,
                        lucro_estimado = excluded.lucro_estimado,
                        margem_real_percentual = excluded.margem_real_percentual,
                        status_margem = excluded.status_margem,
                        ativo = true,
                        atualizado_em = excluded.atualizado_em
                    """,
                    uuidDemo("simulacao:" + simulacao.empresaCodigo() + ":" + simulacao.nomeProcedimento()),
                    empresaId,
                    servicoId,
                    simulacao.nomeProcedimento(),
                    simulacao.duracaoMinutos(),
                    dinheiro(simulacao.custoInsumos()),
                    dinheiro(simulacao.custoSalaPorHora()),
                    dinheiro(simulacao.valorHoraProfissional()),
                    dinheiro(simulacao.custoDeslocamento()),
                    dinheiro(simulacao.custoAlimentacao()),
                    dinheiro(simulacao.taxas()),
                    dinheiro(simulacao.margemDesejadaPercentual()),
                    dinheiro(simulacao.precoVenda()),
                    dinheiro(simulacao.custoTotal()),
                    dinheiro(simulacao.custoTotal()),
                    dinheiro(simulacao.precoRecomendado()),
                    dinheiro(simulacao.lucroEstimado()),
                    dinheiro(simulacao.margemRealPercentual()),
                    simulacao.status(),
                    Timestamp.from(atualizadoEm),
                    Timestamp.from(atualizadoEm)
            );
        }
    }

    private UUID buscarIdEmpresaPorDocumento(String documento) {
        return jdbcTemplate.queryForObject("select id from empresas where documento = ?", UUID.class, documento);
    }

    private UUID buscarIdPlanoPorCodigo(String codigo) {
        return jdbcTemplate.queryForObject("select id from planos where codigo = ?", UUID.class, codigo);
    }

    private UUID buscarIdUsuarioPorEmail(String email) {
        return jdbcTemplate.queryForObject("select id from auth_usuarios where email = ?", UUID.class, email);
    }

    private Instant inicio(LocalDate data, int hora) {
        return data.atTime(LocalTime.of(hora, 0)).atZone(clock.getZone()).toInstant();
    }

    private static UUID uuidDemo(String chave) {
        return UUID.nameUUIDFromBytes((PREFIXO_UUID + chave).getBytes(StandardCharsets.UTF_8));
    }

    private static BigDecimal dinheiro(String valor) {
        return new BigDecimal(valor);
    }

    private static List<String> modulos(String... modulos) {
        return List.of(modulos);
    }

    private static String chaveServico(String empresaCodigo, String nome) {
        return empresaCodigo + "|" + nome;
    }

    private static String valorCustoEstrutura(String empresaCodigo) {
        return switch (empresaCodigo) {
            case "BIOMED" -> "5200.00";
            case "SPACES" -> "7400.00";
            case "SALAO" -> "3900.00";
            default -> "3200.00";
        };
    }

    private static String emailDemo(String nome) {
        return nome.toLowerCase()
                .replace("ã", "a")
                .replace("á", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("ú", "u")
                .replace("ç", "c")
                .replace(".", "")
                .replace(" ", ".") + "@demo.atendepro.local";
    }

    private static ClienteDemo cliente(String empresaCodigo, String nome, String tipo, String area, String dataNascimento) {
        return new ClienteDemo(empresaCodigo, nome, tipo, area, dataNascimento);
    }

    private static ServicoDemo servico(String empresaCodigo, String nome, String area, int duracaoMinutos, String precoBase) {
        return new ServicoDemo(empresaCodigo, nome, area, duracaoMinutos, precoBase);
    }

    private static ProdutoDemo produto(
            String empresaCodigo,
            String nome,
            String categoria,
            String unidade,
            String quantidadeAtual,
            String custoUnitario,
            String estoqueMinimo,
            int diasParaValidade
    ) {
        return new ProdutoDemo(empresaCodigo, nome, categoria, unidade, quantidadeAtual, custoUnitario,
                estoqueMinimo, diasParaValidade);
    }

    private static EquipamentoDemo equipamento(
            String empresaCodigo,
            String nome,
            String categoria,
            String marca,
            String modelo,
            String valorAquisicao
    ) {
        return new EquipamentoDemo(empresaCodigo, nome, categoria, marca, modelo, valorAquisicao);
    }

    private static AgendaDemo agenda(
            String empresaCodigo,
            String usuarioCodigo,
            String profissionalNome,
            String sala,
            String tipo,
            String status,
            int dias,
            int hora,
            int duracaoMinutos
    ) {
        return new AgendaDemo(empresaCodigo, usuarioCodigo, profissionalNome, sala, tipo, status, dias, hora,
                duracaoMinutos);
    }

    private static SimulacaoDemo simulacao(
            String empresaCodigo,
            String nomeProcedimento,
            int duracaoMinutos,
            String custoInsumos,
            String custoSalaPorHora,
            String valorHoraProfissional,
            String custoDeslocamento,
            String custoAlimentacao,
            String taxas,
            String margemDesejadaPercentual,
            String precoVenda,
            String custoTotal,
            String precoRecomendado,
            String lucroEstimado,
            String margemRealPercentual,
            String status,
            int diasAtras
    ) {
        return new SimulacaoDemo(empresaCodigo, nomeProcedimento, duracaoMinutos, custoInsumos, custoSalaPorHora,
                valorHoraProfissional, custoDeslocamento, custoAlimentacao, taxas, margemDesejadaPercentual,
                precoVenda, custoTotal, precoRecomendado, lucroEstimado, margemRealPercentual, status, diasAtras);
    }

    private record ContextoDemo(Instant agora, LocalDate hoje) {
    }

    private record EmpresaDemo(
            String codigo,
            String nomeFantasia,
            String razaoSocial,
            String documento,
            String email,
            String telefone
    ) {
    }

    private record PlanoDemo(
            String codigo,
            String nome,
            String descricao,
            String valorMensal,
            int limiteUsuarios,
            int limiteClientes,
            int limiteProfissionais,
            boolean estudante,
            String marcaDaguaAcademica,
            List<String> modulos
    ) {
    }

    private record AssinaturaDemo(String empresaCodigo, String planoCodigo) {
    }

    private record UsuarioDemo(
            String codigo,
            String nome,
            String email,
            String empresaCodigo,
            List<String> perfis
    ) {
    }

    private record ClienteDemo(
            String empresaCodigo,
            String nome,
            String tipo,
            String area,
            String dataNascimento
    ) {
    }

    private record ServicoDemo(
            String empresaCodigo,
            String nome,
            String area,
            int duracaoMinutos,
            String precoBase
    ) {
    }

    private record ProdutoDemo(
            String empresaCodigo,
            String nome,
            String categoria,
            String unidade,
            String quantidadeAtual,
            String custoUnitario,
            String estoqueMinimo,
            int diasParaValidade
    ) {
    }

    private record EquipamentoDemo(
            String empresaCodigo,
            String nome,
            String categoria,
            String marca,
            String modelo,
            String valorAquisicao
    ) {
    }

    private record AgendaDemo(
            String empresaCodigo,
            String usuarioCodigo,
            String profissionalNome,
            String sala,
            String tipo,
            String status,
            int dias,
            int hora,
            int duracaoMinutos
    ) {
    }

    private record RecursoSpacesDemo(
            String codigo,
            String nome,
            String tipo,
            String descricao,
            int capacidadePessoas,
            String localizacao
    ) {
    }

    private record PacoteSpacesDemo(
            String codigo,
            String recursoCodigo,
            String nome,
            String tipo,
            String duracaoHoras,
            String valorFixo,
            String percentualReceita
    ) {
    }

    private record SimulacaoDemo(
            String empresaCodigo,
            String nomeProcedimento,
            int duracaoMinutos,
            String custoInsumos,
            String custoSalaPorHora,
            String valorHoraProfissional,
            String custoDeslocamento,
            String custoAlimentacao,
            String taxas,
            String margemDesejadaPercentual,
            String precoVenda,
            String custoTotal,
            String precoRecomendado,
            String lucroEstimado,
            String margemRealPercentual,
            String status,
            int diasAtras
    ) {
    }
}
