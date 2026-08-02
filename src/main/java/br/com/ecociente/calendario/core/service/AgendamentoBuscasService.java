package br.com.ecociente.calendario.core.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecociente.calendario.core.domain.AgendamentoColeta;
import br.com.ecociente.calendario.core.domain.AgendamentoFiltro;
import br.com.ecociente.calendario.core.exception.PerfilNaoAutorizadoException;
import br.com.ecociente.calendario.core.gateway.AgendamentoColetaGateway;
import br.com.ecociente.calendario.core.usecase.BuscarProximaVisitaUseCase;
import br.com.ecociente.calendario.core.usecase.ListarAgendamentosUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoBuscasService implements ListarAgendamentosUseCase, BuscarProximaVisitaUseCase {

    private final AgendamentoColetaGateway agendamentoColetaGateway;

    @Override
    public Page<AgendamentoColeta> executar(
            Integer usuarioId,
            String perfil,
            AgendamentoFiltro filtro,
            Pageable pageable) {

        if ("SINDICO".equalsIgnoreCase(perfil)) {
            return agendamentoColetaGateway.buscarPorSindico(usuarioId,filtro, pageable);
        }

        if ("COOPERATIVA".equalsIgnoreCase(perfil)) {
            return agendamentoColetaGateway.buscarPorCooperativa(usuarioId, filtro, pageable);
        }

        throw new PerfilNaoAutorizadoException(perfil);
    }

    @Override
    public Optional<AgendamentoColeta> executar(Integer usuarioId, AgendamentoFiltro filtro, String perfil) {
        if ("SINDICO".equalsIgnoreCase(perfil)) {
            return agendamentoColetaGateway.buscarProximoAgendamentoPorSindico(usuarioId,filtro);
        }
        if ("COOPERATIVA".equalsIgnoreCase(perfil)) {
            return agendamentoColetaGateway.buscarProximoAgendamentoPorCooperativa(usuarioId,filtro);
        }
        throw new PerfilNaoAutorizadoException(perfil);
    }
}
