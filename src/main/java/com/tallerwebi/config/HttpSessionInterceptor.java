package com.tallerwebi.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class HttpSessionInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
            org.springframework.http.server.ServletServerHttpRequest servletRequest =
                    (org.springframework.http.server.ServletServerHttpRequest) request;

            // te trae el nombre de usuario por url para tests
            String nombreUsuario = servletRequest.getServletRequest().getParameter("usuario");

            // te trae lo que este en HttpSession
            if (nombreUsuario == null) {
                HttpSession session = servletRequest.getServletRequest().getSession(false);
                if (session != null) {
                    nombreUsuario = (String) session.getAttribute("usuario");
                }
            }
            if (nombreUsuario != null) {
                attributes.put("usuario", nombreUsuario);
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Nada
    }
}
