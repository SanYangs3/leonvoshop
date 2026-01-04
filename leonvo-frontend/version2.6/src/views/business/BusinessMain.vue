<template>
  <div class="business-main">
    <!-- 顶部导航栏 -->
    <header class="top-navigation">
      <div class="top-left" :class="{ collapsed: sidebarCollapsed }">
        <div class="company-name">泡泡联盟商家中心</div>
      </div>
      
      <!-- 折叠按钮移动到这里，作为flex item紧接top-left -->
      <div class="sidebar-toggle-btn" @click="toggleSidebar">
        <svg viewBox="0 0 24 24" width="18" height="18" fill="white">
          <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
        </svg>
      </div>

      <div class="top-right">
        <!-- 当前时间 -->
        <div class="current-time">
          <span class="time-text">{{ currentTime }}</span>
        </div>

        <!-- 商家信息 -->
        <div class="admin-info">
          <div class="admin-avatar">
            <div class="avatar-text">{{ businessName.charAt(0) }}</div>
            <span class="online-dot"></span>
          </div>
          <div class="admin-details">
            <div class="admin-name">{{ businessName }}</div>
            <div class="admin-status">在线</div>
          </div>
        </div>

        <!-- 退出按钮 -->
        <div class="logout-btn" @click="handleLogout" title="退出登录">
          <span class="logout-text">退出</span>
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
                <span class="nav-icon" v-html="item.svg"></span>
                <span class="nav-text" v-if="!sidebarCollapsed">{{ item.text }}</span>
              </a>
            </li>
          </ul>
        </nav>
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
        { 
          id: 'dashboard', 
          text: '商家概况', 
          route: '/business/dashboard',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>'
        },
        { 
          id: 'products', 
          text: '商品管理', 
          route: '/business/products',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>'
        },
        { 
          id: 'orders', 
          text: '订单管理', 
          route: '/business/orders',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>'
        },
        { 
          id: 'statistics', 
          text: '销售统计', 
          route: '/business/statistics',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>'
        },
        { 
          id: 'profile', 
          text: '商家信息', 
          route: '/business/profile',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>'
        }
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
/* AdminLTE 风格样式 */
.business-main {
  min-height: 100vh;
  background: #ecf0f5;
  font-family: 'Source Sans Pro', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 100%;
}

/* ========== 顶部导航栏 ========== */
.top-navigation {
  height: 50px;
  background: #3c8dbc; /* AdminLTE 头部蓝 */
  display: flex;
  align-items: center;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1030;
}

.top-left {
  width: 230px;
  height: 100%;
  background: #367fa9; /* AdminLTE Logo区深蓝 */
  display: flex;
  align-items: center;
  padding-left: 15px;
  transition: width 0.3s;
  overflow: hidden;
  flex-shrink: 0;
}

/* 侧边栏收起时的logo区域样式 */
.top-left.collapsed {
  width: 50px;
  padding-left: 0;
  justify-content: center;
}

.top-left.collapsed .company-name {
  display: none;
}

/* 侧边栏收起时的toggle按钮位置 */
.top-left.collapsed + .sidebar-toggle-btn {
  margin-left: 0; /* 确保紧贴 */
}

.company-name {
  color: white;
  font-size: 20px;
  font-weight: 300;
  letter-spacing: 1px;
  margin-right: auto;
}

.sidebar-toggle-btn {
  width: 40px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.3s;
  margin-right: 10px;
}

.sidebar-toggle-btn:hover {
  background: rgba(0, 0, 0, 0.1);
}

.company-name b {
  font-weight: 700;
}

.top-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 20px;
  height: 100%;
}

.current-time {
  color: white;
  margin-right: 20px;
  font-size: 14px;
}

/* 顶部商家信息 */
.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 100%;
  padding: 0 15px;
  cursor: pointer;
  transition: background 0.3s;
}

.admin-info:hover {
  background: rgba(0, 0, 0, 0.1);
}

.admin-avatar {
  position: relative;
  width: 25px;
  height: 25px;
}

.avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 0;
  background: #3949ab;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 7px;
  height: 7px;
  background: #00a65a;
  border: 1px solid #3c8dbc;
  border-radius: 0;
}

.admin-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.admin-name {
  color: white;
  font-size: 14px;
}

.admin-status {
  display: none; /* 顶部不显示状态文字，仅头像红点 */
}

/* 退出按钮 */
.logout-btn {
  background: #dd4b39;
  color: white;
  padding: 5px 12px;
  border-radius: 0;
  font-size: 12px;
  margin-left: 15px;
  cursor: pointer;
  border: 1px solid #d73925;
}

.logout-btn:hover {
  background: #d73925;
}

/* ========== 主容器 ========== */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 50px);
  overflow: hidden;
}

/* 确保侧边栏背景为深黑灰色 */
.sidebar {
  width: 230px;
  background-color: #222d32 !important; /* AdminLTE 侧边栏深灰 */
  background: #222d32 !important;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  z-index: 810;
}

/* 导航菜单容器也强制背景色 */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  background: #222d32 !important;
  background-color: #222d32 !important;
}

.sidebar.collapsed {
  width: 50px;
}

/* 侧边栏用户信息 */
.sidebar-user-info {
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: transparent !important;
  margin-bottom: 10px;
}

.sidebar-avatar {
  position: relative;
  width: 45px;
  height: 45px;
  flex-shrink: 0;
}

.sidebar-avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 0;
  background: #3949ab;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
}

.sidebar-online-dot {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 9px;
  height: 9px;
  background: #00a65a;
  border: 1px solid #222d32;
  border-radius: 0;
}

.sidebar-user-details {
  color: white;
  overflow: hidden;
}

.sidebar-user-name {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 5px;
}

.sidebar-user-status {
  font-size: 11px;
  color: #b8c7ce;
}

.status-indicator {
  color: #00a65a;
  margin-right: 3px;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li a {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  color: #b8c7ce;
  text-decoration: none;
  border-left: 3px solid transparent;
  transition: all 0.3s;
  background: transparent !important;
}

.sidebar-nav li a:hover {
  background: #1e282c !important;
  color: white;
}

.sidebar-nav li.active a {
  background: #1e282c !important;
  color: white;
  border-left: none !important;
}

/* 导航图标和文字 */
.nav-icon {
  width: 20px;
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}

.nav-text {
  font-size: 14px;
}

/* 折叠状态下的样式 */
.sidebar.collapsed .nav-text,
.sidebar.collapsed .sidebar-user-details {
  display: none;
}

.sidebar.collapsed .sidebar-avatar {
  width: 30px;
  height: 30px;
  margin: 0 auto;
}

.sidebar.collapsed .nav-icon {
  margin-right: 0;
}

.sidebar.collapsed li a {
  justify-content: center;
  padding: 15px 0;
}

/* 移除旧的折叠按钮样式 */
.sidebar-collapse-btn {
  display: none;
}

/* ========== 主内容区域 ========== */
.main-content {
  flex: 1;
  background: #ecf0f5; /* AdminLTE 背景灰 */
  padding: 0;
  overflow-y: auto;
}

.content-wrapper {
  padding: 15px;
  min-height: 100%;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0,0,0,0.2);
  border-radius: 0;
}

::-webkit-scrollbar-track {
  background: transparent;
}
</style>