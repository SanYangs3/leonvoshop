<template>
  <div class="management-section">
    <div class="section-header">
      <h3>订单管理 <span class="simulation-badge">模拟数据</span></h3>
      <div class="section-actions">
        <!-- 搜索框 -->
        <div class="search-box">
          <input
              type="text"
              v-model="orderSearch"
              placeholder="搜索订单号、收货人、电话..."
              class="search-input"
          />
          <span class="search-icon">搜</span>
        </div>

        <!-- 状态筛选 -->
        <select v-model="statusFilter" class="filter-select">
          <option value="all">全部状态</option>
          <option value="0">待付款</option>
          <option value="1">待发货</option>
          <option value="2">待收货</option>
          <option value="3">已完成</option>
          <option value="4">已取消</option>
        </select>
        <span class="api-hint-simple">（状态筛选：当前无对应API，需后期完善）</span>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>订单号</th>
          <th>收货信息</th>
          <th>金额</th>
          <th>状态</th>
          <th>下单时间</th>
          <th>备注</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="order in filteredOrders" :key="order.oid">
          <td><strong>#{{ order.oid }}</strong></td>
          <td>
            <div class="receiver-info">
              <div class="receiver-name">{{ order.receiverName }}</div>
              <div class="receiver-phone">{{ order.receiverPhone }}</div>
              <div class="receiver-address">
                {{ order.province }}{{ order.receiverAddress }}
              </div>
            </div>
          </td>
          <td><strong class="order-amount">¥{{ order.amount.toFixed(2) }}</strong></td>
          <td>
              <span :class="['status-badge', getStatusClass(order.status)]">
                {{ getStatusText(order.status) }}
              </span>
          </td>
          <td>{{ order.orderTime }}</td>
          <td class="order-remark">{{ order.remark || '-' }}</td>
          <td>
            <div class="action-buttons">
              <!-- 根据状态显示不同操作按钮 -->
              <button
                  v-if="order.status === 1"
                  class="btn-action btn-ship"
                  @click="shipOrder(order)"
              >
                发货
              </button>
              <button
                  v-if="order.status === 0"
                  class="btn-action btn-cancel"
                  @click="cancelOrder(order)"
              >
                取消
              </button>
              <button
                  v-if="order.status === 2"
                  class="btn-action btn-complete"
                  @click="completeOrder(order)"
              >
                收货
              </button>

              <!-- 总是显示的查看详情按钮 -->
              <button
                  class="btn-action btn-view"
                  @click="viewOrderDetail(order)"
              >
                详情
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredOrders.length === 0" class="empty-state">
        {{ orderSearch || statusFilter !== 'all' ? '暂无符合条件的订单' : '暂无订单数据' }}
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="table-footer">
      <div class="stats-info">
        共 {{ filteredOrders.length }} 条订单
        <span v-if="statusFilter !== 'all'" class="filter-hint">
          （状态：{{ getStatusText(statusFilter) }}）
        </span>
      </div>
    </div>

    <!-- 订单详情模态框 -->
    <div v-if="selectedOrder" class="modal-overlay" @click.self="selectedOrder = null">
      <div class="modal modal-md">
        <div class="modal-header">
          <h3>订单详情 #{{ selectedOrder.oid }}</h3>
          <button class="modal-close" @click="selectedOrder = null">×</button>
        </div>
        <div class="modal-body">
          <div class="order-detail">
            <!-- 状态和金额 -->
            <div class="detail-row">
              <div class="detail-item">
                <label>订单状态：</label>
                <span :class="['status-badge', getStatusClass(selectedOrder.status)]">
                  {{ getStatusText(selectedOrder.status) }}
                </span>
              </div>
              <div class="detail-item">
                <label>订单金额：</label>
                <span class="order-amount">¥{{ selectedOrder.amount.toFixed(2) }}</span>
              </div>
            </div>

            <!-- 时间信息 -->
            <div class="detail-row">
              <div class="detail-item">
                <label>下单时间：</label>
                <span>{{ selectedOrder.orderTime }}</span>
              </div>
              <div class="detail-item">
                <label>用户ID：</label>
                <span>{{ selectedOrder.uid }}</span>
              </div>
            </div>

            <!-- 收货信息 -->
            <div class="detail-section">
              <h4>收货信息</h4>
              <div class="detail-grid">
                <div class="detail-item full-width">
                  <label>收货人：</label>
                  <span>{{ selectedOrder.receiverName }}</span>
                </div>
                <div class="detail-item full-width">
                  <label>联系电话：</label>
                  <span>{{ selectedOrder.receiverPhone }}</span>
                </div>
                <div class="detail-item full-width">
                  <label>收货地址：</label>
                  <span>{{ selectedOrder.province }}{{ selectedOrder.receiverAddress }}</span>
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminOrders',
  data() {
    return {
      // 搜索和筛选
      orderSearch: '',
      statusFilter: 'all',

      // 订单数据 - 完全模拟数据
      orders: [
        { oid: 1001, status: 1, orderTime: '2023-12-19 14:30', amount: 298.50, uid: 1, province: '北京市', receiverAddress: '朝阳区建国路88号', receiverName: '张三', receiverPhone: '13800138000', remark: '尽快发货' },
        { oid: 1002, status: 0, orderTime: '2023-12-18 10:20', amount: 1299.00, uid: 2, province: '上海市', receiverAddress: '浦东新区陆家嘴', receiverName: '李四', receiverPhone: '13900139000', remark: '包装好一点' },
        { oid: 1003, status: 2, orderTime: '2023-12-17 16:45', amount: 8999.00, uid: 3, province: '广东省', receiverAddress: '深圳市南山区科技园', receiverName: '王五', receiverPhone: '13600136000', remark: '发票抬头：个人' },
        { oid: 1004, status: 1, orderTime: '2023-12-19 09:15', amount: 5499.00, uid: 4, province: '浙江省', receiverAddress: '杭州市西湖区文三路', receiverName: '赵六', receiverPhone: '13500135000', remark: '周末配送' },
        { oid: 1005, status: 3, orderTime: '2023-12-15 11:30', amount: 3299.00, uid: 5, province: '江苏省', receiverAddress: '南京市鼓楼区中山路', receiverName: '孙七', receiverPhone: '13700137000', remark: '已收货，很满意' },
        { oid: 1006, status: 4, orderTime: '2023-12-14 13:20', amount: 7299.00, uid: 6, province: '四川省', receiverAddress: '成都市锦江区春熙路', receiverName: '周八', receiverPhone: '13400134000', remark: '临时有事，取消订单' },
        { oid: 1007, status: 0, orderTime: '2023-12-19 08:45', amount: 4599.00, uid: 7, province: '湖北省', receiverAddress: '武汉市洪山区光谷', receiverName: '吴九', receiverPhone: '13300133000', remark: '选择到店自提' },
        { oid: 1008, status: 1, orderTime: '2023-12-18 15:10', amount: 12999.00, uid: 8, province: '陕西省', receiverAddress: '西安市雁塔区高新路', receiverName: '郑十', receiverPhone: '13200132000', remark: '送公司前台' }
      ],

      selectedOrder: null,

      // 状态映射
      statusMap: {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      }
    }
  },
  computed: {
    // 前端搜索和筛选
    filteredOrders() {
      let filtered = this.orders

      // 1. 状态筛选
      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(order => order.status.toString() === this.statusFilter)
      }

      // 2. 搜索筛选
      if (this.orderSearch) {
        const search = this.orderSearch.toLowerCase()
        filtered = filtered.filter(order =>
            order.oid.toString().includes(search) ||
            order.receiverName.toLowerCase().includes(search) ||
            order.receiverPhone.includes(search) ||
            order.receiverAddress.toLowerCase().includes(search) ||
            (order.remark && order.remark.toLowerCase().includes(search))
        )
      }

      return filtered
    }
  },
  methods: {
    // 状态相关方法
    getStatusText(status) {
      return this.statusMap[status] || '未知状态'
    },

    getStatusClass(status) {
      return `status-${status}`
    },

    // 订单操作
    viewOrderDetail(order) {
      this.selectedOrder = { ...order }
    },

    shipOrder(order) {
      alert(`发货订单 #${order.oid}\n（当前为模拟操作）`)
    },

    cancelOrder(order) {
      if (confirm(`确定要取消订单 #${order.oid} 吗？`)) {
        alert(`取消订单 #${order.oid}\n（当前为模拟操作）`)
      }
    },

    completeOrder(order) {
      alert(`确认收货 #${order.oid}\n（当前为模拟操作）`)
    }
  }
}
</script>

