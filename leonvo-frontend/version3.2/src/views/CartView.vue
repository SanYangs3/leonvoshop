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
// 导入推荐算法
import { generateRecommendations } from '@/utils/recommodation.js';

export default {
  name: 'CartView',
  data() {
    return {
      cartItems: [],
      isLoggedIn: false,
      username: '',
      allProducts: [], // 存储所有商品数据
      // 相似商品推荐数据 - 将使用推荐算法生成
      recommendedProducts: []
    };
  },
  computed: {
    totalItems() {
      let total = 0;
      for (let item of this.cartItems) {
        total += item.quantity;
      }
      return total;
    },
    totalPrice() {
      let total = 0;
      for (let item of this.cartItems) {
        total += item.price * item.quantity;
      }
      return total;
    }
  },
  mounted() {
    this.checkLogin();
    this.loadCart();
    this.fetchAllProducts(); // 获取所有商品用于推荐
    // 监听其他页面可能发送的清空事件
    window.addEventListener('storage', this.handleStorageChange);
  },
  beforeUnmount() {
    // 移除事件监听
    window.removeEventListener('storage', this.handleStorageChange);
  },
  watch: {
    cartItems: {
      handler() {
        // 当购物车商品变化时，重新生成推荐
        if (this.allProducts.length > 0) {
          this.generateProductRecommendations();
        }
      },
      deep: true
    },
    allProducts: {
      handler() {
        // 当所有商品数据加载完成后，生成推荐
        if (this.allProducts.length > 0) {
          this.generateProductRecommendations();
        }
      }
    }
  },
  methods: {
    // 获取所有商品
    async fetchAllProducts() {
      try {
        const response = await fetch('http://localhost:8081/api/products');
        const products = await response.json();
        this.allProducts = products.map(product => ({
          ...product,
          added: false // 初始化添加状态
        }));
      } catch (error) {
        console.error('获取商品数据失败:', error);
        // 如果没有后端数据，使用默认数据
        this.allProducts = this.getDefaultProducts();
      }
    },

    // 获取默认商品数据（备用）
    getDefaultProducts() {
      return [
        {
          id: 1001,
          name: '联想Yoga Air 14s',
          price: 6999,
          brand: '联想',
          tag1: '轻薄本',
          tag2: 'OLED触屏',
          added: false
        },
        {
          id: 1002,
          name: '联想拯救者R7000',
          price: 6499,
          brand: '联想',
          tag1: '游戏本',
          tag2: 'RTX4060',
          added: false
        },
        {
          id: 1003,
          name: '联想ThinkBook 16+',
          price: 5799,
          brand: '联想',
          tag1: '商务本',
          tag2: '大屏办公',
          added: false
        },
        {
          id: 1004,
          name: '联想小新Pro 14',
          price: 5499,
          brand: '联想',
          tag1: '性能本',
          tag2: '2.8K屏',
          added: false
        },
        {
          id: 1005,
          name: '联想Legion Y9000P',
          price: 8999,
          brand: '联想',
          tag1: '旗舰游戏本',
          tag2: 'RTX4070',
          added: false
        },
        {
          id: 1006,
          name: '联想ThinkPad X1 Carbon',
          price: 12999,
          brand: '联想',
          tag1: '商务旗舰',
          tag2: '超轻薄',
          added: false
        }
      ];
    },

    // 生成商品推荐
    generateProductRecommendations() {
      if (this.cartItems.length === 0) {
        // 如果购物车为空，显示热门商品
        this.recommendedProducts = this.allProducts
            .slice(0, 4)
            .map(product => ({
              ...product,
              added: false
            }));
        return;
      }

      // 使用推荐算法生成推荐商品
      const recommendations = generateRecommendations(
          this.cartItems,
          this.allProducts,
          {
            maxRecommendations: 4,
            minSimilarityScore: 40
          }
      );

      // 标记推荐商品是否已在购物车中
      this.recommendedProducts = recommendations.map(product => ({
        ...product,
        added: this.cartItems.some(item => item.id === product.id)
      }));

      // 如果推荐数量不足，补充热门商品
      if (this.recommendedProducts.length < 4) {
        const recommendedIds = new Set(this.recommendedProducts.map(p => p.id));
        const additionalProducts = this.allProducts
            .filter(product => !recommendedIds.has(product.id))
            .slice(0, 4 - this.recommendedProducts.length)
            .map(product => ({
              ...product,
              added: false
            }));
        this.recommendedProducts = [...this.recommendedProducts, ...additionalProducts];
      }
    },

    // 处理storage变化（用于跨页面同步）
    handleStorageChange(event) {
      if (event.key === 'lenovo_cart_clear_all') {
        this.cartItems = [];
        this.saveCart();
        this.generateProductRecommendations();
      }
      // 如果登录状态改变
      if (event.key === 'isLoggedIn') {
        if (event.newValue === 'true') {
          // 用户登录，重新加载购物车
          setTimeout(() => {
            this.checkLogin();
            this.loadCart();
          }, 100);
        } else if (event.newValue === 'false') {
          // 用户退出登录，清除显示但不删除数据
          this.isLoggedIn = false;
          this.username = '';
          this.cartItems = [];
        }
      }
    },

    // 检查登录状态
    checkLogin() {
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      const user = localStorage.getItem('username');

      this.isLoggedIn = loggedIn;
      this.username = user || '';

      // 如果未登录，清除显示但不删除购物车数据
      if (!loggedIn) {
        this.cartItems = [];
      }
    },

    // 加载购物车
    loadCart() {
      // 先检查登录状态
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!loggedIn) {
        // 如果未登录，不显示购物车数据
        this.cartItems = [];
        return;
      }

      const cartData = localStorage.getItem('lenovo_cart');
      if (cartData) {
        try {
          this.cartItems = JSON.parse(cartData);
        } catch (error) {
          console.error('解析购物车数据失败:', error);
          this.cartItems = [];
          localStorage.removeItem('lenovo_cart');
        }
      }
    },

    // 保存购物车
    saveCart() {
      // 只有在登录状态下才保存购物车数据
      const loggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!loggedIn) {
        return;
      }

      localStorage.setItem('lenovo_cart', JSON.stringify(this.cartItems));
      // 更新购物车角标数量
      this.updateCartBadge();
    },

    // 更新购物车角标数量
    updateCartBadge() {
      const totalItems = this.cartItems.reduce((total, item) => total + item.quantity, 0);
      // 存储到localStorage供其他组件使用
      localStorage.setItem('cart_item_count', totalItems.toString());
      // 触发自定义事件通知其他组件更新
      const event = new CustomEvent('cart-updated', {
        detail: { count: totalItems }
      });
      window.dispatchEvent(event);
    },

    // 减少数量
    decreaseQuantity(productId) {
      for (let item of this.cartItems) {
        if (item.id === productId && item.quantity > 1) {
          item.quantity--;
          this.saveCart();
          break;
        }
      }
    },

    // 增加数量
    increaseQuantity(productId) {
      for (let item of this.cartItems) {
        if (item.id === productId) {
          item.quantity++;
          this.saveCart();
          break;
        }
      }
    },

    // 删除商品
    removeItem(productId) {
      if (confirm('确定要删除这个商品吗？')) {
        this.cartItems = this.cartItems.filter(item => item.id !== productId);
        this.saveCart();
        this.generateProductRecommendations();
      }
    },

    // 清空购物车
    clearCart() {
      if (confirm('确定要清空购物车吗？')) {
        this.cartItems = [];
        localStorage.setItem('lenovo_cart', JSON.stringify([]));
        this.generateProductRecommendations();
        this.updateCartBadge();
      }
    },

    // 退出登录时（只移除登录状态，保留购物车数据）
    logoutOnly() {
      // 清除登录状态
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');

      // 清空购物车数据
      localStorage.removeItem('lenovo_cart');

      // 更新本地状态
      this.isLoggedIn = false;
      this.username = '';
      this.cartItems = [];

      // 触发storage事件让其他页面也更新
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'isLoggedIn',
        newValue: 'false',
        oldValue: 'true'
      }));
    },

    // 结算
    checkout() {
      alert(`结算成功！\n\n用户：${this.username}\n商品数量：${this.totalItems}件\n总金额：¥${this.totalPrice.toLocaleString()}\n\n订单已生成，感谢您的购买！`);
    },

    // 去登录
    goToLogin() {
      this.$router.push('/login');
    },

    // 返回首页
    goToHome() {
      this.$router.push('/');
    },

    // 退出登录（只移除登录状态，不清除购物车数据）
    handleLogout() {
      if (confirm('确定要退出登录吗？购物车数据将被清空。')) {
        // 清空购物车数据
        localStorage.removeItem('lenovo_cart');
        // 清除登录状态
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('username');
        // 跳转到首页
        this.$router.push('/');
      }
    },

    // 添加推荐商品到购物车
    addToCart(product) {
      // 检查是否已在购物车中
      const existingItem = this.cartItems.find(item => item.id === product.id);

      if (existingItem) {
        // 如果已存在，增加数量
        existingItem.quantity++;
      } else {
        // 如果不存在，添加新商品
        this.cartItems.push({
          id: product.id,
          name: product.name,
          price: product.price,
          quantity: 1
        });
        // 标记为已添加
        const productIndex = this.recommendedProducts.findIndex(p => p.id === product.id);
        if (productIndex !== -1) {
          this.recommendedProducts[productIndex].added = true;
        }

        // 显示添加成功的消息
        this.showAddToCartMessage(product.name);
      }

      this.saveCart();
      this.generateProductRecommendations();

      // 3秒后重置添加状态（仅对新添加的商品）
      if (!existingItem) {
        setTimeout(() => {
          const productIndex = this.recommendedProducts.findIndex(p => p.id === product.id);
          if (productIndex !== -1) {
            this.recommendedProducts[productIndex].added = false;
          }
        }, 2000);
      }
    },

    // 显示添加成功的消息
    showAddToCartMessage(productName) {
      // 创建消息元素
      const message = document.createElement('div');
      message.className = 'add-to-cart-message';
      message.innerHTML = `
        <div class="message-content">
          <span class="message-icon">✓</span>
          <span>${productName} 已添加到购物车！</span>
        </div>
      `;

      // 添加到页面
      document.body.appendChild(message);

      // 显示动画
      setTimeout(() => {
        message.classList.add('show');
      }, 10);

      // 3秒后移除
      setTimeout(() => {
        message.classList.remove('show');
        setTimeout(() => {
          if (document.body.contains(message)) {
            document.body.removeChild(message);
          }
        }, 300);
      }, 2000);
    }
  }
};
</script>

