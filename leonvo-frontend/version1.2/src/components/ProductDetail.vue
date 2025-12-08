<template>
  <div class="product-detail">
    <!-- 返回按钮 -->
    <div class="back-button">
      <button @click="goBack" class="btn-back">
        <span class="back-icon">←</span>
        返回商品列表
      </button>
    </div>

    <!-- 商品详情 -->
    <div v-if="loading" class="loading">
      <div class="spinner-container">
        <div class="spinner"></div>
        <div class="spinner-inner"></div>
      </div>
      <p>加载商品详情中...</p>
    </div>

    <div v-else-if="product" class="detail-container">
      <!-- 商品基本信息 -->
      <div class="product-header">
        <div class="header-content">
          <h1>{{ product.name }}</h1>
          <p class="product-subtitle">绿色科技 · 匠心制造</p>
        </div>
        <div class="product-meta">
          <span class="meta-item brand">
            <span class="meta-icon">🏷️</span>
            品牌：{{ product.brand || '联想' }}
          </span>
          <span class="meta-item rating">
            <span class="meta-icon">⭐</span>
            评分：{{ product.rating || 4.5 }} ★
          </span>
          <span class="meta-item stock" :class="{ 'low-stock': product.stock < 20 }">
            <span class="meta-icon">📦</span>
            库存：{{ product.stock || 100 }}件
          </span>
        </div>
      </div>

      <div class="detail-content">
        <!-- 左侧：图片和价格 -->
        <div class="left-column">
          <div class="product-image-section">
            <div class="eco-badge">
              <span class="eco-icon">🌱</span>
              环保认证产品
            </div>
            <div class="image-container">
              <img :src="product.imageUrl" :alt="product.name" class="product-image-large" v-if="product.imageUrl" />
              <div v-else class="image-placeholder-large">
                <span class="placeholder-icon">🌿</span>
                <span>暂无图片</span>
              </div>
            </div>
            <div class="image-actions">
              <button class="btn-action" @click="viewFullImage">
                <span class="action-icon">🔍</span>
                查看大图
              </button>
              <button class="btn-action" @click="shareProduct">
                <span class="action-icon">📤</span>
                分享商品
              </button>
            </div>
          </div>

          <!-- 价格盒子 -->
          <div class="price-box">
            <div class="price-section">
              <div class="price-header">
                <span class="price-label">优惠价格</span>
                <span class="discount-tag">绿色特惠</span>
              </div>
              <div class="current-price">
                <span class="price-value">¥{{ product.price.toLocaleString() }}</span>
                <span class="original-price" v-if="product.price > 5000">
                  ¥{{ (product.price * 1.2).toLocaleString() }}
                </span>
              </div>
              <div class="price-benefits">
                <div class="benefit">
                  <span class="benefit-icon">🌿</span>
                  环保材料制造
                </div>
                <div class="benefit">
                  <span class="benefit-icon">🔋</span>
                  节能省电设计
                </div>
              </div>
            </div>

            <div class="action-buttons">
              <button class="btn-add-to-cart" @click="addToCart">
                <span class="cart-icon">🛒</span>
                加入购物车
              </button>
              <button class="btn-buy" @click="buyNow">
                <span class="buy-icon">⚡</span>
                立即购买
              </button>
              <button class="btn-favorite" @click="toggleFavorite">
                <span class="favorite-icon">{{ isFavorite ? '❤️' : '🤍' }}</span>
                收藏
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧：详细信息 -->
        <div class="right-column">
          <div class="description-section">
            <div class="section-header">
              <h3>
                <span class="section-icon">📋</span>
                商品描述
              </h3>
              <div class="section-line"></div>
            </div>
            <div class="description-content">
              <p>{{ product.description }}</p>
              <div class="green-features">
                <div class="feature-item">
                  <span class="feature-icon">♻️</span>
                  <span class="feature-text">采用可再生材料制造</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">⚡</span>
                  <span class="feature-text">能源效率等级：A++</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">📦</span>
                  <span class="feature-text">环保包装，可回收利用</span>
                </div>
              </div>
            </div>
          </div>

          <div class="specifications-section" v-if="product.specifications">
            <div class="section-header">
              <h3>
                <span class="section-icon">⚙️</span>
                详细规格
              </h3>
              <div class="section-line"></div>
            </div>
            <pre class="specs-text">{{ product.specifications }}</pre>
          </div>

          <div class="additional-info">
            <div class="section-header">
              <h3>
                <span class="section-icon">📊</span>
                其他信息
              </h3>
              <div class="section-line"></div>
            </div>
            <ul class="info-list">
              <li>
                <span class="info-icon">🔢</span>
                <span class="info-label">商品ID：</span>
                <span class="info-value">{{ product.id }}</span>
              </li>
              <li>
                <span class="info-icon">🏭</span>
                <span class="info-label">制造商：</span>
                <span class="info-value">南方机械研发中心</span>
              </li>
              <li>
                <span class="info-icon">💼</span>
                <span class="info-label">投资方：</span>
                <span class="info-value">投资集团</span>
              </li>
              <li>
                <span class="info-icon">📈</span>
                <span class="info-label">评分：</span>
                <span class="info-value rating-stars">
                  <span class="stars">★★★★★</span>
                  {{ product.rating || 4.5 }} / 5.0
                </span>
              </li>
              <li>
                <span class="info-icon">📦</span>
                <span class="info-label">库存状态：</span>
                <span :class="[
                  'info-value',
                  (product.stock || 100) > 20 ? 'stock-available' : 'stock-low'
                ]">
                  {{ (product.stock || 100) > 20 ? '✅ 库存充足' : '⚠️ 仅剩少量' }}
                </span>
              </li>
              <li>
                <span class="info-icon">🌍</span>
                <span class="info-label">环保认证：</span>
                <span class="info-value">绿色产品认证 · 节能认证 · 环保材料认证</span>
              </li>
            </ul>
          </div>

          <div class="warranty-info">
            <div class="section-header">
              <h3>
                <span class="section-icon">🛡️</span>
                保修服务
              </h3>
              <div class="section-line"></div>
            </div>
            <div class="warranty-content">
              <div class="warranty-item">
                <div class="warranty-icon">📅</div>
                <div class="warranty-details">
                  <h4>标准保修</h4>
                  <p>整机2年保修，主要部件3年保修</p>
                </div>
              </div>
              <div class="warranty-item">
                <div class="warranty-icon">🔄</div>
                <div class="warranty-details">
                  <h4>绿色回收</h4>
                  <p>提供旧设备回收服务，支持环保处理</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="not-found">
      <div class="empty-state">
        <div class="empty-icon">❌</div>
        <h2>商品不存在</h2>
        <p>抱歉，没有找到您要查看的商品。</p>
        <button @click="goBack" class="btn-back-to-list">
          <span class="back-icon">←</span>
          返回商品列表
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: null,
      loading: true,
      productId: null,
      isFavorite: false
    };
  },
  created() {
    this.productId = this.$route.params.id;
    this.fetchProductDetail();
    this.checkFavoriteStatus();
  },
  methods: {
    async fetchProductDetail() {
      this.loading = true;
      try {
        const response = await axios.get(`http://localhost:8081/api/products/${this.productId}`);
        this.product = response.data;

        // 确保图片URL是完整路径
        if (this.product.imageUrl && !this.product.imageUrl.startsWith('http')) {
          this.product.imageUrl = `http://localhost:8081${this.product.imageUrl}`;
        }
      } catch (error) {
        console.error('获取商品详情失败:', error);
        this.product = null;
      } finally {
        this.loading = false;
      }
    },

    checkFavoriteStatus() {
      const favorites = JSON.parse(localStorage.getItem('lenovo_favorites') || '[]');
      this.isFavorite = favorites.includes(this.productId);
    },

    goBack() {
      this.$router.push('/');
    },

    addToCart() {
      // 检查是否已登录
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        alert('请先登录才能添加到购物车！');
        this.$router.push('/login');
        return;
      }

      // 从本地存储获取购物车
      const cartData = localStorage.getItem('lenovo_cart');
      let cart = cartData ? JSON.parse(cartData) : [];

      // 查找是否已存在
      let found = false;
      for (let item of cart) {
        if (item.id === this.product.id) {
          item.quantity += 1;
          found = true;
          break;
        }
      }

      // 如果不存在，添加新商品
      if (!found) {
        cart.push({
          id: this.product.id,
          name: this.product.name,
          price: this.product.price,
          imageUrl: this.product.imageUrl,
          quantity: 1,
          brand: this.product.brand
        });
      }

      // 保存到本地存储
      localStorage.setItem('lenovo_cart', JSON.stringify(cart));

      // 显示绿色主题提示
      this.showNotification('🛒', `${this.product.name} 已加入购物车`);

      // 更新购物车数量
      window.dispatchEvent(new CustomEvent('cart-updated'));
    },

    buyNow() {
      // 检查是否已登录
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        alert('请先登录才能购买商品！');
        this.$router.push('/login');
        return;
      }

      this.showNotification('⚡', `即将购买 ${this.product.name}`);
    },

    toggleFavorite() {
      let favorites = JSON.parse(localStorage.getItem('lenovo_favorites') || '[]');

      if (this.isFavorite) {
        favorites = favorites.filter(id => id !== this.productId);
        this.showNotification('💔', `已取消收藏 ${this.product.name}`);
      } else {
        favorites.push(this.productId);
        this.showNotification('❤️', `已收藏 ${this.product.name}`);
      }

      localStorage.setItem('lenovo_favorites', JSON.stringify(favorites));
      this.isFavorite = !this.isFavorite;
    },

    viewFullImage() {
      if (this.product.imageUrl) {
        window.open(this.product.imageUrl, '_blank');
      } else {
        this.showNotification('⚠️', '该商品暂无大图');
      }
    },

    shareProduct() {
      const shareUrl = window.location.href;
      if (navigator.share) {
        navigator.share({
          title: this.product.name,
          text: `来看看这款绿色科技产品：${this.product.name}`,
          url: shareUrl
        });
      } else {
        navigator.clipboard.writeText(shareUrl);
        this.showNotification('📋', '链接已复制到剪贴板');
      }
    },

    showNotification(icon, message) {
      const notification = document.createElement('div');
      notification.className = 'green-notification';
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
.product-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 200px);
}

