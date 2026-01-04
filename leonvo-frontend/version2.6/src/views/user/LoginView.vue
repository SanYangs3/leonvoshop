<template>
  <div class="login-view">
    <div class="login-container">
      <!-- 返回首页 -->
      <div class="back-home">
        <router-link to="/" class="back-link">
          <span class="back-icon">←</span>
          返回首页
        </router-link>
      </div>

      <!-- 登录框 -->
      <div class="login-box">
        <div class="login-header">
          <h1>用户登录</h1>
          <p class="subtitle">欢迎来到泡泡联盟联想电脑商城</p>
        </div>

        <div class="login-form">
          <!-- 登录方式选择 -->
          <div class="login-method">
            <button
                @click="loginMethod = 'account'"
                :class="{ 'active': loginMethod === 'account' }"
                class="method-btn"
            >
              账号登录
            </button>
            <button
                @click="loginMethod = 'phone'"
                :class="{ 'active': loginMethod === 'phone' }"
                class="method-btn"
            >
              手机登录
            </button>
            <button
                @click="loginMethod = 'email'"
                :class="{ 'active': loginMethod === 'email' }"
                class="method-btn"
            >
              邮箱登录
            </button>
          </div>

          <!-- 账号登录 -->
          <div v-if="loginMethod === 'account'" class="form-group">
            <label for="username">用户名：</label>
            <div class="input-wrapper">
              <input
                  type="text"
                  id="username"
                  v-model="accountInfo.username"
                  placeholder="请输入用户名"
                  class="form-input"
                  @keyup.enter="handleLogin"
              />
            </div>
          </div>

          <!-- 手机登录 -->
          <div v-if="loginMethod === 'phone'" class="form-group">
            <label for="phone">手机号：</label>
            <div class="input-wrapper">
              <input
                  type="tel"
                  id="phone"
                  v-model="phoneInfo.phone"
                  placeholder="请输入手机号"
                  class="form-input"
                  @keyup.enter="handleLogin"
              />
            </div>
          </div>

          <!-- 邮箱登录 -->
          <div v-if="loginMethod === 'email'" class="form-group">
            <label for="email">邮箱：</label>
            <div class="input-wrapper">
              <input
                  type="email"
                  id="email"
                  v-model="emailInfo.email"
                  placeholder="请输入邮箱"
                  class="form-input"
                  @keyup.enter="handleLogin"
              />
            </div>
          </div>

          <!-- 密码 -->
          <div class="form-group">
            <label for="password">密码：</label>
            <div class="input-wrapper">
              <input
                  type="password"
                  id="password"
                  v-model="password"
                  placeholder="请输入密码"
                  class="form-input"
                  @keyup.enter="handleLogin"
              />
            </div>
          </div>

          <!-- 记住我 -->
          <div class="form-group remember">
            <label class="checkbox-label">
              <input
                  type="checkbox"
                  id="remember"
                  v-model="rememberMe"
                  class="checkbox"
              />
              <span class="checkmark"></span>
              <span class="checkbox-text">记住我</span>
            </label>
          </div>

          <!-- 登录按钮 -->
          <button
              @click="handleLogin"
              :disabled="isButtonDisabled || isLoading"
              class="main-button"
              :class="{ 'button-disabled': isButtonDisabled || isLoading }"
          >
            {{ isLoading ? '登录中...' : '登录' }}
          </button>

          <!-- 跳转到注册页面 -->
          <div class="mode-switch">
            <span>还没有账号？</span>
            <router-link to="/register" class="switch-link">
              邮箱验证注册
            </router-link>
            <span class="switch-divider">|</span>
            <router-link to="/register/sms" class="switch-link">
              短信验证注册
            </router-link>
          </div>

          <!-- 测试账号提示 -->
          <div v-if="loginMethod === 'account'" class="test-account">
            <div class="test-header">
              <div class="test-icon">🧪</div>
              <div class="test-title">测试账号</div>
            </div>
            <p class="test-subtitle">
              密码均为 <span class="password-hint">123456</span>
            </p>
            <div class="bubble-accounts">
              <div
                  class="bubble-account green-bubble"
                  @click="fillAccount('小绿泡泡')"
              >
                <div class="bubble-icon">🟢</div>
                <div class="bubble-name">小绿泡泡</div>
              </div>
              <div
                  class="bubble-account blue-bubble"
                  @click="fillAccount('小蓝泡泡')"
              >
                <div class="bubble-icon">🔵</div>
                <div class="bubble-name">小蓝泡泡</div>
              </div>
              <div
                  class="bubble-account yellow-bubble"
                  @click="fillAccount('小黄泡泡')"
              >
                <div class="bubble-icon">🟡</div>
                <div class="bubble-name">小黄泡泡</div>
              </div>
              <div
                  class="bubble-account red-bubble"
                  @click="fillAccount('小红泡泡')"
              >
                <div class="bubble-icon">❤️</div>
                <div class="bubble-name">小红泡泡</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 成功提示 -->
      <div v-if="showSuccess" class="success-message">
        <div class="success-content">
          <div class="success-icon">✅</div>
          <h3>登录成功！</h3>
          <p>欢迎 {{ currentUsername }} 🎉</p>
          <div class="welcome-bubble" :class="getBubbleColor(currentUsername)">
            {{ currentUsername }}
          </div>
          <div class="success-desc">
            <span class="desc-icon">🌿</span>
            正在跳转回首页...
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
// 从外部文件导入API配置
import apiConfig from '@/config/api.config'