<style scoped>
.cart-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 70vh;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
}

/* ===== 未登录状态样式 ===== */
.not-logged-in {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
  padding: 40px 20px;
}

.login-required {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 60px 50px;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
  border: 2px solid #bbdefb;
  position: relative;
  overflow: hidden;
  text-align: center;
  max-width: 500px;
  width: 100%;
}

.login-required::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #90cdf4, #667eea);
}

.lock-icon {
  font-size: 80px;
  color: #667eea;
  margin-bottom: 30px;
  animation: lockFloat 3s ease-in-out infinite;
}

@keyframes lockFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  25% { transform: translateY(-10px) scale(1.05); }
  50% { transform: translateY(0) scale(1); }
  75% { transform: translateY(-10px) scale(0.95); }
}

.login-required h2 {
  color: #2b6cb0;
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 15px;
  letter-spacing: 1px;
}

.login-required p {
  color: #666;
  font-size: 18px;
  margin-bottom: 40px;
  line-height: 1.6;
}

.login-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  flex-wrap: wrap;
}

.btn-login, .btn-back-home {
  padding: 16px 35px;
  border-radius: 50px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.4s ease;
  min-width: 160px;
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.3);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.btn-login {
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
}

.btn-login:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #1e40af 100%);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.5);
}

.btn-back-home {
  background: linear-gradient(135deg, #90cdf4 0%, #667eea 100%);
  color: white;
}

.btn-back-home:hover {
  background: linear-gradient(135deg, #64b5f6 0%, #4299e1 100%);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.5);
}

/* ===== 导航栏布局调整 ===== */
.cart-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.1);
  border: 2px solid #bbdefb;
  position: relative;
  overflow: hidden;
}

