package com.vue.app.web;

import com.vue.app.repo.model.Message;
import com.vue.app.repo.model.User;
import com.vue.app.service.MessageService;
import com.vue.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private List<String> connections = new ArrayList<>();
    private Map<String, Boolean> writtingUsers = new HashMap();

    @RequestMapping(value = "/chat", method = RequestMethod.GET)
    public String chat(Principal principal, Model model) {

        List<User> contacts = userService.getContactsByUser(principal.getName()).stream()
                .map(user -> userService.toPublicUser(user))
            .collect(Collectors.toList());

        model.addAttribute("user", userService.toPublicUser(userService.findById(principal.getName()).get()));
        model.addAttribute("contacts", contacts);
        model.addAttribute("messages", messageService.findByFromOrTo(principal.getName()));

        return "chat/chat";
    }

    /* Cuando un usuario se conecta al chat enviamos a todos los usuarios
    el nombre para mostrar aviso de conexión y marcarlo como conectado */
    @MessageMapping("/conexion")
    public void conexion(SimpMessageHeaderAccessor accesor, Principal principal) {
        String username = principal.getName();

        if (!connections.contains(username))
            connections.add(username);

        sendMessage("/nueva-conexion", new ResponseEntity<>(username, HttpStatus.OK));
        // Aprovechamos esta petición para enviar todas las conexiones sólo al usuario que conecta
        sendMessage("/conexion", new ResponseEntity<>(connections, HttpStatus.OK), accesor);
        sendMessage("/escribiendo", new ResponseEntity<>(writtingUsers, HttpStatus.OK), accesor);

        userService.updateLastLogin(username);
        logger.info("El usuario {} se ha conectado al chat.", username);
    }

    // Para dejar de mostrar que el usuario está conectado
    @MessageMapping("/desconexion")
    @SendTo("/box/desconexion")
    public ResponseEntity<?> desconexion(Principal principal) {
        String username = principal.getName();
        connections.remove(username);
        writtingUsers.remove(principal.getName());

        logger.info("El usuario {} se ha desconectado del chat.", username);
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    // Controla si los usuarios están escribiendo o no
    @MessageMapping("/escribiendo")
    @SendTo("/box/escribiendo")
    public ResponseEntity<?> usuariosEscribiendo(@Payload Boolean escribiendo, Principal principal) {
        writtingUsers.put(principal.getName(), escribiendo);
        return new ResponseEntity<>(writtingUsers, HttpStatus.OK);
    }

    @MessageMapping("/nuevo-mensaje")
    @SendTo("/box/nuevo-mensaje")
    public Message nuevoMensaje(@Payload Message message, Principal principal) {

        message.setSender(principal.getName())
               .setTime(new Date());

        message = messageService.save(message);
        return message;
    }

    private void sendMessage(String url, ResponseEntity<?> response) {
        sendMessage(url, response, null);
    }
    private void sendMessage(String url, ResponseEntity<?> response, SimpMessageHeaderAccessor accesor) {
        // Si no se pasa el accesor se envia a todas las conexiones, si se pasa se envía al usuario que hizo la llamada
        if (accesor == null) {
            messagingTemplate.convertAndSend("/box" + url, response);
        } else {
            messagingTemplate.convertAndSendToUser(accesor.getSessionId(), "/queue" + url,
                    response, accesor.getMessageHeaders());
        }
    }
}