import { login } from "../public/login.js";
import { chat } from "../public/chat.js";
vm = new Vue({
    el: "#app",
    router: new VueRouter({ routes: [
            { path: '/login', component: login, alias: '' },
            { path: '/chat', component: chat }
        ] }),
    data: {
        path: path,
        user: user
    },
    computed: {
        authenticated() {
            return this.user != null ? true : false;
        }
    },
    mounted() {
        this.$router.push(this.authenticated ? 'chat' : 'login');
    }
});
