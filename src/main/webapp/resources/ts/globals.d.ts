//////////////////  Declaraciones de ambiente de JavaScript, no relacionadas con ningún framework  //////////////////
import Vue from 'vue/types/index';

// Objeto de error de ajax
interface Error {
	status:number;
	data: {
		cause:string,
		message:string
	};
}

// Chat.ts
declare global {
	const path: string;
	const authenticated: boolean;
	const contacts: Array<Contact>;
	const messages: Array<Message>;
	let vm: any;

	type ChatSession = {
		user: User,
		contacts: Array<Contact>,
		messages: Array<Message>,
		connections: Array<string>,
		writtingUsers: Object
	}

	interface User {
		username: string;
		avatar: string;
		lastLogin: Date;
	}
	
	interface Contact {
		username: string;
		avatar: string;
		connected: boolean;
		writting: boolean;
		messages: Array<Message>
	}
	
	interface Message {
		id: number;
		message: string;
		sender: string;
		receiver: string;
		time: Date;
		readed: boolean;
	}
}