export default {
  name: 'LoginView',
  data() {
    return {
      loginMethod: 'account',
      accountInfo: { username: '' },
      phoneInfo: { phone: '' },
      emailInfo: { email: '' },
      password: '',
      rememberMe: false,
      showSuccess: false,
      currentUsername: '',
      isLoading: false
    };
  },
  computed: {
    isButtonDisabled() {
      if (this.loginMethod === 'account') {
        return !this.accountInfo.username.trim() || !this.password;
      } else if (this.loginMethod === 'phone') {
        return !this.phoneInfo.phone.trim() || !this.password;
      } else {
        return !this.emailInfo.email.trim() || !this.password;
      }
    },
    // 获取登录接口URL
    loginUrl() {
      if (this.loginMethod === 'account') {
        return apiConfig.user.usernameLogin;
      } else if (this.loginMethod === 'phone') {
        return apiConfig.user.phoneLogin;
      } else {
        return apiConfig.user.emailLogin;
      }
    }
  },
  methods: {
    async handleLogin() {
      if (this.isButtonDisabled || this.isLoading) return;

      this.isLoading = true;

      try {
        let params = {};

        // 根据登录方式设置参数
        if (this.loginMethod === 'account') {
          params = {
            username: this.accountInfo.username.trim(),
            password: this.password
          };
        } else if (this.loginMethod === 'phone') {
          params = {
            phone: this.phoneInfo.phone.trim(),
            password: this.password
          };
        } else {
          params = {
            email: this.emailInfo.email.trim(),
            password: this.password
          };
        }

        console.log('登录请求URL:', this.loginUrl);
        console.log('登录参数:', params);

        // 调用后端登录接口 - 使用GET请求，参数放在URL查询字符串中
        const response = await axios.get(this.loginUrl, { params });

        console.log('登录响应:', response.data);

        // 关键修改：后端返回的code是1表示成功，而不是200
        if (response.data && response.data.code === 1) {
          // 登录成功
          this.handleLoginSuccess(params);
        } else {
          // 登录失败
          this.handleLoginFailure(response.data);
        }
      } catch (error) {
        console.error('登录请求失败:', error);
        console.error('错误详情:', error.response);
        this.handleLoginError(error);
      } finally {
        this.isLoading = false;
      }
    },

    handleLoginSuccess(loginData) {
      // 获取用户名（根据不同的登录方式）
      let username = '';
      if (this.loginMethod === 'account') {
        username = loginData.username;
      } else if (this.loginMethod === 'phone') {
        username = loginData.phone;
      } else {
        // 邮箱登录，取@前面的部分作为显示名
        username = loginData.email.split('@')[0];
      }

      this.currentUsername = username;

      // 清空之前的购物车数据
      localStorage.removeItem('lenovo_cart');

      // 保存登录状态
      localStorage.setItem('isLoggedIn', 'true');
      localStorage.setItem('username', username);
      localStorage.setItem('loginMethod', this.loginMethod);

      // 保存具体的登录信息，方便后续使用
      if (this.loginMethod === 'account') {
        localStorage.setItem('loginUsername', loginData.username);
      } else if (this.loginMethod === 'phone') {
        localStorage.setItem('loginPhone', loginData.phone);
      } else {
        localStorage.setItem('loginEmail', loginData.email);
      }

      if (this.rememberMe) {
        localStorage.setItem('rememberMe', 'true');
      } else {
        localStorage.removeItem('rememberMe');
      }

      this.showSuccess = true;

      // 2秒后跳转到首页
      setTimeout(() => {
        this.$router.push('/');
      }, 2000);
    },

    handleLoginFailure(responseData) {
      console.log('登录失败响应:', responseData);

      let errorMsg = '登录失败！\n\n';

      if (responseData && responseData.msg) {
        errorMsg += responseData.msg + '\n\n';
      }

      if (this.loginMethod === 'account') {
        errorMsg += '可能的原因：\n';
        errorMsg += '1. 用户名或密码错误\n';
        errorMsg += '2. 用户不存在\n';
        errorMsg += '3. 账户已被注销\n\n';
        errorMsg += '可用测试账号：\n';
        errorMsg += '🟢 小绿泡泡 / 123456\n';
        errorMsg += '🔵 小蓝泡泡 / 123456\n';
        errorMsg += '🟡 小黄泡泡 / 123456\n';
        errorMsg += '❤️ 小红泡泡 / 123456';
      } else if (this.loginMethod === 'phone') {
        errorMsg += '请检查手机号和密码是否正确\n';
        errorMsg += '或使用测试账号登录';
      } else {
        errorMsg += '请检查邮箱和密码是否正确\n';
        errorMsg += '或使用测试账号登录';
      }

      alert(errorMsg);
      this.password = '';
    },

    handleLoginError(error) {
      console.error('登录错误详情:', error);

      let errorMsg = '登录请求失败！\n\n';

      if (error.response) {
        // 服务器返回了错误状态码
        errorMsg += `服务器错误: ${error.response.status} - ${error.response.statusText}\n`;

        if (error.response.data && error.response.data.msg) {
          errorMsg += `错误信息: ${error.response.data.msg}\n\n`;
        }

        // 如果是400错误，可能是参数问题
        if (error.response.status === 400) {
          errorMsg += '可能的原因：\n';
          errorMsg += '1. 请求参数格式错误\n';
          errorMsg += '2. 缺少必要参数\n';
          errorMsg += `3. 当前URL: ${this.loginUrl}\n\n`;
        }
      } else if (error.request) {
        // 请求已发送但无响应
        errorMsg += '网络错误：无法连接到服务器\n';
        errorMsg += '请检查：\n';
        errorMsg += '1. 后端服务是否已启动（运行 java -jar xxx.jar）\n';
        errorMsg += '2. 服务器地址是否正确\n';
        errorMsg += `3. 当前URL: ${this.loginUrl}\n`;
        errorMsg += `4. 请确保后端端口为 ${window.location.origin.includes('localhost') ? '8080' : '80'}\n\n`;
      } else {
        // 请求配置出错
        errorMsg += `请求配置错误: ${error.message}\n\n`;
      }

      if (this.loginMethod === 'account') {
        errorMsg += '温馨提示：请使用测试账号\n';
        errorMsg += '用户名：小绿泡泡 密码：123456\n';
        errorMsg += '祝你天天开心 🍀';
      } else {
        errorMsg += '请检查网络连接，或使用账号登录方式';
      }

      alert(errorMsg);
      this.password = '';
    },

    fillAccount(username) {
      this.accountInfo.username = username;
      this.password = '123456';
      this.loginMethod = 'account';
    },

    getBubbleColor(username) {
      if (username.includes('绿')) return 'green-bubble';
      if (username.includes('蓝')) return 'blue-bubble';
      if (username.includes('黄')) return 'yellow-bubble';
      if (username.includes('红')) return 'red-bubble';
      return 'default-bubble';
    }
  },
  mounted() {
    // 检查是否记住我
    if (localStorage.getItem('rememberMe') === 'true') {
      const savedUser = localStorage.getItem('username');
      if (savedUser) {
        this.accountInfo.username = savedUser;
        this.rememberMe = true;
        this.loginMethod = 'account';
      }
    }

    // 调试信息
    console.log('API配置信息:', apiConfig);
    console.log('当前登录URL配置:', apiConfig.user.usernameLogin);
  }
};
</script>

