<template>
  <div class="business-orders">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="order-stats">
        <div class="stat-item">
          <span class="stat-label">待发货</span>
          <span class="stat-value">{{ orderStats.pending }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">已发货</span>
          <span class="stat-value">{{ orderStats.shipped }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">已完成</span>
          <span class="stat-value">{{ orderStats.completed }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">总订单</span>
          <span class="stat-value">{{ orderStats.total }}</span>
        </div>
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

        <select v-model="dateRange" class="filter-select">
          <option value="all">全部时间</option>
          <option value="today">今天</option>
          <option value="week">本周</option>
          <option value="month">本月</option>
        </select>

        <button class="btn-export" @click="exportOrders">
          <span class="export-icon">📥</span>
          导出订单
        </button>
      </div>
    </div>

    <!-- 订单表格 -->
    <div class="orders-table">
      <table>
        <thead>
        <tr>
          <th>订单号</th>
          <th>商品信息</th>
          <th>收货人</th>
          <th>金额</th>
          <th>状态</th>
          <th>下单时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="order in paginatedOrders" :key="order.oid">
          <td>
            <div class="order-id">#{{ order.oid }}</div>
          </td>
          <td>
            <div class="order-products">
              <div v-if="order.items && order.items.length > 0" v-for="item in order.items" :key="item.id || Math.random()" class="order-item">
                <div class="item-main-info">
                  <span class="item-name">{{ item.name || '默认商品' }}</span>
                  <span class="item-model" v-if="item.model">({{ item.model }})</span>
                </div>
                <span class="item-quantity">×{{ item.quantity || 1 }}</span>
              </div>
              <div v-else class="no-items">
                <span class="item-name">商品信息未提供</span>
                <span class="item-quantity">×1</span>
              </div>
            </div>
          </td>
          <td>
            <div class="receiver-info">
              <div class="receiver-name">{{ order.receiver_name }}</div>
              <div class="receiver-phone">{{ order.receiver_phone }}</div>
            </div>
          </td>
          <td>
            <div class="order-amount">¥{{ order.amount }}</div>
          </td>
          <td>
              <span :class="['status-badge', getStatusClass(order.status)]">
                {{ getStatusText(order.status) }}
              </span>
          </td>
          <td>
            <div class="order-time">
              {{ formatDateTime(order.order_time) }}
            </div>
          </td>
          <td>
            <div class="order-actions">
              <button
                  v-if="order.status === 1"
                  class="btn-ship"
                  @click="shipOrder(order)"
              >
                发货
              </button>
              <button
                  v-if="order.status === 2"
                  class="btn-track"
                  @click="viewTracking(order)"
              >
                物流
              </button>
              <button
                  v-if="order.status < 3"
                  class="btn-cancel"
                  @click="cancelOrder(order)"
              >
                取消
              </button>
              <button
                  class="btn-detail"
                  @click="viewOrderDetail(order)"
              >
                详情
              </button>
            </div>
          </td>
        </tr>
        <tr v-if="paginatedOrders.length === 0">
          <td colspan="7" class="no-orders">
            暂无订单数据
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button
          class="pagination-btn"
          :disabled="currentPage === 1"
          @click="prevPage"
      >
        上一页
      </button>
      <span class="pagination-info">
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
      </span>
      <button
          class="pagination-btn"
          :disabled="currentPage === totalPages"
          @click="nextPage"
      >
        下一页
      </button>
    </div>

    <!-- 订单详情模态框 -->
    <div v-if="selectedOrder" class="modal-overlay" @click.self="selectedOrder = null">
      <div class="modal modal-lg">
        <div class="modal-header">
          <h3>订单详情 #{{ selectedOrder.oid }}</h3>
          <button class="modal-close" @click="selectedOrder = null">×</button>
        </div>
        <div class="modal-body">
          <div class="order-detail">
            <!-- 订单基本信息 -->
            <div class="detail-section">
              <h4>订单信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>订单号：</label>
                  <span>#{{ selectedOrder.oid }}</span>
                </div>
                <div class="detail-item">
                  <label>订单状态：</label>
                  <span :class="['status-badge', getStatusClass(selectedOrder.status)]">
                    {{ getStatusText(selectedOrder.status) }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>下单时间：</label>
                  <span>{{ formatDateTime(selectedOrder.order_time || new Date().toISOString()) }}</span>
                </div>
                <div class="detail-item">
                  <label>订单金额：</label>
                  <span class="amount">¥{{ selectedOrder.amount }}</span>
                </div>
              </div>
            </div>

            <!-- 收货信息 -->
            <div class="detail-section">
              <h4>收货信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>收货人：</label>
                  <span>{{ selectedOrder.receiver_name }}</span>
                </div>
                <div class="detail-item">
                  <label>联系电话：</label>
                  <span>{{ selectedOrder.receiver_phone }}</span>
                </div>
                <div class="detail-item full-width">
                  <label>收货地址：</label>
                  <span>{{ selectedOrder.receiver_address }}</span>
                </div>
                <div class="detail-item">
                  <label>省份：</label>
                  <span>{{ selectedOrder.province || '未设置' }}</span>
                </div>
              </div>
            </div>

            <!-- 商品信息 -->
            <div class="detail-section">
              <h4>商品清单</h4>
              <div class="products-list">
                <div v-for="item in selectedOrder.items" :key="item.id" class="product-item">
                  <div class="product-image">
                    <span class="image-placeholder">💻</span>
                  </div>
                  <div class="product-info">
                    <div class="product-name">{{ item.name }}</div>
                    <div class="product-price">单价：¥{{ item.price }}</div>
                  </div>
                  <div class="product-quantity">
                    ×{{ item.quantity }}
                  </div>
                  <div class="product-total">
                    ¥{{ item.subtotal || (item.price * item.quantity) }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单备注 -->
            <div class="detail-section" v-if="selectedOrder.remark">
              <h4>订单备注</h4>
              <div class="remark-content">
                {{ selectedOrder.remark }}
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="detail-actions" v-if="selectedOrder.status === 1">
              <button class="btn-ship-large" @click="shipOrder(selectedOrder)">
                <span class="icon">🚚</span>
                确认发货
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发货模态框 -->
    <div v-if="showShipModal" class="modal-overlay" @click.self="showShipModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>确认发货</h3>
          <button class="modal-close" @click="showShipModal = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="confirmShipment">
            <div class="form-group">
              <label>物流公司 *</label>
              <select v-model="shipment.courier" required>
                <option value="">请选择物流公司</option>
                <option value="顺丰速运">顺丰速运</option>
                <option value="圆通速递">圆通速递</option>
                <option value="中通快递">中通快递</option>
                <option value="申通快递">申通快递</option>
                <option value="韵达快递">韵达快递</option>
                <option value="京东物流">京东物流</option>
                <option value="邮政EMS">邮政EMS</option>
              </select>
            </div>

            <div class="form-group">
              <label>运单号 *</label>
              <input
                  type="text"
                  v-model="shipment.tracking_number"
                  placeholder="请输入运单号"
                  required
              >
            </div>

            <div class="form-group">
              <label>发货备注</label>
              <textarea
                  v-model="shipment.note"
                  placeholder="可输入发货备注信息"
                  rows="3"
              ></textarea>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="showShipModal = false">
                取消
              </button>
              <button type="submit" class="btn-submit">
                确认发货
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
  name: 'BusinessOrders',
  data() {
    return {
      loading: false,
      searchKeyword: '',
      statusFilter: 'all',
      dateRange: 'all',
      currentPage: 1,
      pageSize: 10,

      orders: [],
      orderStats: {
        pending: 0,
        shipped: 0,
        completed: 0,
        total: 0
      },

      selectedOrder: null,
      showShipModal: false,
      shipmentOrder: null,

      shipment: {
        courier: '',
        tracking_number: '',
        note: ''
      }
    }
  },
  computed: {
    filteredOrders() {
      let filtered = [...this.orders];

      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        filtered = filtered.filter(order =>
            (order.oid && order.oid.toString().includes(keyword)) ||
            (order.receiver_name && order.receiver_name.toLowerCase().includes(keyword)) ||
            (order.receiver_phone && order.receiver_phone.includes(keyword)) ||
            (order.items && order.items.some(item =>
                item.name && item.name.toLowerCase().includes(keyword)))
        );
      }

      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(order => order.status.toString() === this.statusFilter);
      }

      if (this.dateRange !== 'all') {
        const now = new Date();
        let startDate;

        switch (this.dateRange) {
          case 'today':
            startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate());
            break;
          case 'week':
            startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - 7);
            break;
          case 'month':
            startDate = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
            break;
        }

        filtered = filtered.filter(order => {
          const orderDate = new Date(order.order_time);
          return orderDate >= startDate;
        });
      }

      filtered.sort((a, b) => new Date(b.order_time) - new Date(a.order_time));

      return filtered;
    },

    paginatedOrders() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredOrders.slice(start, end);
    },

    totalPages() {
      return Math.ceil(this.filteredOrders.length / this.pageSize);
    }
  },
  mounted() {
    this.loadOrders();
  },
  methods: {
    formatDate(dateString) {
      if (!dateString) return '';
      try {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN');
      } catch (e) {
        return dateString;
      }
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
        0: 'pending',
        1: 'pending',
        2: 'shipped',
        3: 'completed',
        4: 'cancelled'
      };
      return statusMap[status] || 'pending';
    },

    getStatusText(status) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消'
      };
      return statusMap[status] || '未知状态';
    },

    // 获取商家ID的统一方法
    getBusinessId() {
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      if (!businessInfo) return null;
      return businessInfo.bid || businessInfo.id;
    },

    async loadOrders() {
      this.loading = true;
      const bid = this.getBusinessId();

      if (!bid) {
        console.error('未找到商家信息');
        this.orders = [];
        return;
      }

      try {
        // 使用api.config.js中的配置获取订单
        const response = await axios.get(apiConfig.business.getOrders(bid));
        console.log('API返回的订单数据:', response.data.data);
        if (response.data.code === 1) {
          const orders = response.data.data || [];
          
          // 直接使用API返回的订单数据，不再添加模拟商品信息
          const normalizedOrders = this.normalizeOrdersData(orders);
          console.log('规范化后的订单数据:', normalizedOrders);
          this.orders = normalizedOrders;
          this.calculateStats();
        } else {
          console.warn('获取订单失败:', response.data.message);
          this.orders = [];
        }
      } catch (error) {
        console.error('加载订单数据失败:', error);
        // 备用方案：尝试获取所有订单然后筛选
        try {
          const allOrdersResponse = await axios.get('/api/orders');
          console.log('备用方案返回的订单数据:', allOrdersResponse.data.data);
          if (allOrdersResponse.data.code === 1) {
            const allOrders = allOrdersResponse.data.data || [];
            // 直接使用API返回的订单数据，不再添加模拟商品信息
            const normalizedOrders = this.normalizeOrdersData(allOrders);
            console.log('备用方案规范化后的订单数据:', normalizedOrders);
            this.orders = normalizedOrders;
            this.calculateStats();
          }
        } catch (secondError) {
          console.error('备用方案也失败:', secondError);
          this.orders = [];
        }
      } finally {
        this.loading = false;
      }
    },

    // 规范化订单数据，处理不同字段名
    normalizeOrdersData(orders) {
      console.log('所有订单原始数据:', orders);
      return orders.map(order => {
        console.log('单个订单原始数据:', order);
        // 针对订单#4添加特别调试
        if (order.id === 4 || order.oid === 4 || order.oid === '#4') {
          console.log('=== 订单#4详细数据结构 ===');
          console.log('订单完整对象:', JSON.stringify(order, null, 2));
          console.log('订单所有键:', Object.keys(order));
          // 检查各种可能的商品数据位置
          console.log('order.items:', order.items);
          console.log('order.products:', order.products);
          console.log('order.product:', order.product);
          console.log('order.goods:', order.goods);
          console.log('order.productId:', order.productId);
          console.log('order.goodsId:', order.goodsId);
        }
        // 规范化收货人信息
        const receiverName = order.receiver_name || order.receiverName || order.receiver || '';
        const receiverPhone = order.receiver_phone || order.receiverPhone || order.phone || '';
        const receiverAddress = order.receiver_address || order.receiverAddress || order.address || '';
        
        // 规范化订单号
        const orderId = order.oid || order.id || '';
        
        // 规范化订单金额
        const amount = order.amount || order.total || 0;
        
        // 规范化下单时间 - 更健壮的处理
        let orderTime = order.order_time || order.createdAt || order.createTime || order.orderTime || order.time || order.timestamp || '';
        
        // 如果时间是数字（时间戳），转换为字符串
        if (typeof orderTime === 'number') {
          orderTime = new Date(orderTime).toISOString();
        }
        
        // 确保时间字符串有值
        if (!orderTime) {
          // 如果没有时间，使用当前时间（作为默认值）
          orderTime = new Date().toISOString();
        }
        
        // 规范化订单状态
        const status = order.status !== undefined ? order.status : 0;
        
        // 规范化商品项 - 支持多种数据来源
        let items = [];
        // 检查各种可能的商品数据位置
        if (Array.isArray(order.items)) {
          items = order.items;
        } else if (Array.isArray(order.products)) {
          items = order.products;
        } else if (Array.isArray(order.orderItems)) {
          // 支持orderItems字段（订单项）
          items = order.orderItems;
        } else if (Array.isArray(order.order_items)) {
          // 支持order_items字段
          items = order.order_items;
        } else if (order.product && typeof order.product === 'object') {
          // 处理单个商品对象的情况
          items = [order.product];
        } else if (order.goods && Array.isArray(order.goods)) {
          items = order.goods;
        } else if (order.goods && typeof order.goods === 'object') {
          // 处理单个goods对象的情况
          items = [order.goods];
        } else if (order.orderItem && Array.isArray(order.orderItem)) {
          // 支持orderItem数组
          items = order.orderItem;
        } else if (order.orderItem && typeof order.orderItem === 'object') {
          // 支持单个orderItem对象
          items = [order.orderItem];
        } else {
          // 处理订单对象直接包含商品字段的情况（常见于单商品订单）
          const hasProductFields = order.product_id || order.product_name || order.product_price || 
                                 order.goods_id || order.goods_name || order.goods_price ||
                                 order.productId || order.productName || order.productPrice;
          
          if (hasProductFields) {
            // 从订单对象直接提取商品信息
            items = [{...order}];
          }
        }
        
        // 处理商品项
        const normalizedItems = items.map(item => {
          if (!item || typeof item !== 'object') {
            // 如果商品项不是对象，返回默认值
            return {
              id: '',
              name: '未知商品',
              model: '',
              price: 0,
              quantity: 1,
              subtotal: 0
            };
          }
          
          return {
            id: item.id || item.product_id || item.goods_id || item.productId || item.goodsId || '',
            name: item.name || item.product_name || item.goods_name || item.productName || 
                  item.goodsName || item.product || item.product_title || '未知商品',
            model: item.model || item.specs || item.attributes || item.spec || 
                  item.product_model || item.goods_spec || '',
            price: parseFloat(item.price || item.unit_price || item.amount || 
                           item.product_price || item.goods_price || item.productPrice || 
                           item.goodsPrice || 0),
            quantity: parseInt(item.quantity || item.count || item.num || 
                              item.product_num || item.goods_num || 1),
            subtotal: parseFloat(item.subtotal || (item.price * item.quantity) || 
                               (item.unit_price * item.quantity) || 0)
          };
        });
        
        // 如果没有商品项，创建一个默认商品项
        if (normalizedItems.length === 0) {
          normalizedItems.push({
            id: '',
            name: '默认商品',
            model: '',
            price: 0,
            quantity: 1,
            subtotal: 0
          });
        }
        
        return {
          ...order,
          oid: orderId,
          receiver_name: receiverName,
          receiver_phone: receiverPhone,
          receiver_address: receiverAddress,
          amount: amount,
          order_time: orderTime,
          status: status,
          items: normalizedItems
        };
      });
    },

    calculateStats() {
      const stats = {
        pending: 0,
        shipped: 0,
        completed: 0,
        total: 0
      };

      this.orders.forEach(order => {
        if (order.status === 1) stats.pending++;
        if (order.status === 2) stats.shipped++;
        if (order.status === 3) stats.completed++;
        stats.total++;
      });

      this.orderStats = stats;
    },

    shipOrder(order) {
      this.shipmentOrder = order;
      this.shipment = {
        courier: '',
        tracking_number: '',
        note: ''
      };
      this.showShipModal = true;
    },

    async confirmShipment() {
      if (!this.validateShipment()) {
        return;
      }

      const bid = this.getBusinessId();

      if (!bid) {
        alert('商家信息不完整');
        return;
      }

      try {
        // 使用api.config.js中的配置发货
        const response = await axios.put(
            apiConfig.business.shipOrder(bid, this.shipmentOrder.oid),
            {
              courier: this.shipment.courier,
              trackingNumber: this.shipment.tracking_number,
              note: this.shipment.note
            }
        );

        if (response.data.code === 1) {
          this.shipmentOrder.status = 2;
          this.showShipModal = false;
          this.calculateStats();
          alert('发货成功！');
        } else {
          alert(`发货失败：${response.data.message}`);
        }
      } catch (error) {
        console.error('发货失败:', error);
        alert('发货失败，请稍后重试');
      }
    },

    validateShipment() {
      if (!this.shipment.courier.trim()) {
        alert('请选择物流公司');
        return false;
      }

      if (!this.shipment.tracking_number.trim()) {
        alert('请输入运单号');
        return false;
      }

      return true;
    },

    viewTracking(order) {
      alert(`查看物流信息：订单 #${order.oid}\n物流公司：${order.courier || '未设置'}\n运单号：${order.tracking_number || '未设置'}`);
    },

    cancelOrder(order) {
      if (confirm(`确定要取消订单 #${order.oid} 吗？`)) {
        this.cancelOrderAsync(order);
      }
    },

    async cancelOrderAsync(order) {
      const bid = this.getBusinessId();

      if (!bid) {
        alert('商家信息不完整');
        return;
      }

      try {
        // 注意：文档中只有用户取消订单的API，没有商家取消订单的API
        // 这里可能需要调用用户取消订单的API，或者在前端直接更新状态
        const response = await axios.put(
            `/api/orders/${order.oid}/cancel?uid=${order.uid}`
        );

        if (response.data.code === 1) {
          order.status = 4; // 商家取消设置为4
          this.calculateStats();
          alert('订单已取消');
        } else {
          alert(`取消失败：${response.data.message}`);
        }
      } catch (error) {
        console.error('取消订单失败:', error);
        alert('取消失败，请稍后重试');
      }
    },

    viewOrderDetail(order) {
      this.selectedOrder = { ...order };
    },

    exportOrders() {
      // 这里可以实现导出功能
      const data = this.filteredOrders.map(order => ({
        订单号: order.oid,
        收货人: order.receiver_name,
        电话: order.receiver_phone,
        地址: order.receiver_address,
        金额: order.amount,
        状态: this.getStatusText(order.status),
        下单时间: this.formatDateTime(order.order_time)
      }));

      // 简单的导出实现（实际项目中可以使用第三方库如xlsx）
      const csvContent = "data:text/csv;charset=utf-8,"
          + "订单号,收货人,电话,地址,金额,状态,下单时间\n"
          + data.map(row => Object.values(row).join(",")).join("\n");

      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `订单数据_${new Date().toLocaleDateString()}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    }
  },
  watch: {
    statusFilter() {
      this.currentPage = 1;
    },
    dateRange() {
      this.currentPage = 1;
    },
    searchKeyword() {
      this.currentPage = 1;
    }
  }
}
</script>

<style scoped>
.business-orders {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #2d3748;
  font-weight: 700;
}

.order-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px;
  background: white;
  border-radius: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  min-width: 100px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 16px;
  border-radius: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-box {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.search-input {
  width: 100%;
  padding: 10px 40px 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 0;
  font-size: 14px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #718096;
  font-size: 16px;
}

.filter-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 0;
  background: white;
  color: #4a5568;
  font-size: 14px;
  min-width: 120px;
}

.btn-export {
  padding: 10px 20px;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 0;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: background 0.3s;
}

.btn-export:hover {
  background: #38a169;
}

.export-icon {
  font-size: 16px;
}

.orders-table {
  background: white;
  border-radius: 0;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.orders-table table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th {
  background: #f8fafc;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.orders-table td {
  padding: 16px;
  border-bottom: 1px solid #f7fafc;
  vertical-align: middle;
}

.orders-table tbody tr:hover {
  background: #f8fafc;
}

.order-id {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
}

.order-products {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-width: 250px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.item-main-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.item-name {
  font-weight: 500;
  color: #4a5568;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-model {
  font-size: 12px;
  color: #718096;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-quantity {
  color: #718096;
  font-size: 13px;
  flex-shrink: 0;
}

.receiver-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.receiver-name {
  font-weight: 500;
  color: #2d3748;
}

.receiver-phone {
  font-size: 13px;
  color: #718096;
}

.order-amount {
  font-weight: 700;
  color: #667eea;
  font-size: 16px;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 0;
  font-size: 13px;
  font-weight: 500;
  display: inline-block;
}

.status-badge.pending {
  background: #fffaf0;
  color: #c05621;
}

.status-badge.shipped {
  background: #ebf8ff;
  color: #2b6cb0;
}

.status-badge.completed {
  background: #f0fff4;
  color: #276749;
}

.status-badge.cancelled {
  background: #fff5f5;
  color: #c53030;
}

.order-time {
  color: #718096;
  font-size: 13px;
  white-space: nowrap;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 100px;
  align-items: center;
  justify-content: center;
}

.btn-ship, .btn-track, .btn-cancel, .btn-detail {
  padding: 6px 12px;
  border: none;
  border-radius: 0;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-ship {
  background: #bee3f8;
  color: #2c5282;
}

.btn-ship:hover {
  background: #90cdf4;
}

.btn-track {
  background: #c6f6d5;
  color: #22543d;
}

.btn-track:hover {
  background: #9ae6b4;
}

.btn-cancel {
  background: #fed7d7;
  color: #c53030;
}

.btn-cancel:hover {
  background: #feb2b2;
}

.btn-detail {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-detail:hover {
  background: #cbd5e0;
}

.no-orders {
  text-align: center;
  color: #a0aec0;
  padding: 40px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pagination-btn {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #4a5568;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #718096;
  font-size: 14px;
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
  padding: 20px;
}

.modal {
  background: white;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 0;
}

.modal-lg {
  max-width: 800px;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #718096;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0;
  transition: background 0.3s;
}

.modal-close:hover {
  background: #f7fafc;
}

.modal-body {
  padding: 20px;
}

.order-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.detail-section:first-child {
  padding-top: 0;
  border-top: none;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #2d3748;
  font-weight: 600;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  font-size: 14px;
  color: #718096;
  min-width: 80px;
}

.detail-item span {
  font-size: 14px;
  color: #2d3748;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-item .amount {
  font-weight: 700;
  color: #667eea;
  font-size: 18px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 0;
}

.product-image {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-placeholder {
  font-size: 24px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 4px;
}

.product-price {
  font-size: 13px;
  color: #718096;
}

.product-quantity {
  font-weight: 600;
  color: #4a5568;
  min-width: 40px;
  text-align: center;
}

.product-total {
  font-weight: 700;
  color: #667eea;
  min-width: 80px;
  text-align: right;
}

.remark-content {
  padding: 12px;
  background: #f8fafc;
  border-radius: 0;
  color: #718096;
  line-height: 1.5;
}

.detail-actions {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-ship-large {
  padding: 12px 32px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: background 0.3s;
}

.btn-ship-large:hover {
  background: #5a67d8;
}

.icon {
  font-size: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #4a5568;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 0;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
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
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #4a5568;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.btn-cancel:hover {
  background: #f7fafc;
}

.btn-submit {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.btn-submit:hover {
  background: #5a67d8;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-stats {
    width: 100%;
    justify-content: space-between;
  }

  .stat-item {
    flex: 1;
    min-width: auto;
  }

  .filters {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    min-width: 100%;
  }

  .filter-group {
    flex-direction: column;
    width: 100%;
  }

  .filter-select, .btn-export {
    width: 100%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .order-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }
}
</style>