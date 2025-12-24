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
        component: () => import('../views/user/EmailRegister.vue')
    },

    // 新增短信注册路由
    {
        path: '/register/sms',
        name: 'sms-register',
        component: () => import('../views/user/TelRegister.vue')
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

    // ======================= 商家端路由区域 =======================
    // 注意：/business/login 必须在 /business 之前定义，避免路由冲突
    {
        path: '/business/login',
        name: 'BusinessLogin',
        component: () => import('@/views/business/BusinessLogin.vue'),
        meta: {
            requiresAuth: false,
            requiresBusiness: false,
            title: '商家登录',
            layout: 'business'
        }
    },

    // 商家主框架路由
    {
        path: '/business',
        component: () => import('@/views/business/BusinessMain.vue'),
        meta: {
            requiresAuth: false,
            requiresBusiness: true,
            layout: 'business'
        },
        children: [
            {
                path: '',
                redirect: 'dashboard'
            },
            {
                path: 'dashboard',
                name: 'BusinessDashboard',
                component: () => import('@/views/business/BusinessDashboard.vue'),
                meta: {
                    title: '商家概况'
                }
            },
            {
                path: 'products',
                name: 'BusinessProduct',
                component: () => import('@/views/business/BusinessProduct.vue'),
                meta: {
                    title: '商品管理'
                }
            },
            {
                path: 'orders',
                name: 'BusinessOrder',
                component: () => import('@/views/business/BusinessOrder.vue'),
                meta: {
                    title: '订单管理'
                }
            },
            {
                path: 'statistics',
                name: 'BusinessStatistics',
                component: () => import('@/views/business/BusinessStatistic.vue'),
                meta: {
                    title: '销售统计'
                }
            },
            {
                path: 'profile',
                name: 'BusinessProfile',
                component: () => import('@/views/business/BusinessProfile.vue'),
                meta: {
                    title: '商家信息'
                }
            }
        ]
    },
    // ======================= 商家端路由结束 =======================

    // ======================= 管理员路由区域 =======================
    // 1. 管理员登录页 - 不需要守卫，但已登录则跳转
    {
        path: '/admin/login',
        name: 'admin-login',
        component: () => import('@/views/admin/AdminLogin.vue'),
        meta: { layout: 'admin' }
    },

    // 2. 管理员主框架 + 嵌套路由（需要管理员权限）
    {
        path: '/admin',
        name: 'admin-main',
        component: () => import('@/views/admin/AdminMain.vue'),
        meta: {
            requiresAdminAuth: true,
            layout: 'admin'
        },
        children: [
            // 2.1 默认重定向到仪表盘
            {
                path: '',
                redirect: 'dashboard'
            },

            // 2.2 仪表盘
            {
                path: 'dashboard',
                name: 'admin-dashboard',
                component: () => import('@/views/admin/Admin1Dashboard.vue')
            },

            // 2.3 用户管理
            {
                path: 'users',
                name: 'admin-users',
                component: () => import('@/views/admin/Admin2Users.vue')
            },

            // 2.4 商品管理
            {
                path: 'products',
                name: 'admin-products',
                component: () => import('@/views/admin/Admin3Products.vue')
            },

            // 2.5 订单管理
            {
                path: 'orders',
                name: 'admin-orders',
                component: () => import('@/views/admin/Admin4Orders.vue')
            },

            // 2.6 商家管理
            {
                path: 'businesses',
                name: 'admin-businesses',
                component: () => import('@/views/admin/Admin5Businesses.vue')
            },

            // 2.7 评论管理
            {
                path: 'comments',
                name: 'admin-comments',
                component: () => import('@/views/admin/Admin6Comments.vue')
            },

            // 2.8 系统设置
            {
                path: 'settings',
                name: 'admin-settings',
                component: () => import('@/views/admin/Admin7Settings.vue')
            }
        ]
    },

    // 3. 访问 /admin 重定向到 /admin/dashboard（经过守卫验证）
    {
        path: '/admin',
        redirect: '/admin/dashboard'
    }
    // ======================= 管理员路由结束 =======================
]

// 管理员路由守卫函数
const requireAdminAuth = (to, from, next) => {
    const isLoggedIn = localStorage.getItem('adminLoggedIn');
    const adminToken = localStorage.getItem('adminToken');

    // 检查是否已登录且有token
    if (isLoggedIn === 'true' && adminToken) {
        next();
    } else {
        // 未登录，跳转到登录页
        next('/admin/login');
    }
};

// 商家端路由守卫函数
const requireBusinessAuth = (to, from, next) => {
    const isBusinessLoggedIn = localStorage.getItem('businessLoggedIn') === 'true';
    const businessToken = localStorage.getItem('businessToken');

    if (isBusinessLoggedIn && businessToken) {
        next();
    } else {
        // 清空可能的残留数据
        localStorage.removeItem('businessToken');
        localStorage.removeItem('businessLoggedIn');
        localStorage.removeItem('businessInfo');
        next('/business/login');
    }
};

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    if (to.meta && to.meta.title) {
        if (to.meta.requiresAdminAuth) {
            document.title = `${to.meta.title} - 泡泡联盟管理后台`;
        } else if (to.meta.requiresBusiness) {
            document.title = `${to.meta.title} - 泡泡联盟商家中心`;
        } else {
            document.title = `${to.meta.title} - 泡泡联盟`;
        }
    } else {
        document.title = '泡泡联盟';
    }

    // 用户路由守卫
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';

        if (!isLoggedIn) {
            alert('请先登录！');
            next('/login');
            return;
        }
    }

    // 商家路由守卫 - 需要商家权限
    if (to.matched.some(record => record.meta.requiresBusiness)) {
        requireBusinessAuth(to, from, next);
        return;
    }

    // 商家登录页 - 如果已登录则重定向到商家后台
    if (to.path === '/business/login') {
        const isBusinessLoggedIn = localStorage.getItem('businessLoggedIn') === 'true';
        const businessToken = localStorage.getItem('businessToken');

        if (isBusinessLoggedIn && businessToken) {
            next('/business/dashboard');
            return;
        }
    }

    // 管理员登录页守卫 - 如果已登录则跳转到管理员后台
    if (to.path === '/admin/login') {
        const isLoggedIn = localStorage.getItem('adminLoggedIn') === 'true';
        const adminToken = localStorage.getItem('adminToken');

        if (isLoggedIn && adminToken) {
            next('/admin/dashboard');
            return;
        }
    }

    // 管理员路由守卫 - 需要管理员权限
    if (to.matched.some(record => record.meta.requiresAdminAuth)) {
        requireAdminAuth(to, from, next);
        return;
    }

    next();
})

export default router