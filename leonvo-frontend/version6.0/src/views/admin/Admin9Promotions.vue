<template>
  <div class="admin-promotions">
    <div class="content-header">
      <h1>
        推广管理
        <small>商品推广情况监控</small>
      </h1>
      <ol class="breadcrumb">
        <li><router-link to="/admin/dashboard"><i class="fa fa-dashboard"></i> 首页</router-link></li>
        <li class="active">推广管理</li>
      </ol>
    </div>

    <div class="content">
      <div class="row">
        <div class="col-xs-12">
          <div class="box box-primary">
            <div class="box-header with-border">
              <h3 class="box-title">推广商品排行 (Top 50)</h3>
              <div class="box-tools pull-right">
                <div class="has-feedback">
                  <input type="text" class="form-control input-sm" placeholder="搜索商品..." v-model="searchKeyword">
                  <span class="glyphicon glyphicon-search form-control-feedback"></span>
                </div>
              </div>
            </div>
            
            <div class="box-body table-responsive no-padding">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th style="width: 60px;">排名</th>
                    <th>商品名称</th>
                    <th>所属商家</th>
                    <th>当前热度 (积分)</th>
                    <th>推广状态</th>
                    <th>大屏展示</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(product, index) in filteredProducts" :key="product.pid">
                    <td>
                      <span class="badge" :class="getRankBadgeClass(index)">{{ index + 1 }}</span>
                    </td>
                    <td>
                      <div class="product-info-compact">
                        <div class="product-icon-wrapper">
                          <!-- 统一使用通用图标 -->
                          <span class="product-icon-placeholder">💻</span>
                        </div>
                        <div class="product-name-wrapper">
                          <div class="product-name-text">{{ product.name }}</div>
                          <div class="product-price-text">¥{{ product.price }}</div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span v-if="product.merchantName" class="merchant-tag" :title="'商家ID: ' + product.bid">
                        {{ product.merchantName }}
                      </span>
                      <span v-else class="text-muted" :title="'商家ID: ' + product.bid">
                        未知商家 (ID: {{ product.bid }})
                      </span>
                    </td>
                    <td>
                      <div class="progress-group">
                        <span class="progress-text">{{ product.heat }}</span>
                        <div class="progress sm">
                          <div class="progress-bar" 
                               :class="product.heat >= 150 ? 'progress-bar-red' : 'progress-bar-aqua'"
                               :style="{ width: getHeatPercentage(product.heat) + '%' }"></div>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span v-if="product.heat > 100" class="text-orange">
                        <i class="fa fa-fire"></i> 推广中 (+{{ product.heat - 100 }})
                      </span>
                      <span class="text-muted" v-else>未推广</span>
                    </td>
                    <td>
                      <span class="label" :class="product.heat >= 150 ? 'label-danger' : 'label-warning'">
                        {{ product.heat >= 150 ? '已上大屏' : `还差 ${150 - product.heat} 分` }}
                      </span>
                    </td>
                  </tr>
                  <tr v-if="filteredProducts.length === 0">
                    <td colspan="6" class="text-center text-muted" style="padding: 20px;">
                      {{ loading ? '加载中...' : '暂无推广数据' }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="box-footer clearfix">
              <ul class="pagination pagination-sm no-margin pull-right">
                <li :class="{ disabled: currentPage === 1 }"><a href="#" @click.prevent="prevPage">&laquo;</a></li>
                <li v-for="p in totalPages" :key="p" :class="{ active: currentPage === p }">
                  <a href="#" @click.prevent="currentPage = p">{{ p }}</a>
                </li>
                <li :class="{ disabled: currentPage === totalPages }"><a href="#" @click.prevent="nextPage">&raquo;</a></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import apiConfig from '@/config/api.config';

export default {
  name: 'AdminPromotions',
  data() {
    return {
      products: [],
      businesses: [],
      loading: false,
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10
    };
  },
  computed: {
    // 过滤并排序商品
    processedProducts() {
      // 1. 过滤掉没有评分或评分<=100的商品（只显示有推广行为的）
      // 必须严格大于100分，才视为有推广
      let list = this.products.filter(p => (p.rating || p.heat || 0) > 100).map(p => {
        // 尝试匹配商家名称
        // 注意：这里必须确保商家ID类型匹配 (String vs Number)
        const merchant = this.businesses.find(b => String(b.bid) === String(p.bid) || String(b.id) === String(p.bid));
        return {
          ...p,
          heat: p.rating || p.heat || 100, // 确保有值
          merchantName: merchant ? merchant.name : null
        };
      });

      // 2. 按热度降序排序
      list.sort((a, b) => b.heat - a.heat);

      // 3. 搜索过滤
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase();
        list = list.filter(p => 
          p.name.toLowerCase().includes(keyword) || 
          (p.merchantName && p.merchantName.toLowerCase().includes(keyword))
        );
      }

      return list;
    },
    
    filteredProducts() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.processedProducts.slice(start, start + this.pageSize);
    },
    
    totalPages() {
      return Math.ceil(this.processedProducts.length / this.pageSize) || 1;
    }
  },
  mounted() {
    this.loadData();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        // 1. 获取所有商品和所有商家
        const [productsRes, businessesRes] = await Promise.all([
          axios.get(apiConfig.product.getAllProducts),
          axios.get(apiConfig.business.getAllBusinesses)
        ]);

        let allBusinesses = [];
        if (businessesRes.data.code === 1) {
          allBusinesses = (businessesRes.data.data || []).map(b => ({
            ...b,
            bid: b.bid || b.id || b.businessId,
            name: b.bname || b.businessName || b.name
          }));
          this.businesses = allBusinesses;
        }

        // 2. 构建 pid -> bid 的映射表
        // 由于商品表可能没有直接存bid，我们需要通过“查询每个商家的商品列表”来反向建立关联
        // 这需要遍历所有商家，虽然请求多一点，但是能拿到真实的关联关系
        const productOwnerMap = new Map(); // pid -> bid

        if (allBusinesses.length > 0) {
          // 并发请求所有商家的商品列表
          const productRequests = allBusinesses.map(b => 
            axios.get(apiConfig.business.getProducts(b.bid)).then(res => ({
              bid: b.bid,
              products: res.data.code === 1 ? res.data.data : []
            })).catch(() => ({ bid: b.bid, products: [] }))
          );
          
          const results = await Promise.all(productRequests);
          
          results.forEach(item => {
            if (item.products && Array.isArray(item.products)) {
              item.products.forEach(p => {
                // 记录这个商品属于哪个商家
                productOwnerMap.set(String(p.pid), item.bid);
              });
            }
          });
        }

        // 3. 处理商品列表
        if (productsRes.data.code === 1) {
          this.products = (productsRes.data.data || []).map(p => {
            // 优先使用 map 中查到的真实 bid，如果没查到，再尝试用 p.bid
            const realBid = productOwnerMap.get(String(p.pid)) || p.bid || p.businessId;
            
            return {
              ...p,
              bid: realBid, // 更新为真实 bid
              heat: p.heat || p.rating || 100
            };
          });
        }
        
      } catch (error) {
        console.error('加载推广数据失败:', error);
      } finally {
        this.loading = false;
      }
    },
    
    getRankBadgeClass(index) {
      if (index === 0) return 'bg-red';
      if (index === 1) return 'bg-yellow';
      if (index === 2) return 'bg-blue';
      return 'bg-gray';
    },
    
    getHeatPercentage(heat) {
      // 调整比例：100分是起点(0%)，150分是满点(100%)
      const max = 150;
      const min = 100;
      // 既然只显示 > 100 的，那么 heat 至少是 101
      if (heat <= min) return 0;
      if (heat >= max) return 100;
      
      const percent = ((heat - min) / (max - min)) * 100;
      return percent;
    },
    
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++;
    }
  }
};
</script>