<style scoped>
/* 完全复用商品管理模块的CSS样式，确保风格一致 */

.management-section {
  background: white;
  border: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

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
  display: flex;
  align-items: center;
  gap: 8px;
}

.simulation-badge {
  background: #bbdefb;
  color: #0d47a1;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 2px;
  font-weight: 500;
}

.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
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
  border-radius: 2px;
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

/* 筛选下拉框 */
.filter-select {
  padding: 8px 12px;
  border: 1px solid #d0d0d0;
  background: white;
  color: #333;
  font-size: 14px;
  border-radius: 2px;
  min-width: 100px;
}

.filter-select:focus {
  outline: none;
  border-color: #1a237e;
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
  min-height: 300px;
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
  border-bottom: 2px solid #e0e0e0;
  white-space: nowrap;
}

.data-table td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: top;
}

.data-table tbody tr:hover {
  background: #f8f9ff;
}

/* 收货信息 */
.receiver-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.receiver-name {
  font-weight: 500;
  color: #333;
}

.receiver-phone {
  font-size: 13px;
  color: #666;
}

.receiver-address {
  font-size: 12px;
  color: #888;
  line-height: 1.3;
  margin-top: 2px;
}

/* 订单金额 */
.order-amount {
  color: #d32f2f;
  font-weight: 600;
}

