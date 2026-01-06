<template>
  <div class="admin-login-view">
    <!-- 右上角蓝色圆角装饰 -->
    <div class="top-right-decor"></div>
    
    <!-- 底部波浪装饰 -->
    <div class="bottom-wave">
      <svg viewBox="0 0 1440 320" preserveAspectRatio="none">
        <path fill="#2196F3" fill-opacity="1" d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
      </svg>
    </div>

    <!-- 主内容容器 -->
    <div class="content-wrapper">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <div class="left-icon">
          <!-- 简化的用户图标 -->
          <svg viewBox="0 0 24 24" width="100" height="100" fill="#2196F3">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
          </svg>
        </div>
        <h2 class="left-title">ADMIN LOGIN</h2>
        <div class="left-shadow"></div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="login-card">
        <div class="card-avatar">
          <img src="/admin-avatar.jpg" alt="Admin Avatar" @error="handleImageError">
        </div>
        
        <h3 class="card-title">管理后台</h3>

        <div class="login-form">
          <div class="input-group">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="#999"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
            </span>
            <input 
              type="text" 
              v-model="form.username" 
              placeholder="username"
              @keyup.enter="handleLogin"
              ref="usernameInput"
            >
          </div>

          <div class="input-group">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="#999"><path d="M12.65 10C11.83 7.67 9.61 6 7 6c-3.31 0-6 2.69-6 6s2.69 6 6 6c2.61 0 4.83-1.67 5.65-4H17v4h4v-4h2v-4H12.65zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"/></svg>
            </span>
            <input 
              type="password" 
              v-model="form.password" 
              placeholder="password"
              @keyup.enter="handleLogin"
            >
          </div>

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button 
            class="login-btn" 
            :disabled="isButtonDisabled"
            @click="handleLogin"
          >
            {{ isButtonDisabled ? '登录中...' : 'LOGIN' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'AdminLogin',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      errorMessage: '',
      isButtonDisabled: false,
      isLoading: false
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.$refs.usernameInput?.focus();
    });

    // 如果已登录，跳转到后台
    const isLoggedIn = localStorage.getItem('adminLoggedIn');
    const token = localStorage.getItem('adminToken');
    if (isLoggedIn === 'true' && token) {
      this.$router.push('/admin/dashboard');
    }
  },
  methods: {
    handleImageError(e) {
      // 如果头像加载失败，使用默认占位图
      e.target.src = 'https://via.placeholder.com/150/2196F3/FFFFFF?text=Admin';
    },
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        this.errorMessage = '请输入账号和密码';
        return;
      }

      this.isButtonDisabled = true;
      this.isLoading = true;
      this.errorMessage = '';

      try {
        const loginResponse = await axios.get(apiConfig.user.usernameLogin, {
          params: {
            username: this.form.username,
            password: this.form.password
          }
        });

        if (loginResponse.data.code === 1) {
          localStorage.setItem('adminToken', 'admin-token-' + Date.now());
          localStorage.setItem('adminUser', JSON.stringify({
            username: this.form.username,
            loginTime: new Date().toISOString()
          }));
          localStorage.setItem('adminLoggedIn', 'true');
          
          this.$router.push('/admin/dashboard');
        } else {
          this.errorMessage = loginResponse.data.message || '账号或密码错误！';
          this.form.password = '';
        }
      } catch (error) {
        console.error('登录失败:', error);
        this.errorMessage = '登录请求失败，请检查网络';
        this.form.password = '';
      } finally {
        this.isButtonDisabled = false;
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.admin-login-view {
  min-height: 100vh;
  width: 100%;
  background-color: #f0f4f8; /* 浅蓝灰色背景 */
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-family: 'Arial', sans-serif;
}

/* 右上角蓝色装饰 - 调整为更接近设计图 */
.top-right-decor {
  position: absolute;
  top: 0;
  right: 0;
  width: 40vw; /* 响应式宽度 */
  height: 50vh; /* 响应式高度 */
  max-width: 500px;
  max-height: 500px;
  background-color: #2196F3;
  border-bottom-left-radius: 100%;
  z-index: 1;
}

/* 底部波浪 */
.bottom-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  line-height: 0;
  z-index: 1;
}

.bottom-wave svg {
  width: 100%;
  height: auto;
  min-height: 150px;
}

/* 内容容器 */
.content-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 100px; /* 增加间距 */
  z-index: 10; /* 确保在最上层 */
  width: 100%;
  max-width: 1200px;
  padding: 20px;
}

/* 左侧面板 */
.left-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
}

.left-icon {
  margin-bottom: 15px;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
}

.left-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  letter-spacing: 1px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin: 0;
}

.left-shadow {
  height: 4px;
  width: 100px;
  background: radial-gradient(ellipse at center, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0) 70%);
  margin-top: 10px;
}

/* 右侧登录卡片 */
.login-card {
  background: white;
  width: 360px;
  padding: 40px; /* 恢复正常的 padding */
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1); /* 更柔和的阴影 */
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative; 
  margin-top: 0; /* 不需要下移 */
}

.card-avatar {
  /* position: absolute;  移除绝对定位 */
  /* top: -50px; */
  /* left: 50%; */
  /* transform: translateX(-50%); */
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  /* border: 5px solid white; */
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  background: white;
  z-index: 20;
  margin-bottom: 20px; /* 给头像下方增加间距 */
}

.card-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  margin-top: 10px;
}

.login-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
}

.input-group input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 1px solid #e0e0e0;
  border-radius: 25px; /* 胶囊圆角 */
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  background-color: white;
  color: #333;
}

.input-group input:focus {
  border-color: #2196F3;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

.input-group input::placeholder {
  color: #bbb;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #0069D9; /* 鲜艳的蓝色 */
  color: white;
  border: none;
  border-radius: 25px; /* 胶囊圆角 */
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 10px;
  box-shadow: 0 4px 15px rgba(0, 105, 217, 0.3);
}

.login-btn:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(0, 105, 217, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(1px);
}

.login-btn:disabled {
  background-color: #ccc;
  box-shadow: none;
  cursor: not-allowed;
}

.error-message {
  color: #ff3b30;
  font-size: 12px;
  text-align: center;
  background: rgba(255, 59, 48, 0.1);
  padding: 8px;
  border-radius: 8px;
}

/* 响应式适配 */
@media (max-width: 800px) {
  .content-wrapper {
    flex-direction: column;
    gap: 40px;
  }
  
  .left-panel {
    display: none; /* 小屏幕隐藏左侧 */
  }
  
  .top-right-decor {
    width: 200px;
    height: 200px;
  }
}
</style>