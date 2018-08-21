import { SocketClient } from "../services/SocketClient.js";
contacts.forEach(function (contact) {
    contact.messages = [];
    messages.forEach(function (message) {
        if (message.sender == contact.username
            || message.receiver == contact.username) {
            contact.messages.push(message);
        }
    });
});
let chat = Vue.component("chat", {
    template: '#chat',
    data() {
        return {
            drawerLeft: null,
            drawerRight: false,
            bottomNav: 0,
            snackbar: {
                show: false,
                text: ""
            },
            nuevoMensaje: "",
            user: user,
            contacts: contacts,
            currentContact: null,
            socketClient: null,
            connectionPromise: null,
        };
    },
    computed: {
        filteredContacts() {
            // Clonamos el array para no alterar el original
            let filteredContacts = JSON.parse(JSON.stringify(this.contacts));
            // Ordenamos la lista de contactos por antiguedad de mensaje (cuanto más reciente mejor posición)
            filteredContacts.sort(function (a, b) {
                return a.messages[a.messages.length - 1].time < b.messages[b.messages.length - 1].time ? 1 : -1;
            });
            return filteredContacts;
        },
        bottomNavColor() {
            switch (this.bottomNav) {
                case 0: return "indigo";
                case 1: return "teal lighten-1";
            }
        }
    },
    mounted() {
        let subcriptions = new Map();
        subcriptions.set("/user/queue/conexion", this.conexion);
        subcriptions.set("/user/queue/escribiendo", this.usuarioEscribiendo);
        subcriptions.set("/box/escribiendo", this.usuarioEscribiendo);
        subcriptions.set("/box/nueva-conexion", this.nuevaConexion);
        subcriptions.set("/box/desconexion", this.desconexion);
        subcriptions.set("/box/nuevo-mensaje", this.mensajeRecibido);
        // Hago toda esta parafernalia de promesas para controlar cuándo se ha establecido la conexión al servidor
        let resolveConnection;
        this.connectionPromise = new Promise((resolve, reject) => {
            resolveConnection = resolve;
            this.socketClient = new SocketClient("/app/chat", subcriptions, resolveConnection);
        });
        this.connectionPromise.then(() => {
            this.socketClient.sendMessage("conexion", {});
            // Mandamos aviso de desconexión al servidor al cerrar la página
            $(window).on("beforeunload", () => {
                this.socketClient.sendMessage("desconexion", {});
            });
        });
    },
    methods: {
        conexion(response) {
            // TODO: Parar pantalla de carga aquí
            let usuariosConectados = response.body;
            usuariosConectados.forEach((usuario) => {
                this.setConnected(usuario, true, false);
            });
        },
        // Llamado por el servidor, cuando un nuevo usuario se conecta
        nuevaConexion(response) {
            this.setConnected(response.body, true, true);
        },
        // Llamado por el servidor, cuando un nuevo usuario se desconecta
        desconexion(response) {
            this.setConnected(response.body, false);
        },
        setConnected(username, connected, showSnackbar) {
            if (username != user.username) {
                contacts.forEach((contact, index) => {
                    if (contact.username === username) {
                        let newValue = contact;
                        newValue.connected = connected;
                        this.contacts.splice(index, 1, newValue);
                        if (showSnackbar)
                            this.showSnackbar("<strong>" + username + "</strong> se ha conectado.");
                    }
                });
            }
        },
        // Avisa al servidor de si el usuario está escribiendo o deja de hacerlo
        escribiendo(escribiendo) {
            if (escribiendo) {
                this.socketClient.sendMessage("escribiendo", true);
            }
            else if (this.nuevoMensaje == "") {
                this.socketClient.sendMessage("escribiendo", false);
            }
        },
        // Controla si los usuarios están escribiendo o no
        usuarioEscribiendo(response) {
            for (const user in response.body) {
                this.setWritting(user, response.body[user]);
            }
        },
        setWritting(username, writting) {
            if (username != user.username) {
                contacts.forEach((contact, index) => {
                    let newValue = contact;
                    newValue.writting = writting;
                    this.contacts.splice(index, 1, newValue);
                });
            }
        },
        // Accionado por el input del chat
        enviarMensaje() {
            let message = {
                message: this.nuevoMensaje,
                receiver: this.contacts[this.currentContact].username
            };
            this.socketClient.sendMessage("nuevo-mensaje", message);
            this.nuevoMensaje = "";
            $("#inputEnviar").blur();
        },
        // Llamado desde el WebSocket cuando alguien escribe un nuevo mensaje
        mensajeRecibido(mensaje) {
            contacts.forEach(function (contact) {
                if (mensaje.sender == contact.username || mensaje.receiver == contact.username)
                    contact.messages.push(mensaje);
            });
            this.messagesScroll();
        },
        setCurrentContact(username) {
            contacts.forEach((contact, index) => {
                if (contact.username === username) {
                    this.currentContact = index;
                }
            });
        },
        unreadMessages(messages) {
            return messages.filter(function (message) {
                return message.receiver == user.username;
            }).length;
        },
        showSnackbar(text) {
            this.snackbar.text = text;
            this.snackbar.show = true;
        },
        messagesScroll() {
            $("#messages").animate({ scrollTop: $('#messages').prop("scrollHeight") }, 1000);
        }
    },
    filters: {
        // Filtro para formatear la fecha en el bloque de contactos
        formatDate: function (value) {
            let date = new Date(value), now = new Date();
            if (date.getFullYear() == now.getFullYear() && date.getMonth() == now.getMonth()
                && (date.getDate() == now.getDate() || date.getDate() == now.getDate() - 1)) {
                if (date.getDate() == now.getDate() - 1) {
                    return "Ayer";
                }
                else if (date.getHours() != now.getHours()) {
                    let hours = now.getHours() - date.getHours();
                    return "Hace " + hours + (hours == 1 ? " hora" : " horas");
                }
                else if (date.getMinutes() != now.getMinutes()) {
                    let minutes = now.getMinutes() - date.getMinutes();
                    return "Hace " + minutes + (minutes == 1 ? " minuto" : " minutos");
                }
                else {
                    return "Hace menos de 1 minuto";
                }
            }
            else {
                return date.toLocaleDateString();
            }
        },
        unreadMessages: function (messages) {
            return messages.filter(function (message) {
                return message.receiver == user.username;
            }).length;
        }
    }
});
export { chat };
