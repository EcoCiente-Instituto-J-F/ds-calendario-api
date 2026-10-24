# Contribuindo com o ds-calendario-api

Ao participar, você concorda em seguir o [Código de Conduta](CODE_OF_CONDUCT.md). Para reportar um problema de segurança, **não** abra uma issue — siga o [SECURITY.md](SECURITY.md).

## Idioma

<<<<<<< HEAD
Documentação, issues, Pull Requests e mensagens ao usuário em português do Brasil.

## Configurando o ambiente

1. Instale o JDK 19 e o Docker (os testes de integração usam Testcontainers com PostgreSQL).
2. Clone o repositório:
   ```bash
   git clone https://github.com/EcoCiente-Instituto-J-F/ds-calendario-api.git
   cd ds-calendario-api
   ```
3. Crie um `.env` a partir do `.env.example` (ignorado pelo Git) e ajuste os valores.
4. Execute com `./gradlew bootRun` (Windows: `.\gradlew.bat bootRun`).

## Testando sua mudança

O CI roda:

```bash
./gradlew clean test --no-daemon
```

Toda mudança de comportamento deve vir com teste (JUnit) que falharia sem ela, em `src/test/java`. Os testes não devem depender de rede, APIs pagas ou dados de produção.

## Regras do projeto

- Nenhum segredo (chaves, tokens, senhas) no repositório.
- Nenhum dado pessoal real em testes, fixtures ou exemplos.
- Nenhuma dependência nova sem discussão prévia.
- Mantenha as camadas `config` / `core` / `dataprovider` / `entrypoint`: o `core` depende apenas de gateways (interfaces).
- Novas variáveis lidas em `application.properties` entram também no `.env.example` e na documentação.
- A autorização por perfil fica em `SecurityConfig`; não duplique essa regra nos controllers.

## Pull requests

1. Crie uma branch a partir da `main` (ex.: `feat/nome-da-mudanca`).
2. Faça commits focados com prefixos `feat:`, `fix:`, `chore:`, `docs:`, `test:` ou `refactor:`.
3. Abra a PR para a `main`. O bot do projeto preenche a descrição automaticamente (PRs de forks não recebem esse recurso).
4. O CI precisa passar (quando houver) e um mantenedor revisa antes do merge.

## Reportando bugs e propondo funcionalidades

Abra uma issue com o comportamento esperado, o observado, os passos para reproduzir e a versão. Use dados fictícios. Para vulnerabilidades, siga o [SECURITY.md](SECURITY.md).
=======
A documentação, as issues, os comentários e as descrições de Pull Request são escritos em português do Brasil.

## Configurando o ambiente

Você precisa de **JDK 19** e, para os testes, de **Docker** em execução. O Gradle não precisa ser instalado, pois o projeto usa o Gradle Wrapper.

```bash
git clone https://github.com/EcoCiente-Instituto-J-F/ds-calendario-api.git
cd ds-calendario-api
cp .env.example .env   # no PowerShell: Copy-Item .env.example .env
```

Preencha o `.env` com valores de desenvolvimento. Os passos completos estão na seção [Instalando](README.md#instalando) do README.

## Testando sua mudança

Rode a suíte completa antes de abrir a PR. É o mesmo comando que o CI executa:

```bash
./gradlew clean test --no-daemon      # Linux/macOS
.\gradlew.bat clean test --no-daemon  # Windows
```

Regras para testes:

* Toda mudança de comportamento deve vir acompanhada de um teste que **falharia sem ela**.
* Testes não podem depender de serviços externos, de APIs pagas ou de dados de produção.
* Dados de teste devem ser fictícios. Os scripts em `src/test/resources/sql/` usam e-mails como `@teste.com`; mantenha esse padrão e não copie dados reais de produção para eles.

## Regras do projeto

* **Nenhum segredo no repositório.** Apenas `.env.example` e `k8s/secret.example.yaml` são versionados, com valores de exemplo. `.env` e `k8s/secret.yaml` estão no `.gitignore`.
* **Nenhum dado pessoal real** em testes, exemplos, SQL ou documentação.
* **Separação de camadas.** O pacote `core` não pode importar `entrypoint` nem `dataprovider`. Regras de negócio ficam em `core`, a implementação de acesso a dados em `dataprovider` e a camada HTTP em `entrypoint`.
* **Regras de acesso em um só lugar.** As permissões por rota ficam no `SecurityConfig`. Não use anotações de autorização espalhadas pelos controllers sem discutir antes.
* **Configuração em um só lugar.** Propriedades lêem variáveis de ambiente em `application.properties`. Ao introduzir uma variável nova, atualize `.env.example` e, se aplicável, os manifests em `k8s/`.
* **Nenhuma dependência nova sem discussão.** Abra uma issue explicando a necessidade antes de adicionar algo ao `build.gradle`.

## Pull requests

1. Crie a branch a partir da `main`, com prefixo que indique o tipo de mudança, por exemplo `feat/nome-da-funcionalidade`, `fix/descricao-do-erro` ou `chore/tarefa`. As branches existentes seguem esse padrão.
2. Faça commits focados em uma mudança, com mensagens no formato `tipo: descrição` (por exemplo `feat: permite consulta do calendário por moradores`). Tipos usados no histórico: `feat:`, `fix:`, `chore:`, `docs:`, `test:` e `refactor:`.
3. Abra a PR para a branch `main`.
4. O CI (`.github/workflows/ci.yml`) roda em PRs para `main` e **precisa passar** antes da revisão.
5. Para PRs abertas a partir de branches do próprio repositório, o bot de descrição (`.github/workflows/pr-bot.yml`) preenche a descrição automaticamente. PRs vindas de forks não recebem esse preenchimento.
6. Um mantenedor revisa a PR. Responda aos comentários e mantenha a branch atualizada com a `main` quando necessário.

## Reportando bugs e propondo funcionalidades

Ao abrir uma issue de bug, descreva:

* o que aconteceu e o que era esperado;
* os passos para reproduzir;
* a branch ou commit testado e a versão do Java.

Não inclua tokens, senhas, valores de `.env` ou dados pessoais na issue.

Para propor uma funcionalidade, descreva primeiro o problema que ela resolve. A solução pode ser discutida depois.
>>>>>>> 3fb3425143445328b11d6481740df26d7eaf45a1
