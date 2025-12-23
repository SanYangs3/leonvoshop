# 1.User接口

## 1.1 获取用户名单和信息

请求路径：/user/user

请求方式：GET

返回格式：

```java
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "uid": 1,
      "username": "admin",
      "password": "$2a$10$ABC123XYZ456...",
      "email": "admin@example.com",
      "phone": "13800138000",
      "status": 1,
      "avatar": "https://example.com/avatar/admin.jpg",
      "role": "admin",
      "createTime": "2023-10-01T10:30:00",
      "updateTime": "2023-10-10T15:20:00"
    },
    {
      "uid": 2,
      "username": "zhangsan",
      "password": "$2a$10$DEF456GHI789...",
      "email": "zhangsan@example.com",
      "phone": "13900139000",
      "status": 1,
      "avatar": "https://example.com/avatar/zhangsan.jpg",
      "role": "user",
      "createTime": "2023-10-02T09:15:00",
      "updateTime": "2023-10-05T14:30:00"
    },
    {
      "uid": 3,
      "username": "lisi",
      "password": "$2a$10$JKL012MNO345...",
      "email": "lisi@example.com",
      "phone": "13600136000",
      "status": 0,
      "avatar": null,
      "role": "user",
      "createTime": "2023-10-03T11:45:00",
      "updateTime": "2023-10-15T16:10:00"
    },
    {
      "uid": 4,
      "username": "wangwu",
      "password": "$2a$10$PQR678STU901...",
      "email": null,
      "phone": "13500135000",
      "status": 1,
      "avatar": "",
      "role": "user",
      "createTime": "2023-10-04T08:20:00",
      "updateTime": "2023-10-04T08:20:00"
    },
    {
      "uid": 5,
      "username": "zhaoliu",
      "password": "$2a$10$VWX234YZA567...",
      "email": "zhaoliu@example.com",
      "phone": null,
      "status": 1,
      "avatar": "https://example.com/avatar/zhaoliu.png",
      "role": "user",
      "createTime": "2023-10-05T13:50:00",
      "updateTime": "2023-10-12T09:05:00"
    }
  ]
}
```

## 1.2 用户姓名和密码录入

请求路径：/admin/user/insert

请求方式：POST

返回格式：

```
{
	"code": 1,
	"data": null,
	"msg": null
}
```

请求格式：

```
  {
  	  "uid": 6,
      "username": "lisi",
      "password": "123456",
      "email": null,
      "phone": null,
      "status":null,
      "avatar": null,
      "role": null,
      "createTime": null,
      "updateTime":null,
      }
  {
  	  "uid": 9,
      "username": "wangwu",
      "password": "123456",
      "email": null,
      "phone": null,
      "status":null,
      "avatar": null,
      "role": null,
      "createTime": null,
      "updateTime":null,
      }
   {
   	  "uid": 10,
      "username": "liusi",
      "password": "123456",
      "email": null,
      "phone": null,
      "status":null,
      "avatar": null,
      "role": null,
      "createTime": null,
      "updateTime":null,
      }
    {
  	  "uid": 15,
      "username": "lisi",
      "password": "123456",
      "email": null,
      "phone": null,
      "status":null,
      "avatar": null,
      "role": null,
      "createTime": null,
      "updateTime":null,
      }
```

使用的测试数据如下：

```
--插入用户注册数据
INSERT INTO users (username, password, email) VALUES
('testuser1', '$2a$10$GHIJKLMNOPQRSTUVWXYZ123456'),
('normal_user', '$2a$10$LMNOPQRSTUVWXYZ123456ABCDEF');
('user_123-test', '$2a$10$XYZ123456ABCDEFGHIJKLMNOP'),
('john.doe', '$2a$10$456ABCDEFGHIJKLMNOPQRSTUVW');
('张三', '$2a$10$BCDEFGHIJKLMNOPQRSTUVWXYZ12'),
('李四', '$2a$10$CDEFGHIJKLMNOPQRSTUVWXYZ123');
('user007', '$2a$10$DEFGHIJKLMNOPQRSTUVWXYZ1234'),
('agent99', '$2a$10$EFGHIJKLMNOPQRSTUVWXYZ12345');
('UserAdmin', '$2a$10$FGHIJKLMNOPQRSTUVWXYZ123456'),
('TestUser', '$2a$10$GHIJKLMNOPQRSTUVWXYZ1234567');
```

