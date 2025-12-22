<template>
  <div class="admin-login-view">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <h1>泡泡联盟管理后台</h1>
          </div>
          <p class="subtitle">联想电脑商城管理系统</p>
        </div>

        <div class="login-form">
          <div class="form-group">
            <label for="username">

              管理员账号
            </label>
            <div class="input-wrapper">
              <input
                  type="text"
                  id="username"
                  v-model="form.username"
                  placeholder="请输入管理员账号"
                  class="form-input"
                  @keyup.enter="handleLogin"
                  ref="usernameInput"
              />
              <div class="input-line"></div>
            </div>
          </div>

          <div class="form-group">
            <label for="password">

              管理员密码
            </label>
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

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button
              @click="handleLogin"
              :disabled="isButtonDisabled"
              class="login-button"
              :class="{ 'button-disabled': isButtonDisabled }"
          >

            <span class="button-text">登录管理后台</span>
          </button>

          <div class="login-tip">

            默认测试账号: <strong>123</strong> 密码: <strong>123</strong>
          </div>

          <div class="back-link">
            <a @click="goToHome" class="back-link-button">
              <span class="back-icon">←</span>
              返回商城首页
            </a>
          </div>
        </div>

        <div class="system-info">
          <div class="info-item">

            <span class="info-text">系统版本: V2.0</span>
          </div>
          <div class="info-item">

            <span class="info-text">{{ currentTime }}</span>
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
  name: 'AdminLogin',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      errorMessage: '',
      currentTime: '',
      isButtonDisabled: false,
      isLoading: false
    };
  },
  mounted() {
    this.updateTime();
    setInterval(this.updateTime, 1000);
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
    updateTime() {
      // 保持不变
    },

    async handleLogin() {
      console.log('Login button clicked');

      if (!this.form.username || !this.form.password) {
        this.errorMessage = '请输入账号和密码';
        return;
      }

      // 禁用按钮防止重复点击
      this.isButtonDisabled = true;
      this.isLoading = true;

      try {
        console.log('Attempting login with:', this.form.username);

        // ✅ 使用 GET 请求验证登录
        const loginResponse = await axios.get(apiConfig.user.usernameLogin, {
          params: {
            username: this.form.username,
            password: this.form.password
          }
        });

        console.log('Login API response:', loginResponse.data);

        if (loginResponse.data.code === 1) {
          // 登录验证成功
          console.log('登录验证成功');

          // ✅ 直接保存登录状态，不需要获取用户详情
          localStorage.setItem('adminToken', 'admin-token-' + Date.now());
          localStorage.setItem('adminUser', JSON.stringify({
            username: this.form.username,
            loginTime: new Date().toISOString()
          }));
          localStorage.setItem('adminLoggedIn', 'true');

          this.errorMessage = '';

          // 跳转到管理后台
          setTimeout(() => {
            this.$router.push('/admin/dashboard');
          }, 100);

        } else {
          console.log('Login failed:', loginResponse.data.message);
          this.errorMessage = loginResponse.data.message || '账号或密码错误！';

          // 清空密码字段
          this.form.password = '';

          // 重新启用按钮
          this.isButtonDisabled = false;
          this.isLoading = false;
        }
      } catch (error) {
        console.error('登录失败:', error);

        // 根据错误类型显示不同的提示
        if (error.response) {
          // 请求已发送，服务器返回错误状态码
          const data = error.response.data;
          if (error.response.status === 401) {
            this.errorMessage = '账号或密码错误！';
          } else if (error.response.status === 403) {
            this.errorMessage = data.message || '没有权限！';
          } else {
            this.errorMessage = data.message || `服务器错误: ${error.response.status}`;
          }
        } else if (error.request) {
          // 请求已发送但无响应
          this.errorMessage = '网络连接失败，请检查网络或服务器！';
        } else {
          // 请求配置出错
          this.errorMessage = '请求配置错误，请联系管理员！';
        }

        // 清空密码字段
        this.form.password = '';

        // 重新启用按钮
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
.admin-login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 20px;
  position: relative;
  overflow: hidden;
  cursor: default; /* 确保默认光标正常 */
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 2;
  margin-top: -60px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 0;
  padding: 45px 40px;
  box-shadow: 0 15px 40px rgba(104, 183, 183, 0.25);
  border: 1px solid rgba(168, 218, 220, 0.4);  /* 淡蓝绿色边框 */
  backdrop-filter: blur(15px);
  pointer-events: auto; /* 确保可以交互 */
}

.login-header {
  text-align: center;
  margin-bottom: 45px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 10px;
}

.logo-icon {
  font-size: 36px;
  color: #2a9d8f;
}

.login-header h1 {
  color: #1a237e;
  margin: 0;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.subtitle {
  color: #666;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #1a237e;
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: text;
}

.label-icon {
  font-size: 16px;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 0;
  font-size: 16px;
  background: #f8f9ff;
  transition: all 0.3s ease;
  color: #333;
  cursor: text;
}

.form-input:focus {
  outline: none;
  border-color: #3949ab;
  background: white;
  box-shadow: 0 0 0 3px rgba(57, 73, 171, 0.1);
}

.form-input::placeholder {
  color: #aaa;
}

.input-line {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #3949ab, #5c6bc0);
  transition: width 0.3s ease;
}

.form-input:focus ~ .input-line {
  width: 100%;
}

.error-message {
  color: #f44336;
  font-size: 14px;
  padding: 10px;
  background: rgba(244, 67, 54, 0.1);
  border-radius: 0;
  text-align: center;
}

.login-button {
  background: linear-gradient(135deg, #3949ab 0%, #1a237e 100%);
  color: white;
  border: none;
  padding: 18px;
  border-radius: 0;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 10px;
  box-shadow: 0 8px 25px rgba(57, 73, 171, 0.4);
  position: relative;
  z-index: 10;
}

.login-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #5c6bc0 0%, #3949ab 100%);
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(57, 73, 171, 0.5);
}

.login-button:active:not(.button-disabled) {
  transform: translateY(0);
}

.button-disabled {
  background: #ccc !important;
  color: #999 !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
  transform: none !important;
}

.button-icon {
  font-size: 20px;
}

.login-tip {
  text-align: center;
  color: #666;
  font-size: 14px;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: default;
}

.tip-icon {
  font-size: 16px;
}

.back-link {
  text-align: center;
  margin-top: 20px;
}

.back-link-button {
  color: #3949ab;
  text-decoration: none;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 0;
  transition: all 0.3s ease;
  cursor: pointer;
  background: none;
  border: none;
  font-size: 16px;
}

.back-link-button:hover {
  background: rgba(57, 73, 171, 0.1);
  transform: translateX(-5px);
}

.back-icon {
  font-size: 18px;
  font-weight: bold;
}

.system-info {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: default;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 12px;
}

.info-icon {
  font-size: 14px;
}






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