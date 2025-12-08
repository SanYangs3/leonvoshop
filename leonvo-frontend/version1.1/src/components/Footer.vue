<template>
  <footer class="footer">
    <div class="footer-content">
      <!-- 第一列：购物指南 -->
      <div class="footer-column">
        <h3 class="footer-title">购物指南</h3>
        <ul class="footer-links">
          <li>
            <span class="link-icon">📋</span>
            购物流程
          </li>
          <li>
            <span class="link-icon">💳</span>
            支付方式
          </li>
          <li>
            <span class="link-icon">🚚</span>
            配送说明
          </li>
          <li>
            <span class="link-icon">🛠️</span>
            售后服务
          </li>
        </ul>
      </div>

      <!-- 第二列：保修政策 -->
      <div class="footer-column">
        <h3 class="footer-title">保修政策</h3>
        <ul class="footer-links">
          <li>
            <span class="link-icon">📃</span>
            保修政策（全损版）
          </li>
          <li>
            <span class="link-icon">🔧</span>
            维修服务（也许存在？）
          </li>
          <li>
            <span class="link-icon">💡</span>
            技术支持（我们精神上给予无限的支持！）
          </li>
        </ul>
      </div>

      <!-- 第三列：关于我们 -->
      <div class="footer-column">
        <h3 class="footer-title">关于我们</h3>
        <ul class="footer-links">
          <li>
            <span class="link-icon">🏢</span>
            公司简介（其实并没有这个公司）
          </li>
          <li>
            <span class="link-icon">📞</span>
            联系我们（你猜呢）
          </li>
          <li>
            <span class="link-icon">👥</span>
            加入我们（这班谁爱上谁上，说不定你就来了呢）
          </li>
          <li>
            <div class="hotline">
              <div class="hotline-header">
                <span class="hotline-icon">📞</span>
                <span>客服热线（假的，别打）</span>
              </div>
              <div class="service-time">
                <span class="time-icon">⏰</span>
                服务时间：007从早到晚全年不朽精诚服务
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
              <a href="#">
                <span class="link-icon">📜</span>
                网站政策
              </a>
              <span class="separator"> | </span>
              <a href="#">
                <span class="link-icon">📝</span>
                用户协议
              </a>
              <span class="separator"> | </span>
              <a href="#">
                <span class="link-icon">🗺️</span>
                网站地图
              </a>
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
.footer {
  background: linear-gradient(135deg, #569370 0%, #4caf8c 100%);
  color: #e8f5e9;
  padding: 60px 20px 30px;
  margin-top: 80px;
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #4caf50, #81c784, #4caf50);
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

.footer-column {
  flex: 1;
  min-width: 250px;
  padding: 0 25px;
  margin-bottom: 40px;
}

.footer-title {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 25px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(129, 199, 132, 0.5);
  position: relative;
}

.footer-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, #81c784, #4caf8c);
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 18px;
  font-size: 15px;
  line-height: 1.6;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  padding: 8px 12px;
  border-radius: 8px;
}

.footer-links li:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.link-icon {
  font-size: 18px;
  margin-right: 12px;
  opacity: 0.8;
  min-width: 24px;
  text-align: center;
}

.hotline {
  background: rgba(255, 255, 255, 0.1);
  padding: 15px;
  border-radius: 12px;
  border-left: 4px solid #81c784;
  margin-top: 10px;
  backdrop-filter: blur(10px);
}

.hotline-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-weight: 600;
}

.hotline-icon {
  font-size: 20px;
}

.service-time {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  opacity: 0.9;
  color: #c8e6c9;
  font-style: italic;
}

.time-icon {
  font-size: 16px;
}

.website-links {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
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
}

.website-links a:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.separator {
  color: rgba(200, 230, 201, 0.5);
  font-weight: 300;
}

.copyright {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.05);
  padding: 12px 18px;
  border-radius: 10px;
  margin-bottom: 20px;
}

.copyright-icon {
  font-size: 24px;
}

.eco-statement {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #c8e6c9;
  font-size: 14px;
  margin-top: 20px;
  padding: 12px 18px;
  background: rgba(129, 199, 132, 0.1);
  border-radius: 10px;
  border: 1px solid rgba(129, 199, 132, 0.3);
}

.eco-icon {
  font-size: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .footer-column {
    flex: 0 0 50%;
    max-width: 50%;
    margin-bottom: 30px;
  }
}

@media (max-width: 768px) {
  .footer-column {
    flex: 0 0 100%;
    max-width: 100%;
    margin-bottom: 30px;
    padding: 0 15px;
  }

  .footer-content {
    flex-direction: column;
  }

  .footer {
    padding: 40px 15px 20px;
  }

  .website-links {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .separator {
    display: none;
  }
}
</style>