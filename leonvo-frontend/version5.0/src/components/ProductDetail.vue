<template>
  <div class="product-detail">
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
        <!-- 返回按钮整合到页眉中 -->
        <div class="header-top">
          <button @click="goBack" class="back-btn-header">
            <span class="back-icon">←</span>
            返回商品列表
          </button>
        </div>

        <div class="header-content">
          <div class="title-section">
            <h1>{{ product.name }}</h1>
          </div>

          <div class="product-meta">
            <div class="meta-group">
              <span class="meta-item brand">
                <span class="meta-icon">🏷️</span>
                <span class="meta-value">{{ product.brand || '联想' }}</span>
              </span>
              <span class="meta-item rating">
                <span class="meta-icon">⭐</span>
                <span class="meta-value">{{ product.rating ? (product.rating / 20).toFixed(1) : 4.5 }}</span>
              </span>
              <span class="meta-item stock" :class="{ 'low-stock': product.stock < 10 }">
                <span class="meta-icon">📦</span>
                <span class="meta-value">{{ product.stock || 0 }}件</span>
              </span>
            </div>
            <div class="price-display">
              <span class="price-tag">¥{{ product.price ? product.price.toLocaleString() : '0' }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-content">
        <!-- 左侧：图片和价格 -->
        <div class="left-column">
          <div class="product-image-section">
            <div class="image-container">
              <img :src="getImageUrl(product.imageUrl)" :alt="product.name" class="product-image-large" />
              <div v-if="!product.imageUrl" class="image-placeholder-large">
                <span class="placeholder-icon">🌿</span>
                <span>暂无图片</span>
              </div>
            </div>
            <div class="image-actions">
              <button class="btn-action" @click="viewFullImage">
                <span class="action-icon">🔍</span>
                查看大图
              </button>
            </div>
          </div>

          <!-- 价格盒子 -->
          <div class="price-box">
            <div class="price-section">
              <div class="price-header">
                <span class="price-label">商品价格</span>
              </div>
              <div class="current-price">
                <span class="price-value">¥{{ product.price ? product.price.toLocaleString() : '0' }}</span>
              </div>
            </div>

            <div class="action-buttons">
              <button class="btn-add-to-cart" @click="addToCart" :disabled="product.stock <= 0">
                <span class="cart-icon">🛒</span>
                {{ product.stock > 0 ? '加入购物车' : '已售罄' }}
              </button>
              <button class="btn-buy" @click="buyNow" :disabled="product.stock <= 0">
                <span class="buy-icon">⚡</span>
                {{ product.stock > 0 ? '立即购买' : '库存不足' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧：详细信息 -->
        <div class="right-column">
          <!-- 商品描述 -->
          <div class="description-section" v-if="product.description">
            <div class="section-header">
              <h3>
                <span class="section-icon">📝</span>
                商品描述
              </h3>
            </div>
            <div class="description-content">
              <p>{{ product.description }}</p>
            </div>
          </div>

          <!-- 规格信息 -->
          <div class="specifications-section">
            <div class="section-header">
              <h3>
                <span class="section-icon">⚙️</span>
                商品规格
              </h3>
            </div>
            <div class="specs-content">
              <div class="spec-row" v-if="product.cpu">
                <span class="spec-label">处理器：</span>
                <span class="spec-value">{{ product.cpu }}</span>
              </div>
              <div class="spec-row" v-if="product.gpu">
                <span class="spec-label">显卡：</span>
                <span class="spec-value">{{ product.gpu }}</span>
              </div>
              <div class="spec-row" v-if="product.storage">
                <span class="spec-label">存储：</span>
                <span class="spec-value">{{ product.storage }}</span>
              </div>
              <div class="spec-row" v-if="product.size">
                <span class="spec-label">尺寸：</span>
                <span class="spec-value">{{ product.size }}</span>
              </div>
              <div class="spec-row" v-if="product.type">
                <span class="spec-label">类型：</span>
                <span class="spec-value">{{ product.type }}</span>
              </div>
              <div class="spec-row" v-if="!product.cpu && !product.gpu && !product.storage && !product.size && !product.type">
                <span class="spec-value">暂无规格信息</span>
              </div>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-section">
            <div class="section-header">
              <h3>
                <span class="section-icon">💬</span>
                商品评价
              </h3>
            </div>
            <div v-if="loadingFeedback" class="comments-loading">加载评价中...</div>
            <div v-else-if="feedbacks.length === 0" class="comments-empty">暂时还没有评价</div>
            <div v-else class="comments-list">
              <div v-for="fb in feedbacks" :key="fb.id || fb.fid" class="comment-item">
                <div class="comment-avatar">{{ (fb.username || '用戶')[0] || '用' }}</div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-user">{{ fb.username || '用户' }}</span>
                    <span class="comment-stars">
                      <span v-for="n in 5" :key="n" class="star" :class="{ active: (fb.star || 0) >= n }">★</span>
                    </span>
                    <span class="comment-time">{{ formatDateTime(fb.createTime || fb.createdTime || fb.created_at) }}</span>
                  </div>
                  <div class="comment-text">{{ fb.comment || '（无文字评价）' }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 基本信息 -->
          <div class="basic-info">
            <div class="section-header">
              <h3>
                <span class="section-icon">ℹ️</span>
                基本信息
              </h3>
            </div>
            <div class="info-content">
              <div class="info-row">
                <span class="info-icon">🔢</span>
                <span class="info-label">商品ID：</span>
                <span class="info-value">{{ product.id }}</span>
              </div>
              <div class="info-row" v-if="product.brand">
                <span class="info-icon">🏷️</span>
                <span class="info-label">品牌：</span>
                <span class="info-value">{{ product.brand }}</span>
              </div>
              <div class="info-row">
                <span class="info-icon">📈</span>
                <span class="info-label">评分：</span>
                <span class="info-value rating-stars">
                  <span class="stars">{{ getStarRating(product.rating) }}</span>
                  {{ product.rating ? (product.rating / 20).toFixed(1) : '暂无' }}
                </span>
              </div>
              <div class="info-row">
                <span class="info-icon">📦</span>
                <span class="info-label">库存：</span>
                <span :class="[
                  'info-value',
                  product.stock > 10 ? 'stock-available' : 'stock-low'
                ]">
                  {{ product.stock || 0 }}件
                  <span v-if="product.stock <= 10 && product.stock > 0" class="low-stock-text">(库存紧张)</span>
                  <span v-if="product.stock === 0" class="sold-out-text">(已售罄)</span>
                </span>
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

  <!-- 单品结算对话框 -->
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
          >
        </div>
        <div class="form-group">
          <label>收货人姓名：<span class="required">*</span></label>
          <input
            v-model="checkoutForm.receiverName"
            type="text"
            class="form-input"
            placeholder="请输入收货人姓名"
            required
          >
        </div>
        <div class="form-group">
          <label>联系电话：<span class="required">*</span></label>
          <input
            v-model="checkoutForm.receiverPhone"
            type="tel"
            class="form-input"
            placeholder="请输入手机号"
            required
          >
        </div>
        <div class="form-group">
          <label>备注：</label>
          <textarea
            v-model="checkoutForm.remark"
            class="form-input"
            rows="2"
            placeholder="选填，给商家的留言"
          ></textarea>
        </div>
      </div>
      <div class="dialog-footer">
        <button class="btn-cancel" @click="closeCheckoutDialog">取消</button>
        <button class="btn-confirm" @click="submitSingleOrder" :disabled="isCreatingOrder">
          {{ isCreatingOrder ? '提交中...' : '确认下单' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import apiConfig from '@/config/api.config';
import axios from 'axios';

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: null,
      loading: true,
      productId: null,
      isFavorite: false,
      feedbacks: [],
      loadingFeedback: false,
      showCheckoutDialog: false,
      isCreatingOrder: false,
      checkoutForm: {
        province: '',
        receiverAddress: '',
        receiverName: '',
        receiverPhone: '',
        remark: ''
      },
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
  created() {
    this.productId = this.$route.params.id;
    this.fetchProductDetail();
    this.checkFavoriteStatus();
  },
  mounted() {
    // 页面加载后滚动到顶部
    window.scrollTo(0, 0);
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
    formatDateTime(dateString) {
      if (!dateString) return '';
      try {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        return dateString;
      }
    },

    // 获取商品详情
    async fetchProductDetail() {
      this.loading = true;
      try {
        const url = apiConfig.product.getProductDetail(this.productId);
        console.log('请求商品详情URL:', url);

        const response = await fetch(url);

        // 首先检查响应状态
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();

        console.log('商品详情接口返回:', result);

        // 根据后端返回的格式调整
        if (result.code === 1 && result.data) {
          const productData = result.data;

          this.product = {
            id: productData.pid,
            name: productData.name || '未命名商品',
            price: productData.price || 0,
            description: productData.description || '',
            imageUrl: productData.picture || '',
            brand: productData.brand || '',
            rating: productData.rating,
            stock: productData.stock || 0,
            // 规格信息
            cpu: productData.cpu || productData.CPU,
            gpu: productData.gpu || productData.GPU,
            storage: productData.storage,
            size: productData.size,
            type: productData.type
          };

          console.log('处理后的商品数据:', this.product);
        } else {
          this.product = null;
          console.error('获取商品详情失败:', result.msg || '未知错误');
          this.showNotification('❌', result.msg || '获取商品详情失败');
        }
      } catch (error) {
        console.error('获取商品详情失败:', error);
        this.product = null;
        this.showNotification('❌', `获取商品详情失败: ${error.message}`);
      } finally {
        this.loading = false;
        this.loadFeedbacks();
      }
    },
    async loadFeedbacks() {
      if (!this.productId) return;
      this.loadingFeedback = true;
      try {
        const res = await axios.get(apiConfig.feedback.getProductFeedbacks(this.productId));
        if (res.data && res.data.code === 1) {
          this.feedbacks = res.data.data || [];
        } else if (Array.isArray(res.data)) {
          // 兼容无包裹的返回
          this.feedbacks = res.data;
        } else {
          this.feedbacks = [];
        }
      } catch (e) {
        console.error('获取商品评价失败', e);
        this.feedbacks = [];
      } finally {
        this.loadingFeedback = false;
      }
    },

    // 获取星星评分显示（将100分制转换为5星制）
    getStarRating(rating) {
      if (!rating) return '☆☆☆☆☆';
      const starRating = rating / 20;
      const fullStars = Math.floor(starRating);
      const halfStar = starRating - fullStars >= 0.5;
      const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

      let stars = '';
      for (let i = 0; i < fullStars; i++) stars += '★';
      if (halfStar) stars += '☆';
      for (let i = 0; i < emptyStars; i++) stars += '☆';

      return stars;
    },

    // 添加到购物车（调用后端接口，带确认弹窗）
    async addToCart() {
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        this.showNotification('⚠️', '请先登录才能添加到购物车！');
        setTimeout(() => {
          this.$router.push('/login');
        }, 1500);
        return;
      }

      if (this.product.stock <= 0) {
        this.showNotification('❌', '商品已售罄');
        return;
      }

      // 显示确认弹窗
      if (!confirm(`确定要将"${this.product.name}"添加到购物车吗？`)) {
        return;
      }

      try {
        const userId = localStorage.getItem('userId');
        if (!userId) {
          this.showNotification('❌', '未找到用户ID，请重新登录');
          return;
        }

        const productId = this.product.pid || this.product.id;
        const bid = this.product.bid || 1;

        // 先检查购物车中是否已有该商品
        try {
          const cartResponse = await axios.get(apiConfig.user.getUserCart(userId));
          if (cartResponse.data.code === 1 && cartResponse.data.data) {
            const existingItem = cartResponse.data.data.find(item => item.pid == productId);
            if (existingItem) {
              // 如果已存在，更新数量
              const updateResponse = await axios.put(apiConfig.user.updateCartQuantity, {
                uid: userId,
                cid: existingItem.cid,
                quantity: existingItem.quantity + 1
              });
              if (updateResponse.data.code === 1) {
                this.showNotification('🛒', `${this.product.name} 已更新到购物车（数量+1）`);
              } else {
                this.showNotification('❌', updateResponse.data.msg || '更新失败');
              }
              return;
            }
          }
        } catch (error) {
          console.error('检查购物车失败:', error);
        }

        // 如果不存在，添加新商品
        const response = await axios.post(apiConfig.user.addToCart, {
          uid: userId,
          pid: productId,
          bid: bid,
          quantity: 1
        });

        if (response.data.code === 1) {
          this.showNotification('🛒', `${this.product.name} 已加入购物车`);
        } else {
          this.showNotification('❌', response.data.msg || '添加到购物车失败');
        }
      } catch (error) {
        console.error('添加到购物车失败:', error);
        this.showNotification('❌', '添加到购物车失败，请稍后重试');
      }
    },

    // 立即购买
    async buyNow() {
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      if (!isLoggedIn) {
        this.showNotification('⚠️', '请先登录才能购买商品！');
        setTimeout(() => {
          this.$router.push('/login');
        }, 1500);
        return;
      }

      if (this.product.stock <= 0) {
        this.showNotification('❌', '商品已售罄');
        return;
      }

      // 打开单品结算弹窗
      this.checkoutForm = {
        province: '',
        receiverAddress: '',
        receiverName: '',
        receiverPhone: '',
        remark: ''
      };
      this.showCheckoutDialog = true;
    },

    closeCheckoutDialog() {
      this.showCheckoutDialog = false;
    },

    async submitSingleOrder() {
      // 表单校验
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
      const phoneRegex = /^1[3-9]\d{9}$/;
      if (!phoneRegex.test(this.checkoutForm.receiverPhone.trim())) {
        alert('请输入正确的手机号码');
        return;
      }

      const userId = localStorage.getItem('userId');
      if (!userId) {
        alert('未找到用户ID，请重新登录');
        this.$router.push('/login');
        return;
      }

      this.isCreatingOrder = true;
      try {
        const pid = this.product.pid || this.product.id;
        const orderData = {
          uid: parseInt(userId),
          items: [
            {
              pid,
              quantity: 1,
              bid: this.product.bid || 1
            }
          ],
          province: this.checkoutForm.province,
          receiverAddress: this.checkoutForm.receiverAddress.trim(),
          receiverName: this.checkoutForm.receiverName.trim(),
          receiverPhone: this.checkoutForm.receiverPhone.trim(),
          remark: this.checkoutForm.remark.trim() || '',
          status: 0 // 新建订单默认待发货
        };

        const response = await axios.post(apiConfig.order.createOrder, orderData);
        if (response.data.code === 1) {
          const orderId = response.data.data;
          this.showNotification('✅', `订单创建成功！订单号：${orderId}`);
          this.closeCheckoutDialog();
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
    },

    // 收藏商品
    async toggleFavorite() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          this.showNotification('⚠️', '请先登录');
          return;
        }

        let favorites = JSON.parse(localStorage.getItem('lenovo_favorites') || '[]');

        if (this.isFavorite) {
          favorites = favorites.filter(id => id !== this.productId);
          this.showNotification('💔', `已取消收藏 ${this.product.name}`);
        } else {
          if (!favorites.includes(this.productId)) {
            favorites.push(this.productId);
          }
          this.showNotification('❤️', `已收藏 ${this.product.name}`);
        }

        localStorage.setItem('lenovo_favorites', JSON.stringify(favorites));
        this.isFavorite = !this.isFavorite;
      } catch (error) {
        console.error('收藏操作失败:', error);
        this.showNotification('❌', '操作失败');
      }
    },

    checkFavoriteStatus() {
      const favorites = JSON.parse(localStorage.getItem('lenovo_favorites') || '[]');
      this.isFavorite = favorites.includes(parseInt(this.productId));
    },

    goBack() {
      this.$router.push('/');
    },

    viewFullImage() {
      const imageUrl = this.getImageUrl(this.product.imageUrl);
      if (imageUrl) {
        window.open(imageUrl, '_blank');
      } else {
        this.showNotification('⚠️', '该商品暂无大图');
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
/* 重置和基础样式 */
.product-detail {
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 200px);
  position: relative;
  background-color: white;
}

/* 加载样式 */
.loading {
  text-align: center;
  padding: 100px 20px;
  background: white;
  border-radius: 12px;
  margin: 20px;
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
  border: 3px solid #e3f2fd;
  border-top: 3px solid #3498db;
  animation: spin 1.5s linear infinite;
  border-radius: 0;
}

.spinner-inner {
  position: absolute;
  top: 15px;
  left: 15px;
  width: 30px;
  height: 30px;
  border: 2px solid #bbdefb;
  border-top: 2px solid #90caf9;
  animation: spin 1s linear infinite reverse;
  border-radius: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 商品详情容器 */
.detail-container {
  background: white;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  overflow: hidden;
  margin: 20px;
}

/* 重新设计的页眉 */
.product-header {
  background: linear-gradient(135deg, #1565c0 0%, #0d47a1 100%);
  color: white;
  padding: 0;
  position: relative;
}

/* 页眉顶部操作栏 */
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.back-btn-header {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  border-radius: 0;
  backdrop-filter: blur(5px);
}

.back-btn-header:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

/* 页眉主要内容 */
.header-content {
  padding: 25px 30px;
}

.title-section {
  margin-bottom: 20px;
}

.title-section h1 {
  margin: 0 0 5px 0;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

/* 商品元信息 */
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 0;
  backdrop-filter: blur(5px);
}

.meta-item.low-stock {
  background: rgba(255, 152, 0, 0.2);
}

.meta-icon {
  font-size: 14px;
}

.price-display {
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 20px;
  border-radius: 0;
  backdrop-filter: blur(10px);
}

.price-tag {
  font-size: 24px;
  font-weight: 800;
  color: #fff;
}

/* 主要内容区域 */
.detail-content {
  display: flex;
  padding: 30px;
  gap: 30px;
  min-height: 500px;
}

.left-column {
  flex: 1;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.right-column {
  flex: 1.5;
  display: flex;
  flex-direction: column;
  gap: 25px;
}

/* 图片区域 */
.product-image-section {
  position: relative;
}

.image-container {
  width: 100%;
  height: 400px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 0;
  overflow: hidden;
}

.product-image-large {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-placeholder-large {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.placeholder-icon {
  font-size: 50px;
  opacity: 0.5;
}

.image-actions {
  display: flex;
  gap: 15px;
  margin-top: 15px;
}

.btn-action {
  flex: 1;
  background: #f5f5f5;
  border: 1px solid #ddd;
  padding: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  border-radius: 0;
  font-size: 14px;
  color: #333;
}

.btn-action:hover {
  background: #e0e0e0;
}

/* 价格盒子 */
.price-box {
  background: #f9f9f9;
  padding: 25px;
  border: 1px solid #e0e0e0;
  border-radius: 0;
}

.price-section {
  margin-bottom: 25px;
}

.price-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.price-label {
  font-size: 16px;
  color: #666;
  font-weight: 500;
}

.current-price {
  margin-bottom: 10px;
}

.price-value {
  font-size: 32px;
  font-weight: 700;
  color: #e91e63;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.btn-add-to-cart, .btn-buy {
  flex: 1;
  padding: 16px;
  border: none;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  border-radius: 0;
}

.btn-add-to-cart {
  background: #2196f3;
  color: white;
}

.btn-add-to-cart:hover:not(:disabled) {
  background: #1976d2;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.btn-buy {
  background: #1565c0;
  color: white;
}

.btn-buy:hover:not(:disabled) {
  background: #0d47a1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(21, 101, 192, 0.3);
}

.btn-add-to-cart:disabled,
.btn-buy:disabled {
  background: #ccc;
  color: #999;
  cursor: not-allowed;
  opacity: 0.7;
  box-shadow: none;
}

/* 右侧内容区域 */
.description-section,
.specifications-section,
.basic-info,
.comments-section {
  padding: 25px;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04);
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  color: #3498db;
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.section-icon {
  font-size: 20px;
}

.description-content p {
  line-height: 1.7;
  color: #333;
  font-size: 15px;
  margin: 0;
}

.comments-loading,
.comments-empty {
  color: #666;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e3f2fd;
  color: #1565c0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.comment-user {
  font-weight: 700;
  color: #182848;
}

.comment-stars .star {
  color: #ccc;
}

.comment-stars .star.active {
  color: #f6a623;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-text {
  color: #333;
  line-height: 1.6;
}

/* 规格信息 */
.specs-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.spec-row {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.spec-row:last-child {
  border-bottom: none;
}

.spec-label {
  font-weight: 600;
  color: #3498db;
  min-width: 80px;
  font-size: 14px;
  flex-shrink: 0;
}

.spec-value {
  color: #333;
  flex: 1;
  font-size: 14px;
}

/* 基本信息 */
.info-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-icon {
  font-size: 16px;
  width: 20px;
  flex-shrink: 0;
}

.info-label {
  font-weight: 600;
  color: #666;
  min-width: 70px;
  font-size: 14px;
  flex-shrink: 0;
}

.info-value {
  color: #333;
  flex: 1;
  font-size: 14px;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stars {
  color: #ffb300;
  font-size: 16px;
  letter-spacing: 1px;
}

.stock-available {
  color: #4caf50;
  font-weight: 600;
}

.stock-low {
  color: #ff9800;
  font-weight: 600;
}

.low-stock-text {
  font-size: 12px;
  color: #ff9800;
  margin-left: 5px;
}

.sold-out-text {
  font-size: 12px;
  color: #f44336;
  margin-left: 5px;
}

/* 未找到商品 */
.not-found {
  text-align: center;
  padding: 100px 20px;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 12px;
  margin: 20px;
}

.empty-state {
  max-width: 400px;
  margin: 0 auto;
}

.empty-icon {
  font-size: 50px;
  margin-bottom: 20px;
  color: #999;
}

.not-found h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.not-found p {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.btn-back-to-list {
  background: #42a5f5;
  color: white;
  border: none;
  padding: 12px 25px;
  cursor: pointer;
  font-size: 15px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  border-radius: 0;
  font-weight: 600;
}

.btn-back-to-list:hover {
  background: #3498db;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 165, 245, 0.3);
}

/* 通知样式 */
.green-notification {
  position: fixed;
  top: 100px;
  right: 30px;
  background: linear-gradient(135deg, #42a5f5 0%, #3498db 100%);
  color: white;
  padding: 15px 25px;
  box-shadow: 0 8px 30px rgba(66, 165, 245, 0.4);
  z-index: 10000;
  transform: translateX(120%);
  transition: transform 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 0;
}

.green-notification.show {
  transform: translateX(0);
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 500;
}

.notification-icon {
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .detail-content {
    flex-direction: column;
  }

  .left-column {
    min-width: 100%;
  }

  .title-section h1 {
    font-size: 24px;
  }

  .price-tag {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .product-detail {
    padding: 0;
    margin: 0;
  }

  .detail-container {
    margin: 0;
    border-radius: 0;
  }

  .detail-content {
    padding: 20px;
    gap: 20px;
  }

  .header-top {
    padding: 12px 20px;
  }

  .back-btn-header {
    padding: 6px 12px;
    font-size: 13px;
  }

  .header-content {
    padding: 20px;
  }

  .title-section h1 {
    font-size: 20px;
  }

  .product-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .meta-group {
    width: 100%;
    flex-direction: column;
    gap: 10px;
  }

  .meta-item {
    width: 100%;
    justify-content: space-between;
  }

  .image-container {
    height: 300px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .price-box {
    padding: 20px;
  }

  .price-value {
    font-size: 28px;
  }

  .description-section,
  .specifications-section,
  .basic-info {
    padding: 20px;
  }

  .section-header h3 {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .product-detail {
    padding: 0;
  }

  .header-top {
    padding: 10px;
  }

  .header-content {
    padding: 15px;
  }

  .title-section h1 {
    font-size: 18px;
  }

  .detail-content {
    padding: 15px;
  }

  .image-container {
    height: 250px;
  }

  .price-box {
    padding: 15px;
  }

  .price-value {
    font-size: 24px;
  }

  .btn-add-to-cart, .btn-buy {
    padding: 14px;
    font-size: 15px;
  }
}

/* 结算弹窗样式（复用购物车风格） */
.checkout-dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.checkout-dialog {
  width: 520px;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.18);
  overflow: hidden;
  animation: fadeIn 0.2s ease;
}

.dialog-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-header h2 {
  margin: 0;
  font-size: 18px;
  color: #182848;
}

.close-btn {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
}

.dialog-content {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-group label {
  font-weight: 600;
  color: #182848;
}

.form-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #d0ddff;
  border-radius: 4px;
  background: #f8fbff;
}

.dialog-footer {
  padding: 14px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel, .btn-confirm {
  padding: 10px 18px;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  font-weight: 600;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-confirm {
  background: #1565c0;
  color: #fff;
}

.btn-confirm:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.required {
  color: #e91e63;
}
</style>