<template>
  <div class="business-main">
    <!-- 顶部导航栏 -->
    <header class="top-navigation">
      <div class="top-left">
        <div class="company-name">泡泡联盟商家中心</div>
      </div>

      <div class="top-right">
        <!-- 当前时间显示 -->
        <div class="current-time">
          <span class="time-icon">🕒</span>
          <span class="time-text">{{ currentTime }}</span>
        </div>

        <!-- 优化后的商家信息 -->
        <div class="business-info-optimized">
          <!-- 商家信息容器 -->
          <div class="business-info-wrapper">
            <div class="business-avatar">
              <span class="avatar-text">{{ businessName.charAt(0) }}</span>
              <!-- 在线状态指示器 -->
              <span class="online-indicator" :class="{ online: isOnline }"></span>
            </div>

            <div class="business-details-optimized">
              <!-- 商家名称 - 支持长名称显示 -->
              <div class="business-name-optimized" :title="businessName">
                {{ truncateText(businessName, 10) }}
              </div>

              <!-- 在线状态和ID -->
              <div class="business-meta">
                <span class="status-badge-optimized" :class="{
                  online: isOnline,
                  offline: !isOnline
                }">
                  {{ isOnline ? '在线' : '离线' }}
                </span>
                <span class="business-id">ID: {{ businessId }}</span>
              </div>
            </div>
          </div>

          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <div class="main-container">
      <!-- 左侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <!-- 商家头像和信息 -->
        <div class="sidebar-user-info">
          <div class="sidebar-avatar">
            <span class="sidebar-avatar-text">{{ businessName.charAt(0) }}</span>
            <!-- 侧边栏在线状态指示器 -->
            <span class="sidebar-online-indicator" :class="{ online: isOnline }"></span>
          </div>
          <div class="sidebar-user-details" v-if="!sidebarCollapsed">
            <div class="sidebar-user-name">{{ truncateText(businessName, 12) }}</div>
            <div class="sidebar-user-meta">
              <span class="sidebar-status-badge" :class="{
                online: isOnline,
                offline: !isOnline
              }">
                {{ isOnline ? '在线' : '离线' }}
              </span>
              <span class="sidebar-user-id">ID: {{ businessId }}</span>
            </div>
          </div>
        </div>

        <!-- 导航菜单 -->
        <nav class="sidebar-nav">
          <ul>
            <li v-for="item in menuItems"
                :key="item.id"
                :class="{ active: activeMenu === item.id }"
                @click="switchMenu(item)">
              <a href="#" @click.prevent>
                <span class="nav-icon">{{ item.icon }}</span>
                <span class="nav-text" v-if="!sidebarCollapsed">{{ item.text }}</span>
              </a>
            </li>
          </ul>
        </nav>

        <!-- 折叠按钮 -->
        <div class="sidebar-collapse-btn" @click="toggleSidebar">
          <div class="arrow-icon" :class="{ 'collapsed': sidebarCollapsed }">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z" fill="#cbd5e0"/>
            </svg>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 - 路由出口 -->
      <main class="main-content">
        <div class="content-wrapper">
          <router-view></router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'BusinessMain',
  data() {
    return {
      sidebarCollapsed: false,
      activeMenu: 'dashboard',
      currentTime: '',
      businessName: '',
      businessId: '',
      isOnline: true,

      menuItems: [
        { id: 'dashboard', text: '商家概况', icon: '📊', route: '/business/dashboard' },
        { id: 'products', text: '商品管理', icon: '📦', route: '/business/products' },
        { id: 'orders', text: '订单管理', icon: '📋', route: '/business/orders' },
        { id: 'statistics', text: '销售统计', icon: '📈', route: '/business/statistics' },
        { id: 'profile', text: '商家信息', icon: '👤', route: '/business/profile' }
      ]
    }
  },

  mounted() {
    // 1. 检查登录状态
    this.checkLoginStatus();

    // 2. 初始化时间
    this.updateTime();
    this.timeInterval = setInterval(this.updateTime, 1000);

    // 3. 根据当前路由设置激活菜单
    this.setActiveMenu();

    // 4. 加载商家信息
    this.loadBusinessInfo();
  },

  beforeUnmount() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval);
    }
  },

  methods: {
    /**
     * 检查登录状态
     */
    async checkLoginStatus() {
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      const token = localStorage.getItem('businessToken');

      if (!businessInfo || !token) {
        this.$router.push('/business/login');
        return;
      }

      // 可选：验证token有效性
      try {
        // 这里可以调用API验证token
        // const response = await axios.get(apiConfig.business.getProfile(businessInfo.bid));
      } catch (error) {
        console.error('登录验证失败:', error);
        // 如果验证失败，跳转到登录页面
        this.handleLogout();
      }
    },

    /**
     * 设置激活菜单
     */
    setActiveMenu() {
      const currentPath = this.$route.path;
      const currentMenu = this.menuItems.find(item =>
          currentPath.startsWith(item.route) || currentPath === item.route
      );

      if (currentMenu) {
        this.activeMenu = currentMenu.id;
      } else {
        // 默认跳转到仪表盘
        this.$router.push('/business/dashboard');
        this.activeMenu = 'dashboard';
      }
    },

    /**
     * 切换侧边栏折叠状态
     */
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed;
    },

    /**
     * 切换菜单
     */
    switchMenu(menuItem) {
      this.activeMenu = menuItem.id;
      if (this.$route.path !== menuItem.route) {
        this.$router.push(menuItem.route);
      }
    },

    /**
     * 更新时间显示
     */
    updateTime() {
      const now = new Date();
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      });
    },

    /**
     * 处理退出登录
     */
    handleLogout() {
      if (confirm('确定要退出商家中心吗？')) {
        // 清除本地存储的商家信息
        localStorage.removeItem('businessToken');
        localStorage.removeItem('businessInfo');
        localStorage.removeItem('businessLoggedIn');

        // 跳转到商家登录页面
        this.$router.push('/business/login');
      }
    },

    /**
     * 加载商家信息
     */
    async loadBusinessInfo() {
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      if (businessInfo) {
        try {
          this.businessName = businessInfo.bname || businessInfo.name || '未命名商家';
          this.businessId = businessInfo.bid || businessInfo.id || 'N/A';

          // 如果需要从API获取最新信息
          // await this.refreshBusinessInfo(businessInfo.bid);
        } catch (error) {
          console.error('加载商家信息失败:', error);
        }
      }
    },

    /**
     * 从API刷新商家信息
     */
    async refreshBusinessInfo(bid) {
      try {
        const response = await axios.get(apiConfig.business.getProfile(bid));
        if (response.data.code === 1) {
          const freshInfo = response.data.data;
          this.businessName = freshInfo.bname || freshInfo.name;
          this.businessId = freshInfo.bid || freshInfo.id;

          // 更新本地存储
          localStorage.setItem('businessInfo', JSON.stringify({
            ...JSON.parse(localStorage.getItem('businessInfo') || '{}'),
            ...freshInfo
          }));
        }
      } catch (error) {
        console.error('刷新商家信息失败:', error);
      }
    },

    /**
     * 截断文本，防止过长
     */
    truncateText(text, maxLength) {
      if (!text) return '未命名';
      if (text.length <= maxLength) return text;
      return text.substring(0, maxLength) + '...';
    }
  },

  watch: {
    '$route'(to) {
      this.setActiveMenu();
    }
  }
}
</script>

