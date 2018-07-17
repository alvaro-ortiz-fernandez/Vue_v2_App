import Vue from 'vue';
import * as vue_ from "vue";

declare module 'vue/types/vue' {
	// Aquí meteríamos todas las propiedades de librerías externas:
	interface Vue {
		
	}

	interface VueConstructor<V extends Vue = Vue> {
		// Aún no me convence mucho meter $options aquí y en ComponentOptions
		// pero es lo único que hace callar al corrector de TypeScript:
		$options?: any;
	}
}

// Para poder usar propiedades adicionales debemos aumentar la definición de la interfaz de ComponentOptions:
declare module 'vue/types/options' {
	interface ComponentOptions<V extends Vue> {
		$options?: ComponentOptions<Vue>;
		myOption?:string,
  	}
}

// Con esto no hace falta importar Vue en el resto de archivos para tener su definición:
declare global {
	const Vue: typeof vue_.default;
}