package com.vue.app.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map attributes) {

        /* Si el principal es null significa que el cliente que se conecta al websocket no se
        ha autenticado en la aplicación, por lo que devolvemos false para rechazar su conexión */
        if (request.getPrincipal() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception ex) {

        // De momento no veo que hacer aquí, podría guardar el Principal de algún modo para
        // poder acceder a él desde el controlador luego.
    }
}