.cart-nav::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #90cdf4, #667eea);
}

.nav-left {
  flex: 1;
}

.nav-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.user-welcome {
  display: flex;
  flex-direction: column;
}

.user-welcome h1 {
  color: #2b6cb0;
  margin: 0 0 5px 0;
  font-size: 32px;
  font-weight: 700;
}

.welcome-text {
  color: #667eea;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.cart-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.continue-shopping {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
  text-decoration: none;
  padding: 12px 25px;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
}

.continue-shopping:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #1e40af 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.continue-icon {
  font-size: 18px;
  font-weight: bold;
}

.logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 0;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(244, 67, 54, 0.3);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #ff6659 0%, #e33f3f 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(244, 67, 54, 0.4);
}

.logout-icon {
  font-size: 18px;
  font-weight: bold;
}

/* 购物车主体内容 - 两列布局 */
.cart-main-content {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
}

/* 左侧商品列表区域 */
.cart-left-section {
  flex: 2;
}

/* 右侧订单汇总区域 */
.cart-right-section {
  flex: 1;
  max-width: 400px;
}

/* ===== 美化空购物车界面 ===== */
.empty-cart {
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.empty-cart-container {
  max-width: 800px;
  width: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 50px 40px;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
  border: 2px solid #bbdefb;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.empty-cart-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #90cdf4, #667eea, #90cdf4, #667eea);
}

.empty-cart-illustration {
  margin-bottom: 40px;
}

.cart-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
}