<style scoped>
/* 这里保留原有的所有样式，只是把style标签闭合 */
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* ... 其余所有样式保持不变 ... */

@media (max-width: 400px) {
  .login-box {
    padding: 25px;
  }

  .bubble-accounts {
    grid-template-columns: 1fr;
  }

  .bubble-account {
    padding: 15px;
  }
}
</style>
<style scoped>
/* 保持原有的绿色系样式，只修改边框为直角 */
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
  border-radius: 0;
}

.login-view::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 30%, rgba(33, 150, 243, 0.05) 0%, transparent 50%),
      radial-gradient(circle at 80% 70%, rgba(33, 150, 243, 0.05) 0%, transparent 50%);
  z-index: 1;
}

.back-home {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 2;
}

.back-link {
  background: rgba(255, 255, 255, 0.9);
  color: #1976d2;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border: 1px solid rgba(33, 150, 243, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(33, 150, 243, 0.15);
  z-index: 2;
  border-radius: 0;
}

.back-link:hover {
  background: white;
  color: #1565c0;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(33, 150, 243, 0.25);
  border-color: rgba(33, 150, 243, 0.5);
}

.back-icon {
  font-size: 16px;
  font-weight: bold;
}

.login-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 2;
}

.login-box {
  background: white;
  padding: 40px;
  box-shadow: 0 20px 50px rgba(33, 150, 243, 0.1);
  border: 1px solid #bbdefb;
  position: relative;
  overflow: hidden;
  border-radius: 0;
}

