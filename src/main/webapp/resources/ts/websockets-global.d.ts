import SockJS_ = require("sockjs-client");
import { Client, Message } from "stompjs";
import * as Stomp_ from "stompjs";

declare global {
	const SockJS: typeof SockJS_;
	type StompClient = Client;
	const Stomp: typeof Stomp_;
	type subscriptionCallback = (message: Message) => any;
}