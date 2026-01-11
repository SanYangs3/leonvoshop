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
          <!-- 购物车和订单中心只在登录时显示 -->
          <router-link v-if="isLoggedIn" to="/cart" class="cart-link">
            🛒 购物车
          </router-link>
          <router-link v-if="isLoggedIn" to="/user/orders" class="order-link">
            📦 订单中心
          </router-link>

          <!-- 商家入口 -->
          <router-link to="/business/login" class="business-link">
            🏪 
          </router-link>

          <!-- 用户信息 -->
          <div v-if="isLoggedIn" class="user-info">
            <router-link to="/user/profile" class="welcome-link">
              <span class="welcome">欢迎, {{ username }}</span>
            </router-link>
            <button @click="handleLogout" class="logout-btn">
              退出
            </button>
          </div>
          <router-link v-else to="/login" class="login-link">
            👤 登录
          </router-link>

          <router-link to="/about">关于</router-link>
          
          <!-- 消息通知图标 -->
          <div class="notification-wrapper">
            <router-link v-if="isLoggedIn" to="/user/notifications" class="notification-icon" title="消息中心">
               📩
               <span v-if="notification.hasUnread" class="notification-dot"></span>
            </router-link>
          </div>
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
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'App',
  components: {
    Footer
  },
  data() {
    return {
      isLoggedIn: false,
      username: '',
      // Fix: Add missing properties
      showNotificationModal: false,
      notification: {
        show: false,
        message: '',
        currentStatus: null
      }
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
        '/admin/student-auth', // 新增学生认证管理页
        '/admin/promotions',   // 新增推广管理页
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
        '/admin/student-auth', // 新增学生认证管理页
        '/admin/promotions',   // 新增推广管理页
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
    this.checkLoginStatus();

    // 监听storage事件
    window.addEventListener('storage', this.handleStorageChange);
    
    // 添加自定义事件监听器
    window.addEventListener('login-status-changed', this.checkLoginStatus);
  },
  beforeUnmount() {
    // 清理事件监听器
    window.removeEventListener('storage', this.handleStorageChange);
    window.removeEventListener('login-status-changed', this.checkLoginStatus);
  },
  watch: {
    // 监听路由变化，强制更新 currentPath
    '$route'(to) {
      this.currentPath = to.path
    }
  },
  methods: {
    handleStorageChange(event) {
      if (event.key === 'isLoggedIn' || event.key === 'username') {
        this.checkLoginStatus();
      }
    },

    checkLoginStatus() {
      try {
        const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
        const user = localStorage.getItem('username');
        
        let userInfo = {};
        try {
          const userInfoStr = localStorage.getItem('userInfo');
          if (userInfoStr && userInfoStr !== 'undefined') {
            userInfo = JSON.parse(userInfoStr);
          }
        } catch (e) {
          console.error('解析用户信息失败:', e);
          // 如果解析失败，清除无效数据
          localStorage.removeItem('userInfo');
        }

        console.log('检查登录状态:', { loggedIn, user, userInfo });

        this.isLoggedIn = loggedIn;
        this.username = user || '';
        
        // 确保获取到正确的UID
        const uid = userInfo.uid || userInfo.id || localStorage.getItem('userId');
        console.log('当前用户UID:', uid);

        if (loggedIn && uid) {
          this.checkStudentStatus(uid);
        }
      } catch (error) {
        console.error('检查登录状态时出错:', error);
      }
    },

    checkStudentStatus(uid) {
        // 使用新Key以重置读取状态
        const readStatus = localStorage.getItem('student_auth_read_status_v2');
        
        console.log('正在检查学生认证状态, UID:', uid);

        axios.get(apiConfig.student.getStatus(uid))
            .then(res => {
                console.log('学生认证状态API响应:', res.data);
                if (res.data.code === 1 && res.data.data) {
                    const status = res.data.data.status;
                    // status: 1 (Approved), 2 (Rejected)
                    console.log(`状态对比: API状态=${status}, 本地已读状态=${readStatus}`);
                    
                    // 宽松比较，防止类型差异
                    if ((status === 1 || status === 2) && String(readStatus) !== String(status)) {
                        console.log('条件满足，显示通知！');
                        this.notification.show = true;
                        this.notification.message = status === 1 
                            ? '恭喜您！您的学生认证申请已通过审核。您现在可以享受学生专属优惠了！' 
                            : '很抱歉，您的学生认证申请未通过。请检查信息后重新提交。';
                        // 暂存当前状态
                        this.notification.currentStatus = status;
                    } else {
                        console.log('条件不满足，不显示通知');
                        this.notification.show = false;
                    }
                } else {
                     console.log('未获取到有效的学生认证数据');
                }
            })
            .catch(err => {
                console.error('获取学生认证状态失败:', err);
            });
    },

    markAllAsRead() {
        this.notification.list.forEach(item => {
            if (!item.read) {
                item.read = true;
                // 如果是学生认证消息，保存到localStorage
                if (item.rawStatus) {
                    localStorage.setItem('student_auth_read_status_v3', item.rawStatus);
                }
            }
        });
        this.notification.hasUnread = false;
    },

    handleLogout() {
      // 使用 window.confirm 确保调用的是原生方法
      if (!window.confirm('确定要退出登录吗？')) {
        return;
      }
      
      // 不清空购物车，因为购物车数据在后端，用户重新登录后会自动加载
      // 清除登录状态
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('userId');
      localStorage.removeItem('userInfo');
      this.isLoggedIn = false;
      this.username = '';

      // 跳转到首页
      if (this.$route.path !== '/') {
        this.$router.push('/');
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
  background: white;
  background-attachment: fixed;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

nav {
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 50%, #1976d2 100%);
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 20px rgba(21, 101, 192, 0.2);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 2px solid #64b5f6;
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
  border-radius: 0;
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
.student-auth-nav {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 8px 18px;
  border-radius: 0;
  transition: all 0.3s ease;
  position: relative;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
}

.student-auth-nav:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

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
  border-radius: 0;
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

.welcome-link {
  text-decoration: none;
  color: white;
  transition: all 0.3s ease;
}

.welcome-link:hover {
  opacity: 1;
  transform: translateY(-1px);
}

.welcome {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
  cursor: pointer;
}

.logout-btn {
  background: rgba(255,255,255,0.1);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 6px 14px;
  border-radius: 0;
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
  border-radius: 0;
  font-size: 12px;
  white-space: nowrap;
  z-index: 1001;
}

.notification-icon {
  text-decoration: none;
  color: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  transition: background-color 0.2s;
  cursor: pointer;
  position: relative;
  z-index: 10;
  font-size: 20px;
  margin-left: 10px;
}
.notification-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  background-color: red;
  border-radius: 50%;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.notification-modal {
  width: 400px;
  max-width: 90vw;
  padding: 0;
  display: flex;
  flex-direction: column;
  max-height: 80vh;
}

.modal-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.modal-body {
  padding: 0;
  overflow-y: auto;
  flex: 1;
}

.notification-list {
  display: flex;
  flex-direction: column;
}

.notification-item {
  padding: 15px 20px;
  border-bottom: 1px solid #f5f5f5;
  display: flex;
  gap: 15px;
  align-items: flex-start;
  transition: background 0.2s;
}

.notification-item.unread {
  background: #f0f7ff;
}

.notification-icon-small {
  font-size: 20px;
}

.notification-content {
  flex: 1;
  text-align: left;
}

.notification-text {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 14px;
  line-height: 1.4;
}

.notification-time {
  font-size: 12px;
  color: #999;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 10px;
}

.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.mark-read-btn {
  background: none;
  border: none;
  color: #1565c0;
  cursor: pointer;
  font-size: 14px;
}

.mark-read-btn:hover {
  text-decoration: underline;
}
</style>