## 1.3 通过用户id获取单个用户所有信息

请求路径：/admin/user/{id}

请求方式：GET

返回格式

```
{
  "code": 1,
  "msg": null,
  "data": 
    {
      "uid": 1,
      "username": "admin",
      "password": "$2a$10$ABC123XYZ456...",
      "email": "admin@example.com",
      "phone": "13800138000",
      "status": 1,
      "avatar": "https://example.com/avatar/admin.jpg",
      "role": "admin",
      "createTime": "2023-10-01T10:30:00",
      "updateTime": "2023-10-10T15:20:00"
    }
}
```

测试用例

```
INSERT INTO users (id, username, password, email, phone, status, role, create_time) VALUES
(1, 'admin', '$2a$10$ABC123XYZ456', 'admin@example.com', '13800138000', 1, 'admin', '2023-10-01 10:00:00'),
(2, 'zhangsan', '$2a$10$DEF456GHI789', 'zhangsan@example.com', '13900139000', 1, 'user', '2023-10-02 09:15:00'),
(3, 'lisi', '$2a$10$JKL012MNO345', 'lisi@example.com', '13600136000', 0, 'user', '2023-10-03 11:45:00');
```

## 1.4 通过用户id修改用户的信息(uid无法修改)

请求路径：/admin/user/update

请求方式：POST

返回格式

```
{
	"code": 1,
	"data": null,
	"msg": null
}
```

请求格式

```
{
  "id": 1,
  "username": "admin",
  "nickname": "管理员",
  "email": "admin@example.com",
  "phone": "13800138000",
  "avatar": "https://example.com/avatar.jpg",
  "status": 1,
  "gender": 1,
  "birthday": "1990-01-01",
  "role": "admin",
  "remark": "系统管理员"
}
```

测试用例

```
-- 测试用例1: 只修改昵称
UPDATE user SET nickname = '新昵称' WHERE uid = 1;

-- 测试用例2: 只修改邮箱
UPDATE user SET email = 'newemail@example.com' WHERE uid = 2;

-- 测试用例3: 只修改手机号
UPDATE user SET phone = '13800138001' WHERE uid = 3;

-- 测试用例4: 只修改头像
UPDATE user SET avatar = 'https://example.com/new-avatar.jpg' WHERE uid = 4;
-- 测试用例5: 修改昵称和邮箱
UPDATE user SET nickname = '张三', email = 'zhangsan@example.com' WHERE uid = 1;

-- 测试用例6: 修改基本信息
UPDATE user SET 
  nickname = '李四',
  phone = '13900139000',
  gender = 1,
  birthday = '1990-01-01'
WHERE uid = 2;

-- 测试用例7: 修改状态和角色
UPDATE user SET status = 0, role = 'vip' WHERE uid = 3;

-- 测试用例8: 修改所有可修改字段
UPDATE user SET
  username = 'newusername',
  nickname = '新昵称',
  email = 'new@example.com',
  phone = '13800138001',
  avatar = 'https://example.com/avatar.jpg',
  status = 1,
  gender = 2,
  birthday = '1992-05-15',
  role = 'user',
  remark = '用户信息已更新'
WHERE uid = 4;
```

## 1.5 通过用户id判断是user还是admin

可以通过1.3中的接口前端获取实现

## 1.6 通过用户id获取用户身份是有学生

与1.7合并实现

## 1.7 通过用户id获取用户身份（身份表）

请求路径：/admin/user/role/{id}

请求方式：GET

返回格式

```
{
  "code": 1,
  "msg":null,
  "data": {
    "iid": 1,
    "level": 2,
    "student": 0,
    "points": 3500,
  },
}
```

测试用例

