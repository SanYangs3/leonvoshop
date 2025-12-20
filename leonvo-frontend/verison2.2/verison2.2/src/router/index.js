import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/user/HomeView.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/search',
        name: 'search',
        component: () => import('../views/user/SearchView.vue')
    },
    {
        path: '/about',
        name: 'about',
        component: () => import('../views/user/AboutView.vue')
    },
    {
        path: '/product/:id',
        name: 'product',
        component: () => import('../components/ProductDetail.vue')
    },
    {
        path: '/cart',
        name: 'cart',
        component: () => import('../views/user/CartView.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/user/LoginView.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/user/UserRegister.vue')
    },

    // 新增短信注册路由
    {
        path: '/register/sms',
        name: 'sms-register',
        component: () => import('../views/user/SmsRegister.vue')
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
    },

    // ======================= 管理员路由区域 =======================
// ======================= 管理员路由区域 =======================
// 1. 访问/admin直接重定向到登录页
    {
        path: '/admin',
        redirect: '/admin/login'
    },

// 2. 管理员登录页
    {
        path: '/admin/login',
        name: 'admin-login',
        component: () => import('@/views/admin/AdminLogin.vue'),
        meta: { layout: 'admin' }
    },

// 3. 管理员主框架 + 嵌套路由（简洁路径）
    {
        path: '/admin',
        name: 'admin-main',
        component: () => import('@/views/admin/AdminMain.vue'),
        meta: {
            requiresAdminAuth: true,
            layout: 'admin'
        },
        children: [
            // 3.1 默认重定向到仪表盘
            {
                path: '',
                redirect: 'dashboard'
            },

            // 3.2 仪表盘
            {
                path: 'dashboard',
                name: 'admin-dashboard',
                component: () => import('@/views/admin/Admin1Dashboard.vue')
            },

            // 3.3 用户管理
            {
                path: 'users',
                name: 'admin-users',
                component: () => import('@/views/admin/Admin2Users.vue')
            },

            // 3.4 商品管理
            {
                path: 'products',
                name: 'admin-products',
                component: () => import('@/views/admin/Admin3Products.vue')
            },

            // 3.5 订单管理
            {
                path: 'orders',
                name: 'admin-orders',
                component: () => import('@/views/admin/Admin4Orders.vue')
            },

            // 3.6 商家管理
            {
                path: 'businesses',
                name: 'admin-businesses',
                component: () => import('@/views/admin/Admin5Businesses.vue')
            },

            // 3.7 评论管理
            {
                path: 'comments',
                name: 'admin-comments',
                component: () => import('@/views/admin/Admin6Comments.vue')
            },

            // 3.8 系统设置
            {
                path: 'settings',
                name: 'admin-settings',
                component: () => import('@/views/admin/Admin7Settings.vue')
            }
        ]
    }
// ======================= 管理员路由结束 =======================


    // ======================= 管理员路由结束 =======================
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    // 用户路由守卫
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

        if (!isLoggedIn) {
            alert('请先登录！');
            next('/login');
            return;  // 重要：这里要return，避免继续执行
        }
    }

    // 管理员路由守卫
    if (to.matched.some(record => record.meta.requiresAdminAuth)) {
        const isAdminLoggedIn = localStorage.getItem('adminLoggedIn') === 'true';

        if (!isAdminLoggedIn) {
            next('/admin/login');
            return;  // 重要：这里要return，避免继续执行
        }
    }

    next();  // 放行
})

export default router