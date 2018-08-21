/**
 * EventBus para lanzar eventos, con los tipos de eventos permitidos y la plantilal de evento a lanzar.
 * Deben lanzarse eventos con la sintaxis:
 * 		eventBus.post(EventType.XX, new Event(message, BootstrapClass.XX));
 * Y escucharlos con:
 * 		eventBus.subscribe(EventType.Alert, (event:Event) => { // ...  });
 */
const eventBus = new Vue({
	methods: {
		/* Método para emitir un evento, se puede llamar directamente a $emit pero con esto
		podemos hacer la conversión del tipo de evento (con $emit hay que pasar el string): */
		post(eventType: EventType, event: Event):void {
			eventBus.$emit(EventType[eventType], event);
		},
		/* Igual que el anterior, podríamos usar directamente $on
		pero así hacemos directamente la conversión del Enum:*/
		subscribe(eventType: EventType, callback: Function) {
			eventBus.$on(EventType[eventType], (event: Event) => {
				callback(event);
			});
		}
	}
});

/**
 * Plantilla de evento a lanzar por el eventBus
 */
class Event {
	private readonly message: string; // Mensaje a transmitir
	private readonly status: boolean; // Mensaje a mostrar
	private readonly cssClass: string; // Clase CSS a aplicar en elementos bootstrap (danger, success...)

	constructor(status: boolean);
	constructor(message: string, cssClass: BootstrapClass);
	constructor(statusOrMessage: any, cssClass?: BootstrapClass) {
		if (typeof statusOrMessage === "string" && typeof cssClass !== "undefined") {
			this.message = statusOrMessage;
			this.cssClass = BootstrapClass[cssClass].toLowerCase();
			this.status = false;

		} else {
			this.status = statusOrMessage;
			this.message = "";
			this.cssClass = "";
		}
	}
	
	/* No tiene setters porque el evento
	sólo debe leerse una vez recibido */
	public getMessage():string {
		return this.message;
	}
	public getStatus():boolean {
		return this.status;
	}
	public getCssClass():string {
		return this.cssClass;
	}
}

/**
 * Tipos de evento que se puede lanzar por el eventBus
 */
enum EventType {
	Alert,
	Loading
}

/**
 * Clases bootstrap
 */
enum BootstrapClass {
	Primary,
	Secondary,
	Info,
	Success,
	Warning,
	Danger
}

export {
	eventBus,
	Event,
	EventType,
	BootstrapClass
}