我传了一些我更新接口用的文件

之后我的user相关的后端也是临时的，大家后端可以自己更改
这边主要说一下我的接口
https://push.spug.cc/sms/yKmaCuBTT86nBx_t0rtWcg
就是这边调用的这个，找了很多网站，大多数官方网站无法提供非企业发送短信，所以找了个小网站花了点钱钱买了50条短信和定制短信内容
我目前放出的这条是最基础，会提供验证码，网站还提供其他定义内容等我之后研究

[Spug推送助手 - 消息通知一个请求搞定](https://push.spug.cc/)

[Spug | 短信助手](https://sms.spug.cc/)
上面是具体的消息专区！

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



### 2025.12.17更新

更新了邮箱方面配置，经过我的寻找，发现qq官方邮箱就自己推出了SMTP服务（具体代码后端麻烦后端自己添加！（感谢，我就不生成了））
这边我给出我的qq邮箱：3379403637@qq.com，其中我的授权码是rrgffhupdccocjfe
（这两个数据用来实现我的邮箱进行邮箱内容发送！）具体我和deepseek聊的连接放在这边，可以自行参考看如何实现，具体板块后端人员自己添加即可

https://chat.deepseek.com/share/wp5nlcjjz4qnf2tfq0

### 关于短信验证！这边更通俗的讲就是

注册账号密匙：yKmaCuBTT86nBx_t0rtWcg
调用内容：您正在申请注册小绿泡泡网站账号，您的验证码是${code}，祝您生活愉快天天开心=v=，如果非本人操作可能是阳阳阳攻击了网站！
其中${code}是可以自己生成的随即号（4-6位）

密码重置密匙：9dJJTVtRRByifLd-lfoN2A

调用内容：您正在重置注册小绿泡泡网站账号密码，您的验证码是${code}，祝您生活愉快天天开心=v=，如果非本人操作可能是阳阳阳攻击了网站！

同样的其中${code}是可以自己生成的随即号（4-6位）
[Spug | 短信助手](https://sms.spug.cc/guide/发送短信验证码)
下面是具体的使用文档！大家可以具体参考这个来使用，如果打不开连接就直接看我放在本文件夹的照片

调用模板实例（其中我们调用的时候其实给到连接的一个是我上面的密匙，第二就是我们随机生成的code，第三就是我们从前端抓取的电话号码！）

```
import java.io.*;
import java.net.*;

public class Demo {
    public static void main(String[] args) throws Exception {
        String url = "https://push.spug.cc/sms/A27Lsf3fs2bgEY";
        String json = "{\"name\":\"推送助手\",\"code\":\"153146\",\"to\":\"186xxxx9898\"}";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
```

