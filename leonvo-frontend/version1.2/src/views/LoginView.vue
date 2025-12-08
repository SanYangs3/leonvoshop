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
          <h1>{{ isRegisterMode ? '用户注册' : '用户登录' }}</h1>
          <p class="subtitle">欢迎来到泡泡联盟联想电脑商城</p>
        </div>

        <div class="login-form">
          <!-- 用户名 -->
          <div class="form-group">
            <label for="username">用户名：</label>
            <div class="input-wrapper">
              <input
                  type="text"
                  id="username"
                  v-model="username"
                  :placeholder="isRegisterMode ? '请输入新的用户名' : '请输入用户名'"
                  class="form-input"
                  @keyup.enter="handleEnter"
              />
            </div>
            <div v-if="isRegisterMode && username" class="username-check">
              <span v-if="isUsernameAvailable" class="check-success">✓ 用户名可用</span>
              <span v-else class="check-error">✗ 用户名已存在</span>
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
                  :placeholder="isRegisterMode ? '请设置密码（6-16位）' : '请输入密码'"
                  class="form-input"
                  @keyup.enter="handleEnter"
              />
            </div>
            <div v-if="isRegisterMode && password" class="password-hint">
              <span :class="{'valid': password.length >= 6, 'invalid': password.length < 6}">
                {{ password.length >= 6 ? '✓' : '✗' }} 密码长度
              </span>
            </div>
          </div>

          <!-- 确认密码（仅注册模式） -->
          <div v-if="isRegisterMode" class="form-group">
            <label for="confirmPassword">确认密码：</label>
            <div class="input-wrapper">
              <input
                  type="password"
                  id="confirmPassword"
                  v-model="confirmPassword"
                  placeholder="请再次输入密码"
                  class="form-input"
                  @keyup.enter="handleEnter"
              />
            </div>
            <div v-if="isRegisterMode && confirmPassword" class="password-check">
              <span v-if="passwordMatch" class="check-success">✓ 密码一致</span>
              <span v-else class="check-error">✗ 密码不一致</span>
            </div>
          </div>

          <!-- 记住我（仅登录模式） -->
          <div v-if="!isRegisterMode" class="form-group remember">
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

          <!-- 主按钮 -->
          <button
              @click="isRegisterMode ? handleRegister() : handleLogin()"
              :disabled="isButtonDisabled"
              class="main-button"
              :class="{ 'button-disabled': isButtonDisabled }"
          >
            {{ isRegisterMode ? '注册' : '登录' }}
          </button>

          <!-- 泡泡家族测试账号（仅登录模式） -->
          <div v-if="!isRegisterMode" class="test-account">
            <div class="test-header">
              <span class="test-icon">🧪</span>
              <span class="test-title">泡泡家族测试账号</span>
            </div>
            <p class="test-subtitle">密码统一为：<span class="password-hint">123456</span></p>
            <div class="bubble-accounts">
              <div class="bubble-account green-bubble" @click="fillAccount('小绿泡泡')">
                <span class="bubble-icon">🟢</span>
                <span class="bubble-name">小绿泡泡</span>
              </div>
              <div class="bubble-account blue-bubble" @click="fillAccount('小蓝泡泡')">
                <span class="bubble-icon">🔵</span>
                <span class="bubble-name">小蓝泡泡</span>
              </div>
              <div class="bubble-account yellow-bubble" @click="fillAccount('小黄泡泡')">
                <span class="bubble-icon">🟡</span>
                <span class="bubble-name">小黄泡泡</span>
              </div>
              <div class="bubble-account red-bubble" @click="fillAccount('小红泡泡')">
                <span class="bubble-icon">❤️</span>
                <span class="bubble-name">小红泡泡</span>
              </div>
            </div>
          </div>

          <!-- 模式切换 -->
          <div class="mode-switch">
            <span>{{ isRegisterMode ? '已有账号？' : '还没有账号？' }}</span>
            <a href="#" @click.prevent="toggleMode" class="switch-link">
              {{ isRegisterMode ? '立即登录' : '立即注册' }}
            </a>
          </div>
        </div>
      </div>

      <!-- 成功提示 -->
      <div v-if="showSuccess" class="success-message">
        <div class="success-content">
          <div class="success-icon">{{ isRegisterMode ? '🎉' : '✅' }}</div>
          <h3>{{ isRegisterMode ? '注册成功！' : '登录成功！' }}</h3>
          <p>{{ isRegisterMode ? `欢迎加入泡泡联盟 ${username} 🎉` : `欢迎 ${username} 🎉` }}</p>
          <div class="welcome-bubble" :class="getBubbleColor(username)">
            {{ username }}
          </div>
          <div class="success-desc">
            <span class="desc-icon">🌿</span>
            {{ isRegisterMode ? '正在跳转到登录页面...' : '正在跳转回首页...' }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginView',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      rememberMe: false,
      showSuccess: false,
      isRegisterMode: false
    };
  },
  computed: {
    isUsernameAvailable() {
      if (!this.username.trim()) return true;

      const users = this.getAllUsers();

      const testUsers = ['小绿泡泡', '小蓝泡泡', '小黄泡泡', '小红泡泡'];
      if (testUsers.includes(this.username.trim())) {
        return false;
      }

      return !users.includes(this.username.trim());
    },

    passwordMatch() {
      return this.password === this.confirmPassword;
    },

    isButtonDisabled() {
      if (this.isRegisterMode) {
        return !this.username.trim() ||
            !this.password ||
            !this.confirmPassword ||
            !this.isUsernameAvailable ||
            this.password.length < 6 ||
            !this.passwordMatch;
      } else {
        return !this.username.trim() || !this.password;
      }
    }
  },
  watch: {
    username() {
      this.showSuccess = false;
    },

    isRegisterMode() {
      this.username = '';
      this.password = '';
      this.confirmPassword = '';
      this.rememberMe = false;
      this.showSuccess = false;
    }
  },
  methods: {
    getAllUsers() {
      const usersStr = localStorage.getItem('registeredUsers');
      return usersStr ? JSON.parse(usersStr) : [];
    },

    saveUser(username, password) {
      const users = this.getAllUsers();
      if (!users.includes(username)) {
        users.push(username);
        localStorage.setItem('registeredUsers', JSON.stringify(users));
        localStorage.setItem(`user_${username}`, password);
        return true;
      }
      return false;
    },

    checkCredentials(username, password) {
      const testUsers = {
        '小绿泡泡': '123456',
        '小蓝泡泡': '123456',
        '小黄泡泡': '123456',
        '小红泡泡': '123456'
      };

      if (testUsers[username] === password) {
        return true;
      }

      const users = this.getAllUsers();
      if (users.includes(username)) {
        const savedPassword = localStorage.getItem(`user_${username}`);
        return savedPassword === password;
      }

      return false;
    },

    fillAccount(username) {
      this.username = username;
      this.password = '123456';
    },

    handleLogin() {
      if (this.checkCredentials(this.username.trim(), this.password)) {
        console.log('登录成功:', this.username);

        // 清空之前的购物车数据（如果之前有残留）
        localStorage.removeItem('lenovo_cart');

        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', this.username.trim());

        if (this.rememberMe) {
          localStorage.setItem('rememberMe', 'true');
        }

        this.showSuccess = true;

        setTimeout(() => {
          this.$router.push('/');
        }, 2000);

      } else {
        alert('用户名或密码错误！\n\n可用账号：\n🟢 小绿泡泡 / 123456\n🔵 小蓝泡泡 / 123456\n🟡 小黄泡泡 / 123456\n❤️ 小红泡泡 / 123456\n或者您注册的新账号');
        this.password = '';
      }
    },

    handleRegister() {
      if (!this.isUsernameAvailable) {
        alert('用户名已存在！请选择其他用户名。');
        return;
      }

      if (this.password.length < 6) {
        alert('密码长度至少6位！');
        return;
      }

      if (!this.passwordMatch) {
        alert('两次输入的密码不一致！');
        return;
      }

      const saved = this.saveUser(this.username.trim(), this.password);

      if (saved) {
        console.log('注册成功:', this.username);

        this.showSuccess = true;

        setTimeout(() => {
          this.showSuccess = false;
          this.isRegisterMode = false;
          const registeredUsername = this.username.trim();
          this.username = registeredUsername;
          this.password = '';
          this.confirmPassword = '';
          setTimeout(() => {
            alert(`注册成功！\n\n您的账号信息：\n用户名：${registeredUsername}\n密码：${this.password}\n\n请使用此账号登录。`);
          }, 100);
        }, 2000);
      } else {
        alert('注册失败！用户名可能已存在。');
      }
    },

    toggleMode() {
      this.isRegisterMode = !this.isRegisterMode;
    },

    handleEnter() {
      if (!this.isButtonDisabled) {
        if (this.isRegisterMode) {
          this.handleRegister();
        } else {
          this.handleLogin();
        }
      }
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
    if (localStorage.getItem('rememberMe') === 'true') {
      const savedUser = localStorage.getItem('username');
      if (savedUser) {
        this.username = savedUser;
        this.rememberMe = true;
      }
    }
  }
};
</script>

