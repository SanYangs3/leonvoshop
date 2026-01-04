<template>
  <div class="admin-dashboard">
    <!-- 第一排统计卡片 -->
    <div class="row">
      <!-- 总销售额 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-aqua">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">总销售额 <span v-if="dashboardData.totalSales.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">¥{{ formatNumber(dashboardData.totalSales.value) }}</span>
            <div v-if="dashboardData.totalSales.trend" class="progress-description" :class="dashboardData.totalSales.trend.type">
              {{ dashboardData.totalSales.trend.value }}
            </div>
          </div>
        </div>
      </div>

      <!-- 总用户数 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-red">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">总用户数 <span v-if="dashboardData.totalUsers.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">
              <span v-if="!dashboardData.totalUsers.noApi">{{ formatNumber(dashboardData.totalUsers.value) }}</span>
              <span v-else>--</span>
            </span>
          </div>
        </div>
      </div>

      <!-- 总商品数 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-green">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">总商品数 <span v-if="dashboardData.totalProducts.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">{{ formatNumber(dashboardData.totalProducts.value) }}</span>
          </div>
        </div>
      </div>

      <!-- 总订单数 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-yellow">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">总订单数 <span v-if="dashboardData.totalOrders.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">{{ formatNumber(dashboardData.totalOrders.value) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二排统计卡片 -->
    <div class="row">
      <!-- 今日销售额 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-aqua">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">今日销售额 <span v-if="dashboardData.todaySales.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">¥{{ formatNumber(dashboardData.todaySales.value) }}</span>
            <div v-if="dashboardData.todaySales.trend" class="progress-description" :class="dashboardData.todaySales.trend.type">
              {{ dashboardData.todaySales.trend.value }}
            </div>
          </div>
        </div>
      </div>

      <!-- 今日新增用户 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-red">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">今日新增用户 <span v-if="dashboardData.newUsers.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">{{ dashboardData.newUsers.value }}</span>
            <div v-if="dashboardData.newUsers.trend" class="progress-description" :class="dashboardData.newUsers.trend.type">
              {{ dashboardData.newUsers.trend.value }}
            </div>
          </div>
        </div>
      </div>

      <!-- 低库存商品 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-green">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">低库存商品 <span v-if="dashboardData.lowStockProducts.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">{{ dashboardData.lowStockProducts.value }}</span>
          </div>
        </div>
      </div>

      <!-- 今日订单数 -->
      <div class="col-md-3 col-sm-6 col-xs-12">
        <div class="info-box">
          <span class="info-box-icon bg-yellow">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/></svg>
          </span>
          <div class="info-box-content">
            <span class="info-box-text">今日订单数 <span v-if="dashboardData.todayOrders.isMock" class="mock-badge">(模拟数据)</span></span>
            <span class="info-box-number">{{ dashboardData.todayOrders.value }}</span>
             <div v-if="dashboardData.todayOrders.trend" class="progress-description" :class="dashboardData.todayOrders.trend.type">
              {{ dashboardData.todayOrders.trend.value }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二排：订单地域分布（独占一行） -->
    <div class="row">
      <div class="col-xs-12">
        <div class="box">
          <div class="box-header with-border header-highlight-map">
            <h3 class="box-title">订单地域分布 <span v-if="dashboardData.userDistribution.isMock" class="mock-badge">(模拟数据)</span></h3>
          </div>
          <div class="box-body">
            <div class="chart-container map-container-large">
              <div id="china-map-chart" style="width: 100%; height: 100%;"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 第三排：销售趋势 + 用户增长趋势（并列） -->
    <div class="row equal-height-row">
      <!-- 销售趋势 -->
      <div class="col-md-6 col-xs-12">
        <div class="box">
          <div class="box-header with-border">
            <h3 class="box-title">销售趋势 <span v-if="dashboardData.salesTrend.isMock" class="mock-badge">(模拟数据)</span></h3>
            <div class="box-tools pull-right">
              <div v-if="dashboardData.salesTrend.noApi" class="label label-warning">暂无API</div>
              <select v-else v-model="salesTimeRange" class="time-select">
                <option value="monthly">月度</option>
                <option value="quarterly">季度</option>
                <option value="yearly">年度</option>
              </select>
            </div>
          </div>
          <div class="box-body">
            <div class="chart-container">
              <div v-if="dashboardData.salesTrend.noApi" class="no-api-chart">
                <div class="no-api-placeholder">暂无数据</div>
              </div>
              <div v-else class="chart-mock-line">
                <svg width="100%" height="100%" class="line-chart">
                  <line x1="0" y1="36" x2="100%" y2="36" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="72" x2="100%" y2="72" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="108" x2="100%" y2="108" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="144" x2="100%" y2="144" stroke="#f4f4f4" stroke-width="1"/>
                  <path :d="salesLinePath" fill="none" stroke="#3c8dbc" stroke-width="2" stroke-linecap="round"/>
                  <circle v-for="(point, index) in salesPoints" :key="index" :cx="point.x" :cy="point.y" r="3" fill="#3c8dbc" stroke="white" stroke-width="1"/>
                </svg>
                <div class="x-axis">
                  <span v-for="(item, index) in dashboardData.salesTrend.data" :key="index" class="x-label">{{ item.month }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 用户增长趋势 -->
      <div class="col-md-6 col-xs-12">
        <div class="box">
          <div class="box-header with-border">
            <h3 class="box-title">用户增长趋势 <span v-if="dashboardData.userGrowth.isMock" class="mock-badge">(模拟数据)</span></h3>
            <div class="box-tools pull-right">
              <span class="label label-success">+{{ calculateGrowthRate() }}%</span>
            </div>
          </div>
          <div class="box-body">
            <div class="chart-container">
               <svg width="100%" height="180" class="line-chart">
                  <line x1="0" y1="36" x2="100%" y2="36" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="72" x2="100%" y2="72" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="108" x2="100%" y2="108" stroke="#f4f4f4" stroke-width="1"/>
                  <line x1="0" y1="144" x2="100%" y2="144" stroke="#f4f4f4" stroke-width="1"/>
                  <path :d="growthLinePath" fill="none" stroke="#00a65a" stroke-width="2" stroke-linecap="round"/>
                  <circle v-for="(point, index) in growthPoints" :key="index" :cx="point.x" :cy="point.y" r="3" fill="#00a65a" stroke="white" stroke-width="1"/>
               </svg>
               <div class="x-axis">
                  <span v-for="(item, index) in dashboardData.userGrowth.data" :key="index" class="x-label">{{ getDateLabel(item) }}</span>
               </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 第四排：最近活动（独占一行） -->
    <div class="row">
      <div class="col-xs-12">
        <div class="box">
          <div class="box-header with-border header-highlight-activity">
            <h3 class="box-title">最近活动 <span v-if="dashboardData.recentActivities.isMock" class="mock-badge">(模拟数据)</span></h3>
            <div class="box-tools pull-right">
              <button class="btn btn-box-tool" @click="refreshActivities" v-if="!dashboardData.recentActivities.noApi">
                刷新
              </button>
            </div>
          </div>
          <div class="box-body p-0">
            <div class="activity-list activity-list-large">
              <div v-for="activity in dashboardData.recentActivities.data" :key="activity.id" class="activity-item">
                <div class="activity-icon-wrapper">
                  <!-- 使用简单的形状代替复杂图标，或复用SVG -->
                  <div class="simple-dot" :style="{ backgroundColor: activity.color }"></div>
                </div>
                <div class="activity-content">
                  <div class="activity-text">{{ activity.text }}</div>
                  <div class="activity-time">{{ activity.time }}</div>
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
import axios from 'axios';
import apiConfig from '@/config/api.config'
import * as echarts from 'echarts/core';
import { MapChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, VisualMapComponent, GridComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { LabelLayout } from 'echarts/features';

echarts.use([MapChart, TitleComponent, TooltipComponent, VisualMapComponent, GridComponent, CanvasRenderer, LabelLayout]);

const API_ENDPOINTS = {
  totalSales: apiConfig.statistics.getTotalSales,
  totalOrders: apiConfig.statistics.getOrderCount,
  totalProducts: apiConfig.statistics.getProductCount,
  todaySales: apiConfig.statistics.getSalesComparison,
  todayOrders: apiConfig.statistics.getOrderComparison,
  lowStockProducts: apiConfig.statistics.getLowStockCount,
  // 修正：使用正确的省份统计接口 (所有订单，非今日)
  userDistribution: apiConfig.order.countOrdersByProvince,
  salesTrend: apiConfig.statistics.getSalesTrend || apiConfig.order.getSalesTrend,
  userGrowthTrend: apiConfig.statistics.getUserGrowthTrend,
  totalUsers: apiConfig.user.getTotalUsers,
  newUsers: apiConfig.user.getUserComparison, // 改为使用对比接口
  recentActivities: null
};

const MOCK_DATA = {
  totalSales: { value: 1284500, trend: { value: '+12.5%', type: 'up' } },
  totalUsers: { value: 3542 },
  totalProducts: { value: 287, trend: { value: '±0%', type: 'neutral' } },
  totalOrders: { value: 5642, trend: { value: '+5.7%', type: 'up' } },
  todaySales: { value: 28450, trend: { value: '+18.2%', type: 'up' } },
  newUsers: { value: 42 },
  todayOrders: { value: 124, trend: { value: '+8.7%', type: 'up' } },
  lowStockProducts: { value: 7 },
  salesTrend: [{ month: '1月', sales: 120000 }, { month: '2月', sales: 180000 }, { month: '3月', sales: 150000 }, { month: '4月', sales: 220000 }, { month: '5月', sales: 210000 }, { month: '6月', sales: 280000 }],
  userGrowth: [{ date: '01-01', users: 3200 }, { date: '01-02', users: 3250 }, { date: '01-03', users: 3310 }, { date: '01-04', users: 3380 }, { date: '01-05', users: 3420 }, { date: '01-06', users: 3480 }, { date: '01-07', users: 3542 }],
  userDistribution: [{ province: '广东', users: 560 }, { province: '北京', users: 420 }, { province: '上海', users: 380 }],
  recentActivities: [
    { id: 1, type: 'order', text: '用户张三购买联想小新Pro16一台', time: '10分钟前', color: '#00c0ef' },
    { id: 2, type: 'product', text: '商品"联想拯救者Y9000P"库存不足', time: '25分钟前', color: '#dd4b39' },
    { id: 3, type: 'user', text: '新用户李四完成注册', time: '1小时前', color: '#00a65a' }
  ]
};

async function fetchData(endpoint, mockData, dataKey = null, defaultValue = null) {
  if (!endpoint) return { data: mockData, isMock: false, noApi: true, value: defaultValue || mockData?.value || mockData };
  try {
    const response = await axios.get(endpoint);
    if (response.data.code === 1) {
      let apiData = dataKey ? response.data.data[dataKey] : response.data.data;
      if (dataKey && typeof apiData === 'undefined') apiData = response.data.data;
      return { data: apiData, isMock: false, noApi: false, value: apiData || defaultValue };
    }
    return { data: mockData, isMock: true, noApi: false, value: defaultValue || mockData?.value || mockData };
  } catch (error) {
    return { data: mockData, isMock: true, noApi: false, value: defaultValue || mockData?.value || mockData };
  }
}

async function fetchUserDistribution() {
  try {
    // 优先尝试从后端获取聚合统计数据 (使用您提供的 countOrdersByProvince 接口)
    const response = await axios.get(API_ENDPOINTS.userDistribution);
    
    if (response.data.code === 1 && Array.isArray(response.data.data)) {
      // 假设后端返回的是 [{province: "广东", count: 10}, ...] 或 Map 列表
      const apiData = response.data.data.map(item => ({
        // 兼容不同的字段名：province/name, count/value/orderCount
        // 不在这里移除后缀，而是交给 loadUserDistribution 中的 normalizeProvinceName 处理
        name: (item.province || item.name || ''), 
        value: item.orderCount || item.count || item.value || 0 
      })).filter(item => item.name); // 过滤掉空名称

      return { data: apiData, isMock: false, noApi: false, value: apiData };
    }
    
    // 如果聚合接口没数据，才尝试降级方案：获取所有订单并前端聚合
    // ... (之前的降级逻辑保留作为保险)
    const ordersResponse = await axios.get(apiConfig.order.getAllOrders);
    
    if (ordersResponse.data.code === 1 && Array.isArray(ordersResponse.data.data)) {
      const orders = ordersResponse.data.data;
      const provinceMap = {};
      orders.forEach(order => {
        const province = order.province || order.receiverProvince || order.address?.substring(0, 2) || '未知';
        // 不在这里移除后缀
        if (province && province !== '未知') {
          provinceMap[province] = (provinceMap[province] || 0) + 1;
        }
      });
      const apiData = Object.keys(provinceMap).map(key => ({ name: key, value: provinceMap[key] }));
      return { data: apiData, isMock: false, noApi: false, value: apiData };
    }

    throw new Error('Data format error or empty data');
  } catch (error) {
    console.warn('Failed to fetch/aggregate province data, using mock:', error);
    return { data: MOCK_DATA.userDistribution, isMock: true, noApi: false, value: MOCK_DATA.userDistribution };
  }
}

async function fetchSalesTrend() {
  try {
    const endpoint = API_ENDPOINTS.salesTrend;
    if (!endpoint) return { data: MOCK_DATA.salesTrend, isMock: true, noApi: true, value: MOCK_DATA.salesTrend };
    const response = await axios.get(endpoint);
    if (response.data && response.data.code === 1 && Array.isArray(response.data.data)) {
      return { data: response.data.data.map(item => ({ month: item.month || item.date || '', sales: item.sales || item.amount || 0 })), isMock: false, noApi: false, value: response.data.data };
    }
    return { data: MOCK_DATA.salesTrend, isMock: true, noApi: false, value: MOCK_DATA.salesTrend };
  } catch (error) {
    return { data: MOCK_DATA.salesTrend, isMock: true, noApi: false, value: MOCK_DATA.salesTrend };
  }
}

async function fetchUserComparison() {
  try {
    const endpoint = API_ENDPOINTS.userGrowthTrend || API_ENDPOINTS.userGrowth;
    if (!endpoint) return { data: MOCK_DATA.userGrowth, isMock: true, noApi: true, value: MOCK_DATA.userGrowth };
    const response = await axios.get(endpoint);
    if (response.data && response.data.code === 1) {
      let growthData = Array.isArray(response.data.data) ? response.data.data : (response.data.data?.data || []);
      if (growthData.length > 0) return { data: growthData.map(item => ({ date: item.date || item.day || '', users: item.users || item.count || 0 })), isMock: false, noApi: false, value: growthData };
    }
    return { data: MOCK_DATA.userGrowth, isMock: true, noApi: false, value: MOCK_DATA.userGrowth };
  } catch (error) {
    return { data: MOCK_DATA.userGrowth, isMock: true, noApi: false, value: MOCK_DATA.userGrowth };
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
      provinceOrders: [],
      dashboardData: {
        totalSales: { value: 0 }, totalUsers: { value: 0 }, totalProducts: { value: 0 }, totalOrders: { value: 0 },
        todaySales: { value: 0 }, newUsers: { value: 0 }, todayOrders: { value: 0 }, lowStockProducts: { value: 0 },
        salesTrend: { data: [] }, userGrowth: { data: [] }, userDistribution: { data: [] }, recentActivities: { data: [] }
      }
    };
  },
  computed: {
    salesLinePath() {
      if (this.salesPoints.length === 0) return '';
      let path = `M ${this.salesPoints[0].x} ${this.salesPoints[0].y}`;
      for (let i = 1; i < this.salesPoints.length; i++) path += ` L ${this.salesPoints[i].x} ${this.salesPoints[i].y}`;
      return path;
    },
    growthLinePath() {
      if (this.growthPoints.length === 0) return '';
      let path = `M ${this.growthPoints[0].x} ${this.growthPoints[0].y}`;
      for (let i = 1; i < this.growthPoints.length; i++) path += ` L ${this.growthPoints[i].x} ${this.growthPoints[i].y}`;
      return path;
    }
  },
  mounted() {
    this.loadAllData().then(() => {
      this.calculateChartPoints();
      this.$nextTick(() => { this.initChinaMap(); });
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
      if (this.chinaMapChart) this.chinaMapChart.resize();
    },
    formatNumber(num) {
      if (num === undefined || num === null) return '0';
      const n = Number(num);
      if (isNaN(n)) return '0';
      if (n >= 10000) return (n / 10000).toFixed(1) + '万';
      return n.toLocaleString();
    },
    getDateLabel(item) {
      if (!item) return '';
      if (item.date) {
        const parts = item.date.split('-');
        return parts.length > 1 ? parts[parts.length - 1] + '日' : item.date;
      } else if (item.month) {
        return item.month.substring(0, 2) + '日';
      }
      return '';
    },
    async loadAllData() {
      await Promise.all([
        this.loadTotalSales(), this.loadTotalUsers(), this.loadTotalProducts(), this.loadTotalOrders(),
        this.loadTodaySales(), this.loadNewUsers(), this.loadTodayOrders(), this.loadLowStockProducts(),
        this.loadSalesTrend(), this.loadUserGrowth(), this.loadUserDistribution(), this.loadRecentActivities()
      ]);
    },
    async loadTotalSales() {
      const result = await fetchData(API_ENDPOINTS.totalSales, MOCK_DATA.totalSales, null, 0);
      let value = result.value;
      if (typeof value === 'object' && value !== null) value = value.totalSales || value.value || value.total || 0;
      this.dashboardData.totalSales = { value, isMock: result.isMock, noApi: result.noApi, trend: result.isMock ? MOCK_DATA.totalSales.trend : null };
    },
    async loadTotalUsers() {
      const result = await fetchData(API_ENDPOINTS.totalUsers, MOCK_DATA.totalUsers, null, 0);
      let value = result.value;
      if (typeof value === 'object' && value !== null) value = value.totalUsers || value.value || value.total || 0;
      this.dashboardData.totalUsers = { value, isMock: result.isMock, noApi: result.noApi };
    },
    async loadTotalProducts() {
      const result = await fetchData(API_ENDPOINTS.totalProducts, MOCK_DATA.totalProducts, null, 0);
      let value = result.value;
      if (typeof value === 'object' && value !== null) value = value.totalProducts || value.value || value.total || 0;
      this.dashboardData.totalProducts = { value, isMock: result.isMock, noApi: result.noApi, trend: result.isMock ? MOCK_DATA.totalProducts.trend : null };
    },
    async loadTotalOrders() {
      const result = await fetchData(API_ENDPOINTS.totalOrders, MOCK_DATA.totalOrders, null, 0);
      let value = result.value;
      if (typeof value === 'object' && value !== null) value = value.totalOrders || value.value || value.total || 0;
      this.dashboardData.totalOrders = { value, isMock: result.isMock, noApi: result.noApi, trend: result.isMock ? MOCK_DATA.totalOrders.trend : null };
    },
    async loadTodaySales() {
      const result = await fetchData(API_ENDPOINTS.todaySales, MOCK_DATA.todaySales, null, 0);
      let value = result.value;
      let trend = result.isMock ? MOCK_DATA.todaySales.trend : null;
      if (typeof result.value === 'object' && result.value !== null) {
        value = result.value.todayValue || result.value.value || 0;
        if (result.value.changePercent !== undefined) {
          const percent = result.value.changePercent;
          trend = { value: (percent > 0 ? '+' : '') + percent + '%', type: percent > 0 ? 'up' : (percent < 0 ? 'down' : 'neutral') };
        }
      }
      this.dashboardData.todaySales = { value, isMock: result.isMock, noApi: result.noApi, trend };
    },
    async loadNewUsers() {
      const result = await fetchData(API_ENDPOINTS.newUsers, MOCK_DATA.newUsers, null, 0);
      let value = result.value;
      let trend = null;

      if (typeof result.value === 'object' && result.value !== null) {
        // 优先尝试从 compare 接口格式获取 (todayCount, growthRate)
        if (result.value.todayCount !== undefined) {
          value = result.value.todayCount;
          if (result.value.growthRate !== undefined) {
             const rate = parseFloat(result.value.growthRate);
             trend = {
               value: (rate > 0 ? '+' : '') + rate + '%',
               type: rate > 0 ? 'up' : (rate < 0 ? 'down' : 'neutral')
             };
          }
        } else {
           // 兼容旧格式或直接数值
           value = result.value.value || result.value;
        }
      }
      
      this.dashboardData.newUsers = { value, isMock: result.isMock, noApi: result.noApi, trend };
    },
    async loadTodayOrders() {
      const result = await fetchData(API_ENDPOINTS.todayOrders, MOCK_DATA.todayOrders, null, 0);
      let value = result.value;
      let trend = result.isMock ? MOCK_DATA.todayOrders.trend : null;
      
      // 处理 API 返回对象的情况
      if (typeof result.value === 'object' && result.value !== null) {
        // 提取今日数值
        value = result.value.todayValue !== undefined ? result.value.todayValue : (result.value.value || 0);
        
        // 提取或计算百分比
        if (result.value.changePercent !== undefined) {
          const percent = parseFloat(result.value.changePercent);
          trend = { 
            value: (percent > 0 ? '+' : '') + percent + '%', 
            type: percent > 0 ? 'up' : (percent < 0 ? 'down' : 'neutral') 
          };
        } else if (result.value.todayValue !== undefined && result.value.yesterdayValue !== undefined) {
          // 如果API没有直接返回百分比，但有昨日数据，尝试手动计算
          const today = Number(result.value.todayValue);
          const yesterday = Number(result.value.yesterdayValue);
          if (yesterday === 0) {
            trend = { value: today === 0 ? '0%' : '+100%', type: today === 0 ? 'neutral' : 'up' };
          } else {
            const diff = today - yesterday;
            const percent = ((diff / yesterday) * 100).toFixed(1);
            trend = { 
              value: (diff > 0 ? '+' : '') + percent + '%', 
              type: diff > 0 ? 'up' : (diff < 0 ? 'down' : 'neutral') 
            };
          }
        }
      } 
      // 特殊处理：如果 value 是 0 (数字)，且不是 Mock 数据，且没有 trend，可能是 API 直接返回了 0
      // 这种情况下我们无法计算趋势，只能显示数值 0
      
      this.dashboardData.todayOrders = { value, isMock: result.isMock, noApi: result.noApi, trend };
    },
    async loadLowStockProducts() {
      const result = await fetchData(API_ENDPOINTS.lowStockProducts, MOCK_DATA.lowStockProducts, null, 0);
      this.dashboardData.lowStockProducts = { value: result.value, isMock: result.isMock, noApi: result.noApi };
    },
    async loadSalesTrend() {
      const result = await fetchSalesTrend();
      this.dashboardData.salesTrend = { data: result.data, isMock: result.isMock, noApi: result.noApi };
    },
    async loadUserGrowth() {
      const result = await fetchUserComparison();
      this.dashboardData.userGrowth = { data: result.data, isMock: result.isMock, noApi: result.noApi, value: result.value };
    },
    // 标准化省份名称
    normalizeProvinceName(name) {
      if (!name) return '';
      let normalized = name.trim();
      
      // 完整名称映射表
      const provinceMap = {
        '北京': '北京市', '天津': '天津市', '上海': '上海市', '重庆': '重庆市',
        '河北': '河北省', '山西': '山西省', '辽宁': '辽宁省', '吉林': '吉林省', '黑龙江': '黑龙江省',
        '江苏': '江苏省', '浙江': '浙江省', '安徽': '安徽省', '福建': '福建省', '江西': '江西省', '山东': '山东省',
        '河南': '河南省', '湖北': '湖北省', '湖南': '湖南省', '广东': '广东省', '海南': '海南省',
        '四川': '四川省', '贵州': '贵州省', '云南': '云南省', '陕西': '陕西省', '甘肃': '甘肃省', '青海': '青海省', '台湾': '台湾省',
        '内蒙古': '内蒙古自治区', '广西': '广西壮族自治区', '西藏': '西藏自治区', '宁夏': '宁夏回族自治区', '新疆': '新疆维吾尔自治区',
        '香港': '香港特别行政区', '澳门': '澳门特别行政区'
      };

      // 如果已经是完整名称（在Map的值中），直接返回
      if (Object.values(provinceMap).includes(normalized)) return normalized;
      
      // 如果在键中，返回对应的值
      if (provinceMap[normalized]) return provinceMap[normalized];

      // 尝试模糊匹配（处理 "广西省" -> "广西壮族自治区" 这种特殊情况）
      for (const [key, value] of Object.entries(provinceMap)) {
        if (normalized.startsWith(key)) return value;
      }

      return normalized;
    },

    async loadUserDistribution() {
      const result = await fetchUserDistribution();
      console.log('User Distribution Data:', result); // 调试日志

      this.dashboardData.userDistribution = { data: result.data, isMock: result.isMock, noApi: result.noApi };
      
      if (result.data) {
        // 使用标准化后的名称
        this.provinceOrders = result.data.map(item => ({ 
          name: this.normalizeProvinceName(item.name), 
          value: item.value 
        }));
        console.log('Mapped Province Orders:', this.provinceOrders); // 调试日志
      }
      
      if (!this.dashboardData.userDistribution.noApi && result.data) {
        this.$nextTick(() => { setTimeout(() => this.initChinaMap(), 100); });
      }
    },
    async loadRecentActivities() {
      // 尝试从真实API获取订单和用户数据来生成动态活动流
      try {
        const [ordersRes, usersRes] = await Promise.all([
          axios.get(apiConfig.order.getAllOrders),
          axios.get(apiConfig.user.getAllUsers)
        ]);

        let activities = [];

        // 处理订单数据
        if (ordersRes.data.code === 1 && Array.isArray(ordersRes.data.data)) {
          const orders = ordersRes.data.data.map(order => ({
            id: 'order-' + order.oid,
            type: 'order',
            text: `用户 ${order.receiverName || '未知用户'} 下了一笔订单 (¥${order.amount})`,
            time: order.orderTime,
            timestamp: new Date(order.orderTime).getTime(),
            color: '#00c0ef' // 蓝色
          }));
          activities = activities.concat(orders);
        }

        // 处理用户数据
        if (usersRes.data.code === 1 && Array.isArray(usersRes.data.data)) {
          const users = usersRes.data.data.map(user => ({
            id: 'user-' + user.uid,
            type: 'user',
            text: `新用户 ${user.username} 注册了账号`,
            time: user.createTime,
            timestamp: new Date(user.createTime).getTime(),
            color: '#00a65a' // 绿色
          }));
          activities = activities.concat(users);
        }

        // 如果没有获取到任何真实数据，使用模拟数据
        if (activities.length === 0) {
          const result = await fetchData(API_ENDPOINTS.recentActivities, MOCK_DATA.recentActivities);
          this.dashboardData.recentActivities = { data: result.data, isMock: true, noApi: true };
          return;
        }

        // 按时间倒序排序
        activities.sort((a, b) => b.timestamp - a.timestamp);

        // 取前20条
        activities = activities.slice(0, 20);

        // 格式化时间显示 (例如: "10分钟前", "2小时前")
        const now = new Date().getTime();
        activities.forEach(item => {
          const diff = now - item.timestamp;
          const minute = 60 * 1000;
          const hour = 60 * minute;
          const day = 24 * hour;

          if (diff < minute) item.time = '刚刚';
          else if (diff < hour) item.time = Math.floor(diff / minute) + '分钟前';
          else if (diff < day) item.time = Math.floor(diff / hour) + '小时前';
          else {
            // 详细日期格式: YYYY-MM-DD HH:mm
            const date = new Date(item.timestamp);
            const year = date.getFullYear();
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const d = date.getDate().toString().padStart(2, '0');
            const h = date.getHours().toString().padStart(2, '0');
            const m = date.getMinutes().toString().padStart(2, '0');
            item.time = `${year}-${month}-${d} ${h}:${m}`;
          }
        });

        this.dashboardData.recentActivities = { data: activities, isMock: false, noApi: false };

      } catch (error) {
        console.error("Failed to load real activities:", error);
        // 出错时回退到模拟数据
        const result = await fetchData(API_ENDPOINTS.recentActivities, MOCK_DATA.recentActivities);
        this.dashboardData.recentActivities = { data: result.data, isMock: true, noApi: true };
      }
    },
    calculateChartPoints() {
      if (this.dashboardData.salesTrend.data && this.dashboardData.salesTrend.data.length > 0) {
        const maxSales = Math.max(...this.dashboardData.salesTrend.data.map(item => item.sales || 0));
        this.salesPoints = this.dashboardData.salesTrend.data.map((item, index) => {
          const x = (index / (this.dashboardData.salesTrend.data.length - 1)) * 85 + 7.5;
          const y = 180 - ((item.sales || 0) / (maxSales || 300000)) * 180;
          return { x: `${x}%`, y };
        });
      }
      if (this.dashboardData.userGrowth.data && this.dashboardData.userGrowth.data.length > 0) {
        const maxUsers = Math.max(...this.dashboardData.userGrowth.data.map(item => item.users || 0));
        this.growthPoints = this.dashboardData.userGrowth.data.map((item, index) => {
          const x = (index / (this.dashboardData.userGrowth.data.length - 1)) * 90 + 5;
          const y = 180 - ((item.users || 0) / (maxUsers || 4000)) * 180;
          return { x: `${x}%`, y };
        });
      }
    },
    calculateGrowthRate() {
      if (!this.dashboardData.userGrowth.data || this.dashboardData.userGrowth.data.length < 2) return 0;
      const data = this.dashboardData.userGrowth.data;
      const firstItem = data.find(item => item.users !== undefined);
      const lastItem = data.findLast(item => item.users !== undefined);
      if (!firstItem || !lastItem || !firstItem.users) return 0;
      return Math.round(((lastItem.users - firstItem.users) / firstItem.users) * 100);
    },
    refreshActivities() {
      this.loadRecentActivities();
    },
    async initChinaMap() {
      try {
        const mapDataUrl = 'https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json';
        const response = await axios.get(mapDataUrl, { timeout: 10000 });
        if (!response.data) throw new Error('Map data failed');
        echarts.registerMap('china', response.data);
        const chartDom = document.getElementById('china-map-chart');
        if (!chartDom) return;
        this.chinaMapChart = echarts.init(chartDom);
        const option = {
          tooltip: { trigger: 'item', formatter: '{b}<br/>订单数: {c}' },
          visualMap: {
            min: 0, 
            max: 10, 
            left: '30', 
            bottom: '30', 
            itemWidth: 15, // 恢复正常宽度
            itemHeight: 100, // 恢复正常高度
            text: ['高', '低'], 
            textStyle: { fontSize: 12 },
            calculable: true,
            inRange: { color: ['#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'] }
          },
          series: [{
            name: '订单数量', type: 'map', map: 'china', roam: false, 
            zoom: 1.6, // 保持适中缩放
            top: '25%', 
            label: { show: true, fontSize: 10 },
            data: this.provinceOrders,
            itemStyle: { areaColor: '#f3f3f3', borderColor: '#ccc' },
            emphasis: { itemStyle: { areaColor: '#f39c12' } }
          }]
        };
        this.chinaMapChart.setOption(option);
        window.addEventListener('resize', this.handleResize);
      } catch (error) {
        console.error('Map init error:', error);
      }
    }
  }
};
</script>

<style scoped>
.admin-dashboard {
  font-family: 'Source Sans Pro', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  color: #333;
}

/* 栅格系统模拟 Bootstrap */
.row {
  display: flex;
  flex-wrap: wrap;
  margin-right: -15px;
  margin-left: -15px;
}

.equal-height-row > [class*='col-'] {
  display: flex;
  flex-direction: column;
}

.equal-height-row .box {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.equal-height-row .box-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.col-md-3, .col-sm-6, .col-xs-12, .col-md-6 {
  position: relative;
  min-height: 1px;
  padding-right: 15px;
  padding-left: 15px;
}

.col-xs-12 { width: 100%; }
@media (min-width: 768px) {
  .col-sm-6 { width: 50%; }
}
@media (min-width: 992px) {
  .col-md-3 { width: 25%; }
  .col-md-6 { width: 50%; }
}

/* Info Box */
.info-box {
  display: block;
  min-height: 90px;
  background: #fff;
  width: 100%;
  box-shadow: 0 1px 1px rgba(0,0,0,0.1);
  border-radius: 2px;
  margin-bottom: 15px;
}

.info-box-icon {
  border-top-left-radius: 2px;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 2px;
  display: block;
  float: left;
  height: 90px;
  width: 90px;
  text-align: center;
  font-size: 45px;
  line-height: 90px;
  background: rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-box-content {
  padding: 5px 10px;
  margin-left: 90px;
}

.info-box-number {
  display: block;
  font-weight: bold;
  font-size: 18px;
}

.progress-description {
  display: block;
  margin: 0;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 5px;
}

.progress-description.up { color: #00a65a; }
.progress-description.down { color: #dd4b39; }
.progress-description.neutral { color: #777; }

.info-box-text {
  text-transform: uppercase;
  display: block;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Colors */
.bg-aqua { background-color: #00c0ef !important; color: #fff !important; }
.bg-red { background-color: #dd4b39 !important; color: #fff !important; }
.bg-green { background-color: #00a65a !important; color: #fff !important; }
.bg-yellow { background-color: #f39c12 !important; color: #fff !important; }

/* Box Style */
.box {
  position: relative;
  border-radius: 3px;
  background: #ffffff;
  border-top: 3px solid #d2d6de;
  margin-bottom: 20px;
  width: 100%;
  box-shadow: 0 1px 1px rgba(0,0,0,0.1);
}

.box-header {
  color: #444;
  display: block;
  padding: 10px;
  position: relative;
}

.box-header.with-border {
  border-bottom: 1px solid #f4f4f4;
}

.box-title {
  display: inline-block;
  font-size: 18px;
  margin: 0;
  line-height: 1;
}

.box-body {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
  padding: 10px;
}

.box-tools {
  position: absolute;
  right: 10px;
  top: 5px;
}

.label {
  display: inline;
  padding: .2em .6em .3em;
  font-size: 75%;
  font-weight: 700;
  line-height: 1;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  vertical-align: baseline;
  border-radius: .25em;
}
.label-warning { background-color: #f0ad4e; }
.label-success { background-color: #5cb85c; }

.chart-container {
  height: 300px;
  position: relative;
}

/* 特别调整地图容器高度，让地图显示更大 */
.map-container {
  height: 380px;
}

.line-chart {
  width: 100%;
  height: 100%;
}

.x-axis {
  display: flex;
  justify-content: space-between;
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

/* Activity List */
.activity-list {
  padding: 0;
  margin: 0;
  height: 300px;
  overflow-y: auto;
}

.activity-item {
  border-bottom: 1px solid #f4f4f4;
  padding: 10px;
  display: flex;
  align-items: center;
}

.activity-icon-wrapper {
  margin-right: 10px;
}

.simple-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  margin-bottom: 2px;
}

.activity-time {
  color: #999;
  font-size: 12px;
}

/* Buttons */
.btn-box-tool {
  padding: 5px;
  font-size: 12px;
  background: transparent;
  color: #97a0b3;
  border: none;
  cursor: pointer;
}
.btn-box-tool:hover { color: #606c84; }

.time-select {
  border: 1px solid #ddd;
  padding: 2px;
  font-size: 12px;
}

.mock-badge {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  margin-left: 5px;
  background: #f4f4f4;
  padding: 1px 4px;
  border-radius: 3px;
}

/* 头部高亮样式 */
.header-highlight-map {
  background: linear-gradient(to right, #e8f5e9, #ffffff);
  border-left: 4px solid #2e7d32;
}

.header-highlight-activity {
  background: linear-gradient(to right, #e3f2fd, #ffffff);
  border-left: 4px solid #1976d2;
}

/* 宽屏地图容器 */
.map-container-large {
  height: 300px;
}

/* 宽屏活动列表 */
.activity-list-large {
  height: auto;
  max-height: 400px;
}
</style>