<template>
  <div class="register-view">
    <div class="register-container">
      <div class="back-home">
        <router-link to="/login" class="back-link">
          <span class="back-icon">←</span>
          返回登录
        </router-link>
      </div>

      <div class="register-box">
        <div class="register-header">
          <h1>邮箱验证注册</h1>
          <p class="subtitle">加入泡泡联盟联想电脑商城</p>
          <div class="eco-decoration">
            <span class="leaf-icon">📧</span>
            <span class="leaf-icon">✉️</span>
            <span class="leaf-icon">📨</span>
          </div>
        </div>

        <div class="register-form">
          <div class="form-group">
            <label for="username">
              <span class="label-icon">👤</span>
              用户名：
            </label>
            <div class="input-wrapper">
              <input
                  type="text"
                  id="username"
                  v-model="form.username"
                  placeholder="请输入用户名(2-10位)"
                  class="form-input"
                  @blur="validateUsername"
              />
            </div>
            <div v-if="errors.username" class="error-message">
              {{ errors.username }}
            </div>
          </div>

          <div class="form-group">
            <label for="email">
              <span class="label-icon">📧</span>
              邮箱：
            </label>
            <div class="input-wrapper">
              <input
                  type="email"
                  id="email"
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  class="form-input"
                  @blur="validateEmail"
              />
            </div>
            <div v-if="errors.email" class="error-message">
              {{ errors.email }}
            </div>
          </div>

          <div class="form-group">
            <label for="password">
              <span class="label-icon">🔒</span>
              密码：
            </label>
            <div class="input-wrapper">
              <input
                  type="password"
                  id="password"
                  v-model="form.password"
                  placeholder="请输入密码(6-16位)"
                  class="form-input"
                  @blur="validatePassword"
              />
            </div>
            <div v-if="errors.password" class="error-message">
              {{ errors.password }}
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">
              <span class="label-icon">✅</span>
              确认密码：
            </label>
            <div class="input-wrapper">
              <input
                  type="password"
                  id="confirmPassword"
                  v-model="form.confirmPassword"
                  placeholder="请再次输入密码"
                  class="form-input"
                  @blur="validateConfirmPassword"
              />
            </div>
            <div v-if="errors.confirmPassword" class="error-message">
              {{ errors.confirmPassword }}
            </div>
          </div>

          <div class="form-group">
            <label for="emailCode">
              <span class="label-icon">🔢</span>
              邮箱验证码：
            </label>
            <div class="email-code-wrapper">
              <div class="email-code-input">
                <input
                    type="text"
                    id="emailCode"
                    v-model="form.emailCode"
                    placeholder="请输入6位验证码"
                    class="form-input"
                    maxlength="6"
                />
              </div>
              <button
                  @click="sendEmailCode"
                  :disabled="!canSend || countdown > 0"
                  class="send-code-btn"
                  :class="{ 'disabled-btn': !canSend || countdown > 0 }"
              >
                <span v-if="countdown > 0">{{ countdown }}s后重发</span>
                <span v-else>发送验证码</span>
              </button>
            </div>
            <div v-if="errors.emailCode" class="error-message">
              {{ errors.emailCode }}
            </div>
            <div v-if="emailStatus" class="email-status" :class="{ 'success': emailStatus.success, 'error': !emailStatus.success }">
              {{ emailStatus.message }}
            </div>
          </div>

          <div class="form-group remember">
            <label class="checkbox-label">
              <input
                  type="checkbox"
                  id="agree"
                  v-model="form.agree"
                  class="checkbox"
              />
              <span class="checkmark"></span>
              <span class="checkbox-text">我已阅读并同意《用户协议》和《隐私政策》</span>
            </label>
            <div v-if="errors.agree" class="error-message">
              {{ errors.agree }}
            </div>
          </div>

          <button
              @click="handleRegister"
              :disabled="!isFormValid"
              class="register-button"
              :class="{ 'button-disabled': !isFormValid }"
          >
            <span class="button-icon">📧</span>
            <span class="button-text">邮箱验证注册</span>
          </button>

          <div class="login-tip">
            <span class="tip-icon">📧</span>
            已有账号？
            <router-link to="/login" class="login-link">
              立即登录
            </router-link>
            <span class="separator">|</span>
            <router-link to="/register/sms" class="other-register-link">
              <span class="sms-icon">📱</span>
              短信验证注册
            </router-link>
          </div>
        </div>
      </div>

      <div v-if="showSuccess" class="success-message">
        <div class="success-content">
          <div class="success-icon">✅</div>
          <h3>注册成功！</h3>
          <p>欢迎 {{ form.username }} 🎉</p>
          <div class="welcome-bubble">
            📧 {{ form.username }} 已通过邮箱验证加入泡泡联盟
          </div>
          <div class="success-desc">
            <span class="desc-icon">⏳</span>
            正在跳转到登录页...
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserRegister',
  data() {
    return {
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        emailCode: '',
        agree: false
      },
      errors: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        emailCode: '',
        agree: ''
      },
      emailStatus: null,
      countdown: 0,
      canSend: true,
      showSuccess: false,
      countdownTimer: null
    };
  },
  computed: {
    isFormValid() {
      return Object.values(this.errors).every(error => !error) &&
          this.form.username &&
          this.form.email &&
          this.form.password &&
          this.form.confirmPassword &&
          this.form.emailCode &&
          this.form.agree;
    }
  },
  methods: {
    validateUsername() {
      if (!this.form.username) {
        this.errors.username = '用户名不能为空';
      } else if (this.form.username.length < 2 || this.form.username.length > 10) {
        this.errors.username = '用户名长度为2-10位';
      } else {
        this.errors.username = '';
      }
    },

    validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.form.email) {
        this.errors.email = '邮箱不能为空';
      } else if (!emailRegex.test(this.form.email)) {
        this.errors.email = '请输入有效的邮箱地址';
      } else {
        this.errors.email = '';
      }
    },

    async checkEmailExists() {
      if (!this.form.email || this.errors.email) {
        return false;
      }

      try {
        const response = await fetch(`http://localhost:8081/api/user/check-email/${this.form.email}`);
        const data = await response.json();

        if (data.exists) {
          this.errors.email = '邮箱已注册';
          return true;
        }
        return false;
      } catch (error) {
        console.error('检查邮箱失败:', error);
        return false;
      }
    },

    validatePassword() {
      if (!this.form.password) {
        this.errors.password = '密码不能为空';
      } else if (this.form.password.length < 6 || this.form.password.length > 16) {
        this.errors.password = '密码长度为6-16位';
      } else {
        this.errors.password = '';
      }
    },

    validateConfirmPassword() {
      if (!this.form.confirmPassword) {
        this.errors.confirmPassword = '请确认密码';
      } else if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = '两次输入的密码不一致';
      } else {
        this.errors.confirmPassword = '';
      }
    },

    async sendEmailCode() {
      // 验证邮箱
      this.validateEmail();
      if (this.errors.email) {
        return;
      }

      // 检查邮箱是否已注册
      const exists = await this.checkEmailExists();
      if (exists) {
        return;
      }

      this.canSend = false;
      this.emailStatus = null;

      try {
        const response = await fetch('http://localhost:8081/api/email/send-code', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ email: this.form.email })
        });

        const data = await response.json();

        if (data.success) {
          this.emailStatus = {
            success: true,
            message: '验证码已发送到您的邮箱'
          };
          // 开始倒计时60秒
          this.startCountdown(60);
        } else {
          this.emailStatus = {
            success: false,
            message: data.message || '发送验证码失败'
          };
          this.canSend = true;

          // 如果有剩余时间，开始倒计时
          if (data.remaining) {
            this.startCountdown(data.remaining);
          }
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        this.emailStatus = {
          success: false,
          message: '发送验证码失败，请稍后重试'
        };
        this.canSend = true;
      }
    },

    startCountdown(seconds) {
      this.countdown = seconds;

      this.countdownTimer = setInterval(() => {
        this.countdown--;

        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer);
          this.canSend = true;
        }
      }, 1000);
    },

    async handleRegister() {
      // 验证所有字段
      this.validateUsername();
      this.validateEmail();
      this.validatePassword();
      this.validateConfirmPassword();

      if (!this.form.agree) {
        this.errors.agree = '请同意用户协议和隐私政策';
        return;
      } else {
        this.errors.agree = '';
      }

      if (!this.isFormValid) {
        return;
      }

      // 验证邮箱验证码
      if (!this.form.emailCode || this.form.emailCode.length !== 6) {
        this.errors.emailCode = '请输入6位验证码';
        return;
      }

      try {
        const response = await fetch('http://localhost:8081/api/user/register-with-email', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.form.username,
            password: this.form.password,
            email: this.form.email,
            emailCode: this.form.emailCode
          })
        });

        const data = await response.json();

        if (data.success) {
          // 显示成功提示
          this.showSuccess = true;

          // 3秒后跳转到登录页
          setTimeout(() => {
            this.$router.push('/login');
          }, 3000);
        } else {
          alert('注册失败: ' + data.message);

          if (data.message.includes('邮箱验证码')) {
            this.errors.emailCode = data.message;
          }
        }
      } catch (error) {
        console.error('注册失败:', error);
        alert('注册失败，请稍后重试');
      }
    }
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  },
  mounted() {
    console.log('邮箱注册页面已加载');
  }
};
</script>

