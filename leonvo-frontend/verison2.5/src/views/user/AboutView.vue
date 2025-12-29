<!--已经改过 -->
<template>
  <div class="about">
    <!-- 可以添加更多内容或调用后端API获取关于信息 -->
    <div class="about-container">
      <h1>关于泡泡联盟联想电脑商城</h1>
      <div class="about-content">
        <p>欢迎来到泡泡联盟联想电脑商城，我们致力于提供最优质的绿色科技产品。</p>

        <!-- 如果需要从后端获取关于信息，可以在这里添加API调用 -->
        <!-- <div v-if="aboutInfo">
          {{ aboutInfo.content }}
        </div> -->

        <div class="contact-info">
          <h3>联系我们</h3>
          <p>邮箱: support@lenovo-mall.com</p>
          <p>电话: 400-100-5678</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AboutView',
  data() {
    return {
      // aboutInfo: null // 如果需要从后端获取关于信息
    };
  },
  // mounted() {
  //   // 如果需要从后端获取关于信息
  //   this.fetchAboutInfo();
  // },
  // methods: {
  //   async fetchAboutInfo() {
  //     try {
  //       // API 缺失: 获取关于页面信息接口
  //       // const response = await fetch('/api/about');
  //       // this.aboutInfo = await response.json();
  //       console.warn('缺少获取关于信息API接口');
  //     } catch (error) {
  //       console.error('获取关于信息失败:', error);
  //     }
  //   }
  // }
};
</script>

<style scoped>
.about {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: 70vh;
}

.about-container {
  background: white;
  padding: 40px;
  border: 2px solid #4CAF50;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.1);
  border-radius: 0;
}

.about-container h1 {
  color: #2E7D32;
  text-align: center;
  margin-bottom: 30px;
  font-size: 36px;
}

.about-content {
  color: #333;
  line-height: 1.8;
  font-size: 16px;
}

.about-content p {
  margin-bottom: 20px;
}

.contact-info {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 2px solid #C8E6C9;
}

.contact-info h3 {
  color: #2E7D32;
  margin-bottom: 15px;
}

.contact-info p {
  margin: 10px 0;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about-container {
    padding: 30px 20px;
  }

  .about-container h1 {
    font-size: 28px;
  }
}
</style>