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

    <!-- 结算对话框 -->
    <div v-if="showCheckoutDialog" class="checkout-dialog-overlay" @click.self="closeCheckoutDialog">
      <div class="checkout-dialog">
        <div class="dialog-header">
          <h2>填写收货信息</h2>
          <button @click="closeCheckoutDialog" class="close-btn">✕</button>
        </div>
        <div class="dialog-content">
          <div class="form-group">
            <label>省份：<span class="required">*</span></label>
            <select v-model="checkoutForm.province" class="form-input" required>
              <option value="">请选择省份</option>
              <option v-for="p in provinces" :key="p" :value="p">{{ p }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>详细地址：<span class="required">*</span></label>
            <input
              v-model="checkoutForm.receiverAddress"
              type="text"
              class="form-input"
              placeholder="请输入详细地址"
              required
            />
          </div>
          <div class="form-group">
            <label>收货人：<span class="required">*</span></label>
            <input
              v-model="checkoutForm.receiverName"
              type="text"
              class="form-input"
              placeholder="请输入收货人姓名"
              required
            />
          </div>
          <div class="form-group">
            <label>联系电话：<span class="required">*</span></label>
            <input
              v-model="checkoutForm.receiverPhone"
              type="tel"
              class="form-input"
              placeholder="请输入联系电话"
              required
            />
          </div>
          <div class="form-group">
            <label>备注：</label>
            <textarea
              v-model="checkoutForm.remark"
              class="form-textarea"
              placeholder="选填，如：工作日配送、需要发票等"
              rows="3"
            ></textarea>
          </div>
          <div class="order-summary-info">
            <div class="summary-item">
              <span>商品数量：</span>
              <span>{{ totalItems }}件</span>
            </div>
            <div class="summary-item">
              <span>订单总额：</span>
              <span class="total-price">¥{{ totalPrice.toLocaleString() }}</span>
            </div>
          </div>
          <!-- 付款二维码 -->
          <div class="payment-qrcode-section">
            <div class="qrcode-label">扫码付款</div>
            <div class="qrcode-container">
              <img :src="qrcodeImageSrc" alt="付款二维码" class="qrcode-image" />
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button @click="closeCheckoutDialog" class="btn-cancel">取消</button>
          <button @click="createOrder" class="btn-submit" :disabled="isCreatingOrder">
            {{ isCreatingOrder ? '创建中...' : '提交订单' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '@/config/api.config';
import axios from 'axios';

export default {
  name: 'CartView',
  data() {
    return {
      cartItems: [],
      isLoggedIn: false,
      username: '',
      userId: null,
      allProducts: [],
      recommendedProducts: [],
      showCheckoutDialog: false,
      isCreatingOrder: false,
      checkoutForm: {
        province: '',
        receiverAddress: '',
        receiverName: '',
        receiverPhone: '',
        remark: ''
      },
      qrcodeImageSrc: require('@/assets/付款.jpg'),
      provinces: [
        '北京市', '天津市', '上海市', '重庆市',
        '河北省', '山西省', '辽宁省', '吉林省', '黑龙江省',
        '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省',
        '河南省', '湖北省', '湖南省', '广东省', '海南省',
        '四川省', '贵州省', '云南省', '陕西省', '甘肃省', '青海省', '台湾省',
        '内蒙古自治区', '广西壮族自治区', '西藏自治区', '宁夏回族自治区', '新疆维吾尔自治区',
        '香港特别行政区', '澳门特别行政区'
      ]
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
        this.userId = localStorage.getItem('userId');
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

    // 处理登出（不清空购物车，因为购物车数据在后端）
    handleLogout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('userId');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('loginMethod');
      localStorage.removeItem('rememberMe');

      // 不清空购物车，因为购物车数据在后端，用户重新登录后会自动加载

      this.isLoggedIn = false;
      this.username = '';
      this.userId = null;
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

    // 从后端加载购物车
    async loadCart() {
      if (!this.userId) {
        this.cartItems = [];
        return;
      }

      try {
        const response = await axios.get(apiConfig.user.getUserCart(this.userId));
        if (response.data.code === 1 && response.data.data) {
          // 后端返回的购物车数据格式: [{cid, uid, pid, quantity, bid}]
          // 需要转换为前端格式，并获取商品详情
          const cartData = response.data.data;
          
          // 获取所有商品信息以补充商品详情
          if (this.allProducts.length === 0) {
            await this.fetchAllProducts();
          }

          // 转换为前端格式
          this.cartItems = cartData.map(cartItem => {
            const product = this.allProducts.find(p => (p.pid || p.id) == cartItem.pid);
            return {
              id: cartItem.pid,
              cid: cartItem.cid, // 保存购物车项ID，用于更新和删除
              pid: cartItem.pid,
              bid: cartItem.bid,
              quantity: cartItem.quantity,
              name: product?.name || '商品',
              price: product?.price || 0,
              brand: product?.brand || '联想',
              imageUrl: product?.imageUrl || ''
            };
          });

          this.updateCartBadge();
        } else {
          this.cartItems = [];
        }
      } catch (error) {
        console.error('加载购物车失败:', error);
        this.cartItems = [];
      }
    },

    // 更新购物车角标（触发事件通知其他组件）
    updateCartBadge() {
      const totalItems = this.cartItems.reduce((total, item) => total + (item.quantity || 0), 0);
      // 触发自定义事件，让其他组件知道购物车数量变化
      const event = new CustomEvent('cart-updated', { detail: { count: totalItems } });
      window.dispatchEvent(event);
    },

    // 增加商品数量
    async increaseQuantity(productId) {
      const item = this.cartItems.find(item => item.id === productId);
      if (!item || !this.userId) return;

      try {
        const newQuantity = item.quantity + 1;
        
        // 如果有cid，使用更新接口；否则使用添加接口
        if (item.cid) {
          const response = await axios.put(apiConfig.user.updateCartQuantity, {
            uid: this.userId,
            cid: item.cid,
            quantity: newQuantity
          });
          
          if (response.data.code === 1) {
            item.quantity = newQuantity;
            this.updateCartBadge();
          } else {
            alert(response.data.msg || '更新失败');
          }
        } else {
          // 如果没有cid，说明是新添加的商品，调用添加接口
          const product = this.allProducts.find(p => (p.pid || p.id) == productId);
          await this.addToCartBackend(product || { id: productId, bid: item.bid || 1 });
        }
      } catch (error) {
        console.error('增加数量失败:', error);
        alert('操作失败，请稍后重试');
      }
    },

    // 减少商品数量
    async decreaseQuantity(productId) {
      const item = this.cartItems.find(item => item.id === productId);
      if (!item || item.quantity <= 1 || !this.userId) return;

      try {
        const newQuantity = item.quantity - 1;
        
        if (item.cid) {
          const response = await axios.put(apiConfig.user.updateCartQuantity, {
            uid: this.userId,
            cid: item.cid,
            quantity: newQuantity
          });
          
          if (response.data.code === 1) {
            item.quantity = newQuantity;
            this.updateCartBadge();
          } else {
            alert(response.data.msg || '更新失败');
          }
        }
      } catch (error) {
        console.error('减少数量失败:', error);
        alert('操作失败，请稍后重试');
      }
    },

    // 删除商品
    async removeItem(productId) {
      if (!confirm('确定要删除这个商品吗？')) return;
      if (!this.userId) return;

      const item = this.cartItems.find(item => item.id === productId);
      if (!item) return;

      try {
        let response;
        
        // 如果有cid，使用cid删除；否则使用pid删除
        if (item.cid) {
          response = await axios.delete(apiConfig.user.deleteCartItemByCid(item.cid, this.userId));
        } else {
          response = await axios.delete(apiConfig.user.deleteCartItem(this.userId, productId));
        }

        if (response.data.code === 1) {
          this.cartItems = this.cartItems.filter(item => item.id !== productId);
          this.updateCartBadge();
          this.generateProductRecommendations();
        } else {
          alert(response.data.msg || '删除失败');
        }
      } catch (error) {
        console.error('删除商品失败:', error);
        alert('删除失败，请稍后重试');
      }
    },

    // 清空购物车
    async clearCart() {
      if (!confirm('确定要清空购物车吗？')) return;
      if (!this.userId) return;

      try {
        const response = await axios.delete(apiConfig.user.clearCart(this.userId));
        
        if (response.data.code === 1) {
          this.cartItems = [];
          this.updateCartBadge();
          this.generateProductRecommendations();
        } else {
          alert(response.data.msg || '清空失败');
        }
      } catch (error) {
        console.error('清空购物车失败:', error);
        alert('清空失败，请稍后重试');
      }
    },

    // 添加推荐商品到购物车（调用后端接口）
    async addToCart(product) {
      if (!this.userId) {
        alert('请先登录');
        this.$router.push('/login');
        return;
      }

      await this.addToCartBackend(product);
    },

    // 调用后端接口添加商品到购物车（合并相同商品）
    async addToCartBackend(product) {
      if (!this.userId) return;

      try {
        const productId = product.pid || product.id;
        const bid = product.bid || 1;

        // 先检查购物车中是否已有该商品
        try {
          const cartResponse = await axios.get(apiConfig.user.getUserCart(this.userId));
          if (cartResponse.data.code === 1 && cartResponse.data.data) {
            const existingItem = cartResponse.data.data.find(item => item.pid == productId);
            if (existingItem) {
              // 如果已存在，更新数量
              const updateResponse = await axios.put(apiConfig.user.updateCartQuantity, {
                uid: this.userId,
                cid: existingItem.cid,
                quantity: existingItem.quantity + 1
              });
              if (updateResponse.data.code === 1) {
                // 重新加载购物车以获取最新数据
                await this.loadCart();
                this.showAddToCartMessage(product.name || '商品');
              } else {
                alert(updateResponse.data.msg || '更新失败');
              }
              return;
            }
          }
        } catch (error) {
          console.error('检查购物车失败:', error);
        }

        // 如果不存在，添加新商品
        const response = await axios.post(apiConfig.user.addToCart, {
          uid: this.userId,
          pid: productId,
          bid: bid,
          quantity: 1
        });

        if (response.data.code === 1) {
          // 重新加载购物车以获取最新数据（包括cid）
          await this.loadCart();
          
          // 标记为已添加
          const productIndex = this.recommendedProducts.findIndex(p => (p.pid || p.id) == productId);
          if (productIndex !== -1) {
            this.recommendedProducts[productIndex].added = true;
          }
          
          this.showAddToCartMessage(product.name || '商品');
        } else {
          alert(response.data.msg || '添加失败');
        }
      } catch (error) {
        console.error('添加到购物车失败:', error);
        alert('添加失败，请稍后重试');
      }
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
      if (this.cartItems.length === 0) {
        alert('购物车为空，无法结算');
        return;
      }
      // 重置表单
      this.checkoutForm = {
        province: '',
        receiverAddress: '',
        receiverName: '',
        receiverPhone: '',
        remark: ''
      };
      this.showCheckoutDialog = true;
    },
    // 关闭结算对话框
    closeCheckoutDialog() {
      this.showCheckoutDialog = false;
    },
    // 创建订单
    async createOrder() {
      // 验证表单
      if (!this.checkoutForm.province) {
        alert('请选择省份');
        return;
      }
      if (!this.checkoutForm.receiverAddress || !this.checkoutForm.receiverAddress.trim()) {
        alert('请输入详细地址');
        return;
      }
      if (!this.checkoutForm.receiverName || !this.checkoutForm.receiverName.trim()) {
        alert('请输入收货人姓名');
        return;
      }
      if (!this.checkoutForm.receiverPhone || !this.checkoutForm.receiverPhone.trim()) {
        alert('请输入联系电话');
        return;
      }
      // 验证手机号格式（简单验证）
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.checkoutForm.receiverPhone.trim())) {
        alert('请输入正确的手机号码');
        return;
      }

      if (!this.userId) {
        alert('未找到用户ID，请重新登录');
        this.$router.push('/login');
        return;
      }

      this.isCreatingOrder = true;

      try {
        // 将购物车数据转换为订单格式
        const orderItems = this.cartItems.map(item => {
          // 从allProducts中查找商品信息获取bid
          const product = this.allProducts.find(p => (p.pid || p.id) == item.id);
          return {
            pid: item.id,
            quantity: item.quantity,
            bid: product?.bid || 1 // 如果没有bid，默认使用1
          };
        });

        const orderData = {
          uid: parseInt(this.userId),
          items: orderItems,
          province: this.checkoutForm.province,
          receiverAddress: this.checkoutForm.receiverAddress.trim(),
          receiverName: this.checkoutForm.receiverName.trim(),
          receiverPhone: this.checkoutForm.receiverPhone.trim(),
          remark: this.checkoutForm.remark.trim() || ''
        };

        console.log('创建订单数据:', orderData);

        const response = await axios.post(apiConfig.order.createOrder, orderData);

        if (response.data.code === 1) {
          const orderId = response.data.data;
          alert(`订单创建成功！订单号：${orderId}`);
          
          // 清空购物车（调用后端接口）
          if (this.userId) {
            try {
              await axios.delete(apiConfig.user.clearCart(this.userId));
            } catch (error) {
              console.error('清空购物车失败:', error);
            }
          }
          this.cartItems = [];
          this.updateCartBadge();
          this.generateProductRecommendations();
          
          // 关闭对话框
          this.closeCheckoutDialog();
          
          // 跳转到订单中心
          this.$router.push('/user/orders');
        } else {
          alert(response.data.msg || '创建订单失败');
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        if (error.response && error.response.data) {
          alert(error.response.data.msg || '创建订单失败，请稍后重试');
        } else {
          alert('创建订单失败，请稍后重试');
        }
      } finally {
        this.isCreatingOrder = false;
      }
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

/* 结算对话框样式 */
.checkout-dialog-overlay {
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
}

.checkout-dialog {
  background: white;
  border-radius: 0;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  border: 2px solid #4b6cb7;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 2px solid #e0e0e0;
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}

.dialog-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 28px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s;
}

.close-btn:hover {
  opacity: 0.7;
}

.dialog-content {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.required {
  color: #f44336;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #bbdefb;
  border-radius: 0;
  font-size: 16px;
  background: #f8fbff;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #4b6cb7;
  background: white;
  box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.order-summary-info {
  margin-top: 25px;
  padding: 15px;
  background: linear-gradient(135deg, #f8fbff 0%, #e3f2fd 100%);
  border: 2px solid #bbdefb;
  border-radius: 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
}

.summary-item:last-child {
  margin-bottom: 0;
  padding-top: 10px;
  border-top: 1px solid #bbdefb;
  font-size: 18px;
  font-weight: 700;
}

.summary-item .total-price {
  color: #4b6cb7;
  font-size: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 2px solid #e0e0e0;
  background: #f8fbff;
}

.btn-cancel,
.btn-submit {
  padding: 12px 30px;
  border: none;
  border-radius: 0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f5f5f5;
  color: #333;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 付款二维码样式 */
.payment-qrcode-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  text-align: center;
}

.qrcode-label {
  font-size: 16px;
  font-weight: 600;
  color: #182848;
  margin-bottom: 15px;
}

.qrcode-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.qrcode-image {
  max-width: 250px;
  width: 100%;
  height: auto;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
}
</style>