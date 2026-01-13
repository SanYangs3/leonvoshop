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
      <!-- 订单数趋势 -->
      <div class="col-md-6 col-xs-12">
        <div class="box">
          <div class="box-header with-border">
            <h3 class="box-title">订单数趋势 <span v-if="dashboardData.orderTrend.isMock" class="mock-badge">(模拟数据)</span></h3>
            <div class="box-tools pull-right">
              <select v-model="orderTrendRange" class="time-select" @change="updateOrderTrendChart">
                <option value="week">本周</option>
                <option value="month">本月</option>
              </select>
            </div>
          </div>
          <div class="box-body">
            <div class="chart-container">
               <div id="order-trend-chart" style="width: 100%; height: 100%;"></div>
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
              <select v-model="userGrowthRange" class="time-select" @change="updateUserGrowthChart">
                <option value="week">本周</option>
                <option value="month">本月</option>
              </select>
            </div>
          </div>
          <div class="box-body">
            <div class="chart-container">
               <div id="user-growth-chart" style="width: 100%; height: 100%;"></div>
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
import { MapChart, LineChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, VisualMapComponent, GridComponent, LegendComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import { LabelLayout } from 'echarts/features';

echarts.use([MapChart, LineChart, TitleComponent, TooltipComponent, VisualMapComponent, GridComponent, LegendComponent, CanvasRenderer, LabelLayout]);

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
      orderTrendRange: 'week',
      userGrowthRange: 'week',
      
      chinaMapChart: null,
      orderTrendChart: null,
      userGrowthChart: null,
      
      provinceOrders: [],
      
      allOrders: [],
      allUsers: [],
      allProducts: [], // 新增：保存所有商品用于生成推广活动
      
      dashboardData: {
        totalSales: { value: 0 }, totalUsers: { value: 0 }, totalProducts: { value: 0 }, totalOrders: { value: 0 },
        todaySales: { value: 0 }, newUsers: { value: 0 }, todayOrders: { value: 0 }, lowStockProducts: { value: 0 },
        orderTrend: { isMock: false }, userGrowth: { isMock: false }, userDistribution: { data: [] }, recentActivities: { data: [] }
      }
    };
  },
  mounted() {
    this.loadAllData().then(() => {
      this.$nextTick(() => { 
        this.initChinaMap();
        this.updateOrderTrendChart();
        this.updateUserGrowthChart();
      });
    });
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    if (this.chinaMapChart) {
      this.chinaMapChart.dispose();
      this.chinaMapChart = null;
    }
    if (this.orderTrendChart) {
      this.orderTrendChart.dispose();
      this.orderTrendChart = null;
    }
    if (this.userGrowthChart) {
      this.userGrowthChart.dispose();
      this.userGrowthChart = null;
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if (this.chinaMapChart) this.chinaMapChart.resize();
      if (this.orderTrendChart) this.orderTrendChart.resize();
      if (this.userGrowthChart) this.userGrowthChart.resize();
    },
    formatNumber(num) {
      if (num === undefined || num === null) return '0';
      const n = Number(num);
      if (isNaN(n)) return '0';
      if (n >= 10000) return (n / 10000).toFixed(1) + '万';
      return n.toLocaleString();
    },
    
    async loadAllData() {
      // 1. 先获取基础数据：所有订单、用户和商品
      await Promise.all([
        this.fetchAllOrders(), 
        this.fetchAllUsers(),
        this.loadTotalProducts() // 这会填充 allProducts (需要修改 loadTotalProducts)
      ]);
      
      // 2. 基于基础数据计算或加载其他统计
      await Promise.all([
        this.loadTotalSales(), 
        this.loadTotalUsers(), 
        // this.loadTotalProducts(), // 移到上面了
        this.loadTotalOrders(),
        this.loadTodaySales(), 
        this.loadNewUsers(), 
        this.loadTodayOrders(), 
        this.loadLowStockProducts(),
        this.loadUserDistribution(), 
        this.loadRecentActivities()
      ]);
    },

    async fetchAllOrders() {
      try {
        const response = await axios.get(apiConfig.order.getAllOrders);
        if (response.data.code === 1) {
          this.allOrders = response.data.data || [];
        }
      } catch (e) {
        console.error("Failed to fetch orders", e);
        this.allOrders = []; // Use empty array, load functions will handle mocks if needed
      }
    },

    async fetchAllUsers() {
      try {
        const response = await axios.get(apiConfig.user.getAllUsers);
        if (response.data.code === 1) {
          // 兼容不同的返回结构
          let users = response.data.data;
          if (users && users.list) users = users.list;
          this.allUsers = Array.isArray(users) ? users : [];
        }
      } catch (e) {
        console.error("Failed to fetch users", e);
        this.allUsers = [];
      }
    },

    async loadTotalSales() {
        // 优先使用后端接口，失败则前端计算
        const result = await fetchData(API_ENDPOINTS.totalSales, MOCK_DATA.totalSales, null, 0);
        if (!result.isMock && !result.noApi) {
             let value = result.value;
             if (typeof value === 'object' && value !== null) value = value.totalSales || value.value || value.total || 0;
             this.dashboardData.totalSales = { value, isMock: false };
        } else {
            // 前端计算
            const total = this.allOrders.reduce((sum, order) => sum + (order.amount || 0), 0);
            this.dashboardData.totalSales = { value: total, isMock: this.allOrders.length === 0 };
        }
    },
    
    async loadTotalUsers() {
        this.dashboardData.totalUsers = { value: this.allUsers.length || MOCK_DATA.totalUsers.value, isMock: this.allUsers.length === 0 };
    },

    async loadTotalOrders() {
        this.dashboardData.totalOrders = { value: this.allOrders.length || MOCK_DATA.totalOrders.value, isMock: this.allOrders.length === 0 };
    },
    
    async loadTotalProducts() {
      try {
        const res = await axios.get(apiConfig.product.getAllProducts);
        if (res.data.code === 1) {
          const products = res.data.data || [];
          this.allProducts = products; // 保存所有商品数据
          this.dashboardData.totalProducts = { value: products.length, isMock: false };
        }
      } catch (error) {
        console.error('加载商品总数失败:', error);
      }
    },

    async loadTodaySales() {
      // 前端计算今日销售额
      if (this.allOrders.length > 0) {
        const todayStr = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        
        const todaySales = this.allOrders
            .filter(order => {
              const orderTime = order.orderTime || order.createTime;
              if (!orderTime) return false;
              const orderDate = new Date(orderTime).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
              return orderDate === todayStr;
            })
            .reduce((sum, order) => sum + (order.amount || 0), 0);
            
        this.dashboardData.todaySales = { value: todaySales, isMock: false };
      } else {
        // Fallback to mock
         this.dashboardData.todaySales = { value: MOCK_DATA.todaySales.value, isMock: true };
      }
    },

    async loadNewUsers() {
       // 前端计算今日新增用户
       if (this.allUsers.length > 0) {
        const todayStr = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        
        const count = this.allUsers.filter(user => {
            const time = user.createTime || user.registerTime;
            if (!time) return false;
            const date = new Date(time).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
            return date === todayStr;
        }).length;
        
        this.dashboardData.newUsers = { value: count, isMock: false };
       } else {
         this.dashboardData.newUsers = { value: MOCK_DATA.newUsers.value, isMock: true };
       }
    },

    async loadTodayOrders() {
      // 前端计算
      if (this.allOrders.length > 0) {
        const todayStr = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        
        const count = this.allOrders.filter(order => {
            const orderTime = order.orderTime || order.createTime;
            if (!orderTime) return false;
            const orderDate = new Date(orderTime).toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
            return orderDate === todayStr;
        }).length;
        
        this.dashboardData.todayOrders = { value: count, isMock: false };
      } else {
         this.dashboardData.todayOrders = { value: MOCK_DATA.todayOrders.value, isMock: true };
      }
    },

    async loadLowStockProducts() {
      const result = await fetchData(API_ENDPOINTS.lowStockProducts, MOCK_DATA.lowStockProducts, null, 0);
      this.dashboardData.lowStockProducts = { value: result.value, isMock: result.isMock };
    },

    // 计算订单趋势数据 (已改为从API获取)
    async calculateOrderTrendData(range) {
        // 使用真实API
        try {
            const url = range === 'week' ? apiConfig.order.getOrderTrendWeekly : apiConfig.order.getOrderTrendMonthly;
            const response = await axios.get(url);
            
            if (response.data.code === 1 && Array.isArray(response.data.data)) {
                const labels = [];
                const values = [];
                
                response.data.data.forEach(item => {
                    // item.date 格式为 "2026-01-01"
                    // 格式化日期显示，例如 "01-01" 或 "1日"
                    const dateObj = new Date(item.date);
                    const label = dateObj.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                    
                    labels.push(label);
                    values.push(item.daily_count || 0);
                });
                
                return { labels, values };
            }
        } catch (error) {
            console.error("Failed to fetch order trend:", error);
        }

        // 如果API失败，回退到前端计算 (保留原有逻辑作为兜底)
        console.warn("Falling back to frontend calculation for order trend");
        const now = new Date();
        const labels = [];
        const values = [];
        const dateMap = new Map();
        
        if (range === 'week') {
            // 本周 (周一到周日)
            const day = now.getDay() || 7; // Sunday is 0 -> 7
            const startOfWeek = new Date(now);
            startOfWeek.setDate(now.getDate() - day + 1); // Monday
            startOfWeek.setHours(0,0,0,0);
            
            for (let i = 0; i < 7; i++) {
                const d = new Date(startOfWeek);
                d.setDate(startOfWeek.getDate() + i);
                const label = d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                labels.push(label);
                dateMap.set(key, 0);
            }
        } else {
            // 本月 (1号到今天/月底)
            const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
            const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
            
            for (let i = 1; i <= daysInMonth; i++) {
                 const d = new Date(now.getFullYear(), now.getMonth(), i);
                 const label = d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                 const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                 labels.push(label);
                 dateMap.set(key, 0);
            }
        }
        
        // 填充数据
        this.allOrders.forEach(order => {
            const time = order.orderTime || order.createTime;
            if (time) {
                const d = new Date(time);
                const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                if (dateMap.has(key)) {
                    dateMap.set(key, dateMap.get(key) + 1);
                }
            }
        });
        
        return { labels, values: Array.from(dateMap.values()) };
    },

    // 计算用户增长数据 (已改为从API获取)
    async calculateUserGrowthData(range) {
        // 使用真实API
        try {
            const url = range === 'week' ? apiConfig.user.getUserGrowthWeekly : apiConfig.user.getUserGrowthMonthly;
            const response = await axios.get(url);
            
            if (response.data.code === 1 && Array.isArray(response.data.data)) {
                const labels = [];
                const values = [];
                
                response.data.data.forEach(item => {
                    const dateObj = new Date(item.date);
                    const label = dateObj.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                    
                    labels.push(label);
                    values.push(item.userCount || 0);
                });
                
                return { labels, values };
            }
        } catch (error) {
            console.error("Failed to fetch user growth trend:", error);
        }

        // 如果API失败，回退到前端计算
        console.warn("Falling back to frontend calculation for user growth");
        const now = new Date();
        const labels = [];
        const dateMap = new Map();
        
        if (range === 'week') {
            const day = now.getDay() || 7;
            const startOfWeek = new Date(now);
            startOfWeek.setDate(now.getDate() - day + 1);
            startOfWeek.setHours(0,0,0,0);
            
            for (let i = 0; i < 7; i++) {
                const d = new Date(startOfWeek);
                d.setDate(startOfWeek.getDate() + i);
                const label = d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                labels.push(label);
                dateMap.set(key, 0);
            }
        } else {
            const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
            for (let i = 1; i <= daysInMonth; i++) {
                 const d = new Date(now.getFullYear(), now.getMonth(), i);
                 const label = d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' });
                 const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                 labels.push(label);
                 dateMap.set(key, 0);
            }
        }
        
        this.allUsers.forEach(user => {
            const time = user.createTime || user.registerTime;
            if (time) {
                const d = new Date(time);
                const key = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
                if (dateMap.has(key)) {
                    dateMap.set(key, dateMap.get(key) + 1);
                }
            }
        });
        
        return { labels, values: Array.from(dateMap.values()) };
    },

    async updateOrderTrendChart() {
        const data = await this.calculateOrderTrendData(this.orderTrendRange);
        
        if (!data || !data.labels || !data.values) {
            console.warn('Order trend data is missing or invalid');
            return;
        }

        if (!this.orderTrendChart) {
             const dom = document.getElementById('order-trend-chart');
             if (!dom) return;
             // Ensure we dispose old instance if any (though this.orderTrendChart check handles most cases)
             const existingInstance = echarts.getInstanceByDom(dom);
             if (existingInstance) existingInstance.dispose();
             this.orderTrendChart = echarts.init(dom);
        }
        
        const option = {
            tooltip: {
                trigger: 'axis',
                formatter: '{b}<br />订单数: {c}'
            },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: data.labels,
                axisLine: { show: false },
                axisTick: { show: false }
            },
            yAxis: {
                type: 'value',
                splitLine: { lineStyle: { type: 'dashed' } }
            },
            series: [{
                name: '订单数',
                type: 'line',
                smooth: true, // 平滑曲线
                data: data.values,
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                      { offset: 0, color: 'rgba(59, 130, 246, 0.5)' },
                      { offset: 1, color: 'rgba(59, 130, 246, 0.0)' }
                    ])
                },
                itemStyle: { color: '#3b82f6' }
            }]
        };
        // Ensure data is valid before setting option to avoid "Cannot read properties of undefined (reading 'type')"
        if (data.values.some(v => v === undefined || v === null)) {
             console.warn('Order trend values contain null/undefined', data.values);
             // Fill with 0
             option.series[0].data = data.values.map(v => v || 0);
        }
        
        this.orderTrendChart.setOption(option);
    },

    async updateUserGrowthChart() {
        const data = await this.calculateUserGrowthData(this.userGrowthRange);
        
        if (!data || !data.labels || !data.values) {
             console.warn('User growth data is missing or invalid');
             return;
        }
        
        if (!this.userGrowthChart) {
             const dom = document.getElementById('user-growth-chart');
             if (!dom) return;
             const existingInstance = echarts.getInstanceByDom(dom);
             if (existingInstance) existingInstance.dispose();
             this.userGrowthChart = echarts.init(dom);
        }
        
        const option = {
            tooltip: {
                trigger: 'axis',
                formatter: '{b}<br />新增用户: {c}'
            },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: data.labels,
                axisLine: { show: false },
                axisTick: { show: false }
            },
            yAxis: {
                type: 'value',
                splitLine: { lineStyle: { type: 'dashed' } }
            },
            series: [{
                name: '新增用户',
                type: 'line',
                smooth: true,
                data: data.values,
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                      { offset: 0, color: 'rgba(0, 166, 90, 0.5)' },
                      { offset: 1, color: 'rgba(0, 166, 90, 0.0)' }
                    ])
                },
                itemStyle: { color: '#00a65a' }
            }]
        };
        
        if (data.values.some(v => v === undefined || v === null)) {
             option.series[0].data = data.values.map(v => v || 0);
        }

        this.userGrowthChart.setOption(option);
    },

    // Standardize province name
    normalizeProvinceName(name) {
      if (!name) return '';
      let normalized = name.trim();
      const provinceMap = {
        '北京': '北京市', '天津': '天津市', '上海': '上海市', '重庆': '重庆市',
        '河北': '河北省', '山西': '山西省', '辽宁': '辽宁省', '吉林': '吉林省', '黑龙江': '黑龙江省',
        '江苏': '江苏省', '浙江': '浙江省', '安徽': '安徽省', '福建': '福建省', '江西': '江西省', '山东': '山东省',
        '河南': '河南省', '湖北': '湖北省', '湖南': '湖南省', '广东': '广东省', '海南': '海南省',
        '四川': '四川省', '贵州': '贵州省', '云南': '云南省', '陕西': '陕西省', '甘肃': '甘肃省', '青海': '青海省', '台湾': '台湾省',
        '内蒙古': '内蒙古自治区', '广西': '广西壮族自治区', '西藏': '西藏自治区', '宁夏': '宁夏回族自治区', '新疆': '新疆维吾尔自治区',
        '香港': '香港特别行政区', '澳门': '澳门特别行政区'
      };
      if (Object.values(provinceMap).includes(normalized)) return normalized;
      if (provinceMap[normalized]) return provinceMap[normalized];
      for (const [key, value] of Object.entries(provinceMap)) {
        if (normalized.startsWith(key)) return value;
      }
      return normalized;
    },

    async loadUserDistribution() {
        // 使用前端统计所有订单
        const provinceMap = {};
        this.allOrders.forEach(order => {
             const province = order.province || order.receiverProvince || (order.receiverAddress || '').substring(0, 2);
             if (province) {
                 const normalized = this.normalizeProvinceName(province);
                 if (normalized) {
                     provinceMap[normalized] = (provinceMap[normalized] || 0) + 1;
                 }
             }
        });
        
        const data = Object.keys(provinceMap).map(key => ({ name: key, value: provinceMap[key] }));
        this.provinceOrders = data;
        this.dashboardData.userDistribution = { data, isMock: false };
        
        this.$nextTick(() => { this.initChinaMap(); });
    },
    
    async loadRecentActivities() {
        let activities = [];
        
        // Orders
        this.allOrders.forEach(order => {
             activities.push({
                 id: 'order-' + order.oid,
                 type: 'order',
                 text: `用户 ${order.receiverName || '未知'} 下单 ¥${order.amount}`,
                 timestamp: new Date(order.orderTime || order.createTime).getTime(),
                 color: '#00c0ef'
             });
        });
        
        // Users
        this.allUsers.forEach(user => {
             activities.push({
                 id: 'user-' + user.uid,
                 type: 'user',
                 text: `新用户 ${user.username} 注册`,
                 timestamp: new Date(user.createTime).getTime(),
                 color: '#00a65a'
             });
        });

        // Products Promotion
        if (this.allProducts && this.allProducts.length > 0) {
            this.allProducts.forEach(product => {
                 const points = (product.rating || product.heat || 100) - 100;
                 if (points > 0) {
                     activities.push({
                         id: 'promo-' + product.pid,
                         type: 'promotion',
                         text: `商家为 [${product.name}] 投入了 ${points} 推广积分`,
                         timestamp: new Date(product.updateTime || Date.now()).getTime(),
                         color: '#f39c12'
                     });
                 }
            });
        }
        
        // Sort
        activities.sort((a, b) => b.timestamp - a.timestamp);
        activities = activities.slice(0, 20);
        
        // Format time
        const now = Date.now();
        activities.forEach(item => {
             const diff = now - item.timestamp;
             if (diff < 60000) item.time = '刚刚';
             else if (diff < 3600000) item.time = Math.floor(diff/60000) + '分钟前';
             else if (diff < 86400000) item.time = Math.floor(diff/3600000) + '小时前';
             else item.time = new Date(item.timestamp).toLocaleDateString();
        });
        
        this.dashboardData.recentActivities = { data: activities, isMock: false };
    },
    
    async initChinaMap() {
      try {
        const mapDataUrl = 'https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json';
        const response = await axios.get(mapDataUrl);
        echarts.registerMap('china', response.data);
        const chartDom = document.getElementById('china-map-chart');
        if (!chartDom) return;
        this.chinaMapChart = echarts.init(chartDom);
        const option = {
          tooltip: { trigger: 'item', formatter: '{b}<br/>订单数: {c}' },
          visualMap: {
            min: 0, 
            max: Math.max(...this.provinceOrders.map(p => p.value), 10), 
            left: '30', 
            bottom: '30', 
            text: ['高', '低'], 
            itemWidth: 10,
            itemHeight: 50,
            calculable: true,
            inRange: { color: ['#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027', '#a50026'] }
          },
          series: [{
            name: '订单数量', type: 'map', map: 'china', roam: false, zoom: 1.75,center:[105,36],
            label: { show: true, fontSize: 10 },
            data: this.provinceOrders,
            itemStyle: { areaColor: '#f3f3f3', borderColor: '#ccc' },
            emphasis: { itemStyle: { areaColor: '#f39c12' } }
          }]
        };
        this.chinaMapChart.setOption(option);
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
