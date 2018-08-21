/**
 * Clase para conectar a un WebSocket. Deben pasarse las subcripciones en el constructor,
 * que es un mapa de parejas 'Url' - 'Callback', que se llamará cuando se reciba un mensaje.
 */
class SocketClient {
    constructor(endpoint, subcriptions, resolveConnection) {
        this.endpoint = endpoint;
        this.subcriptions = subcriptions;
        this.stompClient = Stomp.over(new SockJS(endpoint));
        /* Atención a la forma de pasar el onConnected() con lambda,
        sin él 'this' no sería el deseado dentro de ese método */
        this.stompClient.connect({}, () => this.onConnected(resolveConnection), this.onConnectionError);
    }
    onConnected(resolveConnection) {
        this.subcriptions.forEach((callback, url) => {
            this.stompClient.subscribe(url, function (payload) {
                callback(JSON.parse(payload.body));
            }, { id: url });
        });
        if (resolveConnection != null)
            resolveConnection();
    }
    onConnectionError(error) {
        console.log("Ha habido un error en la conexión: " + error);
    }
    sendMessage(url, data) {
        this.stompClient.send("/app/" + url, {}, JSON.stringify(data));
    }
    /**
     * Método para desconectar el cliente JS del WebSocket. No es necesario hacerlo,
     * al cerrar el navegador se acaba cerrando la conexión pero es útil para controlar
     * eventos de desconexión (y así llevar un registro de usarios activos, por ejemplo)
     */
    disconnect() {
        if (this.stompClient != null) {
            this.stompClient.disconnect(() => { });
        }
    }
}
export { SocketClient };
