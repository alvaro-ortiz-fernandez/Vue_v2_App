import { SocketClient } from "../services/SocketClient.js";

contacts.forEach(function(contact: Contact) {
	contact.messages = [];
	messages.forEach(function(message: Message) {
		if (message.sender == contact.username
			|| message.receiver == contact.username) {

			contact.messages.push(message);
		}
	});
});

let chat = Vue.component("chat", {
	template: '#chat',
	data():object {
		return {
			drawerLeft: null,	  // Desplegable izquierdo
			drawerRight: false,	  // Desplegable derecho
			bottomNav: 0,		  // Pestaña del menú inferior del desplegable izquierdo
			snackbar: {		  	  // Mensaje de alerta superior derecho
				show: false,
				text: ""
			},
			nuevoMensaje: "",	  // Mensaje del input de escribir
			user: user,			  // Usuario logeado, para diferenciarlo de los contactos
			contacts: contacts,   // Contactos del desplegable izquierdo
			currentContact: null, // Controla el contacto del que mostrar el chat
			socketClient: null,		  // Cliente de WebSockets
			connectionPromise: null, // Promesa que es resuelta cuando se ha establecido conexión con el WebSocket
		}
	},
	computed: {
		filteredContacts() {
			// Clonamos el array para no alterar el original
			let filteredContacts: Array<Contact> = JSON.parse(JSON.stringify(this.contacts));

			// Ordenamos la lista de contactos por antiguedad de mensaje (cuanto más reciente mejor posición)
			filteredContacts.sort(function(a: Contact, b: Contact): number {
				return a.messages[a.messages.length-1].time < b.messages[b.messages.length-1].time ? 1 : -1;
			});
			
			return filteredContacts;
		},
		bottomNavColor() {	// Color del menú footer del desplegable izquierdo
			switch (this.bottomNav) {
				case 0: return "indigo"
				case 1: return "teal lighten-1"
			}
		}
	},
	mounted() {
		let subcriptions: Map<string, subscriptionCallback> = new Map<string, subscriptionCallback>();
		subcriptions.set("/user/queue/conexion", this.conexion);
		subcriptions.set("/user/queue/escribiendo", this.usuarioEscribiendo);
		subcriptions.set("/box/escribiendo", this.usuarioEscribiendo);
		subcriptions.set("/box/nueva-conexion", this.nuevaConexion);
		subcriptions.set("/box/desconexion", this.desconexion);
		subcriptions.set("/box/nuevo-mensaje", this.mensajeRecibido);

		// Hago toda esta parafernalia de promesas para controlar cuándo se ha establecido la conexión al servidor
		let resolveConnection: Function;
		this.connectionPromise = new Promise((resolve: Function, reject: Function) => { 
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
		conexion(response: any) {
			// TODO: Parar pantalla de carga aquí
			let usuariosConectados: Array<string> = response.body;
			usuariosConectados.forEach((usuario: string) => {
				this.setConnected(usuario, true, false);
			});
		},
		// Llamado por el servidor, cuando un nuevo usuario se conecta
		nuevaConexion(response: any) {
			this.setConnected(response.body, true, true);
		},
		// Llamado por el servidor, cuando un nuevo usuario se desconecta
		desconexion(response: Response) {
			this.setConnected(response.body, false);
		},
		setConnected(username: string, connected: boolean, showSnackbar?: boolean) {
			if (username != user.username) {
				contacts.forEach((contact: Contact, index: number) => {
					if (contact.username === username) {
						let newValue = contact;
						newValue.connected = connected;
						this.contacts.splice(index, 1, newValue);
						if (showSnackbar) this.showSnackbar("<strong>" + username + "</strong> se ha conectado.");
					}
				});
			}
		},
		// Avisa al servidor de si el usuario está escribiendo o deja de hacerlo
		escribiendo(escribiendo: boolean) {
			if (escribiendo) {
				this.socketClient.sendMessage("escribiendo", true);
			} else if (this.nuevoMensaje == "") {
				this.socketClient.sendMessage("escribiendo", false);
			}
		},
		// Controla si los usuarios están escribiendo o no
		usuarioEscribiendo(response: any) {
			for (const user in response.body) {
				this.setWritting(user, response.body[user]);
			}
		},
		setWritting(username: string, writting: boolean) {
			if (username != user.username) {
				contacts.forEach((contact: Contact, index: number) => {
					let newValue = contact;
					newValue.writting = writting;
					this.contacts.splice(index, 1, newValue);
				});
			}
		},
		// Accionado por el input del chat
		enviarMensaje(): void {
			let message = {
				message: this.nuevoMensaje,
				receiver: this.contacts[this.currentContact].username
			}
			this.socketClient.sendMessage("nuevo-mensaje", message);
			this.nuevoMensaje = "";
			$("#inputEnviar").blur();
		},
		// Llamado desde el WebSocket cuando alguien escribe un nuevo mensaje
		mensajeRecibido(mensaje: Message) {
			contacts.forEach(function(contact: Contact) {
				if (mensaje.sender == contact.username || mensaje.receiver == contact.username)
					contact.messages.push(mensaje);
			});

			this.messagesScroll();
		},
		setCurrentContact(username: string) {
			contacts.forEach((contact: Contact, index: number) => {
				if (contact.username === username) {
					this.currentContact = index;
				}
			});
		},
		unreadMessages(messages: Array<Message>): number {
			return messages.filter(function(message: Message) {
				return message.receiver == user.username;
			}).length;
		},
		showSnackbar(text: string) {
			this.snackbar.text = text;
			this.snackbar.show = true;
		},
		messagesScroll() {
			$("#messages").animate({ scrollTop: $('#messages').prop("scrollHeight")}, 1000);
		}
	},
	filters: {
		// Filtro para formatear la fecha en el bloque de contactos
		formatDate: function(value: string): string {
			let date = new Date(value), now = new Date();

			if (date.getFullYear() == now.getFullYear() && date.getMonth() == now.getMonth()
					&& (date.getDate() == now.getDate() || date.getDate() == now.getDate() - 1)) {

				if (date.getDate() == now.getDate() - 1) { 			// Ayer		
					return "Ayer";

				} else if (date.getHours() != now.getHours()) {		// Hace X horas
					let hours = now.getHours() - date.getHours();
					return "Hace " + hours + (hours == 1 ? " hora" : " horas");

				} else if (date.getMinutes() != now.getMinutes()) {	// Hace X minutos
					let minutes = now.getMinutes() - date.getMinutes();
					return "Hace " + minutes + (minutes == 1 ? " minuto" : " minutos");

				} else {											// Hace 1 minuto
					return "Hace menos de 1 minuto";
				}
			} else { 												// Fecha completa
				return date.toLocaleDateString();
			}
		},
		unreadMessages: function(messages: Array<Message>): number {
			return messages.filter(function(message: Message) {
				return message.receiver == user.username;
			}).length;
		}
	}
});

export {
	chat
}