```
-- 插入基础身份数据
INSERT INTO identity (level, student, points, uid) VALUES
(0, 0, 1250, 1),     -- 普通用户，0级，1250积分
(2, 1, 3500, 2),     -- 学生用户，2级，3500积分
(4, 0, 8500, 3),     -- 普通用户，4级，8500积分
(1, 1, 4800, 4),     -- 学生用户，1级，4800积分
(5, 0, 12500, 5);    -- 普通用户，5级，12500积分
(0, 1, 800, 6),      -- 学生，0级，800积分
(1, 0, 2800, 7),     -- 普通，1级，2800积分
(3, 1, 6200, 8),     -- 学生，3级，6200积分
(2, 0, 4500, 9),     -- 普通，2级，4500积分
(4, 1, 9800, 10),    -- 学生，4级，9800积分
(5, 0, 15200, 11),   -- 普通，5级，15200积分
(0, 0, 200, 12),     -- 普通，0级，200积分
(1, 1, 3100, 13),    -- 学生，1级，3100积分
(3, 0, 7200, 14),    -- 普通，3级，7200积分
(2, 1, 5100, 15);    -- 学生，2级，5100积分
(0, 0, 0, 31),           -- 0积分
(0, 0, 4999, 32),        -- 4999积分，差1分到5000
(1, 0, 5000, 33),        -- 正好5000积分，应升级到1级
(2, 0, 9999, 34),        -- 9999积分，差1分到10000
(3, 0, 10000, 35),       -- 10000积分
(4, 0, 14999, 36),       -- 14999积分
(5, 0, 15000, 37),       -- 15000积分，最高积分
(5, 0, 25000, 38),       -- 超高积分用户
(0, 1, 4999, 39),        -- 学生差1分升级
(5, 1, 20000, 40),       -- 学生最高等级
(3, 0, 7200, 41),        -- 普通用户3级
(2, 1, 4800, 42);        -- 学生2级
```



## 1.8 通过用户id获取用户下单订单

请求路径：/admin/order/{uid}

请求方式:GET

返回格式

```
{
	"code": 1,
	"data": [
		{
			"amount": 7999,
			"items": null,
			"oid": 2,
			"orderTime": "2024-03-10T02:15:00.000Z",
			"province": "北京市",
			"receiverAddress": "北京市朝阳区望京SOHO",
			"receiverName": "张三",
			"receiverPhone": "13800138002",
			"remark": "需要发票",
			"status": 2,
			"statusText": "已收货",
			"uid": 2,
			"user": null
		},
		{
			"amount": 18999,
			"items": null,
			"oid": 1,
			"orderTime": "2024-03-01T06:30:00.000Z",
			"province": "北京市",
			"receiverAddress": "北京市海淀区中关村大街1号",
			"receiverName": "张三",
			"receiverPhone": "13800138002",
			"remark": "请尽快发货",
			"status": 3,
			"statusText": "已取消",
			"uid": 2,
			"user": null
		}
	],
	"msg": null
}
```



## 1.9 通过用户id获取用户购物车

请求路径：/admin/cart/{uid}

请求方式:GET

返回格式

```
{
	"code": 1,
	"data": [
		{
			"cid": 1,
			"uid": 2,
			"pid": 1,
			"quantity": 1,
			"bid": 1
		},
		{
			"cid": 2,
			"uid": 2,
			"pid": 5,
			"quantity": 1,
			"bid": 2
		}
	],
	"msg": null
}
```

## 1.10通过用户id注销用户(将status =0)

请求路径：/admin/user/status/{id}

请求方式:POST

返回格式

```
{
	"code": 1,
	"data": null,
	"msg": null
}
```

测试用例

```
1
2 
3
```

## 1.11 用户登录

请求方式：

POST

请求路径：

/admin/user/userlogin

请求格式：

```
{
      "username": "zhangsan",
      "password": "098f6bcd4621d373cade4e832627b4f6"
}
```

返回格式：

```
{
	"code": 0,
	"data": null,
	"msg": "用户名或密码不能为空"
}
{
	"code": 0,
	"data": null,
	"msg": "用户名或密码错误"
}
{
	"code": 1,
	"data": {
		"uid": 10,
		"role": "user",
		"phone": "10086",
		"avatar": "/avatar/test.jpg",
		"email": "test@test.com",
		"username": "zhangsan"
	},
	"msg": null
}
```

