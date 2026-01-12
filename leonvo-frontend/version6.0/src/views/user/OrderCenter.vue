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
                <option value="0">待发货</option>
                <option value="1">已发货</option>
                <option value="2">已收货</option>
                <option value="3">已取消</option>
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
                    <div v-if="order.logistics || order.logisticsCompany" class="order-logistics">
                      <span class="label">物流：</span>
                      <span>
                        {{ (order.logistics?.logisticsCompany) || order.logisticsCompany || '—' }}
                        <span v-if="order.logistics?.trackingNumber || order.trackingNumber">
                          ｜单号：{{ order.logistics?.trackingNumber || order.trackingNumber }}
                        </span>
                      </span>
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
                <button
                  v-if="order.status === 2"
                  @click="openReviewModal(order)"
                  class="btn-action btn-review"
                  :disabled="isProcessing"
                >
                  {{ hasPendingFeedback(order) ? '评价商品' : '查看评价' }}
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
      <!-- 评价模态框 -->
    <div v-if="showReviewModal" class="modal-overlay" @click.self="closeReviewModal">
      <div class="modal">
        <div class="modal-header">
          <h3>评价商品</h3>
          <button class="modal-close" @click="closeReviewModal">×</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedOrder" class="review-product-list">
             <div v-for="(item, index) in selectedOrder.items" :key="index" class="review-item">
                <div class="review-product-info">
                  <span class="product-name">{{ item.name }}</span>
                </div>
                <div class="rating-input">
                   <label>评分：</label>
                   <div class="stars">
                     <span 
                       v-for="n in 5" 
                       :key="n" 
                       class="star" 
                       :class="{ active: (reviews[item.pid]?.rating || 0) >= n, readonly: reviews[item.pid]?.exists }"
                       @click="setRating(item.pid, n)"
                     >★</span>
                   </div>
                   <span class="rating-text">{{ getRatingText(reviews[item.pid]?.rating) }}</span>
                </div>
                <div class="comment-input">
                  <textarea 
                    v-model="reviews[item.pid].comment" 
                    :readonly="reviews[item.pid]?.exists"
                    placeholder="请输入您的评价（可选）"
                    rows="2"
                  ></textarea>
                </div>
             </div>
          </div>
          <div class="form-actions">
            <button class="btn-cancel" @click="closeReviewModal">取消</button>
            <button class="btn-submit" @click="submitReviews" :disabled="isProcessing">提交评价</button>
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
      showReviewModal: false,
      selectedOrder: null,
      reviews: {}, // { pid: { rating, comment, exists } }
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
      // 1. 确保 orders 已经加载
      if (!this.orders || this.orders.length === 0) return [];

      // 2. 创建副本，避免副作用
      let result = [...this.orders];
      
      // 3. 获取并清理搜索词
      const rawKeyword = this.searchKeyword ? String(this.searchKeyword).toLowerCase().trim() : '';
      
      // 4. 如果有搜索词，执行全量暴力搜索
      if (rawKeyword) {
        // 移除所有特殊字符，只保留字母数字中文，用于模糊匹配
        const cleanKeyword = rawKeyword.replace(/[#\s\-]/g, '');
        
        result = result.filter(order => {
          try {
             // 拿到当前订单的所有数据字符串
             const orderStr = JSON.stringify(order).toLowerCase();
             
             // 检查1: 直接包含原始关键词吗？ (例如 "37")
             if (orderStr.includes(rawKeyword)) return true;
             
             // 检查2: 包含去符号后的关键词吗？ (例如 "#37" -> "37")
             if (cleanKeyword && orderStr.includes(cleanKeyword)) return true;
             
             return false;
          } catch (e) {
            return false;
          }
        });
      } else {
        // 5. 没有搜索词时，应用状态筛选
        if (this.statusFilter !== 'all') {
          result = result.filter(order => order.status === parseInt(this.statusFilter));
        }
      }

      // 6. 排序优化：匹配度高的排前面
      return result.sort((a, b) => {
        // 如果没有搜索词，只按时间倒序
        if (!rawKeyword) {
          const timeA = new Date(a.orderTime || 0).getTime();
          const timeB = new Date(b.orderTime || 0).getTime();
          return timeB - timeA;
        }

        // 重新定义 cleanKeyword 以便在 sort 作用域内使用
        const cleanKey = rawKeyword.replace(/[#\s\-]/g, '');

        // 如果有搜索词，按匹配度排序
        const oidA = String(a.oid || a.id || '');
        const oidB = String(b.oid || b.id || '');

        // 优先级1: 订单号完全匹配 (最高级)
        const exactMatchA = oidA === rawKeyword || oidA === cleanKey;
        const exactMatchB = oidB === rawKeyword || oidB === cleanKey;
        if (exactMatchA && !exactMatchB) return -1;
        if (!exactMatchA && exactMatchB) return 1;

        // 优先级2: 订单号包含关键词 (次级)
        const oidMatchA = oidA.includes(rawKeyword) || (cleanKey && oidA.includes(cleanKey));
        const oidMatchB = oidB.includes(rawKeyword) || (cleanKey && oidB.includes(cleanKey));
        if (oidMatchA && !oidMatchB) return -1;
        if (!oidMatchA && oidMatchB) return 1;

        // 优先级3: 按时间倒序 (默认)
        const timeA = new Date(a.orderTime || 0).getTime();
        const timeB = new Date(b.orderTime || 0).getTime();
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
              // 1. 基础字段规范化 (确保字段存在，无论后端返回驼峰还是下划线)
              let normalizedOrder = {
                ...order,
                receiverName: order.receiverName || order.receiver_name || order.receiver || '',
                receiverPhone: order.receiverPhone || order.receiver_phone || order.phone || '',
                receiverAddress: order.receiverAddress || order.receiver_address || order.address || '',
                orderTime: order.orderTime || order.order_time || order.createTime || order.create_time || '',
                amount: order.amount || order.total || 0
              };

              try {
                const detailResponse = await axios.get(apiConfig.order.getOrderDetail(order.oid));
                if (detailResponse.data.code === 1 && detailResponse.data.data) {
                  // 合并详情数据
                  const detail = detailResponse.data.data;
                  normalizedOrder = {
                    ...normalizedOrder,
                    ...detail, // 详情可能包含更多字段
                    // 再次确保核心字段不被覆盖为 undefined
                    receiverName: detail.receiverName || detail.receiver_name || normalizedOrder.receiverName,
                    receiverPhone: detail.receiverPhone || detail.receiver_phone || normalizedOrder.receiverPhone,
                    items: detail.items || normalizedOrder.items || []
                  };
                }
              } catch (error) {
                console.error(`获取订单 ${order.oid} 详情失败:`, error);
              }
              
              // 确保 items 是数组
              if (!Array.isArray(normalizedOrder.items)) {
                  normalizedOrder.items = [];
              }
              
              return normalizedOrder;
            })
          );
          
          const enriched = await Promise.all(
            ordersWithDetails.map(o => this.enrichFeedbackFlags(o))
          );
          
          console.log('处理后的订单数据:', enriched); // 调试日志
          this.orders = enriched;
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
    async enrichFeedbackFlags(order) {
      // 仅在已收货时标记评价状态
      if (order.status !== 2 || !order.items || !this.userId) return order;
      const uid = parseInt(this.userId);
      const items = await Promise.all(
        order.items.map(async (item) => {
          const pid = item.pid || item.id;
          let hasFeedback = false;
          try {
            const res = await axios.get(apiConfig.feedback.checkFeedback(uid, pid));
            if (res.data && res.data.code === 1) {
              hasFeedback = !!res.data.data;
            }
          } catch (e) {
            console.error('检查评价记录失败', e);
          }
          return { ...item, hasFeedback };
        })
      );
      return { ...order, items };
    },
    hasPendingFeedback(order) {
      if (!order.items) return false;
      return order.items.some(i => !i.hasFeedback);
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
        3: 'status-cancelled',
        4: 'status-cancelled'
      };
      return statusMap[status] || 'status-pending';
    },
    getStatusText(status) {
      const statusMap = {
        0: '待发货',
        1: '已发货',
        2: '已收货',
        3: '已取消',
        4: '已取消'
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
    async openReviewModal(order) {
      this.selectedOrder = order;
      this.reviews = {};
      // 初始化/拉取已有评价
      if (order.items) {
        for (const item of order.items) {
          const pid = item.pid || item.id;
          // 默认值
          this.reviews[pid] = { rating: 5, comment: '', exists: false };
          try {
            const res = await axios.get(apiConfig.feedback.getUserFeedback(parseInt(this.userId), pid));
            if (res.data && res.data.code === 1 && res.data.data) {
              const fb = res.data.data;
              this.reviews[pid] = {
                rating: fb.star || 5,
                comment: fb.comment || '',
                exists: true
              };
              item.hasFeedback = true;
            }
          } catch (e) {
            // 若未评价会返回错误，不影响
          }
        }
      }
      this.showReviewModal = true;
    },
    closeReviewModal() {
      this.showReviewModal = false;
      this.selectedOrder = null;
      this.reviews = {};
    },
    setRating(pid, rating) {
      if (!this.reviews[pid]) {
        this.reviews[pid] = { rating: 5, comment: '' };
      }
      if (this.reviews[pid].exists) return;
      this.reviews[pid].rating = rating;
      // 强制更新视图
      this.reviews = { ...this.reviews };
    },
    getRatingText(rating) {
      const map = { 1: '差评', 2: '中评', 3: '中评', 4: '好评', 5: '好评' };
      return map[rating] || '好评';
    },
    async submitReviews() {
      if (!this.selectedOrder) return;

      this.isProcessing = true;
      try {
        const uid = parseInt(this.userId);
        for (const item of this.selectedOrder.items || []) {
          const pid = item.pid || item.id;
          const review = this.reviews[pid];
          if (!review) continue;

          // 已存在的仅查看，不提交
          if (review.exists) continue;

          const payload = {
            uid,
            pid,
            star: review.rating || 5,
            comment: review.comment || ''
          };
          const res = await axios.post(apiConfig.feedback.submitFeedback, payload);
          if (res.data && res.data.code === 1) {
            item.hasFeedback = true;
            review.exists = true;
          } else {
            throw new Error(res.data?.msg || '提交评价失败');
          }
        }

        alert('评价提交成功！感谢您的反馈。');
        // 更新列表标记
        await this.loadOrders();
        this.closeReviewModal();
        
      } catch (error) {
        console.error('评价失败:', error);
        alert(error.message || '评价提交失败');
      } finally {
        this.isProcessing = false;
      }
    }
  }
};
</script>

<style scoped>
.btn-review {
  background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
  color: white;
}

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
}

.modal {
  background: white;
  width: 90%;
  max-width: 500px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.modal-header {
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
}

.review-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.product-name {
  font-weight: bold;
  margin-bottom: 10px;
  display: block;
}

.rating-input {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  gap: 5px;
  cursor: pointer;
}

.star {
  font-size: 24px;
  color: #ddd;
  transition: color 0.2s;
}

.star.active {
  color: #ff9800;
}

.rating-text {
  margin-left: 10px;
  color: #666;
  font-size: 14px;
}

.comment-input textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel, .btn-submit {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
}

.btn-cancel {
  background: #f0f0f0;
  color: #333;
}

.btn-submit {
  background: #4b6cb7;
  color: white;
}

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


