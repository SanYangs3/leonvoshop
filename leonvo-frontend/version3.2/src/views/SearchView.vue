<template>
  <div class="search-view">
    <!-- 搜索框部分 -->
    <div class="search-box">
      <div class="search-header">
        <h1>商品搜索</h1>
        <p class="subtitle">绿色科技 · 智能搜索</p>
      </div>
      <div class="search-container">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="请输入商品名称、型号或关键词..."
            @keyup.enter="handleSearch"
            class="search-input"
        />
        <button @click="handleSearch" class="search-button">
          <span class="search-icon">🔍</span>
          搜索
        </button>
        <button @click="clearSearch" class="clear-button" v-if="searchKeyword">
          <span class="clear-icon">×</span>
          清空
        </button>
      </div>
    </div>

    <!-- 搜索内容区域 -->
    <div class="search-content">
      <!-- 左侧分类筛选 -->
      <div class="category-sidebar">
        <div class="category-header">
          <h3>商品分类</h3>
          <div class="category-line"></div>
        </div>

        <div class="category-list">
          <div
              v-for="category in categories"
              :key="category.id"
              class="category-item"
              :class="{ active: selectedCategory === category.id }"
              @click="selectCategory(category.id)"
          >
            <span class="category-icon">{{ category.icon }}</span>
            <span class="category-name">{{ category.name }}</span>
            <span class="category-count">({{ category.count }})</span>
          </div>
        </div>

        <!-- 价格筛选 -->
        <div class="price-filter">
          <div class="filter-header">
            <h4>价格区间</h4>
            <div class="filter-line"></div>
          </div>
          <div class="price-range">
            <div
                v-for="range in priceRanges"
                :key="range.id"
                class="range-item"
                :class="{ active: selectedPriceRange === range.id }"
                @click="selectPriceRange(range.id)"
            >
              {{ range.label }}
            </div>
          </div>
        </div>

        <!-- 品牌筛选 -->
        <div class="brand-filter">
          <div class="filter-header">
            <h4>品牌选择</h4>
            <div class="filter-line"></div>
          </div>
          <div class="brand-list">
            <div
                v-for="brand in brands"
                :key="brand.id"
                class="brand-item"
                :class="{ active: selectedBrands.includes(brand.id) }"
                @click="toggleBrand(brand.id)"
            >
              <span class="brand-checkbox">
                <span class="check-icon" v-if="selectedBrands.includes(brand.id)">✓</span>
              </span>
              <span class="brand-name">{{ brand.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧商品展示 -->
      <div class="product-results">
        <!-- 搜索结果信息 -->
        <div class="results-info">
          <div class="info-left">
            <span class="results-count">
              找到 <span class="highlight">{{ filteredProducts.length }}</span> 件商品
            </span>
            <span v-if="searchKeyword" class="keyword-info">
              关键词：<span class="keyword">{{ searchKeyword }}</span>
            </span>
          </div>
          <div class="sort-options">
            <select v-model="sortBy" class="sort-select">
              <option value="default">默认排序</option>
              <option value="price_asc">价格从低到高</option>
              <option value="price_desc">价格从高到低</option>
              <option value="newest">最新上架</option>
            </select>
          </div>
        </div>

        <!-- 商品列表 -->
        <div v-if="loading" class="loading">
          <div class="spinner-container">
            <div class="spinner"></div>
            <div class="spinner-inner"></div>
          </div>
          <p>搜索中...</p>
        </div>

        <div v-else>
          <div v-if="filteredProducts.length === 0" class="no-results">
            <div class="empty-state">
              <div class="empty-icon">🔍</div>
              <h3>没有找到相关商品</h3>
              <p>请尝试其他关键词或调整筛选条件</p>
              <button @click="clearAllFilters" class="clear-filters-btn">
                <span class="clear-icon">🔄</span>
                清除所有筛选
              </button>
            </div>
          </div>

          <div v-else class="products-grid">
            <div
                v-for="product in sortedProducts"
                :key="product.id"
                class="product-card"
                @click="goToDetail(product.id)"
            >
              <div class="product-tag">
                <span class="tag">绿色科技</span>
                <span class="tag">环保认证</span>
              </div>

              <div class="image-container">
                <img
                    :src="product.imageUrl || '/placeholder-image.jpg'"
                    :alt="product.name"
                    class="product-image"
                />
              </div>

              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="description">{{ product.description || '绿色环保产品，性能卓越' }}</p>

                <div class="product-meta">
                  <div class="company-info">
                    <div class="company">
                      <span class="company-icon">🏷️</span>
                      {{ product.brand || '联想' }}
                    </div>
                    <div class="rating">
                      <span class="stars">★★★★★</span>
                      <span class="rating-value">{{ product.rating || 4.5 }}</span>
                    </div>
                  </div>
                </div>

                <div class="price-cart-row">
                  <div class="price-section">
                    <span class="price">¥{{ product.price.toLocaleString() }}</span>
                  </div>
                  <button class="cart-button" @click.stop="addToCart(product)">
                    <span class="cart-icon">🛒</span>
                    <span class="cart-text">加入购物车</span>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="filteredProducts.length > 0" class="pagination">
            <button
                @click="prevPage"
                :disabled="currentPage === 1"
                class="page-btn prev-btn"
            >
              上一页
            </button>
            <div class="page-numbers">
              <span
                  v-for="page in visiblePages"
                  :key="page"
                  class="page-number"
                  :class="{ active: page === currentPage }"
                  @click="goToPage(page)"
              >
                {{ page }}
              </span>
              <span v-if="showEllipsis" class="ellipsis">...</span>
            </div>
            <button
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="page-btn next-btn"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SearchView',
  data() {
    return {
      searchKeyword: '',
      loading: false,
      selectedCategory: null,
      selectedPriceRange: null,
      selectedBrands: [],
      sortBy: 'default',
      currentPage: 1,
      itemsPerPage: 12,

      // 分类数据
      categories: [
        { id: 1, name: '笔记本电脑', icon: '💻', count: 45 },
        { id: 2, name: '台式电脑', icon: '🖥️', count: 23 },
        { id: 3, name: '平板电脑', icon: '📱', count: 18 },
        { id: 4, name: '游戏本', icon: '🎮', count: 32 },
        { id: 5, name: '商务本', icon: '💼', count: 27 },
        { id: 6, name: '轻薄本', icon: '📘', count: 21 },
        { id: 7, name: '工作站', icon: '⚙️', count: 15 },
        { id: 8, name: '配件周边', icon: '🎧', count: 38 }
      ],

      // 价格区间
      priceRanges: [
        { id: 1, label: '3000元以下', min: 0, max: 3000 },
        { id: 2, label: '3000-5000元', min: 3000, max: 5000 },
        { id: 3, label: '5000-8000元', min: 5000, max: 8000 },
        { id: 4, label: '8000元以上', min: 8000, max: Infinity }
      ],

      // 品牌数据
      brands: [
        { id: 1, name: '联想' },
        { id: 2, name: 'ThinkPad' },
        { id: 3, name: 'IdeaPad' },
        { id: 4, name: 'Yoga' },
        { id: 5, name: 'Legion' },
        { id: 6, name: 'ThinkBook' }
      ],

      // 模拟商品数据（实际应该从后端获取）
      allProducts: []
    };
  },
  computed: {
    // 动态计算每个分类的商品数量
    categoriesWithCount() {
      return this.categories.map(category => {
        // 计算当前分类的实际商品数量
        const count = this.allProducts.filter(product => {
          // 根据商品名称判断分类
          const categoryName = category.name.replace('电脑', '');
          return product.name.includes(categoryName);
        }).length;
        return { ...category, count };
      });
    },

    // 过滤后的商品
    filteredProducts() {
      let products = this.allProducts;

      // 关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        products = products.filter(product =>
            product.name.toLowerCase().includes(keyword) ||
            (product.description && product.description.toLowerCase().includes(keyword))
        );
      }

      // 分类筛选
      if (this.selectedCategory) {
        // 这里需要根据商品分类进行筛选
        // 由于数据库结构未知，暂时按品牌模拟
        const category = this.categories.find(c => c.id === this.selectedCategory);
        if (category) {
          products = products.filter(product =>
              product.name.includes(category.name.replace('电脑', ''))
          );
        }
      }

      // 价格筛选
      if (this.selectedPriceRange) {
        const range = this.priceRanges.find(r => r.id === this.selectedPriceRange);
        if (range) {
          products = products.filter(product =>
              product.price >= range.min && product.price <= range.max
          );
        }
      }

      // 品牌筛选
      if (this.selectedBrands.length > 0) {
        products = products.filter(product =>
            this.selectedBrands.some(brandId =>
                    product.brand && product.brand.includes(
                        this.brands.find(b => b.id === brandId)?.name || ''
                    )
            )
        );
      }

      return products;
    },

    // 排序后的商品
    sortedProducts() {
      const products = [...this.filteredProducts];

      switch (this.sortBy) {
        case 'price_asc':
          return products.sort((a, b) => a.price - b.price);
        case 'price_desc':
          return products.sort((a, b) => b.price - a.price);
        case 'newest':
          // 按ID降序模拟最新上架
          return products.sort((a, b) => b.id - a.id);
        default:
          // 默认按评分降序排序
          return products.sort((a, b) => (b.rating || 0) - (a.rating || 0));
      }
    },

    // 分页相关计算
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.sortedProducts.slice(start, end);
    },

    totalPages() {
      return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
    },

    visiblePages() {
      const pages = [];
      const maxVisible = 5;

      if (this.totalPages <= maxVisible) {
        for (let i = 1; i <= this.totalPages; i++) {
          pages.push(i);
        }
      } else {
        let start = Math.max(1, this.currentPage - 2);
        let end = Math.min(this.totalPages, start + maxVisible - 1);

        if (end - start < maxVisible - 1) {
          start = Math.max(1, end - maxVisible + 1);
        }

        for (let i = start; i <= end; i++) {
          pages.push(i);
        }
      }

      return pages;
    },

    showEllipsis() {
      return this.totalPages > this.visiblePages[this.visiblePages.length - 1];
    }
  },
  mounted() {
    // 从路由参数获取搜索关键词
    if (this.$route.query.keyword) {
      this.searchKeyword = this.$route.query.keyword;
    }

    this.fetchAllProducts();
  },
  watch: {
    sortBy() {
      this.currentPage = 1; // 排序后回到第一页
    },
    searchKeyword() {
      this.currentPage = 1; // 搜索后回到第一页
    },
    selectedCategory() {
      this.currentPage = 1; // 分类筛选后回到第一页
    },
    selectedPriceRange() {
      this.currentPage = 1; // 价格筛选后回到第一页
    },
    selectedBrands() {
      this.currentPage = 1; // 品牌筛选后回到第一页
    }
  },
  methods: {
    // 从后端获取所有商品
    async fetchAllProducts() {
      this.loading = true;
      try {
        const response = await axios.get('http://localhost:8081/api/products');
        this.allProducts = response.data.map(product => ({
          ...product,
          brand: product.brand || '联想',
          rating: product.rating || 4.5,
          description: product.description || '绿色环保产品，性能卓越'
        }));
      } catch (error) {
        console.error('获取商品失败:', error);
        // 使用模拟数据
        this.generateMockProducts();
      } finally {
        this.loading = false;
      }
    },

    // 生成模拟商品数据（备用）
    generateMockProducts() {
      const mockProducts = [];
      const brands = ['联想', 'ThinkPad', 'IdeaPad', 'Yoga', 'Legion', 'ThinkBook'];
      const categories = ['笔记本电脑', '游戏本', '商务本', '轻薄本', '工作站'];

      for (let i = 1; i <= 50; i++) {
        mockProducts.push({
          id: i,
          name: `${brands[i % brands.length]} ${categories[i % categories.length]} ${i}代`,
          description: '绿色环保设计，高性能配置，适合各种使用场景',
          price: Math.floor(Math.random() * 10000) + 2000,
          brand: brands[i % brands.length],
          rating: 4 + Math.random(),
          imageUrl: `https://picsum.photos/300/200?random=${i}`
        });
      }

      this.allProducts = mockProducts;
    },

    // 处理搜索
    handleSearch() {
      if (!this.searchKeyword.trim()) {
        return;
      }
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
      }, 500);
    },

    // 清空搜索
    clearSearch() {
      this.searchKeyword = '';
      this.selectedCategory = null;
      this.selectedPriceRange = null;
      this.selectedBrands = [];
      this.sortBy = 'default';
    },

    // 清空所有筛选
    clearAllFilters() {
      this.selectedCategory = null;
      this.selectedPriceRange = null;
      this.selectedBrands = [];
      this.sortBy = 'default';
    },

    // 选择分类
    selectCategory(categoryId) {
      this.selectedCategory = this.selectedCategory === categoryId ? null : categoryId;
    },

    // 选择价格区间
    selectPriceRange(rangeId) {
      this.selectedPriceRange = this.selectedPriceRange === rangeId ? null : rangeId;
    },

    // 切换品牌选择
    toggleBrand(brandId) {
      const index = this.selectedBrands.indexOf(brandId);
      if (index > -1) {
        this.selectedBrands.splice(index, 1);
      } else {
        this.selectedBrands.push(brandId);
      }
    },

    // 分页方法
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    goToPage(page) {
      this.currentPage = page;
    },

    // 跳转到商品详情
    goToDetail(productId) {
      this.$router.push(`/product/${productId}`);
    },

    // 添加到购物车
    addToCart(product) {
      event.stopPropagation();

      // 检查是否已登录
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        alert('请先登录才能添加到购物车！');
        this.$router.push('/login');
        return;
      }

      let cart = JSON.parse(localStorage.getItem('lenovo_cart') || '[]');
      const existingItemIndex = cart.findIndex(item => item.id === product.id);

      if (existingItemIndex !== -1) {
        cart[existingItemIndex].quantity += 1;
      } else {
        cart.push({
          id: product.id,
          name: product.name,
          price: product.price,
          imageUrl: product.imageUrl,
          quantity: 1,
          brand: product.brand
        });
      }

      localStorage.setItem('lenovo_cart', JSON.stringify(cart));

      // 显示添加成功提示
      this.showNotification('✅', `${product.name} 已加入购物车`);

      window.dispatchEvent(new CustomEvent('cart-updated'));
    },

    // 显示通知
    showNotification(icon, message) {
      const notification = document.createElement('div');
      notification.className = 'search-notification';
      notification.innerHTML = `
        <div class="notification-content">
          <span class="notification-icon">${icon}</span>
          <span>${message}</span>
        </div>
      `;
      document.body.appendChild(notification);

      setTimeout(() => {
        notification.classList.add('show');
      }, 10);

      setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
          document.body.removeChild(notification);
        }, 300);
      }, 3000);
    }
  }
};
</script>