/* 订单备注 */
.order-remark {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
}

/* 状态徽章 */
.status-badge {
  padding: 4px 10px;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  font-weight: 500;
}

.status-0 { background: #fff3e0; color: #f57c00; } /* 待付款 */
.status-1 { background: #e3f2fd; color: #1976d2; } /* 待发货 */
.status-2 { background: #fff8e1; color: #ffa000; } /* 待收货 */
.status-3 { background: #e8f5e8; color: #2e7d32; } /* 已完成 */
.status-4 { background: #f5f5f5; color: #757575; } /* 已取消 */

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-width: 180px;
}

.btn-action {
  padding: 6px 10px;
  border: none;
  font-size: 12px;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-ship {
  background: #bbdefb;
  color: #1565c0;
}

.btn-ship:hover {
  background: #90caf9;
}

.btn-cancel {
  background: #ffcdd2;
  color: #c62828;
}

.btn-cancel:hover {
  background: #ef9a9a;
}

.btn-complete {
  background: #c8e6c9;
  color: #2e7d32;
}

.btn-complete:hover {
  background: #a5d6a7;
}

.btn-view {
  background: #f5f5f5;
  color: #616161;
}

.btn-view:hover {
  background: #e0e0e0;
}

/* 空状态 */
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #999;
  background: #fafafa;
  border-top: 1px solid #eee;
  font-size: 14px;
  font-style: italic;
}

/* 表格底部 */
.table-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.stats-info {
  color: #666;
  font-size: 13px;
}

.filter-hint {
  color: #f57c00;
  margin-left: 8px;
  font-weight: 500;
}

/* 模态框样式 */
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
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 2px;
}

.modal-md {
  width: 500px;
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

/* 订单详情样式 */
.order-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-row {
  display: flex;
  gap: 30px;
  align-items: center;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  font-size: 14px;
  color: #666;
  min-width: 80px;
}

.detail-item span {
  font-size: 14px;
  color: #333;
}

.detail-item.full-width {
  width: 100%;
}

.detail-section {
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #1a237e;
  font-size: 15px;
  font-weight: 600;
}

.detail-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.remark-content {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 2px;
  color: #555;
  font-size: 14px;
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .section-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .data-table {
    font-size: 13px;
  }

  .data-table th,
  .data-table td {
    padding: 8px;
  }

  .action-buttons {
    min-width: auto;
    flex-direction: column;
    gap: 4px;
  }

  .btn-action {
    width: 100%;
    text-align: center;
  }

  .detail-row {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .modal {
    width: 95%;
  }
}
</style>