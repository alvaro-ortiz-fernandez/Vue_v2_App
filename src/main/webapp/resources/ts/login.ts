import { eventBus, Event, EventType, BootstrapClass } from "./components/eventBus.js";
import { loadingBar } from "./components/loadingBar.js";
import { alertBox } from "./components/alertBox.js";

Vue.component("login-form", {
	props: ["tab", "toggle"],
	template: "#login-form",
	data():object {
		return {
			formPath: "login-processing"
	  	}
	},
	methods: {
		submitForm(event: Event):void {
			eventBus.post(EventType.Loading, new Event(true));

			/* No me gusta hacerlo con el ajax de jQuery, pero
			no encuentro cómo hacer que funcione con spring sec */
			$.ajax({
				url: "login-processing",
				type: "POST",
				data: $('#form').serialize(),
			}).done(function(message: string) {
				eventBus.post(EventType.Alert, new Event(message, BootstrapClass.Success));
				setTimeout(function(){
					window.location.pathname = "/app";
				}, 2000);
			}).fail(function(xhr, status, errorThrown) {
				eventBus.post(EventType.Alert, new Event("Error", BootstrapClass.Danger));
			}).always(function(xhr, status) {
				eventBus.post(EventType.Loading, new Event(false));
			});
		}
	}
});

Vue.component("registro-form", {
	props: ["tab", "toggle"],
	template: "#login-form",
	data():object {
		return {
			formPath: "registro"
	  	}
	},
	methods: {
		submitForm(event: Event):void {
			eventBus.post(EventType.Loading, new Event(true));

			let loginForm = $(document.forms[0]);
			let user = JSON.stringify({
				username: loginForm.find('input[name="username"]').val(),
				password: loginForm.find('input[name="password"]').val()
			});
			
			this.$http.post("registro", user, {
				headers: {
					'X-CSRF-Token': $("meta[name='_csrf']").attr("content"),
					'Accept': 'application/json',
        			'Content-Type': 'application/json;charset=UTF-8'
				}
			/* Atención a la expresión lambda, gracias a ella no se pierde el contexto
			de 'this' (para JavaScript no es necesario pero sí para TypeScript): */
			}).then((response: HttpResponse) => {
				eventBus.post(EventType.Alert, new Event("Usuario logeado correctamente", BootstrapClass.Success));
			}, (error: Error) => {
				eventBus.post(EventType.Alert, new Event(error.data.message, BootstrapClass.Danger));
			}).then(() => {
				eventBus.post(EventType.Loading, new Event(false));
			});
		}
	}
});

new Vue({
	el: "#vue-root",
	components: {
		'alert-box': alertBox,
		'loading-bar': loadingBar
	},
	data: {
		currentTab: "login"
	},
	created():void {
		let url:URL = new URL(window.location.href);
		let view:string|null = url.searchParams.get("view");
		if (view != null && (view == "login" || view == "registro")) {
			this.currentTab = view;
		}
	},
	computed: {
		currentTabComponent():string {
			return this.currentTab.toLowerCase() + "-form";
		},
		currentClass():string {
			return "bloque-" + this.currentTab.toLowerCase();
		}
	},
	methods: {
		rotate():void {
			if (this.currentTab == "login") {
				this.currentTab = "registro";
			}  else {
				this.currentTab = "login";
			} 
		}
	}
});