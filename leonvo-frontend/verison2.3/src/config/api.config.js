// API 配置 - 根据后端接口文档配置
const API_CONFIG = {
    // 开发环境
    development: {
        baseUrl: 'http://localhost:8080',
        adminBase: 'http://localhost:8080/admin',
        userBase: 'http://localhost:8080/user',
        productBase: 'http://localhost:8080/api/products',
        orderBase: 'http://localhost:8080/api',
        statisticsBase: 'http://localhost:8080/api'
    },
    // 生产环境
    production: {
        baseUrl: 'https://your-cloud-server.com',
        adminBase: 'https://your-cloud-server.com/admin',
        userBase: 'https://your-cloud-server.com/user',
        productBase: 'https://your-cloud-server.com/api/products',
        orderBase: 'https://your-cloud-server.com/api',
        statisticsBase: 'https://your-cloud-server.com/api'
    }
}

// 根据环境自动选择配置
const env = process.env.NODE_ENV || 'development'
const currentConfig = API_CONFIG[env]

// 导出配置
export default {
    // 基础 URL
    baseURL: currentConfig.baseUrl,

    // 环境标识
    ENV: env,

    // 用户相关接口
    user: {
        // 1.1 获取所有用户
        getAllUsers: `${currentConfig.adminBase}/user`,

        // 1.2 添加用户
        addUser: `${currentConfig.adminBase}/user/insert`,

        // 1.3 获取单个用户信息
        getUserById: (id) => `${currentConfig.adminBase}/user/${id}`,

        // 1.4 更新用户信息
        updateUser: `${currentConfig.adminBase}/user/update`,

        // 1.7 获取用户身份信息
        getUserRole: (id) => `${currentConfig.adminBase}/user/role/${id}`,

        // 1.8 获取用户订单 - 注意：这里使用的是orders接口，不是order
        getUserOrders: (uid) => `${currentConfig.orderBase}/orders/user/${uid}`,

        // 1.9 获取用户购物车 - 保持原样（后端可能在其他Controller）
        getUserCart: (uid) => `${currentConfig.adminBase}/cart/${uid}`,

        // 1.10 注销用户
        deactivateUser: (id) => `${currentConfig.adminBase}/user/status/${id}`,

        // 登录相关
        phoneLogin: `${currentConfig.adminBase}/user/phonelogin`,
        emailLogin: `${currentConfig.adminBase}/user/emaillogin`,
        usernameLogin: `${currentConfig.adminBase}/user/usernamelogin`,

        // 注册验证
        checkUsername: `${currentConfig.adminBase}/user/namedistinct`,
        checkPhone: `${currentConfig.adminBase}/user/phonedistinct`,

        // 后台统计
        getTotalUsers: `${currentConfig.adminBase}/user/all`,
        getTodayNewUsers: `${currentConfig.adminBase}/user/add`,
        getUserComparison: `${currentConfig.adminBase}/user/compare`,

        // 身份编辑
        updateUserRole: (uid) => `${currentConfig.adminBase}/user/updaterole/${uid}`
    },

    // 商品相关接口
    product: {
        // 获取所有商品
        getAllProducts: `${currentConfig.productBase}`,

        // 获取商品数量
        getProductCount: `${currentConfig.productBase}/count`,

        // 获取商品详情
        getProductDetail: (id) => `${currentConfig.productBase}/${id}`,

        // 获取低库存商品数量
        getLowStockCount: `${currentConfig.productBase}/low-stock/count`,

        // 获取低库存商品列表
        getLowStockProducts: `${currentConfig.productBase}/low-stock`,

        // 今天商品数量
        getTodayCount: `${currentConfig.productBase}/today-count`,

        // 昨天商品数量
        getYesterdayCount: `${currentConfig.productBase}/yesterday-count`
    },

    // 订单相关接口
    order: {
        // 获取所有订单
        getAllOrders: `${currentConfig.orderBase}/orders`,

        // 获取订单数量
        getOrderCount: `${currentConfig.orderBase}/orders/count`,

        // 获取总销售额
        getTotalSales: `${currentConfig.orderBase}/orders/total-sales`,

        // 获取今日销售额
        getTodaySales: `${currentConfig.orderBase}/orders/today-sales`,

        // 获取昨日销售额
        getYesterdaySales: `${currentConfig.orderBase}/orders/yesterday-sales`,

        // 获取今日订单数
        getTodayOrderCount: `${currentConfig.orderBase}/orders/today-count`,

        // 获取昨日订单数
        getYesterdayOrderCount: `${currentConfig.orderBase}/orders/yesterday-count`,

        // 根据订单ID获取订单详情
        getOrderById: (oid) => `${currentConfig.orderBase}/orders/${oid}`,

        // 根据用户ID获取订单
        getOrdersByUserId: (uid) => `${currentConfig.orderBase}/orders/user/${uid}`,

        // 按省份统计订单数量
        getOrdersByProvinceStats: `${currentConfig.orderBase}/orders/statistics/by-province`,

        // 按省份统计销售额
        getSalesByProvinceStats: `${currentConfig.orderBase}/orders/statistics/sales-by-province`,

        // 查询某个省份的订单
        getOrdersByProvince: (province) => `${currentConfig.orderBase}/orders/province/${province}`,

        // 获取今日各省份订单数
        getTodayOrdersByProvince: `${currentConfig.orderBase}/orders/today/statistics/by-province`,

        // 获取今日各省份销售额
        getTodaySalesByProvince: `${currentConfig.orderBase}/orders/today/statistics/sales-by-province`,

        // 销售趋势接口 - 新增
        getSalesTrend: `${currentConfig.orderBase}/orders/sales-trend`
    },

    // 统计相关接口
    statistics: {
        // 统计总览
        getDashboardStats: `${currentConfig.statisticsBase}/statistics/dashboard`,

        // 商品统计
        getProductCount: `${currentConfig.statisticsBase}/statistics/product-count`,

        // 订单统计
        getOrderCount: `${currentConfig.statisticsBase}/statistics/order-count`,
        getTodayOrderCount: `${currentConfig.statisticsBase}/statistics/today-order-count`,
        getYesterdayOrderCount: `${currentConfig.statisticsBase}/statistics/yesterday-order-count`,

        // 销售统计
        getTotalSales: `${currentConfig.statisticsBase}/statistics/total-sales`,
        getTodaySales: `${currentConfig.statisticsBase}/statistics/today-sales`,
        getYesterdaySales: `${currentConfig.statisticsBase}/statistics/yesterday-sales`,

        // 低库存统计
        getLowStockCount: `${currentConfig.statisticsBase}/statistics/low-stock-count`,

        // 对比统计
        getOrderComparison: `${currentConfig.statisticsBase}/statistics/order-comparison`,
        getSalesComparison: `${currentConfig.statisticsBase}/statistics/sales-comparison`,
        getProductComparison: `${currentConfig.statisticsBase}/statistics/product-comparison`,

        // 趋势统计 - 新增
        getUserGrowthTrend: `${currentConfig.statisticsBase}/statistics/user-growth-trend`,
        getSalesTrend: `${currentConfig.statisticsBase}/statistics/sales-trend`
    }
}