<template>
  <div class="admin-dashboard">
    <!-- 第一排统计卡片（深色系） -->
    <div class="top-stats-row">
      <!-- 总销售额 -->
      <div class="stat-card large dark-card sales-card">
        <div class="stat-icon">
          <i class="iconfont">￥</i>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ formatNumber(dashboardData.totalSales.value) }}</div>
          <div class="stat-label">总销售额</div>
          <div v-if="dashboardData.totalSales.isMock" class="mock-tag">【模拟数据】</div>
          <div v-if="dashboardData.totalSales.trend" class="stat-trend" :class="dashboardData.totalSales.trend.type">
            {{ dashboardData.totalSales.trend.value }}
          </div>
        </div>
      </div>

      <!-- 总用户数 -->
      <div class="stat-card large dark-card users-card">
        <div class="stat-icon">
          <i class="iconfont">👤</i>
        </div>
        <div class="stat-content">
          <div v-if="!dashboardData.totalUsers.noApi" class="stat-value">{{ formatNumber(dashboardData.totalUsers.value) }}</div>
          <div v-else class="stat-value">--</div>
          <div class="stat-label">总用户数</div>
          <div v-if="dashboardData.totalUsers.noApi" class="no-api-tag">暂无对应API接口</div>
          <div v-else-if="dashboardData.totalUsers.isMock" class="mock-tag">【模拟数据】</div>
        </div>
      </div>

      <!-- 总商品数 -->
      <div class="stat-card large dark-card products-card">
        <div class="stat-icon">
          <i class="iconfont">📦</i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(dashboardData.totalProducts.value) }}</div>
          <div class="stat-label">总商品数</div>
          <div v-if="dashboardData.totalProducts.isMock" class="mock-tag">【模拟数据】</div>
          <div v-if="dashboardData.totalProducts.trend" class="stat-trend" :class="dashboardData.totalProducts.trend.type">
            {{ dashboardData.totalProducts.trend.value }}
          </div>
        </div>
      </div>

      <!-- 总订单数 -->
      <div class="stat-card large dark-card orders-card">
        <div class="stat-icon">
          <i class="iconfont">📊</i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(dashboardData.totalOrders.value) }}</div>
          <div class="stat-label">总订单数</div>
          <div v-if="dashboardData.totalOrders.isMock" class="mock-tag">【模拟数据】</div>
          <div v-if="dashboardData.totalOrders.trend" class="stat-trend" :class="dashboardData.totalOrders.trend.type">
            {{ dashboardData.totalOrders.trend.value }}
          </div>
        </div>
      </div>
    </div>

    <!-- 第二排统计卡片（浅色系） -->
    <div class="today-stats-row">
      <!-- 今日销售额 -->
      <div class="stat-card small light-card today-sales-card">
        <div class="stat-icon-sm">
          <i class="iconfont-sm">💰</i>
        </div>
        <div class="stat-content-sm">
          <div class="stat-value-sm">¥{{ formatNumber(dashboardData.todaySales.value) }}</div>
          <div class="stat-label-sm">今日销售额</div>
          <div v-if="dashboardData.todaySales.isMock" class="mock-tag-sm">【模拟数据】</div>
          <div v-if="dashboardData.todaySales.trend" class="stat-trend-sm" :class="dashboardData.todaySales.trend.type">
            {{ dashboardData.todaySales.trend.value }}
          </div>
        </div>
      </div>

      <!-- 今日新增用户 -->
      <div class="stat-card small light-card new-users-card">
        <div class="stat-icon-sm">
          <i class="iconfont-sm">➕</i>
        </div>
        <div class="stat-content-sm">
          <div v-if="!dashboardData.newUsers.noApi" class="stat-value-sm">{{ dashboardData.newUsers.value }}</div>
          <div v-else class="stat-value-sm">--</div>
          <div class="stat-label-sm">今日新增用户</div>
          <div v-if="dashboardData.newUsers.noApi" class="no-api-tag-sm">暂无API</div>
          <div v-else-if="dashboardData.newUsers.isMock" class="mock-tag-sm">【模拟数据】</div>
        </div>
      </div>

      <!-- 低库存商品 -->
      <div class="stat-card small light-card low-stock-card">
        <div class="stat-icon-sm">
          <i class="iconfont-sm">⚠️</i>
        </div>
        <div class="stat-content-sm">
          <div class="stat-value-sm">{{ dashboardData.lowStockProducts.value }}</div>
          <div class="stat-label-sm">低库存商品</div>
          <div v-if="dashboardData.lowStockProducts.isMock" class="mock-tag-sm">【模拟数据】</div>
        </div>
      </div>

      <!-- 今日订单数 -->
      <div class="stat-card small light-card today-orders-card">
        <div class="stat-icon-sm">
          <i class="iconfont-sm">📈</i>
        </div>
        <div class="stat-content-sm">
          <div class="stat-value-sm">{{ dashboardData.todayOrders.value }}</div>
          <div class="stat-label-sm">今日订单数</div>
          <div v-if="dashboardData.todayOrders.isMock" class="mock-tag-sm">【模拟数据】</div>
          <div v-if="dashboardData.todayOrders.trend" class="stat-trend-sm" :class="dashboardData.todayOrders.trend.type">
            {{ dashboardData.todayOrders.trend.value }}
          </div>
        </div>
      </div>
    </div>

    <!-- 中间图表区域 -->
    <div class="chart-grid">
      <!-- 销售趋势折线图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>销售趋势</h3>
          <div class="chart-actions">
            <div v-if="dashboardData.salesTrend.noApi" class="no-api-info">暂无对应API接口</div>
            <select v-else v-model="salesTimeRange" class="time-select">
              <option value="monthly">月度</option>
              <option value="quarterly">季度</option>
              <option value="yearly">年度</option>
            </select>
          </div>
        </div>
        <div class="chart-container">
          <div v-if="dashboardData.salesTrend.noApi" class="no-api-chart">
            <div class="no-api-placeholder">
              <i class="placeholder-icon">📊</i>
              <div class="placeholder-text">暂无销售趋势数据接口</div>
            </div>
          </div>
          <div v-else class="chart-mock-line">
            <!-- 模拟折线图 -->
            <svg width="100%" height="100%" class="line-chart">
              <!-- 网格线 -->
              <line x1="0" y1="36" x2="100%" y2="36" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="72" x2="100%" y2="72" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="108" x2="100%" y2="108" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="144" x2="100%" y2="144" stroke="#e2e8f0" stroke-width="1"/>

              <!-- 折线 -->
              <path :d="salesLinePath" fill="none" stroke="#4299e1" stroke-width="3" stroke-linecap="round"/>

              <!-- 数据点 -->
              <circle v-for="(point, index) in salesPoints" :key="index"
                      :cx="point.x" :cy="point.y" r="4" fill="#4299e1" stroke="white" stroke-width="2"/>
            </svg>

            <!-- X轴标签 -->
            <div class="x-axis">
              <span v-for="(item, index) in dashboardData.salesTrend.data" :key="index" class="x-label">
                {{ item.month }}
              </span>
            </div>
            <div v-if="dashboardData.salesTrend.isMock" class="chart-mock-tag">【模拟数据】</div>
          </div>
        </div>
      </div>

      <!-- 用户分布地图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>订单地域分布</h3>
          <div v-if="dashboardData.userDistribution.noApi" class="no-api-info">
            暂无对应API接口
          </div>
          <!-- ✅ 移除红黄绿点的图例部分 -->
        </div>



        <div class="chart-container">
          <div v-if="dashboardData.userDistribution.noApi" class="no-api-chart">
            <div class="no-api-placeholder">
              <i class="placeholder-icon">🗺️</i>
              <div class="placeholder-text">暂无订单分布数据接口</div>
            </div>
          </div>
          <div v-else class="map-container">
            <!-- echarts地图容器 -->
            <div id="china-map-chart" style="width: 100%; height: 380px;"></div>
            <div v-if="dashboardData.userDistribution.isMock" class="chart-mock-tag">【模拟数据】</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部区域 -->
    <div class="bottom-grid">
      <!-- 最近活动 -->
      <div class="activity-card">
        <div class="chart-header">
          <h3>最近活动</h3>
          <div v-if="dashboardData.recentActivities.noApi" class="no-api-info">暂无对应API接口</div>
          <button v-else class="refresh-btn" @click="refreshActivities">
            <i class="refresh-icon">↻</i> 刷新
          </button>
        </div>
        <div class="chart-container">
          <div v-if="dashboardData.recentActivities.noApi" class="no-api-list">
            <div class="no-api-placeholder">
              <i class="placeholder-icon">📋</i>
              <div class="placeholder-text">暂无最近活动数据接口</div>
            </div>
          </div>
          <div v-else class="activity-list">
            <div v-for="activity in dashboardData.recentActivities.data" :key="activity.id" class="activity-item">
              <div class="activity-icon" :style="{ backgroundColor: activity.color }">
                {{ getActivityIcon(activity.type) }}
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ activity.text }}</div>
                <div class="activity-time">{{ activity.time }}</div>
              </div>
            </div>
            <div v-if="dashboardData.recentActivities.isMock" class="chart-mock-tag">【模拟数据】</div>
          </div>
        </div>
      </div>

      <!-- 用户增长折线图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户增长趋势</h3>
          <div v-if="dashboardData.userGrowth.noApi" class="no-api-info">暂无对应API接口</div>
          <span v-else class="growth-rate">+{{ calculateGrowthRate() }}%</span>
        </div>
        <div class="chart-container">
          <div v-if="dashboardData.userGrowth.noApi" class="no-api-chart">
            <div class="no-api-placeholder">
              <i class="placeholder-icon">📈</i>
              <div class="placeholder-text">暂无用户增长数据接口</div>
            </div>
          </div>
          <div v-else class="chart-mock-line">
            <svg width="100%" height="180" class="line-chart">
              <!-- 网格线 -->
              <line x1="0" y1="36" x2="100%" y2="36" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="72" x2="100%" y2="72" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="108" x2="100%" y2="108" stroke="#e2e8f0" stroke-width="1"/>
              <line x1="0" y1="144" x2="100%" y2="144" stroke="#e2e8f0" stroke-width="1"/>

              <!-- 折线（绿色系） -->
              <path :d="growthLinePath" fill="none" stroke="#48bb78" stroke-width="3" stroke-linecap="round"/>

              <!-- 数据点 -->
              <circle v-for="(point, index) in growthPoints" :key="index"
                      :cx="point.x" :cy="point.y" r="4" fill="#48bb78" stroke="white" stroke-width="2"/>
            </svg>

            <!-- X轴标签 -->
            <div class="x-axis">
              <span v-for="(item, index) in dashboardData.userGrowth.data" :key="index" class="x-label">
                {{  item.month.substring(0, 2) }}日
              </span>
            </div>
            <div v-if="dashboardData.userGrowth.isMock" class="chart-mock-tag">【模拟数据】</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

