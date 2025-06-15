package com.tallerwebi.dominio;


import com.tallerwebi.infraestructura.UsuarioPartidaRepositoryImpl;
import com.tallerwebi.infraestructura.UsuarioRepositoryImpl;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class RankingServiceTest {


    @InjectMocks
    private RankingServiceImpl rankingService;
    @Mock
    private UsuarioPartidaRepositoryImpl usuarioPartidaRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

}

@Test
@Transactional
@Rollback
public void consultarRankingTest() {
     givenPartidasJugadas();
    List<Object[]> partidas= new ArrayList<>();
    partidas.add(new Object[] { "Juan", 3 });
    partidas.add(new Object[] { "Maria", 5 });
    Mockito.when(usuarioPartidaRepository.obtenerRanking()).thenReturn(partidas);
    List<Object[]> ranking =whenConsultoPorElRanking();
    thenObtengoElRanking(ranking);
    }

    private void thenObtengoElRanking(List<Object[]> ranking) {
    assertThat(ranking.get(0)[0], equalTo("Juan"));
        assertThat(ranking.get(0)[1], equalTo(3));
}

    private List<Object[]> whenConsultoPorElRanking() {
    return rankingService.obtenerRanking();
    }

    private void givenPartidasJugadas() {

    }
}
