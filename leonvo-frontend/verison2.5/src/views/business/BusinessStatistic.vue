<template>
  <div class="merchant-statistics">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>销售统计</h2>
      <div class="header-actions">
        <select v-model="dateRange" class="filter-select">
          <option value="week">最近7天</option>
          <option value="month">最近30天</option>
          <option value="quarter">最近3个月</option>
          <option value="year">今年</option>
          <option value="custom">自定义</option>
        </select>

        <div v-if="dateRange === 'custom'" class="custom-date">
          <input type="date" v-model="startDate">
          <span>至</span>
          <input type="date" v-model="endDate">
        </div>

        <button class="btn-export" @click="exportStatistics">
          <span class="export-icon">📥</span>
          导出报表
        </button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="overview-card">
        <div class="overview-icon sales">
          <span>💰</span>
        </div>
        <div class="overview-content">
          <div class="overview-value">¥{{ formatNumber(stats.total_sales) }}</div>
          <div class="overview-label">总销售额</div>
          <div class="overview-change" :class="getChangeClass(stats.sales_change)">
            {{ stats.sales_change > 0 ? '+' : '' }}{{ stats.sales_change }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon orders">
          <span>📦</span>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ stats.total_orders }}</div>
          <div class="overview-label">总订单数</div>
          <div class="overview-change" :class="getChangeClass(stats.orders_change)">
            {{ stats.orders_change > 0 ? '+' : '' }}{{ stats.orders_change }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon avg">
          <span>📊</span>
        </div>
        <div class="overview-content">
          <div class="overview-value">¥{{ stats.avg_order_value }}</div>
          <div class="overview-label">客单价</div>
          <div class="overview-change" :class="getChangeClass(stats.avg_change)">
            {{ stats.avg_change > 0 ? '+' : '' }}{{ stats.avg_change }}%
          </div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon customers">
          <span>👥</span>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ stats.total_customers }}</div>
          <div class="overview-label">顾客数</div>
          <div class="overview-change" :class="getChangeClass(stats.customers_change)">
            {{ stats.customers_change > 0 ? '+' : '' }}{{ stats.customers_change }}%
          </div>
        </div>
      </div>
    </div>

    <!-- 销售趋势图表 -->
    <div class="chart-section">
      <div class="chart-card">
        <div class="chart-header">
          <h3>销售趋势</h3>
          <div class="chart-actions">
            <select v-model="chartType" class="chart-select">
              <option value="line">折线图</option>
              <option value="bar">柱状图</option>
            </select>
            <select v-model="dataType" class="chart-select">
              <option value="sales">销售额</option>
              <option value="orders">订单数</option>
              <option value="customers">顾客数</option>
            </select>
          </div>
        </div>
        <div class="chart-container">
          <canvas ref="trendChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 销售排行 -->
    <div class="ranking-section">
      <div class="ranking-card">
        <div class="ranking-header">
          <h3>商品销售排行</h3>
          <button class="btn-view-all" @click="viewAllProducts">查看全部</button>
        </div>
        <div class="ranking-table">
          <table>
            <thead>
            <tr>
              <th>排名</th>
              <th>商品</th>
              <th>销售额</th>
              <th>订单数</th>
              <th>占比</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(product, index) in productRankings" :key="product.id">
              <td>
                  <span :class="['rank-badge', getRankClass(index + 1)]">
                    {{ index + 1 }}
                  </span>
              </td>
              <td>
                <div class="product-info">
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-sku">{{ product.sku }}</div>
                </div>
              </td>
              <td>
                <div class="sales-amount">¥{{ product.sales_amount }}</div>
              </td>
              <td>
                <div class="orders-count">{{ product.orders_count }}</div>
              </td>
              <td>
                <div class="percentage">
                  <div class="percentage-bar">
                    <div
                        class="percentage-fill"
                        :style="{ width: product.percentage + '%' }"
                    ></div>
                  </div>
                  <span class="percentage-value">{{ product.percentage }}%</span>
                </div>
              </td>
            </tr>
            <tr v-if="productRankings.length === 0">
              <td colspan="5" class="no-data">暂无销售数据</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="ranking-card">
        <div class="ranking-header">
          <h3>订单状态分布</h3>
        </div>
        <div class="distribution-chart">
          <canvas ref="distributionChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 详细数据表格 -->
    <div class="detail-section">
      <div class="detail-header">
        <h3>详细销售数据</h3>
        <div class="date-range-info">
          {{ formatDateRange() }}
        </div>
      </div>
      <div class="detail-table">
        <table>
          <thead>
          <tr>
            <th>日期</th>
            <th>销售额</th>
            <th>订单数</th>
            <th>顾客数</th>
            <th>客单价</th>
            <th>转化率</th>
            <th>详情</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="day in dailyData" :key="day.date">
            <td>{{ day.date }}</td>
            <td>¥{{ day.sales }}</td>
            <td>{{ day.orders }}</td>
            <td>{{ day.customers }}</td>
            <td>¥{{ day.avg_order_value }}</td>
            <td>{{ day.conversion_rate }}%</td>
            <td>
              <button class="btn-detail" @click="viewDayDetail(day)">
                查看
              </button>
            </td>
          </tr>
          <tr v-if="dailyData.length === 0">
            <td colspan="7" class="no-data">暂无数据</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 日期详情模态框 -->
    <div v-if="selectedDay" class="modal-overlay" @click.self="selectedDay = null">
      <div class="modal modal-lg">
        <div class="modal-header">
          <h3>{{ selectedDay.date }} 销售详情</h3>
          <button class="modal-close" @click="selectedDay = null">×</button>
        </div>
        <div class="modal-body">
          <div class="day-detail">
            <div class="day-stats">
              <div class="stat-item">
                <div class="stat-value">¥{{ selectedDay.sales }}</div>
                <div class="stat-label">销售额</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ selectedDay.orders }}</div>
                <div class="stat-label">订单数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ selectedDay.customers }}</div>
                <div class="stat-label">顾客数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">¥{{ selectedDay.avg_order_value }}</div>
                <div class="stat-label">客单价</div>
              </div>
            </div>

            <div class="day-orders" v-if="selectedDay.order_details">
              <h4>当日订单</h4>
              <div class="orders-list">
                <div v-for="order in selectedDay.order_details" :key="order.id" class="order-item">
                  <div class="order-id">#{{ order.id }}</div>
                  <div class="order-amount">¥{{ order.amount }}</div>
                  <div class="order-status">{{ getStatusText(order.status) }}</div>
                  <div class="order-time">{{ order.time }}</div>
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
import Chart from 'chart.js/auto';
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'BusinessStatistics',
  data() {
    const today = new Date();
    const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);

    return {
      loading: false,
      dateRange: 'week',
      startDate: weekAgo.toISOString().split('T')[0],
      endDate: today.toISOString().split('T')[0],
      chartType: 'line',
      dataType: 'sales',

      stats: {
        total_sales: 0,
        sales_change: 0,
        total_orders: 0,
        orders_change: 0,
        avg_order_value: 0,
        avg_change: 0,
        total_customers: 0,
        customers_change: 0
      },

      productRankings: [],
      dailyData: [],
      selectedDay: null,

      trendChart: null,
      distributionChart: null
    }
  },
  mounted() {
    this.loadStatistics();
  },
  beforeDestroy() {
    if (this.trendChart) {
      this.trendChart.destroy();
    }
    if (this.distributionChart) {
      this.distributionChart.destroy();
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

    getChangeClass(change) {
      if (change > 0) return 'positive';
      if (change < 0) return 'negative';
      return 'neutral';
    },

    getRankClass(rank) {
      if (rank === 1) return 'first';
      if (rank === 2) return 'second';
      if (rank === 3) return 'third';
      return 'other';
    },

    formatDateRange() {
      const start = new Date(this.startDate);
      const end = new Date(this.endDate);

      if (this.dateRange === 'custom') {
        return `${start.toLocaleDateString('zh-CN')} - ${end.toLocaleDateString('zh-CN')}`;
      }

      const ranges = {
        'week': '最近7天',
        'month': '最近30天',
        'quarter': '最近3个月',
        'year': '今年'
      };

      return ranges[this.dateRange] || '最近7天';
    },

    // 获取商家ID
    getBusinessId() {
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      if (!businessInfo) return null;
      return businessInfo.bid || businessInfo.id;
    },

    // 修改loadStatistics方法，使用apiConfig
    async loadStatistics() {
      this.loading = true;
      const businessInfo = JSON.parse(localStorage.getItem('businessInfo'));
      const bid = businessInfo?.bid || businessInfo?.id;

      if (!bid) {
        console.error('未找到商家信息');
        this.$router.push('/business/login');
        return;
      }

      try {
        // 1. 获取商品数据 - 使用apiConfig
        const productsResponse = await axios.get(apiConfig.business.getProducts(bid));
        let products = [];
        if (productsResponse.data.code === 1) {
          products = productsResponse.data.data || [];
        }

        // 2. 获取订单数据 - 使用apiConfig
        const ordersResponse = await axios.get(apiConfig.business.getOrders(bid));
        let orders = [];
        if (ordersResponse.data.code === 1) {
          orders = ordersResponse.data.data || [];
        }

        // 3. 计算统计概览
        const totalSales = orders.reduce((sum, order) => sum + (order.amount || 0), 0);
        const totalOrders = orders.length;
        const avgOrderValue = totalOrders > 0 ? (totalSales / totalOrders).toFixed(2) : 0;

        // 计算变化率（需要历史数据，这里使用模拟数据）
        this.stats = {
          total_sales: totalSales,
          sales_change: 12.5, // 模拟数据
          total_orders: totalOrders,
          orders_change: 8.3, // 模拟数据
          avg_order_value: avgOrderValue,
          avg_change: 5.2, // 模拟数据
          total_customers: new Set(orders.map(order => order.uid)).size,
          customers_change: 15.0 // 模拟数据
        };

        // 4. 获取销售趋势
        const trendData = await this.calculateSalesTrend(bid, 7);
        this.renderTrendChart(trendData);

        // 5. 获取商品排行
        this.productRankings = products
            .filter(p => p.status === 1)
            .sort((a, b) => (b.sales_count || 0) - (a.sales_count || 0))
            .slice(0, 10)
            .map((product, index) => ({
              id: product.pid,
              name: product.name,
              sku: `SKU${product.pid}`,
              sales_amount: (product.price || 0) * (product.sales_count || 0),
              orders_count: product.sales_count || 0,
              percentage: 0
            }));

        // 计算占比
        const totalProductSales = this.productRankings.reduce((sum, p) => sum + p.sales_amount, 0);
        this.productRankings = this.productRankings.map(p => ({
          ...p,
          percentage: totalProductSales > 0 ? ((p.sales_amount / totalProductSales) * 100).toFixed(1) : '0.0'
        }));

        // 6. 生成订单状态分布
        const statusDistribution = {
          labels: ['待付款', '待发货', '已发货', '已完成', '已取消'],
          values: [0, 0, 0, 0, 0]
        };

        orders.forEach(order => {
          if (order.status >= 0 && order.status <= 4) {
            statusDistribution.values[order.status]++;
          }
        });
        this.renderDistributionChart(statusDistribution);

        // 7. 生成每日数据
        this.generateDailyData(orders);

      } catch (error) {
        console.error('加载统计数据失败:', error);
        this.useMockData();
      } finally {
        this.loading = false;
      }
    },

    // 修改generateDailyData方法
    generateDailyData(orders) {
      const today = new Date();
      this.dailyData = [];

      // 获取最近7天的数据
      for (let i = 6; i >= 0; i--) {
        const date = new Date(today.getTime() - i * 24 * 60 * 60 * 1000);
        const dayStr = date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' });

        // 过滤当天的订单
        const dayOrders = orders.filter(order => {
          const orderDate = new Date(order.orderTime);
          return orderDate.toDateString() === date.toDateString();
        });

        const sales = dayOrders.reduce((sum, order) => sum + (order.amount || 0), 0);
        const customers = new Set(dayOrders.map(order => order.uid)).size;

        this.dailyData.push({
          date: dayStr,
          sales: sales,
          orders: dayOrders.length,
          customers: customers,
          avg_order_value: dayOrders.length > 0 ? Math.round(sales / dayOrders.length) : 0,
          conversion_rate: (Math.random() * 20 + 10).toFixed(1) // 模拟转化率
        });
      }
    },

    renderTrendChart(data) {
      const ctx = this.$refs.trendChart;
      if (!ctx) return;

      if (this.trendChart) {
        this.trendChart.destroy();
      }

      const chartType = this.chartType === 'bar' ? 'bar' : 'line';

      let dataSet;
      if (this.dataType === 'sales' && data.sales) {
        dataSet = {
          label: '销售额',
          data: data.sales,
          borderColor: '#667eea',
          backgroundColor: chartType === 'bar' ? '#667eea' : 'rgba(102, 126, 234, 0.1)',
          yAxisID: 'y'
        };
      } else if (this.dataType === 'orders' && data.orders) {
        dataSet = {
          label: '订单数',
          data: data.orders,
          borderColor: '#ed8936',
          backgroundColor: chartType === 'bar' ? '#ed8936' : 'rgba(237, 137, 54, 0.1)',
          yAxisID: 'y1'
        };
      } else if (this.dataType === 'customers' && data.customers) {
        dataSet = {
          label: '顾客数',
          data: data.customers,
          borderColor: '#48bb78',
          backgroundColor: chartType === 'bar' ? '#48bb78' : 'rgba(72, 187, 120, 0.1)',
          yAxisID: 'y1'
        };
      } else {
        // 默认显示销售额
        dataSet = {
          label: '销售额',
          data: data.values || data.sales || [0, 0, 0, 0, 0, 0, 0],
          borderColor: '#667eea',
          backgroundColor: chartType === 'bar' ? '#667eea' : 'rgba(102, 126, 234, 0.1)',
          yAxisID: 'y'
        };
      }

      const chartData = {
        labels: data.labels || data.dates || ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        datasets: [{
          ...dataSet,
          borderWidth: chartType === 'bar' ? 0 : 2,
          fill: chartType !== 'bar',
          tension: 0.4
        }]
      };

      this.trendChart = new Chart(ctx, {
        type: chartType,
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              display: true,
              position: 'top'
            }
          },
          scales: {
            y: {
              type: 'linear',
              display: this.dataType === 'sales',
              position: 'left',
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
            y1: {
              type: 'linear',
              display: this.dataType !== 'sales',
              position: 'right',
              beginAtZero: true,
              grid: {
                drawOnChartArea: false,
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

    renderDistributionChart(data) {
      const ctx = this.$refs.distributionChart;
      if (!ctx) return;

      if (this.distributionChart) {
        this.distributionChart.destroy();
      }

      const chartData = {
        labels: data.labels || ['待付款', '待发货', '已发货', '已完成', '已取消'],
        datasets: [{
          data: data.values || [0, 0, 0, 0, 0],
          backgroundColor: [
            '#ff9800',
            '#2196f3',
            '#4caf50',
            '#9c27b0',
            '#f44336'
          ],
          borderWidth: 0
        }]
      };

      this.distributionChart = new Chart(ctx, {
        type: 'doughnut',
        data: chartData,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'right'
            }
          },
          cutout: '70%'
        }
      });
    },

    useMockData() {
      // 只有在API调用失败时使用空数据
      this.stats = {
        total_sales: 0,
        sales_change: 0,
        total_orders: 0,
        orders_change: 0,
        avg_order_value: 0,
        avg_change: 0,
        total_customers: 0,
        customers_change: 0
      };

      this.productRankings = [];
      this.dailyData = [];

      // 渲染空图表
      setTimeout(() => {
        this.renderTrendChart({
          labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
          values: [0, 0, 0, 0, 0, 0, 0]
        });

        this.renderDistributionChart({
          labels: ['待付款', '待发货', '已发货', '已完成', '已取消'],
          values: [0, 0, 0, 0, 0]
        });
      }, 100);
    },

    viewAllProducts() {
      this.$router.push('/business/products');
    },

    viewDayDetail(day) {
      // 这里可以调用API获取当日详情
      this.selectedDay = {
        ...day,
        order_details: [] // 实际项目中应该从API获取
      };
    },

    exportStatistics() {
      // 导出统计报表
      const data = {
        统计时间段: this.formatDateRange(),
        总销售额: `¥${this.formatNumber(this.stats.total_sales)}`,
        总订单数: this.stats.total_orders,
        总顾客数: this.stats.total_customers,
        客单价: `¥${this.stats.avg_order_value}`,
        销售额变化: `${this.stats.sales_change > 0 ? '+' : ''}${this.stats.sales_change}%`,
        订单变化: `${this.stats.orders_change > 0 ? '+' : ''}${this.stats.orders_change}%`
      };

      const csvContent = "data:text/csv;charset=utf-8,"
          + Object.entries(data).map(([key, value]) => `${key},${value}`).join("\n");

      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `销售统计_${new Date().toLocaleDateString()}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  },
  watch: {
    dateRange() {
      const today = new Date();
      let startDate;

      switch (this.dateRange) {
        case 'week':
          startDate = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
          break;
        case 'month':
          startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);
          break;
        case 'quarter':
          startDate = new Date(today.getTime() - 90 * 24 * 60 * 60 * 1000);
          break;
        case 'year':
          startDate = new Date(today.getFullYear(), 0, 1);
          break;
        default:
          return;
      }

      this.startDate = startDate.toISOString().split('T')[0];
      this.endDate = today.toISOString().split('T')[0];

      if (this.dateRange !== 'custom') {
        this.loadStatistics();
      }
    },

    startDate() {
      if (this.dateRange === 'custom') {
        this.loadStatistics();
      }
    },

    endDate() {
      if (this.dateRange === 'custom') {
        this.loadStatistics();
      }
    },

    chartType() {
      if (this.trendChart) {
        this.loadStatistics();
      }
    },

    dataType() {
      if (this.trendChart) {
        this.loadStatistics();
      }
    }
  }
}
</script>

<style scoped>
.merchant-statistics {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #2d3748;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  color: #4a5568;
  font-size: 14px;
  min-width: 120px;
}

.custom-date {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-date input {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
}

.custom-date span {
  color: #718096;
  font-size: 14px;
}

.btn-export {
  padding: 10px 20px;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: background 0.3s;
}

.btn-export:hover {
  background: #38a169;
}

.export-icon {
  font-size: 16px;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
}

.overview-card:hover {
  transform: translateY(-4px);
}

.overview-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.overview-icon.sales {
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: white;
}

.overview-icon.orders {
  background: linear-gradient(135deg, #ed8936, #dd6b20);
  color: white;
}

.overview-icon.avg {
  background: linear-gradient(135deg, #9f7aea, #805ad5);
  color: white;
}

.overview-icon.customers {
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
}

.overview-content {
  flex: 1;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 4px;
}

.overview-label {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}

.overview-change {
  font-size: 13px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 12px;
  display: inline-block;
}

.overview-change.positive {
  background: #c6f6d5;
  color: #22543d;
}

.overview-change.negative {
  background: #fed7d7;
  color: #c53030;
}

.overview-change.neutral {
  background: #e2e8f0;
  color: #4a5568;
}

.chart-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
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

.chart-actions {
  display: flex;
  gap: 12px;
}

.chart-select {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 14px;
}

.chart-container {
  height: 400px;
  position: relative;
}

.ranking-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

@media (max-width: 1024px) {
  .ranking-section {
    grid-template-columns: 1fr;
  }
}

.ranking-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.ranking-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.btn-view-all {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.btn-view-all:hover {
  background: #5a67d8;
}

.ranking-table {
  overflow-x: auto;
}

.ranking-table table {
  width: 100%;
  border-collapse: collapse;
}

.ranking-table th {
  background: #f8fafc;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.ranking-table td {
  padding: 12px;
  border-bottom: 1px solid #f7fafc;
  vertical-align: middle;
}

.ranking-table tbody tr:hover {
  background: #f8fafc;
}

.rank-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
}

.rank-badge.first {
  background: #f6e05e;
  color: #744210;
}

.rank-badge.second {
  background: #e2e8f0;
  color: #4a5568;
}

.rank-badge.third {
  background: #fed7d7;
  color: #c53030;
}

.rank-badge.other {
  background: #f7fafc;
  color: #718096;
}

.product-info {
  max-width: 200px;
}

.product-name {
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 2px;
  line-height: 1.4;
}

.product-sku {
  font-size: 12px;
  color: #718096;
}

.sales-amount {
  font-weight: 600;
  color: #667eea;
}

.orders-count {
  font-weight: 500;
  color: #4a5568;
}

.percentage {
  display: flex;
  align-items: center;
  gap: 12px;
}

.percentage-bar {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.percentage-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
}

.percentage-value {
  font-size: 13px;
  color: #718096;
  min-width: 40px;
}

.distribution-chart {
  height: 300px;
  position: relative;
}

.detail-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
  font-weight: 600;
}

.date-range-info {
  color: #718096;
  font-size: 14px;
}

.detail-table {
  overflow-x: auto;
}

.detail-table table {
  width: 100%;
  border-collapse: collapse;
}

.detail-table th {
  background: #f8fafc;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.detail-table td {
  padding: 12px;
  border-bottom: 1px solid #f7fafc;
}

.detail-table tbody tr:hover {
  background: #f8fafc;
}

.btn-detail {
  padding: 6px 12px;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-detail:hover {
  background: #cbd5e0;
}

.no-data {
  text-align: center;
  color: #a0aec0;
  padding: 40px 0;
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
  border-radius: 12px;
}

.modal-lg {
  max-width: 800px;
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
  border-radius: 6px;
  transition: background 0.3s;
}

.modal-close:hover {
  background: #f7fafc;
}

.modal-body {
  padding: 20px;
}

.day-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.day-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
}

.day-orders h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #2d3748;
  font-weight: 600;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.order-id {
  font-weight: 600;
  color: #2d3748;
  flex: 1;
}

.order-amount {
  font-weight: 700;
  color: #667eea;
  flex: 1;
}

.order-status {
  padding: 4px 12px;
  background: #c6f6d5;
  color: #22543d;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  flex: 1;
  text-align: center;
}

.order-time {
  color: #718096;
  font-size: 14px;
  flex: 1;
  text-align: right;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .custom-date {
    flex-direction: column;
    align-items: stretch;
  }

  .stats-overview {
    grid-template-columns: 1fr;
  }

  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-id, .order-amount, .order-status, .order-time {
    flex: none;
    width: 100%;
  }

  .order-time {
    text-align: left;
  }
}
</style>