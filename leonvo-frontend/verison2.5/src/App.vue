[file name]: App.vue
<template>
  <div id="app">
    <!-- 导航栏 - 保留原有功能 -->
    <nav v-if="shouldShowNav">
      <div class="nav-content">
        <router-link to="/" class="logo">
          <h2>联想电脑商城</h2>
        </router-link>
        <div class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link to="/search" class="search-link">
            🔍 搜索
          </router-link>
          <router-link to="/cart" class="cart-link">
            🛒 购物车
            <span v-if="cartCount > 0" class="cart-badge">
              {{ cartCount }}
            </span>
          </router-link>

          <!-- 商家入口 -->
          <router-link to="/business/login" class="business-link">
            🏪 商家中心
          </router-link>

          <!-- 用户信息 -->
          <div v-if="isLoggedIn" class="user-info">
            <span class="welcome">欢迎, {{ username }}</span>
            <button @click="handleLogout" class="logout-btn">
              退出
            </button>
          </div>
          <router-link v-else to="/login" class="login-link">
            👤 登录
          </router-link>

          <router-link to="/about">关于</router-link>
        </div>
      </div>
    </nav>

    <main>
      <router-view/>
    </main>

    <!-- 只在非特殊页面显示尾部 -->
    <Footer v-if="!isSpecialPage" />
  </div>
</template>

<script>
import Footer from '@/components/Footer.vue'

export default {
  name: 'App',
  components: {
    Footer
  },
  data() {
    return {
      cartCount: 0,
      isLoggedIn: false,
      username: ''
    };
  },
  computed: {
    shouldShowNav() {
      // 优先检查 meta.hideNav
      if (this.$route.meta.hideNav) {
        return false
      }

      const path = this.currentPath || this.$route.path

      // 管理员登录页显示导航栏
      if (path === '/admin/login') {
        return true
      }

      // 商家页面不显示导航栏
      if (path.startsWith('/business/')) {
        return false
      }

      // 管理后台页面不显示导航栏
      const adminPaths = [
        '/admin/dashboard',
        '/admin/users',
        '/admin/products',
        '/admin/orders',
        '/admin/businesses',
        '/admin/comments',
        '/admin/settings'
      ]

      // 检查是否以管理后台路径开头
      for (const adminPath of adminPaths) {
        if (path.startsWith(adminPath)) {
          return false
        }
      }

      // 其他页面显示导航栏
      return true
    },

    isAdminPage() {
      const path = this.$route.path

      // 管理员登录页也算管理后台相关页面，不显示Footer
      if (path === '/admin/login') {
        return true
      }

      // 检查是否以管理后台路径开头
      const adminPaths = [
        '/admin/dashboard',
        '/admin/users',
        '/admin/products',
        '/admin/orders',
        '/admin/businesses',
        '/admin/comments',
        '/admin/settings'
      ]

      for (const adminPath of adminPaths) {
        if (path.startsWith(adminPath)) {
          return true
        }
      }

      return false
    },

    isSpecialPage() {
      const path = this.$route.path
      // 商家页面和管理后台页面都不显示Footer
      return path.startsWith('/business/') || this.isAdminPage
    }
  },
  mounted() {
    this.updateCartCount();
    this.checkLoginStatus();

    // 优化：改为监听storage事件，减少定时器使用
    window.addEventListener('storage', this.handleStorageChange);

    // 仍然保留定时器，但延长间隔时间
    this.cartInterval = setInterval(() => {
      this.updateCartCount();
    }, 3000);

    this.loginInterval = setInterval(() => {
      this.checkLoginStatus();
    }, 3000);
  },
  beforeUnmount() {
    // 清理定时器和事件监听器
    if (this.cartInterval) clearInterval(this.cartInterval);
    if (this.loginInterval) clearInterval(this.loginInterval);
    window.removeEventListener('storage', this.handleStorageChange);
  },
  watch: {
    // 监听路由变化，强制更新 currentPath
    '$route'(to) {
      this.currentPath = to.path
    }
  },
  methods: {
    handleStorageChange(event) {
      if (event.key === 'lenovo_cart') {
        this.updateCartCount();
      } else if (event.key === 'isLoggedIn' || event.key === 'username') {
        this.checkLoginStatus();
      }
    },

    updateCartCount() {
      const cartData = localStorage.getItem('lenovo_cart');
      if (cartData) {
        try {
          const cart = JSON.parse(cartData);
          let count = 0;
          for (let item of cart) {
            count += item.quantity;
          }
          this.cartCount = count;
        } catch (error) {
          this.cartCount = 0;
        }
      } else {
        this.cartCount = 0;
      }
    },

    checkLoginStatus() {
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      const user = localStorage.getItem('username');

      this.isLoggedIn = loggedIn;
      this.username = user || '';
    },

    handleLogout() {
      if (confirm('确定要退出登录吗？购物车数据将被清空。')) {
        // 清空购物车
        localStorage.removeItem('lenovo_cart');
        // 清除登录状态
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('username');
        this.isLoggedIn = false;
        this.username = '';
        this.cartCount = 0;

        // 跳转到首页
        if (this.$route.path !== '/') {
          this.$router.push('/');
        }
      }
    }
  }
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', sans-serif;
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 50%, #e8f5e9 100%);
  background-attachment: fixed;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

nav {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 50%, #388e3c 100%);
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 20px rgba(27, 94, 32, 0.2);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 2px solid #81c784;
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo {
  color: white;
  text-decoration: none;
}

.logo h2 {
  font-size: 24px;
  margin: 0;
  font-weight: 700;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.2);
}

.nav-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 8px 18px;
  border-radius: 25px;
  transition: all 0.3s ease;
  position: relative;
  font-weight: 500;
  border: 1px solid rgba(255,255,255,0.1);
}

.nav-links a:hover {
  background: rgba(255,255,255,0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.router-link-exact-active {
  background: rgba(255,255,255,0.2);
  border-color: rgba(255,255,255,0.3);
}

/* 商家入口特殊样式 */
.business-link {
  background: rgba(255, 193, 7, 0.2);
  border-color: rgba(255, 193, 7, 0.3);
}

.business-link:hover {
  background: rgba(255, 193, 7, 0.3);
}

.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: linear-gradient(135deg, #ff9800 0%, #ff5722 100%);
  color: white;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 87, 34, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.welcome {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
}

.logout-btn {
  background: rgba(255,255,255,0.1);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 6px 14px;
  border-radius: 25px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
}

.logout-btn:hover {
  background: rgba(255,255,255,0.25);
  transform: translateY(-1px);
}

.login-link {
  display: flex;
  align-items: center;
  gap: 5px;
}

main {
  flex: 1;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 商家页面特殊处理 */
main:has(.business-main),
main:has(.business-login-view) {
  padding: 0 !important;
  max-width: 100% !important;
  margin: 0 !important;
}

/* 管理后台页面特殊处理 */
main:has(.admin-main),
main:has(.admin-login-view) {
  padding: 0 !important;
  max-width: 100% !important;
  margin: 0 !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-content {
    flex-direction: column;
    gap: 15px;
  }

  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
    gap: 12px;
  }

  .logo h2 {
    font-size: 20px;
  }

  .nav-links a {
    padding: 6px 12px;
    font-size: 14px;
  }
}

.search-link {
  position: relative;
}

.search-link:hover::after {
  content: '快速查找商品';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1001;
}
</style>