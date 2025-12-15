<template>
  <div class="footer-page">
    <div class="page-header">
      <button @click="goBack" class="back-button">
        <span class="back-icon">←</span>
        返回
      </button>
      <h1>📜 网站政策</h1>
      <p class="page-subtitle">了解我们的隐私政策和网站使用条款</p>
    </div>

    <div class="page-content">
      <div class="policy-tabs">
        <div class="tabs-header">
          <button
              @click="activeTab = 'privacy'"
              :class="['tab-button', { active: activeTab === 'privacy' }]"
          >
            <span class="tab-icon">🔒</span>
            隐私政策
          </button>
          <button
              @click="activeTab = 'cookies'"
              :class="['tab-button', { active: activeTab === 'cookies' }]"
          >
            <span class="tab-icon">🍪</span>
            Cookie政策
          </button>
          <button
              @click="activeTab = 'security'"
              :class="['tab-button', { active: activeTab === 'security' }]"
          >
            <span class="tab-icon">🛡️</span>
            安全政策
          </button>
        </div>

        <div class="tabs-content">
          <!-- 隐私政策 -->
          <div v-if="activeTab === 'privacy'" class="tab-panel">
            <h2><span class="icon">🔒</span> 隐私政策</h2>
            <div class="policy-section">
              <h3>1. 信息收集</h3>
              <p>我们收集的信息包括但不限于：</p>
              <ul>
                <li>您注册时提供的个人信息（姓名、邮箱、电话等）</li>
                <li>您的浏览记录和购物行为</li>
                <li>设备信息和访问日志</li>
                <li>支付信息（由第三方支付平台处理）</li>
              </ul>
            </div>
            <div class="policy-section">
              <h3>2. 信息使用</h3>
              <p>我们使用您的信息主要用于：</p>
              <ul>
                <li>提供和优化我们的服务</li>
                <li>处理订单和发货</li>
                <li>发送重要的服务通知</li>
                <li>改进产品和服务质量</li>
                <li>进行数据分析和研究</li>
              </ul>
            </div>
            <div class="policy-section">
              <h3>3. 信息保护</h3>
              <p>我们采取多种安全措施保护您的信息：</p>
              <ul>
                <li>使用SSL加密传输数据</li>
                <li>定期进行安全审计</li>
                <li>限制员工访问权限</li>
                <li>数据备份和恢复机制</li>
              </ul>
            </div>
          </div>

          <!-- Cookie政策 -->
          <div v-if="activeTab === 'cookies'" class="tab-panel">
            <h2><span class="icon">🍪</span> Cookie政策</h2>
            <div class="policy-section">
              <h3>什么是Cookie？</h3>
              <p>Cookie是网站存储在您设备上的小型文本文件，用于改善您的浏览体验。</p>
            </div>
            <div class="policy-section">
              <h3>我们使用的Cookie类型</h3>
              <div class="cookie-types">
                <div class="cookie-type">
                  <h4>必要Cookie</h4>
                  <p>确保网站基本功能正常运行，无法关闭。</p>
                </div>
                <div class="cookie-type">
                  <h4>偏好Cookie</h4>
                  <p>记住您的偏好设置，如语言选择。</p>
                </div>
                <div class="cookie-type">
                  <h4>统计Cookie</h4>
                  <p>帮助我们了解用户如何使用网站。</p>
                </div>
                <div class="cookie-type">
                  <h4>营销Cookie</h4>
                  <p>用于展示相关广告内容。</p>
                </div>
              </div>
            </div>
            <div class="policy-section">
              <h3>如何管理Cookie</h3>
              <p>您可以通过浏览器设置管理Cookie：</p>
              <ul>
                <li>Chrome：设置 > 隐私和安全 > Cookie和其他网站数据</li>
                <li>Firefox：选项 > 隐私与安全 > Cookie</li>
                <li>Safari：偏好设置 > 隐私 > Cookie</li>
              </ul>
            </div>
          </div>

          <!-- 安全政策 -->
          <div v-if="activeTab === 'security'" class="tab-panel">
            <h2><span class="icon">🛡️</span> 安全政策</h2>
            <div class="policy-section">
              <h3>1. 数据安全</h3>
              <p>我们采用行业标准的安全措施保护您的数据：</p>
              <div class="security-features">
                <div class="security-feature">
                  <span class="feature-icon">🔐</span>
                  <span>SSL/TLS加密传输</span>
                </div>
                <div class="security-feature">
                  <span class="feature-icon">🔍</span>
                  <span>定期安全漏洞扫描</span>
                </div>
                <div class="security-feature">
                  <span class="feature-icon">🛡️</span>
                  <span>Web应用防火墙</span>
                </div>
                <div class="security-feature">
                  <span class="feature-icon">📊</span>
                  <span>实时安全监控</span>
                </div>
              </div>
            </div>
            <div class="policy-section">
              <h3>2. 账户安全</h3>
              <p>为保护您的账户安全，请：</p>
              <ul>
                <li>使用强密码（包含大小写字母、数字和特殊字符）</li>
                <li>不要与他人共享您的账户信息</li>
                <li>定期更换密码</li>
                <li>启用双重验证（如果可用）</li>
              </ul>
            </div>
            <div class="policy-section">
              <h3>3. 安全事件响应</h3>
              <p>如果发生安全事件，我们将：</p>
              <ul>
                <li>立即采取措施控制事件影响</li>
                <li>在72小时内通知受影响的用户</li>
                <li>进行事件调查和原因分析</li>
                <li>采取措施防止类似事件再次发生</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div class="policy-actions">
        <p class="policy-note">
          最后更新日期：2025年1月<br>
          如果您对我们的政策有任何疑问，请通过联系我们页面与我们取得联系。
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WebsitePolicy',
  data() {
    return {
      activeTab: 'privacy'
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>

<style scoped>
/* 通用样式 */
.footer-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  font-family: 'Microsoft YaHei', 'Segoe UI', sans-serif;
}

.page-header {
  background: linear-gradient(to right, #4b6cb7, #182848);
  color: white;
  padding: 25px 30px;
  border-radius: 15px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
}

.page-header h1 {
  margin: 10px 0;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-subtitle {
  margin: 5px 0 0;
  opacity: 0.9;
  font-size: 16px;
}

.back-button {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-5px);
}

.back-icon {
  font-size: 18px;
  font-weight: bold;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: center;
  margin: 40px 0 25px;
  gap: 15px;
}

.section-header h2 {
  font-size: 24px;
  color: #182848;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon {
  font-size: 28px;
}

/* WarrantyPolicy 页面样式 */
.warranty-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  margin-top: 20px;
}

.warranty-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  display: flex;
  gap: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-left: 5px solid #4b6cb7;
}

