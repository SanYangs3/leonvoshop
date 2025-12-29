<template>
  <div class="business-dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <!-- 总销售额 -->
      <div class="stat-card">
        <div class="stat-icon sales">
          <span>💰</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ formatNumber(dashboardData.totalSales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>

      <!-- 今日销售额 -->
      <div class="stat-card">
        <div class="stat-icon today-sales">
          <span>📈</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ formatNumber(dashboardData.todaySales) }}</div>
          <div class="stat-label">今日销售额</div>
        </div>
      </div>

      <!-- 在售商品 -->
      <div class="stat-card">
        <div class="stat-icon products">
          <span>📦</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.activeProducts }}</div>
          <div class="stat-label">在售商品</div>
        </div>
      </div>

      <!-- 待处理订单 -->
      <div class="stat-card">
        <div class="stat-icon orders">
          <span>📋</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.pendingOrders }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-area">
      <div class="chart-card">
        <div class="chart-header">
          <h3>近7日销售趋势</h3>
          <select v-model="salesRange" class="time-select">
            <option value="7">近7天</option>
            <option value="30">近30天</option>
          </select>
        </div>
        <div class="chart-container">
          <div v-if="loading" class="loading-chart">
            加载中...
          </div>
          <div v-else class="sales-chart">
            <canvas ref="salesChart"></canvas>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-header">
          <h3>热门商品</h3>
        </div>
        <div class="chart-container">
          <div v-if="loading" class="loading-chart">
            加载中...
          </div>
          <div v-else class="products-list">
            <div v-for="product in topProducts" :key="product.pid" class="product-item">
              <div class="product-icon">💻</div>
              <div class="product-info">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-sales">销量: {{ product.sales_count || 0 }}件</div>
              </div>
              <div class="product-amount">¥{{ product.price }}</div>
            </div>
            <div v-if="topProducts.length === 0" class="empty-products">
              暂无销售数据
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="recent-orders">
      <div class="section-header">
        <h3>最近订单</h3>
        <button class="view-all-btn" @click="goToOrders">查看全部</button>
      </div>
      <div class="orders-table">
        <table>
          <thead>
          <tr>
            <th>订单号</th>
            <th>商品</th>
            <th>金额</th>
            <th>状态</th>
            <th>时间</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="order in recentOrders" :key="order.oid">
            <td>#{{ order.oid }}</td>
            <td>
              <div class="order-product">
                {{ order.items?.[0]?.name || '商品' }}
                <span v-if="order.items?.[0]?.quantity > 1" class="quantity">×{{ order.items[0].quantity }}</span>
              </div>
            </td>
            <td>¥{{ order.amount }}</td>
            <td>
                <span :class="['status-badge', getOrderStatusClass(order.status)]">
                  {{ getOrderStatusText(order.status) }}
                </span>
            </td>
            <td>{{ formatTime(order.order_time) }}</td>
          </tr>
          <tr v-if="recentOrders.length === 0">
            <td colspan="5" class="no-orders">暂无订单</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import axios from 'axios';
import apiConfig from '@/config/api.config';

Chart.register(...registerables);