<style scoped>
.search-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 200px);
}

/* 搜索框部分 */
.search-box {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 50%, #43a047 100%);
  color: white;
  padding: 40px 20px;
  border-radius: 20px;
  margin-bottom: 30px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(27, 94, 32, 0.2);
  position: relative;
  overflow: hidden;
}

.search-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" opacity="0.1"><path fill="white" d="M50,50 Q60,30 80,50 T100,50 Q90,70 70,50 T50,50Z"/></svg>');
  background-size: 200px;
}

.search-header h1 {
  margin: 0 0 10px 0;
  font-size: 36px;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
  letter-spacing: 1px;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 30px;
  font-weight: 300;
}

.search-container {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  gap: 15px;
}

.search-input {
  flex: 1;
  padding: 16px 25px;
  border: none;
  border-radius: 50px;
  font-size: 16px;
  outline: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  background: rgba(255,255,255,0.95);
  border: 2px solid transparent;
}

.search-input:focus {
  border-color: #81c784;
  box-shadow: 0 4px 25px rgba(129, 199, 132, 0.4);
  transform: translateY(-2px);
}

.search-button {
  background: linear-gradient(135deg, rgba(129, 199, 132, 0.4) 0%, rgb(86, 147, 112) 100%);
  color: white;
  border: none;
  padding: 16px 30px;
  border-radius: 50px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 20px rgba(129, 199, 132, 0.4);
}