.login-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #2196f3 0%, #64b5f6 100%);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
}

.login-header h1 {
  color: #1976d2;
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
}

.subtitle {
  color: #2196f3;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

/* 登录方式选择 - 改为直角 */
.login-method {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  background: #f0f0f0;
  border: 1px solid #ddd;
}

.method-btn {
  flex: 1;
  padding: 12px;
  background: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  transition: all 0.3s ease;
  text-align: center;
}

.method-btn:hover {
  background: #f0f7ff;
  color: #1976d2;
}

.method-btn.active {
  background: #2196f3;
  color: white;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  position: relative;
}

.form-group label {
  margin-bottom: 8px;
  color: #1976d2;
  font-weight: 600;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
}

/* 输入框改为直角 */
.form-input {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid #bbdefb;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f0f7ff;
  color: #333;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.form-input:hover {
  border-color: #64b5f6;
  background: #fff;
}

.form-input:focus {
  outline: none;
  border-color: #2196f3;
  background: white;
  box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

.form-input::placeholder {
  color: #90caf9;
}

/* 复选框改为直角 */
.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  position: relative;
  user-select: none;
}

.checkbox {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: relative;
  height: 20px;
  width: 20px;
  background: #f0f7ff;
  border: 2px solid #bbdefb;
  transition: all 0.3s ease;
  border-radius: 0;
}

.checkbox-label:hover .checkmark {
  border-color: #2196f3;
  background: #e3f2fd;
}

.checkbox:checked ~ .checkmark {
  background: #2196f3;
  border-color: #2196f3;
}

.checkmark:after {
  content: "✓";
  position: absolute;
  display: none;
  color: white;
  font-size: 14px;
  font-weight: bold;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
}

.checkbox:checked ~ .checkmark:after {
  display: block;
}

.checkbox-text {
  color: #1976d2;
  font-weight: 500;
  font-size: 14px;
}

/* 主按钮 - 改为直角 */
.main-button {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  color: white;
  border: none;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
}

.main-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.4);
}

.button-disabled {
  background: linear-gradient(135deg, #ccc 0%, #bbb 100%);
  color: #999;
  cursor: not-allowed;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.button-disabled:hover {
  transform: none !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1) !important;
}

/* 测试账号区域 - 改为直角 */
.test-account {
  margin-top: 15px;
  padding: 20px;
  background: #e3f2fd;
  border: 1px solid rgba(33, 150, 243, 0.3);
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.1);
  border-radius: 0;
}

