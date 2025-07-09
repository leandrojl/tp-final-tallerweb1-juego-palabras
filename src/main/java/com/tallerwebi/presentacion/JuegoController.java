package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.DTO.RondaDto;

import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.tallerwebi.dominio.DTO.JugadorPuntajeDto;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/juego")
public class JuegoController {

    private final RondaService rondaServicio;
    private final PuntajeService puntajeServicio;
    private final PartidaService partidaServicio;
    private final UsuarioPartidaService usuarioPartidaService;
    private final UsuarioService usuarioService;

    @Autowired
    public JuegoController(RondaService rondaServicio,
                           PuntajeService puntajeServicio,
                           PartidaService partidaServicio,
                           UsuarioPartidaService usuarioPartidaService,
                           UsuarioService usuarioService) {
        this.rondaServicio = rondaServicio;
        this.puntajeServicio = puntajeServicio;
        this.partidaServicio = partidaServicio;
        this.usuarioPartidaService = usuarioPartidaService;
        this.usuarioService = usuarioService;
    }
    @GetMapping
    public ModelAndView mostrarVistaJuego(HttpSession session) {

        Long idUsuario = (Long) session.getAttribute("idUsuario");
        String nombreUsuario = (String) session.getAttribute("usuario");

        Long idPartida = (Long) session.getAttribute("idPartida");
        System.out.println("Datos de sesión: idUsuario=" +
                idUsuario + ", nombreUsuario=" + nombreUsuario + ", idPartida=" + idPartida);

        if (idUsuario == null || nombreUsuario == null || idPartida == null) {
            return new ModelAndView("redirect:/login");
        }

        if (idPartida != null) {
            Partida partida = partidaServicio.obtenerPartidaPorId(idPartida);
        }

        ModelMap model = new ModelMap();
        model.put("idUsuario", idUsuario);
        model.put("usuario", nombreUsuario);

        RondaDto definicion = partidaServicio.iniciarNuevaRonda(idPartida);

        if (definicion == null) {
            System.out.println("No se pudo obtener la ronda actual para partidaId=" + idPartida);
            session.removeAttribute("idPartida");
            return new ModelAndView("redirect:/juego");
        }

        model.put("idPartida", idPartida);
        model.put("palabra", definicion.getPalabra());
        model.put("definicion", definicion.getDefinicionTexto());
        model.put("rondaActual", definicion.getNumeroDeRonda());

        int puntaje = definicion.getJugadores().stream()
                .filter(j -> j.getNombre().equals(nombreUsuario))
                .map(JugadorPuntajeDto::getPuntaje)
                .findFirst()
                .orElse(0);
        model.put("puntaje", puntaje);

        return new ModelAndView("juego", model);
    }


    @PostMapping("/abandonarPartida")
    @ResponseBody
    public ResponseEntity<String> abandonarPartida(@RequestParam Long usuarioId,
                                                   @RequestParam Long partidaId,
                                                   HttpSession session) {
        UsuarioPartida relacion = usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId);

        if (relacion != null) {
            usuarioPartidaService.marcarComoPerdedor(usuarioId, partidaId);

            session.removeAttribute("partidaID");

            return ResponseEntity.ok("OK");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No encontrado");
    }


}