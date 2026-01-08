// src/router/auth.js
//保护后台的路由 要先登录后台账号才能进入后台
export const requireAdminAuth = (to, from, next) => {
    const isLoggedIn = localStorage.getItem('adminLoggedIn');
    const adminToken = localStorage.getItem('adminToken');
    const adminUser = localStorage.getItem('adminUser');

    if (isLoggedIn === 'true' && adminToken && adminUser) {
        try {
            const user = JSON.parse(adminUser);
            // 验证角色是否为管理员 (role === 1)
            if (user.role === 1) {
                next();
            } else {
                // 不是管理员，跳转到登录页
                next('/admin/login');
            }
        } catch (error) {
            console.error('解析用户信息失败:', error);
            next('/admin/login');
        }
    } else {
        // 未登录，跳转到登录页
        next('/admin/login');
    }
};

// 如果已登录，重定向到后台首页
export const redirectIfLoggedIn = (to, from, next) => {
    const isLoggedIn = localStorage.getItem('adminLoggedIn');
    const adminToken = localStorage.getItem('adminToken');

    if (isLoggedIn === 'true' && adminToken) {
        next('/admin/dashboard');
    } else {
        next();
    }
};

// 登出函数
export const logoutAdmin = () => {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    localStorage.removeItem('adminLoggedIn');
    // 跳转到登录页
    window.location.href = '/admin/login';
};