<template>
  <div class="notification-page">
    <div class="page-container">
      <div class="page-header">
        <h1>消息中心</h1>
        <div class="header-actions">
          <button @click="markAllAsRead" class="mark-read-btn" :disabled="!hasUnread">
            <span class="icon">✓</span> 全部标记为已读
          </button>
        </div>
      </div>

      <div class="notification-tabs">
        <button 
          :class="['tab-btn', { active: currentTab === 'all' }]" 
          @click="currentTab = 'all'"
        >
          全部消息
        </button>
        <button 
          :class="['tab-btn', { active: currentTab === 'unread' }]" 
          @click="currentTab = 'unread'"
        >
          未读消息
          <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
        </button>
      </div>

      <div class="notification-list" v-if="filteredList.length > 0">
        <div 
          v-for="item in filteredList" 
          :key="item.id" 
          class="notification-card"
          :class="{ unread: !item.read }"
        >
          <div class="card-icon">
            <span v-if="item.type === 'success'" class="icon-success">✅</span>
            <span v-else-if="item.type === 'error'" class="icon-error">❌</span>
            <span v-else class="icon-info">📢</span>
          </div>
          <div class="card-content">
            <div class="card-header">
              <span class="card-title">{{ item.title }}</span>
              <span class="card-time">{{ item.time }}</span>
            </div>
            <p class="card-message">{{ item.message }}</p>
          </div>
          <div class="card-actions" v-if="!item.read">
             <button @click="markAsRead(item)" class="read-btn" title="标记为已读">
               🔵
             </button>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <div class="empty-icon">📭</div>
        <h3>暂无{{ currentTab === 'unread' ? '未读' : '' }}消息</h3>
        <p>当有新的通知时，会出现在这里</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'NotificationView',
  data() {
    return {
      currentTab: 'all',
      list: []
    };
  },
  computed: {
    filteredList() {
      if (this.currentTab === 'unread') {
        return this.list.filter(item => !item.read);
      }
      return this.list;
    },
    unreadCount() {
      return this.list.filter(item => !item.read).length;
    },
    hasUnread() {
      return this.unreadCount > 0;
    }
  },
  mounted() {
    this.fetchNotifications();
  },
  methods: {
    fetchNotifications() {
      // 获取用户ID
      const userInfoStr = localStorage.getItem('userInfo');
      const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {};
      const uid = userInfo.uid || userInfo.id || localStorage.getItem('userId');

      if (!uid) return;

      // 检查学生认证状态
      this.checkStudentStatus(uid);
    },

    checkStudentStatus(uid) {
      const readStatus = localStorage.getItem('student_auth_read_status_v3');
      
      axios.get(apiConfig.student.getStatus(uid))
        .then(res => {
          if (res.data.code === 1 && res.data.data) {
            const status = res.data.data.status;
            // status: 1 (Approved), 2 (Rejected)
            
            if (status === 1 || status === 2) {
              const isRead = String(readStatus) === String(status);
              
              // 模拟消息数据
              const notification = {
                id: 'auth_' + status,
                title: '学生认证通知',
                message: status === 1 
                  ? '恭喜您！您的学生认证申请已通过审核。您现在可以享受学生专属优惠了！' 
                  : '很抱歉，您的学生认证申请未通过。请检查信息后重新提交。',
                time: new Date().toLocaleString(), // 实际应使用后端时间
                type: status === 1 ? 'success' : 'error',
                read: isRead,
                rawStatus: status
              };

              this.list = [notification];
            }
          }
        })
        .catch(console.error);
    },

    markAsRead(item) {
      item.read = true;
      if (item.rawStatus) {
        localStorage.setItem('student_auth_read_status_v3', item.rawStatus);
      }
      // 触发App.vue更新红点
      window.dispatchEvent(new Event('login-status-changed'));
    },

    markAllAsRead() {
      this.list.forEach(item => {
        if (!item.read) {
          this.markAsRead(item);
        }
      });
    }
  }
};
</script>

<style scoped>
.notification-page {
  min-height: 80vh;
  background: #f5f7fa;
  padding: 40px 20px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  overflow: hidden;
}

.page-header {
  padding: 20px 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.mark-read-btn {
  background: white;
  border: 1px solid #dcdfe6;
  color: #606266;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.mark-read-btn:hover:not(:disabled) {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

.mark-read-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.notification-tabs {
  padding: 0 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  gap: 20px;
}

.tab-btn {
  padding: 15px 0;
  background: none;
  border: none;
  font-size: 16px;
  color: #606266;
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-btn.active {
  color: #409eff;
  font-weight: 500;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #409eff;
}

.badge {
  background: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 0 6px;
  border-radius: 10px;
  line-height: 18px;
}

.notification-list {
  padding: 20px;
}

.notification-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
  border-radius: 4px;
}

.notification-card:hover {
  background: #f9fafc;
}

.notification-card.unread {
  background: #ecf5ff;
}

.card-icon {
  font-size: 24px;
  padding-top: 2px;
}

.card-content {
  flex: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-title {
  font-weight: bold;
  color: #303133;
  font-size: 16px;
}

.card-time {
  color: #909399;
  font-size: 14px;
}

.card-message {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}

.read-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.read-btn:hover {
  opacity: 1;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  font-weight: normal;
  color: #606266;
}
</style>