package com.tallerwebi.presentacion;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RankingControllerTest {

    @Test
    public void queSeDevuelvaLaVistaDeRanking(){
        RankingController rankingController = new RankingController();
        ModelAndView retorno = rankingController.verRanking();
        assertThat(retorno.getViewName(), equalTo("ranking"));
    }
}
