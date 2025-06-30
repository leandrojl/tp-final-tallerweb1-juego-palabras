package com.tallerwebi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wschat")
                .addInterceptors(new HttpSessionInterceptor())            // Copia datos de la sesión HTTP (como el usuario) a la sesión WebSocket //Para que tu WebSocket sepa quién es el usuario que se conectó.
                .setHandshakeHandler(new CustomHandshakeHandler())       // Define el Principal (nombre de usuario) durante el handshake.	Para que puedas enviar mensajes "a un usuario" con convertAndSendToUser.
                .setAllowedOrigins("*")   // OJO: adaptar origenes permitidos
                .withSockJS();
    }
}