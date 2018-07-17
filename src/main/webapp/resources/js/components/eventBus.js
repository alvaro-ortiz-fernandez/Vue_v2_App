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
        post(eventType, event) {
            eventBus.$emit(EventType[eventType], event);
        },
        /* Igual que el anterior, podríamos usar directamente $on
        pero así hacemos directamente la conversión del Enum:*/
        subscribe(eventType, callback) {
            eventBus.$on(EventType[eventType], (event) => {
                callback(event);
            });
        }
    }
});
/**
 * Plantilla de evento a lanzar por el eventBus
 */
class Event {
    constructor(statusOrMessage, cssClass) {
        if (typeof statusOrMessage === "string" && typeof cssClass !== "undefined") {
            this.message = statusOrMessage;
            this.cssClass = BootstrapClass[cssClass].toLowerCase();
            this.status = false;
        }
        else {
            this.status = statusOrMessage;
            this.message = "";
            this.cssClass = "";
        }
    }
    /* No tiene setters porque el evento
    sólo debe leerse una vez recibido */
    getMessage() {
        return this.message;
    }
    getStatus() {
        return this.status;
    }
    getCssClass() {
        return this.cssClass;
    }
}
/**
 * Tipos de evento que se puede lanzar por el eventBus
 */
var EventType;
(function (EventType) {
    EventType[EventType["Alert"] = 0] = "Alert";
    EventType[EventType["Loading"] = 1] = "Loading";
})(EventType || (EventType = {}));
/**
 * Clases bootstrap
 */
var BootstrapClass;
(function (BootstrapClass) {
    BootstrapClass[BootstrapClass["Primary"] = 0] = "Primary";
    BootstrapClass[BootstrapClass["Secondary"] = 1] = "Secondary";
    BootstrapClass[BootstrapClass["Info"] = 2] = "Info";
    BootstrapClass[BootstrapClass["Success"] = 3] = "Success";
    BootstrapClass[BootstrapClass["Warning"] = 4] = "Warning";
    BootstrapClass[BootstrapClass["Danger"] = 5] = "Danger";
})(BootstrapClass || (BootstrapClass = {}));
export { eventBus, Event, EventType, BootstrapClass };
