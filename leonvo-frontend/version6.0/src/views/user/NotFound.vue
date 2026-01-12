<!--已经改过 -->
<template>
  <div class="not-found">
    <div class="not-found-container">
      <div class="error-code">404</div>
      <h1>页面未找到</h1>
      <p>抱歉，您访问的页面不存在。</p>
      <div class="action-buttons">
        <router-link to="/" class="btn-home">
          返回首页
        </router-link>
        <button @click="goBack" class="btn-back">
          返回上一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  padding: 20px;
}

.not-found-container {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border: 2px solid #2196f3;
  box-shadow: 0 20px 60px rgba(33, 150, 243, 0.15);
  max-width: 500px;
  width: 100%;
  border-radius: 0;
}

.error-code {
  font-size: 120px;
  font-weight: 900;
  color: #2196f3;
  margin-bottom: 20px;
  line-height: 1;
}

.not-found h1 {
  color: #1565c0;
  margin: 0 0 15px 0;
  font-size: 32px;
}

.not-found p {
  color: #666;
  font-size: 18px;
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-home, .btn-back {
  padding: 15px 35px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  border-radius: 0;
}

.btn-home {
  background: linear-gradient(135deg, #2196f3 0%, #1565c0 100%);
  color: white;
}

.btn-home:hover {
  background: linear-gradient(135deg, #64b5f6 0%, #1976d2 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.3);
}

.btn-back {
  background: linear-gradient(135deg, #90caf9 0%, #2196f3 100%);
  color: white;
}

.btn-back:hover {
  background: linear-gradient(135deg, #bbdefb 0%, #64b5f6 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(144, 202, 249, 0.3);
}

/* 响应式设计 */
@media (max-width: 600px) {
  .not-found-container {
    padding: 40px 20px;
  }

  .error-code {
    font-size: 80px;
  }

  .not-found h1 {
    font-size: 24px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 15px;
  }

  .btn-home, .btn-back {
    width: 100%;
  }
}
</style>