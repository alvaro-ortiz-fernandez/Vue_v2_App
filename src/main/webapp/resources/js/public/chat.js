import { SocketClient } from "../services/SocketClient.js";
let chat = Vue.component("chat", {
    template: '#chat',
    data() {
        return {
            drawerLeft: null,
            drawerRight: false,
            bottomNav: 0,
            dialogContact: {
                show: false,
                contact: '',
                loading: false,
                alert: {
                    type: '',
                    message: '',
                    show: false
                }
            },
            dialogGroup: {
                show: false,
                group: '',
                loading: false
            },
            snackbar: {
                show: false,
                text: ""
            },
            nuevoMensaje: "",
            user: {},
            contacts: [],
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
                if (b.messages.length === 0) {
                    return -1;
                }
                else if (a.messages.length === 0) {
                    return 1;
                }
                else {
                    return a.messages[a.messages.length - 1].time < b.messages[b.messages.length - 1].time ? 1 : -1;
                }
            });
            return filteredContacts;
        },
        notifications() {
            let messages = [];
            this.contacts.forEach((contact) => {
                messages = messages.concat(contact.messages);
            });
            messages = messages.filter(msj => msj.receiver == this.user.username && msj.readed == false);
            return messages.length;
        },
        bottomNavColor() {
            switch (this.bottomNav) {
                case 0: return "deep-purple accent-4";
                case 1: return "blue accent-3";
            }
        }
    },
    created() {
        let subcriptions = new Map();
        subcriptions.set("/user/queue/conexion", this.conexion);
        subcriptions.set("/box/escribiendo", this.usuarioEscribiendo);
        subcriptions.set("/box/nueva-conexion", this.nuevaConexion);
        subcriptions.set("/box/desconexion", this.desconexion);
        subcriptions.set("/box/mensajes/nuevo", this.mensajeRecibido);
        subcriptions.set("/user/queue/mensajes/leidos", this.mensajesLeidos);
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
            let sesion = response.body;
            this.user = sesion.user;
            sesion.contacts.forEach(function (contact) {
                contact.messages = [];
                sesion.messages.forEach(function (message) {
                    if (message.sender == contact.username
                        || message.receiver == contact.username) {
                        contact.messages.push(message);
                    }
                });
            });
            this.contacts = sesion.contacts;
            sesion.connections.forEach((usuario) => {
                this.setConnected(usuario, true, false);
            });
            for (const user in sesion.writtingUsers) {
                this.setWritting(user, sesion.writtingUsers[user]);
            }
            setTimeout(() => { this.$root.loading = false; }, 500);
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
            if (username != this.user.username) {
                this.contacts.forEach((contact, index) => {
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
        // Llamado por el modal de crear contacto
        crearContacto() {
            this.dialogContact.alert.show = false;
            this.dialogContact.loading = true;
            this.$http.post(path + "contactos/nuevo", this.dialogContact.contact).then((response) => {
                let contact = response.body;
                contact.messages = [];
                this.contacts.push(contact);
                this.dialogContact.alert.type = "success";
                this.dialogContact.alert.message = "Contacto agregado correctamente.";
                this.dialogContact.alert.show = true;
            }, (error) => {
                this.dialogContact.alert.type = 'error';
                if (error.status === 409) {
                    this.dialogContact.alert.message = error.body.message;
                }
                else if (error.status === 0) {
                    this.dialogContact.alert.message = "El servidor no está disponible en estos momentos.";
                }
                else {
                    this.dialogContact.alert.message = "Error interno del servidor: " + error.statusText;
                }
                this.dialogContact.alert.show = true;
            }).then(() => { this.dialogContact.loading = false; });
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
            if (username != this.user.username) {
                this.contacts.forEach((contact, index) => {
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
            this.socketClient.sendMessage("mensajes/nuevo", message);
            this.nuevoMensaje = "";
            $("#inputEnviar").blur();
        },
        // Llamado desde el WebSocket cuando alguien escribe un nuevo mensaje
        mensajeRecibido(mensaje) {
            this.contacts.forEach(function (contact) {
                if (mensaje.sender == contact.username || mensaje.receiver == contact.username)
                    contact.messages.push(mensaje);
            });
            this.messagesScroll();
        },
        setCurrentContact(username) {
            this.contacts.forEach((contact, index) => {
                if (contact.username === username) {
                    this.currentContact = index;
                    this.markReaded(contact.messages);
                }
            });
        },
        markReaded(messages) {
            let readedMessages = messages.filter(message => message.readed == false && message.receiver == this.user.username);
            if (readedMessages.length > 0)
                this.socketClient.sendMessage("mensajes/leidos", readedMessages);
        },
        mensajesLeidos(response) {
            let messages = response.body;
            this.contacts.find((contact) => {
                return contact.username == messages[0].sender;
            }).messages.forEach((message, index) => {
                if (message.readed == false && messages.some((msj) => { return msj.id === message.id; })) {
                    message.readed = true;
                }
            });
        },
        unreadMessages(messages) {
            return messages.filter((message) => {
                return message.receiver == this.user.username && message.readed == false;
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
        unreadMessages: function (messages, username) {
            return messages.filter((message) => {
                return message.receiver == username && message.readed == false;
            }).length;
        }
    }
});
export { chat };
