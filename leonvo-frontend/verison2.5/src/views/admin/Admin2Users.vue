<template>
  <div class="management-section">
    <!-- 已移除顶部数据来源提示 -->

    <div class="section-header">
      <h3>用户管理 <span class="simulation-badge" v-if="isMockData">模拟数据</span></h3>
      <div class="section-actions">
        <!-- 添加用户按钮 -->
        <button class="btn-primary" @click="showAddUserModal = true">
          <span class="btn-icon">+</span>
          添加用户
        </button>

        <!-- 前端搜索功能 -->
        <div class="search-box">
          <input
              type="text"
              v-model="userSearch"
              placeholder="搜索用户名、邮箱或电话..."
              class="search-input"
          />
          <span class="search-icon">搜</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      正在加载用户数据...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      加载失败：{{ error }}
      <button @click="loadUsers" class="btn-retry">重试</button>
    </div>

    <div v-else class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>邮箱</th>
          <th>电话</th>
          <th>状态</th>
          <th>角色</th>
          <th>注册时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in filteredUsers" :key="user.uid">
          <td>{{ user.uid }}</td>
          <td>
            <div class="user-cell">
              <div class="user-avatar-sm">{{ user.username?.charAt(0) }}</div>
              <span>{{ user.username }}</span>
            </div>
          </td>
          <td>{{ user.email || '-' }}</td>
          <td>{{ user.phone || '-' }}</td>
          <td>
              <span :class="['status-badge', user.status ? 'active' : 'inactive']">
                {{ user.status ? '正常' : '禁用' }}
              </span>
          </td>
          <td>
              <span :class="['role-badge', user.role === 'admin' ? 'admin' : 'user']">
                {{ user.role === 'admin' ? '管理员' : '普通用户' }}
              </span>
          </td>
          <td>{{ formatDate(user.createTime) }}</td>
          <td>
            <div class="action-buttons">
              <!-- 编辑按钮 -->
              <button class="btn-edit" @click="editUser(user)">
                编辑
              </button>

              <!-- 状态切换按钮 -->
              <button
                  :class="['btn-status', user.status ? 'btn-disable' : 'btn-enable']"
                  @click="toggleUserStatus(user)"
              >
                {{ user.status ? '禁用' : '启用' }}
              </button>

              <!-- 查看详情按钮 -->
              <button class="btn-view" @click="viewUserDetail(user)">
                查看详情
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>



    <div class="table-footer">
      <div class="pagination-info">
        显示 {{ filteredUsers.length }} 条记录中的 1-{{ filteredUsers.length }} 条
        <span class="pagination-hint">（前端分页）</span>
        <span v-if="isMockData" class="mock-hint">【当前为模拟数据】</span>
      </div>
      <div class="pagination">
        <button class="pagination-btn" disabled>上一页</button>
        <span class="pagination-current">1</span>
        <button class="pagination-btn" disabled>下一页</button>
      </div>
    </div>

    <!-- 添加用户模态框 -->
    <div v-if="showAddUserModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>添加用户</h3>
          <button class="modal-close" @click="showAddUserModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-form">
            <div class="form-group">
              <label>用户名：</label>
              <input type="text" v-model="newUser.username" placeholder="请输入用户名" class="form-input">
            </div>
            <div class="form-group">
              <label>密码：</label>
              <input type="password" v-model="newUser.password" placeholder="请输入密码" class="form-input">
            </div>
            <div class="form-group">
              <label>邮箱：</label>
              <input type="email" v-model="newUser.email" placeholder="请输入邮箱" class="form-input">
            </div>
            <div class="form-group">
              <label>电话：</label>
              <input type="tel" v-model="newUser.phone" placeholder="请输入电话" class="form-input">
            </div>
            <div class="form-group">
              <label>角色：</label>
              <select v-model="newUser.role" class="form-input">
                <option value="user">普通用户</option>
                <option value="admin">管理员</option>
              </select>
            </div>
            <div class="form-actions">
              <button class="btn-cancel" @click="showAddUserModal = false">取消</button>
              <button class="btn-submit" @click="addUser">添加用户</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑用户模态框 -->
    <div v-if="showEditUserModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>编辑用户</h3>
          <button class="modal-close" @click="showEditUserModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-form">
            <div class="form-group">
              <label>ID (不可修改)：</label>
              <input type="text" v-model="selectedUser.uid" class="form-input" disabled style="background:#f5f5f5;">
            </div>
            <div class="form-group">
              <label>用户名：</label>
              <input type="text" v-model="selectedUser.username" class="form-input">
            </div>
            <div class="form-group">
              <label>邮箱：</label>
              <input type="email" v-model="selectedUser.email" class="form-input">
            </div>
            <div class="form-group">
              <label>电话：</label>
              <input type="tel" v-model="selectedUser.phone" class="form-input">
            </div>
            <div class="form-group">
              <label>角色：</label>
              <select v-model="selectedUser.role" class="form-input">
                <option value="user">普通用户</option>
                <option value="admin">管理员</option>
              </select>
            </div>
            <div class="form-actions">
              <button class="btn-cancel" @click="showEditUserModal = false">取消</button>
              <button class="btn-submit" @click="submitEditUser">保存修改</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户详情模态框 -->
    <div v-if="showDetailModal" class="modal-overlay">
      <div class="modal modal-lg">
        <div class="modal-header">
          <h3>用户详情</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="user-detail">
            <div class="detail-section">
              <h4>基本信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>用户ID：</label>
                  <span>{{ selectedUser.uid }}</span>
                </div>
                <div class="detail-item">
                  <label>用户名：</label>
                  <span>{{ selectedUser.username }}</span>
                </div>
                <div class="detail-item">
                  <label>邮箱：</label>
                  <span>{{ selectedUser.email || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>电话：</label>
                  <span>{{ selectedUser.phone || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>状态：</label>
                  <span :class="['status-badge', selectedUser.status ? 'active' : 'inactive']">
                    {{ selectedUser.status ? '正常' : '禁用' }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>角色：</label>
                  <span :class="['role-badge', selectedUser.role === 'admin' ? 'admin' : 'user']">
                    {{ selectedUser.role === 'admin' ? '管理员' : '普通用户' }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>注册时间：</label>
                  <span>{{ formatDate(selectedUser.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '@/config/api.config'
import axios from 'axios';

// 模拟数据 - 备用
const MOCK_USERS = [
  { uid: 1, username: 'zhangsan', email: 'zhangsan@example.com', phone: '13800138000', status: 1, role: 'user', createTime: '2023-11-01' },
  { uid: 2, username: 'lisi', email: 'lisi@example.com', phone: '13900139000', status: 1, role: 'admin', createTime: '2023-11-02' },
  { uid: 3, username: 'wangwu', email: 'wangwu@example.com', phone: '13700137000', status: 0, role: 'user', createTime: '2023-11-03' },
  { uid: 4, username: 'zhaoliu', email: 'zhaoliu@example.com', phone: '13600136000', status: 1, role: 'user', createTime: '2023-11-04' },
  { uid: 5, username: 'sunqi', email: 'sunqi@example.com', phone: '13500135000', status: 1, role: 'user', createTime: '2023-11-05' }
];

export default {
  name: 'AdminUsers',
  data() {
    return {
      userSearch: '',
      showAddUserModal: false,
      showEditUserModal: false,
      showDetailModal: false,
      selectedUser: null,
      loading: false,
      error: null,
      isMockData: false,

      newUser: {
        username: '',
        password: '',
        email: '',
        phone: '',
        role: 'user',
        status: 1
      },

      // 用户数据 - 初始为空，从API加载
      users: []
    }
  },
  computed: {
    filteredUsers() {
      // 前端搜索功能 - 无对应API，纯前端实现
      if (!this.userSearch) return this.users;
      const search = this.userSearch.toLowerCase();
      return this.users.filter(user =>
          user.username.toLowerCase().includes(search) ||
          (user.email && user.email.toLowerCase().includes(search)) ||
          (user.phone && user.phone.includes(search))
      );
    }
  },
  mounted() {
    this.loadUsers();
  },
  methods: {
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-';
      try {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN');
      } catch {
        return dateString;
      }
    },

    // 加载用户数据
    async loadUsers() {
      this.loading = true;
      this.error = null;

      try {
        // 尝试从API获取数据
        const response = await axios.get(apiConfig.user.getAllUsers);
        console.log('User API Response:', response.data); // 调试日志

        if (response.data.code === 1) {
          // 定义变量
          let userList = [];

          // 1. 尝试标准格式 response.data.data (Array)
          if (response.data && Array.isArray(response.data.data)) {
             userList = response.data.data;
          } 
          // 2. 尝试直接返回数组 response.data (Array)
          else if (Array.isArray(response.data)) {
             userList = response.data;
          }
          // 3. 尝试 response.data.data 是对象但包含 list 字段? (常见变体)
          else if (response.data.data && Array.isArray(response.data.data.list)) {
             userList = response.data.data.list;
          }
          
          console.log('Parsed User List:', userList);

          // API请求成功，使用真实数据
          this.users = userList.map(user => ({
            uid: user.uid || user.id, // 兼容 id
            username: user.username,
            password: user.password, // 保存密码用于更新
            email: user.email || '',
            phone: user.phone || '',
            status: user.status !== undefined ? user.status : 1,
            role: user.role || 'user',
            avatar: user.avatar,
            createTime: user.createTime || '-'
          }));
          this.isMockData = false;
        } else {
          // API返回错误
          // 修正：后端返回字段为 msg 而不是 message
          throw new Error(response.data.msg || `API返回错误 (code=${response.data.code})`);
        }
      } catch (error) {
        console.error('加载用户数据失败详情:', error);
        // 显示详细错误信息以便调试
        const errorMsg = error.response ? 
            `Status: ${error.response.status} ${error.response.statusText}` : 
            error.message;
            
        this.error = `API连接失败 (${errorMsg})，已显示模拟数据`;
        
        // API调用失败，使用模拟数据
        this.users = [...MOCK_USERS];
        this.isMockData = true;
      } finally {
        this.loading = false;
      }
    },

    // 检查用户名是否重复
    async checkUsername(username) {
      try {
        const response = await axios.get(apiConfig.user.checkUsername, {
          params: { username }
        });
        return response.data.code === 1; // 返回true表示可用
      } catch {
        return true; // 如果API失败，假设用户名可用
      }
    },

    // 检查手机号是否重复
    async checkPhone(phone) {
      try {
        const response = await axios.get(apiConfig.user.checkPhone, {
          params: { phone }
        });
        return response.data.code === 1; // 返回true表示可用
      } catch {
        return true; // 如果API失败，假设手机号可用
      }
    },

    // 编辑用户
    editUser(user) {
      this.selectedUser = { ...user };
      this.showEditUserModal = true;
    },

    // 提交编辑用户
     async submitEditUser() {
       // 构造更新数据
       const updateData = {
         uid: this.selectedUser.uid,
         username: this.selectedUser.username,
         email: this.selectedUser.email,
         phone: this.selectedUser.phone,
         role: this.selectedUser.role,
         // 补全后端User实体需要的字段，防止更新时被置空
         status: this.selectedUser.status,
         avatar: this.selectedUser.avatar || '',
         password: this.selectedUser.password, // 注意：如果后端updateById会覆盖密码，这里需要传原密码
         createTime: this.selectedUser.createTime,
         updateTime: new Date().toISOString() // 假设需要更新时间
       };

       try {
         const response = await axios.post(apiConfig.user.updateUser, updateData);

         if (response.data.code === 1) {
           alert('用户信息更新成功！');
           this.showEditUserModal = false;
           await this.loadUsers(); // 重新加载列表
         } else {
           alert('更新失败：' + response.data.msg);
         }
       } catch (error) {
         console.error('更新用户信息失败:', error);
         alert('更新失败（API调用异常）');
       }
     },

    // 切换用户状态 (禁用/启用)
    async toggleUserStatus(user) {
      // 判断当前动作
      const action = user.status === 1 ? '禁用' : '启用';
      const confirmMsg = user.status === 1 
          ? `确定要禁用用户"${user.username}"吗？\n(操作将设置状态为禁用)`
          : `确定要启用用户"${user.username}"吗？\n(操作将恢复用户为正常状态)`;

      if (confirm(confirmMsg)) {
        try {
          // 注意：后端接口是 /admin/user/status/{id}
          // 根据之前提供的代码：@PostMapping("/status/{id}") ... userService.updateStatus(id)
          // 需要确认后端逻辑是 "toggle"(切换) 还是 "set to 0"(只禁用)
          // 假设后端是 updateStatus(id) -> update user set status = 0 where uid = id (如果是这样，则只能禁用)
          // 但通常命名为 updateStatus 且只传ID，可能是切换。
          // 如果后端只能注销(设为0)，那么我们需要请求后端修改，或者看是否有其他接口。
          // 根据你刚才提供的 UserMapper: update user set status = 0 where uid = #{id}
          // **发现后端目前只能注销(设为0)**。
          
          // 如果要启用，需要调用 update 接口手动设置 status=1
          if (user.status === 0) {
             // 启用逻辑：调用 updateUser 接口
             const updateData = {
               ...user,
               status: 1, // 强制设为1
               updateTime: new Date().toISOString()
             };
             // 确保传所有必要字段，因为我们复用了 submitEditUser 的逻辑
             // 但这里直接调接口
             const res = await axios.post(apiConfig.user.updateUser, updateData);
             if (res.data.code === 1) {
                user.status = 1;
                alert('已启用用户');
             } else {
                alert('启用失败：' + res.data.msg);
             }
          } else {
             // 禁用逻辑：调用原有的 status 接口 (因为它只能设为0)
             const response = await axios.post(apiConfig.user.deactivateUser(user.uid));
             if (response.data.code === 1) {
               user.status = 0;
               alert(`已禁用用户`);
             } else {
               alert(`操作失败：` + response.data.msg);
             }
          }
        } catch (error) {
          console.error(`${action}用户失败:`, error);
          alert(`操作失败（API调用失败）`);
        }
      }
    },

    // 查看用户详情
    viewUserDetail(user) {
      this.selectedUser = { ...user };
      this.showDetailModal = true;
    },

    // 添加用户
    async addUser() {
      if (!this.newUser.username.trim()) {
        alert('请输入用户名');
        return;
      }

      if (!this.newUser.password.trim()) {
        alert('请输入密码');
        return;
      }

      // 验证用户名是否重复
      const isUsernameAvailable = await this.checkUsername(this.newUser.username);
      if (!isUsernameAvailable) {
        alert('用户名已存在！');
        return;
      }

      // 验证手机号是否重复（如果有手机号）
      if (this.newUser.phone) {
        const isPhoneAvailable = await this.checkPhone(this.newUser.phone);
        if (!isPhoneAvailable) {
          alert('手机号已存在！');
          return;
        }
      }

      // 构建用户数据
      const userData = {
        username: this.newUser.username,
        password: this.newUser.password,
        email: this.newUser.email || '',
        phone: this.newUser.phone || '',
        role: this.newUser.role,
        status: 1
      };

      // 尝试调用API添加用户
      try {
        const response = await axios.post(apiConfig.user.addUser, userData);

        if (response.data.code === 1) {
          // 添加成功后重新加载用户列表
          await this.loadUsers();

          // 清空表单
          this.resetNewUserForm();

          this.showAddUserModal = false;
          alert('用户添加成功！');
        } else {
          alert('添加失败：' + response.data.message);
        }
      } catch (error) {
        console.error('添加用户失败:', error);
        alert('用户添加成功（API调用失败）');
      }
    },

    // 重置新用户表单
    resetNewUserForm() {
      this.newUser = {
        username: '',
        password: '',
        email: '',
        phone: '',
        role: 'user',
        status: 1
      };
    }
  }
}
</script>

<style scoped>
/* 基础容器 */
.management-section {
  background: white;
  border: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

/* 模拟数据标记 */
.simulation-badge {
  background: #bbdefb;
  color: #0d47a1;
  padding: 2px 8px;
  font-size: 12px;
  margin-left: 8px;
  border-radius: 2px;
}

/* 加载状态 */
.loading-state {
  padding: 40px 20px;
  text-align: center;
  color: #666;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1a237e;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  padding: 40px 20px;
  text-align: center;
  color: #c62828;
  background: #ffebee;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.btn-retry {
  padding: 6px 16px;
  background: #1a237e;
  color: white;
  border: none;
  border-radius: 2px;
  cursor: pointer;
  font-size: 13px;
}

.btn-retry:hover {
  background: #283593;
}

/* 头部区域 */
.section-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
  color: #1a237e;
  font-size: 18px;
}

.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 按钮样式 */
.btn-primary {
  background: #1a237e;
  color: white;
  border: none;
  padding: 8px 16px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.btn-primary:hover {
  background: #283593;
}

/* 搜索框 */
.search-box {
  position: relative;
}

.search-input {
  padding: 8px 32px 8px 12px;
  border: 1px solid #d0d0d0;
  width: 250px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #1a237e;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 14px;
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
  min-height: 400px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.data-table th {
  background: #f8f9ff;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #1a237e;
  border-bottom: 1px solid #e0e0e0;
  white-space: nowrap;
}

.data-table td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

.data-table tbody tr:hover {
  background: #f8f9ff;
}

/* 用户单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar-sm {
  width: 32px;
  height: 32px;
  background: #3949ab;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  border-radius: 2px;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 10px;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  font-weight: 500;
}

.status-badge.active {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-badge.inactive {
  background: #ffebee;
  color: #c62828;
}

/* 角色徽章 */
.role-badge {
  padding: 4px 10px;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  font-weight: 500;
}

.role-badge.admin {
  background: #e3f2fd;
  color: #1565c0;
}

.role-badge.user {
  background: #f3e5f5;
  color: #7b1fa2;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-edit, .btn-status, .btn-view {
  padding: 6px 12px;
  border: none;
  font-size: 12px;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-edit {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-edit:hover {
  background: #bbdefb;
}

.btn-status {
  min-width: 60px;
}

.btn-status.btn-disable {
  background: #ffebee;
  color: #d32f2f;
}

.btn-status.btn-disable:hover {
  background: #ffcdd2;
}

.btn-status.btn-enable {
  background: #e8f5e8;
  color: #2e7d32;
}

.btn-status.btn-enable:hover {
  background: #c8e6c9;
}

.btn-view {
  background: #f5f5f5;
  color: #616161;
}

.btn-view:hover {
  background: #e0e0e0;
}

/* 表格底部 */
.table-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fafafa;
}

.pagination-info {
  color: #666;
  font-size: 13px;
}

.pagination-hint {
  color: #999;
  font-size: 11px;
  margin-left: 8px;
  font-style: italic;
}

.mock-hint {
  color: #f57c00;
  margin-left: 8px;
  font-weight: 500;
  font-size: 11px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-btn {
  padding: 6px 12px;
  border: 1px solid #d0d0d0;
  background: white;
  cursor: not-allowed;
  color: #999;
  font-size: 13px;
}

.pagination-current {
  padding: 6px 12px;
  background: #1a237e;
  color: white;
  font-size: 13px;
  font-weight: 500;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 2px;
}

.modal-lg {
  width: 700px;
}

.modal-header {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9ff;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  color: #1a237e;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 2px;
}

.modal-close:hover {
  background: #f0f0f0;
}

.modal-body {
  padding: 20px;
}

/* 表单样式 */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.form-input {
  padding: 8px 12px;
  border: 1px solid #d0d0d0;
  font-size: 14px;
  border-radius: 2px;
}

.form-input:focus {
  outline: none;
  border-color: #1a237e;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid #d0d0d0;
  background: white;
  color: #333;
  cursor: pointer;
  font-size: 13px;
  border-radius: 2px;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.btn-submit {
  padding: 8px 16px;
  background: #1a237e;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 13px;
  border-radius: 2px;
}

.btn-submit:hover {
  background: #283593;
}

/* 用户详情样式 */
.user-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  color: #1a237e;
  font-size: 15px;
  font-weight: 600;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  font-size: 13px;
  color: #666;
  min-width: 80px;
}

.detail-item span {
  font-size: 13px;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .section-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-input {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .btn-edit, .btn-status, .btn-view {
    width: 100%;
    text-align: center;
  }

  .table-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .modal {
    width: 95%;
  }
}

@media (max-width: 480px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .data-table {
    font-size: 13px;
  }

  .data-table th,
  .data-table td {
    padding: 8px;
  }
}
</style>