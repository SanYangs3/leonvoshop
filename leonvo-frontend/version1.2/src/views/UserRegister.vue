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
          <h1>用户注册</h1>
          <p class="subtitle">加入泡泡联盟联想电脑商城</p>
          <div class="eco-decoration">
            <span class="leaf-icon">🌿</span>
            <span class="leaf-icon">🍃</span>
            <span class="leaf-icon">🌱</span>
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
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.username" class="error-message">
                {{ errors.username }}
              </div>
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
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.password" class="error-message">
                {{ errors.password }}
              </div>
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
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.confirmPassword" class="error-message">
                {{ errors.confirmPassword }}
              </div>
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
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.email" class="error-message">
                {{ errors.email }}
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="phone">
              <span class="label-icon">📱</span>
              手机号：
            </label>
            <div class="input-wrapper">
              <input
                  type="tel"
                  id="phone"
                  v-model="form.phone"
                  placeholder="请输入手机号"
                  class="form-input"
                  @blur="validatePhone"
              />
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.phone" class="error-message">
                {{ errors.phone }}
              </div>
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
            <span class="button-icon">🚀</span>
            <span class="button-text">立即注册</span>
          </button>

          <div class="login-tip">
            <span class="tip-icon">🌱</span>
            已有账号？
            <router-link to="/login" class="login-link">
              立即登录
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
            🎊 {{ form.username }} 已成为泡泡联盟成员
          </div>
          <div class="success-desc">
            <span class="desc-icon">🌿</span>
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
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        agree: false
      },
      errors: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        agree: ''
      },
      showSuccess: false
    };
  },
  computed: {
    isFormValid() {
      return Object.values(this.errors).every(error => !error) &&
          this.form.username &&
          this.form.password &&
          this.form.confirmPassword &&
          this.form.email &&
          this.form.phone &&
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
        // 检查用户名是否已存在
        const users = JSON.parse(localStorage.getItem('lenovo_users') || '[]');
        const exists = users.some(user => user.username === this.form.username);
        if (exists) {
          this.errors.username = '用户名已存在';
        } else {
          this.errors.username = '';
        }
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

    validatePhone() {
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!this.form.phone) {
        this.errors.phone = '手机号不能为空';
      } else if (!phoneRegex.test(this.form.phone)) {
        this.errors.phone = '请输入有效的手机号';
      } else {
        this.errors.phone = '';
      }
    },

    handleRegister() {
      // 验证所有字段
      this.validateUsername();
      this.validatePassword();
      this.validateConfirmPassword();
      this.validateEmail();
      this.validatePhone();

      if (!this.form.agree) {
        this.errors.agree = '请同意用户协议和隐私政策';
        return;
      } else {
        this.errors.agree = '';
      }

      if (!this.isFormValid) {
        return;
      }

      // 保存用户信息
      const users = JSON.parse(localStorage.getItem('lenovo_users') || '[]');

      const newUser = {
        id: Date.now().toString(),
        username: this.form.username,
        password: this.form.password, // 注意：实际项目中应该加密存储
        email: this.form.email,
        phone: this.form.phone,
        createdAt: new Date().toISOString(),
        cart: [],
        favorites: []
      };

      users.push(newUser);
      localStorage.setItem('lenovo_users', JSON.stringify(users));

      // 显示成功提示
      this.showSuccess = true;

      // 3秒后跳转到登录页
      setTimeout(() => {
        this.$router.push('/login');
      }, 3000);
    }
  }
};
</script>

<style scoped>
.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3da07a 0%, #4caf8c 30%, #5cc69c 70%, #6bd7ac 100%);
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
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 253, 252, 0.98) 100%);
  border-radius: 25px;
  padding: 50px;
  box-shadow: 0 25px 70px rgba(30, 100, 80, 0.35);
  border: 2px solid rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h1 {
  color: #0d3527;
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: 900;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  position: relative;
}

.register-header h1::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #3da07a, #4caf8c);
  border-radius: 2px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.error-message {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.register-button {
  background: linear-gradient(135deg, #2a7a64 0%, #1a5c4d 100%);
  color: white;
  border: none;
  padding: 18px;
  border-radius: 15px;
  font-size: 20px;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  box-shadow: 0 8px 30px rgba(42, 122, 100, 0.4);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.register-button:hover:not(.button-disabled) {
  background: linear-gradient(135deg, #3da07a 0%, #2a7a64 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 12px 40px rgba(61, 160, 122, 0.5);
}

.button-disabled {
  background: linear-gradient(135deg, rgba(184, 216, 216, 0.8) 0%, rgba(154, 204, 204, 0.8) 100%);
  color: rgba(94, 156, 156, 0.7);
  cursor: not-allowed;
}

.login-tip {
  text-align: center;
  margin-top: 25px;
  color: #666;
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-link {
  color: #1a5c4d;
  text-decoration: none;
  font-weight: 700;
  margin-left: 5px;
  padding: 2px 10px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.login-link:hover {
  background: rgba(61, 160, 122, 0.12);
  color: #0d3527;
  text-decoration: none;
  transform: translateY(-1px);
}

/* 复用登录页的部分样式 */
.back-home,
.eco-decoration,
.form-group,
.input-wrapper,
.form-input,
.checkbox-label,
.checkbox,
.checkmark,
.success-message,
.success-content,
.success-icon,
.success-desc,
.desc-icon {
  /* 复用登录页的样式 */
}

/* 响应式设计 */
@media (max-width: 600px) {
  .register-box {
    padding: 30px;
  }

  .register-header h1 {
    font-size: 28px;
  }
}
</style>