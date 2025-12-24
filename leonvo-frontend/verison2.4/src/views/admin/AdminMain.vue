<template>
  <div class="admin-main">
    <!-- 顶部导航栏 -->
    <header class="top-navigation">
      <div class="top-left">
        <div class="company-name">泡泡联盟管理</div>
      </div>

      <div class="top-right">
        <!-- 当前时间 -->
        <div class="current-time">
          <span class="time-icon">🕒</span>
          <span class="time-text">{{ currentTime }}</span>
        </div>

        <!-- 管理员信息 -->
        <div class="admin-info">
          <div class="admin-avatar">
            <img :src="adminAvatar" alt="管理员头像" class="avatar-img">
            <span class="online-dot"></span>
          </div>
          <div class="admin-details">
            <div class="admin-name">超级管理员</div>
            <div class="admin-status">在线</div>
          </div>
        </div>
      </div>
    </header>

    <div class="main-container">
      <!-- 左侧边栏 -->
      <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <!-- 管理员信息 -->
        <div class="sidebar-user-info">
          <div class="sidebar-avatar">
            <img :src="adminAvatar" alt="管理员头像" class="sidebar-avatar-img">
            <span class="sidebar-online-dot"></span>
          </div>
          <div class="sidebar-user-details" v-if="!sidebarCollapsed">
            <div class="sidebar-user-name">超级管理员</div>
            <div class="sidebar-user-status">在线</div>
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
      // 使用稳定的UI Avatars链接，确保圆形显示
      adminAvatar: '/admin-avatar.jpg',

      menuItems: [
        { id: 'dashboard', text: '仪表盘', icon: '📊', route: '/admin/dashboard' },
        { id: 'users', text: '用户管理', icon: '👥', route: '/admin/users' },
        { id: 'products', text: '商品管理', icon: '💻', route: '/admin/products' },
        { id: 'orders', text: '订单管理', icon: '📦', route: '/admin/orders' },
        { id: 'businesses', text: '商家管理', icon: '🏢', route: '/admin/businesses' },
        { id: 'comments', text: '评论管理', icon: '💬', route: '/admin/comments' },
        { id: 'settings', text: '系统设置', icon: '⚙️', route: '/admin/settings' }
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
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      })
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
    } else {
      this.$router.push('/admin/dashboard')
      this.activeMenu = 'dashboard'
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
/* 全局样式 */
.admin-main {
  min-height: 100vh;
  background: #ffffff;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止双滚动条 */
  width: 100%;
  margin: 0;
  padding: 0;
}

/* ========== 顶部导航栏 ========== */
.top-navigation {
  height: 60px;
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.top-left .company-name {
  color: white;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.top-right {
  display: flex;
  align-items: center;
  gap: 25px;
}

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
  font-size: 16px;
  color: #93c5fd;
}

.time-text {
  color: #e2e8f0;
  font-size: 14px;
  font-weight: 500;
}

/* 顶部管理员头像 */
.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 25px;
  transition: background 0.3s;
}

.admin-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.admin-avatar {
  position: relative;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

/* 确保圆形头像 */
.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;  /* 圆形 */
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  background: #10b981;
  border: 2px solid #1e40af;
  border-radius: 50%;
}

.admin-details {
  color: white;
}

.admin-name {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 2px;
}

.admin-status {
  font-size: 12px;
  color: #93c5fd;
}

/* ========== 主容器 ========== */
.main-container {
  display: flex;
  flex: 1;
  height: calc(100vh - 60px);
  overflow-y: auto;
  min-height: 0;
}

/* ========== 左侧边栏 ========== */
.sidebar {
  width: 240px;
  background: #000000 !important;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.sidebar.collapsed {
  width: 70px;
}

/* 侧边栏用户信息 */
.sidebar-user-info {
  background: #000000;
  padding: 25px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar-avatar {
  position: relative;
  width: 50px;
  height: 50px;
  flex-shrink: 0;
}

/* 侧边栏头像确保圆形 */
.sidebar-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;  /* 圆形 */
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.2);
  background: #1f2937;
}

.sidebar-online-dot {
  position: absolute;
  bottom: 3px;
  right: 3px;
  width: 12px;
  height: 12px;
  background: #10b981;
  border: 2px solid #000000;
  border-radius: 50%;
}

.sidebar-user-details {
  flex: 1;
  min-width: 0;
}

.sidebar-user-name {
  color: white;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-user-status {
  color: #9ca3af;
  font-size: 13px;
}

/* 导航菜单 */
.sidebar-nav {
  background: #000000;
  flex: 1;
  padding: 20px 0;
  //overflow-y: auto;
}

.sidebar-nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-nav li {
  margin: 4px 12px;
}

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
  background: #1a1a1a;
  color: white;
}

.sidebar-nav li.active a {
  background: #1a1a1a;
  color: white;
  border-left: 4px solid #3b82f6;
}

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

/* 折叠按钮 */
.sidebar-collapse-btn {
  position: absolute;
  right: -12px;
  top:20%;
  transform: translateY(-50%);
  width: 24px;
  height: 48px;
  background: #1a1a1a;
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
  background: #2d2d2d;
  width: 26px;
}

.arrow-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s;
}

.arrow-icon.collapsed {
  transform: rotate(180deg);
}

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

/* ========== 主内容区域 ========== */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  min-width: 0;
  height: 100%;
}

.content-wrapper {
  flex: 1;
  padding: 0;
  background: #ffffff;
  height: 100%;



}

/* 响应式设计 */
@media (max-width: 768px) {
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
</style>