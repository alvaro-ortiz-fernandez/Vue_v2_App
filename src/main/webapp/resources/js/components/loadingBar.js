import { eventBus, EventType } from "../components/eventBus.js";
/**
 * Componente para mostrar el spin de carga. La implementación HTML no requiere bindeos,
 * para activar o desactivar el spin debe enviarse un evento de EventType.Loading por el
 * eventBus con el status [ true | false ] para activar o desactivarla, despectivamente.
 */
export const loadingBar = Vue.extend({
    template: `<div class="loading-bar" v-bind:class="[ loading ? '' : 'hidden', endAnimation ? 'end' : '' ]">
					<div class="loading-content">
						<div v-for="index in 8" v-bind:class="'bar' + index">
							<div class="inner-bar"></div>
						</div>
					</div>
				</div>`,
    data() {
        return {
            loading: false,
            endAnimation: false
        };
    },
    created() {
        /* Registramos el manejador de evento que muestra la
        alerta con el mensaje y la clase css correspondiente: */
        eventBus.subscribe(EventType.Loading, (event) => {
            if (event.getStatus() == true) {
                this.loading = true;
                this.endAnimation = false;
            }
            else {
                this.endAnimation = true;
                // No me gusta este apaño pero no he encontrado nada mejor
                let that = this;
                setTimeout(function () {
                    that.loading = false;
                }, 500);
            }
        });
    }
});
