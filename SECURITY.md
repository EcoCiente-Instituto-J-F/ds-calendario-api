# Política de Segurança

<<<<<<< HEAD
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
=======
A API autentica usuários por JWT e retorna agendamentos de coleta vinculados a condomínios e cooperativas, com regras de acesso por perfil (síndico, cooperativa e morador). Falhas que exponham esses dados ou contornem o controle de acesso são tratadas com prioridade.

## Versões suportadas

O projeto não publica releases versionadas. Apenas a branch `main` recebe correções de segurança.

| Versão | Suporte de segurança |
| --- | --- |
| `main` (mais recente) | ✅ |
| Branches de funcionalidade e versões antigas | ❌ |

## Reportando uma vulnerabilidade

**Não abra issue, Pull Request ou discussão pública** descrevendo a falha. Isso expõe o problema antes da correção.

Reporte pelo canal privado do GitHub:

1. Acesse [Security → Advisories → Report a vulnerability](https://github.com/EcoCiente-Instituto-J-F/ds-calendario-api/security/advisories/new).
2. Preencha o formulário com as informações abaixo.

> **Para mantenedores:** o recurso *Private vulnerability reporting* precisa estar ativo em **Settings → Code security**. Sem ele, o link acima não funciona para quem reporta.

### O que incluir no relato

* **Impacto:** o que um atacante consegue fazer e quais dados ficam expostos;
* **Passos para reproduzir:** requisições exatas, usando apenas dados fictícios;
* **Versão testada:** commit ou branch, e a configuração usada (sem incluir segredos reais);
* **Sugestão de correção**, se você tiver uma.

### O que acontece depois

1. Confirmamos o recebimento e avaliamos o relato.
2. Trabalhamos na correção em canal privado com quem reportou.
3. Publicamos um advisory com a descrição da falha e, se você quiser, com crédito pela descoberta.

Pedimos que aguarde a correção ser publicada antes de divulgar qualquer detalhe.

## Escopo

### Dentro do escopo

* Acesso a dados de outro usuário, como um síndico consultando agendamentos de um condomínio que não gerencia;
* Contorno de autenticação ou autorização (validação de JWT, perfis e regras de rota);
* Injeção de SQL ou de comandos que exponha dados;
* Vazamento de dados pessoais ou segredos em logs, respostas de erro ou stack traces;
* Segredos commitados no repositório;
* Falhas na configuração de deploy que exponham segredos ou dados.

### Fora do escopo

* Bugs sem impacto de segurança;
* Falhas em serviços de terceiros, como o PostgreSQL ou o Docker Hub;
* Valores de exemplo de `.env.example` e `k8s/secret.example.yaml` (`SUA_...`), que devem ser substituídos em cada ambiente;
* Ataques de negação de serviço contra a instância de deploy de outra pessoa.
>>>>>>> 3fb3425143445328b11d6481740df26d7eaf45a1

## Como o sistema protege os dados

| Camada | Onde | O que faz |
| --- | --- | --- |
<<<<<<< HEAD
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
=======
| Validação de JWT | `config/security/JwtService.java` e `config/security/JwtAuthenticationFilter.java` | Verifica a assinatura HMAC com `JWT_SECRET` e exige os claims `usuarioId` e `perfil`. Token inválido ou expirado retorna `401`; perfil desconhecido retorna `403` |
| Autorização por rota | `config/security/SecurityConfig.java` | `POST` e `PATCH` em `/api/v1/agendamentos/**` exigem os perfis `SINDICO` ou `COOPERATIVA`. Todas as demais rotas exigem autenticação |
| Consulta por perfil | `core/service/ResolvedorConsultaAgendamento.java` e `ListarAgendamentoService.java` | A consulta é resolvida a partir do `usuarioId` e do `perfil` do token, e não de parâmetros enviados pelo cliente |
| Sessão sem estado | `config/security/SecurityConfig.java` | A API não cria sessão no servidor (`STATELESS`). Como a autenticação é feita pelo header `Authorization`, a proteção CSRF está desabilitada |
| Segredos fora do código | `src/main/resources/application.properties` e `.gitignore` | Credenciais são lidas de variáveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`). `.env` e `k8s/secret.yaml` não são versionados |
| Processo sem root | `Dockerfile` | A imagem cria o usuário de sistema `spring` e executa a aplicação com ele |

## Checklist de deploy

Antes de colocar a API em produção, confirme:

- [ ] `JWT_SECRET` tem pelo menos 32 caracteres, é único por ambiente e está em um `Secret` do Kubernetes ou em um cofre de segredos, nunca em `ConfigMap`, imagem ou repositório;
- [ ] Todos os valores `SUA_...` de `.env.example` e `k8s/secret.example.yaml` foram substituídos;
- [ ] `DB_URL` usa `sslmode=require` (ou equivalente), como no exemplo;
- [ ] O usuário do banco tem apenas as permissões que a aplicação usa, sem ser dono das tabelas;
- [ ] A Swagger UI (`/swagger-ui/**`, `/v3/api-docs/**`) está liberada sem autenticação no `SecurityConfig`. Avalie desabilitá-la ou restringi-la em produção;
- [ ] A porta 9800 fica atrás de um proxy ou balanceador com TLS, e não exposta diretamente à internet;
- [ ] Logs não registram tokens, senhas nem dados pessoais.
>>>>>>> 3fb3425143445328b11d6481740df26d7eaf45a1
