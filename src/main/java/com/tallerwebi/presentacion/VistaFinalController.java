package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.RankingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/juego")
public class VistaFinalController {

    @Autowired
    private PartidaService partidaService;

    @GetMapping("/vistaFinalJuego")
    public String mostrarVistaFinal(@RequestParam Long partidaId, Model model) {
        List<RankingDTO> ranking = partidaService.obtenerRanking(partidaId);

        String ganador = ranking.isEmpty()
                ? "Sin ganador"
                : ranking.get(0).getNombreUsuario();

        model.addAttribute("ranking", ranking);
        model.addAttribute("ganador", ganador);

        return "vistaFinalJuego"; // ← nombre del archivo HTML
    }
}
