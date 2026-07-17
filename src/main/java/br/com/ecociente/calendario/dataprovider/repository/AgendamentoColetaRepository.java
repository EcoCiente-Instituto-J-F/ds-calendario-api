package br.com.ecociente.calendario.dataprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecociente.calendario.dataprovider.entity.AgendamentoColetaEntity;

public interface AgendamentoColetaRepository extends JpaRepository<AgendamentoColetaEntity, Integer> {
  
}
