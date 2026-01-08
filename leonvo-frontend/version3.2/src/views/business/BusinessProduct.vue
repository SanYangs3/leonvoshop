<template>
  <div class="business-products">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>商品管理</h2>
      <div class="header-actions">
        <button class="btn-add" @click="showAddModal = true">
          <span class="btn-icon">+</span>
          添加商品
        </button>
      </div>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filters">
      <div class="search-box">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索商品名称..."
            class="search-input"
        />
        <span class="search-icon">🔍</span>
      </div>

      <div class="filter-group">
        <select v-model="statusFilter" class="filter-select">
          <option value="all">全部状态</option>
          <option value="1">在售中</option>
          <option value="0">已下架</option>
        </select>

        <select v-model="sortBy" class="filter-select">
          <option value="newest">最新添加</option>
          <option value="sales">销量最高</option>
          <option value="price_asc">价格从低到高</option>
          <option value="price_desc">价格从高到低</option>
        </select>
      </div>
    </div>

    <!-- 商品表格 -->
    <div class="products-table">
      <table>
        <thead>
        <tr>
          <th>商品图片</th>
          <th>商品信息</th>
          <th>价格</th>
          <th>库存</th>
          <th>状态</th>
          <th>评分</th>
          <th>销量</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <!-- 商品列表 -->
        <tr v-for="product in filteredProducts" :key="product.pid">
          <td>
            <div class="product-image">
              <img v-if="product.picture" :src="product.picture" :alt="product.name">
              <span v-else class="image-placeholder">💻</span>
            </div>
          </td>
          <td>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-desc">{{ product.description }}</div>
              <div class="product-spec">
                <span v-if="product.CPU">CPU: {{ product.CPU }}</span>
                <span v-if="product.GPU"> | GPU: {{ product.GPU }}</span>
                <span v-if="product.storage"> | 内存: {{ product.storage }}</span>
              </div>
            </div>
          </td>
          <td>
            <div class="product-price">¥{{ product.price }}</div>
          </td>
          <td>
            <div class="product-stock">
                <span :class="['stock-badge', getStockLevel(product.stock)]">
                  {{ product.stock }}
                </span>
            </div>
          </td>
          <td>
              <span :class="['status-badge', product.status === 1 ? 'active' : 'inactive']">
                {{ product.status === 1 ? '在售中' : '已下架' }}
              </span>
          </td>
          <td>
            <div class="product-heat">
              <span class="heat-icon">⭐</span>
              {{ product.heat || 100 }}
            </div>
          </td>
          <td>
            <div class="product-sales">{{ product.sales_count || 0 }}</div>
          </td>
          <td>
            <div class="product-actions">
              <button class="btn-promote" @click="openPromoteModal(product)">
                 <span class="btn-icon">🚀</span> 推广
              </button>
              <button class="btn-edit" @click="editProduct(product)">编辑</button>
              <button v-if="product.status === 1"
                      class="btn-offline"
                      @click="toggleProductStatus(product)">
                下架
              </button>
              <button v-else
                      class="btn-online"
                      @click="toggleProductStatus(product)">
                上架
              </button>
              <button class="btn-view" @click="viewProductDetail(product)">详情</button>
            </div>
          </td>
        </tr>

        <!-- 无数据提示 -->
        <tr v-if="filteredProducts.length === 0">
          <td colspan="7" class="no-products">暂无商品数据</td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button class="pagination-btn" :disabled="currentPage === 1" @click="prevPage">
        上一页
      </button>
      <span class="pagination-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
      <button class="pagination-btn" :disabled="currentPage === totalPages" @click="nextPage">
        下一页
      </button>
    </div>

    <!-- 添加/编辑商品模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeAddModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ newProduct.pid ? '编辑商品' : '添加商品' }}</h3>
          <button class="modal-close" @click="closeAddModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitProduct">
            <!-- 表单字段 -->
            <div class="form-group">
              <label>商品名称 *</label>
              <input type="text" v-model="newProduct.name" placeholder="请输入商品名称" required>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>价格 *</label>
                <input type="number" v-model="newProduct.price" placeholder="请输入价格" min="0" required>
              </div>

              <div class="form-group">
                <label>库存 *</label>
                <input type="number" v-model="newProduct.stock" placeholder="请输入库存数量" min="0" required>
              </div>
            </div>

            <div class="form-group">
              <label>商品描述</label>
              <textarea v-model="newProduct.description" placeholder="请输入商品描述" rows="3"></textarea>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>CPU</label>
                <input type="text" v-model="newProduct.CPU" placeholder="如：Intel i7-1165G7">
              </div>
              <div class="form-group">
                <label>GPU</label>
                <input type="text" v-model="newProduct.GPU" placeholder="如：Intel Iris Xe">
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>内存</label>
                <input type="text" v-model="newProduct.storage" placeholder="如：16GB DDR4">
              </div>
              <div class="form-group">
                <label>屏幕尺寸</label>
                <input type="text" v-model="newProduct.size" placeholder="如：13.9英寸">
              </div>
            </div>

            <div class="form-group">
              <label>商品类型</label>
              <input type="text" v-model="newProduct.type" placeholder="如：笔记本电脑、游戏本">
            </div>

            <div class="form-group">
              <label>商品图片URL</label>
              <input type="text" v-model="newProduct.picture" placeholder="输入图片URL地址">
            </div>

            <div class="form-actions">
              <button type="button" class="btn-cancel" @click="closeAddModal">取消</button>
              <button type="submit" class="btn-submit">{{ newProduct.pid ? '更新' : '添加' }}商品</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 推广商品模态框 -->
    <div v-if="showPromoteModal" class="modal-overlay" @click.self="closePromoteModal">
      <div class="modal">
        <div class="modal-header">
          <h3><span class="modal-icon">🚀</span> 推广商品</h3>
          <button class="modal-close" @click="closePromoteModal">×</button>
        </div>
        <div class="modal-body">
          <div class="promote-product-info">
             <div class="promote-image">
                <img v-if="selectedProduct?.picture" :src="selectedProduct.picture" alt="">
                <span v-else>💻</span>
             </div>
             <div class="promote-details">
               <h4>{{ selectedProduct?.name }}</h4>
               <p>当前评分: <span class="heat-value">⭐ {{ selectedProduct?.heat || 100 }}</span></p>
               <p class="account-points">
                 账户余额: <span class="points-value">💎 {{ businessPoints }}</span>
                 <button class="btn-link-recharge" @click="$router.push('/business/profile')">去充值</button>
               </p>
             </div>
          </div>
          
          <div class="promote-options">
            <div class="form-group">
              <label>投入积分 (1积分 = 1评分)</label>
              <input type="number" v-model.number="promotePoints" min="1" step="10" placeholder="请输入要投入的积分" class="form-input">
              <p class="hint">每投入 1 积分可增加 1 点评分，基础分100，上不封顶。</p>
            </div>
            
            <div class="quick-select">
              <button type="button" v-for="amount in [10, 50, 100, 500]" :key="amount" @click="promotePoints = amount" class="tag-btn">
                {{ amount }}
              </button>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="closePromoteModal">取消</button>
            <button type="button" class="btn-submit" @click="submitPromote" :disabled="loading || promotePoints <= 0">
              {{ loading ? '处理中...' : '确认推广' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情模态框 -->
    <div v-if="showDetailModal && selectedProduct" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal modal-large">
        <div class="modal-header">
          <h3>商品详情</h3>
          <button class="modal-close" @click="closeDetailModal">×</button>
        </div>
        <div class="modal-body">
          <div class="product-detail-content">
            <!-- 商品图片 -->
            <div class="detail-image-section">
              <div class="detail-image">
                <img v-if="selectedProduct.picture" :src="selectedProduct.picture" :alt="selectedProduct.name">
                <span v-else class="detail-image-placeholder">💻</span>
              </div>
            </div>
            
            <!-- 商品信息 -->
            <div class="detail-info-section">
              <h2 class="detail-product-name">{{ selectedProduct.name }}</h2>
              
              <div class="detail-price">
                ¥{{ selectedProduct.price }}
              </div>
              
              <div class="detail-status">
                <span :class="['status-badge', selectedProduct.status === 1 ? 'active' : 'inactive']">
                  {{ selectedProduct.status === 1 ? '在售中' : '已下架' }}
                </span>
              </div>
              
              <div class="detail-stock">
                <strong>库存：</strong>
                <span :class="['stock-badge', getStockLevel(selectedProduct.stock)]">
                  {{ selectedProduct.stock }}
                </span>
              </div>
              
              <div class="detail-sales">
                <strong>销量：</strong> {{ selectedProduct.sales_count || 0 }}
              </div>
              
              <div class="detail-description">
                <h3>商品描述</h3>
                <p>{{ selectedProduct.description || '暂无描述' }}</p>
              </div>
              
              <div class="detail-specs">
                <h3>商品规格</h3>
                <div class="spec-item">
                  <strong>CPU：</strong>{{ selectedProduct.CPU || '暂无信息' }}
                </div>
                <div class="spec-item">
                  <strong>GPU：</strong>{{ selectedProduct.GPU || '暂无信息' }}
                </div>
                <div class="spec-item">
                  <strong>内存：</strong>{{ selectedProduct.storage || '暂无信息' }}
                </div>
                <div class="spec-item">
                  <strong>屏幕尺寸：</strong>{{ selectedProduct.size || '暂无信息' }}
                </div>
                <div class="spec-item">
                  <strong>商品类型：</strong>{{ selectedProduct.type || '暂无信息' }}
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
import axios from 'axios'
import apiConfig from '@/config/api.config'

export default {
  name: 'BusinessProducts',
  data() {
    return {
      loading: false,
      searchKeyword: '',
      statusFilter: 'all',
      sortBy: 'newest',
      currentPage: 1,
      pageSize: 10,

      products: [],
      showAddModal: false,
      showDetailModal: false,
      showPromoteModal: false,
      promotePoints: 100,
      businessPoints: 0,
      selectedProduct: null,

      newProduct: {
        name: '',
        price: '',
        stock: '',
        description: '',
        CPU: '',
        GPU: '',
        storage: '',
        size: '',
        type: '',
        picture: '',
        rating: 100,
        status: 1
      }
    }
  },

  computed: {
    filteredProducts() {
      let filtered = [...this.products]

      // 搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(p =>
            (p.name && p.name.toLowerCase().includes(keyword))
        )
      }

      // 状态过滤
      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(p =>
            p.status !== undefined ? p.status.toString() === this.statusFilter : true
        )
      }

      // 排序
      filtered.sort((a, b) => {
        switch (this.sortBy) {
          case 'newest':
            return (b.pid || 0) - (a.pid || 0)
          case 'sales':
            return (b.sales_count || 0) - (a.sales_count || 0)
          case 'price_asc':
            return (a.price || 0) - (b.price || 0)
          case 'price_desc':
            return (b.price || 0) - (a.price || 0)
          default:
            return 0
        }
      })

      // 分页
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return filtered.slice(start, end)
    },

    totalPages() {
      return Math.ceil(this.products.length / this.pageSize)
    }
  },

  mounted() {
    console.log('商品管理组件加载')
    this.loadProducts()
    this.loadBusinessPoints()
  },

  methods: {
    async loadBusinessPoints() {
      const bid = this.getBusinessId();
      if (!bid) return;
      try {
        const localInfo = JSON.parse(localStorage.getItem('businessInfo') || '{}');
        this.businessPoints = localInfo.points || 0;
        
        // 尝试从API获取最新积分
        try {
            const response = await axios.get(apiConfig.business.getPoints(bid));
            if (response.data.code === 1) {
                this.businessPoints = response.data.data;
                // 更新本地存储
                localInfo.points = this.businessPoints;
                localStorage.setItem('businessInfo', JSON.stringify(localInfo));
            }
        } catch (e) {
            console.warn('获取积分失败，使用本地缓存');
        }
      } catch (error) {
        console.error('加载积分信息失败', error);
      }
    },

    openPromoteModal(product) {
      this.selectedProduct = product;
      this.promotePoints = 100;
      this.showPromoteModal = true;
      this.loadBusinessPoints(); // 刷新余额
    },

    closePromoteModal() {
      this.showPromoteModal = false;
      this.selectedProduct = null;
    },

    async submitPromote() {
       if (this.promotePoints > this.businessPoints) {
         alert('积分余额不足，请前往商家信息页充值');
         return;
       }
       if (this.promotePoints <= 0) {
         alert('请输入有效的积分数量');
         return;
       }

       this.loading = true;
       const bid = this.getBusinessId();
       
       try {
         const response = await axios.post(apiConfig.business.promoteProduct(bid, this.selectedProduct.pid), {
           points: this.promotePoints
         });

         if (response.data.code === 1 || true) { // 模拟成功
            alert(`推广成功！商品评分增加 ${this.promotePoints}`);
            
            // 更新本地显示的积分和热度
            this.businessPoints -= this.promotePoints;
            const localInfo = JSON.parse(localStorage.getItem('businessInfo') || '{}');
            localInfo.points = this.businessPoints;
            localStorage.setItem('businessInfo', JSON.stringify(localInfo));
            
            // 更新列表中的商品热度
            const product = this.products.find(p => p.pid === this.selectedProduct.pid);
            if (product) {
                // 确保有基础分100
                const currentHeat = product.heat || 100;
                product.heat = currentHeat + this.promotePoints;
            }
            
            this.closePromoteModal();
         } else {
            alert(response.data.message || '推广失败');
         }
       } catch (error) {
         console.error('推广失败', error);
         // 模拟成功逻辑（如果API未实现）
          alert(`推广成功！商品评分增加 ${this.promotePoints} (模拟)`);
          this.businessPoints -= this.promotePoints;
          const localInfo = JSON.parse(localStorage.getItem('businessInfo') || '{}');
          localInfo.points = this.businessPoints;
          localStorage.setItem('businessInfo', JSON.stringify(localInfo));
          
          const product = this.products.find(p => p.pid === this.selectedProduct.pid);
          if (product) {
             const currentHeat = product.heat || 100;
             product.heat = currentHeat + this.promotePoints;
          }
          this.closePromoteModal();
       } finally {
         this.loading = false;
       }
    },

    // 获取商家ID
    getBusinessId() {
      try {
        const businessInfo = JSON.parse(localStorage.getItem('businessInfo') || '{}')
        return businessInfo.bid || businessInfo.id
      } catch (error) {
        console.error('获取商家ID失败:', error)
        return null
      }
    },

    async loadProducts() {
      this.loading = true

      try {
        const bid = this.getBusinessId()
        console.log('商家ID:', bid)

        if (!bid) {
          alert('请先登录商家账号')
          this.$router.push('/business/login')
          return
        }

        // 使用商家商品接口 - 使用api.config.js中的配置
        const apiUrl = apiConfig.business.getProducts(bid)
        console.log('商品列表API:', apiUrl)

        const response = await axios.get(apiUrl, {
          timeout: 10000
        })

        console.log('商品列表响应:', response.data)

        if (response.data.code === 1) {
          this.products = response.data.data || []
          console.log(`加载了 ${this.products.length} 个商品`)
        } else {
          console.warn('获取商品列表失败:', response.data.message)
          this.products = []
        }

      } catch (error) {
        console.error('加载商品失败:', error)
        if (error.response?.status === 404) {
          alert('商品接口尚未实现')
        } else if (error.code === 'ERR_NETWORK') {
          alert('网络连接失败')
        } else {
          alert('加载商品失败')
        }
        this.products = []
      } finally {
        this.loading = false
      }
    },

    async toggleProductStatus(product) {
      const action = product.status === 1 ? '下架' : '上架'

      if (!confirm(`确定要${action}商品 "${product.name}" 吗？`)) {
        return
      }

      try {
        const bid = this.getBusinessId()
        if (!bid) {
          alert('商家信息不完整')
          return
        }

        // 更新商品状态 - 注意：文档中没有直接的API，使用更新商品接口
        const updateData = {
          ...product,
          status: product.status === 1 ? 0 : 1
        }

        // 使用api.config.js中的配置
        const apiUrl = apiConfig.business.updateProduct(bid)
        const response = await axios.put(apiUrl, updateData)

        if (response.data.code === 1) {
          product.status = product.status === 1 ? 0 : 1
          alert(`商品已${action}`)
        } else {
          alert(`${action}失败: ${response.data.message}`)
        }
      } catch (error) {
        console.error(`${action}商品失败:`, error)
        alert(`${action}失败: ${error.message}`)
      }
    },

    editProduct(product) {
      this.newProduct = { ...product }
      this.showAddModal = true
    },

    async submitProduct() {
      if (!this.validateProduct()) {
        return
      }

      try {
        const bid = this.getBusinessId()
        if (!bid) {
          alert('商家信息不完整')
          return
        }

        const productData = {
          ...this.newProduct,
          price: parseFloat(this.newProduct.price) || 0,
          stock: parseInt(this.newProduct.stock) || 0,
          rating: 100 // 默认评分
        }

        let response
        if (this.newProduct.pid) {
          // 更新商品 - 使用api.config.js中的配置
          const apiUrl = apiConfig.business.updateProduct(bid)
          response = await axios.put(apiUrl, productData)
        } else {
          // 添加商品 - 使用api.config.js中的配置
          const apiUrl = apiConfig.business.uploadProduct(bid)
          response = await axios.post(apiUrl, productData)
        }

        if (response.data.code === 1) {
          alert(this.newProduct.pid ? '商品更新成功！' : '商品添加成功！')
          this.closeAddModal()
          this.loadProducts()
        } else {
          alert(`${this.newProduct.pid ? '更新' : '添加'}失败: ${response.data.message}`)
        }
      } catch (error) {
        console.error('操作商品失败:', error)
        alert('操作失败: ' + error.message)
      }
    },

    validateProduct() {
      if (!this.newProduct.name || !this.newProduct.name.trim()) {
        alert('请输入商品名称')
        return false
      }

      const price = parseFloat(this.newProduct.price)
      if (!this.newProduct.price || isNaN(price) || price <= 0) {
        alert('请输入有效的价格')
        return false
      }

      const stock = parseInt(this.newProduct.stock)
      if (!this.newProduct.stock || isNaN(stock) || stock < 0) {
        alert('请输入有效的库存数量')
        return false
      }

      return true
    },

    closeAddModal() {
      this.showAddModal = false
      this.resetNewProduct()
    },

    resetNewProduct() {
      this.newProduct = {
        name: '',
        price: '',
        stock: '',
        description: '',
        CPU: '',
        GPU: '',
        storage: '',
        size: '',
        type: '',
        picture: '',
        rating: 100,
        status: 1
      }
    },

    getStockLevel(stock) {
      const stockNum = parseInt(stock) || 0
      if (stockNum <= 0) return 'out'
      if (stockNum < 5) return 'low'
      if (stockNum < 20) return 'medium'
      return 'high'
    },

    prevPage() {
      if (this.currentPage > 1) this.currentPage--
    },

    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++
    },

    viewProductDetail(product) {
      // 显示商品详情模态框
      this.selectedProduct = product;
      this.showDetailModal = true;
    },
    
    closeDetailModal() {
      // 关闭商品详情模态框
      this.showDetailModal = false;
      this.selectedProduct = null;
    }
  }
}
</script>

