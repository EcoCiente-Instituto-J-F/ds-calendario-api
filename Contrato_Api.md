# Contrato da API

## Implementado

Prefixo: `/api/v1/agendamentos`. Todas as rotas exigem JWT; `POST` e `PATCH` exigem o perfil `SINDICO` ou `COOPERATIVA`.

| Método | Endpoint                                  | Objetivo                                                                 |
| ------ | ----------------------------------------- | ------------------------------------------------------------------------ |
| GET    | `/api/v1/agendamentos`                    | Listar agendamentos (filtros: `status`, `dataInicio`, `dataFim`, `possuiRecorrencia`, `condominioId`, `cooperativaId`) |
| GET    | `/api/v1/agendamentos/proxima`            | Próxima coleta (mesmos filtros, exceto `status`)                         |
| POST   | `/api/v1/agendamentos`                    | Criar agendamento                                                        |
| PATCH  | `/api/v1/agendamentos/{id}/status`        | Alterar status                                                           |

## Previsto (ainda não implementado)

| Método | Endpoint                         | Objetivo                       |
| ------ | -------------------------------- | ------------------------------ |
| GET    | `/calendario`                    | Listar calendário de coletas   |
| GET    | `/calendario/proxima`            | Próxima coleta                 |
| GET    | `/agendamentos/{id}`             | Buscar um agendamento          |
| GET    | `/agendamentos/historico`        | Histórico de coletas           |
| POST   | `/agendamentos`                  | Criar agendamento              |
| PUT    | `/agendamentos/{id}`             | Atualizar agendamento          |
| PATCH  | `/agendamentos/{id}/status`      | Alterar status                 |
| PATCH  | `/agendamentos/{id}/confirmacao` | Confirmar realização da coleta |
| GET    | `/recorrencias`                  | Listar recorrências            |
| POST   | `/recorrencias`                  | Criar recorrência              |
| PUT    | `/recorrencias/{id}`             | Atualizar recorrência          |
| DELETE | `/recorrencias/{id}`             | Excluir recorrência            |
