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
                <!-- <div class="product-sales">销量: {{ product.sales_count || 0 }}件</div> -->
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
                {{ (order.items && order.items[0]) ? order.items[0].name : '商品' }}
                <span v-if="order.items && order.items[0] && order.items[0].quantity > 1" class="quantity">×{{ order.items[0].quantity }}</span>
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
      salesRange: 'week',
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

        // 2.1 建立商品ID和名称映射表
        const productMap = {};
        const nameToPidMap = {}; // 新增：名称到PID的映射
        products.forEach(p => {
            if (p.pid) {
                productMap[p.pid] = p;
                if (p.name) nameToPidMap[p.name] = p.pid;
            }
        });

        // 2.2 准备补全订单信息的逻辑
        const detailPromises = [];

        // 先扫描一遍，找出需要补全详情的订单
        orders.forEach(order => {
            let hasValidInfo = false;
            
            // 检查是否有有效的商品信息
            if (order.items && Array.isArray(order.items) && order.items.length > 0) {
                 // 只要有一个商品有名字且不是占位符，就认为有效
                 const firstItem = order.items[0];
                 if (firstItem.name && firstItem.name !== '商品' && firstItem.name !== '默认商品' && firstItem.name !== '未知商品') {
                     hasValidInfo = true;
                 }
            } else if (order.product_name || order.productName) {
                hasValidInfo = true;
            }
            
            // 如果信息不全，且有OID，加入补全队列
            if (!hasValidInfo && (order.oid || order.id)) {
                const oid = order.oid || order.id;
                // 尝试从本地 productMap 反查 (如果有 pid)
                let foundInLocal = false;
                if ((order.pid || order.productId) && productMap[order.pid || order.productId]) {
                    // 如果本地有，直接补全，不用请求接口
                    const p = productMap[order.pid || order.productId];
                    if (!order.items || !order.items.length) {
                        order.items = [{ name: p.name, quantity: order.quantity || 1, pid: p.pid }];
                    } else {
                        order.items[0].name = p.name;
                        order.items[0].pid = p.pid;
                    }
                    order.productName = p.name;
                    foundInLocal = true;
                }
                
                // 如果本地也没找到，请求详情接口
                if (!foundInLocal) {
                    detailPromises.push(
                        axios.get(apiConfig.order.getOrderDetail(oid))
                            .then(res => {
                                if (res.data.code === 1 && res.data.data) {
                                    const detail = res.data.data;
                                    // 提取商品名和ID
                                    let name = '';
                                    let pid = '';
                                    let price = 0;
                                    
                                    if (detail.items && detail.items.length) {
                                        name = detail.items[0].name;
                                        pid = detail.items[0].id || detail.items[0].pid;
                                        price = detail.items[0].price;
                                    } else if (detail.product_name) {
                                        name = detail.product_name;
                                    }
                                    
                                    if (name) {
                                        // 更新订单对象
                                        order.productName = name;
                                        if (!order.items || !order.items.length) {
                                            order.items = [{ 
                                                name: name, 
                                                quantity: 1, 
                                                pid: pid, 
                                                price: price 
                                            }];
                                        } else {
                                            order.items[0].name = name;
                                            if (pid) order.items[0].pid = pid;
                                            if (price) order.items[0].price = price;
                                        }
                                    }
                                }
                            })
                            .catch(e => console.warn(`补全订单 ${oid} 详情失败`, e))
                    );
                }
            }
        });

        // 等待所有详情补全完成
        if (detailPromises.length > 0) {
            console.log(`正在补全 ${detailPromises.length} 个订单的商品信息...`);
            await Promise.allSettled(detailPromises);
            console.log('订单信息补全完成，开始计算销量');
        }

        // 2.3 计算每个商品的销量 (确保在补全后执行)
        const salesMap = {};
        
        orders.forEach(order => {
            // 归一化处理 items
            let items = [];
            if (order.items && Array.isArray(order.items)) {
                items = order.items;
            } else if (order.products && Array.isArray(order.products)) {
                items = order.products;
            } else if (order.pid || order.productName) {
                // 单商品结构
                items = [{
                    pid: order.pid,
                    name: order.productName,
                    quantity: order.quantity || 1
                }];
            }

            items.forEach(item => {
                const qty = parseInt(item.quantity || item.count || 1);
                
                // 1. 优先使用 PID 匹配
                if (item.pid || item.id) {
                    const pidStr = String(item.pid || item.id);
                    salesMap[pidStr] = (salesMap[pidStr] || 0) + qty;
                } 
                // 2. 其次使用名称匹配 (去除空格，忽略大小写)
                else if (item.name) {
                    const cleanName = item.name.trim();
                    // 尝试从 nameToPidMap 找 ID
                    if (nameToPidMap[cleanName]) {
                        const pidStr = String(nameToPidMap[cleanName]);
                        salesMap[pidStr] = (salesMap[pidStr] || 0) + qty;
                    } else {
                        // 找不到 ID 就直接存名字
                        salesMap[cleanName] = (salesMap[cleanName] || 0) + qty;
                    }
                }
            });
        });

        console.log('销量统计结果:', salesMap);

        // 将销量合并到商品数据中
        products = products.map(p => {
            const pidStr = String(p.pid);
            // 尝试通过 ID 获取
            let totalSales = salesMap[pidStr] || 0;
            
            // 尝试通过名称获取 (防止 ID 不匹配的情况)
            if (p.name) {
                totalSales += (salesMap[p.name.trim()] || 0);
            }
            
            return {
                ...p,
                sales_count: totalSales || p.sales_count || 0
            };
        });

        // 3. 计算统计信息
        const totalSales = orders.reduce((sum, order) => sum + (order.amount || 0), 0);

        // 计算今日销售额
        const today = new Date();
        // Fix: Use local date string to match order dates correctly in local timezone
        const todayStr = today.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
        
        const todaySales = orders
            .filter(order => {
              const orderTime = order.order_time || order.orderTime;
              if (!orderTime) return false;
              const orderDate = new Date(orderTime);
              const orderDateStr = orderDate.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
              return orderDateStr === todayStr;
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

        // 5. 获取热门商品 (按销售额排序: 销量 * 价格)
        this.topProducts = products
            // .filter(p => p.status === 1) // 用户希望能看到所有热门商品，即使已下架
            .sort((a, b) => {
               const revenueA = (a.sales_count || 0) * (a.price || 0);
               const revenueB = (b.sales_count || 0) * (b.price || 0);
               return revenueB - revenueA;
            })
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
            .map(order => {
              // 尝试从不同字段获取商品列表
              let items = [];
              if (order.items && order.items.length) items = order.items;
              else if (order.products && order.products.length) items = order.products;
              else if (order.orderItems && order.orderItems.length) items = order.orderItems;
              else if (order.order_items && order.order_items.length) items = order.order_items;
              else if (order.goods && order.goods.length) items = order.goods;
              else if (order.product) items = [order.product];
              
              // 如果还是没有，尝试从扁平字段构建
              if (items.length === 0) {
                  // 优先使用 PID 反查
                  if ((order.pid || order.productId) && productMap[order.pid || order.productId]) {
                      const product = productMap[order.pid || order.productId];
                      items = [{
                          name: product.name,
                          quantity: order.quantity || order.count || 1,
                          pid: product.pid
                      }];
                  } 
                  // 其次尝试使用名称字段
                  else if (order.product_name || order.productName) {
                      items = [{
                          name: order.product_name || order.productName,
                          quantity: order.quantity || order.count || 1
                      }];
                  }
              }

              return {
                  oid: order.oid || order.id,
                  amount: order.amount || 0,
                  status: order.status || 0,
                  order_time: order.order_time || order.orderTime,
                  items: items.length ? items : [{ name: '商品', quantity: 1 }]
              };
            });
            
        // 4. 获取订单趋势 (放在最后渲染，确保DOM准备就绪)
        // 必须先结束loading状态，让v-else显示出来，才能获取canvas context
        this.loading = false;
        this.$nextTick(() => {
            const trendData = this.calculateOrderTrend(orders, this.salesRange);
            this.renderSalesChart(trendData);
        });

      } catch (error) {
        console.error('加载仪表盘数据失败:', error);
        this.useMockData();
        this.loading = false;
      } 
      // finally {
      //   this.loading = false; // Moved inside try/catch to handle render timing
      // }
    },

    calculateOrderTrend(orders, rangeType) {
      const today = new Date();
      let startDate = new Date(today);
      let endDate = new Date(today);
      let labels = [];
      let data = [];
      let tooltips = []; // To store date and count for tooltip

      if (rangeType === 'week') {
        // Current Week (Monday to Sunday)
        const day = startDate.getDay() || 7; // Get current day number, make Sunday 7
        if (day !== 1) startDate.setHours(-24 * (day - 1)); // Go back to Monday
        else startDate.setHours(0,0,0,0);
        
        // Generate labels for Mon-Sun
        for (let i = 0; i < 7; i++) {
          const d = new Date(startDate);
          d.setDate(startDate.getDate() + i);
          // Only up to today? User said "Current week", usually implies whole week view, but future days are 0
          labels.push(d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }));
          
          const dateStr = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
          
          const count = orders.filter(o => {
             const oTime = o.order_time || o.orderTime;
             if(!oTime) return false;
             const oDate = new Date(oTime);
             const oDateStr = oDate.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
             return oDateStr === dateStr;
          }).length;
          
          data.push(count);
          tooltips.push({ date: dateStr, count: count });
        }
      } else {
        // Current Month
        startDate.setDate(1); // 1st of month
        const daysInMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0).getDate();
        
        for (let i = 1; i <= daysInMonth; i++) {
          const d = new Date(today.getFullYear(), today.getMonth(), i);
          labels.push(d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }));
          
          const dateStr = d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
          
           const count = orders.filter(o => {
             const oTime = o.order_time || o.orderTime;
             if(!oTime) return false;
             const oDate = new Date(oTime);
             const oDateStr = oDate.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).replace(/\//g, '-');
             return oDateStr === dateStr;
          }).length;
          
          data.push(count);
          tooltips.push({ date: dateStr, count: count });
        }
      }

      return { labels, values: data, tooltips };
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
          labels: data.labels,
          datasets: [{
            label: '订单数',
            data: data.values,
            borderColor: '#3b82f6', // Blue like the image
            backgroundColor: (context) => {
              const ctx = context.chart.ctx;
              const gradient = ctx.createLinearGradient(0, 0, 0, 300);
              gradient.addColorStop(0, 'rgba(59, 130, 246, 0.5)');
              gradient.addColorStop(1, 'rgba(59, 130, 246, 0.0)');
              return gradient;
            },
            borderWidth: 3,
            fill: true,
            tension: 0.4, // Smooth curve
            pointBackgroundColor: '#ffffff',
            pointBorderColor: '#3b82f6',
            pointBorderWidth: 2,
            pointRadius: 4,
            pointHoverRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: {
            intersect: false,
            mode: 'index',
          },
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              backgroundColor: '#3b82f6',
              titleColor: '#fff',
              bodyColor: '#fff',
              displayColors: false,
              callbacks: {
                title: (context) => {
                   return data.tooltips[context[0].dataIndex].date;
                },
                label: (context) => {
                   return context.parsed.y + ' 单';
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              grid: {
                color: 'rgba(0, 0, 0, 0.05)',
                drawBorder: false
              },
              ticks: {
                stepSize: 1
              }
            },
            x: {
              grid: {
                display: false,
                drawBorder: false
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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
  border-radius: 0;
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