<style scoped>
.admin-promotions {
  min-height: 100%;
}

.content-header {
  padding: 15px 15px 0 15px;
  margin-bottom: 20px;
}

.content-header > h1 {
  margin: 0;
  font-size: 24px;
}

.content-header > h1 > small {
  font-size: 15px;
  display: inline-block;
  padding-left: 4px;
  font-weight: 300;
}

.breadcrumb {
  float: right;
  background: transparent;
  margin-top: 0;
  margin-bottom: 0;
  font-size: 12px;
  padding: 7px 5px;
  position: absolute;
  top: 15px;
  right: 15px;
  border-radius: 2px;
}

.content {
  padding: 15px;
}

/* Box Styles */
.box {
  position: relative;
  border-radius: 3px;
  background: #ffffff;
  border-top: 3px solid #d2d6de;
  margin-bottom: 20px;
  width: 100%;
  box-shadow: 0 1px 1px rgba(0,0,0,0.1);
}

.box.box-primary {
  border-top-color: #3c8dbc;
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

.box-tools {
  position: absolute;
  right: 10px;
  top: 5px;
}

.box-body {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
  padding: 10px;
}

.box-footer {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  border-bottom-right-radius: 3px;
  border-bottom-left-radius: 3px;
  border-top: 1px solid #f4f4f4;
  padding: 10px;
  background-color: #ffffff;
  /* 使用flex布局解决高度塌陷和对齐问题 */
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* 强制分页右对齐 */
.box-footer .pagination {
  margin: 0;
  /* 移除float，交由flex控制 */
  display: flex; 
}

.table-responsive {
  min-height: .01%;
  overflow-x: auto;
}

.no-padding {
  padding: 0 !important;
}

/* Table Styles */
.table {
  width: 100%;
  max-width: 100%;
  margin-bottom: 20px;
  border-collapse: collapse;
}

.table > thead > tr > th,
.table > tbody > tr > td {
  border-top: 1px solid #f4f4f4;
  padding: 12px;
  vertical-align: middle;
}

/* Compact Product Info */
.product-info-compact {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: #f9f9f9;
  border: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.product-icon-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.product-icon-placeholder {
  font-size: 20px;
}

.product-name-wrapper {
  display: flex;
  flex-direction: column;
}

.product-name-text {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.product-price-text {
  font-size: 12px;
  color: #999;
}

/* Merchant Tag */
.merchant-tag {
  color: #666;
  font-size: 13px;
}

.merchant-tag-default {
  color: #999;
  font-style: italic;
  font-size: 13px;
}

.badge {
  display: inline-block;
  min-width: 10px;
  padding: 3px 7px;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  vertical-align: middle;
  border-radius: 10px;
}

.bg-red { background-color: #dd4b39 !important; }
.bg-yellow { background-color: #f39c12 !important; }
.bg-blue { background-color: #0073b7 !important; }
.bg-gray { background-color: #d2d6de !important; color: #444 !important; }

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

.label-success { background-color: #00a65a !important; }
.label-default { background-color: #d2d6de !important; color: #444 !important; }
.label-danger { background-color: #dd4b39 !important; }
.label-warning { background-color: #f39c12 !important; }

.text-orange { color: #ff851b !important; font-weight: bold; }
.text-muted { color: #777; }

/* Progress Bar */
.progress-group {
  width: 150px;
}
.progress-text {
  font-weight: 600;
}
.progress {
  height: 10px;
  margin-bottom: 0;
  border-radius: 5px;
  background-color: #f5f5f5;
  overflow: hidden;
  box-shadow: inset 0 1px 2px rgba(0,0,0,.1);
}
.progress-bar {
  float: left;
  width: 0;
  height: 100%;
  font-size: 12px;
  line-height: 20px;
  color: #fff;
  text-align: center;
  background-color: #337ab7;
  transition: width .6s ease;
}
.progress-bar-aqua { background-color: #00c0ef !important; }

/* Pagination */
.pagination {
  display: inline-block;
  padding-left: 0;
  margin: 20px 0;
  border-radius: 4px;
}
.pagination > li { display: inline; }
.pagination > li > a {
  position: relative;
  float: left;
  padding: 6px 12px;
  margin-left: -1px;
  line-height: 1.42857143;
  color: #337ab7;
  text-decoration: none;
  background-color: #fff;
  border: 1px solid #ddd;
}
.pagination > li.active > a {
  z-index: 2;
  color: #fff;
  cursor: default;
  background-color: #337ab7;
  border-color: #337ab7;
}
.pagination > li.disabled > a {
  color: #777;
  cursor: not-allowed;
  background-color: #fff;
  border-color: #ddd;
}
</style>