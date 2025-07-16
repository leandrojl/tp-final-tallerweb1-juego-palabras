package com.tallerwebi.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.ServicioImplementacion.UsuarioPartidaServiceImpl;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    UsuarioPartidaService usuarioPartidaService;
  @Autowired
    public AutenticacionInterceptor(UsuarioPartidaService usuarioPartidaService) {
      this.usuarioPartidaService = usuarioPartidaService;


    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String contextPath = request.getContextPath(); // por ej. "/mi-app"
        String uri = request.getRequestURI();          // por ej. "/mi-app/registrar"
        String path = uri.substring(contextPath.length()); // por ej. "/registrar"

        // Excepciones: se permite sin login
        if (path.equals("/jugar-rapido") || path.equals("/login") || path.equals("/procesarLogin") || path.equals("/registro") || path.equals("/procesarRegistro") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/webjars/") || path.startsWith("/resources/")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        // Verificar si la sesión existe antes de acceder a sus atributos
        if (session != null) {
            Boolean estaJugando = (Boolean) session.getAttribute("jugando");

            if (Boolean.TRUE.equals(estaJugando) && path.equals("/juego")) {
                session.removeAttribute("jugando");
                usuarioPartidaService.marcarTodasLasPartidasComoFinalizadas((Long) session.getAttribute("idUsuario"), Estado.FINALIZADA);
                response.sendRedirect(contextPath + "/lobby");
                return false;
            }

            if (session.getAttribute("usuario") != null) {
                return true;
            }
        }

        // Si no hay sesión o no hay usuario en la sesión, redirigir a login
        response.sendRedirect(contextPath + "/login");
        return false;
    }
}