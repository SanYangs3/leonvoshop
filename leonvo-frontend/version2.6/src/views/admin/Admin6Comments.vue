<template>
  <div class="management-section">
    <div class="section-header">
      <h3>评论管理</h3>
      <div class="section-actions">
        <!-- 商品选择框 -->
        <div class="product-selector">
          <select v-model="selectedProductId" class="filter-select">
            <option value="">所有商品</option>
            <option v-for="product in products" :key="product.pid" :value="product.pid">
              {{ product.name }}
            </option>
          </select>
        </div>

        <!-- 评分筛选 -->
        <select v-model="ratingFilter" class="filter-select">
          <option value="all">全部评分</option>
          <option value="5">5星</option>
          <option value="4">4星</option>
          <option value="3">3星</option>
          <option value="2">2星</option>
          <option value="1">1星</option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      正在加载评价数据...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      加载失败：{{ error }}
      <button @click="loadData" class="btn-retry">重试</button>
    </div>

    <!-- 评论表格 -->
    <div v-else class="table-container">
      <div class="product-header">
        <div class="product-info">
          <div class="product-name">
            {{ selectedProductId ? (currentProduct?.name || '未知商品') : '所有商品评价' }}
          </div>
          <div class="product-stats">
            共 {{ filteredFeedbacks.length }} 条评价 
            <span v-if="filteredFeedbacks.length > 0">
              • 平均评分：{{ averageRating.toFixed(1) }} ★
            </span>
          </div>
        </div>
      </div>

      <table class="data-table" v-if="filteredFeedbacks.length > 0">
        <thead>
        <tr>
          <th>评价ID</th>
          <th>商品</th>
          <th>用户ID</th>
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
            <span class="product-badge">{{ getProductName(feedback.pid) }}</span>
          </td>
          <td>{{ feedback.uid }}</td>
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
            </div>
          </td>
        </tr>
        </tbody>
      </table>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        暂无评价数据
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="table-footer" v-if="filteredFeedbacks.length > 0">
      <div class="stats-info">
        显示 {{ filteredFeedbacks.length }} 条评价
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
                <span class="product-name">{{ getProductName(selectedFeedback.pid) }}</span>
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
import apiConfig from '@/config/api.config'
import axios from 'axios';

// 模拟数据 - 备用（当API全挂时）
const MOCK_FEEDBACKS = [
  { fid: 101, uid: 1, pid: 1, feedTime: '2023-12-19T14:30:45', star: 5, comment: '商品质量非常好，物流也很快！强烈推荐！' },
  { fid: 102, uid: 2, pid: 1, feedTime: '2023-12-18T10:20:33', star: 4, comment: '还不错，性价比高，就是包装有点简陋' },
  { fid: 103, uid: 3, pid: 2, feedTime: '2023-12-17T16:45:22', star: 5, comment: '非常满意，性能强劲，玩游戏很流畅' }
];