.cart-icon {
  font-size: 120px;
  color: #667eea;
  filter: drop-shadow(0 8px 20px rgba(102, 126, 234, 0.3));
  animation: cartFloat 3s ease-in-out infinite;
}

.empty-indicator {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-line {
  width: 2px;
  height: 40px;
  background: #f44336;
  margin: 4px 0;
  opacity: 0.8;
  animation: indicatorPulse 2s ease-in-out infinite;
}

.empty-line:nth-child(2) {
  animation-delay: 0.3s;
}

@keyframes cartFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  25% { transform: translateY(-10px) rotate(-2deg); }
  50% { transform: translateY(0) rotate(0deg); }
  75% { transform: translateY(-10px) rotate(2deg); }
}

@keyframes indicatorPulse {
  0%, 100% { transform: scaleY(1); opacity: 0.8; }
  50% { transform: scaleY(1.2); opacity: 1; }
}

.empty-cart-content {
  max-width: 500px;
  margin: 0 auto;
}

.empty-title {
  color: #2b6cb0;
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 15px;
  letter-spacing: 1px;
}

.empty-subtitle {
  color: #666;
  font-size: 18px;
  margin-bottom: 40px;
  line-height: 1.6;
}

.empty-actions {
  margin: 40px 0;
}

.btn-go-shopping {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
  text-decoration: none;
  padding: 20px 50px;
  border-radius: 0;
  font-size: 20px;
  font-weight: 700;
  transition: all 0.4s ease;
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
  min-width: 220px;
}

.btn-go-shopping::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.7s ease;
}

.btn-go-shopping:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #1e40af 100%);
  transform: translateY(-5px) scale(1.05);
  box-shadow: 0 18px 45px rgba(102, 126, 234, 0.5);
}

.btn-go-shopping:hover::before {
  left: 100%;
}

.btn-icon {
  font-size: 24px;
  animation: iconBounce 2s ease-in-out infinite;
}

.btn-text {
  position: relative;
  z-index: 1;
}

@keyframes iconBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.empty-tips {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #e8f5e9;
}

.tip-item {
  color: #555;
  font-size: 16px;
  margin: 12px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.tip-item::before {
  content: '';
  width: 8px;
  height: 8px;
  background: #4caf50;
  border-radius: 50%;
}

/* ===== 相似商品推荐栏样式（绿色系） ===== */
.recommendations-section {
  margin-top: 20px;
}

.recommendations-container {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-radius: 0;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.1);
  border: 2px solid #bbdefb;
  position: relative;
  overflow: hidden;
}