.search-button:hover {
  background: linear-gradient(135deg, rgba(129, 199, 132, 0.4) 0%, #569370 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(129, 199, 132, 0.4);
}

.search-icon {
  font-size: 18px;
}

.clear-button {
  background: linear-gradient(135deg, #78909c 0%, #546e7a 100%);
  color: white;
  border: none;
  padding: 16px 25px;
  border-radius: 50px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-button:hover {
  background: linear-gradient(135deg, #90a4ae 0%, #607d8b 100%);
  transform: translateY(-2px);
}

.clear-icon {
  font-size: 20px;
  font-weight: bold;
}

/* 搜索内容区域 */
.search-content {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

/* 左侧分类栏 */
.category-sidebar {
  width: 280px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 8px 30px rgba(27, 94, 32, 0.1);
  border: 2px solid #c8e6c9;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.category-header {
  margin-bottom: 25px;
  position: relative;
}

.category-header h3 {
  color: #1b5e20;
  margin: 0 0 15px 0;
  font-size: 22px;
  font-weight: 700;
  padding-bottom: 10px;
}

.category-line {
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #4caf50, #81c784);
  border-radius: 2px;
}

.category-list {
  margin-bottom: 30px;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 14px 18px;
  margin-bottom: 10px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  background: #f1f8e9;
}

.category-item:hover {
  background: #e8f5e9;
  transform: translateX(5px);
  border-color: #c8e6c9;
}

.category-item.active {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border-color: #2e7d32;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.category-item.active .category-icon,
.category-item.active .category-name,
.category-item.active .category-count {
  color: white;
}

.category-icon {
  font-size: 20px;
  margin-right: 12px;
  color: #4caf50;
}

.category-name {
  flex: 1;
  font-weight: 600;
  color: #333;
}

.category-count {
  font-size: 13px;
  color: #81c784;
  font-weight: 600;
}

/* 筛选器 */
.filter-header {
  margin-bottom: 20px;
}

.filter-header h4 {
  color: #1b5e20;
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
}

.filter-line {
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, #81c784, #c8e6c9);
  border-radius: 2px;
}

.price-filter,
.brand-filter {
  margin-bottom: 30px;
}

.price-range {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.range-item {
  padding: 12px 16px;
  border-radius: 10px;
  background: #f8fdf8;
  border: 2px solid #c8e6c9;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  text-align: center;
}

.range-item:hover {
  background: #e8f5e9;
  transform: translateX(3px);
}

.range-item.active {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border-color: #2e7d32;
  box-shadow: 0 3px 10px rgba(76, 175, 80, 0.2);
}

.brand-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.brand-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8fdf8;
  border: 2px solid transparent;
}

.brand-item:hover {
  background: #e8f5e9;
  transform: translateX(3px);
}

.brand-item.active {
  background: #e8f5e9;
  border-color: #81c784;
}

.brand-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #81c784;
  border-radius: 4px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.brand-item.active .brand-checkbox {
  background: #4caf50;
  border-color: #4caf50;
}

.check-icon {
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.brand-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 右侧商品结果区域 */
.product-results {
  flex: 1;
  min-width: 0; /* 防止flex元素溢出 */
}

.results-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border-radius: 15px;
  margin-bottom: 25px;
  border: 2px solid #c8e6c9;
  box-shadow: 0 5px 20px rgba(27, 94, 32, 0.1);
}

.info-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.results-count {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.highlight {
  color: #4caf50;
  font-weight: 800;
  font-size: 24px;
}

.keyword-info {
  background: rgba(76, 175, 80, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  color: #2e7d32;
}

.keyword {
  font-weight: 700;
  color: #1b5e20;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-select {
  padding: 10px 20px;
  border: 2px solid #c8e6c9;
  border-radius: 25px;
  background: white;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
}

.sort-select:focus {
  border-color: #4caf50;
  box-shadow: 0 0 0 3px rgba(76, 175, 80, 0.1);
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.product-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border: 1px solid #dcedc8;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(220, 237, 200, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 380px;
  position: relative;
}

.product-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 15px 35px rgba(76, 175, 80, 0.15);
  border-color: #81c784;
}

.product-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
  display: flex;
  gap: 8px;
}

.tag {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.image-container {
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

.image-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4caf50, #81c784, #4caf50);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.6s ease;
}

.product-card:hover .product-image {
  transform: scale(1.1);
}

.product-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-info h3 {
  margin: 0 0 10px 0;
  color: #1b5e20;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.description {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  margin: 0 0 15px 0;
  flex: 1;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  margin-bottom: 15px;
}

.company-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.company {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4a4a4a;
  font-size: 13px;
  font-weight: 500;
}

.company-icon {
  font-size: 14px;
  opacity: 0.8;
}

.rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stars {
  color: #ffb300;
  font-size: 14px;
  letter-spacing: 1px;
}

.rating-value {
  color: #666;
  font-size: 12px;
  font-weight: 600;
}

.price-cart-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px solid #e8f5e9;
}

.price-section {
  display: flex;
  flex-direction: column;
}

.price {
  color: #e91e63;
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 4px;
}

.cart-button {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.cart-button:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.cart-icon {
  font-size: 16px;
}

.cart-text {
  font-weight: 600;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 80px 20px;
}

.spinner-container {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 60px;
  margin-bottom: 20px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 3px solid #e0f2f1;
  border-top: 3px solid #1b5e20;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.spinner-inner {
  position: absolute;
  top: 15px;
  left: 15px;
  width: 30px;
  height: 30px;
  border: 2px solid #c8e6c9;
  border-top: 2px solid #4caf50;
  border-radius: 50%;
  animation: spin 1s linear infinite reverse;
}

.loading p {
  color: #2e7d32;
  font-size: 18px;
  font-weight: 500;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 无结果状态 */
.no-results {
  text-align: center;
  padding: 60px 20px;
}

.empty-state {
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
  padding: 40px;
  border-radius: 20px;
  display: inline-block;
  border: 2px dashed #c8e6c9;
  max-width: 500px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  color: #81c784;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.no-results h3 {
  color: #2e7d32;
  margin-bottom: 15px;
  font-size: 24px;
}

.no-results p {
  color: #666;
  margin-bottom: 25px;
  font-size: 16px;
}

.clear-filters-btn {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.clear-filters-btn:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  padding: 30px 0;
  border-top: 1px solid #e8f5e9;
}

.page-btn {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  min-width: 80px;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.page-btn:disabled {
  background: #cccccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #f1f8e9;
  color: #333;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.page-number:hover {
  background: #e8f5e9;
  transform: translateY(-2px);
  border-color: #c8e6c9;
}

.page-number.active {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.ellipsis {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #81c784;
  font-weight: bold;
}

/* 通知样式 */
.search-notification {
  position: fixed;
  top: 100px;
  right: 30px;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 15px 25px;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(76, 175, 80, 0.4);
  z-index: 9999;
  transform: translateX(120%);
  transition: transform 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
}

.search-notification.show {
  transform: translateX(0);
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-icon {
  font-size: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 992px) {
  .search-content {
    flex-direction: column;
  }

  .category-sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .category-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }

  .category-item {
    margin-bottom: 0;
  }
}

@media (max-width: 768px) {
  .search-container {
    flex-direction: column;
  }

  .search-input,
  .search-button,
  .clear-button {
    width: 100%;
  }

  .search-header h1 {
    font-size: 28px;
  }

  .results-info {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .info-left {
    flex-direction: column;
    gap: 10px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }

  .pagination {
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .search-view {
    padding: 10px;
  }

  .search-box {
    padding: 30px 15px;
  }

  .category-sidebar {
    padding: 20px 15px;
  }

  .products-grid {
    grid-template-columns: 1fr;
  }

  .page-numbers {
    display: none;
  }
}
</style>