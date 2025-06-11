package com.tallerwebi.config;

import com.tallerwebi.dominio.model.Usuario;
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

            HttpSession httpSession = servletRequest.getServletRequest().getSession(false);
            if (httpSession != null) {
                Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
                if (usuario != null) {
                    attributes.put("usuario", usuario);
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}