.recommendations-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #90cdf4, #667eea, #90cdf4, #667eea);
}

.recommendations-header {
  text-align: center;
  margin-bottom: 30px;
}

.recommendations-header h3 {
  margin: 0 0 10px 0;
  color: #2b6cb0;
  font-size: 24px;
  font-weight: 700;
  position: relative;
  display: inline-block;
}

.recommendations-header h3::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #90cdf4);
  border-radius: 0;
}

.recommendations-subtitle {
  color: #667eea;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
  margin-bottom: 20px;
}

@media (max-width: 1200px) {
  .recommendations-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .cart-nav {
    flex-direction: column;
    gap: 20px;
  }

  .nav-left, .nav-right {
    width: 100%;
  }

  .nav-right {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .recommendations-grid {
    grid-template-columns: 1fr;
  }

  .cart-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}

.recommended-product {
  display: flex;
  flex-direction: column;
}

.product-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 25px 20px;
  border: 2px solid #e8f5e9;
  border-radius: 12px;
  background: white;
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
  height: 100%;
}

.product-card:hover {
  border-color: #667eea;
  background: #ebf8ff;
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.2);
}

.product-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #90cdf4);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover::before {
  opacity: 1;
}

.product-image-placeholder {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #ebf8ff 0%, #bee3f8 100%);
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.product-image-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  animation: shine 3s infinite;
}

@keyframes shine {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.product-icon {
  font-size: 48px;
  color: #667eea;
  filter: drop-shadow(0 2px 4px rgba(102, 126, 234, 0.2));
}

.product-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #ff9800, #ff5722);
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(255, 87, 34, 0.3);
}

.product-info {
  text-align: center;
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  color: #2b6cb0;
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  color: #f44336;
  font-size: 24px;
  font-weight: 800;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
}