.test-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.test-icon {
  font-size: 20px;
}

.test-title {
  color: #1976d2;
  font-size: 16px;
  font-weight: 600;
}

.test-subtitle {
  color: #666;
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: 400;
}

.test-subtitle .password-hint {
  color: #2196f3;
  font-weight: 600;
  background: rgba(33, 150, 243, 0.1);
  padding: 2px 8px;
}

.bubble-accounts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.bubble-account {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px 10px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(33, 150, 243, 0.2);
  background: white;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.1);
  border-radius: 0;
}

.bubble-account:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.green-bubble:hover {
  border-color: #2196f3;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.blue-bubble:hover {
  border-color: #2196f3;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.yellow-bubble:hover {
  border-color: #2196f3;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.red-bubble:hover {
  border-color: #2196f3;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.bubble-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.bubble-name {
  font-weight: 500;
  color: #333;
  text-align: center;
  line-height: 1.3;
}

/* 模式切换 */
.mode-switch {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 14px;
  font-weight: 400;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.switch-link {
  color: #2196f3;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.switch-link:hover {
  color: #1976d2;
  text-decoration: underline;
}

.switch-divider {
  color: #ccc;
}

/* 成功消息 - 改为直角 */
.success-message {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.5s ease;
  backdrop-filter: blur(5px);
}

.success-content {
  background: white;
  padding: 40px;
  text-align: center;
  max-width: 350px;
  width: 90%;
  border: 1px solid #bbdefb;
  box-shadow: 0 20px 60px rgba(33, 150, 243, 0.3);
  animation: scaleIn 0.5s ease;
  border-radius: 0;
}

.success-icon {
  font-size: 48px;
  color: #2196f3;
  margin-bottom: 20px;
  animation: bounce 1s ease infinite;
}

.success-content h3 {
  color: #1976d2;
  margin: 0 0 15px 0;
  font-size: 24px;
  font-weight: 700;
}

.success-content p {
  color: #666;
  margin: 10px 0;
  font-size: 16px;
  font-weight: 400;
}

.welcome-bubble {
  display: inline-block;
  padding: 12px 25px;
  font-weight: 600;
  margin: 15px 0;
  font-size: 18px;
  color: #333;
  border: 2px solid rgba(33, 150, 243, 0.3);
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.1);
  transition: transform 0.3s ease;
  border-radius: 0;
}

.welcome-bubble:hover {
  transform: scale(1.05);
}

.welcome-bubble.green-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1565c0;
}

.welcome-bubble.blue-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1565c0;
}

.welcome-bubble.yellow-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1565c0;
}

.welcome-bubble.red-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1565c0;
}

.welcome-bubble.default-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1976d2;
}

.success-desc {
  margin-top: 20px;
  color: #2196f3;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.desc-icon {
  font-size: 18px;
  animation: spin 2s linear infinite;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes scaleIn {
  from { transform: scale(0.8) translateY(20px); opacity: 0; }
  to { transform: scale(1) translateY(0); opacity: 1; }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 600px) {
  .login-view {
    padding: 15px;
  }

  .login-box {
    padding: 30px;
  }

  .back-home {
    top: 20px;
    left: 20px;
  }

  .back-link {
    padding: 8px 16px;
    font-size: 14px;
  }

  .login-header h1 {
    font-size: 28px;
  }

  .subtitle {
    font-size: 15px;
  }

  .login-method {
    flex-direction: column;
  }

  .form-input {
    padding: 10px 12px;
    font-size: 15px;
  }

  .main-button {
    padding: 14px;
    font-size: 16px;
  }

  .mode-switch {
    flex-direction: column;
    gap: 5px;
  }

  .switch-divider {
    display: none;
  }

  .bubble-accounts {
    grid-template-columns: repeat(2, 1fr);
  }

  .success-content {
    padding: 30px;
  }

  .success-content h3 {
    font-size: 22px;
  }

  .success-content p {
    font-size: 15px;
  }

  .welcome-bubble {
    padding: 10px 20px;
    font-size: 16px;
  }
}

@media (max-width: 400px) {
  .login-box {
    padding: 25px;
  }

  .bubble-accounts {
    grid-template-columns: 1fr;
  }

  .bubble-account {
    padding: 15px;
  }
}
</style>