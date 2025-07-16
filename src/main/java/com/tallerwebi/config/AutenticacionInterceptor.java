package com.tallerwebi.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

public class AutenticacionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String contextPath = request.getContextPath(); // por ej. "/mi-app"
        String uri = request.getRequestURI();          // por ej. "/mi-app/registrar"
        String path = uri.substring(contextPath.length()); // por ej. "/registrar"

        // Excepciones: se permite sin login
        if (path.equals("/login") || path.equals("/procesarLogin") || path.equals("/registrar") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/webjars/") || path.startsWith("/resources/")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            return true;
        }

        response.sendRedirect(contextPath + "/login");
        return false;
    }
}