/* 返回按钮 */
.back-button {
  margin-bottom: 30px;
}

.btn-back {
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
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.btn-back:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.back-icon {
  font-size: 20px;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 100px 20px;
}

.spinner-container {
  position: relative;
  display: inline-block;
  width: 80px;
  height: 80px;
  margin-bottom: 30px;
}

.spinner {
  width: 80px;
  height: 80px;
  border: 4px solid #e8f5e9;
  border-top: 4px solid #2e7d32;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.spinner-inner {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 40px;
  height: 40px;
  border: 3px solid #c8e6c9;
  border-top: 3px solid #4caf50;
  border-radius: 50%;
  animation: spin 1s linear infinite reverse;
}

.loading p {
  color: #2e7d32;
  font-size: 20px;
  font-weight: 600;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 商品头部 */
.detail-container {
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border-radius: 25px;
  box-shadow: 0 10px 40px rgba(27, 94, 32, 0.15);
  overflow: hidden;
  border: 2px solid #c8e6c9;
}

.product-header {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%);
  color: white;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.product-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" opacity="0.1"><path fill="white" d="M50,50 Q60,30 80,50 T100,50 Q90,70 70,50 T50,50Z"/></svg>');
  background-size: 200px;
}

.header-content {
  position: relative;
  z-index: 1;
  margin-bottom: 20px;
}

.product-header h1 {
  margin: 0 0 10px 0;
  font-size: 36px;
  font-weight: 800;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
  letter-spacing: 0.5px;
}

.product-subtitle {
  font-size: 18px;
  opacity: 0.9;
  font-weight: 300;
  margin: 0;
}

.product-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

.meta-item {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  padding: 12px 25px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.meta-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

.meta-icon {
  font-size: 18px;
}

.low-stock {
  background: linear-gradient(135deg, rgba(255, 235, 59, 0.8) 0%, rgba(255, 193, 7, 0.8) 100%);
  color: #1b5e20;
}

/* 详情内容布局 */
.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 40px;
}

@media (max-width: 1100px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}

/* 左侧列 */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 图片区域 */
.product-image-section {
  position: relative;
}

.eco-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 2;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
  backdrop-filter: blur(10px);
}

