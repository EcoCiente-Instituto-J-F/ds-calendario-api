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