export default {
  name: 'AdminComments',
  data() {
    return {
      // 商品选择
      selectedProductId: '',
      ratingFilter: 'all',

      // 数据
      products: [],
      allFeedbacks: [], // 存储所有评论
      
      loading: false,
      error: null,
      selectedFeedback: null,
      isMockData: false
    }
  },
  computed: {
    // 当前选中的商品
    currentProduct() {
      if (!this.selectedProductId) return null;
      return this.products.find(p => p.pid == this.selectedProductId) || null
    },

    // 筛选后的评价
    filteredFeedbacks() {
      let result = this.allFeedbacks;

      // 1. 商品筛选
      if (this.selectedProductId) {
        result = result.filter(f => f.pid == this.selectedProductId)
      }

      // 2. 评分筛选
      if (this.ratingFilter !== 'all') {
        result = result.filter(f => f.star == this.ratingFilter)
      }
      
      return result;
    },

    // 平均评分
    averageRating() {
      if (this.filteredFeedbacks.length === 0) return 0
      const total = this.filteredFeedbacks.reduce((sum, f) => sum + (f.star || 0), 0)
      return total / this.filteredFeedbacks.length
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    // 初始化加载所有数据
    async loadData() {
      this.loading = true;
      this.error = null;
      try {
        // 1. 先加载商品列表
        await this.loadProducts();
        
        // 2. 加载所有商品的评论
        await this.loadAllFeedbacks();
        
      } catch (err) {
        console.error('加载数据失败:', err);
        this.error = '数据加载失败，请重试';
        // 兜底
        this.allFeedbacks = MOCK_FEEDBACKS;
        this.isMockData = true;
      } finally {
        this.loading = false;
      }
    },

    // 加载商品列表
    async loadProducts() {
      try {
        const response = await axios.get(apiConfig.product.getAllProducts);
        if (response.data.code === 1) {
          this.products = response.data.data.map(p => ({
            pid: p.pid || p.id,
            name: p.name || p.productName || '未命名商品'
          }));
        }
      } catch (error) {
        console.warn('加载商品列表失败:', error);
        // 使用一些默认商品防止页面完全空白
        this.products = [
          { pid: 1, name: '示例商品1' },
          { pid: 2, name: '示例商品2' }
        ];
      }
    },

    // 加载所有评论（遍历所有商品）
    async loadAllFeedbacks() {
      if (this.products.length === 0) return;

      const promises = this.products.map(product => 
        axios.get(apiConfig.feedback.getProductFeedbacks(product.pid))
          .then(res => {
            if (res.data.code === 1) {
              // 确保返回的是数组
              return Array.isArray(res.data.data) ? res.data.data : [];
            }
            return [];
          })
          .catch(err => {
            console.warn(`加载商品 ${product.pid} 评论失败:`, err);
            return [];
          })
      );

      // 并发请求所有商品的评论
      const results = await Promise.all(promises);
      
      // 合并所有结果
      let all = results.flat();
      
      // 按时间倒序排序 (最新的在前)
      all.sort((a, b) => {
        const timeA = a.feedTime ? new Date(a.feedTime).getTime() : 0;
        const timeB = b.feedTime ? new Date(b.feedTime).getTime() : 0;
        return timeB - timeA;
      });

      this.allFeedbacks = all;
      this.isMockData = false;
    },

    // 获取商品名称
    getProductName(pid) {
      const product = this.products.find(p => p.pid == pid);
      return product ? product.name : `商品ID:${pid}`;
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
    }
  }
}
</script>

<style scoped>
/* 基础样式复用 */
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
}

.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 筛选控件 */
.filter-select {
  padding: 6px 12px;
  border: 1px solid #d0d0d0;
  background: white;
  color: #333;
  font-size: 13px;
  border-radius: 2px;
  min-width: 140px;
}

.filter-select:focus {
  outline: none;
  border-color: #1a237e;
}

/* 加载状态 */
.loading-state {
  padding: 40px;
  text-align: center;
  color: #666;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1a237e;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 表格样式 */
.table-container {
  overflow-x: auto;
  min-height: 400px;
}

.product-header {
  padding: 12px 16px;
  background: #f8f9ff;
  border-bottom: 1px solid #e0e0e0;
}

.product-name {
  font-weight: 600;
  color: #1a237e;
  margin-bottom: 4px;
}

.product-stats {
  font-size: 12px;
  color: #666;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  background: #fff;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #333;
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

.product-badge {
  background: #e3f2fd;
  color: #1565c0;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
}

/* 评分星星 */
.rating-display {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-stars {
  display: flex;
  gap: 1px;
}

.star {
  color: #e0e0e0;
  font-size: 14px;
}

.star.filled {
  color: #ff9800;
}

.rating-value {
  color: #f57c00;
  font-weight: 500;
  font-size: 12px;
}

.comment-content {
  max-width: 300px;
  color: #555;
  line-height: 1.4;
}

/* 按钮 */
.action-buttons {
  display: flex;
  gap: 6px;
}

.btn-view {
  padding: 4px 10px;
  border: none;
  border-radius: 2px;
  font-size: 12px;
  cursor: pointer;
  background: #e3f2fd;
  color: #1976d2;
}

.empty-state {
  padding: 40px;
  text-align: center;
  color: #999;
}

.table-footer {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  color: #666;
  font-size: 12px;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
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
  border-radius: 2px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.modal-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.feedback-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-row {
  display: flex;
  gap: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  color: #666;
  font-size: 13px;
}

.detail-item span {
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.detail-section h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #1a237e;
  border-bottom: 1px solid #eee;
  padding-bottom: 4px;
}

.rating-large {
  display: flex;
  align-items: center;
  gap: 10px;
}

.star-large {
  font-size: 24px;
  color: #e0e0e0;
}

.star-large.filled {
  color: #ff9800;
}

.rating-value-large {
  font-size: 18px;
  color: #f57c00;
  font-weight: bold;
}

.comment-content-large {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 4px;
  color: #333;
  line-height: 1.6;
}
</style>