## 1.12 admin登录

请求方式：

POST

请求路径：

/admin/user/adminlogin

请求格式：

```
{
      "username": "zhangsan",
      "password": "098f6bcd4621d373cade4e832627b4f6"
      
}
```

返回格式：

```
{
	"code": 0,
	"data": null,
	"msg": "用户名或密码不能为空"
}
{
	"code": 0,
	"data": null,
	"msg": "用户名或密码错误"
}
{
	"code": 1,
	"data": {
		"uid": 1,
		"role": "admin",
		"phone": "13800138001",
		"avatar": "/avatar/admin.jpg",
		"email": "admin@example.com",
		"username": "admin"
	},
	"msg": null
}
```

## 



# 2.后端相关

完成了“后端相关”的部分功能
后端相关

## 2.1获取所有用户人数

请求路径：

/admin/user/all

请求方式：

GET

返回格式：

```
{
	"code": 1,
	"data": 9,
	"msg": null
}
```

## 2.2获取所有商品数
已在Product Controller中实现
请求路径：/api/products/count
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": 156
}

## 2.3获取所有订单数
已在OrderedController中实现
请求路径：/api/orders/count
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": 125
}

## 2.4 总销售额
已在OrderedController中实现
请求路径：/api/orders/total-sales
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": 156780.50
}

## 2.5 获取昨天和今天的订单数对比（含百分数）
已在StatisticsController中实现
请求路径：/api/statistics/order-comparison
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": {
    "todayValue": 15,
    "yesterdayValue": 12,
    "changePercent": 25.00
  }
}
说明：changePercent正数表示增长，负数表示减少

## 2.6 获取今日新增用户数

请求方式：

GET

请求路径：

/admin/user/add

请求格式：

```
/admin/user/add
```

返回格式：

```
{
	"code": 1,
	"data": 4,
	"msg": null
}
```

## 2.7 获取昨天和今天的总用户数（可以计算出一个今天相较于昨天的一个增长或减少的百分值）

请求方式：

GET

请求路径：

/admin/user/compare

返回格式：

```
{
	"code": 1,
	"data": {
		"todayCount": 2,
		"yesterdayCount": 1,
		"growthRate": 100
	},
	"msg": null
}
```

功能8：获取昨天和今天的销售额对比（包含百分比）
已在StatisticsController中实现
请求路径：/api/statistics/sales-comparison
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": {
    "todaySales": 12800.75,
    "yesterdaySales": 9800.50,
    "changePercent": 30.62
  }
}

功能9：获取昨天和今天的商品数对比（包含百分比）
已在StatisticsController中实现
请求路径：/api/statistics/product-comparison
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": {
    "todayValue": 12,
    "yesterdayValue": 8,
    "changePercent": 50.00
  }
}

功能10：获取今日低库存商品数
已在ProductController中实现
请求路径：/api/products/low-stock/count
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": 5
}

功能11：获取今日各省份订单数
已在OrderedController中实现
请求路径：/api/orders/today/statistics/by-province
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "province": "北京市",
      "today_order_count": 8
    },
    {
      "province": "上海市",
      "today_order_count": 6
    }
  ]
}



## 2.15 用户禁用选项

已在1.10中实现

## 2.16 获取所有用户信息（ID，用户名，手机号，邮箱，状态，注册时间）

已在1.1中实现,返回了user的所有属性，从中解析即可



功能17：获取所有商品信息
已在ProductController中实现
请求路径：/api/products
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "pid": 1,
      "name": "iPhone 15 Pro",
      "price": 8999.00,
      "description": "苹果旗舰手机",
      "cpu": "A17 Pro",
      "gpu": "6核图形处理器",
      "storage": "256GB",
      "size": "6.1英寸",
      "type": "手机",
      "picture": "https://example.com/iphone15.jpg",
      "stock": 100,
      "rating": 5
    },
    {
      "pid": 2,
      "name": "华为Mate 60",
      "price": 6999.00,
      "description": "华为旗舰手机",
      "cpu": "麒麟9000S",
      "gpu": "Maleoon 910",
      "storage": "512GB",
      "size": "6.8英寸",
      "type": "手机",
      "picture": "https://example.com/mate60.jpg",
      "stock": 150,
      "rating": 4
    }
  ]
}

