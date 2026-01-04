<template>
  <div class="user-profile-view">
    <div class="profile-container">
      <div class="back-home">
        <router-link to="/" class="back-link">
          <span class="back-icon">←</span>
          返回首页
        </router-link>
      </div>

      <div class="profile-box">
        <div class="profile-header">
          <h1>个人中心</h1>
          <p class="subtitle">管理您的账户信息</p>
        </div>

        <div class="profile-content">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img 
                :src="userInfo.avatar || defaultAvatar" 
                alt="头像" 
                class="avatar-img"
                @error="handleAvatarError"
              />
              <div class="avatar-overlay">
                <label for="avatar-upload" class="upload-label">
                  <span class="upload-icon">📷</span>
                  <span class="upload-text">更换头像</span>
                </label>
                <input 
                  type="file" 
                  id="avatar-upload" 
                  accept="image/*" 
                  @change="handleAvatarUpload"
                  style="display: none;"
                />
              </div>
            </div>
            <div class="avatar-info">
              <h2>{{ userInfo.username || '用户' }}</h2>
              <p class="user-role">{{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}</p>
            </div>
          </div>

          <!-- 信息表单 -->
          <div class="form-section">
            <div class="form-group">
              <label for="username">
                <span class="label-icon">👤</span>
                用户名：
              </label>
              <div class="input-wrapper-with-hint">
                <input
                  type="text"
                  id="username"
                  v-model="form.username"
                  placeholder="请输入用户名(2-10位)"
                  class="form-input"
                  :disabled="!isEditing"
                  maxlength="10"
                />
                <div v-if="isEditing && form.username !== userInfo.username" class="field-hint">
                  <span class="hint-icon">⚠️</span>
                  <span class="hint-text">修改用户名需要验证密码并检查是否重复</span>
                </div>
              </div>
            </div>

            <div class="form-group">
              <label for="phone">
                <span class="label-icon">📱</span>
                手机号：
              </label>
              <div class="input-with-action">
                <input
                  type="tel"
                  id="phone"
                  v-model="form.phone"
                  placeholder="请输入手机号"
                  class="form-input"
                  :disabled="!isEditing"
                />
                <button 
                  v-if="isEditing && form.phone !== userInfo.phone" 
                  @click="handleChangePhone"
                  class="verify-btn"
                >
                  验证
                </button>
              </div>
            </div>

            <div class="form-group">
              <label for="email">
                <span class="label-icon">📧</span>
                邮箱：
              </label>
              <div class="input-with-action">
                <input
                  type="email"
                  id="email"
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  class="form-input"
                  :disabled="!isEditing"
                />
                <button 
                  v-if="isEditing && form.email !== userInfo.email" 
                  @click="handleChangeEmail"
                  class="verify-btn"
                >
                  验证
                </button>
              </div>
            </div>

            <div class="form-group" v-if="isEditing && (needPasswordVerify || isChangingPassword)">
              <label for="password">
                <span class="label-icon">🔒</span>
                当前密码：
              </label>
              <input
                type="password"
                id="password"
                v-model="passwordVerify"
                placeholder="请输入当前密码进行验证"
                class="form-input"
              />
            </div>

            <div class="form-group" v-if="isChangingPassword">
              <label for="newPassword">
                <span class="label-icon">🔑</span>
                新密码：
              </label>
              <input
                type="password"
                id="newPassword"
                v-model="form.newPassword"
                placeholder="请输入新密码(6-16位)"
                class="form-input"
              />
            </div>

            <div class="form-group" v-if="isChangingPassword">
              <label for="confirmPassword">
                <span class="label-icon">✅</span>
                确认密码：
              </label>
              <input
                type="password"
                id="confirmPassword"
                v-model="form.confirmPassword"
                placeholder="请再次输入新密码"
                class="form-input"
              />
            </div>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <button 
                v-if="!isEditing" 
                @click="startEdit" 
                class="edit-btn"
              >
                <span class="btn-icon">✏️</span>
                编辑信息
              </button>
              <div v-else class="edit-actions">
                <button 
                  @click="handleChangePassword" 
                  class="change-password-btn"
                  v-if="!isChangingPassword"
                >
                  <span class="btn-icon">🔑</span>
                  修改密码
                </button>
                <button 
                  @click="saveChanges" 
                  class="save-btn"
                  :disabled="isSaving"
                >
                  <span class="btn-icon">💾</span>
                  {{ isSaving ? '保存中...' : '保存修改' }}
                </button>
                <button 
                  @click="cancelEdit" 
                  class="cancel-btn"
                  :disabled="isSaving"
                >
                  <span class="btn-icon">❌</span>
                  取消
                </button>
              </div>
            </div>

            <!-- 错误提示 -->
            <div v-if="errorMessage" class="error-message">
              {{ errorMessage }}
            </div>

            <!-- 成功提示 -->
            <div v-if="successMessage" class="success-message">
              {{ successMessage }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '@/config/api.config.js';
import axios from 'axios';

export default {
  name: 'UserProfile',
  data() {
    return {
      userInfo: {
        uid: null,
        username: '',
        phone: '',
        email: '',
        avatar: '',
        role: 'user'
      },
      form: {
        username: '',
        phone: '',
        email: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordVerify: '',
      isEditing: false,
      isChangingPassword: false,
      needPasswordVerify: false,
      isSaving: false,
      errorMessage: '',
      successMessage: '',
      defaultAvatar: 'https://via.placeholder.com/150/4b6cb7/FFFFFF?text=User'
    };
  },
  mounted() {
    this.loadUserInfo();
  },
  methods: {
    async loadUserInfo() {
      const uid = localStorage.getItem('userId');
      if (!uid) {
        this.errorMessage = '未找到用户ID，请重新登录';
        setTimeout(() => {
          this.$router.push('/login');
        }, 2000);
        return;
      }

      try {
        const response = await axios.get(apiConfig.user.getUserById(uid));
        if (response.data.code === 1) {
          this.userInfo = response.data.data;
          this.form = {
            username: this.userInfo.username || '',
            phone: this.userInfo.phone || '',
            email: this.userInfo.email || '',
            newPassword: '',
            confirmPassword: ''
          };
        } else {
          this.errorMessage = response.data.msg || '获取用户信息失败';
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.errorMessage = '获取用户信息失败，请稍后重试';
      }
    },

    startEdit() {
      this.isEditing = true;
      this.errorMessage = '';
      this.successMessage = '';
    },

    cancelEdit() {
      this.isEditing = false;
      this.isChangingPassword = false;
      this.needPasswordVerify = false;
      this.passwordVerify = '';
      this.form = {
        username: this.userInfo.username || '',
        phone: this.userInfo.phone || '',
        email: this.userInfo.email || '',
        newPassword: '',
        confirmPassword: ''
      };
      this.errorMessage = '';
      this.successMessage = '';
    },

    handleChangePassword() {
      this.isChangingPassword = true;
      this.needPasswordVerify = true;
    },

    async handleChangePhone() {
      // 换绑手机号需要密码验证
      if (!this.passwordVerify) {
        this.errorMessage = '请先输入当前密码进行验证';
        this.needPasswordVerify = true;
        return;
      }

      // 验证密码
      const isValid = await this.verifyPassword();
      if (!isValid) {
        return;
      }

      // 这里可以添加手机号验证码验证逻辑
      this.successMessage = '手机号修改功能需要短信验证，请先保存其他信息';
    },

    async handleChangeEmail() {
      // 换绑邮箱需要密码验证
      if (!this.passwordVerify) {
        this.errorMessage = '请先输入当前密码进行验证';
        this.needPasswordVerify = true;
        return;
      }

      // 验证密码
      const isValid = await this.verifyPassword();
      if (!isValid) {
        return;
      }

      this.successMessage = '邮箱修改功能需要邮箱验证，请先保存其他信息';
    },

    async verifyPassword() {
      if (!this.passwordVerify) {
        this.errorMessage = '请输入当前密码';
        return false;
      }

      try {
        const uid = localStorage.getItem('userId');
        
        // 注意：后端没有单独的验证密码接口
        // 方案1：通过尝试登录来验证密码（使用用户名登录）
        // 方案2：直接修改，让后端验证（但这样会先修改再验证，不太好）
        // 方案3：暂时跳过验证，在保存时让后端验证
        
        // 使用方案1：通过用户名登录接口验证密码
        // 获取当前用户名
        const username = this.userInfo.username || localStorage.getItem('username');
        if (!username) {
          this.errorMessage = '无法获取用户名，请重新登录';
          return false;
        }

        // 尝试使用用户名和密码登录来验证
        const response = await axios.get(apiConfig.user.usernameLogin, {
          params: {
            username: username,
            password: this.passwordVerify
          }
        });

        if (response.data && response.data.code === 1) {
          // 密码验证成功
          this.errorMessage = '';
          return true;
        } else {
          // 密码验证失败
          this.errorMessage = response.data?.msg || '密码验证失败';
          return false;
        }
      } catch (error) {
        console.error('密码验证失败:', error);
        if (error.response && error.response.data) {
          this.errorMessage = error.response.data.msg || '密码验证失败';
        } else {
          this.errorMessage = '密码验证失败，请稍后重试';
        }
        return false;
      }
    },

    async handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (!file) return;

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        this.errorMessage = '请选择图片文件';
        return;
      }

      // 验证文件大小（限制5MB）
      if (file.size > 5 * 1024 * 1024) {
        this.errorMessage = '图片大小不能超过5MB';
        return;
      }

      this.isSaving = true;
      this.errorMessage = '';

      try {
        const formData = new FormData();
        formData.append('file', file);

        const response = await axios.put(apiConfig.user.updateAvatar, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        if (response.data.code === 1) {
          this.userInfo.avatar = response.data.data;
          this.successMessage = '头像上传成功';
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        } else {
          this.errorMessage = response.data.msg || '头像上传失败';
        }
      } catch (error) {
        console.error('头像上传失败:', error);
        this.errorMessage = '头像上传失败，请稍后重试';
      } finally {
        this.isSaving = false;
      }
    },

    handleAvatarError(event) {
      event.target.src = this.defaultAvatar;
    },

    // 检查用户名是否已存在
    async checkUsernameExists(username) {
      if (!username || username.trim() === '') {
        return false;
      }

      // 如果用户名没有变化，不需要检查
      if (username === this.userInfo.username) {
        return false;
      }

      try {
        const response = await axios.get(apiConfig.user.checkUsername, {
          params: { username: username }
        });

        // code === 0 表示用户名已存在
        if (response.data.code === 0) {
          return true;
        }
        return false;
      } catch (error) {
        console.error('检查用户名失败:', error);
        // 如果检查失败，为了安全起见，不允许修改
        this.errorMessage = '检查用户名失败，请稍后重试';
        return true; // 返回true阻止修改
      }
    },

    async saveChanges() {
      this.errorMessage = '';
      this.successMessage = '';

      // 验证表单
      if (!this.form.username || this.form.username.trim() === '') {
        this.errorMessage = '用户名不能为空';
        return;
      }

      // 验证用户名长度
      if (this.form.username.length < 2 || this.form.username.length > 10) {
        this.errorMessage = '用户名长度为2-10位';
        return;
      }

      // 如果修改了用户名，需要验证密码和检查用户名是否重复
      if (this.form.username !== this.userInfo.username) {
        if (!this.passwordVerify) {
          this.errorMessage = '修改用户名需要验证当前密码';
          this.needPasswordVerify = true;
          return;
        }

        // 验证密码
        const isValid = await this.verifyPassword();
        if (!isValid) {
          return;
        }

        // 检查用户名是否已存在
        const usernameExists = await this.checkUsernameExists(this.form.username);
        if (usernameExists) {
          this.errorMessage = '用户名已存在，请使用其他用户名';
          return;
        }
      }

      // 如果修改了手机号，需要密码验证和额外验证（预留接口）
      if (this.form.phone !== this.userInfo.phone) {
        if (!this.passwordVerify) {
          this.errorMessage = '修改手机号需要验证当前密码';
          this.needPasswordVerify = true;
          return;
        }

        // 验证密码
        const isValid = await this.verifyPassword();
        if (!isValid) {
          return;
        }

        // 验证手机号格式
        const phoneRegex = /^1[3-9]\d{9}$/;
        if (!phoneRegex.test(this.form.phone)) {
          this.errorMessage = '请输入有效的手机号';
          return;
        }

        // 检查手机号是否已被其他用户使用
        try {
          const response = await axios.get(apiConfig.user.checkPhone, {
            params: { phone: this.form.phone }
          });

          // code === 0 表示手机号已存在
          if (response.data.code === 0) {
            // 检查是否是当前用户的手机号
            if (this.form.phone !== this.userInfo.phone) {
              this.errorMessage = '该手机号已被注册，请使用其他手机号';
              return;
            }
          }
        } catch (error) {
          console.error('检查手机号失败:', error);
          this.errorMessage = '检查手机号失败，请稍后重试';
          return;
        }

        // 预留：手机号修改需要短信验证（目前接口未提供）
        // 这里可以添加短信验证码验证逻辑
        this.successMessage = '提示：手机号修改需要短信验证，当前仅保存到数据库，请确保手机号正确';
      }

      // 如果修改了邮箱，需要密码验证和额外验证（预留接口）
      if (this.form.email !== this.userInfo.email) {
        if (!this.passwordVerify) {
          this.errorMessage = '修改邮箱需要验证当前密码';
          this.needPasswordVerify = true;
          return;
        }

        // 验证密码
        const isValid = await this.verifyPassword();
        if (!isValid) {
          return;
        }

        // 验证邮箱格式
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (this.form.email && !emailRegex.test(this.form.email)) {
          this.errorMessage = '请输入有效的邮箱地址';
          return;
        }

        // 预留：邮箱修改需要邮箱验证（目前接口未提供）
        // 这里可以添加邮箱验证码验证逻辑
        this.successMessage = '提示：邮箱修改需要邮箱验证，当前仅保存到数据库，请确保邮箱正确';
      }

      // 如果修改密码，验证新密码
      if (this.isChangingPassword) {
        if (!this.form.newPassword || this.form.newPassword.length < 6 || this.form.newPassword.length > 16) {
          this.errorMessage = '新密码长度为6-16位';
          return;
        }

        if (this.form.newPassword !== this.form.confirmPassword) {
          this.errorMessage = '两次输入的密码不一致';
          return;
        }

        // 验证当前密码
        const isValid = await this.verifyPassword();
        if (!isValid) {
          return;
        }
      }

      this.isSaving = true;

      try {
        const uid = localStorage.getItem('userId');
        
        // 重要：后端updateById会更新所有字段（username, password, email, phone, status, avatar, role）
        // 如果某个字段为null，会被设置为null，导致数据库约束错误
        // 所以必须包含所有必填字段，使用原有值或新值
        
        // 确保username不为空
        const username = (this.form.username && this.form.username.trim()) || (this.userInfo.username && this.userInfo.username.trim()) || '';
        if (!username) {
          this.errorMessage = '用户名不能为空，请先设置用户名';
          this.isSaving = false;
          return;
        }

        const updateData = {
          uid: parseInt(uid || this.userInfo.uid || this.userInfo.id),
          // 必须包含username，因为数据库约束不允许为null
          username: username,
          // 包含其他字段，使用新值（如果有变化）或保持原有值
          phone: (this.form.phone !== undefined && this.form.phone !== null && this.form.phone !== '') 
            ? this.form.phone 
            : (this.userInfo.phone || null),
          email: (this.form.email !== undefined && this.form.email !== null && this.form.email !== '') 
            ? this.form.email 
            : (this.userInfo.email || null),
          // 保持原有状态和角色不变（这些字段不应该被用户修改）
          status: (this.userInfo.status !== undefined && this.userInfo.status !== null) ? this.userInfo.status : 1,
          avatar: this.userInfo.avatar || null,
          role: this.userInfo.role || 'user'
        };

        // 如果修改了密码，添加新密码
        if (this.isChangingPassword && this.form.newPassword) {
          updateData.password = this.form.newPassword;
        }
        // 注意：如果不修改密码，不传password字段
        // 但根据后端SQL，可能会将password设置为null
        // 如果后端要求必须传password，可能需要特殊处理

        console.log('准备更新的数据:', updateData);

        // 使用POST请求（根据后端代码）
        const response = await axios.post(apiConfig.user.updateUserInfo, updateData);

        if (response.data.code === 1) {
          this.successMessage = '信息修改成功';
          this.isEditing = false;
          this.isChangingPassword = false;
          this.needPasswordVerify = false;
          this.passwordVerify = '';
          this.form.newPassword = '';
          this.form.confirmPassword = '';

          // 重新加载用户信息
          await this.loadUserInfo();

          // 更新localStorage中的用户名
          if (this.form.username !== this.userInfo.username) {
            localStorage.setItem('username', this.form.username);
            // 触发App.vue更新
            window.dispatchEvent(new Event('storage'));
          }

          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        } else {
          this.errorMessage = response.data.msg || '修改失败';
        }
      } catch (error) {
        console.error('保存失败:', error);
        this.errorMessage = '保存失败，请稍后重试';
      } finally {
        this.isSaving = false;
      }
    }
  }
};
</script>

<style scoped>
.user-profile-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 30%, #90caf9 70%, #64b5f6 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.profile-container {
  width: 100%;
  max-width: 800px;
  position: relative;
  z-index: 2;
}

.back-home {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 3;
}

.back-link {
  background: white;
  color: #4b6cb7;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 0;
  border: 2px solid #4b6cb7;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 3px 10px rgba(75, 108, 183, 0.15);
}

.back-link:hover {
  background: #e3f2fd;
  color: #182848;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(75, 108, 183, 0.25);
}

.back-icon {
  font-size: 16px;
  font-weight: bold;
}

.profile-box {
  background: white;
  border-radius: 0;
  padding: 50px;
  box-shadow: 0 10px 40px rgba(75, 108, 183, 0.2);
  border: 2px solid #4b6cb7;
  position: relative;
  overflow: hidden;
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.profile-header h1 {
  color: #182848;
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: 900;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  position: relative;
}

.profile-header h1::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #4b6cb7, #182848);
  border-radius: 0;
}

.subtitle {
  color: #4b6cb7;
  font-size: 16px;
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #f8fbff 0%, #e3f2fd 100%);
  border: 2px solid #bbdefb;
  border-radius: 0;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 0;
  overflow: hidden;
  border: 3px solid #4b6cb7;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(75, 108, 183, 0.3);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.upload-label {
  color: white;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  font-weight: 600;
}

.upload-icon {
  font-size: 24px;
}

.avatar-info h2 {
  color: #182848;
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 700;
}

.user-role {
  color: #4b6cb7;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #182848;
  font-weight: 600;
  font-size: 14px;
}

.label-icon {
  font-size: 16px;
}

.form-input {
  width: 100%;
  padding: 14px;
  border: 2px solid #bbdefb;
  border-radius: 0;
  font-size: 16px;
  background: #f8fbff;
  color: #333;
  transition: all 0.3s ease;
}

.form-input:hover:not(:disabled) {
  border-color: #90caf9;
  background: #fff;
}

.form-input:focus:not(:disabled) {
  outline: none;
  border-color: #4b6cb7;
  background: white;
  box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.1);
}

.form-input:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.input-wrapper-with-hint {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field-hint {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #ff9800;
  padding: 5px 10px;
  background: #fff3e0;
  border: 1px solid #ffb74d;
  border-radius: 0;
}

.hint-icon {
  font-size: 14px;
}

.hint-text {
  font-weight: 500;
}

.input-with-action {
  display: flex;
  gap: 10px;
  align-items: center;
}

.verify-btn {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.verify-btn:hover {
  background: linear-gradient(135deg, #182848 0%, #0f1419 100%);
  transform: translateY(-2px);
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.edit-btn,
.save-btn,
.cancel-btn,
.change-password-btn {
  padding: 14px 28px;
  border: none;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-btn {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}

.edit-btn:hover {
  background: linear-gradient(135deg, #182848 0%, #0f1419 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(75, 108, 183, 0.3);
}

.save-btn {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: white;
}

.save-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #388e3c 0%, #2e7d32 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.save-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.cancel-btn {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
}

.cancel-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #d32f2f 0%, #c62828 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

.change-password-btn {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: white;
}

.change-password-btn:hover {
  background: linear-gradient(135deg, #f57c00 0%, #e65100 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
}

.edit-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.btn-icon {
  font-size: 18px;
}

.error-message {
  color: #f44336;
  font-size: 14px;
  margin-top: 10px;
  padding: 10px;
  background: #ffebee;
  border: 1px solid #f44336;
  border-radius: 0;
}

.success-message {
  color: #4caf50;
  font-size: 14px;
  margin-top: 10px;
  padding: 10px;
  background: #e8f5e9;
  border: 1px solid #4caf50;
  border-radius: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-box {
    padding: 30px;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .action-buttons {
    flex-direction: column;
  }

  .edit-btn,
  .save-btn,
  .cancel-btn,
  .change-password-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>