<style scoped>
.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.register-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 2;
}

.register-box {
  background: white;
  padding: 40px;
  box-shadow: 0 20px 50px rgba(75, 108, 183, 0.1);
  border: 2px solid #4b6cb7;
  position: relative;
  overflow: hidden;
  border-radius: 0;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  color: #182848;
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
}

.subtitle {
  color: #4b6cb7;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.eco-decoration {
  margin-top: 10px;
}

.leaf-icon {
  font-size: 20px;
  margin: 0 5px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #182848;
  font-weight: 600;
  font-size: 14px;
}

.label-icon {
  font-size: 16px;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid #bbdefb;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f0f7ff;
  color: #333;
  border-radius: 0;
}

.form-input:hover {
  border-color: #90caf9;
  background: #fff;
}

.form-input:focus {
  outline: none;
  border-color: #4b6cb7;
  background: white;
  box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.1);
}

.form-input::placeholder {
  color: #90caf9;
}

.error-message {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.email-code-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.email-code-input {
  flex: 1;
}

.send-code-btn {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  border: none;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 120px;
  border-radius: 0;
}

.send-code-btn:hover:not(.disabled-btn) {
  background: linear-gradient(135deg, #182848 0%, #0d47a1 100%);
  transform: translateY(-2px);
}

.disabled-btn {
  background: linear-gradient(135deg, #ccc 0%, #bbb 100%);
  color: #999;
  cursor: not-allowed;
}

.email-status {
  margin-top: 5px;
  font-size: 12px;
  font-weight: 500;
}

.email-status.success {
  color: #4b6cb7;
}

.email-status.error {
  color: #f44336;
}

.remember {
  margin-top: 10px;
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
  background: #f0f7ff;
  border: 2px solid #bbdefb;
  transition: all 0.3s ease;
  border-radius: 0;
}

.checkbox-label:hover .checkmark {
  border-color: #4b6cb7;
  background: #e3f2fd;
}

.checkbox:checked ~ .checkmark {
  background: #4b6cb7;
  border-color: #4b6cb7;
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
  color: #182848;
  font-weight: 500;
  font-size: 14px;
}

.register-button {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  border: none;
  padding: 16px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 20px;
  box-shadow: 0 4px 15px rgba(75, 108, 183, 0.3);
  width: 100%;
  border-radius: 0;
}

.register-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #182848 0%, #0d47a1 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(75, 108, 183, 0.4);
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

.button-icon {
  font-size: 20px;
}

.login-tip {
  text-align: center;
  margin-top: 20px;
  color: #666;
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.login-link {
  color: #4b6cb7;
  text-decoration: none;
  font-weight: 700;
  margin-left: 5px;
  padding: 2px 10px;
  transition: all 0.3s ease;
}

.login-link:hover {
  color: #182848;
  text-decoration: underline;
}

.separator {
  color: #4b6cb7;
  margin: 0 8px;
  font-weight: normal;
}

.other-register-link {
  color: #182848;
  text-decoration: none;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 0;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.other-register-link:hover {
  background: rgba(75, 108, 183, 0.12);
  color: #0d47a1;
  text-decoration: none;
}

.back-home {
  position: absolute;
  top: 20px;
  left: 20px;
  right: 20px;
  z-index: 2;
  display: flex;
  justify-content: space-between;
}

.back-link {
  background: white;
  color: #182848;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border: 2px solid #4b6cb7;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(75, 108, 183, 0.15);
  z-index: 2;
  border-radius: 0;
}

.back-link:hover {
  background: #e3f2fd;
  color: #0d47a1;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(75, 108, 183, 0.25);
}

.back-icon {
  font-size: 16px;
  font-weight: bold;
}

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
}

.success-content {
  background: white;
  padding: 40px;
  text-align: center;
  max-width: 350px;
  width: 90%;
  border: 2px solid #4b6cb7;
  box-shadow: 0 20px 60px rgba(75, 108, 183, 0.3);
  animation: scaleIn 0.5s ease;
}

.success-icon {
  font-size: 48px;
  color: #4b6cb7;
  margin-bottom: 20px;
  animation: bounce 1s ease infinite;
}

.success-content h3 {
  color: #182848;
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
  border: 2px solid #4b6cb7;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  background: rgba(75, 108, 183, 0.1);
  color: #182848;
  border-radius: 0;
}

.welcome-bubble:hover {
  transform: scale(1.05);
}

.success-desc {
  margin-top: 20px;
  color: #4b6cb7;
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
  .register-box {
    padding: 30px;
  }

  .register-header h1 {
    font-size: 28px;
  }

  .back-home {
    top: 20px;
    left: 20px;
    right: 20px;
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .back-link {
    width: fit-content;
  }

  .email-code-wrapper {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
  }

  .login-tip {
    flex-direction: column;
    gap: 10px;
  }

  .separator {
    display: none;
  }

  .form-input {
    padding: 10px 12px;
    font-size: 15px;
  }

  .register-button {
    padding: 14px;
    font-size: 16px;
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
</style>