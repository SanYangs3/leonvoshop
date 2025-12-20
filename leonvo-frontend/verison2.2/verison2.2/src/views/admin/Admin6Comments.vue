<template>
  <div class="management-section">
    <div class="section-header">
      <h3>评论管理 <span class="simulation-badge">按商品查看</span></h3>
      <div class="section-actions">
        <!-- 商品选择框 -->
        <div class="product-selector">
          <select v-model="selectedProductId" class="filter-select" @change="loadProductFeedbacks">
            <option value="">请选择商品...</option>
            <option v-for="product in products" :key="product.pid" :value="product.pid">
              {{ product.name }} (ID: {{ product.pid }})
            </option>
          </select>
        </div>

        <!-- 评分筛选 -->
        <select v-model="ratingFilter" class="filter-select" :disabled="!selectedProductId">
          <option value="all">全部评分</option>
          <option value="5">5星</option>
          <option value="4">4星</option>
          <option value="3">3星</option>
          <option value="2">2星</option>
          <option value="1">1星</option>
        </select>
      </div>
    </div>

    <!-- 提示信息 -->
    <div v-if="!selectedProductId" class="selection-hint">
      <div class="hint-icon">💡</div>
      <div class="hint-text">
        <strong>使用说明：</strong>请先选择商品查看对应评价<br>
        <span class="hint-api">对应API：GET /api/feedbacks/product/{商品ID}</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-state">
      正在加载评价数据...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      加载失败：{{ error }}
      <button @click="loadProductFeedbacks" class="btn-retry">重试</button>
    </div>

    <!-- 评论表格 -->
    <div v-else class="table-container">
      <div class="product-header" v-if="selectedProduct">
        <div class="product-info">
          <div class="product-name">{{ selectedProduct.name }}</div>
          <div class="product-stats">
            共 {{ filteredFeedbacks.length }} 条评价 • 平均评分：{{ averageRating.toFixed(1) }} ★
          </div>
        </div>
      </div>

      <table class="data-table" v-if="filteredFeedbacks.length > 0">
        <thead>
        <tr>
          <th>评价ID</th>
          <th>用户</th>
          <th>评分</th>
          <th>评价内容</th>
          <th>评价时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="feedback in filteredFeedbacks" :key="feedback.fid">
          <td>{{ feedback.fid }}</td>
          <td>
            <div class="user-cell">
              <div class="user-avatar-sm">U{{ feedback.uid }}</div>
              <div class="user-info">
                <div class="user-name">用户 {{ feedback.uid }}</div>
              </div>
            </div>
          </td>
          <td>
            <div class="rating-display">
                <span class="rating-stars">
                  <span v-for="n in 5" :key="n" :class="['star', n <= feedback.star ? 'filled' : 'empty']">★</span>
                </span>
              <span class="rating-value">{{ feedback.star }}星</span>
            </div>
          </td>
          <td class="comment-content">
            {{ feedback.comment || '（无文字评价）' }}
          </td>
          <td>{{ formatFeedTime(feedback.feedTime) }}</td>
          <td>
            <div class="action-buttons">
              <button class="btn-view" @click="viewFeedbackDetail(feedback)">详情</button>
              <button class="btn-delete" @click="deleteFeedback(feedback)">删除</button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        该商品暂无评价
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="table-footer" v-if="selectedProductId && filteredFeedbacks.length > 0">
      <div class="stats-info">
        共 {{ filteredFeedbacks.length }} 条评价
        <span v-if="ratingFilter !== 'all'" class="filter-hint">
          （{{ ratingFilter }}星筛选）
        </span>
      </div>
    </div>

    <!-- 评价详情模态框 -->
    <div v-if="selectedFeedback" class="modal-overlay" @click.self="selectedFeedback = null">
      <div class="modal modal-sm">
        <div class="modal-header">
          <h3>评价详情 #{{ selectedFeedback.fid }}</h3>
          <button class="modal-close" @click="selectedFeedback = null">×</button>
        </div>
        <div class="modal-body">
          <div class="feedback-detail">
            <!-- 基本信息 -->
            <div class="detail-row">
              <div class="detail-item">
                <label>商品：</label>
                <span class="product-name">{{ selectedProduct?.name || '商品' + selectedProductId }}</span>
              </div>
              <div class="detail-item">
                <label>用户ID：</label>
                <span>{{ selectedFeedback.uid }}</span>
              </div>
            </div>

            <!-- 评分 -->
            <div class="detail-section">
              <h4>评分</h4>
              <div class="rating-large">
                <span class="rating-stars-large">
                  <span v-for="n in 5" :key="n" :class="['star-large', n <= selectedFeedback.star ? 'filled' : 'empty']">★</span>
                </span>
                <span class="rating-value-large">{{ selectedFeedback.star }}星</span>
              </div>
            </div>

            <!-- 评价内容 -->
            <div class="detail-section">
              <h4>评价内容</h4>
              <div class="comment-content-large">
                {{ selectedFeedback.comment || '（无文字评价）' }}
              </div>
            </div>

            <!-- 时间信息 -->
            <div class="detail-section">
              <h4>时间信息</h4>
              <div class="detail-grid">
                <div class="detail-item full-width">
                  <label>评价时间：</label>
                  <span>{{ formatFeedTime(selectedFeedback.feedTime) }}</span>
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
export default {
  name: 'AdminComments',
  data() {
    return {
      // 商品选择
      selectedProductId: '',
      ratingFilter: 'all',

      // 数据
      products: [
        { pid: 1, name: '联想拯救者Y9000P' },
        { pid: 2, name: '联想小新Pro 14' },
        { pid: 3, name: '联想ThinkPad X1' },
        { pid: 4, name: '联想拯救者R7000' }
      ],
      feedbacks: [],
      loading: false,
      error: null,
      selectedProduct: null,
      selectedFeedback: null
    }
  },
  computed: {
    // 当前选中的商品
    currentProduct() {
      return this.products.find(p => p.pid == this.selectedProductId) || null
    },

    // 筛选后的评价
    filteredFeedbacks() {
      if (this.ratingFilter === 'all') return this.feedbacks
      return this.feedbacks.filter(f => f.star == this.ratingFilter)
    },

    // 平均评分
    averageRating() {
      if (this.feedbacks.length === 0) return 0
      const total = this.feedbacks.reduce((sum, f) => sum + f.star, 0)
      return total / this.feedbacks.length
    }
  },
  methods: {
    // 加载商品评价
    async loadProductFeedbacks() {
      if (!this.selectedProductId) {
        this.feedbacks = []
        this.selectedProduct = null
        return
      }

      this.loading = true
      this.error = null
      this.selectedProduct = this.currentProduct

      try {
        // 模拟API调用 - 对应真实API：GET /api/feedbacks/product/{pid}
        await new Promise(resolve => setTimeout(resolve, 500)) // 模拟网络延迟

        // 模拟数据 - 根据商品ID生成不同的评价
        const mockFeedbacks = this.generateMockFeedbacks(this.selectedProductId)
        this.feedbacks = mockFeedbacks
      } catch (err) {
        this.error = err.message || '加载评价失败'
      } finally {
        this.loading = false
      }
    },

    // 生成模拟评价数据
    generateMockFeedbacks(productId) {
      const baseFeedbacks = [
        { fid: 101, uid: 1, pid: productId, feedTime: '2023-12-19T14:30:45', star: 5, comment: '商品质量非常好，物流也很快！强烈推荐！' },
        { fid: 102, uid: 2, pid: productId, feedTime: '2023-12-18T10:20:33', star: 4, comment: '还不错，性价比高，就是包装有点简陋' },
        { fid: 103, uid: 3, pid: productId, feedTime: '2023-12-17T16:45:22', star: 5, comment: '非常满意，性能强劲，玩游戏很流畅' },
        { fid: 104, uid: 4, pid: productId, feedTime: '2023-12-16T09:15:18', star: 3, comment: '中规中矩，没有想象中那么好' },
        { fid: 105, uid: 5, pid: productId, feedTime: '2023-12-15T11:30:55', star: 2, comment: '有点失望，散热不太好' }
      ]

      // 为不同商品添加一些差异化评价
      if (productId == 1) { // 游戏本
        baseFeedbacks.push(
            { fid: 106, uid: 6, pid: productId, feedTime: '2023-12-14T13:20:33', star: 5, comment: '电竞神器！跑分很高，144Hz屏幕很流畅' },
            { fid: 107, uid: 7, pid: productId, feedTime: '2023-12-13T15:40:12', star: 4, comment: '性能不错，就是续航一般' }
        )
      } else if (productId == 2) { // 轻薄本
        baseFeedbacks.push(
            { fid: 106, uid: 6, pid: productId, feedTime: '2023-12-14T13:20:33', star: 5, comment: '轻薄便携，适合出差办公' },
            { fid: 107, uid: 7, pid: productId, feedTime: '2023-12-13T15:40:12', star: 5, comment: '颜值很高，屏幕色彩鲜艳' }
        )
      }

      return baseFeedbacks
    },

    // 格式化时间
    formatFeedTime(timeString) {
      if (!timeString) return '-'
      try {
        const date = new Date(timeString)
        return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } catch {
        return timeString
      }
    },

    // 评价操作
    viewFeedbackDetail(feedback) {
      this.selectedFeedback = { ...feedback }
    },

    deleteFeedback(feedback) {
      if (confirm(`确定要删除评价 #${feedback.fid} 吗？`)) {
        alert(`删除评价 #${feedback.fid}\n（当前为模拟操作，需对应API支持）`)
      }
    }
  },
  watch: {
    // 监听评分筛选变化
    ratingFilter() {
      // 筛选已在计算属性中处理
    },

    // 监听商品选择变化
    selectedProductId() {
      this.loadProductFeedbacks()
    }
  }
}
</script>

