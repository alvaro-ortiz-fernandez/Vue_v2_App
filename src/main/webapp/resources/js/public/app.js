import { login } from "../public/login.js";
import { chat } from "../public/chat.js";
Vue.http.headers.common['Content-Type'] = 'application/json;charset=UTF-8';
Vue.http.headers.common['Accept'] = 'application/json';
vm = new Vue({
    el: "#app",
    router: new VueRouter({ routes: [
            { path: '/login', component: login, alias: '' },
            { path: '/chat', component: chat }
        ] }),
    data: {
        path: path,
        authenticated: authenticated,
        loading: false
    },
    created() {
        this.authenticated ? this.loading = true : '';
        this.$router.push(this.authenticated ? 'chat' : 'login');
    }
});
