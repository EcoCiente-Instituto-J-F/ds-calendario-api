package br.com.ecociente.calendario.core.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.domain.Perfil;
import br.com.ecociente.calendario.core.usecase.ConsultaAgendamentoPorPerfil;
import br.com.ecociente.calendario.core.usecase.ListarAgendamentosUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarAgendamentoService implements ListarAgendamentosUseCase {

  private final ResolvedorConsultaAgendamento resolvedor;

    @Override
    public Page<AgendamentoColeta> executar(
            Integer usuarioId,
            Perfil perfil,
            AgendamentoFiltro filtro,
            Pageable pageable) {

        ConsultaAgendamentoPorPerfil consulta = resolvedor.resolver(perfil);
        return consulta.listar(usuarioId, filtro, pageable);
    }
  
}
