<template>
  <div class="management-section">
    <div class="section-header">
      <h3>商品管理 <span class="simulation-badge" v-if="isMockData">模拟数据</span></h3>
      <div class="section-actions">
        <!-- 搜索框 -->
        <div class="search-box">
          <input
              type="text"
              v-model="productSearch"
              placeholder="搜索商品名称..."
              class="search-input"
          />
          <span class="search-icon">搜</span>
        </div>

        <!-- 简单的筛选按钮 -->
        <button
            class="btn-filter"
            :class="{ active: showLowStock }"
            @click="showLowStock = !showLowStock"
        >
          <span class="filter-icon">⚠️</span>
          低库存
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      正在加载商品数据...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      加载失败：{{ error }}
      <button @click="loadProducts" class="btn-retry">重试</button>
    </div>

    <div v-else class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>商品名称</th>
          <th>价格</th>
          <th>库存</th>
          <th>CPU</th>
          <th>显卡</th>
          <th>评分</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="product in filteredProducts" :key="product.pid">
          <td>{{ product.pid }}</td>
          <td>
            <div class="product-cell">
              <!-- 简单的文字图标代替图片 -->
              <div class="product-icon">💻</div>
              <div class="product-info">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-type">{{ product.type }}</div>
              </div>
            </div>
          </td>
          <td><strong>¥{{ product.price }}</strong></td>
          <td>
              <span :class="['stock-badge', getStockLevel(product.stock)]">
                {{ product.stock }}件
              </span>
          </td>
          <td>{{ product.CPU || '-' }}</td>
          <td>{{ product.GPU || '-' }}</td>
          <td>
            <div class="rating">
              <span class="rating-value">{{ product.rating }}</span>
              <span class="rating-star">★</span>
            </div>
          </td>
          <td>
            <div class="action-buttons">
              <button class="btn-edit" @click="editProduct(product)">编辑</button>
              <button class="btn-view" @click="viewProduct(product)">查看</button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 简单的底部信息 -->
    <div class="table-footer">
      <div class="pagination-info">
        共 {{ filteredProducts.length }} 件商品
        <span v-if="showLowStock" class="filter-hint">（低库存筛选）</span>
        <span v-if="isMockData" class="mock-hint">【当前为模拟数据】</span>
      </div>
    </div>

    <!-- 查看商品模态框（简化版） -->
    <div v-if="selectedProduct" class="modal-overlay" @click.self="selectedProduct = null">
      <div class="modal">
        <div class="modal-header">
          <h3>商品详情</h3>
          <button class="modal-close" @click="selectedProduct = null">×</button>
        </div>
        <div class="modal-body">
          <div class="product-detail">
            <div class="detail-section">
              <h4>{{ selectedProduct.name }}</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label>商品ID：</label>
                  <span>{{ selectedProduct.pid }}</span>
                </div>
                <div class="detail-item">
                  <label>价格：</label>
                  <span class="price">¥{{ selectedProduct.price }}</span>
                </div>
                <div class="detail-item">
                  <label>库存：</label>
                  <span :class="['stock-badge', getStockLevel(selectedProduct.stock)]">
                    {{ selectedProduct.stock }}件
                  </span>
                </div>
                <div class="detail-item">
                  <label>CPU：</label>
                  <span>{{ selectedProduct.CPU || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>显卡：</label>
                  <span>{{ selectedProduct.GPU || '-' }}</span>
                </div>
                <div class="detail-item">
                  <label>类型：</label>
                  <span>{{ selectedProduct.type }}</span>
                </div>
                <div class="detail-item">
                  <label>评分：</label>
                  <span class="rating">{{ selectedProduct.rating }} ★</span>
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
const MOCK_PRODUCTS = [
  { pid: 1, name: '联想拯救者Y9000P', price: 8999, stock: 15, CPU: 'i7-13700H', GPU: 'RTX 4060', type: '游戏本', rating: 4.8 },
  { pid: 2, name: '联想小新Pro 14', price: 5499, stock: 45, CPU: 'i5-13500H', GPU: '核显', type: '轻薄本', rating: 4.6 },
  { pid: 3, name: '联想ThinkPad X1', price: 12999, stock: 8, CPU: 'i7-1360P', GPU: '核显', type: '商务本', rating: 4.9 },
  { pid: 4, name: '联想拯救者R7000', price: 6599, stock: 3, CPU: 'Ryzen 7 5800H', GPU: 'RTX 3050 Ti', type: '游戏本', rating: 4.7 },
  { pid: 5, name: '联想Yoga Air 14s', price: 7299, stock: 22, CPU: 'i5-1340P', GPU: '核显', type: '轻薄本', rating: 4.5 },
  { pid: 6, name: '联想GeekPro 2023', price: 5299, stock: 0, CPU: 'i5-13400F', GPU: 'RTX 3060', type: '台式机', rating: 4.4 },
  { pid: 7, name: '联想天逸510S', price: 3999, stock: 12, CPU: 'i3-13100', GPU: '核显', type: '台式机', rating: 4.2 },
  { pid: 8, name: '联想ThinkBook 14+', price: 5699, stock: 18, CPU: 'i5-13500H', GPU: 'RTX 2050', type: '全能本', rating: 4.7 }
];

export default {
  name: 'AdminProducts',
  data() {
    return {
      productSearch: '',
      showLowStock: false,
      selectedProduct: null,
      loading: false,
      error: null,
      isMockData: false,

      // 商品数据 - 初始为空，从API加载
      products: []
    }
  },
  computed: {
    filteredProducts() {
      let products = this.products;

      // 1. 搜索筛选
      if (this.productSearch) {
        const search = this.productSearch.toLowerCase();
        products = products.filter(p =>
            p.name.toLowerCase().includes(search) ||
            (p.type && p.type.toLowerCase().includes(search))
        );
      }

      // 2. 低库存筛选
      if (this.showLowStock) {
        products = products.filter(p => p.stock < 10 && p.stock > 0);
      }

      return products;
    }
  },
  mounted() {
    this.loadProducts();
  },
  methods: {
    getStockLevel(stock) {
      if (stock === 0) return 'out';
      if (stock < 5) return 'low';
      if (stock < 20) return 'medium';
      return 'high';
    },

    // 加载商品数据
    async loadProducts() {
      this.loading = true;
      this.error = null;

      try {
        // 尝试从API获取数据
        const response = await axios.get(apiConfig.product.getAllProducts);

        if (response.data.code === 1) {
          // API请求成功，使用真实数据
          this.products = response.data.data.map(product => ({
            pid: product.pid || product.id,
            name: product.name || product.productName,
            price: product.price || 0,
            stock: product.stock || product.quantity || 0,
            CPU: product.CPU || product.cpu || '-',
            GPU: product.GPU || product.gpu || '-',
            type: product.type || product.category || '未分类',
            rating: product.rating || 0
          }));
          this.isMockData = false;
        } else {
          // API返回错误，使用模拟数据
          throw new Error(response.data.message || 'API返回错误');
        }
      } catch (error) {
        console.error('加载商品数据失败，使用模拟数据:', error);
        this.products = [...MOCK_PRODUCTS];
        this.isMockData = true;
        this.error = '使用模拟数据：' + error.message;
      } finally {
        this.loading = false;
      }
    },

    // 编辑商品
    editProduct(product) {
      // 由于后端没有提供商品编辑API，我们使用模拟操作
      this.selectedProduct = { ...product };
      alert(`编辑商品：${product.name}\n对应API：商品编辑接口暂未提供`);
    },

    // 查看商品详情
    viewProduct(product) {
      this.selectedProduct = { ...product };
    }
  }
}
</script>

<style scoped>
/* 使用和用户管理相同的样式体系，保持统一 */

.management-section {
  background: white;
  border: 1px solid #e0e0e0;
  margin-bottom: 20px;
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

.simulation-badge {
  background: #bbdefb;
  color: #0d47a1;
  padding: 2px 8px;
  font-size: 12px;
  margin-left: 8px;
  border-radius: 2px;
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

/* 筛选按钮 */
.btn-filter {
  padding: 8px 16px;
  border: 1px solid #d0d0d0;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-filter.active {
  background: #fff3e0;
  border-color: #ff9800;
  color: #e65100;
}

.filter-icon {
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

/* 商品单元格 */
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 2px;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 500;
  color: #333;
}

.product-type {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

/* 库存徽章 */
.stock-badge {
  padding: 4px 10px;
  font-size: 12px;
  display: inline-block;
  border-radius: 2px;
  font-weight: 500;
}

.stock-badge.high {
  background: #e8f5e8;
  color: #2e7d32;
}

.stock-badge.medium {
  background: #fff3e0;
  color: #f57c00;
}

.stock-badge.low {
  background: #ffebee;
  color: #c62828;
}

.stock-badge.out {
  background: #f5f5f5;
  color: #757575;
}

/* 评分 */
.rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating-value {
  font-weight: 600;
  color: #ff9800;
}

.rating-star {
  color: #ff9800;
  font-size: 16px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-view {
  padding: 6px 12px;
  border: none;
  font-size: 12px;
  cursor: pointer;
  border-radius: 2px;
  font-weight: 500;
}

.btn-edit {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-edit:hover {
  background: #bbdefb;
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
  background: #fafafa;
}

.pagination-info {
  color: #666;
  font-size: 13px;
}

.filter-hint {
  color: #f57c00;
  margin-left: 8px;
}

.mock-hint {
  color: #f57c00;
  margin-left: 8px;
  font-weight: 500;
  font-size: 11px;
}

/* 模态框 - 复用之前的样式 */
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

/* 商品详情 */
.product-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  color: #1a237e;
  font-size: 18px;
  font-weight: 600;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
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

.detail-item .price {
  font-weight: 600;
  color: #d32f2f;
  font-size: 16px;
}

.detail-item .rating {
  font-weight: 600;
  color: #ff9800;
}

/* 响应式 */
@media (max-width: 768px) {
  .section-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-input {
    width: 100%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .btn-edit, .btn-view {
    width: 100%;
    text-align: center;
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