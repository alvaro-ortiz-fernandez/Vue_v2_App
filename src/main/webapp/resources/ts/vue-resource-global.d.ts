import vue_resource from "vue-resource";

declare module 'vue/types/vue' {
	// Aquí meteríamos todas las propiedades de librerías externas:
	interface Vue {
		$http: {
            (options: vue_resource.HttpOptions): PromiseLike<vue_resource.HttpResponse>;
            get: vue_resource.$http;
            post: vue_resource.$http;
            put: vue_resource.$http;
            patch: vue_resource.$http;
            delete: vue_resource.$http;
            jsonp: vue_resource.$http;
        };
		$resource: vue_resource.$resource;
	}
}

declare global {
	const HttpResponse_: vue_resource.HttpResponse;
	type HttpResponse = typeof HttpResponse_;
}