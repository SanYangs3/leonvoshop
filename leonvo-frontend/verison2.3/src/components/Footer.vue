<template>
  <footer class="footer">
    <div class="footer-content">
      <!-- 第一列：购物指南 -->
      <div class="footer-column">
        <h3 class="footer-title">购物指南</h3>
        <ul class="footer-links">
          <li>
            <router-link to="/footer/shopping-guide">
              <span class="link-icon">📋</span>
              购物流程
            </router-link>
          </li>
          <li>
            <router-link to="/footer/shopping-guide">
              <span class="link-icon">💳</span>
              支付方式
            </router-link>
          </li>
          <li>
            <router-link to="/footer/shopping-guide">
              <span class="link-icon">🚚</span>
              配送说明
            </router-link>
          </li>
          <li>
            <router-link to="/footer/shopping-guide">
              <span class="link-icon">🛠️</span>
              售后服务
            </router-link>
          </li>
        </ul>
      </div>

      <!-- 第二列：保修政策 -->
      <div class="footer-column">
        <h3 class="footer-title">保修政策</h3>
        <ul class="footer-links">
          <li>
            <router-link to="/footer/warranty-policy">
              <span class="link-icon">📃</span>
              保修政策（全损版）
            </router-link>
          </li>
          <li>
            <router-link to="/footer/warranty-policy">
              <span class="link-icon">🔧</span>
              维修服务（也许存在？）
            </router-link>
          </li>
          <li>
            <router-link to="/footer/warranty-policy">
              <span class="link-icon">💡</span>
              技术支持（我们精神上给予无限的支持！）
            </router-link>
          </li>
        </ul>
      </div>

      <!-- 第三列：关于我们 -->
      <div class="footer-column">
        <h3 class="footer-title">关于我们</h3>
        <ul class="footer-links">
          <li>
            <router-link to="/footer/about-company">
              <span class="link-icon">🏢</span>
              公司简介（其实并没有这个公司）
            </router-link>
          </li>
          <li>
            <router-link to="/footer/contact-us">
              <span class="link-icon">📞</span>
              联系我们（你猜呢）
            </router-link>
          </li>
          <li>
            <router-link to="/footer/join-us">
              <span class="link-icon">👥</span>
              加入我们（这班谁爱上谁上，说不定你就来了呢）
            </router-link>
          </li>
          <li>
            <div class="hotline">
              <div class="hotline-header">
                <span class="hotline-icon">📞</span>
                <span>客服热线：400-123-4567</span>
              </div>
              <div class="service-time">
                <span class="time-icon">⏰</span>
                服务时间：周一至周日 9:00-21:00
              </div>
            </div>
          </li>
        </ul>
      </div>

      <!-- 第四列：网站信息 -->
      <div class="footer-column">
        <h3 class="footer-title">网站信息</h3>
        <ul class="footer-links">
          <li class="copyright">
            <span class="copyright-icon">🌿</span>
            © 2025软件工程泡泡联盟
          </li>
          <li>
            <div class="website-links">
              <router-link to="/footer/website-policy">
                <span class="link-icon">📜</span>
                网站政策
              </router-link>
              <span class="separator"> | </span>
              <router-link to="/footer/user-agreement">
                <span class="link-icon">📝</span>
                用户协议
              </router-link>
              <span class="separator"> | </span>
              <router-link to="/sitemap">
                <span class="link-icon">🗺️</span>
                网站地图
              </router-link>
            </div>
          </li>
          <li class="eco-statement">
            <span class="eco-icon">🌱</span>
            我们致力于绿色环保科技
          </li>
        </ul>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>

<style scoped>
/* 基本页脚样式 */
.footer {
  background: linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%);
  color: #e8f5e9;
  padding: 40px 20px 20px;
  margin-top: 60px;
  border-top: 4px solid #4caf50;
  font-family: 'Segoe UI', Arial, sans-serif;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.footer-column {
  display: flex;
  flex-direction: column;
}

.footer-title {
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(76, 175, 80, 0.3);
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-title::before {
  content: '▶';
  font-size: 12px;
  color: #4caf50;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
  transition: transform 0.3s ease;
}

.footer-links li:hover {
  transform: translateX(3px);
}

.link-icon {
  display: inline-block;
  width: 20px;
  text-align: center;
  margin-right: 10px;
  font-size: 16px;
}

/* 链接样式 - 保持你已有的 */
.footer-links a {
  color: #c8e6c9;
  text-decoration: none;
  display: flex;
  align-items: center;
  width: 100%;
  transition: all 0.3s ease;
  font-size: 14px;
  padding: 4px 0;
}

.footer-links a:hover {
  color: #ffffff;
  transform: translateX(5px);
}

/* 客服热线特殊样式 */
.hotline {
  margin-top: 10px;
  padding: 15px;
  background: rgba(76, 175, 80, 0.1);
  border-radius: 8px;
  border-left: 3px solid #4caf50;
}

.hotline-header {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.hotline-icon {
  font-size: 18px;
  color: #4caf50;
}

.service-time {
  font-size: 13px;
  color: #a5d6a7;
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-icon {
  font-size: 14px;
}

/* 版权信息 */
.copyright {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  padding: 10px 0;
}

.copyright-icon {
  font-size: 18px;
  color: #4caf50;
}

/* 网站链接 */
.website-links {
  display: flex;
  align-items: center;
  gap: 5px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.website-links a {
  color: #c8e6c9;
  text-decoration: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
}

.website-links a:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.separator {
  color: #81c784;
  font-size: 14px;
}

/* 环保声明 */
.eco-statement {
  font-size: 14px;
  color: #a5d6a7;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 10px;
  background: rgba(129, 199, 132, 0.1);
  border-radius: 6px;
  font-style: italic;
}

.eco-icon {
  font-size: 16px;
  color: #4caf50;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 25px;
  }

  .footer {
    padding: 30px 15px 15px;
  }

  .footer-title {
    font-size: 16px;
  }

  .footer-links a {
    font-size: 13px;
  }
}
</style>