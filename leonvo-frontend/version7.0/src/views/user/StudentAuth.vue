<template>
  <div class="student-auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="title">
          <span class="icon">🎓</span>
          <div>
            <h2>学生认证</h2>
            <p>提交学生信息，享受学生专属优惠9折！</p>
          </div>
        </div>
        <button class="back-btn" @click="$router.back()">返回</button>
      </div>

      <div class="form-group">
        <label>学校名称</label>
        <input v-model="studentForm.school" type="text" placeholder="请输入学校全称" />
      </div>

      <div class="form-group">
        <label>学号</label>
        <input v-model="studentForm.studentId" type="text" placeholder="请输入学号" />
      </div>

      <div class="form-group">
        <label>学生证/录取通知书</label>
        <div class="upload-box" @click="$refs.cardInput.click()">
          <div v-if="studentForm.cardImage" class="preview-image">
            <img :src="studentForm.cardImage" alt="学生证预览" />
            <span class="reupload-text">点击重新上传</span>
          </div>
          <div v-else class="upload-placeholder">
            <span class="upload-icon">📷</span>
            <span>点击上传</span>
          </div>
        </div>
        <input
          ref="cardInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleStudentCardUpload"
        />
      </div>

      <div class="actions">
        <button class="submit-btn" :disabled="verifying" @click="submitStudentVerify">
          {{ verifying ? '提交中...' : '提交认证' }}
        </button>
      </div>

      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'StudentAuth',
  data() {
    return {
      studentForm: {
        school: '',
        studentId: '',
        cardImage: ''
      },
      verifying: false,
      errorMessage: '',
      successMessage: '',
      status: -1 // -1: none, 0: pending, 1: approved, 2: rejected
    };
  },
  methods: {
    handleStudentCardUpload(event) {
      const file = event.target.files && event.target.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = () => {
        this.studentForm.cardImage = reader.result;
      };
      reader.readAsDataURL(file);
    },

    async submitStudentVerify() {
      this.errorMessage = '';
      this.successMessage = '';

      if (!this.studentForm.school || !this.studentForm.studentId || !this.studentForm.cardImage) {
        this.errorMessage = '请完善学校名称、学号并上传学生证照片';
        return;
      }
      
      const user = JSON.parse(localStorage.getItem('userInfo') || '{}');
      if (!user.uid && !user.id) {
        this.errorMessage = '请先登录';
        return;
      }

      this.verifying = true;
      try {
        const res = await axios.post(apiConfig.student.verify, {
            uid: user.uid || user.id,
            school: this.studentForm.school,
            studentId: this.studentForm.studentId,
            cardImage: this.studentForm.cardImage
        });

        if (res.data.code === 1) {
            this.successMessage = '认证已提交，正在审核中';
            this.status = 0;
        } else {
            this.errorMessage = res.data.msg || '提交失败';
        }
      } catch (error) {
        console.error('提交学生认证失败:', error);
        this.errorMessage = '提交学生认证失败，请稍后重试';
      } finally {
        this.verifying = false;
      }
    }
  }
};
</script>

<style scoped>
.student-auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 30%, #90caf9 70%, #64b5f6 100%);
  padding: 20px;
}

.auth-card {
  background: #fff;
  width: 480px;
  padding: 28px;
  border-radius: 6px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #d0ddff;
}

.auth-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.title {
  display: flex;
  gap: 10px;
  align-items: center;
}

.title h2 {
  margin: 0;
  color: #182848;
}

.title p {
  margin: 2px 0 0;
  color: #4b6cb7;
}

.icon {
  font-size: 26px;
}

.back-btn {
  background: #182848;
  color: #fff;
  border: none;
  padding: 8px 14px;
  cursor: pointer;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}

.form-group label {
  color: #182848;
  font-weight: 600;
}

.form-group input {
  padding: 12px;
  border: 1px solid #d0ddff;
  border-radius: 4px;
  background: #f8fbff;
}

.upload-box {
  height: 160px;
  border: 2px dashed #bbdefb;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8fbff;
  overflow: hidden;
}

.upload-box:hover {
  border-color: #4b6cb7;
  background: #e3f2fd;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #64b5f6;
}

.upload-icon {
  font-size: 28px;
}

.preview-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.reupload-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  text-align: center;
  padding: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-image:hover .reupload-text {
  opacity: 1;
}

.actions {
  margin-top: 12px;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #4b6cb7, #182848);
  color: #fff;
  border: none;
  cursor: pointer;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: #f44336;
  margin-top: 10px;
  background: #ffebee;
  padding: 8px;
}

.success-message {
  color: #4caf50;
  margin-top: 10px;
  background: #e8f5e9;
  padding: 8px;
}
</style>

