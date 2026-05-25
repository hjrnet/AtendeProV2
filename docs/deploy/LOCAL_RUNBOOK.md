# Local Runbook

## Infra

```bash
cp .env.example .env
docker compose up -d --build
docker compose ps
```

O Compose nao fixa `container_name`; os nomes ficam sob controle do projeto Docker Compose para evitar conflito entre clones ou ambientes locais diferentes.

Detalhes de variaveis e profiles ficam em `docs/deploy/ENVIRONMENT.md`.

## Backend

```bash
cd backend
mvn test
mvn spring-boot:run
```

`mvn test` pode iniciar Testcontainers e exige Docker local disponivel.
O backend exige Java 21. No Windows, se o Maven estiver apontando para Java 17, use:

```powershell
$env:JAVA_HOME='C:\Program Files\Zulu\zulu-21'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn test
```

## Dados demo locais

Ao subir o backend com o profile `local`, o AtendePro popula dados ficticios e idempotentes para apresentacao:

- empresas demo por vertical;
- usuarios demo com senha `AtendePro@123`;
- planos comerciais e assinaturas ativas;
- clientes/pacientes ficticios;
- agenda, procedimentos, custos, estoque, equipamentos;
- dados Spaces;
- simulacoes de precificacao saudaveis, em margem baixa e em prejuizo.

Logins:

| Perfil | E-mail | Senha |
|---|---|---|
| Super Admin | `admin@atendepro.local` | `AtendePro@123` |
| Nutri Pro | `karol.nutri@atendepro.local` | `AtendePro@123` |
| Beauty Pro | `ana.estetica@atendepro.local` | `AtendePro@123` |
| Biomed Pro | `bianca.biomed@atendepro.local` | `AtendePro@123` |
| Fisio Pro | `felipe.fisio@atendepro.local` | `AtendePro@123` |
| Spaces | `paula.spaces@atendepro.local` | `AtendePro@123` |
| Estudante | `estudante@atendepro.local` | `AtendePro@123` |

Os dados ficam restritos ao profile `local` e usam apenas nomes, documentos e e-mails ficticios. Veja `docs/product/demo-data.md`.

## Web

```bash
cd web
corepack pnpm install
corepack pnpm lint
corepack pnpm build
corepack pnpm dev --host 0.0.0.0
```

## Teste no celular

1. Descobrir IP da máquina.
2. Configurar `NEXT_PUBLIC_API_URL=http://IP_DA_MAQUINA:8080`.
3. Acessar `http://IP_DA_MAQUINA:3000`.
4. Garantir CORS para origem do IP.