export default {
  name: 'BusinessDashboard',
  data() {
    return {
      loading: true,
      salesRange: '7',
      dashboardData: {
        totalSales: 0,
        todaySales: 0,
        activeProducts: 0,
        pendingOrders: 0
      },
      salesChart: null,
      topProducts: [],
      recentOrders: []
    }
  },
  mounted() {
    this.loadDashboardData();
  },
  beforeDestroy() {
    if (this.salesChart) {
      this.salesChart.destroy();
    }
  },
  methods: {
    formatNumber(num) {
      if (!num) return '0';
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
      }
      return num.toLocaleString();
    },

    formatTime(time) {
      if (!time) return '';
      try {
        const date = new Date(time);
        return date.toLocaleDateString('zh-CN', {
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        return time;
      }
    },

    getOrderStatusClass(status) {
      const statusMap = {
        0: 'pending',
        1: 'pending',
        2: 'shipped',
        3: 'completed',
        4: 'cancelled'
      };
      return statusMap[status] || 'pending';
    },

    getOrderStatusText(status) {
      const statusMap = {
        0: '待付款',
        1: '待发货',
        2: '已发货',
        3: '已完成',
        4: '已取消'
      };
      return statusMap[status] || '未知状态';
    },

    async loadDashboardData() {
      this.loading = true;
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      const bid = businessInfo?.bid || businessInfo?.id;

      if (!bid) {
        console.error('未找到商家信息');
        this.$router.push('/business/login');
        return;
      }

      try {
        // 1. 获取商品数据 - 使用api.config.js中的配置
        const productsResponse = await axios.get(apiConfig.business.getProducts(bid));
        let products = [];
        if (productsResponse.data.code === 1) {
          products = productsResponse.data.data || [];
        }

        // 2. 获取订单数据 - 使用api.config.js中的配置
        const ordersResponse = await axios.get(apiConfig.business.getOrders(bid));
        let orders = [];
        if (ordersResponse.data.code === 1) {
          orders = ordersResponse.data.data || [];
        }

        // 3. 计算统计信息
        const totalSales = orders.reduce((sum, order) => sum + (order.amount || 0), 0);

        // 计算今日销售额
        const today = new Date();
        const todayStr = today.toISOString().split('T')[0];
        const todaySales = orders
            .filter(order => {
              const orderDate = new Date(order.order_time || order.orderTime);
              return orderDate.toISOString().split('T')[0] === todayStr;
            })
            .reduce((sum, order) => sum + (order.amount || 0), 0);

        const activeProducts = products.filter(p => p.status === 1).length;
        const pendingOrders = orders.filter(order => order.status === 1).length;

        this.dashboardData = {
          totalSales,
          todaySales,
          activeProducts,
          pendingOrders
        };

        // 4. 获取销售趋势
        const trendData = await this.calculateSalesTrend(bid, parseInt(this.salesRange));
        this.renderSalesChart(trendData);

        // 5. 获取热门商品
        this.topProducts = products
            .filter(p => p.status === 1)
            .sort((a, b) => (b.sales_count || 0) - (a.sales_count || 0))
            .slice(0, 4)
            .map(p => ({
              pid: p.pid,
              name: p.name,
              sales_count: p.sales_count || 0,
              price: p.price || 0
            }));

        // 6. 获取最近订单
        this.recentOrders = orders
            .sort((a, b) => {
              const timeA = new Date(a.order_time || a.orderTime);
              const timeB = new Date(b.order_time || b.orderTime);
              return timeB - timeA;
            })
            .slice(0, 5)
            .map(order => ({
              oid: order.oid,
              amount: order.amount || 0,
              status: order.status || 0,
              order_time: order.order_time || order.orderTime,
              items: order.items || [{ name: order.product_name || '商品', quantity: 1 }]
            }));

      } catch (error) {
        console.error('加载仪表盘数据失败:', error);
        this.useMockData();
      } finally {
        this.loading = false;
      }
    },

    async calculateSalesTrend(bid, days) {
      try {
        // 获取订单数据
        const ordersResponse = await axios.get(apiConfig.business.getOrders(bid));
        let orders = [];
        if (ordersResponse.data.code === 1) {
          orders = ordersResponse.data.data || [];
        }

        // 生成最近days天的销售数据
        const labels = [];
        const sales = [];
        const ordersCount = [];

        const today = new Date();
        for (let i = days - 1; i >= 0; i--) {
          const date = new Date(today);
          date.setDate(date.getDate() - i);
          const dateStr = `${date.getMonth() + 1}/${date.getDate()}`;
          labels.push(dateStr);

          // 过滤当天的订单
          const dayOrders = orders.filter(order => {
            const orderDate = new Date(order.order_time || order.orderTime);
            return orderDate.toDateString() === date.toDateString();
          });

          const daySales = dayOrders.reduce((sum, order) => sum + (order.amount || 0), 0);
          sales.push(daySales);
          ordersCount.push(dayOrders.length);
        }

        return {
          labels,
          sales,
          orders: ordersCount
        };
      } catch (error) {
        console.error('计算销售趋势失败:', error);
        return this.getMockTrendData(days);
      }
    },

    renderSalesChart(data) {
      const ctx = this.$refs.salesChart;
      if (!ctx) return;

      if (this.salesChart) {
        this.salesChart.destroy();
      }

      this.salesChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: data.labels || ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          datasets: [{
            label: '销售额',
            data: data.values || data.sales || [1200, 1900, 3000, 5000, 2000, 3000, 4500],
            borderColor: '#667eea',
            backgroundColor: 'rgba(102, 126, 234, 0.1)',
            borderWidth: 2,
            fill: true,
            tension: 0.4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              },
              ticks: {
                callback: function(value) {
                  return '¥' + value;
                }
              }
            },
            x: {
              grid: {
                color: 'rgba(0, 0, 0, 0.05)'
              }
            }
          }
        }
      });
    },

    getMockTrendData(days) {
      const labels = [];
      const sales = [];

      for (let i = days - 1; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        labels.push(`${date.getMonth() + 1}/${date.getDate()}`);
        sales.push(Math.floor(Math.random() * 5000) + 1000);
      }

      return { labels, sales };
    },

    useMockData() {
      // 只有在API调用失败时使用模拟数据
      this.dashboardData = {
        totalSales: 0,
        todaySales: 0,
        activeProducts: 0,
        pendingOrders: 0
      };
      this.topProducts = [];
      this.recentOrders = [];

      this.$nextTick(() => {
        this.renderSalesChart({
          labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          values: [0, 0, 0, 0, 0, 0, 0]
        });
      });
    },

    goToOrders() {
      this.$router.push('/business/orders');
    }
  },
  watch: {
    salesRange() {
      this.loadDashboardData();
    }
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.business-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.stat-icon.sales {
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: white;
}

.stat-icon.today-sales {
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
}

.stat-icon.products {
  background: linear-gradient(135deg, #ed8936, #dd6b20);
  color: white;
}

.stat-icon.orders {
  background: linear-gradient(135deg, #9f7aea, #805ad5);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
}

.chart-area {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

@media (max-width: 1024px) {
  .chart-area {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.time-select {
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 14px;
}

.chart-container {
  height: 300px;
}

.loading-chart {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #718096;
}

.sales-chart {
  height: 100%;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  transition: background 0.3s;
}

.product-item:hover {
  background: #edf2f7;
}

.product-icon {
  font-size: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 8px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 2px;
}

.product-sales {
  font-size: 12px;
  color: #718096;
}

.product-amount {
  font-weight: 600;
  color: #667eea;
}

.empty-products {
  text-align: center;
  color: #a0aec0;
  padding: 40px 0;
}

.recent-orders {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.view-all-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.view-all-btn:hover {
  background: #5a67d8;
}

.orders-table {
  overflow-x: auto;
}

.orders-table table {
  width: 100%;
  border-collapse: collapse;
}

.orders-table th {
  background: #f8fafc;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.orders-table td {
  padding: 12px;
  border-bottom: 1px solid #f7fafc;
}

.orders-table tbody tr:hover {
  background: #f8fafc;
}

.order-product {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.quantity {
  font-size: 12px;
  color: #718096;
}

.status-badge {
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
  font-weight: 500;
}

.status-badge.pending {
  background: #fffaf0;
  color: #c05621;
}

.status-badge.shipped {
  background: #ebf8ff;
  color: #2b6cb0;
}

.status-badge.completed {
  background: #f0fff4;
  color: #276749;
}

.status-badge.cancelled {
  background: #fff5f5;
  color: #c53030;
}

.no-orders {
  text-align: center;
  color: #a0aec0;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .orders-table {
    font-size: 14px;
  }
}
</style>