package com.tallerwebi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // Para enviar mensajes del servidor al cliente tipo broadcast (a
        // todos los usuarios)
        registry.setApplicationDestinationPrefixes("/app"); //Para enviar mensajes del cliente al servidor

        //el controlador indicado para recibir los mensajes enviados por /app usara @MessageMapping("/nombre-canal")
        //que en el script para que se reciba se usara /app/nombre-canal

        //para que el servidor envie el mensaje al cliente se usara @SendTo(/topic/mensaje) en el mismo metodo del
        // controller que use @MessageMapping que luego se manejara con un script
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wschat");
    }

}