.eco-icon {
  font-size: 16px;
}

.image-container {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  overflow: hidden;
  border: 2px solid #c8e6c9;
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
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

.product-image-large {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
  transition: transform 0.6s ease;
}

.image-container:hover .product-image-large {
  transform: scale(1.05);
}

.image-placeholder-large {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #81c784;
  font-size: 18px;
  font-weight: 500;
}

.placeholder-icon {
  font-size: 60px;
  margin-bottom: 15px;
  opacity: 0.7;
}

.image-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.btn-action {
  flex: 1;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 14px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  position: relative;
  overflow: hidden;
}

.btn-action::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-action:hover::before {
  left: 100%;
}

.btn-action:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-2px);
}

.action-icon {
  font-size: 18px;
}

/* 价格盒子 - 纯绿色主题 */
.price-box {
  background: linear-gradient(135deg, #e8f5e9 0%, #f1f8e9 100%);
  padding: 30px;
  border-radius: 20px;
  border: 2px solid #c8e6c9;
  position: relative;
  overflow: hidden;
}

.price-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #1b5e20, #2e7d32, #1b5e20);
}

.price-section {
  margin-bottom: 30px;
}

.price-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.price-label {
  font-size: 18px;
  color: #1b5e20;
  font-weight: 700;
  letter-spacing: 1px;
}

