<template>
  <div class="business-profile">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>商家信息</h2>
      <button class="btn-edit-profile" @click="editProfile">
        <span class="edit-icon">✏️</span>
        编辑信息
      </button>
    </div>

    <!-- 商家信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="profile-avatar">
          <span class="avatar-text">{{ businessInfo.bname ? businessInfo.bname.charAt(0) : '商' }}</span>
          <!-- 在线状态指示器 -->
          <span class="profile-online-indicator" :class="{
            'status-online': isOnline,
            'status-offline': !isOnline
          }"></span>
        </div>
        <div class="profile-info">
          <h3>{{ businessInfo.bname || '未设置商家名称' }}</h3>
          <div class="profile-id">商家ID: {{ businessInfo.bid || 'N/A' }}</div>
          <div class="profile-status">
            <span class="status-badge" :class="{
              'status-online': isOnline,
              'status-offline': !isOnline
            }">
              <span class="status-dot"></span>
              {{ isOnline ? '在线营业' : '离线' }}
            </span>
            <span class="join-date">入驻时间: {{ businessInfo.join_date || '未设置' }}</span>
            <button class="btn-status-toggle" @click="toggleOnlineStatus">
              {{ isOnline ? '设为离线' : '设为在线' }}
            </button>
          </div>
        </div>
      </div>

      <div class="profile-details">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4><span class="section-icon">📝</span> 基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label><span class="item-icon">🏢</span> 商家名称：</label>
              <span class="item-value">{{ businessInfo.bname || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <label><span class="item-icon">🆔</span> 商家ID：</label>
              <span class="item-value">{{ businessInfo.bid || 'N/A' }}</span>
            </div>
            <div class="detail-item">
              <label><span class="item-icon">📅</span> 入驻时间：</label>
              <span class="item-value">{{ businessInfo.join_date || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <label><span class="item-icon">🔔</span> 店铺状态：</label>
              <span class="status-badge-small" :class="{
                'status-online': isOnline,
                'status-offline': !isOnline
              }">
                <span class="status-dot-small"></span>
                {{ isOnline ? '在线营业' : '已离线' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 联系信息 -->
        <div class="detail-section">
          <h4><span class="section-icon">📞</span> 联系信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label><span class="item-icon">👤</span> 联系人：</label>
              <span class="item-value">{{ businessInfo.contact || '未设置' }}</span>
            </div>
            <div class="detail-item">
              <label><span class="item-icon">📱</span> 联系电话：</label>
              <span class="item-value">
                <a v-if="businessInfo.phone" :href="'tel:' + businessInfo.phone" class="contact-link">
                  {{ businessInfo.phone }}
                </a>
                <span v-else>未设置</span>
              </span>
            </div>
            <div class="detail-item">
              <label><span class="item-icon">📧</span> 联系邮箱：</label>
              <span class="item-value">
                <a v-if="businessInfo.email" :href="'mailto:' + businessInfo.email" class="contact-link">
                  {{ businessInfo.email }}
                </a>
                <span v-else>未设置</span>
              </span>
            </div>
            <div class="detail-item full-width">
              <label><span class="item-icon">📍</span> 店铺地址：</label>
              <span class="item-value">{{ businessInfo.address || '未设置地址' }}</span>
            </div>
          </div>
        </div>

        <!-- 店铺信息 -->
        <div class="detail-section">
          <h4><span class="section-icon">📊</span> 店铺统计</h4>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon">📦</div>
              <div class="stat-content">
                <div class="stat-value">{{ businessStats.total_products || 0 }}</div>
                <div class="stat-label">商品总数</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">🛒</div>
              <div class="stat-content">
                <div class="stat-value">{{ businessStats.active_products || 0 }}</div>
                <div class="stat-label">在售商品</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">📋</div>
              <div class="stat-content">
                <div class="stat-value">{{ businessStats.total_orders || 0 }}</div>
                <div class="stat-label">累计订单</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">⭐</div>
              <div class="stat-content">
                <div class="stat-value">{{ businessStats.rating || 0.0 }}</div>
                <div class="stat-label">店铺评分</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">💰</div>
              <div class="stat-content">
                <div class="stat-value">¥{{ formatNumber(businessStats.total_sales || 0) }}</div>
                <div class="stat-label">累计销售额</div>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">📈</div>
              <div class="stat-content">
                <div class="stat-value">¥{{ formatNumber(businessStats.monthly_sales || 0) }}</div>
                <div class="stat-label">本月销售额</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 店铺描述 -->
        <div class="detail-section" v-if="businessInfo.description">
          <h4><span class="section-icon">📋</span> 店铺描述</h4>
          <div class="description-content">
            {{ businessInfo.description }}
          </div>
        </div>
      </div>
    </div>

    <!-- 账号安全 -->
    <div class="security-card">
      <h4><span class="section-icon">🔒</span> 账号安全</h4>
      <div class="security-info">
        <div class="security-item">
          <div class="security-icon">🔑</div>
          <div class="security-content">
            <div class="security-title">登录密码</div>
            <div class="security-desc">定期更换密码有助于保护账号安全</div>
          </div>
          <button class="btn-change-password" @click="showPasswordModal = true">
            修改密码
          </button>
        </div>

        <div class="security-item">
          <div class="security-icon">📱</div>
          <div class="security-content">
            <div class="security-title">在线状态</div>
            <div class="security-desc">当前状态:
              <span :class="{
                'text-online': isOnline,
                'text-offline': !isOnline
              }">
                {{ isOnline ? '在线' : '离线' }}
              </span>
            </div>
          </div>
          <button class="btn-status-toggle" @click="toggleOnlineStatus">
            {{ isOnline ? '设为离线' : '设为在线' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 编辑信息模态框 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal">
        <div class="modal-header">
          <h3><span class="modal-icon">✏️</span> 编辑商家信息</h3>
          <button class="modal-close" @click="closeEditModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="updateProfile">
            <div class="form-group">
              <label><span class="form-icon">🏢</span> 商家名称 *</label>
              <input
                  type="text"
                  v-model="editForm.bname"
                  placeholder="请输入商家名称"
                  required
                  class="form-input"
              >
            </div>

            <div class="form-row">
              <div class="form-group">
                <label><span class="form-icon">👤</span> 联系人</label>
                <input
                    type="text"
                    v-model="editForm.contact"
                    placeholder="请输入联系人姓名"
                    class="form-input"
                >
              </div>

              <div class="form-group">
                <label><span class="form-icon">📱</span> 联系电话</label>
                <input
                    type="tel"
                    v-model="editForm.phone"
                    placeholder="请输入联系电话"
                    class="form-input"
                >
              </div>
            </div>

            <div class="form-group">
              <label><span class="form-icon">📧</span> 联系邮箱</label>
              <input
                  type="email"
                  v-model="editForm.email"
                  placeholder="请输入联系邮箱"
                  class="form-input"
              >
            </div>

            <div class="form-group">
              <label><span class="form-icon">📍</span> 店铺地址</label>
              <textarea
                  v-model="editForm.address"
                  placeholder="请输入详细的店铺地址"
                  rows="3"
                  class="form-textarea"
              ></textarea>
            </div>

            <div class="form-group">
              <label><span class="form-icon">📋</span> 店铺描述</label>
              <textarea
                  v-model="editForm.description"
                  placeholder="请输入店铺描述，如主营业务、特色等"
                  rows="4"
                  class="form-textarea"
              ></textarea>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="closeEditModal">
                取消
              </button>
              <button type="submit" class="btn-submit">
                <span v-if="loading" class="loading-spinner"></span>
                {{ loading ? '保存中...' : '保存修改' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 修改密码模态框 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="closePasswordModal">
      <div class="modal">
        <div class="modal-header">
          <h3><span class="modal-icon">🔑</span> 修改密码</h3>
          <button class="modal-close" @click="closePasswordModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="changePassword">
            <div class="form-group">
              <label>当前密码</label>
              <input
                  type="password"
                  v-model="passwordData.current_password"
                  placeholder="请输入当前密码"
                  required
                  class="form-input"
              >
            </div>
            <div class="form-group">
              <label>新密码</label>
              <input
                  type="password"
                  v-model="passwordData.new_password"
                  placeholder="请输入新密码（至少6位）"
                  required
                  minlength="6"
                  class="form-input"
              >
              <div class="password-hint">密码长度至少6位，建议包含字母和数字</div>
            </div>
            <div class="form-group">
              <label>确认新密码</label>
              <input
                  type="password"
                  v-model="passwordData.confirm_password"
                  placeholder="请再次输入新密码"
                  required
                  minlength="6"
                  class="form-input"
              >
            </div>
            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="closePasswordModal">
                取消
              </button>
              <button type="submit" class="btn-submit">
                <span v-if="loading" class="loading-spinner"></span>
                {{ loading ? '修改中...' : '确认修改' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'BusinessProfile',
  data() {
    return {
      businessInfo: {},
      businessStats: {},
      isOnline: true,
      showEditModal: false,
      showPasswordModal: false,
      editForm: {},
      passwordData: {
        current_password: '',
        new_password: '',
        confirm_password: ''
      },
      loading: false,
      statusLoading: false
    }
  },
  mounted() {
    this.loadBusinessInfo();
    this.loadBusinessStats();
  },
  methods: {
    formatNumber(num) {
      if (!num) return '0';
      if (num >= 100000000) {
        return (num / 100000000).toFixed(1) + '亿';
      }
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
      }
      return num.toLocaleString();
    },

    // 获取商家ID的统一方法
    getBusinessId() {
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      if (!businessInfo) return null;
      return businessInfo.bid || businessInfo.id || businessInfo.uid;
    },

    // 修改loadBusinessInfo方法，使用apiConfig
    async loadBusinessInfo() {
      const bid = this.getBusinessId();

      if (!bid) {
        console.warn('未找到商家信息');
        this.$router.push('/business/login');
        return;
      }

      try {
        console.log('正在获取商家信息，bid:', bid);
        console.log('API路径:', apiConfig.business.getProfile(bid));

        const response = await axios.get(apiConfig.business.getProfile(bid));

        console.log('API响应:', response);
        console.log('响应数据:', response.data);

        if (response.data.code === 1) {
          this.businessInfo = response.data.data || {};
          console.log('商家信息加载成功:', this.businessInfo);

          // 保存到本地存储
          localStorage.setItem('businessInfo', JSON.stringify(this.businessInfo));
        } else {
          console.warn('获取商家信息失败:', response.data.message || '未知错误');
          console.warn('响应状态码:', response.data.code);

          // 使用本地存储的信息作为回退
          const localInfo = JSON.parse(localStorage.getItem('businessInfo')) || {};
          this.businessInfo = localInfo;
        }
      } catch (error) {
        console.error('加载商家信息失败:', error);
        console.error('错误详情:', error.response || error.message);

        // 使用本地存储的信息作为回退
        const localInfo = JSON.parse(localStorage.getItem('businessInfo')) || {};
        this.businessInfo = localInfo;
      }
    },

    // 修改loadBusinessStats方法，使用apiConfig
    async loadBusinessStats() {
      const bid = this.getBusinessId();

      if (!bid) {
        console.warn('未获取到商家ID');
        return;
      }

      try {
        // 获取商品数据 - 使用配置的API路径
        const productsResponse = await axios.get(apiConfig.business.getProducts(bid));
        let products = [];
        if (productsResponse.data.code === 1) {
          products = productsResponse.data.data || [];
        }

        // 获取订单数据 - 使用配置的API路径
        const ordersResponse = await axios.get(apiConfig.business.getOrders(bid));
        let orders = [];
        if (ordersResponse.data.code === 1) {
          orders = ordersResponse.data.data || [];
        }

        // 计算统计信息
        const totalProducts = products.length;
        const activeProducts = products.filter(p => p.status === 1).length;
        const totalOrders = orders.length;
        const totalSales = orders.reduce((sum, order) => sum + (order.amount || 0), 0);

        // 计算本月销售额
        const now = new Date();
        const firstDayOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
        const monthlySales = orders
            .filter(order => new Date(order.orderTime) >= firstDayOfMonth)
            .reduce((sum, order) => sum + (order.amount || 0), 0);

        this.businessStats = {
          total_products: totalProducts,
          active_products: activeProducts,
          total_orders: totalOrders,
          rating: this.businessInfo.rating || 0.0,
          total_sales: totalSales,
          monthly_sales: monthlySales
        };
      } catch (error) {
        console.error('加载商家统计失败:', error);
        this.businessStats = {
          total_products: 0,
          active_products: 0,
          total_orders: 0,
          rating: 0.0,
          total_sales: 0,
          monthly_sales: 0
        };
      }
    },

    async toggleOnlineStatus() {
      if (this.statusLoading) return;

      this.statusLoading = true;
      const bid = this.getBusinessId();

      if (!bid) {
        alert('商家信息不完整');
        this.statusLoading = false;
        return;
      }

      try {
        const newStatus = !this.isOnline;

        // 注意：需要后端实现在线状态更新接口
        // 暂时直接更新前端状态
        this.isOnline = newStatus;
        alert(`已设为${newStatus ? '在线' : '离线'}状态`);
      } catch (error) {
        console.error('切换在线状态失败:', error);
        alert('状态切换失败，请重试');
      } finally {
        this.statusLoading = false;
      }
    },

    editProfile() {
      this.editForm = { ...this.businessInfo };
      this.showEditModal = true;
    },

    closeEditModal() {
      this.showEditModal = false;
      this.editForm = {};
      this.loading = false;
    },

    closePasswordModal() {
      this.showPasswordModal = false;
      this.passwordData = {
        current_password: '',
        new_password: '',
        confirm_password: ''
      };
      this.loading = false;
    },

    async updateProfile() {
      if (!this.validateEditForm()) {
        return;
      }

      this.loading = true;
      const bid = this.getBusinessId();

      if (!bid) {
        alert('商家信息不完整');
        this.loading = false;
        return;
      }

      try {
        // 使用配置的API路径更新商家信息
        const response = await axios.put(
            apiConfig.business.updateProfile(bid),
            this.editForm
        );

        if (response.data.code === 1) {
          // 更新本地存储
          const existingInfo = JSON.parse(localStorage.getItem('businessInfo')) || {};
          const updatedInfo = {
            ...existingInfo,
            ...this.editForm,
            bid: bid
          };
          localStorage.setItem('businessInfo', JSON.stringify(updatedInfo));

          // 更新显示的商家信息
          this.businessInfo = { ...updatedInfo };

          this.closeEditModal();
          alert('商家信息更新成功！');
        } else {
          alert(`更新失败：${response.data.message}`);
        }
      } catch (error) {
        console.error('更新商家信息失败:', error);
        alert('更新失败，请检查网络连接');
      } finally {
        this.loading = false;
      }
    },

    validateEditForm() {
      if (!this.editForm.bname || !this.editForm.bname.trim()) {
        alert('请输入商家名称');
        return false;
      }

      if (this.editForm.phone && !/^1[3-9]\d{9}$/.test(this.editForm.phone)) {
        alert('请输入有效的手机号码');
        return false;
      }

      if (this.editForm.email && !/\S+@\S+\.\S+/.test(this.editForm.email)) {
        alert('请输入有效的邮箱地址');
        return false;
      }

      return true;
    },

    async changePassword() {
      if (!this.validatePassword()) {
        return;
      }

      this.loading = true;
      const bid = this.getBusinessId();

      if (!bid) {
        alert('商家信息不完整');
        this.loading = false;
        return;
      }

      try {
        // 使用配置的API路径修改密码
        const response = await axios.put(
            apiConfig.business.changePassword(bid),
            {
              current_password: this.passwordData.current_password,
              new_password: this.passwordData.new_password
            }
        );

        if (response.data.code === 1) {
          this.closePasswordModal();
          alert('密码修改成功！');
        } else {
          alert(response.data.message || '密码修改失败');
        }
      } catch (error) {
        console.error('修改密码失败:', error);
        if (error.response && error.response.data && error.response.data.message) {
          alert(error.response.data.message);
        } else {
          alert('密码修改失败，请检查网络连接');
        }
      } finally {
        this.loading = false;
      }
    },

    validatePassword() {
      if (!this.passwordData.current_password) {
        alert('请输入当前密码');
        return false;
      }

      if (!this.passwordData.new_password) {
        alert('请输入新密码');
        return false;
      }

      if (this.passwordData.new_password.length < 6) {
        alert('新密码长度不能少于6位');
        return false;
      }

      if (this.passwordData.new_password !== this.passwordData.confirm_password) {
        alert('两次输入的新密码不一致');
        return false;
      }

      return true;
    }
  }
}
</script>

<style scoped>
.business-profile {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #2d3748;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-edit-profile {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 6px rgba(102, 126, 234, 0.2);
}

.btn-edit-profile:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(102, 126, 234, 0.3);
}

.edit-icon {
  font-size: 16px;
}

/* 商家信息卡片 */
.profile-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.profile-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

/* 头部区域 */
.profile-header {
  display: flex;
  gap: 28px;
  align-items: center;
  padding-bottom: 28px;
  margin-bottom: 28px;
  border-bottom: 1px solid #e2e8f0;
}

/* 头像区域 */
.profile-avatar {
  position: relative;
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}

.avatar-text {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: bold;
  border: 4px solid white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

/* 在线状态指示器 */
.profile-online-indicator {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 3px solid white;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.profile-online-indicator.status-online {
  background: linear-gradient(135deg, #48bb78, #38a169);
  animation: pulse 2s infinite;
}

.profile-online-indicator.status-offline {
  background: linear-gradient(135deg, #a0aec0, #718096);
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(72, 187, 120, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(72, 187, 120, 0); }
  100% { box-shadow: 0 0 0 0 rgba(72, 187, 120, 0); }
}

/* 商家信息 */
.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-info h3 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #2d3748;
  font-weight: 700;
  line-height: 1.2;
}

.profile-id {
  font-size: 14px;
  color: #718096;
  margin-bottom: 12px;
  padding: 4px 12px;
  background: #f8fafc;
  border-radius: 6px;
  display: inline-block;
}

.profile-status {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

/* 状态徽章 */
.status-badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.status-badge.status-online {
  background: linear-gradient(135deg, rgba(72, 187, 120, 0.1), rgba(56, 161, 105, 0.1));
  color: #22543d;
  border: 1px solid rgba(72, 187, 120, 0.3);
}

.status-badge.status-offline {
  background: linear-gradient(135deg, rgba(160, 174, 192, 0.1), rgba(113, 128, 150, 0.1));
  color: #4a5568;
  border: 1px solid rgba(160, 174, 192, 0.3);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-badge.status-online .status-dot {
  background: #48bb78;
  animation: blink 2s infinite;
}

.status-badge.status-offline .status-dot {
  background: #a0aec0;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.join-date {
  color: #718096;
  font-size: 14px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 6px;
}

.btn-status-toggle {
  padding: 8px 16px;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-status-toggle:hover {
  background: #cbd5e0;
}

/* 详情区域 */
.profile-details {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.detail-section {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.detail-section h4 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-icon {
  font-size: 20px;
}

/* 详情网格 */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item label {
  font-size: 14px;
  color: #718096;
  min-width: 100px;
  line-height: 1.6;
  display: flex;
  align-items: center;
  gap: 6px;
}

.item-icon {
  font-size: 16px;
}

.item-value {
  font-size: 15px;
  color: #2d3748;
  line-height: 1.6;
  flex: 1;
}

.contact-link {
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s;
}

.contact-link:hover {
  color: #5a67d8;
  text-decoration: underline;
}

/* 小型状态徽章 */
.status-badge-small {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 13px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-badge-small.status-online {
  background: rgba(72, 187, 120, 0.1);
  color: #22543d;
  border: 1px solid rgba(72, 187, 120, 0.2);
}

.status-badge-small.status-offline {
  background: rgba(160, 174, 192, 0.1);
  color: #4a5568;
  border: 1px solid rgba(160, 174, 192, 0.2);
}

.status-dot-small {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-badge-small.status-online .status-dot-small {
  background: #48bb78;
}

.status-badge-small.status-offline .status-dot-small {
  background: #a0aec0;
}

/* 统计网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  color: #667eea;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
}

/* 描述内容 */
.description-content {
  padding: 20px;
  background: white;
  border-radius: 8px;
  color: #718096;
  line-height: 1.8;
  white-space: pre-line;
  border: 1px solid #e2e8f0;
}

/* 账号安全卡片 */
.security-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

.security-card h4 {
  margin: 0 0 24px 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.security-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.security-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  color: #667eea;
}

.security-content {
  flex: 1;
}

.security-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
}

.security-desc {
  font-size: 14px;
  color: #718096;
}

.text-online {
  color: #48bb78;
  font-weight: 500;
}

.text-offline {
  color: #a0aec0;
  font-weight: 500;
}

.btn-change-password {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  min-width: 100px;
}

.btn-change-password:hover {
  background: #5a67d8;
  transform: translateY(-2px);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  backdrop-filter: blur(5px);
}

.modal {
  background: white;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalFadeIn 0.3s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  color: white;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-icon {
  font-size: 20px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 28px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.3s;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.modal-body {
  padding: 24px;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #4a5568;
  font-weight: 500;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-icon {
  font-size: 16px;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background: #f8fafc;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s;
  background: #f8fafc;
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}

.password-hint {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  padding: 12px 24px;
  border: 2px solid #e2e8f0;
  background: white;
  color: #4a5568;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.btn-submit {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
  }

  .avatar-text {
    font-size: 32px;
  }

  .profile-info h3 {
    font-size: 24px;
  }

  .profile-status {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .security-item {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-cancel, .btn-submit {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .profile-card, .security-card {
    padding: 20px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .modal-header h3 {
    font-size: 18px;
  }

  .modal-body {
    padding: 20px;
  }
}
</style>