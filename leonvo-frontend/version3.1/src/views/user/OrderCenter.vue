<template>
  <div class="order-center-view">
    <div class="order-container">
      <div class="back-home">
        <router-link to="/" class="back-link">
          <span class="back-icon">←</span>
          返回首页
        </router-link>
      </div>

      <div class="order-box">
        <div class="order-header">
          <h1>订单中心</h1>
          <p class="subtitle">查看和管理您的所有订单</p>
        </div>

        <!-- 如果未登录，显示登录提示 -->
        <div v-if="!isLoggedIn" class="not-logged-in">
          <div class="login-required">
            <div class="lock-icon">🔒</div>
            <h2>需要登录</h2>
            <p>请先登录才能查看订单</p>
            <div class="login-actions">
              <button @click="goToLogin" class="btn-login">
                去登录
              </button>
              <button @click="goToHome" class="btn-back-home">
                返回首页
              </button>
            </div>
          </div>
        </div>

        <!-- 如果已登录，显示订单内容 -->
        <div v-else>
          <!-- 订单统计 -->
          <div class="order-stats">
            <div class="stat-item">
              <div class="stat-value">{{ orderStats.total }}</div>
              <div class="stat-label">全部订单</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ orderStats.pending }}</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ orderStats.shipped }}</div>
              <div class="stat-label">已发货</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ orderStats.completed }}</div>
              <div class="stat-label">已收货</div>
            </div>
          </div>

          <!-- 筛选和搜索 -->
          <div class="filters">
            <div class="search-box">
              <input
                type="text"
                v-model="searchKeyword"
                placeholder="搜索订单号、收货人、电话..."
                class="search-input"
              />
              <span class="search-icon">🔍</span>
            </div>

            <div class="filter-group">
              <select v-model="statusFilter" class="filter-select">
                <option value="all">全部状态</option>
                <option value="0">待付款</option>
                <option value="1">待发货</option>
                <option value="2">已发货</option>
                <option value="3">已完成</option>
                <option value="4">已取消</option>
              </select>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
          </div>

          <!-- 订单列表 -->
          <div v-else-if="filteredOrders.length > 0" class="orders-list">
            <div v-for="order in filteredOrders" :key="order.oid" class="order-item">
              <div class="order-header-info">
                <div class="order-id">
                  <span class="order-id-label">订单号：</span>
                  <span class="order-id-value">#{{ order.oid }}</span>
                </div>
                <div class="order-time">
                  {{ formatDateTime(order.orderTime) }}
                </div>
                <div :class="['order-status', getStatusClass(order.status)]">
                  {{ getStatusText(order.status) }}
                </div>
              </div>

              <div class="order-content">
                <div class="order-items">
                  <div v-if="order.items && order.items.length > 0">
                    <div v-for="(item, index) in order.items" :key="index" class="order-item-product">
                      <div class="product-info">
                        <span class="product-name">{{ item.name || '商品' }}</span>
                        <span class="product-quantity">×{{ item.quantity || 1 }}</span>
                      </div>
                      <div class="product-price">¥{{ (item.price || 0).toLocaleString() }}</div>
                    </div>
                  </div>
                  <div v-else class="no-items">
                    <span>商品信息未提供</span>
                  </div>
                </div>

                <div class="order-summary">
                  <div class="receiver-info">
                    <div class="receiver-name">
                      <span class="label">收货人：</span>
                      {{ order.receiverName }}
                    </div>
                    <div class="receiver-phone">
                      <span class="label">电话：</span>
                      {{ order.receiverPhone }}
                    </div>
                    <div class="receiver-address">
                      <span class="label">地址：</span>
                      {{ order.province }} {{ order.receiverAddress }}
                    </div>
                    <div v-if="order.remark" class="order-remark">
                      <span class="label">备注：</span>
                      {{ order.remark }}
                    </div>
                  </div>

                  <div class="order-amount">
                    <div class="amount-label">订单金额</div>
                    <div class="amount-value">¥{{ order.amount.toLocaleString() }}</div>
                  </div>
                </div>
              </div>

              <div class="order-actions">
                <button
                  v-if="order.status === 1"
                  @click="confirmReceipt(order.oid)"
                  class="btn-action btn-confirm"
                  :disabled="isProcessing"
                >
                  确认收货
                </button>
                <button
                  v-if="order.status === 0"
                  @click="cancelOrder(order.oid)"
                  class="btn-action btn-cancel"
                  :disabled="isProcessing"
                >
                  取消订单
                </button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-orders">
            <div class="empty-icon">📦</div>
            <h2>暂无订单</h2>
            <p>您还没有任何订单，快去选购心仪的商品吧！</p>
            <router-link to="/" class="btn-go-shopping">
              去逛逛
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'OrderCenter',
  data() {
    return {
      isLoggedIn: false,
      userId: null,
      orders: [],
      loading: false,
      searchKeyword: '',
      statusFilter: 'all',
      isProcessing: false,
      orderStats: {
        total: 0,
        pending: 0,
        shipped: 0,
        completed: 0
      }
    };
  },
  computed: {
    filteredOrders() {
      let filtered = this.orders;

      // 按状态筛选
      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(order => order.status === parseInt(this.statusFilter));
      }

      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(order => {
          return (
            order.oid.toString().includes(keyword) ||
            (order.receiverName && order.receiverName.toLowerCase().includes(keyword)) ||
            (order.receiverPhone && order.receiverPhone.includes(keyword))
          );
        });
      }

      // 按时间倒序排列
      return filtered.sort((a, b) => {
        const timeA = new Date(a.orderTime || 0);
        const timeB = new Date(b.orderTime || 0);
        return timeB - timeA;
      });
    }
  },
  mounted() {
    this.checkLoginStatus();
    if (this.isLoggedIn) {
      this.loadOrders();
    }
  },
  methods: {
    checkLoginStatus() {
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      this.isLoggedIn = loggedIn;
      if (loggedIn) {
        this.userId = localStorage.getItem('userId');
      }
    },
    goToLogin() {
      this.$router.push('/login');
    },
    goToHome() {
      this.$router.push('/');
    },
    async loadOrders() {
      if (!this.userId) {
        this.errorMessage = '未找到用户ID，请重新登录';
        setTimeout(() => {
          this.$router.push('/login');
        }, 2000);
        return;
      }

      this.loading = true;
      try {
        const response = await axios.get(apiConfig.order.getOrdersByUserId(this.userId));
        if (response.data.code === 1) {
          const orders = response.data.data || [];
          
          // 为每个订单获取详细信息（包含商品信息）
          const ordersWithDetails = await Promise.all(
            orders.map(async (order) => {
              try {
                const detailResponse = await axios.get(apiConfig.order.getOrderDetail(order.oid));
                if (detailResponse.data.code === 1 && detailResponse.data.data) {
                  // 合并详情数据
                  return {
                    ...order,
                    items: detailResponse.data.data.items || []
                  };
                }
                return order;
              } catch (error) {
                console.error(`获取订单 ${order.oid} 详情失败:`, error);
                return order;
              }
            })
          );
          
          this.orders = ordersWithDetails;
          this.calculateStats();
        } else {
          console.error('获取订单失败:', response.data.msg);
          this.orders = [];
        }
      } catch (error) {
        console.error('获取订单失败:', error);
        this.orders = [];
      } finally {
        this.loading = false;
      }
    },
    calculateStats() {
      this.orderStats = {
        total: this.orders.length,
        pending: this.orders.filter(o => o.status === 0).length,
        shipped: this.orders.filter(o => o.status === 1).length,
        completed: this.orders.filter(o => o.status === 2).length
      };
    },
    formatDateTime(dateString) {
      if (!dateString) return '';
      try {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        return dateString;
      }
    },
    getStatusClass(status) {
      const statusMap = {
        0: 'status-pending',
        1: 'status-shipped',
        2: 'status-completed',
        3: 'status-cancelled'
      };
      return statusMap[status] || 'status-pending';
    },
    getStatusText(status) {
      const statusMap = {
        0: '待发货',
        1: '已发货',
        2: '已收货',
        3: '已取消'
      };
      return statusMap[status] || '未知状态';
    },
    async confirmReceipt(oid) {
      if (!confirm('确认收货后订单将标记为已收货，是否继续？')) {
        return;
      }

      this.isProcessing = true;
      try {
        // 确保userId是整数类型
        const uid = parseInt(this.userId);
        const response = await axios.put(
          apiConfig.order.confirmReceipt(oid, uid)
        );
        if (response.data.code === 1) {
          alert('确认收货成功');
          await this.loadOrders();
        } else {
          alert(response.data.msg || '确认收货失败');
        }
      } catch (error) {
        console.error('确认收货失败:', error);
        alert('确认收货失败，请稍后重试');
      } finally {
        this.isProcessing = false;
      }
    },
    async cancelOrder(oid) {
      if (!confirm('确定要取消这个订单吗？')) {
        return;
      }

      this.isProcessing = true;
      try {
        const uid = parseInt(this.userId);
        const response = await axios.put(
          apiConfig.order.cancelOrder(oid, uid)
        );
        if (response.data.code === 1) {
          alert('取消订单成功');
          await this.loadOrders();
        } else {
          alert(response.data.msg || '取消订单失败');
        }
      } catch (error) {
        console.error('取消订单失败:', error);
        alert('取消订单失败，请稍后重试');
      } finally {
        this.isProcessing = false;
      }
    },
  }
};
</script>