.discount-tag {
  background: linear-gradient(135deg, #388e3c 0%, #2e7d32 100%);
  color: #ffffff;
  padding: 8px 18px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 3px 10px rgba(56, 142, 60, 0.3);
  animation: pricePulse 2s infinite;
}

@keyframes pricePulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(56, 142, 60, 0.7);
  }
  70% {
    transform: scale(1.02);
    box-shadow: 0 0 0 10px rgba(56, 142, 60, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(56, 142, 60, 0);
  }
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fdf8 100%);
  border-radius: 15px;
  border: 1px solid #c8e6c9;
  position: relative;
}

.current-price::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #1b5e20, #2e7d32, #4caf50, #81c784, #4caf50, #2e7d32, #1b5e20);
  background-size: 400% 400%;
  border-radius: 17px;
  z-index: -1;
  animation: gradientBorder 3s ease infinite;
  opacity: 0.5;
}

@keyframes gradientBorder {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.price-value {
  font-size: 48px;
  color: #1b5e20;
  font-weight: 900;
  text-shadow: 1px 1px 2px rgba(27, 94, 32, 0.2);
  font-family: 'Arial', sans-serif;
}

.original-price {
  font-size: 24px;
  color: #81c784;
  text-decoration: line-through;
  font-weight: 600;
  opacity: 0.8;
}

.price-benefits {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 25px;
}

.benefit {
  background: linear-gradient(135deg, #c8e6c9 0%, #a5d6a7 100%);
  padding: 16px 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #1b5e20;
  font-weight: 600;
  border: 1px solid #a5d6a7;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.benefit::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.benefit:hover::before {
  left: 100%;
}

.benefit:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 20px rgba(165, 214, 167, 0.4);
  border-color: #81c784;
}

.benefit-icon {
  font-size: 24px;
  color: #2e7d32;
  background: rgba(255, 255, 255, 0.9);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.2);
}

/* 操作按钮 - 绿色系 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 30px;
  padding-top: 25px;
  border-top: 2px solid #c8e6c9;
}

.btn-add-to-cart, .btn-buy, .btn-favorite {
  padding: 18px;
  border: none;
  border-radius: 15px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  position: relative;
  overflow: hidden;
}

.btn-add-to-cart::before, .btn-buy::before, .btn-favorite::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-add-to-cart:hover::before, .btn-buy:hover::before, .btn-favorite:hover::before {
  left: 100%;
}

.btn-add-to-cart {
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  color: white;
  box-shadow: 0 6px 25px rgba(46, 125, 50, 0.4);
}

.btn-add-to-cart:hover {
  background: linear-gradient(135deg, #388e3c 0%, #2e7d32 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 10px 30px rgba(46, 125, 50, 0.5);
}

.btn-buy {
  background: linear-gradient(135deg, #43a047 0%, #2e7d32 100%);
  color: white;
  box-shadow: 0 6px 25px rgba(67, 160, 71, 0.4);
}

.btn-buy:hover {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.5);
}

.btn-favorite {
  background: linear-gradient(135deg, #66bb6a 0%, #43a047 100%);
  color: white;
  box-shadow: 0 6px 25px rgba(102, 187, 106, 0.4);
}

.btn-favorite:hover {
  background: linear-gradient(135deg, #81c784 0%, #4caf50 100%);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 10px 30px rgba(102, 187, 106, 0.5);
}

.cart-icon, .buy-icon, .favorite-icon {
  font-size: 24px;
  filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.1));
}

.btn-add-to-cart span:last-child,
.btn-buy span:last-child,
.btn-favorite span:last-child {
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

/* 右侧列 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 通用区块样式 */
.description-section, .specifications-section, .additional-info, .warranty-info {
  background: white;
  padding: 30px;
  border-radius: 20px;
  border: 2px solid #e8f5e9;
  box-shadow: 0 6px 25px rgba(200, 230, 201, 0.2);
}

