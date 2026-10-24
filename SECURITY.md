# Política de Segurança

O ds-calendario-api gerencia agendamentos e visitas de coleta de recicláveis, consulta dados de condomínios, cooperativas e moradores em PostgreSQL e autentica as requisições com JWT.

## Versões suportadas

| Versão | Suporte |
| --- | --- |
| `main` (mais recente) | Sim |
| Demais branches | Não |

## Reportando uma vulnerabilidade

**Não abra issue, Pull Request ou discussão pública** para relatar vulnerabilidades.

Use o relato privado do GitHub: <https://github.com/EcoCiente-Instituto-J-F/ds-calendario-api/security/advisories/new> (Security → Advisories → Report a vulnerability).

> Mantenedores: ative *Private vulnerability reporting* em Settings → Code security.

Inclua no relato:

- o impacto da falha;
- os passos para reproduzir, usando **dados fictícios**;
- o commit testado e a configuração;
- se possível, uma ideia de correção.

Depois do relato: confirmaremos o recebimento, corrigiremos de forma privada e publicaremos um advisory com crédito a quem reportou, se desejado. Pedimos um prazo razoável para a correção antes de qualquer divulgação pública.

## Escopo

**Dentro do escopo**

- Burla de autenticação (JWT) ou de autorização por perfil (`SINDICO`, `COOPERATIVA`, morador)
- Acesso a agendamentos ou dados de outro condomínio, cooperativa ou morador
- Criar agendamento ou alterar status sem o perfil permitido
- Injeção SQL/JPQL
- Vazamento de dados pessoais, de `JWT_SECRET` ou de credenciais de banco em logs, respostas ou repositório
- Segredos commitados e falhas nos workflows (`ci.yml`, `pr-bot.yml`) que exponham segredos

**Fora do escopo**

- Bugs sem impacto de segurança
- Falhas em serviços de terceiros (PostgreSQL gerenciado, GitHub)
- Valores de exemplo do `.env.example` e do `k8s/secret.example.yaml`
- Negação de serviço no próprio deploy

## Como o sistema protege os dados

| Camada | Onde | O que faz |
| --- | --- | --- |
| Autenticação | `config/security/JwtService.java`, `JwtAuthenticationFilter.java` | Valida o JWT; sessão stateless; login por formulário e HTTP Basic desabilitados |
| Autorização | `config/security/SecurityConfig.java` | `POST` e `PATCH` em `/api/v1/agendamentos/**` só para `SINDICO` e `COOPERATIVA`; o restante exige autenticação; só Swagger e `/actuator/health` são públicos |
| Consulta por perfil | `core/service/ResolvedorConsultaAgendamento.java` | Escolhe a consulta conforme o perfil do usuário |
| Banco | `application.properties` | Hibernate em `validate` (não altera o schema) |
| Erros | `entrypoint/exception/` | Respostas de erro padronizadas |

## Checklist de deploy

- `JWT_SECRET` com pelo menos 32 caracteres e `DB_PASSWORD` em Secrets do Kubernetes (veja `k8s/secret.example.yaml`), nunca em ConfigMap, imagem ou repositório.
- Usuário de banco com o mínimo de permissões; conexão com `sslmode=require`.
- Avaliar restringir `/swagger-ui.html` e `/v3/api-docs` em produção.
- Servir somente via HTTPS.