.warranty-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.warranty-card.gold {
  border-left-color: #FFD700;
  background: linear-gradient(135deg, #FFF9C4, #FFEB3B);
}

.warranty-card.silver {
  border-left-color: #C0C0C0;
  background: linear-gradient(135deg, #F5F5F5, #E0E0E0);
}

.warranty-card.bronze {
  border-left-color: #CD7F32;
  background: linear-gradient(135deg, #FFE0B2, #FFB74D);
}

.warranty-icon {
  font-size: 40px;
  min-width: 50px;
}

.warranty-content h3 {
  margin: 0 0 10px;
  color: #182848;
  font-size: 20px;
}

.warranty-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.warranty-duration {
  background: rgba(75, 108, 183, 0.1);
  color: #4b6cb7;
  padding: 8px 15px;
  border-radius: 20px;
  display: inline-block;
  font-weight: 600;
}

.policy-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.policy-section h3 {
  color: #182848;
  margin: 0 0 20px;
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.policy-section ul, .policy-section ol {
  margin: 0;
  padding-left: 20px;
}

.policy-section li {
  margin-bottom: 10px;
  line-height: 1.6;
  color: #555;
}

.support-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.feature {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
}

.feature-icon {
  font-size: 24px;
}

/* WebsitePolicy 页面样式 */
.tabs-header {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.tab-button {
  flex: 1;
  min-width: 150px;
  background: white;
  border: 2px solid #e0e0e0;
  padding: 15px 25px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  transition: all 0.3s ease;
}

.tab-button:hover {
  border-color: #4b6cb7;
  color: #4b6cb7;
}

.tab-button.active {
  background: linear-gradient(to right, #4b6cb7, #182848);
  border-color: #4b6cb7;
  color: white;
}

.tab-icon {
  font-size: 20px;
}

.tab-panel {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.tab-panel h2 {
  color: #182848;
  margin: 0 0 25px;
  font-size: 26px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.cookie-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.cookie-type {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 12px;
  border-left: 4px solid #4b6cb7;
}

.cookie-type h4 {
  margin: 0 0 10px;
  color: #182848;
}

.security-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.security-feature {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #e3f2fd;
  border-radius: 10px;
}

.policy-actions {
  margin-top: 40px;
  text-align: center;
}

.policy-note {
  background: #f0f7ff;
  padding: 20px;
  border-radius: 12px;
  color: #666;
  line-height: 1.6;
}

/* JoinUs 页面样式 */
.join-intro {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 15px;
  margin-bottom: 40px;
}

.intro-content h2 {
  color: white;
  margin: 0 0 15px;
}

.intro-text {
  font-size: 18px;
  line-height: 1.6;
  opacity: 0.9;
  margin-bottom: 25px;
}

.company-values {
  display: flex;
  gap: 25px;
  flex-wrap: wrap;
}

.value-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
}

.value-icon {
  font-size: 20px;
}

.value-text {
  font-weight: 600;
}

.positions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 25px;
}

.position-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.position-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
}

.position-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.position-header h3 {
  margin: 0;
  color: #182848;
  font-size: 20px;
}

.position-tag {
  background: #e3f2fd;
  color: #4b6cb7;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
}

.position-info p {
  margin: 8px 0;
  color: #666;
}

.info-label {
  font-weight: 600;
  color: #182848;
}

.apply-button {
  width: 100%;
  background: linear-gradient(to right, #4b6cb7, #182848);
  color: white;
  border: none;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  margin-top: 20px;
}

.apply-button:hover {
  transform: scale(1.02);
  box-shadow: 0 5px 15px rgba(75, 108, 183, 0.4);
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.benefit-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 15px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
}

.benefit-icon {
  font-size: 32px;
  min-width: 50px;
}

.benefit-content h4 {
  margin: 0 0 8px;
  color: #182848;
}

.benefit-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* ShoppingGuide 页面样式 */
.steps-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.step {
  background: white;
  border-radius: 15px;
  padding: 25px;
  position: relative;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.step-number {
  position: absolute;
  top: -15px;
  left: -15px;
  background: linear-gradient(135deg, #4b6cb7, #182848);
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.step-content h3 {
  margin: 0 0 10px;
  color: #182848;
}

.step-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  margin-top: 40px;
}

.info-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  display: flex;
  gap: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  border-top: 4px solid #4b6cb7;
}

.info-icon {
  font-size: 40px;
  min-width: 50px;
}

.info-content h3 {
  margin: 0 0 15px;
  color: #182848;
}

.info-content ul {
  margin: 0;
  padding-left: 20px;
}

.info-content li {
  margin-bottom: 8px;
  color: #666;
}

/* SiteMap 页面样式 */
.sitemap-container {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.main-header {
  margin-top: 0;
}

.nav-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.nav-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: #e3f2fd;
  transform: translateX(5px);
}

.nav-icon {
  font-size: 32px;
  min-width: 50px;
}

.nav-info h3 {
  margin: 0 0 8px;
  color: #182848;
}

.nav-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.category-item {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 15px;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: #e3f2fd;
  transform: translateY(-3px);
}

.category-icon {
  font-size: 28px;
}

.category-info h4 {
  margin: 0 0 5px;
  color: #182848;
}

.category-info p {
  margin: 0;
  color: #666;
  font-size: 13px;
}

.service-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
}

.service-column h4 {
  margin: 0 0 15px;
  color: #182848;
}

.service-column ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.service-column li {
  margin-bottom: 8px;
}

.service-column a {
  color: #666;
  text-decoration: none;
  transition: color 0.3s ease;
}

.service-column a:hover {
  color: #4b6cb7;
}

.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.quick-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f8f9fa;
  border-radius: 25px;
  text-decoration: none;
  color: #182848;
  transition: all 0.3s ease;
}

.quick-link:hover {
  background: #4b6cb7;
  color: white;
}

.link-icon {
  font-size: 18px;
}

.sitemap-info {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 30px;
  margin-top: 40px;
  padding-top: 40px;
  border-top: 2px dashed #e0e0e0;
}

.info-item {
  display: flex;
  gap: 15px;
  text-align: center;
}

.info-icon {
  font-size: 32px;
}

.info-content h5 {
  margin: 0 0 5px;
  color: #182848;
}

.info-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* ContactUs 页面样式 */
.contact-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.contact-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  display: flex;
  gap: 20px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-5px);
}

.contact-card.phone {
  border-left: 5px solid #4CAF50;
}

.contact-card.email {
  border-left: 5px solid #2196F3;
}

.contact-icon {
  font-size: 40px;
  min-width: 50px;
}

.contact-info h3 {
  margin: 0 0 10px;
  color: #182848;
}

.phone-number, .email-address {
  font-size: 22px;
  font-weight: bold;
  color: #4b6cb7;
  margin: 10px 0;
}

.service-time, .response-time {
  color: #666;
  margin: 5px 0;
}

.address-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  display: flex;
  gap: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  border-left: 5px solid #FF9800;
}

.address-icon {
  font-size: 45px;
  min-width: 50px;
}

.address-info h3 {
  margin: 0 0 15px;
  color: #182848;
}

.address-info p {
  margin: 8px 0;
  color: #666;
}

.address-note {
  color: #4b6cb7;
  font-weight: 600;
  margin-top: 15px !important;
}

.social-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.social-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.social-card:hover {
  transform: translateY(-3px);
}

.social-card.wechat {
  border-left: 4px solid #07C160;
}

.social-card.weibo {
  border-left: 4px solid #E6162D;
}

.social-card.douyin {
  border-left: 4px solid #000000;
}

.social-icon {
  font-size: 28px;
}

.social-info h4 {
  margin: 0 0 5px;
  color: #182848;
}

.social-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.contact-form {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #182848;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #4b6cb7;
  outline: none;
  box-shadow: 0 0 0 3px rgba(75, 108, 183, 0.1);
}

.submit-button {
  background: linear-gradient(to right, #4b6cb7, #182848);
  color: white;
  border: none;
  padding: 15px 40px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(75, 108, 183, 0.3);
}

/* AboutCompany 页面样式 */
.company-overview {
  text-align: center;
  background: white;
  border-radius: 15px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.company-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
}

.logo-icon {
  font-size: 60px;
  background: linear-gradient(135deg, #4CAF50, #8BC34A);
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.company-logo h2 {
  margin: 0;
  color: #182848;
  font-size: 28px;
}

.company-intro {
  font-size: 18px;
  line-height: 1.8;
  color: #666;
  max-width: 800px;
  margin: 0 auto;
}

.mission-vision {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.mission-card, .vision-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
}

.mission-card {
  border-top: 5px solid #4CAF50;
}

.vision-card {
  border-top: 5px solid #2196F3;
}

.card-icon {
  font-size: 40px;
  margin-bottom: 20px;
}

.card-content h3 {
  margin: 0 0 15px;
  color: #182848;
}

.card-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.timeline {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 30px;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(to bottom, #4b6cb7, transparent);
}

.timeline-item {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
  position: relative;
}

.timeline-year {
  background: linear-gradient(135deg, #4b6cb7, #182848);
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  font-weight: bold;
  min-width: 80px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.timeline-content {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.05);
}

.timeline-content h4 {
  margin: 0 0 10px;
  color: #182848;
}

.timeline-content p {
  margin: 0;
  color: #666;
}

.eco-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.eco-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.eco-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.eco-icon {
  font-size: 40px;
  margin-bottom: 15px;
}

.eco-content h4 {
  margin: 0 0 10px;
  color: #182848;
}

.eco-content p {
  margin: 0;
  color: #666;
}

/* UserAgreement 页面样式 */
.agreement-header {
  background: white;
  border-radius: 15px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.agreement-intro p {
  margin: 0 0 15px;
  line-height: 1.6;
  color: #666;
}

.important-note {
  background: #fff8e1;
  padding: 15px;
  border-radius: 10px;
  border-left: 4px solid #FFC107;
}

.agreement-content {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.agreement-section {
  margin-bottom: 40px;
  padding-bottom: 40px;
  border-bottom: 1px solid #eee;
}

.agreement-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.agreement-section h2 {
  color: #182848;
  margin: 0 0 20px;
  font-size: 22px;
}

.agreement-section p {
  margin: 0 0 15px;
  line-height: 1.6;
  color: #666;
}

.terms-list, .order-terms {
  display: grid;
  gap: 20px;
}

.term-item, .order-term {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  border-left: 4px solid #4b6cb7;
}

.term-item h3, .order-term h3 {
  margin: 0 0 10px;
  color: #182848;
  font-size: 16px;
}

.rules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
}

.rule-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
}

.rule-icon {
  font-size: 30px;
  margin-bottom: 15px;
}

.rule-content h4 {
  margin: 0 0 15px;
  color: #182848;
}

.rule-content ul {
  margin: 0;
  padding-left: 20px;
}

.rule-content li {
  margin-bottom: 8px;
  color: #666;
}

.service-terms {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.service-term {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  gap: 15px;
  align-items: center;
}

.service-icon {
  font-size: 28px;
}

.service-content h4 {
  margin: 0 0 5px;
  color: #182848;
}

.service-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.disclaimer-list {
  margin: 15px 0 0;
  padding-left: 20px;
}

.disclaimer-list li {
  margin-bottom: 10px;
  color: #666;
}

.update-notice {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: #e3f2fd;
  padding: 10px 20px;
  border-radius: 20px;
  margin-top: 15px;
  color: #4b6cb7;
  font-weight: 600;
}

.update-icon {
  font-size: 18px;
}

.agreement-footer {
  margin-top: 40px;
  text-align: center;
}

.accept-section {
  background: #f0f7ff;
  padding: 25px;
  border-radius: 12px;
}

.accept-text {
  font-size: 18px;
  font-weight: 600;
  color: #4b6cb7;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .section-header h2 {
    font-size: 20px;
  }

  .warranty-cards,
  .positions-grid,
  .steps-container,
  .info-section {
    grid-template-columns: 1fr;
  }

  .tabs-header {
    flex-direction: column;
  }

  .tab-button {
    min-width: 100%;
  }

  .mission-vision,
  .rules-grid {
    grid-template-columns: 1fr;
  }

  .company-values {
    flex-direction: column;
    align-items: flex-start;
  }

  .contact-methods {
    grid-template-columns: 1fr;
  }

  .service-links {
    grid-template-columns: 1fr;
  }

  .timeline::before {
    left: 20px;
  }

  .timeline-year {
    min-width: 60px;
    padding: 8px 15px;
  }
}
</style>