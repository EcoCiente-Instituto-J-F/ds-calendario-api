package br.com.ecociente.calendario.dataprovider.repository;

import java.time.LocalDateTime;
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
    INNER JOIN status_agendamentos sa
        ON sa.id_status = ac.status_agendamento_id        
    WHERE c.sindico_usuario_id = :usuarioId
        AND (:status IS NULL OR sa.nome_status = :status)
        AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
        AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
        AND (:cooperativaId IS NULL OR ac.cooperativa_id = :cooperativaId)
        AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM agendamentos_coletas ac
    INNER JOIN condominios c
     ON c.id_condominio = ac.condominio_id
    INNER JOIN status_agendamentos sa
        ON sa.id_status = ac.status_agendamento_id
    WHERE c.sindico_usuario_id = :usuarioId
        AND (:status IS NULL OR sa.nome_status = :status)
        AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
        AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
        AND (:cooperativaId IS NULL OR ac.cooperativa_id = :cooperativaId)
        AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)
    """,
    nativeQuery = true)
    Page<AgendamentoColetaEntity> buscarPorSindico(
      @Param("usuarioId") Integer usuarioId,
      @Param("status") String status,
      @Param("dataInicio") LocalDateTime dataInicio,
      @Param("dataFim") LocalDateTime dataFim,
      @Param("cooperativaId") Integer cooperativaId,
      @Param("possuiRecorrencia") Boolean possuiRecorrencia,
      Pageable pageable);


 @Query(value = """
     SELECT ac.*
      FROM agendamentos_coletas ac
      INNER JOIN cooperativas co
          ON co.id_cooperativa = ac.cooperativa_id
      INNER JOIN status_agendamentos sa
          ON sa.id_status = ac.status_agendamento_id    
      WHERE co.usuario_id = :usuarioId
          AND (:status IS NULL OR sa.nome_status = :status)
          AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
          AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
          AND (:condominioId IS NULL OR ac.condominio_id = :condominioId)
          AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)    
      """,
      countQuery = """
      SELECT COUNT(*)
      FROM agendamentos_coletas ac
      INNER JOIN cooperativas co
          ON co.id_cooperativa = ac.cooperativa_id
      INNER JOIN status_agendamentos sa
          ON sa.id_status = ac.status_agendamento_id        
      WHERE co.usuario_id = :usuarioId
          AND (:status IS NULL OR sa.nome_status = :status)
          AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
          AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
          AND (:condominioId IS NULL OR ac.condominio_id = :condominioId)
          AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)
      """,
      nativeQuery = true)
      Page<AgendamentoColetaEntity> buscarPorCooperativa(
        @Param("usuarioId") Integer usuarioId,
        @Param("status") String status,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim,
        @Param("condominioId") Integer condominioId,
        @Param("possuiRecorrencia") Boolean possuiRecorrencia,
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
        AND sa.nome_status <> 'REALIZADO'
        AND ac.data_inicio > NOW()
        AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)
        AND (:cooperativaId IS NULL OR ac.cooperativa_id = :cooperativaId)
        AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
        AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
    ORDER BY ac.data_inicio
    LIMIT 1
    """,
    nativeQuery = true)
    Optional<AgendamentoColetaEntity> buscarProximoAgendamentoPorSindico(
    @Param("usuarioId") Integer usuarioId, 
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    @Param("possuiRecorrencia") Boolean possuiRecorrencia, 
    @Param("cooperativaId") Integer cooperativaId);

  @Query(value = """
    SELECT ac.*
    FROM agendamentos_coletas ac
    INNER JOIN cooperativas co
        ON co.id_cooperativa = ac.cooperativa_id
    INNER JOIN status_agendamentos sa
        ON sa.id_status = ac.status_agendamento_id    
    WHERE co.usuario_id = :usuarioId
        AND sa.nome_status <> 'CANCELADO'
        AND sa.nome_status <> 'REALIZADO'
        AND ac.data_inicio > NOW()
        AND (:possuiRecorrencia IS NULL OR ac.possui_recorrencia = :possuiRecorrencia)
        AND (:cooperativaId IS NULL OR ac.condominio_id = :condominioId)
        AND (:dataInicio IS NULL OR ac.data_inicio >= :dataInicio)
        AND (:dataFim IS NULL OR ac.data_fim <= :dataFim)
    ORDER BY ac.data_inicio
    LIMIT 1
    """,
    nativeQuery = true)
    Optional<AgendamentoColetaEntity> buscarProximoAgendamentoPorCooperativa(
    @Param("usuarioId") Integer usuarioId,
    @Param("dataInicio") LocalDateTime dataInicio,
    @Param("dataFim") LocalDateTime dataFim,
    @Param("possuiRecorrencia") Boolean possuiRecorrencia,
    @Param("condominioId") Integer condominioId
  );  



}