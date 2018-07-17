import { eventBus, Event, EventType } from "../components/eventBus.js";

/**
 * Componente que muestra un mensaje 'alert' de bootstrap. Para usarse no es necesario bindear
 * ningún atributo en su implementación en HTML, pero sí invocar su mixin alertBox en SASS,
 * para hacer que muestre un mensaje debe enviarse por el eventBus un evento de EventType.Alert.
 */
const alertBox = Vue.extend({
	template:  `<div class="ajaxMessage">
					<div class="alert" v-bind:class="[ cssClass, hidden ? 'closed' : '', firstTime ? 'hidden': '' ]">
						<button type="button" class="close" @click.prevent="hide">&times;</button>
						<span>{{ message }}</span>
					</div>
				</div>`,
	data() {
		return {
			hidden: true,
			firstTime: true,
			message: "",
			cssClass: ""
	  	}
	},
	created():void {
		/* Registramos el manejador de evento que muestra la
		alerta con el mensaje y la clase css correspondiente: */
		eventBus.subscribe(EventType.Alert, (event: Event) => {
			this.firstTime = false;
			this.hidden = false;

			this.message = event.getMessage();
			this.cssClass = "alert-" + event.getCssClass();
		});
	},
	methods: {
		// Llamado desde el botón de cerrar de la alerta
		hide():void {
			this.hidden = true;
			this.message = "";
		}
	}
});

/* Exportamos lo que queramos que se importe en otros módulos
(lo que no exportemos será 'privado' para este módulo): */
export {
	alertBox
}