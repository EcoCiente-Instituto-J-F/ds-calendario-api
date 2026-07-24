package br.com.ecociente.calendario.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.calendario.dataprovider.entity.StatusAgendamentoEntity;

public interface StatusAgendamentoRepository extends JpaRepository<StatusAgendamentoEntity, Integer> {
  StatusAgendamentoEntity findByNomeStatus(String name);
}
