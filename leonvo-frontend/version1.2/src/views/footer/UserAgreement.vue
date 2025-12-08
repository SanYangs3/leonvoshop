<template>
  <div class="footer-page">
    <div class="page-header">
      <button @click="goBack" class="back-button">
        <span class="back-icon">←</span>
        返回
      </button>
      <h1>📝 用户协议</h1>
      <p class="page-subtitle">泡泡联盟用户服务协议与条款</p>
    </div>

    <div class="page-content">
      <div class="agreement-header">
        <div class="agreement-intro">
          <p>欢迎使用泡泡联盟联想电脑商城！请仔细阅读以下用户协议。</p>
          <p class="important-note">
            📌 <strong>重要提示：</strong>在您注册或使用我们的服务之前，请务必仔细阅读并理解本协议的全部内容。
          </p>
        </div>
      </div>

      <div class="agreement-content">
        <div class="agreement-section">
          <h2>一、协议接受</h2>
          <p>当您完成注册或使用我们的服务时，即表示您已阅读、理解并同意接受本协议的所有条款。</p>
        </div>

        <div class="agreement-section">
          <h2>二、账户注册</h2>
          <div class="terms-list">
            <div class="term-item">
              <h3>2.1 注册资格</h3>
              <p>您必须是具备完全民事行为能力的个人或合法实体。</p>
            </div>
            <div class="term-item">
              <h3>2.2 账户信息</h3>
              <p>您需要提供真实、准确、完整的注册信息，并及时更新。</p>
            </div>
            <div class="term-item">
              <h3>2.3 账户安全</h3>
              <p>您有责任妥善保管账户密码，并对账户下的所有行为负责。</p>
            </div>
          </div>
        </div>

        <div class="agreement-section">
          <h2>三、使用规则</h2>
          <div class="rules-grid">
            <div class="rule-card">
              <div class="rule-icon">✅</div>
              <div class="rule-content">
                <h4>可以做的</h4>
                <ul>
                  <li>浏览和购买商品</li>
                  <li>发表真实的商品评价</li>
                  <li>参与促销活动</li>
                  <li>使用客服支持</li>
                </ul>
              </div>
            </div>
            <div class="rule-card">
              <div class="rule-icon">❌</div>
              <div class="rule-content">
                <h4>禁止做的</h4>
                <ul>
                  <li>发布虚假信息</li>
                  <li>恶意刷单或评价</li>
                  <li>侵犯他人知识产权</li>
                  <li>进行非法交易</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <div class="agreement-section">
          <h2>四、订单与支付</h2>
          <div class="order-terms">
            <div class="order-term">
              <h3>4.1 订单生成</h3>
              <p>当您提交订单并完成支付后，订单正式生成。</p>
            </div>
            <div class="order-term">
              <h3>4.2 价格说明</h3>
              <p>商品价格可能因促销活动而调整，以实际支付价格为准。</p>
            </div>
            <div class="order-term">
              <h3>4.3 支付方式</h3>
              <p>我们支持多种支付方式，支付由第三方支付平台处理。</p>
            </div>
          </div>
        </div>

        <div class="agreement-section">
          <h2>五、售后服务</h2>
          <div class="service-terms">
            <div class="service-term">
              <div class="service-icon">🔄</div>
              <div class="service-content">
                <h4>退货政策</h4>
                <p>符合条件商品7天内可无理由退货</p>
              </div>
            </div>
            <div class="service-term">
              <div class="service-icon">🔧</div>
              <div class="service-content">
                <h4>维修服务</h4>
                <p>提供2年整机保修服务</p>
              </div>
            </div>
            <div class="service-term">
              <div class="service-icon">📞</div>
              <div class="service-content">
                <h4>客服支持</h4>
                <p>7×24小时在线客服</p>
              </div>
            </div>
          </div>
        </div>

        <div class="agreement-section">
          <h2>六、免责声明</h2>
          <p>在法律法规允许的范围内，我们对以下情况不承担责任：</p>
          <ul class="disclaimer-list">
            <li>不可抗力导致的损失</li>
            <li>第三方服务中断造成的影响</li>
            <li>用户不当使用造成的损害</li>
            <li>法律规定应由用户承担的风险</li>
          </ul>
        </div>

        <div class="agreement-section">
          <h2>七、协议修改</h2>
          <p>我们有权根据需要修改本协议。重大修改将通过网站公告通知用户。</p>
          <div class="update-notice">
            <span class="update-icon">📅</span>
            本协议最后更新日期：2025年1月
          </div>
        </div>
      </div>

      <div class="agreement-footer">
        <div class="accept-section">
          <p class="accept-text">继续使用我们的服务，即表示您接受本用户协议的所有条款。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserAgreement',
  methods: {
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>