// 替换原有的 echarts 导入语句，使用按需引入以优化性能
import * as echarts from 'echarts/core';
import { MapChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, VisualMapComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { LabelLayout } from 'echarts/features';

// 注册必要的组件
echarts.use([
  MapChart,
  TitleComponent,
  TooltipComponent,
  VisualMapComponent,
  CanvasRenderer,
  LabelLayout
]);

// API 端点配置
const API_ENDPOINTS = {
  totalSales: '/api/orders/total-sales',
  totalOrders: '/api/orders/count',
  totalProducts: '/api/products/count',
  todaySales: '/api/statistics/sales-comparison',
  todayOrders: '/api/statistics/order-comparison',
  lowStockProducts: '/api/products/low-stock/count',
  userDistribution: '/api/orders/today/statistics/by-province',

  // 暂无API（用null表示）
  totalUsers: null,
  newUsers: null,
  salesTrend: null,
  recentActivities: null,
  userGrowth: null
};

// 模拟数据
const MOCK_DATA = {
  totalSales: { value: 1284500, trend: { value: '+12.5%', type: 'up' } },
  totalUsers: { value: 3542 },
  totalProducts: { value: 287, trend: { value: '±0%', type: 'neutral' } },
  totalOrders: { value: 5642, trend: { value: '+5.7%', type: 'up' } },
  todaySales: { value: 28450, trend: { value: '+18.2%', type: 'up' } },
  newUsers: { value: 42 },
  todayOrders: { value: 124, trend: { value: '+8.7%', type: 'up' } },
  lowStockProducts: { value: 7 },
  salesTrend: [
    { month: '1月', sales: 120000 },
    { month: '2月', sales: 180000 },
    { month: '3月', sales: 150000 },
    { month: '4月', sales: 220000 },
    { month: '5月', sales: 210000 },
    { month: '6月', sales: 280000 }
  ],
  userGrowth: [
    { date: '1', users: 3200 },
    { date: '2', users: 3250 },
    { date: '3', users: 3310 },
    { date: '4', users: 3380 },
    { date: '5', users: 3420 },
    { date: '6', users: 3480 },
    { date: '7', users: 3542 }
  ],
  userDistribution: [
    { province: '广东', users: 560, lng: 113.27, lat: 23.13 },
    { province: '北京', users: 420, lng: 116.40, lat: 39.90 },
    { province: '上海', users: 380, lng: 121.47, lat: 31.23 },
    { province: '江苏', users: 310, lng: 118.78, lat: 32.04 },
    { province: '浙江', users: 290, lng: 120.15, lat: 30.28 },
    { province: '四川', users: 220, lng: 104.06, lat: 30.67 },
    { province: '湖北', users: 180, lng: 114.30, lat: 30.60 },
    { province: '湖南', users: 160, lng: 112.97, lat: 28.20 },
    { province: '河南', users: 150, lng: 113.65, lat: 34.76 },
    { province: '山东', users: 140, lng: 117.00, lat: 36.67 }
  ],
  recentActivities: [
    { id: 1, type: 'order', text: '用户张三购买联想小新Pro16一台', time: '10分钟前', color: '#4299e1' },
    { id: 2, type: 'product', text: '商品"联想拯救者Y9000P"库存不足', time: '25分钟前', color: '#ed8936' },
    { id: 3, type: 'user', text: '新用户李四完成注册', time: '1小时前', color: '#48bb78' },
    { id: 4, type: 'comment', text: '用户王五对"联想ThinkPad X1"发表好评', time: '2小时前', color: '#9f7aea' },
    { id: 5, type: 'order', text: '批量订单发货完成', time: '3小时前', color: '#4299e1' }
  ]
};

// 智能数据获取函数
async function fetchData(endpoint, mockData, dataKey = null) {
  if (!endpoint) {
    return {
      data: mockData,
      isMock: false,
      noApi: true,
      value: mockData?.value || mockData
    };
  }

  try {
    const response = await axios.get(endpoint);

    if (response.data.code === 1) {
      const apiData = dataKey ? response.data.data[dataKey] : response.data.data;
      return {
        data: apiData,
        isMock: false,
        noApi: false,
        value: apiData?.value || apiData
      };
    } else {
      return {
        data: mockData,
        isMock: true,
        noApi: false,
        value: mockData?.value || mockData
      };
    }
  } catch (error) {
    console.error(`API请求失败 (${endpoint}):`, error);
    return {
      data: mockData,
      isMock: true,
      noApi: false,
      value: mockData?.value || mockData
    };
  }
}

export default {
  name: 'AdminDashboard',
  data() {
    return {
      salesTimeRange: 'monthly',
      salesPoints: [],
      growthPoints: [],
      chinaMapChart: null,

      // 添加的省份订单模拟数据 - 专门用于地图显示
      provinceOrders: [
        { name: '广东省', value: 560 },
        { name: '北京市', value: 420 },
        { name: '上海市', value: 380 },
        { name: '江苏省', value: 310 },
        { name: '浙江省', value: 290 },
        { name: '四川省', value: 220 },
        { name: '湖北省', value: 180 },
        { name: '湖南省', value: 160 },
        { name: '河南省', value: 150 },
        { name: '山东省', value: 140 }
      ],

      dashboardData: {
        totalSales: { value: 0, isMock: false, noApi: false },
        totalUsers: { value: 0, isMock: false, noApi: false },
        totalProducts: { value: 0, isMock: false, noApi: false },
        totalOrders: { value: 0, isMock: false, noApi: false },
        todaySales: { value: 0, isMock: false, noApi: false },
        newUsers: { value: 0, isMock: false, noApi: false },
        todayOrders: { value: 0, isMock: false, noApi: false },
        lowStockProducts: { value: 0, isMock: false, noApi: false },
        salesTrend: { data: [], isMock: false, noApi: false },
        userGrowth: { data: [], isMock: false, noApi: false },
        userDistribution: { data: [], isMock: false, noApi: false },
        recentActivities: { data: [], isMock: false, noApi: false }
      }
    };
  },
  computed: {
    salesLinePath() {
      if (this.salesPoints.length === 0) return '';
      let path = `M ${this.salesPoints[0].x} ${this.salesPoints[0].y}`;
      for (let i = 1; i < this.salesPoints.length; i++) {
        path += ` L ${this.salesPoints[i].x} ${this.salesPoints[i].y}`;
      }
      return path;
    },
    growthLinePath() {
      if (this.growthPoints.length === 0) return '';
      let path = `M ${this.growthPoints[0].x} ${this.growthPoints[0].y}`;
      for (let i = 1; i < this.growthPoints.length; i++) {
        path += ` L ${this.growthPoints[i].x} ${this.growthPoints[i].y}`;
      }
      return path;
    }
  },
  mounted() {
    this.loadAllData().then(() => {
      this.calculateChartPoints();
      this.$nextTick(() => {
        this.initChinaMap();
      });
    });
  },
  beforeDestroy() {
    if (this.chinaMapChart) {
      this.chinaMapChart.dispose();
      this.chinaMapChart = null;
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if (this.chinaMapChart) {
        this.chinaMapChart.resize();
      }
    },
    formatNumber(num) {
      if (typeof num !== 'number') return '0';
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
      }
      return num.toLocaleString();
    },
    async loadAllData() {
      const promises = [
        this.loadTotalSales(),
        this.loadTotalUsers(),
        this.loadTotalProducts(),
        this.loadTotalOrders(),
        this.loadTodaySales(),
        this.loadNewUsers(),
        this.loadTodayOrders(),
        this.loadLowStockProducts(),
        this.loadSalesTrend(),
        this.loadUserGrowth(),
        this.loadUserDistribution(),
        this.loadRecentActivities()
      ];
      await Promise.all(promises);
    },
    async loadTotalSales() {
      const result = await fetchData(API_ENDPOINTS.totalSales, MOCK_DATA.totalSales);
      this.dashboardData.totalSales = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi,
        trend: result.isMock ? MOCK_DATA.totalSales.trend : null
      };
    },
    async loadTotalUsers() {
      const result = await fetchData(API_ENDPOINTS.totalUsers, MOCK_DATA.totalUsers);
      this.dashboardData.totalUsers = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    async loadTotalProducts() {
      const result = await fetchData(API_ENDPOINTS.totalProducts, MOCK_DATA.totalProducts);
      this.dashboardData.totalProducts = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi,
        trend: result.isMock ? MOCK_DATA.totalProducts.trend : null
      };
    },
    async loadTotalOrders() {
      const result = await fetchData(API_ENDPOINTS.totalOrders, MOCK_DATA.totalOrders);
      this.dashboardData.totalOrders = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi,
        trend: result.isMock ? MOCK_DATA.totalOrders.trend : null
      };
    },
    async loadTodaySales() {
      const result = await fetchData(API_ENDPOINTS.todaySales, MOCK_DATA.todaySales);
      this.dashboardData.todaySales = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi,
        trend: result.isMock ? MOCK_DATA.todaySales.trend : null
      };
    },
    async loadNewUsers() {
      const result = await fetchData(API_ENDPOINTS.newUsers, MOCK_DATA.newUsers);
      this.dashboardData.newUsers = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    async loadTodayOrders() {
      const result = await fetchData(API_ENDPOINTS.todayOrders, MOCK_DATA.todayOrders);
      this.dashboardData.todayOrders = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi,
        trend: result.isMock ? MOCK_DATA.todayOrders.trend : null
      };
    },
    async loadLowStockProducts() {
      const result = await fetchData(API_ENDPOINTS.lowStockProducts, MOCK_DATA.lowStockProducts);
      this.dashboardData.lowStockProducts = {
        value: result.value,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    async loadSalesTrend() {
      const result = await fetchData(API_ENDPOINTS.salesTrend, MOCK_DATA.salesTrend);
      this.dashboardData.salesTrend = {
        data: result.data,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    async loadUserGrowth() {
      const result = await fetchData(API_ENDPOINTS.userGrowth, MOCK_DATA.userGrowth);
      this.dashboardData.userGrowth = {
        data: result.data,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    async loadUserDistribution() {
      const result = await fetchData(API_ENDPOINTS.userDistribution, MOCK_DATA.userDistribution);
      this.dashboardData.userDistribution = {
        data: result.data,
        isMock: result.isMock,
        noApi: result.noApi
      };

      if (!this.dashboardData.userDistribution.noApi && result.data) {
        this.$nextTick(() => {
          setTimeout(() => this.initChinaMap(), 100);
        });
      }
    },
    async loadRecentActivities() {
      const result = await fetchData(API_ENDPOINTS.recentActivities, MOCK_DATA.recentActivities);
      this.dashboardData.recentActivities = {
        data: result.data,
        isMock: result.isMock,
        noApi: result.noApi
      };
    },
    calculateChartPoints() {
      if (this.dashboardData.salesTrend.data && this.dashboardData.salesTrend.data.length > 0) {
        this.salesPoints = this.dashboardData.salesTrend.data.map((item, index) => {
          const x = (index / (this.dashboardData.salesTrend.data.length - 1)) * 85 + 7.5;
          const y = 180 - (item.sales / 300000) * 180;
          return { x: `${x}%`, y };
        });
      }

      if (this.dashboardData.userGrowth.data && this.dashboardData.userGrowth.data.length > 0) {
        this.growthPoints = this.dashboardData.userGrowth.data.map((item, index) => {
          const x = (index / (this.dashboardData.userGrowth.data.length - 1)) * 90 + 5;
          const y = 180 - (item.users / 4000) * 180;
          return { x: `${x}%`, y };
        });
      }
    },
    getActivityIcon(type) {
      const icons = {
        order: '🛒',
        product: '📦',
        user: '👤',
        comment: '💬'
      };
      return icons[type] || '📝';
    },
    calculateGrowthRate() {
      if (this.dashboardData.userGrowth.noApi || !this.dashboardData.userGrowth.data || this.dashboardData.userGrowth.data.length < 2) return 0;
      const first = this.dashboardData.userGrowth.data[0].users;
      const last = this.dashboardData.userGrowth.data[this.dashboardData.userGrowth.data.length - 1].users;
      return Math.round(((last - first) / first) * 100);
    },
    refreshActivities() {
      if (!this.dashboardData.recentActivities.noApi && this.dashboardData.recentActivities.data) {
        this.dashboardData.recentActivities.data = [...this.dashboardData.recentActivities.data].sort(() => Math.random() - 0.5);
      }
    },

    // 重写的 initChinaMap 方法
    async initChinaMap() {
      try {
        console.log('开始加载中国地图数据...');

        // ✅ 修改1：直接使用阿里云DataV在线地图数据
        const mapDataUrl = 'https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json';

        // ✅ 修改2：简化请求和解析逻辑
        const response = await axios.get(mapDataUrl, {
          timeout: 10000 // 设置10秒超时
        });

        if (!response.data) {
          throw new Error('地图数据加载失败：返回数据为空');
        }

        const chinaJson = response.data;
        console.log('地图JSON数据加载成功', chinaJson ? '数据有效' : '数据无效');

        // 3. 注册地图
        echarts.registerMap('china', chinaJson);
        console.log('地图注册成功');

        // 4. 初始化图表
        const chartDom = document.getElementById('china-map-chart');
        if (!chartDom) {
          console.error('找不到地图容器元素: #china-map-chart');
          return;
        }

        this.chinaMapChart = echarts.init(chartDom);




        // 5. 配置地图选项
        // ✅ 修改3：更新配色方案为指定纯色系
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{b}<br/>订单数: {c}'
          },
          visualMap: {
            min: 100,
            max: 600,
            left: '15px',
            bottom: '5px',
            text: ['高', '低'],

            textStyle: {
              fontSize: 12,  // 图例文字大小
            },

            calculable: true,
            inRange: {
              // ✅ 修改4：使用指定的纯色系配色
              color: ['#fef0d9', '#fdcc8a', '#fc8d59', '#d7301f']
            },
            itemHeight: 50,  // 增加图例高度
            itemWidth: 10,   // 增加图例宽度
            orient: 'vertical',
          },
          series: [
            {
              name: '订单数量',
              type: 'map',
              map: 'china',
              roam: false,
              zoom: 1.4,
              center: [105, 38], // ✅ 地图居中位置（经度，纬度）

              layoutCenter: ['50%', '43%'],  // 水平居中，垂直居中
              layoutSize: '98%',             // 占容器95%的大小

              label: {
                show: true,
                fontSize: 10
              },
              data: this.provinceOrders,  // 使用我们添加的模拟数据
              emphasis: {
                itemStyle: {
                  areaColor: '#ff9900'
                }
              },
              itemStyle: {
                // ✅ 修改5：地图基础颜色设为最浅的纯色
                areaColor: '#fef0d9',
                borderColor: '#fff'
              }
            }
          ],

          /* ✅ 添加grid配置，移除底部空间 */
          grid: {
            top: 0,
            right: 0,
            bottom: 0,  /* ✅ 关键：底部为0 */
            left: 0,
            containLabel: true
          }



        };


        // 6. 设置配置项并渲染
        this.chinaMapChart.setOption(option);
        console.log('中国地图图表初始化完成！');

        // 7. 窗口大小变化时重绘
        window.addEventListener('resize', this.handleResize);

      } catch (error) {
        console.error('初始化中国地图时出错:', error);

        // 错误处理：在图表容器中显示错误信息
        const chartDom = document.getElementById('china-map-chart');
        if (chartDom) {
          chartDom.innerHTML = `
            <div style="text-align: center; padding: 20px; color: #666;">
              <p>地图加载失败</p>
              <p style="font-size: 12px;">${error.message}</p>
              <p style="font-size: 12px; margin-top: 10px;">【模拟数据】</p>
            </div>
          `;
        }
      }
    }
  }
};
</script>



<style scoped>
.admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

.top-stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.today-stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 0 !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.stat-card.large.dark-card {
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  color: white;
  border: none;
}

.sales-card { background: linear-gradient(135deg, #1e3a8a, #2563eb); }
.users-card { background: linear-gradient(135deg, #065f46, #059669); }
.products-card { background: linear-gradient(135deg, #9a3412, #ea580c); }
.orders-card { background: linear-gradient(135deg, #7f1d1d, #dc2626); }

.stat-card.small.light-card {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.today-sales-card { background: linear-gradient(135deg, #60a5fa, #93c5fd); }
.new-users-card { background: linear-gradient(135deg, #4ade80, #86efac); }
.low-stock-card { background: linear-gradient(135deg, #fb923c, #fdba74); }
.today-orders-card { background: linear-gradient(135deg, #f87171, #fca5a5); }

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  background: rgba(255, 255, 255, 0.2) !important;
}

.stat-icon-sm {
  width: 50px;
  height: 50px;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: rgba(255, 255, 255, 0.3);
}

.iconfont, .iconfont-sm {
  font-style: normal;
  font-weight: normal;
}

.stat-content, .stat-content-sm {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: white;
  margin-bottom: 5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.stat-value-sm {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 2px;
}

.stat-label {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 10px;
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.1);
}

.stat-label-sm {
  font-size: 15px;
  color: #718096;
}

.stat-trend {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 12px;
  display: inline-block;
  background: rgba(255, 255, 255, 0.2);
}

.stat-trend-sm {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 10px;
  display: inline-block;
  margin-top: 5px;
}

.stat-trend.up, .stat-trend-sm.up {
  background: rgba(198, 246, 213, 0.3);
  color: #22543d;
}

.stat-trend.down, .stat-trend-sm.down {
  background: rgba(254, 215, 215, 0.3);
  color: #742a2a;
}

.stat-trend.neutral, .stat-trend-sm.neutral {
  background: rgba(226, 232, 240, 0.3);
  color: #4a5568;
}

.mock-tag, .mock-tag-sm {
  display: inline-block;
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
  border-radius: 4px;
  margin-top: 5px;
  font-weight: 500;
}

.mock-tag-sm {
  font-size: 10px;
  padding: 1px 5px;
}

.no-api-tag, .no-api-tag-sm {
  display: inline-block;
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(160, 174, 192, 0.1);
  color: #718096;
  border-radius: 4px;
  margin-top: 5px;
  font-weight: 500;
  border: 1px dashed #cbd5e0;
}

.no-api-tag-sm {
  font-size: 10px;
  padding: 1px 5px;
}

.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;

}

.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.chart-card, .activity-card {
  background: white;
  border-radius: 0 !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.chart-header {
  padding: 10px 25px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #2d3748;
}

.no-api-info {
  font-size: 12px;
  color: #a0aec0;
  padding: 2px 6px;
  background: #f7fafc;
  line-height: 1.2; /* ✅ 调整行高 */
  border-radius: 4px;
  border: 1px dashed #e2e8f0;
}

.no-api-chart, .no-api-list {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-api-placeholder {
  text-align: center;
  color: #a0aec0;
}

.placeholder-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 14px;
  color: #718096;
}

.chart-container {
  padding: 1px 25px;
  height: 380px;
}

.chart-mock-line {
  position: relative;
  height: 100%;
}

.line-chart {
  width: 100%;
  height: 100%;
}

.x-axis {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding: 0 2%;
}

.x-label {
  font-size: 12px;
  color: #718096;
  transform: rotate(-45deg);
  transform-origin: center;
  white-space: nowrap;
}

.chart-mock-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
  border-radius: 4px;
  font-weight: 500;
}

.map-container {
  height: 380px;
  position: relative;
  width: 100%;
  margin: 0 ; /* ✅ 确保没有外边距 */
  padding: 0 ; /* ✅ 确保没有内边距 */
}

.map-legend {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #718096;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: inline-block;
}

.dot.high { background: #f56565; }
.dot.medium { background: #ecc94b; }
.dot.low { background: #48bb78; }

.activity-list {
  max-height: 320px;
  overflow-y: auto;
  padding-right: 10px;
  position: relative;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f7fafc;
  transition: background 0.3s;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f7fafc;
  border-radius: 0;
  padding: 15px;
  margin: 0 -15px;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 14px;
  color: #2d3748;
  margin-bottom: 4px;
  line-height: 1.4;
  word-break: break-word;
}

.activity-time {
  font-size: 12px;
  color: #a0aec0;
}

.refresh-btn {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0;
  padding: 6px 12px;
  font-size: 12px;
  color: #4a5568;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.refresh-icon {
  font-size: 14px;
}

.growth-rate {
  background: #c6f6d5;
  color: #22543d;
  padding: 4px 12px;
  border-radius: 0;
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 1200px) {
  .top-stats-row,
  .today-stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .top-stats-row,
  .today-stats-row {
    grid-template-columns: 1fr;
  }
  .stat-card.large.dark-card {
    padding: 20px;
    gap: 15px;
  }
  .stat-icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }
  .stat-value {
    font-size: 28px;
  }
}

.activity-list::-webkit-scrollbar {
  width: 6px;
}

.activity-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.activity-list::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.activity-list::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 确保两个卡片内部容器高度一致 */
.chart-card {
  display: flex;
  flex-direction: column;
}

.chart-header {
  flex-shrink: 0; /* 标题不收缩 */
}

.chart-container {
  flex: 1; /* 图表区域占据剩余空间 */
  display: flex;
  flex-direction: column;
  min-height: 0; /* 重要：允许内容收缩 */
}

/* 确保地图和折线图都能占满容器 */
#china-map-chart {
  flex: 1; /* 占满剩余空间 */
}

.chart-mock-line {
  flex: 1; /* 占满剩余空间 */
  display: flex;
  flex-direction: column;
}

.line-chart {
  flex: 1; /* 占满容器 */
}

/* 确保两个卡片占满网格空间 */
.chart-card {
  width: 100%; /* 占满网格单元格 */
  height: 100%; /* 如果有高度问题也统一 */
}
</style>