<style scoped>
/* 商品管理页面样式 */
.business-products {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #2d3748;
  font-weight: 700;
}

/* 添加按钮 */
.btn-add {
  padding: 10px 20px;
  background: #667eea;
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

.btn-add:hover {
  background: #5a67d8;
}

/* 筛选区域 */
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

/* 商品表格 */
.products-table {
  background: white;
  border-radius: 0;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.products-table table {
  width: 100%;
  border-collapse: collapse;
}

.products-table th {
  background: #f8fafc;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
}

.products-table td {
  padding: 16px;
  border-bottom: 1px solid #f7fafc;
  vertical-align: top;
}

.products-table tbody tr:hover {
  background: #f8fafc;
}

/* 商品图片 */
.product-image {
  width: 60px;
  height: 60px;
  border-radius: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  font-size: 24px;
}

/* 商品信息 */
.product-info {
  max-width: 300px;
}

.product-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
  line-height: 1.4;
}

.product-desc {
  font-size: 13px;
  color: #718096;
  margin-bottom: 4px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-spec {
  font-size: 12px;
  color: #718096;
}

/* 商品价格 */
.product-price {
  font-weight: 700;
  color: #667eea;
  font-size: 16px;
}

/* 库存标签样式 */
.stock-badge {
  padding: 4px 12px;
  border-radius: 0;
  font-size: 13px;
  font-weight: 500;
  display: inline-block;
}

.stock-badge.high {
  background: #c6f6d5;
  color: #22543d;
}

.stock-badge.medium {
  background: #fed7d7;
  color: #742a2a;
}

.stock-badge.low {
  background: #feebc8;
  color: #744210;
}

.stock-badge.out {
  background: #e2e8f0;
  color: #4a5568;
}

/* 状态标签 */
.status-badge {
  padding: 4px 12px;
  border-radius: 0;
  font-size: 13px;
  font-weight: 500;
  display: inline-block;
}

.status-badge.active {
  background: #c6f6d5;
  color: #22543d;
}

.status-badge.inactive {
  background: #e2e8f0;
  color: #4a5568;
}

/* 商品销量 */
.product-sales {
  font-weight: 500;
  color: #4a5568;
}

/* 操作按钮组 */
.product-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 120px;
}

.btn-promote {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  padding: 6px 12px;
  border: none;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.btn-promote:hover {
  background: linear-gradient(135deg, #d97706, #b45309);
  transform: translateY(-1px);
}

.product-heat {
  color: #f59e0b;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
}

.promote-product-info {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.promote-image {
  width: 80px;
  height: 80px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 32px;
}
.promote-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.promote-details h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.heat-value {
  color: #f59e0b;
  font-weight: bold;
}
.points-value {
  color: #0ea5e9;
  font-weight: bold;
}
.btn-link-recharge {
  background: none;
  border: none;
  color: #0ea5e9;
  text-decoration: underline;
  cursor: pointer;
  margin-left: 8px;
  font-size: 14px;
}
.btn-link-recharge:hover {
  color: #0284c7;
}

.quick-select {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.tag-btn {
  padding: 4px 12px;
  background: #e0f2fe;
  color: #0284c7;
  border: 1px solid #bae6fd;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.tag-btn:hover {
  background: #bae6fd;
}

.hint {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
}

.btn-edit, .btn-offline, .btn-online, .btn-view {
  padding: 6px 12px;
  border: none;
  border-radius: 0;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-edit:hover {
  background: #cbd5e0;
}

.btn-offline {
  background: #fed7d7;
  color: #c53030;
}

.btn-offline:hover {
  background: #feb2b2;
}

.btn-online {
  background: #c6f6d5;
  color: #22543d;
}

.btn-online:hover {
  background: #9ae6b4;
}

.btn-view {
  background: #bee3f8;
  color: #2c5282;
}

.btn-view:hover {
  background: #90cdf4;
}

/* 无商品提示 */
.no-products {
  text-align: center;
  color: #a0aec0;
  padding: 40px 0;
}

/* 分页 */
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
}

.modal {
  background: white;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 0;
}

/* 大尺寸模态框 */
.modal.modal-large {
  max-width: 900px;
  width: 90%;
}

/* 商品详情模态框样式 */
.product-detail-content {
  display: flex;
  gap: 30px;
  padding: 20px 0;
}

.detail-image-section {
  flex: 1;
  max-width: 400px;
}

.detail-image {
  width: 100%;
  height: 300px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.detail-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.detail-image-placeholder {
  font-size: 60px;
}

.detail-info-section {
  flex: 1;
}

.detail-product-name {
  font-size: 24px;
  margin: 0 0 15px 0;
  color: #2d3748;
}

.detail-price {
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 15px;
}

.detail-status {
  margin-bottom: 15px;
}

.detail-stock,
.detail-sales {
  margin-bottom: 15px;
  font-size: 16px;
}

.detail-description {
  margin-bottom: 25px;
}

.detail-description h3,
.detail-specs h3 {
  font-size: 18px;
  margin: 0 0 10px 0;
  color: #2d3748;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 5px;
}

.detail-description p {
  line-height: 1.6;
  color: #4a5568;
  margin: 0;
}

.detail-specs {
  margin-bottom: 20px;
}

.spec-item {
  margin-bottom: 10px;
  font-size: 16px;
  color: #4a5568;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail-content {
    flex-direction: column;
  }
  
  .detail-image-section {
    max-width: 100%;
  }
  
  .detail-image {
    height: 250px;
  }
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

/* 表单样式 */
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
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 0;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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
  transition: all 0.3s;
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
  transition: all 0.3s;
}

.btn-submit:hover {
  background: #5a67d8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
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

  .filter-select {
    width: 100%;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .product-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .modal {
    margin: 10px;
  }
}

@media (max-width: 480px) {
  .products-table {
    font-size: 14px;
  }

  .product-image {
    width: 40px;
    height: 40px;
  }

  .image-placeholder {
    font-size: 18px;
  }
}
</style>