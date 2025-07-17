package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.RankingJugadorDTO;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class RankingController {

    private final UsuarioPartidaService usuarioPartidaService;

    @Autowired
    public RankingController(UsuarioPartidaService usuarioPartidaService) {
        this.usuarioPartidaService = usuarioPartidaService;
    }

    @GetMapping("/ranking-global")
    public String mostrarRankingGlobal(Model model) {

        List<RankingJugadorDTO> ranking = usuarioPartidaService.obtenerRankingGlobal();
        model.addAttribute("ranking", ranking);
        return "ranking-global";
    }


}