<style scoped>
/* 复用之前的样式体系 */
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
  background: #e3f2fd;
  color: #1565c0;
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

/* 商品选择器 */
.product-selector {
  min-width: 200px;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #d0d0d0;
  background: white;
  color: #333;
  font-size: 14px;
  border-radius: 2px;
  min-width: 150px;
}

.filter-select:focus {
  outline: none;
  border-color: #1a237e;
}

.filter-select:disabled {
  background: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

/* 提示信息 */
.selection-hint {
  padding: 20px;
  text-align: center;
  background: #f8f9ff;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.hint-icon {
  font-size: 32px;
  color: #3949ab;
}

.hint-text {
  font-size: 14px;
  color: #555;
  line-height: 1.5;
}

.hint-api {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #666;
  font-family: monospace;
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 2px;
}

/* 商品头部 */
.product-header {
  padding: 16px;
  background: #f8f9ff;
  border-bottom: 1px solid #e0e0e0;
}

.product-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a237e;
}

.product-stats {
  font-size: 13px;
  color: #666;
}

/* 加载和错误状态 */
.loading-state,
.error-state,
.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #666;
  background: #fafafa;
  border-top: 1px solid #eee;
  font-size: 14px;
}

.error-state {
  color: #c62828;
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

.empty-state {
  color: #999;
  font-style: italic;
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
  font-size: 12px;
  border-radius: 2px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  color: #333;
}

/* 评分显示 */
.rating-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-stars {
  display: flex;
  gap: 1px;
}

.star {
  font-size: 14px;
}

.star.filled {
  color: #ff9800;
}

.star.empty {
  color: #e0e0e0;
}

.rating-value {
  font-size: 12px;
  color: #f57c00;
  font-weight: 500;
}

/* 评价内容 */
.comment-content {
  max-width: 200px;
  line-height: 1.4;
  color: #555;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-view, .btn-delete {
  padding: 6px 10px;
  border: none;
  font-size: 12px;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-view {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-view:hover {
  background: #bbdefb;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
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

.modal-sm {
  width: 450px;
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
  display: flex;
  align-items: center;
  gap: 8px;
}

.modal-badge {
  background: #f3e5f5;
  color: #7b1fa2;
  padding: 2px 8px;
  font-size: 11px;
  border-radius: 2px;
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

/* 评价详情样式 */
.feedback-detail {
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
  min-width: 60px;
}

.detail-item span {
  font-size: 14px;
  color: #333;
}

.detail-item .product-name {
  font-weight: 600;
  color: #1a237e;
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

/* 大号评分 */
.rating-large {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.rating-stars-large {
  display: flex;
  gap: 4px;
}

.star-large {
  font-size: 28px;
}

.star-large.filled {
  color: #ff9800;
}

.star-large.empty {
  color: #e0e0e0;
}

.rating-value-large {
  font-size: 18px;
  font-weight: 600;
  color: #f57c00;
}

/* 大号评价内容 */
.comment-content-large {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 2px;
  color: #555;
  font-size: 15px;
  line-height: 1.6;
  border-left: 3px solid #e3f2fd;
}

.detail-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item.full-width {
  width: 100%;
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

  .product-selector,
  .filter-select {
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
    flex-direction: column;
    gap: 4px;
  }

  .btn-view, .btn-delete {
    width: 100%;
    text-align: center;
  }

  .detail-row {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .product-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .modal {
    width: 95%;
  }
}
</style>