功能18：获取所有订单信息
已在OrderedController中实现
请求路径：/api/orders
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "oid": 1001,
      "status": 1,
      "orderTime": "2023-10-16T14:30:45",
      "amount": 298.50,
      "uid": 1,
      "province": "北京市",
      "receiverAddress": "朝阳区建国路88号",
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "remark": "尽快发货"
    },
    {
      "oid": 1002,
      "status": 0,
      "orderTime": "2023-10-15T10:20:33",
      "amount": 1299.00,
      "uid": 2,
      "province": "上海市",
      "receiverAddress": "浦东新区陆家嘴",
      "receiverName": "李四",
      "receiverPhone": "13900139000",
      "remark": "包装好一点"
    }
  ]
}

功能19：获取所有商家信息
已在BusinessController中实现
请求路径：/api/businesses
请求方式：GET
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "bid": 1,
      "bname": "苹果官方旗舰店",
      "password": "$2a$10$ABC123XYZ456..."
    },
    {
      "bid": 2,
      "bname": "华为官方旗舰店",
      "password": "$2a$10$DEF456GHI789..."
    }
  ]
}

功能20：获取对应商品对应评价
已在FeedbackController中实现
请求路径：/api/feedbacks/product/{pid}
请求方式：GET
路径参数：pid (商品ID)
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "fid": 1,
      "uid": 1,
      "pid": 1,
      "feedTime": "2023-10-16T14:30:45",
      "star": 5,
      "comment": "商品质量非常好，物流也很快！"
    },
    {
      "fid": 2,
      "uid": 2,
      "pid": 1,
      "feedTime": "2023-10-15T10:20:33",
      "star": 4,
      "comment": "还不错，性价比高"
    }
  ]
}

## 2.21 编辑用户身份

请求方式：

PUT

请求路径：

/admin/user/updaterole/{uid}

请求格式：

```
/admin/user/updaterole/2?role=admin
```

返回格式

```
{
	"code": 1,
	"data": null,
	"msg": null
}
```





# 3.登录注册

## 3.1 用户手机号+密码登录

请求方式：

GET

请求路径：

/admin/user/phonelogin

请求方式：

```
/admin/user/phonelogin?phone=13800138002&password=e10adc3949ba59abbe56e057f20f883e
```

返回格式：

```
{
	"code": 1,
	"data": null,
	"msg": null
}
{
	"code": 0,
	"data": null,
	"msg": "手机号或密码错误"
}
```

## 3.2 用户邮箱+密码登录

请求方式：

GET

请求路径：

/admin/user/emaillogin

请求方式：

```
/admin/user/emaillogin?email=zhangsan@qq.com&password=e10adc3949ba59abbe56e057f20f883
```

返回格式：

```
{
	"code": 1,
	"data": null,
	"msg": null
}
{
	"code": 0,
	"data": null,
	"msg": "邮箱号或密码错误"
}
```

## 3.3 用户名+密码登录

请求方式：

GET

请求路径：

/admin/user/usernamelogin

请求方式：

```
http://localhost:8080/admin/user/usernamelogin?username=张三&password=e10adc3949ba59abbe56e057f20f883e
```

返回格式：

```
{
	"code": 1,
	"data": null,
	"msg": null
}
{
	"code": 0,
	"data": null,
	"msg": "用户名或密码错误"
}
```

## 3.4 检查用户名是否已存在

请求方式：

GET

请求路径：

/admin/user/namedistinct

请求方式：

```
/admin/user/namedistinct?username=张三
```

返回格式：

```
{
	"code": 0,
	"data": null,
	"msg": "用户名已存在"
}
{
	"code": 1,
	"data": null,
	"msg": null
}
```

## 3.5 检测手机号是不是被注册check-tel

请求方式：

GET

请求路径：

/admin/user/phonedistinct

请求方式：

```
/admin/user/phonedistinct?phone=1380013800
```

返回格式：

```
{
	"code": 0,
	"data": null,
	"msg": "手机号已存在"
}
{
	"code": 1,
	"data": null,
	"msg": null
}
```

