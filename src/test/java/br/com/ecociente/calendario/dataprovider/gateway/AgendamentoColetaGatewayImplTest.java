package br.com.ecociente.calendario.dataprovider.gateway;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ecociente.calendario.core.mapper.AgendamentoColetaMapper;
import br.com.ecociente.calendario.dataprovider.repository.AgendamentoColetaRepository;

@ExtendWith(MockitoExtension.class)
public class AgendamentoColetaGatewayImplTest {

  @Mock
  private AgendamentoColetaRepository agendamentoColetaRepository;

  @Mock
  private AgendamentoColetaMapper agendamentoColetaMapper;

  @InjectMocks
  private AgendamentoColetaGatewayImpl agendamentoColetaGateway;
  

}