.product-specs {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.spec-tag {
  background: linear-gradient(135deg, #ebf8ff 0%, #bee3f8 100%);
  color: #2b6cb0;
  padding: 6px 12px;
  border-radius: 0;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid #90cdf4;
  transition: all 0.3s ease;
}

.spec-tag:hover {
  background: linear-gradient(135deg, #bee3f8 0%, #90cdf4 100%);
  transform: translateY(-1px);
}

/* 加入购物车按钮（绿色系） */
.btn-add-to-cart {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
  border: none;
  padding: 12px 15px;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-add-to-cart:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #1e40af 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.4);
}

.btn-add-to-cart.added-to-cart {
  background: linear-gradient(135deg, #90cdf4 0%, #667eea 100%);
  cursor: default;
}

.btn-add-to-cart.added-to-cart:hover {
  transform: none;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.btn-icon {
  font-size: 16px;
  font-weight: bold;
}

.recommendations-footer {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #e8f5e9;
  text-align: center;
}

.recommendations-tips {
  color: #81c784;
  font-size: 13px;
  font-style: italic;
  margin: 0;
}

/* ===== 保持所有原有的购物车样式不变 ===== */
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e8f5e9;
}

.cart-header h3 {
  margin: 0;
  color: #2b6cb0;
  font-size: 24px;
  font-weight: 700;
}

.btn-clear {
  background: linear-gradient(135deg, #ff6b6b 0%, #f44336 100%);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 0;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 6px 20px rgba(244, 67, 54, 0.3);
}

.btn-clear:hover {
  background: linear-gradient(135deg, #ff8585 0%, #ff6659 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(244, 67, 54, 0.4);
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border: 2px solid #e8f5e9;
  border-radius: 12px;
  background: white;
  transition: all 0.3s ease;
}

.cart-item:hover {
  border-color: #667eea;
  background: #ebf8ff;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-weight: 700;
  color: #2b6cb0;
  margin-bottom: 8px;
  font-size: 18px;
}

.item-price {
  color: #667eea;
  font-weight: 700;
  font-size: 20px;
}

.item-quantity {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: 2px solid #667eea;
  background: white;
  border-radius: 0;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  font-weight: bold;
  transition: all 0.3s ease;
}

.quantity-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-color: #ccc;
  color: #ccc;
}

.quantity {
  min-width: 40px;
  text-align: center;
  font-weight: 700;
  font-size: 18px;
  color: #2b6cb0;
}

.item-total {
  font-weight: 700;
  color: #667eea;
  font-size: 22px;
  text-align: center;
}

.btn-remove {
  background: linear-gradient(135deg, #ff6b6b 0%, #f44336 100%);
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

.btn-remove:hover {
  background: linear-gradient(135deg, #ff8585 0%, #ff6659 100%);
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 6px 20px rgba(244, 67, 54, 0.4);
}

.cart-summary h3 {
  margin-top: 0;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 2px solid #ebf8ff;
  color: #2b6cb0;
  font-size: 24px;
  font-weight: 700;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 12px 0;
  border-bottom: 1px dashed #c8e6c9;
  color: #555;
  font-size: 16px;
}

.summary-row.total {
  border-bottom: none;
  border-top: 2px solid #667eea;
  padding-top: 20px;
  margin-top: 20px;
  font-size: 20px;
  font-weight: 700;
  color: #2b6cb0;
}

.total-price {
  color: #667eea;
  font-size: 28px;
  font-weight: 800;
}

.discount {
  color: #81c784;
}

.btn-checkout {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
  border: none;
  padding: 16px;
  border-radius: 0;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  margin-top: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  letter-spacing: 1px;
}

.btn-checkout:hover {
  background: linear-gradient(135deg, #5a67d8 0%, #1e40af 100%);
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.cart-tips {
  margin-top: 15px;
  color: #81c784;
  font-size: 13px;
  text-align: center;
  font-style: italic;
}

.user-info-footer {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 2px solid #e8f5e9;
  font-size: 14px;
  color: #666;
}

.user-info-footer p {
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info-footer strong {
  color: #2b6cb0;
  font-size: 16px;
}

.status-online {
  color: #667eea;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 5px;
}

.status-online::before {
  content: '●';
  color: #667eea;
  font-size: 12px;
  animation: pulse 2s infinite;
}

/* 响应式调整 */
@media (max-width: 900px) {
  .cart-main-content {
    flex-direction: column;
  }

  .cart-right-section {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .cart-nav {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .cart-item {
    grid-template-columns: 1fr;
    gap: 15px;
    text-align: center;
  }

  .item-quantity {
    justify-content: center;
  }

  .cart-actions {
    flex-wrap: wrap;
    justify-content: center;
  }

  .login-required {
    padding: 30px 20px;
    margin: 20px;
  }

  .btn-login, .btn-back-home {
    margin: 5px;
    padding: 10px 25px;
  }

  .empty-title {
    font-size: 28px;
  }

  .empty-subtitle {
    font-size: 16px;
  }

  .btn-go-shopping {
    padding: 16px 40px;
    font-size: 18px;
  }

  .login-actions {
    flex-direction: column;
    align-items: center;
  }

  .btn-login, .btn-back-home {
    width: 100%;
    max-width: 300px;
    margin: 5px 0;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 添加成功的消息样式 */
.add-to-cart-message {
  position: fixed;
  top: 100px;
  right: 30px;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  color: white;
  padding: 15px 25px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.4);
  z-index: 9999;
  transform: translateX(400px);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.add-to-cart-message.show {
  transform: translateX(0);
  opacity: 1;
}

.message-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
}

.message-icon {
  font-size: 24px;
  font-weight: bold;
  animation: bounceIn 0.5s ease;
}

@keyframes bounceIn {
  0% { transform: scale(0); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}
</style>

<!-- 添加全局样式用于消息提示 -->
<style>
.add-to-cart-message {
  position: fixed;
  top: 100px;
  right: 30px;
  background: linear-gradient(135deg, #667eea 0%, #2b6cb0 100%);
  color: white;
  padding: 15px 25px;
  border-radius: 0;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
  z-index: 9999;
  transform: translateX(400px);
  opacity: 0;
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

.add-to-cart-message.show {
  transform: translateX(0);
  opacity: 1;
}

.message-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
}

.message-icon {
  font-size: 24px;
  font-weight: bold;
  animation: bounceIn 0.5s ease;
}
</style>