## 3.6  用户通过手机号注册

这里需要分为几个需求

1、验证手机号是否已被注册，已在3.5实现

2、发送短信验证码

请求方式：

POST

请求路径：

/api/sms/send-code

请求格式：

```
{
  "phone":"13778954955",
  "purpose":"register"
}
```

返回格式：

```
{
	"code": 1,
	"data": "验证码发送成功",
	"msg": null
}
```

3、检查短信验证码

请求方式：

POST

请求路径：

/api/sms/verify-code

请求格式：

```
{
    "phone": "13778954955",
    "code": "742608"
}
```

返回格式：

```
{
	"code": 1,
	"data": "验证码验证成功",
	"msg": null
}
{
	"code": 0,
	"data": null,
	"msg": "验证码错误或已过期"
}
```

4、检查是否可以重新发送短信验证码

请求方式：

GET

请求路径：

/api/sms/can-resend/{phone}

请求格式：

```
/api/sms/can-resend/13778954955
```

返回格式：

```
{
	"code": 1,
	"data": {
		"canResend": true,
		"remaining": 0
	},
	"msg": null
}
{
	"code": 1,
	"data": {
		"canResend": false,
		"remaining": 297    //剩余有效期
	},
	"msg": null
}
```





## 3.8 通过id用户修改用户信息（头像/换绑手机号/邮箱换绑/换密码）（修改除头像外信息都会有密码再次验证）

分为3个需求实现

1、验证用户密码

请求方式：

GET

请求路径：

/admin/user/verifypassword

请求格式：

```
/admin/user/verifypassword?uid=2&password=e10adc3949ba59abbe56e057f20f883
```

返回格式：

```
{
	"code": 1,
	"data": "验证通过",
	"msg": null
}
{
	"code": 0,
	"data": null,
	"msg": "密码错误"
}
```

2、修改用户信息

请求方式：

PUT

请求路径：

/admin/user/updateinfo

请求格式：

```
{
  "uid":"10",
  "username":"zhangsan",
  "phone":"10086"
}//可以传递任意字段，其中uid为必须，其他可选
```

返回格式：

```
{
	"code": 1,
	"data": null,
	"msg": null
}
```

3、上传头像

请求方式：

PUT

请求路径：

/admin/user/avator

请求类型：

```
multipart/form-data
```

返回格式：

```
{
	"code": 1,
	"data": "https://yxtbucket01.oss-cn-beijing.aliyuncs.com/5bebc532-2bd7-44d2-b606-957881972d82..jpg",
	"msg": null
}//返回了图片的url，根据url可以直接打开图片
```


# 4.商品商家相关
## 4.1 获取所有商品信息（商家角度）
请求路径：/api/business/products/my/{bid}
请求方式：GET
路径参数：bid - 商家ID
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "pid": 1,
      "name": "联想ThinkPad X1",
      "price": 12999.00,
      "description": "高端商务笔记本",
      "cpu": "i7-1260P",
      "gpu": "集成显卡",
      "storage": "1TB SSD",
      "size": "14英寸",
      "type": "笔记本",
      "picture": "thinkpad.jpg",
      "stock": 50,
      "rating": 100
    }
  ]
}
说明：商家只能查看自己管辖范围内的商品

## 4.2 通过商品id获取单个商品信息
请求路径：/api/business/products/my/{bid}/{pid}
请求方式：GET
路径参数：

bid - 商家ID

pid - 商品ID
返回格式：
{
  "code": 1,
  "msg": null,
  "data": {
    "pid": 1,
    "name": "联想ThinkPad X1",
    "price": 12999.00,
    "description": "高端商务笔记本",
    "cpu": "i7-1260P",
    "gpu": "集成显卡",
    "storage": "1TB SSD",
    "size": "14英寸",
    "type": "笔记本",
    "picture": "thinkpad.jpg",
    "stock": 50,
    "rating": 100
  }
}

