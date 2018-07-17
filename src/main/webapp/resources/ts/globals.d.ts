//////////////////  Declaraciones de ambiente de JavaScript, no relacionadas con ningún framework  //////////////////

// Objeto de error de ajax:
interface Error {
	status:number;
	data: {
		cause:string, message:string
	};
}