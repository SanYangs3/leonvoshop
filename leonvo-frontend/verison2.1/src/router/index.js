import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/search',
        name: 'search',
        component: () => import('../views/SearchView.vue')
    },
    {
        path: '/about',
        name: 'about',
        component: () => import('../views/AboutView.vue')
    },
    {
        path: '/product/:id',
        name: 'product',
        component: () => import('../components/ProductDetail.vue')
    },
    {
        path: '/cart',
        name: 'cart',
        component: () => import('../views/CartView.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/LoginView.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/UserRegister.vue')
    },
    // 新增短信注册路由
    {
        path: '/register/sms',
        name: 'sms-register',
        component: () => import('../views/SmsRegister.vue')
    },
    // 在routes数组中添加footer相关路由
    {
        path: '/footer/shopping-guide',
        name: 'shopping-guide',
        component: () => import('@/views/footer/ShoppingGuide.vue')
    },
    {
        path: '/footer/warranty-policy',
        name: 'warranty-policy',
        component: () => import('@/views/footer/WarrantyPolicy.vue')
    },
    {
        path: '/footer/about-company',
        name: 'about-company',
        component: () => import('@/views/footer/AboutCompany.vue')
    },
    {
        path: '/footer/contact-us',
        name: 'contact-us',
        component: () => import('@/views/footer/ContactUs.vue')
    },
    {
        path: '/footer/join-us',
        name: 'join-us',
        component: () => import('@/views/footer/JoinUs.vue')
    },
    {
        path: '/footer/website-policy',
        name: 'website-policy',
        component: () => import('@/views/footer/WebsitePolicy.vue')
    },
    {
        path: '/footer/user-agreement',
        name: 'user-agreement',
        component: () => import('@/views/footer/UserAgreement.vue')
    },
    {
        path: '/sitemap',
        name: 'sitemap',
        component: () => import('@/views/footer/SiteMap.vue')
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 导航守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

        if (!isLoggedIn) {
            alert('请先登录！');
            next('/login');
        } else {
            next();
        }
    } else {
        next();
    }
})

export default router