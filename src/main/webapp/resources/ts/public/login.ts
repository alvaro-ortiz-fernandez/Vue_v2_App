let login = Vue.component("login", {
	template: "#login",
	data():object {
		return {
			currentTab: "login",
			loading: false,
			alert: {
				show: false,
				cssClass: 'alert-danger',
				message: 'Error interno del servidor. Por favor, vuelva a intentarlo.'
			},
			registerForm: {
				username: '',
				password: ''
			}
		}
	},
	methods: {
		login() {
			this.alert.show = false;
			this.loading = true;

			$.ajax({
				url: "login-processing",
				type: "POST",
				data: $(".login-form").serialize()
			}).done((message: string) => {
				this.$root.loading = true;
				setTimeout(() => { this.$root.$router.push('chat'); }, 500);
			}).fail((xhr, status, errorThrown) => {
				if (xhr.status === 401 && xhr.responseText == "null") {
					this.showAlert("El usuario y/o contraseña son incorrectos.", "alert-danger");
				} else {
					this.showAlert("Error interno del servidor: " + xhr.responseText, "alert-danger");
				}
			}).always((xhr, status) => {
				this.loading = false;
			});
		},
		register() {
			this.alert.show = false;
			this.loading = true;

			let data = JSON.stringify({
				username: this.registerForm.username,
				password: this.registerForm.password
			});

			$.ajax({
				url: "registro",
				type: "POST",
				data: data,
				headers: {
        			'Content-Type': 'application/json;charset=UTF-8',
					'Accept': 'application/json'
				}
			}).done((message: string) => {
				this.showAlert("Usuario registrado correctamente.", "alert-success");
			}).fail((xhr, status, errorThrown) => {
				if (xhr.status === 409) {
					this.showAlert("El usuario introducido ya existe.", "alert-danger");
				} else {
					this.showAlert("Error interno del servidor: " + errorThrown, "alert-danger");
				}
			}).always((xhr, status) => {
				this.loading = false;
			});
		},
		changeView(action: string) {
			this.currentTab = action;
			this.alert.show = false;
			$('form').animate({height: "toggle", opacity: "toggle"}, "slow");
		},
		showAlert(message: string, cssClass: string) {
			this.alert.message = message;
			this.alert.cssClass = cssClass;
			this.alert.show = true;
		}
	}
});

export {
	login
}