<style scoped>
/* 1. 主容器 */
.business-main {
  min-height: 100vh;
  background: #ffffff;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 100%;
  margin: 0;
  padding: 0;
}

/* 2. 顶部导航栏 */
.top-navigation {
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);  /* 渐变背景 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
}

.company-name {
  color: white;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 3. 时间显示 */
.current-time {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 15px;
  border-radius: 20px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.time-icon {
  color: #e2e8f0;
  font-size: 14px;
}

.time-text {
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
}

/* 4. 优化后的商家信息区域 */
.business-info-optimized {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 10px;
  border-radius: 25px;
  transition: background 0.3s;
  min-width: 0;
  max-width: 300px;
}

.business-info-optimized:hover {
  background: rgba(255, 255, 255, 0.1);
}

.business-info-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  flex: 1;
}

/* 5. 商家头像 */
.business-avatar {
  position: relative;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

.avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

/* 6. 在线状态指示器 */
.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: 2px solid white;
  background: #cbd5e0; /* 默认离线状态 */
  transition: background 0.3s;
}

.online-indicator.online {
  background: #48bb78; /* 在线状态绿色 */
  box-shadow: 0 0 5px #48bb78;
}

/* 7. 商家详情区域 */
.business-details-optimized {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  flex: 1;
}

.business-name-optimized {
  color: white;
  font-weight: 600;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
  cursor: default;
}

.business-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 8. 状态徽章 */
.status-badge-optimized {
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 10px;
  font-weight: 500;
  white-space: nowrap;
  transition: all 0.3s;
}

.status-badge-optimized.online {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
  border: 1px solid rgba(72, 187, 120, 0.3);
}

.status-badge-optimized.offline {
  background: rgba(203, 213, 224, 0.2);
  color: #cbd5e0;
  border: 1px solid rgba(203, 213, 224, 0.3);
}

.business-id {
  color: rgba(255, 255, 255, 0.7);
  font-size: 11px;
  white-space: nowrap;
}

/* 9. 退出按钮 */
.logout-btn {
  margin-left: 10px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
  flex-shrink: 0;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

/* 10. 主容器布局 */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px);  /* 减去顶部导航栏高度 */
  overflow-y: auto;
  min-height: 0;
}

