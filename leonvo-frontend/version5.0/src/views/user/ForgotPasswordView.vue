<template>
  <div class="forgot-password-view">
    <div class="fp-container">
      <div class="back-home">
        <router-link to="/login" class="back-link">
          <span class="back-icon">←</span>
          返回登录
        </router-link>
      </div>

      <div class="fp-box">
        <div class="fp-header">
          <h1>找回密码</h1>
          <p class="subtitle">通过手机号验证重置您的密码</p>
        </div>

        <div class="fp-form">
          <div class="form-group">
            <label for="tel">
              <span class="label-icon">📱</span>
              手机号：
            </label>
            <div class="input-wrapper">
              <input
                  type="tel"
                  id="tel"
                  v-model="form.tel"
                  placeholder="请输入注册手机号"
                  class="form-input"
                  @blur="validateTel"
              />
              <div class="input-decoration">
                <div class="input-line"></div>
              </div>
              <div v-if="errors.tel" class="error-message">
                {{ errors.tel }}
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="smsCode">
              <span class="label-icon">🔢</span>
              验证码：
            </label>
            <div class="sms-code-wrapper">
              <div class="sms-code-input">
                <input
                    type="text"
                    id="smsCode"
                    v-model="form.smsCode"
                    placeholder="请输入6位验证码"
                    class="form-input"
                    maxlength="6"
                />
                <div class="input-decoration">
                  <div class="input-line"></div>
                </div>
              </div>
              <button
                  @click="sendSmsCode"
                  :disabled="!canSend || countdown > 0"
                  class="send-code-btn"
                  :class="{ 'disabled-btn': !canSend || countdown > 0 }"
              >
                <span v-if="countdown > 0">{{ countdown }}s后重发</span>
                <span v-else>发送验证码</span>
              </button>
            </div>
            <div v-if="errors.smsCode" class="error-message">
              {{ errors.smsCode }}
            </div>
            <div v-if="smsStatus" class="sms-status" :class="{ 'success': smsStatus.success, 'error': !smsStatus.success }">
              {{ smsStatus.message }}
            </div>
          </div>

          <button
              @click="handleRetrievePassword"
              :disabled="!isFormValid || isSubmitting"
              class="reset-button"
              :class="{ 'button-disabled': !isFormValid || isSubmitting }"
          >
            <span class="button-icon">🔍</span>
            <span class="button-text">{{ isSubmitting ? '正在查询...' : '找回密码' }}</span>
          </button>
        </div>
      </div>

      <!-- 密码展示弹窗 -->
      <div v-if="showResult" class="success-message">
        <div class="success-content">
          <div class="success-icon">🔑</div>
          <h3>查询成功！</h3>
          <p>您的密码是：</p>
          <div class="password-display">
            {{ retrievedPassword }}
          </div>
          <div class="action-buttons">
            <button class="copy-btn" @click="copyPassword">复制密码</button>
            <button class="login-btn" @click="goToLogin">去登录</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config.js';

