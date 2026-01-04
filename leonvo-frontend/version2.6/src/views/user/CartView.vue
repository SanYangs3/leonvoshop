<template>
  <div class="cart-view">
    <!-- 如果未登录，显示登录提示 -->
    <div v-if="!isLoggedIn" class="not-logged-in">
      <div class="login-required">
        <div class="lock-icon">🔒</div>
        <h2>需要登录</h2>
        <p>请先登录才能查看购物车</p>
        <div class="login-actions">
          <button @click="goToLogin" class="btn-login">
            去登录
          </button>
          <button @click="goToHome" class="btn-back-home">
            返回首页
          </button>
        </div>
      </div>
    </div>

    <!-- 如果已登录，显示购物车内容 -->
    <div v-else>
      <!-- 导航 -->
      <div class="cart-nav">
        <div class="nav-left">
          <div class="user-welcome">
            <h1>🛒 我的购物车</h1>
            <p class="welcome-text">欢迎回来，{{ username }}！</p>
          </div>
        </div>

        <div class="nav-right">
          <div class="cart-actions">
            <router-link to="/" class="continue-shopping">
              <span class="continue-icon">←</span>
              继续购物
            </router-link>
            <button @click="handleLogout" class="logout-btn">
              <span class="logout-icon">↪</span>
              退出登录
            </button>
          </div>
        </div>
      </div>

      <!-- 购物车内容 -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-cart-container">
          <div class="empty-cart-illustration">
            <div class="cart-icon-wrapper">
              <div class="cart-icon">🛒</div>
              <div class="empty-indicator">
                <div class="empty-line"></div>
                <div class="empty-line"></div>
              </div>
            </div>
          </div>

          <div class="empty-cart-content">
            <h2 class="empty-title">购物车是空的</h2>
            <p class="empty-subtitle">快去挑选心仪的商品吧！</p>

            <div class="empty-actions">
              <router-link to="/" class="btn-go-shopping">
                <span class="btn-icon">🛍️</span>
                <span class="btn-text">去逛逛</span>
              </router-link>
            </div>

            <div class="empty-tips">
              <p class="tip-item">🎯 发现热门商品</p>
              <p class="tip-item">✨ 每日新品推荐</p>
              <p class="tip-item">🔥 限时优惠活动</p>
            </div>
          </div>
        </div>
      </div>

      <div v-else>
        <!-- 购物车主体内容：商品列表在左，订单汇总在右 -->
        <div class="cart-main-content">
          <!-- 左侧：商品列表 -->
          <div class="cart-left-section">
            <div class="cart-items">
              <div class="cart-header">
                <h3>商品列表 ({{ totalItems }}件)</h3>
                <button @click="clearCart" class="btn-clear">
                  清空购物车
                </button>
              </div>

              <div class="items-list">
                <div v-for="item in cartItems" :key="item.id" class="cart-item">
                  <div class="item-info">
                    <div class="item-name">{{ item.name }}</div>
                    <div class="item-price">¥{{ item.price.toLocaleString() }}</div>
                  </div>

                  <div class="item-quantity">
                    <button
                        @click="decreaseQuantity(item.id)"
                        :disabled="item.quantity <= 1"
                        class="quantity-btn"
                    >
                      -
                    </button>
                    <span class="quantity">{{ item.quantity }}</span>
                    <button
                        @click="increaseQuantity(item.id)"
                        class="quantity-btn"
                    >
                      +
                    </button>
                  </div>

                  <div class="item-total">
                    ¥{{ (item.price * item.quantity).toLocaleString() }}
                  </div>

                  <button
                      @click="removeItem(item.id)"
                      class="btn-remove"
                  >
                    ✕
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：订单汇总 -->
          <div class="cart-right-section">
            <div class="cart-summary">
              <h3>订单汇总</h3>

              <div class="summary-row">
                <span>商品总数：</span>
                <span>{{ totalItems }}件</span>
              </div>

              <div class="summary-row">
                <span>商品总额：</span>
                <span>¥{{ totalPrice.toLocaleString() }}</span>
              </div>

              <div class="summary-row discount">
                <span>优惠：</span>
                <span>-¥0</span>
              </div>

              <div class="summary-row total">
                <span>应付总额：</span>
                <span class="total-price">¥{{ totalPrice.toLocaleString() }}</span>
              </div>

              <button @click="checkout" class="btn-checkout">
                去结算
              </button>

              <p class="cart-tips">
                *购物车数据保存在浏览器本地，用户重新登录后可恢复
              </p>

              <div class="user-info-footer">
                <p>当前用户：<strong>{{ username }}</strong></p>
                <p>登录状态：<span class="status-online">● 在线</span></p>
              </div>
            </div>
          </div>
        </div>

        <!-- 下方：相似商品推荐栏（绿色系） -->
        <div class="recommendations-section">
          <div class="recommendations-container">
            <div class="recommendations-header">
              <h3>✨ 相似商品推荐</h3>
              <p class="recommendations-subtitle">根据您的购物车为您推荐</p>
            </div>

            <div class="recommendations-grid">
              <div v-for="product in recommendedProducts" :key="product.id" class="recommended-product">
                <div class="product-card">
                  <div class="product-image-placeholder">
                    <div class="product-icon">💻</div>
                    <div class="product-badge">推荐</div>
                  </div>
                  <div class="product-info">
                    <h4 class="product-name">{{ product.name }}</h4>
                    <div class="product-price">¥{{ product.price.toLocaleString() }}</div>
                    <div class="product-specs">
                      <span class="spec-tag">{{ product.tag1 || '联想' }}</span>
                      <span class="spec-tag">{{ product.tag2 || '高性能' }}</span>
                    </div>
                    <button
                        @click="addToCart(product)"
                        class="btn-add-to-cart"
                        :class="{ 'added-to-cart': product.added }"
                    >
                      <span v-if="product.added" class="btn-icon">✓</span>
                      <span v-else class="btn-icon">+</span>
                      {{ product.added ? '已添加' : '加入购物车' }}
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="recommendations-footer">
              <p class="recommendations-tips">
                *推荐基于您的购物车商品和浏览历史
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '@/config/api.config';

