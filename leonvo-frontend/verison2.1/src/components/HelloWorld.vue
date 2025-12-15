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
        搜索"{{ searchKeyword }}"的结果 ({{ products.length }}个商品)
      </div>

      <div v-if="products.length === 0" class="no-products">
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
            v-for="product in products"
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
                :src="product.imageUrl"
                :alt="product.name"
                class="product-image"
                v-if="product.imageUrl"
            >
            <div v-else class="image-placeholder">
              <span class="placeholder-icon">🌿</span>
              暂无图片
            </div>
          </div>

          <div class="product-info">
            <h3>{{ product.name }}</h3>
            <p class="description">{{ product.description }}</p>

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
              <div class="stock-info" v-if="product.stock">
                <span class="stock-icon">📦</span>
                库存: {{ product.stock }}件
              </div>
            </div>

            <div class="price-cart-row">
              <div class="price-section">
                <span class="price">¥{{ product.price.toLocaleString() }}</span>
                <div class="rating">
                  <span class="stars">★★★★★</span>
                  <span class="rating-value">{{ product.rating || 4.5 }}</span>
                </div>
              </div>
              <button class="cart-button" @click.stop="addToCart(product)">
                <span class="cart-icon">🛒</span>
                <span class="cart-text">添加到购物车</span>
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

export default {
  name: 'ProductList',
  data() {
    return {
      products: [],
      loading: true,
      searchKeyword: '',
      isSearching: false,
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
  mounted() {
    this.fetchAllProducts();
    this.startAutoSlide();
  },
  beforeUnmount() {
    this.stopAutoSlide();
  },
  methods: {
    // 获取所有商品
    async fetchAllProducts() {
      this.loading = true;
      try {
        console.log('正在请求商品数据...');
        const response = await axios.get('http://localhost:8081/api/products');
        console.log('商品数据获取成功:', response.data);

        this.products = response.data.map(product => ({
          ...product,
          brand: product.brand || '联想',
          rating: product.rating || 4.5,
          stock: product.stock || 100
        }));
        this.isSearching = false;

      } catch (error) {
        console.error('获取商品失败:', error);
        alert('获取商品数据失败，请检查：\n1. 后端服务是否启动 (http://localhost:8081)\n2. 网络连接是否正常\n3. 数据库是否连接成功');
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

      this.loading = true;
      this.isSearching = true;

      try {
        console.log('开始搜索:', this.searchKeyword);

        const response = await axios.get('http://localhost:8081/api/products/search', {
          params: {
            keyword: this.searchKeyword
          }
        });

        this.products = response.data;
        console.log('搜索结果:', this.products);

      } catch (error) {
        console.error('搜索失败:', error);
        alert('搜索失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },

    // 清空搜索
    clearSearch() {
      this.searchKeyword = '';
      this.isSearching = false;
      this.fetchAllProducts();
    },

    // 跳转到商品详情页
    goToDetail(productId) {
      console.log('跳转到商品详情:', productId);
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

      console.log('添加到购物车:', product);

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

      // 显示绿色主题的提示
      const notification = document.createElement('div');
      notification.className = 'add-to-cart-notification';
      notification.innerHTML = `
        <div class="notification-content">
          <span class="notification-icon">✅</span>
          <span>已添加 "${product.name}" 到购物车</span>
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

      window.dispatchEvent(new CustomEvent('cart-updated'));
    },

    // 轮播图相关方法
    startAutoSlide() {
      this.autoSlideInterval = setInterval(() => {
        this.nextSlide();
      }, 5000); // 每5秒自动切换
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
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 搜索框部分 - 绿色主题 */
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

/* 活动轮播样式 */
.activity-carousel {
  margin: 30px auto 40px;
  max-width: 1400px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(27, 94, 32, 0.15);
  border: 2px solid #c8e6c9;
  overflow: hidden;
  position: relative;
}

.activity-carousel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4caf50, #81c784, #4caf50);
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
  border-radius: 50%;
  background: #c8e6c9;
  cursor: pointer;
  transition: all 0.3s ease;
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
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.carousel-btn:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.carousel-container {
  position: relative;
  overflow: hidden;
  border-radius: 15px;
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
  border-radius: 15px;
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
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 20px;
  align-self: flex-start;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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
  border-radius: 12px;
  border: 1px solid #c8e6c9;
  transition: all 0.3s ease;
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
  border-radius: 25px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
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
  border-radius: 15px;
  overflow: hidden;
  position: relative;
}

.slide-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  animation: shine 3s infinite;
}

.image-icon {
  font-size: 120px;
  z-index: 1;
}

@keyframes shine {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
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
  border-radius: 15px;
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
  border-radius: 20px;
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
  border-radius: 25px;
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

/* 商品列表布局 - 每行5个 */
.products {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 25px;
  margin-top: 30px;
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
  height: 450px;
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
  height: 200px;
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

.image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
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
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-info h3 {
  margin: 0 0 12px 0;
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
  height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.product-meta {
  margin-bottom: 15px;
}

.company-info {
  color: #4a4a4a;
  font-size: 12px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.company, .investor {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.company-icon, .investor-icon {
  font-size: 14px;
  opacity: 0.8;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4caf50;
  font-size: 12px;
  font-weight: 600;
  background: rgba(76, 175, 80, 0.1);
  padding: 4px 10px;
  border-radius: 12px;
  display: inline-flex;
}

.stock-icon {
  font-size: 12px;
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

/* 添加到购物车通知 */
.add-to-cart-notification {
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

.add-to-cart-notification.show {
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

  .product-list {
    max-width: 768px;
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
    border-radius: 25px;
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