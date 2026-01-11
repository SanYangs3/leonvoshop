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
              <div class="roles-container">
                <!-- 仅管理员显示管理员标签 -->
                <p v-if="userInfo.role === 'admin'" class="user-role">管理员</p>
                
                <!-- 仅当学生认证通过时显示学生标签 -->
                <span v-if="studentStatus && studentStatus.status === 1" class="student-badge">
                  学生
                </span>
              </div>
            </div>
          </div>

          <!-- 会员等级 -->
          <div class="form-section membership-card">
            <div class="membership-header">
              <div class="membership-title">
                <span class="section-icon">🏅</span>
                <span>会员等级</span>
              </div>
              <button class="benefit-btn" @click="showBenefitModal = true">
                查看等级优惠
              </button>
            </div>
            <div class="membership-summary">
              <span class="level-badge" :style="{ background: membershipInfo.color }">
                {{ membershipInfo.label }}
              </span>
              <span class="points-text">当前积分：{{ membershipInfo.points }}</span>
              <span class="discount-text">当前折扣：{{ membershipInfo.discountText }}</span>
            </div>
            <div class="membership-progress">
              <div class="progress-track">
                <div
                  class="progress-bar"
                  :style="{ width: membershipInfo.progress + '%', background: membershipInfo.color }"
                ></div>
              </div>
              <div class="progress-info">
                <span v-if="membershipInfo.diffToNext > 0">
                  距离 {{ membershipInfo.nextLabel }} 还差 {{ membershipInfo.diffToNext }} 分
                </span>
                <span v-else>已是最高等级</span>
              </div>
              <div class="next-level" v-if="membershipInfo.nextThreshold">
                下一等级门槛：{{ membershipInfo.nextThreshold }} 分
              </div>
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

        <!-- 等级优惠弹窗 -->
        <div v-if="showBenefitModal" class="benefit-modal-backdrop">
          <div class="benefit-modal">
            <h3>会员等级优惠</h3>
            <ul class="benefit-list">
              <li v-for="tier in membershipTiers" :key="tier.key">
                <span>{{ tier.label }}</span>
                <span>{{ tier.discountText }}</span>
              </li>
            </ul>
            <button class="modal-close-btn" @click="showBenefitModal = false">我知道了</button>
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
      membershipInfo: {
        label: '普通会员',
        levelKey: 'normal',
        points: 0,
        discountText: '不打折',
        color: '#64b5f6',
        progress: 0,
        diffToNext: 3000,
        nextThreshold: 3000,
        nextLabel: 'VIP会员'
      },
      showBenefitModal: false,
      errorMessage: '',
      successMessage: '',
      defaultAvatar: 'https://via.placeholder.com/150/4b6cb7/FFFFFF?text=User'
    };
  },
  mounted() {
    this.loadUserInfo();
    this.fetchStudentStatus();
  },
  computed: {
    membershipTiers() {
      return this.getMembershipTiers();
    }
  },
  methods: {
    normalizeToString(value) {
      if (value === null || value === undefined) return '';
      return String(value);
    },

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
          const data = response.data.data || {};
          const normalizedUser = {
            ...this.userInfo,
            ...data,
            username: this.normalizeToString(data.username),
            phone: this.normalizeToString(data.phone),
            email: this.normalizeToString(data.email)
          };
          this.userInfo = normalizedUser;
          this.form = {
            username: normalizedUser.username,
            phone: normalizedUser.phone,
            email: normalizedUser.email,
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

      // 拉取会员信息
      this.fetchMembership();
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
        const username = this.normalizeToString(this.userInfo.username) || this.normalizeToString(localStorage.getItem('username'));
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
        // 添加 uid 参数，确保后端知道是哪个用户
        const uid = localStorage.getItem('userId');
        formData.append('uid', uid);
        formData.append('file', file);

        // 后端接口使用 POST 提交头像文件
        const response = await axios.post(apiConfig.user.updateAvatar, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });

        if (response.data.code === 1) {
          this.userInfo.avatar = response.data.data;
          this.successMessage = '头像上传成功';
          
          // 更新 localStorage 中的头像信息（如果有存储的话）
          // 并触发事件通知其他组件
          window.dispatchEvent(new Event('storage'));
          
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        } else {
          this.errorMessage = response.data.msg || '头像上传失败';
          
          // 如果后端上传失败，回退到本地预览（仅作为兜底，实际应以服务器为准）
          // const reader = new FileReader();
          // reader.onload = (e) => {
          //   this.userInfo.avatar = e.target.result;
          // };
          // reader.readAsDataURL(file);
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

    getMembershipTiers() {
      return [
        { key: 'normal', label: '普通会员', min: 0, max: 3000, discount: 1, color: '#64b5f6', discountText: '不打折' },
        { key: 'vip', label: 'VIP会员', min: 3000, max: 6000, discount: 0.95, color: '#7e57c2', discountText: '9.5折' },
        { key: 'svip', label: 'SVIP会员', min: 6000, max: 12000, discount: 0.9, color: '#ff9800', discountText: '9折' },
        { key: 'king', label: '金主爸爸', min: 12000, max: Infinity, discount: 0.8, color: '#fbc02d', discountText: '8折' }
      ];
    },

    calculateMembership(points = 0) {
      const tiers = this.getMembershipTiers();
      const current = tiers.find(t => points >= t.min && points < t.max) || tiers[tiers.length - 1];
      const currentIndex = tiers.indexOf(current);
      const next = tiers[currentIndex + 1];

      const lower = current.min;
      const upper = current.max;
      const progress = upper === Infinity ? 100 : Math.min(100, Math.max(0, ((points - lower) / (upper - lower)) * 100));
      const diffToNext = upper === Infinity ? 0 : Math.max(0, Math.ceil(upper - points));

      return {
        ...current,
        points,
        progress: Number(progress.toFixed(2)),
        diffToNext,
        nextThreshold: upper === Infinity ? null : upper,
        nextLabel: next ? next.label : null,
        discountText: current.discountText
      };
    },

    async fetchMembership() {
      const uid = localStorage.getItem('userId');
      if (!uid) return;
      try {
        const res = await axios.get(apiConfig.user.getUserRole(uid));
        if (res.data && res.data.code === 1) {
          const role = res.data.data || {};
          const points = Number(role.points || 0);
          this.membershipInfo = this.calculateMembership(points);
        }
      } catch (error) {
        console.error('获取会员信息失败:', error);
      }
    },

    async fetchStudentStatus() {
      const uid = localStorage.getItem('userId');
      if (!uid) return;
      try {
        const res = await axios.get(apiConfig.student.getStatus(uid));
        console.log('个人中心-学生认证状态:', res.data);
        if (res.data.code === 1) {
          this.studentStatus = res.data.data;
          // 强制刷新视图（如果需要）
          this.$forceUpdate();
        }
      } catch (e) {
        console.error('获取学生认证状态失败', e);
      }
    },

    // 检查用户名是否已存在
    async checkUsernameExists(username) {
      const value = this.normalizeToString(username);
      if (!value || value.trim() === '') {
        return false;
      }

      // 如果用户名没有变化，不需要检查
      if (value === this.normalizeToString(this.userInfo.username)) {
        return false;
      }

      try {
        const response = await axios.get(apiConfig.user.checkUsername, {
          params: { username: value }
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

      const usernameValue = this.normalizeToString(this.form.username);
      const currentUsername = this.normalizeToString(this.userInfo.username);
      const hasUsernameChanged = usernameValue !== currentUsername;

      // 验证表单
      if (!usernameValue || usernameValue.trim() === '') {
        this.errorMessage = '用户名不能为空';
        return;
      }

      // 验证用户名长度
      if (usernameValue.length < 2 || usernameValue.length > 10) {
        this.errorMessage = '用户名长度为2-10位';
        return;
      }

      // 如果修改了用户名，需要验证密码和检查用户名是否重复
      if (hasUsernameChanged) {
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
        const usernameExists = await this.checkUsernameExists(usernameValue);
        if (usernameExists) {
          this.errorMessage = '用户名已存在，请使用其他用户名';
          return;
        }
      }

      // 如果修改了手机号，需要密码验证和额外验证（预留接口）
      const phoneValue = this.normalizeToString(this.form.phone);
      const currentPhone = this.normalizeToString(this.userInfo.phone);
      const hasPhoneChanged = phoneValue !== currentPhone;

      if (hasPhoneChanged) {
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
        if (!phoneRegex.test(phoneValue)) {
          this.errorMessage = '请输入有效的手机号';
          return;
        }

        // 检查手机号是否已被其他用户使用
        try {
          const response = await axios.get(apiConfig.user.checkPhone, {
            params: { phone: phoneValue }
          });

          // code === 0 表示手机号已存在
          if (response.data.code === 0) {
            // 检查是否是当前用户的手机号
            if (hasPhoneChanged) {
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
      const emailValue = this.normalizeToString(this.form.email);
      const currentEmail = this.normalizeToString(this.userInfo.email);
      const hasEmailChanged = emailValue !== currentEmail;

      if (hasEmailChanged) {
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
        if (emailValue && !emailRegex.test(emailValue)) {
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
        const username = usernameValue.trim() || currentUsername;
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
          phone: (hasPhoneChanged && phoneValue !== '')
            ? phoneValue
            : (currentPhone || null),
          email: (hasEmailChanged && emailValue !== '')
            ? emailValue
            : (currentEmail || null),
          // 保持原有状态和角色不变（这些字段不应该被用户修改）
          status: (this.userInfo.status !== undefined && this.userInfo.status !== null) ? this.userInfo.status : 1,
          avatar: this.userInfo.avatar || null,
          role: this.userInfo.role || 'user'
        };

        // 重要修复：密码字段处理
        // 数据库约束要求 password 不能为空
        // 如果用户修改了密码，使用新密码
        if (this.isChangingPassword && this.form.newPassword) {
          updateData.password = this.form.newPassword;
        } else {
          // 如果没有修改密码，必须传回当前密码，否则后端会将密码更新为 NULL 导致报错
          // 由于我们前端没有存储明文密码，这里有两个方案：
          // 1. 如果后端支持不传 password 字段就不更新，那就不传（但目前的后端逻辑似乎是全量更新）
          // 2. 既然用户输入了验证密码 (passwordVerify)，我们可以使用这个密码作为当前密码传回
          // 注意：前提是 verifyPassword() 已经验证过这个密码是正确的
          
          if (this.passwordVerify) {
             updateData.password = this.passwordVerify;
          } else {
             // 极端情况：如果没修改密码且没输入验证密码（例如仅修改头像或非敏感信息不需要验证密码的情况）
             // 但本页面的逻辑是修改任何信息都需要验证密码，所以 this.passwordVerify 应该是有值的
             // 如果确实没有，尝试从 localStorage 获取（如果登录时保存了的话，通常不建议）
             // 或者提示用户必须输入密码
             this.errorMessage = '系统需要验证您的密码才能保存修改';
             this.needPasswordVerify = true;
             this.isSaving = false;
             return;
          }
        }

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

.membership-card {
  background: linear-gradient(135deg, #f8fbff 0%, #eef4ff 100%);
  border: 1px solid #d0ddff;
  padding: 20px;
}

.membership-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.membership-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #182848;
}

.benefit-btn {
  background: #182848;
  color: #fff;
  border: none;
  padding: 8px 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.benefit-btn:hover {
  background: #0f1419;
  transform: translateY(-1px);
}

.membership-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.level-badge {
  color: #fff;
  padding: 6px 12px;
  border-radius: 4px;
  font-weight: 700;
}

.points-text, .discount-text {
  color: #182848;
  font-weight: 600;
}

.membership-progress .progress-track {
  width: 100%;
  height: 10px;
  background: #e3e9ff;
  border-radius: 5px;
  overflow: hidden;
}

.membership-progress .progress-bar {
  height: 100%;
  border-radius: 5px;
  transition: width 0.3s ease;
}

.progress-info, .next-level {
  margin-top: 8px;
  color: #182848;
  font-weight: 600;
}

.benefit-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.benefit-modal {
  background: #fff;
  padding: 20px;
  width: 360px;
  border-radius: 6px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.benefit-modal h3 {
  margin: 0 0 10px 0;
  color: #182848;
}

.benefit-list {
  list-style: none;
  padding: 0;
  margin: 0 0 12px 0;
}

.benefit-list li {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px dashed #e0e0e0;
  color: #182848;
}

.modal-close-btn {
  width: 100%;
  padding: 10px;
  background: #182848;
  color: white;
  border: none;
  cursor: pointer;
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

.roles-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.student-badge {
  background: #f44336;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(244, 67, 54, 0.3);
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