export default {
  name: 'CartView',
  data() {
    return {
      cartItems: [],
      isLoggedIn: false,
      username: '',
      allProducts: [],
      recommendedProducts: []
    };
  },
  computed: {
    totalItems() {
      return this.cartItems.reduce((total, item) => total + item.quantity, 0);
    },
    totalPrice() {
      return this.cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
    }
  },
  mounted() {
    this.checkLoginStatus();
    this.loadCart();
    this.fetchAllProducts();
    window.addEventListener('storage', this.handleStorageChange);
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.handleStorageChange);
  },
  watch: {
    cartItems: {
      handler() {
        if (this.allProducts.length > 0) {
          this.generateProductRecommendations();
        }
      },
      deep: true
    }
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      this.isLoggedIn = loggedIn;
      if (loggedIn) {
        this.username = localStorage.getItem('username') || '用户';
      }
    },

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/login');
    },

    // 返回首页
    goToHome() {
      this.$router.push('/');
    },

    // 处理登出
    handleLogout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('loginMethod');
      localStorage.removeItem('rememberMe');

      // 可以保留购物车数据，这样用户重新登录后还能看到
      // localStorage.removeItem('lenovo_cart');

      this.isLoggedIn = false;
      this.username = '';
      this.cartItems = [];

      this.$router.push('/login');
    },

    // 处理本地存储变化
    handleStorageChange(event) {
      if (event.key === 'isLoggedIn') {
        this.checkLoginStatus();
        this.loadCart();
      }
    },

    // 获取所有商品
    async fetchAllProducts() {
      try {
        const response = await fetch(apiConfig.product.getAllProducts);
        const result = await response.json();

        // 根据后端数据结构调整
        if (result.code === 1 && result.data) {
          this.allProducts = result.data.map(product => ({
            ...product,
            added: false,
            id: product.pid || product.id,
            name: product.name,
            price: product.price,
            brand: product.brand || '联想'
          }));
        } else {
          console.warn('获取商品数据失败:', result.msg);
          this.allProducts = [];
        }
      } catch (error) {
        console.error('获取商品数据失败:', error);
        this.allProducts = [];
      }
    },

    // 生成商品推荐
    async generateProductRecommendations() {
      try {
        // 暂时使用前端模拟逻辑
        if (this.allProducts.length > 0) {
          // 过滤掉已经在购物车中的商品
          const cartItemIds = this.cartItems.map(item => item.id);
          const availableProducts = this.allProducts.filter(
              product => !cartItemIds.includes(product.id)
          );

          // 随机选择4个商品作为推荐
          const shuffled = [...availableProducts].sort(() => 0.5 - Math.random());
          this.recommendedProducts = shuffled.slice(0, 4).map(product => ({
            ...product,
            added: false
          }));
        }
      } catch (error) {
        console.error('生成推荐商品失败:', error);
        this.recommendedProducts = [];
      }
    },

    // 加载购物车
    loadCart() {
      const cartData = localStorage.getItem('lenovo_cart');
      this.cartItems = cartData ? JSON.parse(cartData) : [];
    },

    // 保存购物车
    saveCart() {
      localStorage.setItem('lenovo_cart', JSON.stringify(this.cartItems));
      this.updateCartBadge();
    },

    // 更新购物车角标（如果其他地方有使用）
    updateCartBadge() {
      const totalItems = this.cartItems.reduce((total, item) => total + item.quantity, 0);
      // 触发自定义事件，让其他组件知道购物车数量变化
      const event = new CustomEvent('cart-updated', { detail: { totalItems } });
      window.dispatchEvent(event);
    },

    // 增加商品数量
    increaseQuantity(productId) {
      const item = this.cartItems.find(item => item.id === productId);
      if (item) {
        item.quantity++;
        this.saveCart();
      }
    },

    // 减少商品数量
    decreaseQuantity(productId) {
      const item = this.cartItems.find(item => item.id === productId);
      if (item && item.quantity > 1) {
        item.quantity--;
        this.saveCart();
      }
    },

    // 删除商品
    removeItem(productId) {
      if (!confirm('确定要删除这个商品吗？')) return;

      this.cartItems = this.cartItems.filter(item => item.id !== productId);
      this.saveCart();
      this.generateProductRecommendations();
    },

    // 清空购物车
    clearCart() {
      if (!confirm('确定要清空购物车吗？')) return;

      this.cartItems = [];
      localStorage.setItem('lenovo_cart', JSON.stringify([]));
      this.generateProductRecommendations();
      this.updateCartBadge();
    },

    // 添加推荐商品到购物车
    addToCart(product) {
      const existingItem = this.cartItems.find(item => item.id === product.id);

      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.cartItems.push({
          id: product.id,
          name: product.name,
          price: product.price,
          brand: product.brand || '联想',
          quantity: 1
        });

        // 标记为已添加
        const productIndex = this.recommendedProducts.findIndex(p => p.id === product.id);
        if (productIndex !== -1) {
          this.recommendedProducts[productIndex].added = true;
        }
      }

      this.saveCart();
      this.showAddToCartMessage(product.name);
    },

    // 显示添加成功消息
    showAddToCartMessage(productName) {
      // 创建一个临时元素显示消息
      const message = document.createElement('div');
      message.className = 'add-to-cart-message';
      message.textContent = `已添加 ${productName} 到购物车`;
      message.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #4CAF50;
        color: white;
        padding: 12px 20px;
        border-radius: 0;
        z-index: 1000;
        animation: fadeInOut 2s ease;
      `;

      document.body.appendChild(message);

      setTimeout(() => {
        if (message.parentNode) {
          document.body.removeChild(message);
        }
      }, 2000);
    },

    // 结算
    checkout() {
      alert(`结算成功！\n\n用户：${this.username}\n商品数量：${this.totalItems}件\n总金额：¥${this.totalPrice.toLocaleString()}\n\n订单已生成，感谢您的购买！`);
    }
  }
};
</script>

<style scoped>
/* 将所有 border-radius 改为 0 */
.cart-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 70vh;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  border-radius: 0;
}

.not-logged-in {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-required {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 60px 50px;
  box-shadow: 0 20px 60px rgba(33, 150, 243, 0.15);
  border: 2px solid #bbdefb;
  text-align: center;
  max-width: 400px;
  width: 100%;
}

.lock-icon {
  font-size: 64px;
  color: #1565c0;
  margin-bottom: 20px;
}

.login-required h2 {
  color: #1565c0;
  margin-bottom: 10px;
}

.login-required p {
  color: #666;
  margin-bottom: 30px;
}

.login-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-login, .btn-back-home {
  padding: 12px 30px;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-login {
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
  color: white;
}

.btn-login:hover {
  background: linear-gradient(135deg, #0d47a1 0%, #0a3d8c 100%);
  transform: translateY(-2px);
}

.btn-back-home {
  background: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
}

.btn-back-home:hover {
  background: #e0e0e0;
}

.cart-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  box-shadow: 0 10px 30px rgba(33, 150, 243, 0.1);
  border: 2px solid #bbdefb;
}

.user-welcome h1 {
  color: #1565c0;
  margin: 0 0 5px 0;
}

.welcome-text {
  color: #666;
  margin: 0;
}

.cart-actions {
  display: flex;
  gap: 15px;
}

.continue-shopping, .logout-btn {
  padding: 10px 20px;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.continue-shopping {
  background: #f0f0f0;
  color: #333;
  border: 1px solid #ddd;
}

.continue-shopping:hover {
  background: #e0e0e0;
}

.logout-btn {
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #d32f2f 0%, #c62828 100%);
  transform: translateY(-2px);
}

.empty-cart-container {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 50px 40px;
  box-shadow: 0 20px 60px rgba(33, 150, 243, 0.15);
  border: 2px solid #bbdefb;
  text-align: center;
}

.cart-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
}

.cart-icon {
  font-size: 80px;
  opacity: 0.3;
}

.empty-indicator {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
}

.empty-line {
  position: absolute;
  background: #ccc;
  width: 60px;
  height: 4px;
  top: 50%;
  left: 50%;
}

.empty-line:first-child {
  transform: translate(-50%, -50%) rotate(45deg);
}

.empty-line:last-child {
  transform: translate(-50%, -50%) rotate(-45deg);
}

.empty-title {
  color: #1565c0;
  margin-bottom: 10px;
}

.empty-subtitle {
  color: #666;
  margin-bottom: 30px;
}

.btn-go-shopping {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
  color: white;
  padding: 14px 30px;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-go-shopping:hover {
  background: linear-gradient(135deg, #0d47a1 0%, #0a3d8c 100%);
  transform: translateY(-2px);
}

.empty-tips {
  margin-top: 30px;
  color: #666;
}

.tip-item {
  margin: 5px 0;
}

.cart-main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

.cart-left-section, .cart-right-section {
  background: white;
  border: 2px solid #bbdefb;
  padding: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.btn-clear {
  background: #f44336;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
}

.cart-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  gap: 15px;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.item-name {
  font-weight: 600;
  margin-bottom: 5px;
}

.item-price {
  color: #1565c0;
  font-weight: 600;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: #f9f9f9;
  border-radius: 0;
  cursor: pointer;
  font-size: 16px;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity {
  min-width: 30px;
  text-align: center;
  font-weight: 600;
}

.item-total {
  font-weight: 600;
  color: #1565c0;
  font-size: 18px;
}

.btn-remove {
  width: 30px;
  height: 30px;
  border: none;
  background: #f44336;
  color: white;
  border-radius: 0;
  cursor: pointer;
  font-size: 16px;
}

.cart-summary h3 {
  color: #1565c0;
  margin-top: 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  color: #1565c0;
  border-bottom: none;
}

.total-price {
  font-size: 24px;
  color: #1565c0;
}

.btn-checkout {
  width: 100%;
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 0;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  margin: 20px 0;
}

.btn-checkout:hover {
  background: linear-gradient(135deg, #0d47a1 0%, #0a3d8c 100%);
}

.cart-tips {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin: 10px 0;
}

.user-info-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
}

.status-online {
  color: #1565c0;
  font-weight: 600;
}

.recommendations-container {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(33, 150, 243, 0.1);
  border: 2px solid #bbdefb;
}

.recommendations-header {
  text-align: center;
  margin-bottom: 30px;
}

.recommendations-subtitle {
  color: #666;
  margin-top: 5px;
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border: 1px solid #e0e0e0;
  padding: 15px;
  transition: all 0.3s ease;
}

.product-card:hover {
  border-color: #1565c0;
  box-shadow: 0 5px 15px rgba(33, 150, 243, 0.1);
}

.product-image-placeholder {
  height: 120px;
  background: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  position: relative;
  border: 1px solid #e0e0e0;
}

.product-icon {
  font-size: 48px;
}

.product-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #1565c0;
  color: white;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 0;
}

.product-name {
  font-size: 16px;
  margin: 10px 0;
  height: 40px;
  overflow: hidden;
}

.product-price {
  color: #1565c0;
  font-size: 18px;
  font-weight: 600;
  margin: 10px 0;
}

.product-specs {
  display: flex;
  gap: 5px;
  margin: 10px 0;
}

.spec-tag {
  background: #e3f2fd;
  color: #1565c0;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 0;
}

.btn-add-to-cart {
  width: 100%;
  background: #1565c0;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  margin-top: 10px;
}

.btn-add-to-cart:hover {
  background: #0d47a1;
}

.btn-add-to-cart:hover {
  background: #388E3C;
}

.btn-add-to-cart.added-to-cart:hover {
  background: #555;
}

.recommendations-tips {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-main-content {
    grid-template-columns: 1fr;
  }

  .recommendations-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .cart-nav {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .cart-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .login-required {
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .recommendations-grid {
    grid-template-columns: 1fr;
  }

  .cart-view {
    padding: 10px;
  }
}
</style>

<style>
/* 全局动画样式 */
@keyframes fadeInOut {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  10% {
    opacity: 1;
    transform: translateY(0);
  }
  90% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(-10px);
  }
}
</style>