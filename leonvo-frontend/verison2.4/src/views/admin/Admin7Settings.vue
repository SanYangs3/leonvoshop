<template>
  <div class="management-section">
    <div class="settings-container">
      <div class="settings-card">
        <h3>系统设置</h3>
        <div class="setting-item">
          <label>商城名称</label>
          <div class="setting-info">泡泡联盟联想电脑商城</div>
        </div>
        <div class="setting-item">
          <label>客服电话</label>
          <div class="setting-info">400-123-4567</div>
        </div>
        <div class="setting-item">
          <label>邮箱</label>
          <div class="setting-info">support@lenovo.com</div>
        </div>
        <div class="setting-note">
          注：如需修改设置，请联系系统开发人员。
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Admin7Settings'
}
</script>

<style scoped>
.management-section {
  padding: 20px;
  background-color: #f5f5f7;
  min-height: 100vh;
}

.settings-container {
  max-width: 800px;
  margin: 0 auto;
}

.settings-card {
  background-color: white;
  padding: 30px;
  border-radius: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.settings-card h3 {
  margin-bottom: 30px;
  font-size: 20px;
  color: #333;
  font-weight: 600;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.setting-item {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.setting-item label {
  width: 120px;
  font-weight: 500;
  color: #333;
  font-size: 15px;
}

.setting-info {
  flex: 1;
  padding: 10px 14px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #495057;
  font-size: 15px;
  min-height: 40px;
  display: flex;
  align-items: center;
  border-radius: 0;
}

.setting-note {
  margin-top: 40px;
  padding: 16px;
  background-color: #f8f9fa;
  border-left: 4px solid #6c757d;
  color: #6c757d;
  font-size: 14px;
  border-radius: 0;
}
</style>