## 4.3 商家上传商品（包括所有商品信息，默认评分是100
请求路径：/api/business/products/upload/{bid}
请求方式：POST
路径参数：bid - 商家ID
请求格式：
{
  "name": "联想拯救者Y9000P",
  "price": 9999.00,
  "description": "高性能游戏笔记本",
  "cpu": "i7-12700H",
  "gpu": "RTX 3060",
  "storage": "1TB SSD",
  "size": "16英寸",
  "type": "游戏本",
  "picture": "y9000p.jpg",
  "stock": 30
}
返回格式：
{
  "code": 1,
  "msg": null,
  "data": "商品上传成功，商品ID：101"
}
说明：上传的商品会自动设置评分=100，并在business_product表中建立关联

## 4.4 商家获取自己管辖范围内所有商品的所有订单（不显示stauts=3的订单）
请求路径：/api/business/orders/my/{bid}
请求方式：GET
路径参数：bid - 商家ID
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "oid": 1001,
      "status": 0,
      "orderTime": "2023-10-16T14:30:45",
      "amount": 12999.00,
      "uid": 1,
      "province": "北京市",
      "receiverAddress": "朝阳区望京SOHO",
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "remark": "请尽快发货",
      "statusText": "待发货"
    }
  ]
}

## 4.5 商家接到订单时候点击发货（改变status=1
请求路径：/api/business/orders/ship/{bid}/{oid}
请求方式：PUT
路径参数：

bid - 商家ID

oid - 订单ID
返回格式：
{
  "code": 1,
  "msg": null,
  "data": "发货成功"
}
错误情况;
{
  "code": 0,
  "msg": "发货失败：订单不存在、不属于您或状态不正确",
  "data": null
}

## 4.6 当有新评论时，更改对应商品的评分（5星+10，4星+5，三星+0，二星-5，一星-10）
请求路径：/api/feedbacks
请求方式：POST
请求格式：
{
  "uid": 1,
  "pid": 100,
  "star": 5,
  "comment": "商品非常好用！"
}
返回格式：
{
  "code": 1,
  "msg": null,
  "data": "评价发表成功，商品评分已更新"
}

## 4.7 商家花钱自己增加商品评分（200块钱+5，500块钱+15，1000块钱+40
请求路径：/api/business/rating/increase
请求方式：POST
请求格式：
{
  "bid": 1,
  "pid": 100,
  "amount": 500
}
返回格式：
{
  "code": 1,
  "msg": null,
  "data": "评分增加成功，已扣除500元"
}
错误情况：
{
  "code": 0,
  "msg": "评分增加失败：商品不存在、不属于您或金额不正确",
  "data": null
}
说明：商家只能给自己的商品增加评分，只接受200/500/1000三种金额

## 4.8 通过商家登录的bname和password获取商家的所有信息
请求路径：/api/businesses/login
请求方式：POST
请求格式：
{
  "bname": "联想官方旗舰店",
  "password": "123456"
}
返回格式：
{
  "code": 1,
  "msg": null,
  "data": {
    "bid": 1,
    "bname": "联想官方旗舰店"
  }
}
错误情况：
{
  "code": 0,
  "msg": "商家名称或密码错误",
  "data": null
}
 ## 4.9 通过商家id关联获取该商家的所有物品
 GET /api/business/products/my/{bid}
 有重复，见4.1
 ## 4.10 商家添加商品信息（包含所有的）
POST /api/business/products/upload/{bid}
有重复，见4.3
 ## 4.11 返回库存小于等于5的所有商品（商家获取自己商品中库存小于等于5的商品列表）
 请求路径：/api/business/products/my/{bid}/low-stock
请求方式：GET
路径参数：bid - 商家ID
返回格式：
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "pid": 1,
      "name": "联想ThinkPad X1",
      "price": 12999.00,
      "description": "高端商务笔记本",
      "cpu": "i7-1260P",
      "gpu": "集成显卡",
      "storage": "1TB SSD",
      "size": "14英寸",
      "type": "笔记本",
      "picture": "thinkpad.jpg",
      "stock": 3,
      "rating": 100
    }
  ]
}
## 4.12 商家通过商品id修改商品的信息
请求路径：/api/business/products/update/{bid}
请求方式：PUT
路径参数：bid - 商家ID
请求格式：
{
  "pid": 100,
  "name": "联想ThinkPad X1 Carbon 2024",
  "price": 13999.00,
  "description": "2024款旗舰商务本",
  "cpu": "i7-1360P",
  "gpu": "集成显卡",
  "storage": "2TB SSD",
  "size": "14英寸",
  "type": "笔记本",
  "picture": "thinkpad2024.jpg",
  "stock": 80
}
返回格式：
{
  "code": 1,
  "msg": null,
  "data": "商品信息修改成功"
}
错误情况：
{
  "code": 0,
  "msg": "商品不存在或不属于该商家",
  "data": null
}

