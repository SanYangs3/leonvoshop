<template>
  <div class="product-list">
    <!-- 搜索框部分 -->
    <div class="search-box">
      <div class="search-header">
        <h1>联想电脑商城</h1>
        <p class="subtitle">绿色科技 · 智慧生活</p>
      </div>
      <div class="search-container">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="请输入商品名称搜索..."
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

    <!-- 大屏活动滚动推荐 -->
    <div class="activity-carousel">
      <div class="carousel-header">
        <h2>🎉 限时活动推荐</h2>
        <div class="carousel-controls">
          <button @click="prevSlide" class="carousel-btn prev-btn">
            <span>‹</span>
          </button>
          <div class="carousel-dots">
            <span
                v-for="(activity, index) in activities"
                :key="activity.id"
                class="dot"
                :class="{ active: currentActivity === index }"
                @click="goToSlide(index)"
            ></span>
          </div>
          <button @click="nextSlide" class="carousel-btn next-btn">
            <span>›</span>
          </button>
        </div>
      </div>

      <div class="carousel-container">
        <div
            class="carousel-track"
            :style="{ transform: `translateX(-${currentActivity * 100}%)` }"
        >
          <div
              v-for="(activity, index) in activities"
              :key="activity.id"
              class="carousel-slide"
              :class="{ active: currentActivity === index }"
              @click="handleActivityClick(activity)"
          >
            <div class="slide-content">
              <div class="activity-badge" :style="{ background: activity.color }">
                {{ activity.tag }}
              </div>
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-desc">{{ activity.description }}</div>
              <div class="activity-products">
                <div class="product-item" v-for="product in activity.products" :key="product.id">
                  <span class="product-icon">{{ product.icon }}</span>
                  <span class="product-name">{{ product.name }}</span>
                </div>
              </div>
              <button
                  class="activity-btn"
                  :style="{ background: activity.color }"
                  @click.stop="handleActivityAction(activity)"
              >
                {{ activity.buttonText }}
              </button>
            </div>
            <div class="slide-image">
              <span class="image-icon">{{ activity.imageIcon }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门产品标题 -->
    <div class="section-header">
      <div class="title-container">
        <h2>热门产品</h2>
        <div class="decorative-line"></div>
      </div>
      <p class="section-subtitle">精选绿色科技产品，为您带来清新体验</p>
    </div>

    <!-- 加载中显示 -->
    <div v-if="loading" class="loading">
      <div class="spinner-container">
        <div class="spinner"></div>
        <div class="spinner-inner"></div>
      </div>
      <p>加载中...</p>
    </div>

    <!-- 商品列表 -->
    <div v-else>
      <!-- 显示搜索提示 -->
      <div v-if="isSearching" class="search-result-info">
        <span class="result-icon">📊</span>
        搜索"{{ searchKeyword }}"的结果 ({{ filteredProducts.length }}个商品)
      </div>

      <div v-if="filteredProducts.length === 0" class="no-products">
        <div class="empty-state">
          <div class="empty-icon">📦</div>
          <p v-if="isSearching">没有找到"{{ searchKeyword }}"相关的商品</p>
          <p v-else>暂无商品数据</p>
          <button @click="fetchAllProducts" class="refresh-button">
            <span class="refresh-icon">🔄</span>
            刷新页面
          </button>
        </div>
      </div>

      <div v-else class="products">
        <div
            v-for="product in filteredProducts"
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
                :src="getImageUrl(product.picture)"
                :alt="product.name"
                class="product-image"
            >
            <div v-if="!product.picture" class="image-placeholder">
              <span class="placeholder-icon">🌿</span>
              暂无图片
            </div>
          </div>

          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description || '暂无描述' }}</p>

            <div class="product-meta">
              <div class="company-info">
                <div class="company">
                  <span class="company-icon">🏭</span>
                  {{ product.brand || '南方机械研发中心' }}
                </div>
                <div class="investor">
                  <span class="investor-icon">💼</span>
                  投资集团
                </div>
              </div>
              <div class="stock-info">
                <span class="stock-icon">📦</span>
                库存: {{ product.stock || 0 }}件
              </div>
            </div>

            <div class="price-cart-row">
              <div class="price-section">
                <span class="price">¥{{ (product.price || 0).toLocaleString() }}</span>
                <div class="rating">
                  <span class="stars">{{ getStarRating(product.rating) }}</span>
                  <span class="rating-value">{{ getFiveStarRating(product.rating) }}</span>
                </div>
              </div>
              <button class="cart-button" @click.stop="addToCart(product)" :disabled="product.stock <= 0">
                <span class="cart-icon">🛒</span>
                <span class="cart-text">{{ product.stock > 0 ? '加入购物车' : '已售罄' }}</span>
              </button>
            </div>
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
  name: 'ProductList',
  data() {
    return {
      products: [],
      loading: true,
      searchKeyword: '',
      isSearching: false,
      displayCount: 15, // 只显示15个商品
      // 活动轮播数据
      activities: [
        {
          id: 1,
          title: '绿色科技节',
          description: '购买指定环保产品，享最高立减500元优惠',
          tag: '限时优惠',
          color: 'linear-gradient(135deg, #4caf50 0%, #2e7d32 100%)',
          imageIcon: '🌿',
          buttonText: '立即抢购',
          products: [
            { id: 1, name: '联想小新环保版', icon: '💻' },
            { id: 2, name: 'ThinkPad E14', icon: '⚡' },
            { id: 3, name: 'Yoga环保系列', icon: '♻️' }
          ],
          route: '/'
        },
        {
          id: 2,
          title: '新品首发',
          description: '联想Legion 2024旗舰游戏本震撼上市',
          tag: '新品上市',
          color: 'linear-gradient(135deg, #2196f3 0%, #1976d2 100%)',
          imageIcon: '🚀',
          buttonText: '查看详情',
          products: [
            { id: 4, name: 'Legion Y9000P', icon: '🎮' },
            { id: 5, name: 'RTX 4070显卡', icon: '🔥' },
            { id: 6, name: '240Hz电竞屏', icon: '⚡' }
          ],
          route: '/'
        },
        {
          id: 3,
          title: '商务精英优选',
          description: 'ThinkPad X1 Carbon商务旗舰，尊享专属服务',
          tag: '商务推荐',
          color: 'linear-gradient(135deg, #795548 0%, #5d4037 100%)',
          imageIcon: '💼',
          buttonText: '企业采购',
          products: [
            { id: 7, name: 'X1 Carbon', icon: '💼' },
            { id: 8, name: 'T系列', icon: '📊' },
            { id: 9, name: 'P系列工作站', icon: '💻' }
          ],
          route: '/'
        },
        {
          id: 4,
          title: '学生专享优惠',
          description: '凭学生证购买，享教育优惠价及赠品',
          tag: '学生特惠',
          color: 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)',
          imageIcon: '🎓',
          buttonText: '验证学生身份',
          products: [
            { id: 10, name: '小新系列', icon: '📚' },
            { id: 11, name: 'IdeaPad系列', icon: '✏️' },
            { id: 12, name: '平板电脑', icon: '📱' }
          ],
          route: '/login'
        }
      ],
      currentActivity: 0,
      autoSlideInterval: null
    };
  },
  computed: {
    filteredProducts() {
      let result = this.products;

      // 搜索过滤
      if (this.searchKeyword.trim()) {
        const keyword = this.searchKeyword.toLowerCase();
        result = result.filter(product =>
            product.name.toLowerCase().includes(keyword) ||
            (product.description && product.description.toLowerCase().includes(keyword))
        );
      }

      // 限制显示数量
      return result.slice(0, this.displayCount);
    }
  },
  mounted() {
    this.fetchAllProducts();
    this.startAutoSlide();
  },
  beforeUnmount() {
    this.stopAutoSlide();
  },
  methods: {
    // 处理图片URL
    getImageUrl(picture) {
      if (!picture) return '';
      if (picture.startsWith('http://') || picture.startsWith('https://')) {
        return picture;
      }
      // 如果是相对路径，拼接基础URL
      return `${apiConfig.baseURL}${picture.startsWith('/') ? picture : '/' + picture}`;
    },

    // 获取星星评分显示（将100分制转换为5星制）
    getStarRating(rating) {
      if (!rating) return '☆☆☆☆☆';
      const starRating = rating / 20; // 100分制转换为5分制
      const fullStars = Math.floor(starRating);
      const halfStar = starRating - fullStars >= 0.5;
      const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

      let stars = '';
      for (let i = 0; i < fullStars; i++) stars += '★';
      if (halfStar) stars += '☆';
      for (let i = 0; i < emptyStars; i++) stars += '☆';

      return stars;
    },

    // 获取5星制评分（保留1位小数）
    getFiveStarRating(rating) {
      if (!rating) return '4.5';
      return (rating / 20).toFixed(1);
    },

    async fetchAllProducts() {
      this.loading = true;
      try {
        console.log('正在请求商品数据...');
        const response = await axios.get(apiConfig.product.getAllProducts);
        console.log('商品数据获取成功:', response.data);

        const productsData = response.data.data || [];

        // 只取前15个商品
        const limitedProducts = productsData.slice(0, 15);

        this.products = limitedProducts.map(product => ({
          id: product.pid,  // 关键：将 pid 映射为 id，让跳转可以访问 product.id
          pid: product.pid,
          name: product.name,
          price: product.price,
          description: product.description,
          picture: product.picture,
          brand: product.brand,
          rating: product.rating,
          stock: product.stock,
          cpu: product.cpu,
          gpu: product.gpu,
          storage: product.storage,
          size: product.size,
          type: product.type
        }));

        this.isSearching = false;
        console.log('处理后的商品数据（显示15个）:', this.products);

      } catch (error) {
        console.error('获取商品失败:', error);
        this.showError('获取商品数据失败，请检查：\n1. 后端服务是否启动\n2. 网络连接是否正常\n3. 数据库是否连接成功');
      } finally {
        this.loading = false;
      }
    },

    // 搜索商品
    async handleSearch() {
      if (!this.searchKeyword.trim()) {
        this.fetchAllProducts();
        return;
      }

      this.isSearching = true;
    },

    // 清空搜索
    clearSearch() {
      this.searchKeyword = '';
      this.isSearching = false;
    },

    // 跳转到商品详情页
    goToDetail(productId) {
      console.log('跳转到商品详情:', productId);
      this.$router.push(`/product/${productId}`);
    },

    // 添加到购物车
    addToCart(product) {
      event.stopPropagation();

      // 检查库存
      if (product.stock <= 0) {
        this.showNotification('❌', '商品已售罄');
        return;
      }

      // 检查是否已登录
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        this.showNotification('⚠️', '请先登录才能添加到购物车！');
        setTimeout(() => {
          this.$router.push('/login');
        }, 1500);
        return;
      }

      console.log('添加到购物车:', product);

      let cart = JSON.parse(localStorage.getItem('lenovo_cart') || '[]');

      const existingItemIndex = cart.findIndex(item => item.pid === product.pid);

      if (existingItemIndex !== -1) {
        cart[existingItemIndex].quantity += 1;
      } else {
        cart.push({
          pid: product.pid,
          name: product.name,
          price: product.price,
          imageUrl: this.getImageUrl(product.picture),
          quantity: 1,
          brand: product.brand,
          stock: product.stock
        });
      }

      localStorage.setItem('lenovo_cart', JSON.stringify(cart));

      // 显示提示
      this.showNotification('✅', `已添加 "${product.name}" 到购物车`);
      window.dispatchEvent(new CustomEvent('cart-updated'));
    },

    // 显示错误
    showError(message) {
      const notification = document.createElement('div');
      notification.className = 'error-notification';
      notification.innerHTML = `
        <div class="notification-content">
          <span class="notification-icon">❌</span>
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
      }, 5000);
    },

    // 显示通知
    showNotification(icon, message) {
      const notification = document.createElement('div');
      notification.className = 'add-to-cart-notification';
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
    },

    // 轮播图相关方法
    startAutoSlide() {
      this.autoSlideInterval = setInterval(() => {
        this.nextSlide();
      }, 5000);
    },

    stopAutoSlide() {
      if (this.autoSlideInterval) {
        clearInterval(this.autoSlideInterval);
        this.autoSlideInterval = null;
      }
    },

    nextSlide() {
      this.currentActivity = (this.currentActivity + 1) % this.activities.length;
    },

    prevSlide() {
      this.currentActivity = (this.currentActivity - 1 + this.activities.length) % this.activities.length;
    },

    goToSlide(index) {
      this.currentActivity = index;
    },

    handleActivityClick(activity) {
      if (activity.route) {
        this.$router.push(activity.route);
      }
    },

    handleActivityAction(activity) {
      this.$router.push('/');
    }
  }
};
</script>

<style scoped>
.product-list {
  padding: 0;
  max-width: 1400px;
  margin: 0 auto;
}

/* 搜索框部分 */
.search-box {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 50%, #43a047 100%);
  color: white;
  padding: 40px 20px;
  margin-bottom: 30px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(27, 94, 32, 0.2);
  position: relative;
  overflow: hidden;
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

/* 活动轮播样式 */
.activity-carousel {
  margin: 30px auto 40px;
  max-width: 1400px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  padding: 30px;
  box-shadow: 0 4px 12px rgba(27, 94, 32, 0.15);
  border: 2px solid #c8e6c9;
  overflow: hidden;
  position: relative;
}

.carousel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.carousel-header h2 {
  color: #2e7d32;
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.carousel-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.carousel-dots {
  display: flex;
  gap: 10px;
}

.dot {
  width: 10px;
  height: 10px;
  background: #c8e6c9;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 50%;
}

.dot.active {
  background: #2e7d32;
  transform: scale(1.2);
}

.dot:hover {
  background: #4caf50;
}

.carousel-btn {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
  border-radius: 50%;
}

.carousel-btn:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.carousel-container {
  position: relative;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.carousel-slide {
  min-width: 100%;
  display: flex;
  gap: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #f8fdf8 0%, #e8f5e9 100%);
  border: 2px solid #c8e6c9;
  cursor: pointer;
  transition: all 0.3s ease;
}

.carousel-slide:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(27, 94, 32, 0.2);
}

.slide-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.activity-badge {
  display: inline-block;
  padding: 8px 20px;
  color: white;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 20px;
  align-self: flex-start;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.activity-title {
  font-size: 32px;
  color: #1b5e20;
  font-weight: 800;
  margin-bottom: 15px;
  line-height: 1.3;
}

.activity-desc {
  font-size: 18px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 30px;
}

.activity-products {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 30px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 20px;
  border: 1px solid #c8e6c9;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.product-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.product-icon {
  font-size: 20px;
}

.product-name {
  font-size: 14px;
  color: #2e7d32;
  font-weight: 600;
}

.activity-btn {
  align-self: flex-start;
  color: white;
  border: none;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.activity-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.slide-image {
  flex: 0 0 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  overflow: hidden;
  position: relative;
}

.image-icon {
  font-size: 120px;
  z-index: 1;
}

/* 区域标题 */
.section-header {
  text-align: center;
  margin: 40px 0;
}

.title-container {
  display: inline-block;
  position: relative;
  margin-bottom: 15px;
}

.section-header h2 {
  color: #569370;
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  padding: 0 20px;
  position: relative;
  display: inline-block;
}

.decorative-line {
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 3px;
  background: linear-gradient(90deg, transparent, #81c784, transparent);
}

.section-subtitle {
  color: #666;
  font-size: 16px;
  opacity: 0.8;
}

/* 搜索结果提示 */
.search-result-info {
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
  padding: 20px;
  margin-bottom: 25px;
  color: #2e7d32;
  font-size: 18px;
  text-align: center;
  border: 1px solid #c8e6c9;
  box-shadow: 0 4px 15px rgba(200, 230, 201, 0.3);
}

.result-icon {
  font-size: 20px;
  margin-right: 10px;
}

/* 无商品状态 */
.no-products {
  text-align: center;
  padding: 80px 20px;
}

.empty-state {
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
  padding: 40px;
  display: inline-block;
  border: 2px dashed #c8e6c9;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  display: block;
}

.no-products p {
  margin: 10px 0;
  color: #666;
  font-size: 18px;
}

.refresh-button {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 12px 25px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.refresh-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(76, 175, 80, 0.3);
}

.refresh-icon {
  font-size: 18px;
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
  animation: spin 1.5s linear infinite;
  border-radius: 50%;
}

.spinner-inner {
  position: absolute;
  top: 15px;
  left: 15px;
  width: 30px;
  height: 30px;
  border: 2px solid #c8e6c9;
  border-top: 2px solid #4caf50;
  animation: spin 1s linear infinite reverse;
  border-radius: 50%;
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

/* 商品列表布局 - 每行5个 */
.products {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-top: 30px;
}

.product-card {
  background: white;
  border: 1px solid #dcedc8;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(220, 237, 200, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 450px;
  position: relative;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.15);
  border-color: #81c784;
}

.product-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 2;
  display: flex;
  gap: 5px;
}

.tag {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 3px 10px;
  font-size: 10px;
  font-weight: 600;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
  border-radius: 2px;
}

.image-container {
  height: 200px;
  overflow: hidden;
  background: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: #f1f8e9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #81c784;
  font-size: 14px;
}

.placeholder-icon {
  font-size: 40px;
  margin-bottom: 10px;
  opacity: 0.7;
}

.product-info {
  padding: 15px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-info h3 {
  margin: 0 0 10px 0;
  color: #1b5e20;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
  height: 42px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.description {
  color: #666;
  font-size: 12px;
  line-height: 1.5;
  margin: 0 0 12px 0;
  flex: 1;
  height: 54px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.product-meta {
  margin-bottom: 12px;
}

.company-info {
  color: #4a4a4a;
  font-size: 11px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.company, .investor {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 3px;
}

.company-icon, .investor-icon {
  font-size: 12px;
  opacity: 0.8;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #4caf50;
  font-size: 11px;
  font-weight: 600;
  background: rgba(76, 175, 80, 0.1);
  padding: 3px 8px;
  display: inline-flex;
  border-radius: 2px;
}

.stock-icon {
  font-size: 11px;
}

.price-cart-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #e8f5e9;
}

.price-section {
  display: flex;
  flex-direction: column;
}

.price {
  color: #e91e63;
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 4px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stars {
  color: #ffb300;
  font-size: 12px;
  letter-spacing: 0.5px;
}

.rating-value {
  color: #666;
  font-size: 11px;
  font-weight: 600;
}

/* 修改购物车按钮，使其更小 */
.cart-button {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 6px 10px; /* 减小padding */
  cursor: pointer;
  font-size: 11px; /* 减小字体大小 */
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px; /* 减小间距 */
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
  border-radius: 3px;
  min-height: 28px; /* 设置最小高度 */
}

.cart-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.cart-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.cart-icon {
  font-size: 12px; /* 减小图标大小 */
}

.cart-text {
  font-weight: 600;
  font-size: 11px; /* 减小字体大小 */
}

/* 添加到购物车通知 */
.add-to-cart-notification,
.error-notification {
  position: fixed;
  top: 100px;
  right: 30px;
  color: white;
  padding: 12px 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
  z-index: 9999;
  transform: translateX(120%);
  transition: transform 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 4px;
}

.add-to-cart-notification {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
}

.error-notification {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
}

.add-to-cart-notification.show,
.error-notification.show {
  transform: translateX(0);
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-icon {
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .products {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1200px) {
  .products {
    grid-template-columns: repeat(3, 1fr);
  }

  .carousel-slide {
    flex-direction: column;
    gap: 30px;
    padding: 25px;
  }

  .slide-image {
    flex: 0 0 200px;
    width: 100%;
  }

  .activity-title {
    font-size: 28px;
  }

  .activity-desc {
    font-size: 16px;
  }
}

@media (max-width: 992px) {
  .products {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .products {
    grid-template-columns: 1fr;
    max-width: 400px;
    margin: 0 auto;
  }

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

  .section-header h2 {
    font-size: 26px;
  }

  .carousel-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .carousel-controls {
    justify-content: space-between;
  }

  .activity-carousel {
    padding: 20px;
    margin: 20px 15px;
  }

  .carousel-slide {
    padding: 20px;
  }

  .activity-title {
    font-size: 24px;
  }

  .activity-desc {
    font-size: 15px;
  }

  .activity-products {
    gap: 10px;
  }

  .product-item {
    padding: 8px 15px;
  }

  .activity-btn {
    padding: 12px 30px;
    font-size: 16px;
  }

  .image-icon {
    font-size: 80px;
  }
}
</style>