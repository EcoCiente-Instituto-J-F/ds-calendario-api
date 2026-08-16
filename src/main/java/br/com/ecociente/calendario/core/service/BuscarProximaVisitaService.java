package br.com.ecociente.calendario.core.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.usecase.BuscarProximaVisitaUseCase;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BuscarProximaVisitaService implements BuscarProximaVisitaUseCase {

   private final ResolvedorConsultaAgendamento resolvedor;

   @Override
    public Optional<AgendamentoColeta> executar(Integer usuarioId, AgendamentoFiltro filtro, Perfil perfil) {

      ConsultaAgendamentoPorPerfil consulta = resolvedor.resolver(perfil);
      return consulta.buscarProximo(usuarioId, filtro);
    }
  
}
