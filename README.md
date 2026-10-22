# ds-calendario-api

![Java](https://img.shields.io/badge/Java-19-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub repo size](https://img.shields.io/github/repo-size/EcoCiente-Instituto-J-F/ds-calendario-api?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/EcoCiente-Instituto-J-F/ds-calendario-api?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-green?style=for-the-badge)

> API responsável por gerenciar o calendário de coletas de materiais recicláveis: consulta de datas e horários por condomínio ou cooperativa, criação de agendamentos e atualização de status.

## Sobre o projeto

A aplicação permite:

* Consultar os agendamentos de coleta, filtrando por status, período, recorrência, condomínio e cooperativa;
* Consultar a próxima coleta;
* Criar um agendamento de coleta (perfis síndico e cooperativa);
* Atualizar o status de uma coleta (`AGENDADO`, `CONFIRMADO`, `RECUSADO`, `CANCELADO`, `REALIZADO`), informando alterações e cancelamentos;
* Fornecer dados para integração com outros serviços do sistema, via API REST documentada com Swagger.

### Perfis de acesso

O conteúdo retornado depende do perfil do usuário autenticado:

| Perfil | Pode consultar | Pode criar e alterar status |
| --- | --- | --- |
| `SINDICO` | Agendamentos dos próprios condomínios | Sim |
| `COOPERATIVA` | Agendamentos associados à própria cooperativa | Sim |
| `MORADOR` | Consulta do calendário | Não |

## Pré-requisitos

* **JDK 19**. O projeto define `sourceCompatibility` 19 no `build.gradle`, e o Dockerfile e o CI também usam Java 19. Confira com:

  ```powershell
  java -version
  ```

  Caso a versão exibida seja diferente, configure o JDK correto no IDE (no Visual Studio Code, use o JDK 19 como runtime do Java).
* **Gradle não precisa ser instalado**: o projeto usa o Gradle Wrapper (`gradlew`, versão 8.14.3).
* **Docker**, para os testes de integração (que sobem um PostgreSQL 16 com Testcontainers) e para executar via `docker compose`.
* **PostgreSQL** acessível, para rodar a aplicação fora do Docker. O schema do banco não é criado pela aplicação (`spring.jpa.hibernate.ddl-auto=validate`); a referência está em [`src/test/resources/sql/ecociente-schema.sql`](src/test/resources/sql/ecociente-schema.sql).

## Instalando

Clone o repositório:

```bash
git clone https://github.com/EcoCiente-Instituto-J-F/ds-calendario-api.git
cd ds-calendario-api
```

Crie o arquivo `.env` a partir do exemplo e preencha os valores reais:

```bash
cp .env.example .env
```

No PowerShell:

```powershell
Copy-Item .env.example .env
```

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `DB_URL` | Sim | URL JDBC do PostgreSQL, por exemplo `jdbc:postgresql://host:5432/ecociente?sslmode=require` |
| `DB_USER` | Sim | Usuário do banco |
| `DB_PASSWORD` | Sim | Senha do banco |
| `JWT_SECRET` | Sim | Chave usada para validar os tokens JWT. Use pelo menos 32 caracteres |
| `SPRING_DATASOURCE_HIKARI_*` | Não | Configuração do pool de conexões (valores de exemplo já estão em `.env.example`) |

> **Nunca** versione o arquivo `.env`. Ele já está no `.gitignore`.

## Usando

### Executando no Visual Studio Code

Abra a pasta do projeto no Visual Studio Code. A pasta aberta deve conter arquivos como:

```text
build.gradle
settings.gradle
gradlew
gradlew.bat
src
```

Abra o terminal integrado (`Ctrl + ``) e certifique-se de que ele está usando o **PowerShell**. Depois execute:

```powershell
.\gradlew.bat clean bootRun --no-daemon
```

Quando a aplicação iniciar corretamente, será apresentada no terminal uma mensagem semelhante a:

```text
Started CalendarioApplication
```

Em Linux ou macOS, use `./gradlew clean bootRun --no-daemon`.

### Executando com Docker

```bash
docker compose up --build
```

A API fica disponível na porta **9800**. O `docker-compose.yml` lê as variáveis do arquivo `.env`.

> **Atenção:** dentro do container, `localhost` aponta para o próprio container. Se o PostgreSQL estiver na sua máquina, use o endereço real do host na `DB_URL`, e não `localhost`.

### Documentação da API (Swagger)

Com a aplicação rodando, acesse:

```text
http://localhost:9800/swagger-ui.html
```

A documentação OpenAPI está em `/v3/api-docs`. Ela é a referência atual dos endpoints.

### Autenticação

As rotas exigem um token JWT no header `Authorization`:

```text
Authorization: Bearer <token>
```

O token é assinado com HMAC usando o `JWT_SECRET` e deve conter os claims:

* `usuarioId`: identificador numérico do usuário;
* `perfil`: `SINDICO`, `COOPERATIVA` ou `MORADOR`.

A emissão dos tokens não faz parte deste repositório.

### Endpoints

| Método | Endpoint | Perfis | Objetivo |
| --- | --- | --- | --- |
| `GET` | `/api/v1/agendamentos` | Todos os autenticados | Listar agendamentos, com paginação e filtros (`status`, `dataInicio`, `dataFim`, `possuiRecorrencia`, `condominioId`, `cooperativaId`) |
| `GET` | `/api/v1/agendamentos/proxima` | Todos os autenticados | Buscar o próximo agendamento (retorna `404` se não houver) |
| `POST` | `/api/v1/agendamentos` | `SINDICO`, `COOPERATIVA` | Criar um agendamento de coleta (retorna `201`) |
| `PATCH` | `/api/v1/agendamentos/{id}/status` | `SINDICO`, `COOPERATIVA` | Atualizar o status de um agendamento |

Exemplo de criação:

```bash
curl -X POST http://localhost:9800/api/v1/agendamentos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "condominioId": 10,
        "cooperativaId": 5,
        "dataInicio": "2030-01-15T08:00:00-03:00",
        "dataFim": "2030-01-15T10:00:00-03:00"
      }'
```

`dataInicio` precisa estar no futuro, e `dataFim`, quando informado, não pode ser anterior a `dataInicio`.

Exemplo de atualização de status:

```bash
curl -X PATCH http://localhost:9800/api/v1/agendamentos/42/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMADO"}'
```

Respostas de erro comuns: `400` (dados inválidos), `401` (token ausente, inválido ou expirado), `403` (perfil sem permissão) e `404` (recurso não encontrado).

## Testes

Os testes unitários e de integração são executados com o mesmo comando usado no CI:

```bash
./gradlew clean test --no-daemon      # Linux/macOS
```

```powershell
.\gradlew.bat clean test --no-daemon  # Windows
```

Os testes de integração usam Testcontainers e sobem um PostgreSQL 16 temporário, então o **Docker precisa estar em execução**.

Para gerar o JAR sem rodar os testes:

```bash
./gradlew bootJar -x test --no-daemon
```

## Estrutura do projeto

```text
.
├── .github/workflows/          # CI (build e testes) e bot que gera a descrição de PRs
├── k8s/                        # Manifests Kubernetes (namespace, deployment, service, configmap e exemplo de secret)
├── scripts/pr-bot/             # Script Python que gera a descrição das Pull Requests
├── src/
│   ├── main/java/br/com/ecociente/calendario/
│   │   ├── config/             # Segurança (JWT, SecurityConfig) e configuração do Swagger
│   │   ├── core/               # Regras de negócio: domínio, serviços, exceções e interfaces de gateway
│   │   ├── dataprovider/       # Implementação de acesso a dados: entidades, repositórios, gateways e mappers
│   │   └── entrypoint/         # Entrada HTTP: controllers, DTOs, mappers e tratamento de exceções
│   ├── main/resources/
│   │   └── application.properties
│   └── test/                   # Testes unitários, de controller e de integração, com scripts SQL de dados fictícios
├── Contrato_Api.md             # Documento de contrato da API
├── Dockerfile                  # Build multi-stage (Temurin 19) e execução com usuário não-root
├── docker-compose.yml
├── build.gradle
├── settings.gradle
└── gradlew / gradlew.bat       # Gradle Wrapper
```

As dependências entre camadas seguem a direção `entrypoint → core ← dataprovider`: o pacote `core` não importa os pacotes `entrypoint` nem `dataprovider`.

## Solução de problemas

### Erro ao apagar a pasta `build`

Durante a compilação, pode ocorrer o seguinte erro:

```text
Execution failed for task ':compileJava'.

Unable to delete directory:
build\classes\java\main
```

Esse erro normalmente acontece quando algum processo Java ou Gradle ainda está utilizando os arquivos da pasta `build`.

Primeiro, interrompa qualquer execução anterior da aplicação pressionando:

```text
Ctrl + C
```

Depois, execute os comandos abaixo no terminal PowerShell.

#### 1. Parar os processos do Gradle

```powershell
.\gradlew.bat --stop
```

#### 2. Encerrar processos Java em execução

```powershell
Get-Process java,javaw -ErrorAction SilentlyContinue | Stop-Process -Force
```

> **Atenção:** esse comando encerra todos os processos Java em execução no computador, incluindo outras aplicações Java que estejam abertas.

#### 3. Remover a pasta de compilação

```powershell
Remove-Item -Recurse -Force .\build -ErrorAction SilentlyContinue
```

#### 4. Remover o cache local do Gradle

```powershell
Remove-Item -Recurse -Force .\.gradle -ErrorAction SilentlyContinue
```

#### 5. Confirmar a versão do Java

```powershell
java -version
```

#### 6. Executar novamente a aplicação

```powershell
.\gradlew.bat clean bootRun --no-daemon
```

### Sequência completa para corrigir o erro

Caso seja necessário realizar toda a limpeza, execute:

```powershell
.\gradlew.bat --stop

Get-Process java,javaw -ErrorAction SilentlyContinue | Stop-Process -Force

Remove-Item -Recurse -Force .\build -ErrorAction SilentlyContinue

Remove-Item -Recurse -Force .\.gradle -ErrorAction SilentlyContinue

java -version

.\gradlew.bat clean bootRun --no-daemon
```

Caso o problema continue, feche o Visual Studio Code, reinicie o computador, abra novamente a pasta do projeto e execute:

```powershell
.\gradlew.bat clean bootRun --no-daemon
```

## Contribuindo

Leia o [CONTRIBUTING.md](CONTRIBUTING.md) para saber como configurar o ambiente, testar sua mudança e abrir uma Pull Request. Participar do projeto também significa seguir o [Código de Conduta](CODE_OF_CONDUCT.md).

## Segurança

Encontrou uma vulnerabilidade? Siga o [SECURITY.md](SECURITY.md). **Não abra issue pública.**

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE).