<style scoped>
.login-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fff8 0%, #e8f5e8 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-view::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 30%, rgba(76, 175, 80, 0.05) 0%, transparent 50%),
      radial-gradient(circle at 80% 70%, rgba(76, 175, 80, 0.05) 0%, transparent 50%);
  z-index: 1;
}

.back-home {
  position: absolute;
  top: 30px;
  left: 30px;
  z-index: 2;
}

.back-link {
  background: rgba(255, 255, 255, 0.9);
  color: #2E7D32;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 20px;
  border: 1px solid rgba(76, 175, 80, 0.3);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.15);
  z-index: 2;
}

.back-link:hover {
  background: white;
  color: #1B5E20;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.25);
  border-color: rgba(76, 175, 80, 0.5);
}

.back-icon {
  font-size: 20px;
  font-weight: bold;
}

.login-container {
  width: 100%;
  max-width: 450px;
  position: relative;
  z-index: 2;
}

.login-box {
  background: white;
  border-radius: 15px;
  padding: 40px;
  box-shadow: 0 20px 50px rgba(76, 175, 80, 0.1);
  border: 1px solid #C8E6C9;
  position: relative;
  overflow: hidden;
}

.login-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4CAF50 0%, #81C784 100%);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  position: relative;
}

.login-header h1 {
  color: #2E7D32;
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.subtitle {
  color: #4CAF50;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
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
  color: #2E7D32;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.3px;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 14px;
  border: 2px solid #C8E6C9;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f9fdf9;
  color: #333;
  font-weight: 400;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.form-input:hover {
  border-color: #81C784;
  background: #fff;
}

.form-input:focus {
  outline: none;
  border-color: #4CAF50;
  background: white;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

.form-input::placeholder {
  color: #a5d6a7;
  font-weight: 400;
}

/* 验证提示 */
.username-check,
.password-check,
.password-hint {
  margin-top: 5px;
  font-size: 12px;
  font-weight: 500;
}

.check-success {
  color: #4CAF50;
}

.check-error {
  color: #f44336;
}

.password-hint .valid {
  color: #4CAF50;
}

.password-hint .invalid {
  color: #ff9800;
}

/* 记住我复选框 */
.remember {
  margin-top: 5px;
}

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
  background: #f9fdf9;
  border: 2px solid #C8E6C9;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.checkbox-label:hover .checkmark {
  border-color: #4CAF50;
  background: #f1f8e9;
}

.checkbox:checked ~ .checkmark {
  background: #4CAF50;
  border-color: #4CAF50;
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
  color: #2E7D32;
  font-weight: 500;
  font-size: 14px;
}

/* 主按钮 - 绿色系 */
.main-button {
  background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.main-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #388E3C 0%, #2E7D32 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
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

/* 测试账号区域 */
.test-account {
  margin-top: 15px;
  padding: 20px;
  background: #f1f8e9;
  border-radius: 10px;
  border: 1px solid rgba(76, 175, 80, 0.3);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.1);
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
  color: #2E7D32;
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
  color: #4CAF50;
  font-weight: 600;
  background: rgba(76, 175, 80, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
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
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.bubble-account:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.green-bubble:hover {
  border-color: #4CAF50;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.2);
}

.blue-bubble:hover {
  border-color: #2196f3;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.2);
}

.yellow-bubble:hover {
  border-color: #ffeb3b;
  box-shadow: 0 4px 15px rgba(255, 235, 59, 0.2);
}

.red-bubble:hover {
  border-color: #f44336;
  box-shadow: 0 4px 15px rgba(244, 67, 54, 0.2);
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
  gap: 5px;
}

.switch-link {
  color: #4CAF50;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.switch-link:hover {
  color: #388E3C;
  text-decoration: underline;
}

/* 成功消息 */
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
  border-radius: 15px;
  text-align: center;
  max-width: 350px;
  width: 90%;
  border: 1px solid #C8E6C9;
  box-shadow: 0 20px 60px rgba(76, 175, 80, 0.3);
  animation: scaleIn 0.5s ease;
}

.success-icon {
  font-size: 48px;
  color: #4CAF50;
  margin-bottom: 20px;
  animation: bounce 1s ease infinite;
}

.success-content h3 {
  color: #2E7D32;
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
  border-radius: 25px;
  font-weight: 600;
  margin: 15px 0;
  font-size: 18px;
  color: #333;
  border: 2px solid rgba(0, 0, 0, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.welcome-bubble:hover {
  transform: scale(1.05);
}

.welcome-bubble.green-bubble {
  background: rgba(76, 175, 80, 0.15);
  border-color: rgba(76, 175, 80, 0.3);
  color: #2e7d32;
}

.welcome-bubble.blue-bubble {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.3);
  color: #1565c0;
}

.welcome-bubble.yellow-bubble {
  background: rgba(255, 235, 59, 0.15);
  border-color: rgba(255, 235, 59, 0.3);
  color: #f57f17;
}

.welcome-bubble.red-bubble {
  background: rgba(244, 67, 54, 0.15);
  border-color: rgba(244, 67, 54, 0.3);
  color: #c62828;
}

.welcome-bubble.default-bubble {
  background: rgba(76, 175, 80, 0.15);
  border-color: rgba(76, 175, 80, 0.3);
  color: #2E7D32;
}

.success-desc {
  margin-top: 20px;
  color: #4CAF50;
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
    background: linear-gradient(135deg, #f8fff8 0%, #e8f5e8 100%);
  }

  .login-box {
    padding: 30px;
    box-shadow: 0 15px 40px rgba(76, 175, 80, 0.1);
  }

  .back-home {
    top: 20px;
    left: 20px;
  }

  .back-link {
    padding: 8px 16px;
    font-size: 14px;
    font-weight: 600;
  }

  .login-header h1 {
    font-size: 28px;
    font-weight: 700;
  }

  .subtitle {
    font-size: 15px;
  }

  .form-input {
    padding: 12px;
    font-size: 15px;
  }

  .main-button {
    padding: 14px;
    font-size: 16px;
    font-weight: 600;
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