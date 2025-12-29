<template>
  <div class="admin-main">
    <!-- 顶部导航栏 -->
    <header class="top-navigation">
      <div class="top-left" :class="{ collapsed: sidebarCollapsed }">
        <div class="company-name">Admin</div>
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

        <!-- 管理员信息 -->
        <div class="admin-info">
          <div class="admin-avatar">
            <img :src="adminAvatar" alt="Admin" class="avatar-img">
            <span class="online-dot"></span>
          </div>
          <div class="admin-details">
            <div class="admin-name">超级管理员</div>
            <div class="admin-status">在线</div>
          </div>
        </div>

        <!-- 退出按钮 -->
        <div class="logout-btn" @click="logout" title="退出登录">
          <span class="logout-text">退出</span>
        </div>
      </div>
    </header>

    <div class="main-container">
      <!-- 左侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <!-- 管理员信息 -->
        <div class="sidebar-user-info">
          <div class="sidebar-avatar">
            <img :src="adminAvatar" alt="Admin" class="sidebar-avatar-img">
            <span class="sidebar-online-dot"></span>
          </div>
          <div class="sidebar-user-details" v-if="!sidebarCollapsed">
            <div class="sidebar-user-name">超级管理员</div>
            <div class="sidebar-user-status">
              <span class="status-indicator">●</span> 在线
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

        <!-- 折叠按钮 -->
        <div class="sidebar-collapse-btn" @click="toggleSidebar">
          <div class="arrow-icon" :class="{ 'collapsed': sidebarCollapsed }">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="white">
              <path d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"/>
            </svg>
          </div>
        </div>
      </aside>

      <!-- 主内容区域 -->
      <main class="main-content">
        <div class="content-wrapper">
          <router-view></router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminMain',
  data() {
    return {
      sidebarCollapsed: false,
      activeMenu: 'dashboard',
      currentTime: '',
      adminAvatar: '/admin-avatar.jpg',
      
      // SVG 图标定义
      menuItems: [
        { 
          id: 'dashboard', 
          text: '仪表盘', 
          route: '/admin/dashboard',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>'
        },
        { 
          id: 'users', 
          text: '用户管理', 
          route: '/admin/users',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>'
        },
        { 
          id: 'products', 
          text: '商品管理', 
          route: '/admin/products',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>'
        },
        { 
          id: 'orders', 
          text: '订单管理', 
          route: '/admin/orders',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>'
        },
        { 
          id: 'businesses', 
          text: '商家管理', 
          route: '/admin/businesses',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z"/></svg>'
        },
        { 
          id: 'comments', 
          text: '评论管理', 
          route: '/admin/comments',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/></svg>'
        },
        { 
          id: 'settings', 
          text: '系统设置', 
          route: '/admin/settings',
          svg: '<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/></svg>'
        }
      ]
    }
  },
  computed: {
    activeMenuTitle() {
      const menu = this.menuItems.find(item => item.id === this.activeMenu)
      return menu ? menu.text : '仪表盘'
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    switchMenu(menuItem) {
      this.activeMenu = menuItem.id
      if (this.$route.path !== menuItem.route) {
        this.$router.push(menuItem.route)
      }
    },
    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
    },
    logout() {
      if (window.confirm('确定要退出登录吗？')) {
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUser');
        localStorage.removeItem('adminLoggedIn');
        window.location.href = '/admin/login';
      }
    }
  },
  mounted() {
    this.updateTime()
    this.timeInterval = setInterval(this.updateTime, 1000)
    const currentPath = this.$route.path
    const currentMenu = this.menuItems.find(item =>
        currentPath.startsWith(item.route) || currentPath === item.route
    )
    if (currentMenu) {
      this.activeMenu = currentMenu.id
    }
  },
  beforeUnmount() {
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
  },
  watch: {
    '$route'(to) {
      const currentMenu = this.menuItems.find(item =>
          to.path.startsWith(item.route) || to.path === item.route
      )
      if (currentMenu) {
        this.activeMenu = currentMenu.id
      }
    }
  }
}
</script>

<style scoped>
/* AdminLTE 风格样式 */
.admin-main {
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
  /* justify-content: space-between;  移除 space-between，让元素自然排列 */
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
  /* justify-content: center; */
  padding-left: 15px;
  transition: width 0.3s;
  overflow: hidden; /* 防止内容溢出 */
  flex-shrink: 0; /* 防止被压缩 */
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

/* 顶部管理员信息 */
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

.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
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
  border-radius: 50%;
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
  border-radius: 3px;
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

/* 绿色边框问题修复：移除任何可能导致绿色的边框或阴影 */
.admin-main, .sidebar, .sidebar-nav, .sidebar-nav ul, .sidebar-nav li, .sidebar-nav li a {
  border: none !important;
  box-shadow: none !important;
  outline: none !important;
}

/* 确保侧边栏背景为深黑灰色，非绿色 */
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
  background: transparent !important; /* 强制透明 */
  margin-bottom: 10px;
}

.sidebar-avatar {
  width: 45px;
  height: 45px;
  flex-shrink: 0;
}

.sidebar-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
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

/* 移除蓝色竖条 */
.sidebar-nav li a {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  color: #b8c7ce;
  text-decoration: none;
  border-left: 3px solid transparent;
  transition: all 0.3s;
  background: transparent !important; /* 默认背景透明 */
}

.sidebar-nav li a:hover {
  background: #1e282c !important;
  color: white;
}

.sidebar-nav li.active a {
  background: #1e282c !important;
  color: white;
  border-left: none !important; /* 强制移除左边框 */
}

/* 移除之前的折叠按钮相关样式 */
/* .sidebar-collapse-btn { ... } */

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

.sidebar.collapsed .nav-text,
.sidebar.collapsed .sidebar-user-info {
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

/* 移除底部旧的折叠按钮样式 */
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
  border-radius: 4px;
}

::-webkit-scrollbar-track {
  background: transparent;
}
</style>