## 5.购物车订单相关
### 5.1.用户id加入商品到购物车(如果商品已经在购物车中，则直接增加数量)
- 方法： POST
- URL： http://localhost:8080/admin/cart/add
- Body:
  {
    "uid": 1,
    "pid": 2,
    "quantity": 1,
    "bid": 1
  }
- 响应示例（成功）：
  {
    "code": 1,
    "data": true,
    "msg": null
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "加入购物车失败: 商品不存在"
  }
### 5.2.用户id删除购物车的商品
根据用户id和商品id删除购物车中的商品
- 方法： DELETE
- URL： http://localhost:8080/admin/cart/delete?uid={uid}&pid={pid}
- 响应示例（成功）：
  {
    "code": 1,
    "data": true,
    "msg": null
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "删除失败，可能购物车项不存在或不属于该用户"
  }

另外实现：
根据购物车ID删除: DELETE /admin/cart/delete/{cid}?uid={uid}
清空用户购物车: DELETE /admin/cart/clear/{uid}

### 5.3.用户ID购买商品PID生成订单(同时会录入填写地址，昵称，电话，备注，status默认是o)
直接创建订单：
- 方法： POST
- URL： http://localhost:8080/api/orders/create
- Body:
  {
    "uid": 1,
    "items": [
        {
            "pid": 2,
            "quantity": 1,
            "bid": 1
        },
        {
            "pid": 3,
            "quantity": 2,
            "bid": 2
        }
    ],
    "province": "广东省",
    "receiverAddress": "广州市天河区XX街道XX号",
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "remark": "请尽快发货"
  }
- 响应示例（成功）：
{
    "code": 1,
    "data": 10,
    "msg": null
}


从购物车中创建订单：
- 方法： POST
- URL： http://localhost:8080/admin/cart/create-order/{uid}
- Body:
  {
    "province": "北京市",
    "receiverAddress": "朝阳区建国门外大街1号",
    "receiverName": "李四",
    "receiverPhone": "13900139000",
    "remark": "请工作日配送"
  }
- 响应示例（成功）：
  {
    "code": 1,
    "data": 10,
    "msg": null
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "创建订单失败：购物车为空"
  }

### 5.4.生成订单时用户需要首先选择省份，再填写具体地址（前端实现？）

### 5.5.用户点击收货(订单状态status=2)
- 方法： PUT
- URL： http://localhost:8080/api/orders/{oid}/confirm-receipt?uid={uid}
- 响应示例（成功）：
  {
    "code": 1,
    "data": true,
    "msg": null
  }

### 5.6.用户点击取消订单(仅在status=0时候才能status=3)
- 方法： PUT
- URL： http://localhost:8080/api/orders/{oid}/cancel?uid={uid}
- 响应示例（成功）：
  {
    "code": 1,
    "data": true,
    "msg": null
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "取消订单失败：只有待发货的订单才能取消，当前状态：已发货"
  }

### 5.7.用户对已购买的商品评价并打星(此评价同步更新到商品评分)
- 方法： POST
- URL： http://localhost:8080/api/feedbacks/submit
- Body:
  {
    "uid": 1,
    "pid": 2,
    "star": 5,
    "comment": "商品质量非常好，物流很快，下次还会购买！"
  }
- 响应示例（成功）：
  {
    "code": 1,
    "data": true,
    "msg": null
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "评价提交失败: 您尚未购买此商品，无法评价"
  }
- 响应示例（失败）：
  {
	"code": 0,
	"data": null,
	"msg": "评价提交失败: 您已经评价过此商品，如需修改请使用更新评价"
  }

