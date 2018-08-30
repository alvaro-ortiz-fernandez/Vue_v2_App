package com.vue.app.web;

import com.vue.app.chat.ChatSession;
import com.vue.app.repo.model.Group;
import com.vue.app.repo.model.Message;
import com.vue.app.repo.model.User;
import com.vue.app.service.MessageService;
import com.vue.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.*;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Map<String, String> sessions = new HashMap<>();
    private List<String> connections = new ArrayList<>();
    private Map<String, Boolean> writtingUsers = new HashMap();

    /* Cuando un usuario se conecta al chat enviamos a todos los usuarios
    el nombre para mostrar aviso de conexión y marcarlo como conectado */
    @MessageMapping("/conexion")
    public void conexion(SimpMessageHeaderAccessor accesor, Principal principal) {
        String username = principal.getName();
        sessions.put(username, accesor.getSessionId());

        if (!connections.contains(username))
            connections.add(username);

        // Enviamos al usuario todos los datos necesarios a la conexión:
        ChatSession sesion = userService.buildChatSession(username, connections, writtingUsers);
        sendMessage("/conexion", new ResponseEntity<>(sesion, HttpStatus.OK), accesor);

        // Avisamos a todos los usuarios que se ha conectado uno nuevo
        sendMessage("/nueva-conexion", new ResponseEntity<>(username, HttpStatus.OK));

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

    @RequestMapping(value = "/contactos/nuevo", method = RequestMethod.POST,
    consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<?> nuevoContacto(@RequestBody String contactName, @AuthenticationPrincipal Principal principal) {
        try {
            if (contactName == principal.getName()) {
                return new ResponseEntity<>(new Error("No puedes agregatre a tí mismo."), HttpStatus.CONFLICT);
            }
            if (userService.contactExists(principal.getName(), contactName)) {
                return new ResponseEntity<>(new Error("El contacto ya se encuentra en tu lista de contactos."), HttpStatus.CONFLICT);
            }

            Optional<User> userOpt = userService.saveContact(principal.getName(), contactName);

            if (userOpt.isPresent()) {
                // Guardamos ahora para el usuario que ha agregado el usuario que ha realizado la petición
                userService.saveContact(contactName, principal.getName());

                return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new Error("El usuario introducido no existe"), HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @MessageMapping("/mensajes/nuevo")
    @SendTo("/box/mensajes/nuevo")
    public Message nuevoMensaje(@Payload Message message, Principal principal) {

        message.setSender(principal.getName())
                .setTime(new Date());

        message = messageService.save(message);
        return message;
    }

    @MessageMapping("/mensajes/leidos")
    public void mensajesLeidos(@Payload Message[] messages, SimpMessageHeaderAccessor accesor, Principal principal) {

        List<Message> updatedMessages = messageService.markReaded(Arrays.asList(messages), principal.getName());
        sendMessage("/mensajes/leidos", new ResponseEntity<>(updatedMessages, HttpStatus.OK), accesor);
    }

    @MessageMapping("/grupos/nuevo")
    public void nuevoGrupo(@Payload String name, SimpMessageHeaderAccessor accesor, Principal principal) {

        Group group = new Group(name, new HashSet<>());
        group = messageService.createGroup(group);
        group = messageService.registerGroupUser(group, principal.getName());

        sendMessage("/grupos/nuevo", new ResponseEntity<>(group, HttpStatus.OK));
    }

    @MessageMapping("/grupos/agregar")
    public void agregarAGrupo(@Payload String username, SimpMessageHeaderAccessor accesor, Principal principal) {



        sendMessage("/grupos/agregar", new ResponseEntity<>(true, HttpStatus.OK));
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

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}