.section-header {
  margin-bottom: 25px;
  position: relative;
}

.section-header h3 {
  color: #1b5e20;
  margin: 0 0 15px 0;
  padding-bottom: 12px;
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  font-size: 28px;
}

.section-line {
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #4caf50, #81c784);
  border-radius: 2px;
}

/* 描述内容 */
.description-content p {
  line-height: 1.8;
  color: #444;
  font-size: 16px;
  margin-bottom: 25px;
}

.green-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.feature-item {
  background: rgba(129, 199, 132, 0.1);
  padding: 15px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid rgba(129, 199, 132, 0.3);
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 15px;
  color: #2e7d32;
  font-weight: 500;
}

/* 规格详情 */
.specs-text {
  background: #f8fdf8;
  padding: 25px;
  border-radius: 15px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  line-height: 1.7;
  white-space: pre-wrap;
  border: 1px solid #c8e6c9;
  color: #333;
  font-size: 15px;
  max-height: 300px;
  overflow-y: auto;
}

/* 其他信息 */
.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  padding: 16px 0;
  border-bottom: 1px solid #e8f5e9;
  display: flex;
  align-items: center;
  gap: 15px;
}

.info-list li:last-child {
  border-bottom: none;
}

.info-icon {
  font-size: 20px;
  color: #4caf50;
  min-width: 30px;
}

.info-label {
  color: #666;
  font-weight: 600;
  min-width: 100px;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stars {
  color: #4caf50;
  font-size: 18px;
  letter-spacing: 2px;
  opacity: 0.8;
}

.stock-available {
  color: #2e7d32;
  font-weight: 700;
}

.stock-low {
  color: #ff9800;
  font-weight: 700;
}

/* 保修信息 */
.warranty-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.warranty-item {
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
  padding: 25px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 2px solid #c8e6c9;
}

.warranty-icon {
  font-size: 40px;
  color: #2e7d32;
}

.warranty-details h4 {
  margin: 0 0 10px 0;
  color: #1b5e20;
  font-size: 18px;
  font-weight: 700;
}

.warranty-details p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

/* 商品不存在状态 */
.not-found {
  text-align: center;
  padding: 100px 20px;
}

.empty-state {
  background: linear-gradient(135deg, #f1f8e9 0%, #e8f5e9 100%);
  padding: 60px;
  border-radius: 25px;
  display: inline-block;
  border: 2px solid #c8e6c9;
  box-shadow: 0 10px 40px rgba(27, 94, 32, 0.15);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 25px;
  display: block;
  color: #f44336;
}

.not-found h2 {
  color: #d32f2f;
  margin-bottom: 15px;
  font-size: 32px;
}

.not-found p {
  color: #666;
  font-size: 18px;
  margin-bottom: 30px;
}

.btn-back-to-list {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  border: none;
  padding: 16px 35px;
  border-radius: 25px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 700;
  box-shadow: 0 6px 25px rgba(76, 175, 80, 0.4);
}

.btn-back-to-list:hover {
  background: linear-gradient(135deg, #66bb6a 0%, #388e3c 100%);
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.5);
}

/* 通知样式 */
.green-notification {
  position: fixed;
  top: 120px;
  right: 30px;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 18px 28px;
  border-radius: 15px;
  box-shadow: 0 10px 35px rgba(76, 175, 80, 0.5);
  z-index: 9999;
  transform: translateX(120%);
  transition: transform 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.green-notification.show {
  transform: translateX(0);
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.notification-icon {
  font-size: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    padding: 15px;
  }

  .detail-content {
    padding: 20px;
    gap: 25px;
  }

  .product-header {
    padding: 30px 20px;
  }

  .product-header h1 {
    font-size: 28px;
  }

  .product-meta {
    flex-direction: column;
    gap: 15px;
  }

  .image-container {
    height: 300px;
  }

  .price-value {
    font-size: 36px;
  }

  .original-price {
    font-size: 20px;
  }

  .price-benefits {
    grid-template-columns: 1fr;
  }

  .green-features {
    grid-template-columns: 1fr;
  }

  .warranty-content {
    grid-template-columns: 1fr;
  }

  .description-section, .specifications-section, .additional-info, .warranty-info {
    padding: 20px;
  }

  .btn-add-to-cart, .btn-buy, .btn-favorite {
    padding: 16px;
    font-size: 16px;
  }
}
</style>