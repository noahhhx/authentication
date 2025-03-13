import {createRouter, createWebHistory} from "vue-router";

const routes = [
    {
        path: '',
        name: 'home',
        component: () => import('./components/Home.vue')
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('./components/Login.vue')
    }
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

export default router;