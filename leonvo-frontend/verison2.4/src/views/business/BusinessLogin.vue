<template>
  <div class="business-login-view">
    <!-- 渐变背景 -->
    <div class="login-container">
      <div class="login-card">
        <!-- 头部：标题和logo -->
        <div class="login-header">
          <div class="logo">
            <h1>泡泡联盟商家中心</h1>
          </div>
          <p class="subtitle">商家登录与管理</p>
        </div>

        <!-- 登录表单 -->
        <div class="login-form">
          <!-- 商家名称输入 -->
          <div class="form-group">
            <label for="bname">商家名称</label>
            <div class="input-wrapper">
              <input
                  type="text"
                  id="bname"
                  v-model="form.bname"
                  placeholder="请输入商家名称"
                  class="form-input"
                  @keyup.enter="handleLogin"
                  ref="nameInput"
              />
              <div class="input-line"></div>
            </div>
          </div>

          <!-- 密码输入 -->
          <div class="form-group">
            <label for="password">商家密码</label>
            <div class="input-wrapper">
              <input
                  type="password"
                  id="password"
                  v-model="form.password"
                  placeholder="请输入密码"
                  class="form-input"
                  @keyup.enter="handleLogin"
              />
              <div class="input-line"></div>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <!-- 登录按钮 -->
          <button
              @click="handleLogin"
              :disabled="isButtonDisabled"
              class="login-button"
              :class="{ 'button-disabled': isButtonDisabled }"
          >
            <span class="button-text">登录商家中心</span>
          </button>

          <!-- 测试账号提示 -->
          <div class="login-tip">
            商家测试账号: <strong>泡泡数码专营店</strong> 密码: <strong>123456</strong>
          </div>

          <!-- 返回首页链接 -->
          <div class="back-link">
            <a @click="goToHome" class="back-link-button">
              <span class="back-icon">←</span>
              返回商城首页
            </a>
          </div>
        </div>

        <!-- 系统信息 -->
        <div class="system-info">
          <div class="info-item">
            <span class="info-text">商家系统版本: V1.0</span>
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
      isLoading: false        // 加载状态
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
/* 1. 页面容器 */
.business-login-view {
  min-height: 100vh;  /* 占满整个视口 */
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);  /* 渐变背景 */
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 2. 登录卡片容器 */
.login-container {
  width: 100%;
  max-width: 420px;  /* 限制最大宽度 */
  position: relative;
  z-index: 2;
  margin-top: -60px;  /* 向上偏移，居中效果更好 */
}

/* 3. 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);  /* 半透明白色背景 */
  border-radius: 12px;
  padding: 45px 40px;
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.25);  /* 阴影效果 */
  border: 1px solid rgba(118, 75, 162, 0.4);
  backdrop-filter: blur(15px);  /* 毛玻璃效果 */
}

/* 4. 头部样式 */
.login-header {
  text-align: center;
  margin-bottom: 45px;
}

.login-header h1 {
  color: #4a5568;
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.subtitle {
  color: #718096;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

/* 5. 表单样式 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;  /* 表单元素间距 */
}

/* 6. 表单组 */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #4a5568;
  font-weight: 600;
  font-size: 14px;
}

/* 7. 输入框 */
.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 16px;
  background: #f8fafc;
  transition: all 0.3s ease;
  color: #2d3748;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 8. 输入框底部线效果 */
.input-line {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transition: width 0.3s ease;
}

.form-input:focus ~ .input-line {
  width: 100%;
}

/* 9. 错误提示 */
.error-message {
  color: #f56565;
  font-size: 14px;
  padding: 10px;
  background: rgba(245, 101, 101, 0.1);
  border-radius: 8px;
  text-align: center;
}

/* 10. 登录按钮 */
.login-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 18px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.login-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.5);
}

.button-disabled {
  background: #cbd5e0 !important;
  color: #a0aec0 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
  transform: none !important;
}

/* 11. 测试提示 */
.login-tip {
  text-align: center;
  color: #718096;
  font-size: 14px;
  padding: 15px;
  background: #f8fafc;
  border-radius: 8px;
}

/* 12. 返回链接 */
.back-link {
  text-align: center;
  margin-top: 20px;
}

.back-link-button {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  background: none;
  border: none;
  font-size: 16px;
}

.back-link-button:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(-5px);
}

/* 13. 系统信息 */
.system-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item {
  color: #718096;
  font-size: 12px;
}

/* 14. 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px;
  }

  .login-header h1 {
    font-size: 24px;
  }

  .form-input {
    padding: 14px;
  }
}
</style>