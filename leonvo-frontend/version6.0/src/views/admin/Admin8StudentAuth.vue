<template>
  <div class="student-auth-container">
    <div class="page-header">
      <h2>学生认证处理</h2>
      <div class="tabs">
        <button :class="{ active: currentTab === 'pending' }" @click="currentTab = 'pending'">
          待审核
        </button>
        <button :class="{ active: currentTab === 'all' }" @click="currentTab = 'all'">
          全部记录
        </button>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户ID</th>
            <th>学校</th>
            <th>学号</th>
            <th>学生证</th>
            <th>申请时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in displayedList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.uid }}</td>
            <td>{{ item.school }}</td>
            <td>{{ item.studentId }}</td>
            <td>
              <a v-if="item.cardImage" :href="item.cardImage" target="_blank" class="view-link">查看图片</a>
              <span v-else>无图片</span>
            </td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>
              <span :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</span>
            </td>
            <td>
              <div v-if="item.status === 0" class="actions">
                <button class="btn-approve" @click="audit(item, 1)">通过</button>
                <button class="btn-reject" @click="audit(item, 2)">拒绝</button>
              </div>
              <span v-else class="text-gray">已处理</span>
            </td>
          </tr>
          <tr v-if="displayedList.length === 0">
            <td colspan="8" class="text-center">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'Admin8StudentAuth',
  data() {
    return {
      currentTab: 'pending',
      list: [],
    }
  },
  computed: {
    displayedList() {
      if (this.currentTab === 'pending') {
        return this.list.filter(item => item.status === 0);
      }
      return this.list;
    }
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.fetchList();
    },
    fetchList() {
       axios.get(apiConfig.student.getAllList)
        .then(res => {
          if (res.data.code === 1) {
            this.list = res.data.data;
          }
        })
        .catch(console.error);
    },
    audit(item, status) {
      if (!confirm(`确定要${status === 1 ? '通过' : '拒绝'}该申请吗？`)) return;
      
      axios.post(apiConfig.student.audit, {
        id: item.id,
        status: status,
        uid: item.uid
      })
      .then(res => {
        if (res.data.code === 1) {
          alert('操作成功');
          this.fetchList();
        } else {
          alert('操作失败: ' + res.data.msg);
        }
      })
      .catch(err => {
        alert('网络错误');
        console.error(err);
      });
    },
    formatDate(time) {
      if (!time) return '';
      return new Date(time).toLocaleString();
    },
    getStatusText(status) {
      const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' };
      return map[status] || '未知';
    },
    getStatusClass(status) {
      const map = { 0: 'status-pending', 1: 'status-approved', 2: 'status-rejected' };
      return map[status];
    }
  },
  watch: {
    currentTab() {
      // In a real app with pagination, we might fetch different APIs here.
      // Since we fetch all, we just rely on computed property.
      // But re-fetching is good to ensure freshness.
      this.fetchList();
    }
  }
}
</script>

<style scoped>
.student-auth-container {
  background: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
.tabs button {
  padding: 8px 16px;
  margin-left: 10px;
  border: 1px solid #ddd;
  background: #f9f9f9;
  cursor: pointer;
  border-radius: 4px;
}
.tabs button.active {
  background: #3c8dbc;
  color: white;
  border-color: #367fa9;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th, .data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #f4f4f4;
}
.view-link {
  color: #3c8dbc;
  text-decoration: none;
}
.status-pending { color: #f39c12; }
.status-approved { color: #00a65a; }
.status-rejected { color: #dd4b39; }
.btn-approve {
  background: #00a65a;
  color: white;
  border: none;
  padding: 5px 10px;
  margin-right: 5px;
  border-radius: 3px;
  cursor: pointer;
}
.btn-reject {
  background: #dd4b39;
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
}
.text-center { text-align: center; }
.text-gray { color: #999; }
.view-btn {
  background: none;
  border: none;
  color: #3c8dbc;
  cursor: pointer;
  text-decoration: underline;
}
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.modal-content {
  background: white;
  padding: 10px;
  border-radius: 4px;
  max-width: 90%;
  max-height: 90%;
  overflow: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.modal-image {
  max-width: 100%;
  max-height: 80vh;
}
.modal-close {
  margin-top: 10px;
  padding: 5px 15px;
  background: #dd4b39;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 3px;
}
</style>
