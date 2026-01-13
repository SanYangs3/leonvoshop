<template>
  <div class="business-login-view">
    <!-- 右上角蓝色装饰 -->
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
          <!-- 商家图标 -->
          <svg viewBox="0 0 24 24" width="100" height="100" fill="#2196F3">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
          </svg>
        </div>
        <h2 class="left-title">BUSINESS LOGIN</h2>
        <div class="left-shadow"></div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="login-card">
        <div class="card-avatar">
          <img 
            :src="avatarUrl" 
            alt="Business Avatar" 
            @error="handleImageError"
            @load="handleImageLoad"
            :class="{ 'image-loading': isImageLoading }"
          >
        </div>
        
        <h3 class="card-title">商家中心</h3>

        <div class="login-form">
          <div class="input-group">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="#999"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
            </span>
            <input 
              type="text" 
              v-model="form.bname" 
              placeholder="商家名称"
              @keyup.enter="handleLogin"
              ref="nameInput"
            >
          </div>

          <div class="input-group">
            <span class="input-icon">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="#999"><path d="M12.65 10C11.83 7.67 9.61 6 7 6c-3.31 0-6 2.69-6 6s2.69 6 6 6c2.61 0 4.83-1.67 5.65-4H17v4h4v-4h2v-4H12.65zM7 14c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2z"/></svg>
            </span>
            <input 
              type="password" 
              v-model="form.password" 
              placeholder="密码"
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

          <!-- 返回首页链接 -->
          <div class="back-link">
            <a @click="goToHome" class="back-link-button">
              <span class="back-icon">←</span>
              返回商城首页
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'BusinessLogin',
  data() {
    return {
      form: {
        bname: '',      // 商家名称
        password: ''    // 密码
      },
      errorMessage: '',       // 错误信息
      isButtonDisabled: false, // 按钮禁用状态
      isLoading: false,       // 加载状态
      avatarUrl: '/business-avatar.png',  // 头像URL，指向public文件夹
      isImageLoading: false,  // 图片加载状态
      isImageLoaded: false    // 图片加载完成状态
    };
  },

  // 组件挂载时执行
  mounted() {
    // 自动聚焦到用户名输入框
    this.$nextTick(() => {
      this.$refs.nameInput?.focus();
    });

    // 检查是否已登录，如果已登录则跳转到仪表盘
    const isLoggedIn = localStorage.getItem('businessLoggedIn');
    const token = localStorage.getItem('businessToken');
    if (isLoggedIn === 'true' && token) {
      this.$router.push('/business/dashboard');
    }
  },

  methods: {
    /**
     * 处理头像加载错误
     */
    handleImageError(e) {
      // 避免无限循环，只处理一次错误
      if (e.target.src !== 'https://via.placeholder.com/150/2196F3/FFFFFF?text=Business') {
        e.target.src = 'https://via.placeholder.com/150/2196F3/FFFFFF?text=Business';
      }
      this.isImageLoading = false;
      this.isImageLoaded = false;
    },
    
    /**
     * 处理头像加载完成
     */
    handleImageLoad() {
      this.isImageLoading = false;
      this.isImageLoaded = true;
    },
    
    /**
     * 处理登录逻辑
     */
    async handleLogin() {
      // 1. 表单验证
      if (!this.form.bname || !this.form.password) {
        this.errorMessage = '请输入商家名称和密码';
        return;
      }

      // 2. 禁用按钮，防止重复提交
      this.isButtonDisabled = true;
      this.isLoading = true;

      try {
        console.log('开始商家登录:', this.form.bname);

        // 3. 调用登录API（接口4.8）
        const loginResponse = await axios.post(apiConfig.business.login, {
          bname: this.form.bname,
          password: this.form.password
        });

        console.log('登录响应:', loginResponse.data);

        // 4. 处理响应
        if (loginResponse.data.code === 1) {
          const businessData = loginResponse.data.data;

          // 5. 保存登录状态到本地存储
          localStorage.setItem('businessToken', 'business-token-' + Date.now());
          localStorage.setItem('businessInfo', JSON.stringify({
            bid: businessData.bid,        // 商家ID
            bname: businessData.bname,    // 商家名称
            // 保存其他商家信息
            contactPerson: businessData.contactPerson,
            contactPhone: businessData.contactPhone,
            contactEmail: businessData.contactEmail,
            address: businessData.address,
            description: businessData.description,
            status: businessData.status,
            regTime: businessData.regTime
          }));
          localStorage.setItem('businessLoggedIn', 'true');

          // 6. 清除错误信息
          this.errorMessage = '';

          // 7. 跳转到仪表盘
          setTimeout(() => {
            this.$router.push('/business/dashboard');
          }, 100);

        } else {
          // 登录失败
          this.errorMessage = loginResponse.data.message || '商家名称或密码错误！';
          this.form.password = '';  // 清空密码
          this.isButtonDisabled = false;
          this.isLoading = false;
        }
      } catch (error) {
        // 8. 错误处理
        console.error('商家登录失败:', error);

        if (error.response) {
          // 服务器响应错误
          const data = error.response.data;
          if (error.response.status === 401) {
            this.errorMessage = '商家名称或密码错误！';
          } else if (error.response.status === 404) {
            this.errorMessage = '商家不存在！';
          } else if (error.response.status === 403) {
            this.errorMessage = '商家账号已被禁用！';
          } else {
            this.errorMessage = data.message || `服务器错误: ${error.response.status}`;
          }
        } else if (error.request) {
          // 网络错误
          this.errorMessage = '网络连接失败，请检查网络或服务器！';
        } else {
          // 其他错误
          this.errorMessage = '请求配置错误，请联系管理员！';
        }

        // 重置状态
        this.form.password = '';
        this.isButtonDisabled = false;
        this.isLoading = false;
      }
    },

    goToHome() {
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.business-login-view {
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

/* 左侧面板 - 确保始终显示 */
.left-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  /* 移除响应式隐藏，始终显示左侧面板 */
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
  border-radius: 0;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1); /* 更柔和的阴影 */
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative; 
  margin-top: 0; /* 不需要下移 */
}

.card-avatar {
  /* 确保头像容器可见 */
  width: 100px;
  height: 100px;
  border-radius: 0;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  background: white;
  z-index: 20;
  margin-bottom: 20px; /* 给头像下方增加间距 */
  /* 确保头像容器显示 */
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e0e0;
}

.card-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  /* 确保图片不被隐藏 */
  display: block;
  /* 添加过渡效果 */
  transition: opacity 0.3s;
  /* 确保图片可见 */
  opacity: 1;
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
  border-radius: 0; /* 直角 */
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
  border-radius: 0; /* 直角 */
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
  border-radius: 0;
}

/* 测试账号提示 */
.login-tip {
  text-align: center;
  color: #718096;
  font-size: 14px;
  padding: 15px;
  background: #f8fafc;
  border-radius: 0;
  margin-top: 10px;
}

/* 返回链接 */
.back-link {
  text-align: center;
  margin-top: 15px;
}

.back-link-button {
  color: #2196F3;
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  background: none;
  border: none;
  font-size: 14px;
}

.back-link-button:hover {
  background: rgba(33, 150, 243, 0.1);
  transform: translateX(-3px);
}

/* 响应式适配 */
@media (max-width: 800px) {
  .content-wrapper {
    flex-direction: column;
    gap: 40px;
  }
  
  /* 小屏幕也显示左侧面板 */
  .left-panel {
    display: flex; /* 显示左侧面板 */
    margin-bottom: 20px;
  }
  
  .top-right-decor {
    width: 200px;
    height: 200px;
  }
  
  .left-title {
    font-size: 20px; /* 小屏幕减小字体 */
  }
  
  .left-icon svg {
    width: 80px; /* 小屏幕减小图标 */
    height: 80px;
  }
}
</style>