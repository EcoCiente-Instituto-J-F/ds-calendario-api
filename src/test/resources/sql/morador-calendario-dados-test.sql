INSERT INTO tb_lkp_tipos_usuarios (
    id_tipo_usuario, nome_tipo
)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Síndico'),
    (2, 'Cooperativa'),
    (3, 'Morador');

INSERT INTO tb_lkp_tipos_condominios (
    id_tipo_condominio, nome_tipo
)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Residencial');

INSERT INTO tb_lkp_niveis_confianca (
    id_nivel_confianca, nome_nivel, peso_voto
)
OVERRIDING SYSTEM VALUE
VALUES (1, 'morador_comum', 1);

INSERT INTO tb_lkp_status_agendamentos (
    id_status, nome_status
)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Agendado'),
    (2, 'Confirmado'),
    (3, 'Recusado'),
    (4, 'Realizado'),
    (5, 'Cancelado');

INSERT INTO tb_enderecos (
    id_endereco, cep, estado, cidade, logradouro, numero
)
OVERRIDING SYSTEM VALUE
VALUES
    (1, '01001000', 'SP', 'São Paulo', 'Rua Um', '10'),
    (2, '01001000', 'SP', 'São Paulo', 'Rua Dois', '20');

INSERT INTO tb_usuarios (
    id_usuario, nome_usuario, email_usuario,
    senha_hash, ativo, tipo_usuario_id, endereco_id
)
OVERRIDING SYSTEM VALUE
VALUES
    (10, 'Síndico', 'sindico@teste.com', 'hash', TRUE, 1, 1),
    (20, 'Cooperativa', 'coop@teste.com', 'hash', TRUE, 2, 1),
    (70, 'Aprovado', 'aprovado@teste.com', 'hash', TRUE, 3, 1),
    (80, 'Pendente', 'pendente@teste.com', 'hash', TRUE, 3, 1),
    (90, 'Saiu', 'saiu@teste.com', 'hash', TRUE, 3, 1),
    (100, 'Inativo', 'inativo@teste.com', 'hash', FALSE, 3, 1),
    (110, 'Sem vínculo', 'semvinculo@teste.com', 'hash', TRUE, 3, 1),
    (120, 'Outro condomínio', 'outro@teste.com', 'hash', TRUE, 3, 2);

INSERT INTO tb_sindicos (id_sindico, usuario_id)
OVERRIDING SYSTEM VALUE
VALUES (1, 10);

INSERT INTO tb_condominios (
    id_condominio, nome_condominio,
    ativo, tipo_condominio_id, sindico_id, endereco_id
)
OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Condomínio Um', TRUE, 1, 1, 1),
    (2, 'Condomínio Dois', TRUE, 1, NULL, 2);

INSERT INTO tb_cooperativas (
    id_cooperativa, cnpj_cooperativa,
    nome_cooperativa, usuario_id
)
OVERRIDING SYSTEM VALUE
VALUES (1, '34238864000168', 'Cooperativa Um', 20);

INSERT INTO tb_moradores (condominio_id, usuario_id)
VALUES
    (1, 70),
    (1, 80),
    (1, 90),
    (1, 100),
    (1, 110),
    (2, 120);

INSERT INTO tb_rel_usuarios_condominios (
    usuario_id,
    condominio_id,
    aprovado,
    data_saida,
    nivel_confianca_id,
    trust_score,
    postagens_validadas_sem_contestacao,
    denuncias_realizadas,
    denuncias_procedentes
)
VALUES
    (70, 1, TRUE, NULL, 1, 0, 0, 0, 0),
    (80, 1, FALSE, NULL, 1, 0, 0, 0, 0),
    (90, 1, TRUE, CURRENT_TIMESTAMP, 1, 0, 0, 0, 0),
    (100, 1, TRUE, NULL, 1, 0, 0, 0, 0),
    (120, 2, TRUE, NULL, 1, 0, 0, 0, 0);