/* 11. 侧边栏 */
.sidebar {
  width: 240px;
  background: #2d3748 !important;  /* 深色背景 */
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);  /* 平滑过渡 */
  position: relative;
}

.sidebar.collapsed {
  width: 70px;  /* 折叠后的宽度 */
}

/* 12. 侧边栏用户信息 */
.sidebar-user-info {
  background: #2d3748;
  padding: 25px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: relative;
}

.sidebar-avatar {
  position: relative;
  width: 50px;
  height: 50px;
  flex-shrink: 0;
}

.sidebar-avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: bold;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.sidebar-online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #2d3748;
  background: #cbd5e0;
  transition: background 0.3s;
}

.sidebar-online-indicator.online {
  background: #48bb78;
  box-shadow: 0 0 6px #48bb78;
}

.sidebar-user-details {
  flex: 1;
  min-width: 0;
}

.sidebar-user-name {
  color: white;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-user-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.sidebar-status-badge {
  padding: 3px 8px;
  font-size: 11px;
  border-radius: 10px;
  font-weight: 500;
  white-space: nowrap;
  display: inline-block;
  width: fit-content;
}

.sidebar-status-badge.online {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
  border: 1px solid rgba(72, 187, 120, 0.3);
}

.sidebar-status-badge.offline {
  background: rgba(203, 213, 224, 0.2);
  color: #cbd5e0;
  border: 1px solid rgba(203, 213, 224, 0.3);
}

.sidebar-user-id {
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
}

/* 13. 侧边栏导航 */
.sidebar-nav {
  background: #2d3748;
  flex: 1;
  padding: 20px 0;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  margin: 4px 12px;
}

/* 14. 导航菜单项 */
.sidebar-nav li a {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  color: #d1d5db;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s;
  white-space: nowrap;
  background: transparent;
}

.sidebar-nav li a:hover {
  background: #4a5568;
  color: white;
}

/* 15. 激活菜单样式 */
.sidebar-nav li.active a {
  background: #4a5568;
  color: white;
  border-left: 4px solid #667eea;
}

/* 16. 导航图标和文字 */
.nav-icon {
  font-size: 20px;
  margin-right: 15px;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.nav-text {
  font-size: 15px;
  font-weight: 500;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 17. 折叠按钮 */
.sidebar-collapse-btn {
  position: absolute;
  right: -12px;
  top: 20%;
  transform: translateY(-50%);
  width: 24px;
  height: 48px;
  background: #4a5568;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-left: none;
  border-radius: 0 8px 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  z-index: 10;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
}

.sidebar-collapse-btn:hover {
  background: #5a6578;
  width: 26px;
}

/* 18. 箭头图标 */
.arrow-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s;
}

.arrow-icon.collapsed {
  transform: rotate(180deg);  /* 折叠时旋转180度 */
}

/* 19. 折叠状态下的样式 */
.sidebar.collapsed .nav-text,
.sidebar.collapsed .sidebar-user-details {
  display: none;
}

.sidebar.collapsed .nav-icon {
  margin-right: 0;
  font-size: 22px;
}

.sidebar.collapsed .sidebar-user-info {
  justify-content: center;
  padding: 25px 10px;
}

/* 20. 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f7fafc;  /* 浅灰色背景 */
  min-width: 0;
  height: 100%;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  background: #f7fafc;
  height: 100%;
  overflow-y: auto;
}

/* 21. 响应式设计 */
@media (max-width: 1024px) {
  .business-name-optimized {
    max-width: 120px;
  }

  .top-navigation {
    padding: 0 20px;
  }
}

@media (max-width: 768px) {
  .business-info-optimized {
    max-width: 200px;
  }

  .business-name-optimized {
    max-width: 80px;
  }

  .business-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;
  }

  .business-id {
    font-size: 10px;
  }

  .current-time {
    display: none;
  }

  .company-name {
    font-size: 18px;
  }

  .top-navigation {
    padding: 0 15px;
  }

  /* 移动端侧边栏优化 */
  .sidebar {
    position: fixed;
    left: -240px;
    top: 60px;
    bottom: 0;
    z-index: 90;
  }

  .sidebar.show {
    left: 0;
  }

  .sidebar.collapsed {
    width: 240px;
  }

  .sidebar.collapsed .nav-text,
  .sidebar.collapsed .sidebar-user-details {
    display: block;
  }

  .sidebar-collapse-btn {
    display: none;
  }
}

@media (max-width: 480px) {
  .business-info-optimized {
    max-width: 180px;
  }

  .business-name-optimized {
    max-width: 60px;
    font-size: 13px;
  }

  .company-name {
    font-size: 16px;
  }

  .logout-btn {
    padding: 4px 8px;
    font-size: 11px;
  }
}
</style>