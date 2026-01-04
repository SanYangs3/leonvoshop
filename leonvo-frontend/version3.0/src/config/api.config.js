// API 配置 - 根据后端接口文档配置
const API_CONFIG = {
    // 开发环境
    development: {
        baseUrl: 'http://localhost:8080',
        adminBase: 'http://localhost:8080/admin',
        userBase: 'http://localhost:8080/user',
        productBase: 'http://localhost:8080/api/products',
        orderBase: 'http://localhost:8080/api',
        statisticsBase: 'http://localhost:8080/api',
        businessBase: 'http://localhost:8080/api/businesses',      // 用于登录和商家信息
        businessApiBase: 'http://localhost:8080/api/business'      // 用于商品和订单管理
    },
    // 生产环境
    production: {
        baseUrl: 'https://your-cloud-server.com',
        adminBase: 'https://your-cloud-server.com/admin',
        userBase: 'https://your-cloud-server.com/user',
        productBase: 'https://your-cloud-server.com/api/products',
        orderBase: 'https://your-cloud-server.com/api',
        statisticsBase: 'https://your-cloud-server.com/api',
        businessBase: 'https://your-cloud-server.com/api/businesses',
        businessApiBase: 'https://your-cloud-server.com/api/business'
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
        getAllUsers: `${currentConfig.adminBase}/user`, // 实际接口已变更为 /admin/user

        // 1.2 添加用户
        addUser: `${currentConfig.adminBase}/user/insert`,

        // 1.3 获取单个用户信息
        getUserById: (id) => `${currentConfig.adminBase}/user/${id}`,

        // 1.4 更新用户信息（POST请求，可以传递任意字段，uid为必须）
        updateUser: `${currentConfig.adminBase}/user/update`,
        updateUserInfo: `${currentConfig.adminBase}/user/update`, // 别名，保持兼容

        // 1.7 获取用户身份信息
        getUserRole: (id) => `${currentConfig.adminBase}/user/role/${id}`,

        // 1.8 获取用户订单 - 注意：这里使用的是orders接口，不是order
        getUserOrders: (uid) => `${currentConfig.orderBase}/orders/user/${uid}`,

        // 1.9 获取用户购物车
        getUserCart: (uid) => `${currentConfig.adminBase}/cart/${uid}`,
        
        // 购物车相关接口
        addToCart: `${currentConfig.adminBase}/cart/add`,
        deleteCartItem: (uid, pid) => `${currentConfig.adminBase}/cart/delete?uid=${uid}&pid=${pid}`,
        deleteCartItemByCid: (cid, uid) => `${currentConfig.adminBase}/cart/delete/${cid}?uid=${uid}`,
        clearCart: (uid) => `${currentConfig.adminBase}/cart/clear/${uid}`,
        updateCartQuantity: `${currentConfig.adminBase}/cart/update`,
        checkCartItemExists: (uid, pid) => `${currentConfig.adminBase}/cart/exists?uid=${uid}&pid=${pid}`,

        // 1.10 注销用户
        deactivateUser: (id) => `${currentConfig.adminBase}/user/status/${id}`,

        // 登录相关（所有登录接口都是GET请求，只返回成功/失败）
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
        updateUserRole: (uid) => `${currentConfig.adminBase}/user/updaterole/${uid}`,

        // 3.8 用户信息修改相关
        // 注意：后端没有单独的verifypassword接口，使用登录接口验证密码
        // verifyPassword: `${currentConfig.adminBase}/user/verifypassword`, // 后端未实现，使用登录接口代替
        updateUserInfo: `${currentConfig.adminBase}/user/update`, // 使用POST请求（根据后端代码）
        updateAvatar: `${currentConfig.adminBase}/user/avator`
    },

    // 商品相关接口
    product: {
        // 2.17 获取所有商品信息
        getAllProducts: `${currentConfig.productBase}`,

        // 2.2 获取所有商品数
        getProductCount: `${currentConfig.productBase}/count`,

        // 2.10 获取今日低库存商品数
        getLowStockCount: `${currentConfig.productBase}/low-stock/count`,
        
        // 注意：文档中没有直接提供获取低库存商品列表的接口，只有商家的 (4.11)
        // 如果需要管理员查看所有低库存商品，可能需要后端补充接口
        // 这里暂时保留原有的，但标记为可能不存在
        getLowStockProducts: `${currentConfig.productBase}/low-stock`,

        // 获取商品详情
        getProductDetail: (productId) => `${currentConfig.productBase}/${productId}`
    },

    // 订单相关接口
    order: {
        // 2.18 获取所有订单信息
        getAllOrders: `${currentConfig.orderBase}/orders`,

        // 2.3 获取所有订单数
        getOrderCount: `${currentConfig.orderBase}/orders/count`,

        // 2.4 总销售额
        getTotalSales: `${currentConfig.orderBase}/orders/total-sales`,

        // 2.11 获取今日各省份订单数
        getTodayOrdersByProvince: `${currentConfig.orderBase}/orders/today/statistics/by-province`,

        // 1.8 通过用户id获取用户下单订单 - 使用 /api/orders/user/{uid}
        getOrdersByUserId: (uid) => `${currentConfig.orderBase}/orders/user/${uid}`,

        // 确认收货
        confirmReceipt: (oid, uid) => `${currentConfig.orderBase}/orders/${oid}/confirm-receipt?uid=${uid}`,

        // 创建订单
        createOrder: `${currentConfig.orderBase}/orders/create`,

        // 从购物车创建订单
        createOrderFromCart: (uid) => `${currentConfig.adminBase}/cart/create-order/${uid}`,

        // 取消订单
        cancelOrder: (oid, uid) => `${currentConfig.orderBase}/orders/${oid}/cancel?uid=${uid}`,
        
        // 获取订单详情（包含订单项）
        getOrderDetail: (oid) => `${currentConfig.orderBase}/orders/detail/${oid}`,

        // [新增] 按省份统计订单数量 (所有时间)
        countOrdersByProvince: `${currentConfig.orderBase}/orders/statistics/by-province`,
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
    },

    // 商家相关接口
    business: {
        // ===== 商家列表 (管理员用) =====
        getAllBusinesses: `${currentConfig.businessBase}`, // GET /api/businesses

        // ===== 登录认证（使用 businessBase） =====
        login: `${currentConfig.businessBase}/login`,
        loginFull: `${currentConfig.businessBase}/login-full`,
        getProfile: (bid) => `${currentConfig.businessBase}/full/${bid}`,

        // ===== 商品管理（使用 businessApiBase） =====
        getProducts: (bid) => `${currentConfig.businessApiBase}/products/my/${bid}`,
        getProductDetail: (bid, pid) => `${currentConfig.businessApiBase}/products/my/${bid}/${pid}`,
        uploadProduct: (bid) => `${currentConfig.businessApiBase}/products/upload/${bid}`,
        updateProduct: (bid) => `${currentConfig.businessApiBase}/products/update/${bid}`,
        getLowStockProducts: (bid) => `${currentConfig.businessApiBase}/products/my/${bid}/low-stock`,

        // ===== 订单管理（使用 businessApiBase） =====
        getOrders: (bid) => `${currentConfig.businessApiBase}/orders/my/${bid}`,
        shipOrder: (bid, oid) => `${currentConfig.businessApiBase}/orders/ship/${bid}/${oid}`,
        // 注意：商家取消订单的API在文档中可能不存在，先保留配置
        cancelOrder: (bid, oid) => `${currentConfig.businessApiBase}/orders/cancel/${bid}/${oid}`,

        // ===== 统计信息 =====
        // 这些API在文档中没有明确对应，根据情况使用 businessBase 或 businessApiBase
        getStats: (bid) => `${currentConfig.businessBase}/${bid}/stats`,
        getSalesTrend: (bid) => `${currentConfig.businessBase}/${bid}/sales-trend`,
        getTopProducts: (bid) => `${currentConfig.businessBase}/${bid}/top-products`,
        getRecentOrders: (bid) => `${currentConfig.businessBase}/${bid}/recent-orders`,

        // ===== 商家信息管理（使用 businessBase） =====
        updateProfile: (bid) => `${currentConfig.businessBase}/${bid}`,
        changePassword: (bid) => `${currentConfig.businessBase}/${bid}/change-password`,
    },

    // 反馈/评论相关接口
    feedback: {
        // 功能20：获取对应商品对应评价
        getProductFeedbacks: (pid) => `${currentConfig.baseUrl}/api/feedbacks/product/${pid}`
    },

    // 短信验证相关接口
    sms: {
        // 3.6.2 发送短信验证码
        sendCode: `${currentConfig.baseUrl}/api/sms/send-code`,
        
        // 3.6.3 验证短信验证码
        verifyCode: `${currentConfig.baseUrl}/api/sms/verify-code`,
        
        // 3.6.4 检查是否可以重新发送短信验证码
        canResend: (phone) => `${currentConfig.baseUrl}/api/sms/can-resend/${phone}`
    }
}