<style scoped>
.order-center-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.order-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-home {
  margin-bottom: 20px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: white;
  text-decoration: none;
  font-size: 16px;
  transition: opacity 0.3s;
}

.back-link:hover {
  opacity: 0.8;
}

.back-icon {
  font-size: 20px;
}

.order-box {
  background: white;
  border-radius: 0;
  padding: 50px;
  box-shadow: 0 10px 40px rgba(75, 108, 183, 0.2);
  border: 2px solid #4b6cb7;
}

.order-header {
  text-align: center;
  margin-bottom: 40px;
}

.order-header h1 {
  color: #182848;
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: 900;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  position: relative;
}

.order-header h1::after {
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

.not-logged-in {
  text-align: center;
  padding: 60px 20px;
}

.login-required {
  max-width: 400px;
  margin: 0 auto;
}

.lock-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.login-required h2 {
  color: #182848;
  margin-bottom: 10px;
}

.login-required p {
  color: #666;
  margin-bottom: 30px;
}

.login-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-login,
.btn-back-home {
  padding: 12px 30px;
  border: none;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-login {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}

.btn-back-home {
  background: #f5f5f5;
  color: #333;
}

.btn-login:hover,
.btn-back-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.order-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background: linear-gradient(135deg, #f8fbff 0%, #e3f2fd 100%);
  border: 2px solid #bbdefb;
  border-radius: 0;
  padding: 20px;
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: 900;
  color: #4b6cb7;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  align-items: center;
}

.search-box {
  flex: 1;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 12px 40px 12px 15px;
  border: 2px solid #bbdefb;
  border-radius: 0;
  font-size: 16px;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #999;
}

.filter-select {
  padding: 12px 20px;
  border: 2px solid #bbdefb;
  border-radius: 0;
  font-size: 16px;
  background: white;
  cursor: pointer;
}

.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4b6cb7;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  border: 2px solid #bbdefb;
  border-radius: 0;
  padding: 25px;
  background: #f8fbff;
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.order-id {
  font-weight: 600;
  color: #182848;
}

.order-id-label {
  color: #666;
}

.order-id-value {
  color: #4b6cb7;
  font-size: 18px;
}

.order-time {
  color: #666;
  font-size: 14px;
}

.order-status {
  padding: 6px 15px;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
}

.status-pending {
  background: #fff3e0;
  color: #ff9800;
}

.status-shipped {
  background: #e3f2fd;
  color: #2196f3;
}

.status-completed {
  background: #e8f5e9;
  color: #4caf50;
}

.status-cancelled {
  background: #ffebee;
  color: #f44336;
}

.order-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 20px;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-item-product {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: white;
  border: 1px solid #e0e0e0;
}

.product-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-name {
  font-weight: 500;
}

.product-quantity {
  color: #666;
}

.product-price {
  font-weight: 600;
  color: #4b6cb7;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.receiver-info {
  background: white;
  padding: 15px;
  border: 1px solid #e0e0e0;
}

.receiver-info > div {
  margin-bottom: 8px;
  font-size: 14px;
}

.receiver-info > div:last-child {
  margin-bottom: 0;
}

.label {
  color: #666;
  font-weight: 500;
}

.order-amount {
  text-align: right;
  padding: 15px;
  background: linear-gradient(135deg, #f8fbff 0%, #e3f2fd 100%);
  border: 2px solid #4b6cb7;
}

.amount-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.amount-value {
  font-size: 24px;
  font-weight: 900;
  color: #4b6cb7;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.btn-action {
  padding: 10px 20px;
  border: none;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
}

.btn-cancel {
  background: linear-gradient(135deg, #f44336 0%, #c62828 100%);
  color: white;
}

.btn-action:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-action:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-orders {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-orders h2 {
  color: #182848;
  margin-bottom: 10px;
}

.empty-orders p {
  color: #666;
  margin-bottom: 30px;
}

.btn-go-shopping {
  display: inline-block;
  padding: 12px 30px;
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
  text-decoration: none;
  border-radius: 0;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-go-shopping:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

@media (max-width: 768px) {
  .order-content {
    grid-template-columns: 1fr;
  }

  .order-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .order-header-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>