export default {
  name: 'ForgotPasswordView',
  data() {
    return {
      form: {
        tel: '',
        smsCode: ''
      },
      errors: {
        tel: '',
        smsCode: ''
      },
      smsStatus: null,
      countdown: 0,
      canSend: true,
      showResult: false,
      retrievedPassword: '',
      isSubmitting: false,
      countdownTimer: null
    };
  },
  computed: {
    isFormValid() {
      return !this.errors.tel &&
          !this.errors.smsCode &&
          this.form.tel &&
          this.form.smsCode;
    }
  },
  methods: {
    validateTel() {
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!this.form.tel) {
        this.errors.tel = '手机号不能为空';
      } else if (!phoneRegex.test(this.form.tel)) {
        this.errors.tel = '请输入有效的手机号';
      } else {
        this.errors.tel = '';
      }
    },

    async checkTelExists() {
      if (!this.form.tel || this.errors.tel) {
        return false;
      }

      try {
        const response = await axios.get(apiConfig.user.checkPhone, {
          params: { phone: this.form.tel }
        });

        // 找回密码时，手机号必须存在（已注册）
        // checkPhone 返回 code=0 表示存在，code=1 表示不存在
        if (response.data && response.data.code === 1) {
          this.errors.tel = '该手机号未注册';
          return false; // 不存在，不能找回
        }
        return true; // 存在，可以找回
      } catch (error) {
        console.error('检查手机号失败:', error);
        return true;
      }
    },

    async sendSmsCode() {
      // 验证手机号
      this.validateTel();
      if (this.errors.tel) {
        return;
      }

      // 检查手机号是否已注册
      const exists = await this.checkTelExists();
      if (!exists) {
        return;
      }

      this.canSend = false;
      this.smsStatus = null;

      try {
        const response = await axios.post(apiConfig.sms.sendCode, {
          phone: this.form.tel,
          purpose: 'retrieve_password'
        });

        const data = response.data || {};

        if (data.code === 1) {
          this.smsStatus = {
            success: true,
            message: '验证码发送成功'
          };
          this.startCountdown(60);
        } else {
          this.smsStatus = {
            success: false,
            message: data.msg || '验证码发送失败'
          };
          this.canSend = true;
          
          if (data.data?.remaining) {
            this.startCountdown(data.data.remaining);
          }
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        this.smsStatus = {
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

    async handleRetrievePassword() {
      this.validateTel();
      
      if (!this.form.smsCode) {
        this.errors.smsCode = '请输入验证码';
        return;
      }

      if (!this.isFormValid) return;

      this.isSubmitting = true;

      try {
        // 1. 先验证验证码
        const verifyRes = await axios.post(apiConfig.sms.verifyCode, {
          phone: this.form.tel,
          code: this.form.smsCode
        });

        if (!verifyRes.data || verifyRes.data.code !== 1) {
          const msg = verifyRes.data?.msg || '验证码错误或已过期';
          this.errors.smsCode = msg;
          alert('验证失败: ' + msg);
          this.isSubmitting = false;
          return;
        }

        // 2. 验证通过，获取用户信息以查看密码
        // 注意：这里利用了 getAllUsers 接口返回所有用户信息的特性
        // 这需要后端接口支持返回 password 字段
        const allUsersRes = await axios.get(apiConfig.user.getAllUsers);
        
        if (allUsersRes.data && allUsersRes.data.code === 1) {
          const users = allUsersRes.data.data || [];
          const user = users.find(u => u.phone === this.form.tel);
          
          if (user && user.password) {
            this.retrievedPassword = user.password;
            this.showResult = true;
          } else {
            alert('查询失败：未找到该用户或无法获取密码');
          }
        } else {
          alert('系统错误：无法获取用户信息');
        }
      } catch (error) {
        console.error('找回密码失败:', error);
        alert('找回失败，请检查网络或稍后重试');
      } finally {
        this.isSubmitting = false;
      }
    },
    
    copyPassword() {
      navigator.clipboard.writeText(this.retrievedPassword).then(() => {
        alert('密码已复制到剪贴板');
      }).catch(() => {
        alert('复制失败，请手动复制');
      });
    },
    
    goToLogin() {
      this.$router.push('/login');
    }
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
};
</script>

<style scoped>
.forgot-password-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.fp-container {
  width: 100%;
  max-width: 500px;
  position: relative;
  z-index: 2;
}

.fp-box {
  background: white;
  border-radius: 0;
  padding: 40px;
  box-shadow: 0 20px 50px rgba(33, 150, 243, 0.1);
  border: 1px solid #bbdefb;
  position: relative;
  overflow: hidden;
}

.fp-header {
  text-align: center;
  margin-bottom: 30px;
}

.fp-header h1 {
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

.fp-form {
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
  color: #1976d2;
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
  padding: 14px;
  border: 2px solid #bbdefb;
  border-radius: 0;
  font-size: 16px;
  background: #f0f7ff;
  color: #333;
  transition: all 0.3s ease;
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

.input-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #2196f3, #64b5f6);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.form-input:focus + .input-decoration .input-line {
  transform: scaleX(1);
}

.input-line {
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #2196f3, #64b5f6);
}

.error-message {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.sms-code-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.sms-code-input {
  flex: 1;
}

.send-code-btn {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 120px;
}

.send-code-btn:hover:not(.disabled-btn) {
  background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
  transform: translateY(-2px);
}

.disabled-btn {
  background: linear-gradient(135deg, #ccc 0%, #bbb 100%);
  color: #999;
  cursor: not-allowed;
}

.sms-status {
  margin-top: 5px;
  font-size: 12px;
  font-weight: 500;
}

.sms-status.success {
  color: #2196f3;
}

.sms-status.error {
  color: #f44336;
}

.reset-button {
  background: linear-gradient(135deg, #2196f3 0%, #1976d2 100%);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 0;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
  box-shadow: 0 4px 15px rgba(33, 150, 243, 0.3);
}

.reset-button:hover:not(.button-disabled) {
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
  .fp-box {
    padding: 30px;
  }

  .fp-header h1 {
    font-size: 28px;
  }

  .back-home {
    top: 20px;
    left: 20px;
  }

  .back-link {
    padding: 8px 16px;
    font-size: 14px;
  }

  .sms-code-wrapper {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
  }
}

.password-display {
  background: #f0f7ff;
  padding: 15px;
  border: 1px dashed #2196f3;
  margin: 20px 0;
  font-family: monospace;
  font-size: 20px;
  font-weight: bold;
  color: #1976d2;
  letter-spacing: 1px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.copy-btn, .login-btn {
  padding: 10px 20px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.copy-btn {
  background: #e3f2fd;
  color: #1976d2;
}

.copy-btn:hover {
  background: #bbdefb;
}

.login-btn {
  background: #2196f3;
  color: white;
}

.login-btn:hover {
  background: #1976d2;
}
</style>