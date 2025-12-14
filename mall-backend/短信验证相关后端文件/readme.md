我传了一些我更新接口用的文件

之后我的user相关的后端也是临时的，大家后端可以自己更改
这边主要说一下我的接口
https://push.spug.cc/sms/yKmaCuBTT86nBx_t0rtWcg
就是这边调用的这个，找了很多网站，大多数官方网站无法提供非企业发送短信，所以找了个小网站花了点钱钱买了50条短信和定制短信内容
我目前放出的这条是最基础，会提供验证码，网站还提供其他定义内容等我之后研究

[Spug推送助手 - 消息通知一个请求搞定](https://push.spug.cc/)

这个是我买接口的网站



关于数据



-- 创建用户表
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(100),
    password VARCHAR(255),
    email VARCHAR(100),
    tel VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE
);

-- 创建短信验证码表
CREATE TABLE sms_codes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    send_time TIMESTAMP NOT NULL,
    expire_time TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    verify_time TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_sms_phone ON sms_codes(phone);
CREATE INDEX idx_sms_expire ON sms_codes(expire_time);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_tel ON users(tel);
CREATE INDEX idx_users_email ON users(email);

这个是我随意生成的后端数据库，可以参考一下~
以上。