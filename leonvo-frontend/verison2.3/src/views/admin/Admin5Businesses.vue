<template>
  <div class="management-section">
    <div class="section-header">
      <h3>商家管理 <span class="simulation-badge">模拟数据</span></h3>
      <div class="section-actions">
        <!-- 搜索框 -->
        <div class="search-box">
          <input
              type="text"
              v-model="businessSearch"
              placeholder="搜索商家名称..."
              class="search-input"
          />
          <span class="search-icon">搜</span>
        </div>

        <!-- 添加商家按钮 -->
        <button class="btn-add" @click="addBusiness">
          <span class="btn-icon">+</span>
          添加商家
        </button>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>商家ID</th>
          <th>商家信息</th>
          <th>状态</th>
          <th>注册时间</th>
          <th>商品数量</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="business in filteredBusinesses" :key="business.bid">
          <td>{{ business.bid }}</td>
          <td>
            <div class="business-cell">
              <div class="business-icon">🏪</div>
              <div class="business-info">
                <div class="business-name">{{ business.bname }}</div>
                <div class="business-contact">
                  <span v-if="business.contact">联系人：{{ business.contact }}</span>
                  <span v-if="business.phone">电话：{{ business.phone }}</span>
                </div>
              </div>
            </div>
          </td>
          <td>
              <span :class="['status-badge', business.status ? 'active' : 'inactive']">
                {{ business.status ? '正常' : '禁用' }}
              </span>
          </td>
          <td>{{ business.createTime || '2023-11-15' }}</td>
          <td>
              <span :class="['product-count', getProductCountLevel(business.productCount)]">
                {{ business.productCount || 0 }}件
              </span>
          </td>
          <td>
            <div class="action-buttons">
              <button class="btn-edit" @click="editBusiness(business)">编辑</button>
              <button
                  :class="['btn-status', business.status ? 'btn-disable' : 'btn-enable']"
                  @click="toggleBusinessStatus(business)"
              >
                {{ business.status ? '禁用' : '启用' }}
              </button>
              <button class="btn-view" @click="viewBusinessDetail(business)">详情</button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-if="filteredBusinesses.length === 0" class="empty-state">
        {{ businessSearch ? '暂无符合条件的商家' : '暂无商家数据' }}
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="table-footer">
      <div class="stats-info">
        共 {{ filteredBusinesses.length }} 家商家
      </div>
    </div>

    <!-- 商家详情模态框 -->
    <div v-if="selectedBusiness" class="modal-overlay" @click.self="selectedBusiness = null">
      <div class="modal modal-md">
        <div class="modal-header">
          <h3>商家详情 #{{ selectedBusiness.bid }}</h3>
          <button class="modal-close" @click="selectedBusiness = null">×</button>
        </div>
        <div class="modal-body">
          <div class="business-detail">
            <!-- 基本信息 -->
            <div class="detail-section">
              <h4>基本信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>商家名称：</label>
                  <span class="business-name">{{ selectedBusiness.bname }}</span>
                </div>
                <div class="detail-item">
                  <label>商家ID：</label>
                  <span>{{ selectedBusiness.bid }}</span>
                </div>
                <div class="detail-item">
                  <label>状态：</label>
                  <span :class="['status-badge', selectedBusiness.status ? 'active' : 'inactive']">
                    {{ selectedBusiness.status ? '正常' : '禁用' }}
                  </span>
                </div>
                <div class="detail-item">
                  <label>注册时间：</label>
                  <span>{{ selectedBusiness.createTime || '2023-11-15' }}</span>
                </div>
              </div>
            </div>

            <!-- 联系信息 -->
            <div class="detail-section">
              <h4>联系信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>联系人：</label>
                  <span>{{ selectedBusiness.contact || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <label>联系电话：</label>
                  <span>{{ selectedBusiness.phone || '未设置' }}</span>
                </div>
                <div class="detail-item">
                  <label>联系邮箱：</label>
                  <span>{{ selectedBusiness.email || '未设置' }}</span>
                </div>
                <div class="detail-item full-width">
                  <label>商家地址：</label>
                  <span>{{ selectedBusiness.address || '未设置地址' }}</span>
                </div>
              </div>
            </div>

            <!-- 经营信息 -->
            <div class="detail-section">
              <h4>经营信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>商品数量：</label>
                  <span :class="['product-count', getProductCountLevel(selectedBusiness.productCount)]">
                    {{ selectedBusiness.productCount || 0 }}件
                  </span>
                </div>
                <div class="detail-item">
                  <label>在售商品：</label>
                  <span>{{ selectedBusiness.onSaleCount || 0 }}件</span>
                </div>
                <div class="detail-item">
                  <label>店铺评分：</label>
                  <span class="rating">{{ selectedBusiness.rating || 0 }} ★</span>
                </div>
                <div class="detail-item">
                  <label>累计订单：</label>
                  <span>{{ selectedBusiness.orderCount || 0 }}单</span>
                </div>
              </div>
            </div>

            <!-- 商家描述 -->
            <div class="detail-section" v-if="selectedBusiness.description">
              <h4>商家描述</h4>
              <div class="description-content">
                {{ selectedBusiness.description }}
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
  name: 'AdminBusinesses',
  data() {
    return {
      // 搜索
      businessSearch: '',

      // 商家数据 - 模拟数据
      businesses: [
        { bid: 1, bname: '联想官方旗舰店', status: 1, contact: '张经理', phone: '13800138001', email: 'lenovo@lenovo.com', address: '北京市海淀区上地信息产业基地', productCount: 25, onSaleCount: 22, rating: 4.8, orderCount: 156, createTime: '2023-10-10', description: '联想官方直营店，正品保证' },
        { bid: 2, bname: '泡泡数码专营店', status: 1, contact: '李经理', phone: '13900139001', email: 'paopao@digital.com', address: '上海市浦东新区张江高科技园区', productCount: 18, onSaleCount: 15, rating: 4.5, orderCount: 89, createTime: '2023-10-15', description: '专业数码产品销售，七年老店' },
        { bid: 3, bname: '极客电竞装备店', status: 1, contact: '王店长', phone: '13600136001', email: 'geek@esports.com', address: '广州市天河区天河路', productCount: 32, onSaleCount: 30, rating: 4.7, orderCount: 203, createTime: '2023-10-20', description: '专业电竞装备，游戏玩家首选' },
        { bid: 4, bname: '商务办公优选', status: 0, contact: '赵主管', phone: '13500135001', email: 'office@business.com', address: '深圳市南山区科技园', productCount: 12, onSaleCount: 0, rating: 4.3, orderCount: 45, createTime: '2023-10-25', description: '专注商务办公设备，企业采购优选' },
        { bid: 5, bname: '学生特惠专区', status: 1, contact: '刘老师', phone: '13700137001', email: 'student@discount.com', address: '武汉市洪山区珞喻路', productCount: 15, onSaleCount: 12, rating: 4.6, orderCount: 78, createTime: '2023-11-01', description: '学生专享优惠，教育认证商家' },
        { bid: 6, bname: '二手精品数码', status: 1, contact: '陈师傅', phone: '13400134001', email: 'used@digital.com', address: '成都市武侯区科华北路', productCount: 28, onSaleCount: 25, rating: 4.4, orderCount: 112, createTime: '2023-11-05', description: '二手精品数码，经过严格检测' },
        { bid: 7, bname: '企业采购中心', status: 1, contact: '孙总监', phone: '13300133001', email: 'enterprise@procurement.com', address: '南京市鼓楼区中山路', productCount: 42, onSaleCount: 38, rating: 4.9, orderCount: 256, createTime: '2023-11-10', description: '企业批量采购，提供定制解决方案' },
        { bid: 8, bname: '配件精品屋', status: 0, contact: '周店主', phone: '13200132001', email: 'accessories@shop.com', address: '杭州市西湖区文三路', productCount: 56, onSaleCount: 0, rating: 4.2, orderCount: 67, createTime: '2023-11-15', description: '电脑配件专卖，各种外设齐全' }
      ],

      selectedBusiness: null
    }
  },
  computed: {
    // 前端搜索
    filteredBusinesses() {
      if (!this.businessSearch) return this.businesses

      const search = this.businessSearch.toLowerCase()
      return this.businesses.filter(business =>
          business.bname.toLowerCase().includes(search) ||
          (business.contact && business.contact.toLowerCase().includes(search)) ||
          (business.phone && business.phone.includes(search))
      )
    }
  },
  methods: {
    // 根据商品数量获取样式级别
    getProductCountLevel(count) {
      if (!count || count === 0) return 'zero'
      if (count < 10) return 'low'
      if (count < 30) return 'medium'
      return 'high'
    },

    // 商家操作
    addBusiness() {
      alert('添加商家\n（当前为模拟操作）')
    },

    editBusiness(business) {
      alert(`编辑商家：${business.bname}\n（当前为模拟操作）`)
    },

    toggleBusinessStatus(business) {
      const action = business.status ? '禁用' : '启用'
      if (confirm(`确定要${action}商家"${business.bname}"吗？`)) {
        business.status = business.status ? 0 : 1
        alert(`已${action}商家（模拟操作）`)
      }
    },

    viewBusinessDetail(business) {
      this.selectedBusiness = { ...business }
    }
  }
}
</script>

<style scoped>
/* 完全复用商品和订单管理模块的CSS样式，确保风格一致 */

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

/* 添加按钮 */
.btn-add {
  padding: 8px 16px;
  background: #1a237e;
  color: white;
  border: none;
  border-radius: 2px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.btn-add:hover {
  background: #283593;
}

.btn-icon {
  font-size: 16px;
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
  vertical-align: middle;
}

.data-table tbody tr:hover {
  background: #f8f9ff;
}

/* 商家单元格 */
.business-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.business-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 2px;
}

.business-info {
  display: flex;
  flex-direction: column;
}

.business-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.business-contact {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.business-contact span {
  font-size: 12px;
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

.status-badge.active {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-badge.inactive {
  background: #ffebee;
  color: #c62828;
}

/* 商品数量徽章 */
.product-count {
  padding: 4px 10px;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  font-weight: 500;
}

.product-count.zero {
  background: #f5f5f5;
  color: #757575;
}

.product-count.low {
  background: #fff3e0;
  color: #f57c00;
}

.product-count.medium {
  background: #e3f2fd;
  color: #1976d2;
}

.product-count.high {
  background: #e8f5e8;
  color: #2e7d32;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  min-width: 180px;
}

.btn-edit, .btn-status, .btn-view {
  padding: 6px 10px;
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
  background: #ffcdd2;
  color: #c62828;
}

.btn-status.btn-disable:hover {
  background: #ef9a9a;
}

.btn-status.btn-enable {
  background: #c8e6c9;
  color: #2e7d32;
}

.btn-status.btn-enable:hover {
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

/* 商家详情样式 */
.business-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.detail-section:first-child {
  padding-top: 0;
  border-top: none;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #1a237e;
  font-size: 15px;
  font-weight: 600;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
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
  grid-column: 1 / -1;
}

.detail-item .business-name {
  font-weight: 600;
  color: #1a237e;
}

.detail-item .rating {
  font-weight: 600;
  color: #ff9800;
}

.description-content {
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

  .btn-edit, .btn-status, .btn-view {
    width: 100%;
    text-align: center;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .modal {
    width: 95%;
  }
}
</style>