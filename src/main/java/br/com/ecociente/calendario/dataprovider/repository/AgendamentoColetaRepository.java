package br.com.ecociente.calendario.dataprovider.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;

public interface AgendamentoColetaRepository extends JpaRepository<AgendamentoColetaEntity, Integer> {

  @Query(value = """
    SELECT ac.*
    FROM agendamentos_coletas ac
    INNER JOIN condominios c 
        ON c.id_condominio = ac.condominio_id
    WHERE c.sindico_usuario_id = :usuarioId
    ORDER BY ac.data_inicio
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM agendamentos_coletas ac
    INNER JOIN condominios c
     ON c.id_condominio = ac.condominio_id
    WHERE c.sindico_usuario_id = :usuarioId
    """,
    nativeQuery = true)
    Page<AgendamentoColetaEntity> buscarPorSindico(
      @Param("usuarioId") Integer usuarioId,
      Pageable pageable);


 @Query(value = """
     SELECT ac.*
      FROM agendamentos_coletas ac
      INNER JOIN cooperativas co
          ON co.id_cooperativa = ac.cooperativa_id
      WHERE co.usuario_id = :usuarioId
      ORDER BY ac.data_inicio
      """,
      countQuery = """
      SELECT COUNT(*)
      FROM agendamentos_coletas ac
      INNER JOIN cooperativas co
          ON co.id_cooperativa = ac.cooperativa_id
      WHERE co.usuario_id = :usuarioId
      """,
      nativeQuery = true)
      Page<AgendamentoColetaEntity> buscarPorCooperativa(
        @Param("usuarioId") Integer usuarioId,
        Pageable pageable);

  @Query(value = """
    SELECT ac.*
    FROM agendamentos_coletas ac
    INNER JOIN condominios c 
        ON c.id_condominio = ac.condominio_id
    INNER JOIN status_agendamentos sa
        ON sa.id_status = ac.status_agendamento_id    
    WHERE c.sindico_usuario_id = :usuarioId
        AND sa.nome_status <> 'CANCELADO'
        AND sa.nome_status <> 'FINALIZADO'
        AND ac.data_inicio > NOW()
    ORDER BY ac.data_inicio
    LIMIT 1
    """,
    nativeQuery = true)
    Optional<AgendamentoColetaEntity> buscarProximoAgendamentoPorSindico(@Param("usuarioId") Integer usuarioId);

  @Query(value = """
    SELECT ac.*
    FROM agendamentos_coletas ac
    INNER JOIN cooperativas co
        ON co.id_cooperativa = ac.cooperativa_id
    INNER JOIN status_agendamentos sa
        ON sa.id_status = ac.status_agendamento_id    
    WHERE co.usuario_id = :usuarioId
        AND sa.nome_status <> 'CANCELADO'
        AND sa.nome_status <> 'FINALIZADO'
        AND ac.data_inicio > NOW()
    ORDER BY ac.data_inicio
    LIMIT 1
    """,
    nativeQuery = true)
    Optional<AgendamentoColetaEntity> buscarProximoAgendamentoPorCooperativa(@Param("usuarioId") Integer usuarioId);  



}