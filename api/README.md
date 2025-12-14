# 1.User接口

## 1.1 获取用户名单和信息

请求路径：/admin/user

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

使用测试数据如下：

```sql
-- 插入用户测试数据
INSERT INTO user (username, password, email, phone, status, avatar, role) VALUES
-- 管理员用户
('admin', '$2a$10$ABC123XYZ456def789ghi.jklmnoPqrstuVwxyzA1B2C3', 'admin@example.com', '13800138000', 1, 'https://example.com/avatars/admin.jpg', 'admin'),
-- 普通用户
('张三', '$2a$10$DEF456GHI789jkl012mno.pqrstuVwxyzA1B2C3D4E5', 'zhangsan@example.com', '13900139000', 1, 'https://example.com/avatars/zhangsan.jpg', 'user'),
('李四', '$2a$10$JKL012MNO345pqr678stu.vwxyzA1B2C3D4E5F6G7', 'lisi@example.com', '13600136000', 1, NULL, 'user'),
('王五', '$2a$10$PQR678STU901vwx234yza.bcdeF1G2H3I4J5K6L7M8', 'wangwu@example.com', '13500135000', 0, 'https://example.com/avatars/wangwu.png', 'user'),
('赵六', '$2a$10$VWX234YZA567bcd890efg.hijklM1N2O3P4Q5R6S7T8', 'zhaoliu@example.com', '13700137000', 1, '', 'user'),
('孙七', '$2a$10$BCD890EFG123hij456klm.nopqrS1T2U3V4W5X6Y7Z8', 'sunqi@example.com', '13400134000', 1, 'https://example.com/avatars/sunqi.gif', 'user'),
('周八', '$2a$10$HIJ456KLM789nop012qrs.tuvwxY1Z2A3B4C5D6E7F8', 'zhouba@example.com', '13300133000', 1, 'https://example.com/avatars/zhouba.jpg', 'user'),
('吴九', '$2a$10$NOP012QRS345tuv678wxy.zabcdE1F2G3H4I5J6K7L8', 'wujiu@example.com', '13200132000', 0, NULL, 'user'),
('郑十', '$2a$10$TUV678WXY901zab234cde.fghiJ1K2L3M4N5O6P7Q8', 'zhengshi@example.com', '13100131000', 1, 'https://example.com/avatars/zhengshi.jpg', 'user'),
-- 另一个管理员
('sysadmin', '$2a$10$ZAB234CDE567fgh890ijk.lmnoP1Q2R3S4T5U6V7W8', 'sysadmin@example.com', '13000130000', 1, 'https://example.com/avatars/sysadmin.jpg', 'admin');
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
  "msg": null,
  "data": [
      {
        "oid": 1001,
        "orderNo": "ORDER20231016123456",
        "status": 1,
        "statusText": "已发货",
        "orderTime": "2023-10-16 14:30:45",
        "amount": 2,
        "money": 298.50,
        "productName": "iPhone 15 Pro",
        "productImage": "https://example.com/iphone15.jpg",
        "businessName": "苹果官方旗舰店",
        "address": "北京市朝阳区建国路88号",
        "momo": "张三",
        "phone": "13800138000"
      },
      {
        "oid": 1002,
        "orderNo": "ORDER20231015111213",
        "status": 0,
        "statusText": "待发货",
        "orderTime": "2023-10-15 10:20:33",
        "amount": 1,
        "money": 1299.00,
        "productName": "华为Mate 60",
        "productImage": "https://example.com/mate60.jpg",
        "businessName": "华为官方旗舰店",
        "address": "上海市浦东新区陆家嘴",
        "momo": "李四",
        "phone": "13900139000"
      },
      {
        "oid": 1003,
        "orderNo": "ORDER20231014104530",
        "status": 2,
        "statusText": "已收货",
        "orderTime": "2023-10-14 11:15:20",
        "amount": 3,
        "money": 456.80,
        "productName": "小米13 Ultra",
        "productImage": "https://example.com/xiaomi13.jpg",
        "businessName": "小米官方旗舰店",
        "address": "广州市天河区体育西路",
        "momo": "王五",
        "phone": "13600136000"
      }
    ],
    "totalCount": 125,
    "filteredCount": 3,
    "summary": {
      "totalOrders": 125,
      "totalAmount": 156780.50,
      "pendingCount": 8,
      "shippedCount": 25,
      "receivedCount": 89,
      "cancelledCount": 3
    }
  ],
}
```

测试用例

```
-- 插入一些测试订单数据
INSERT INTO ordered (status, order_time, amount, money, pid, uid, bid, address, momo, phone, remark) VALUES
-- 待发货订单
(0, '2023-10-16 11:30:15', 1, 45.50, 11, 11, 2, '天津市和平区南京路', '天津李', '13811223344', ''),
(0, '2023-10-16 10:45:33', 2, 119.80, 12, 12, 3, '大连市中山区人民路', '大连王', '13922334455', '要最新批次'),

-- 已发货订单
(1, '2023-10-15 15:22:18', 1, 68.00, 14, 14, 2, '厦门市思明区湖滨南路', '厦门陈', '13544556677', '不要压坏'),
(1, '2023-10-15 12:10:45', 4, 512.40, 15, 15, 3, '长沙市岳麓区麓山南路', '长沙刘', '13455667788', '赠品请一起发'),

-- 已收货订单
(2, '2023-10-14 18:30:55', 2, 156.00, 16, 16, 1, '郑州市金水区花园路', '郑州赵', '13366778899', '已收货，满意'),
(2, '2023-10-13 14:20:33', 1, 89.90, 17, 17, 2, '合肥市包河区马鞍山路', '合肥孙', '13277889900', '质量不错'),

-- 取消订单
(3, '2023-10-12 16:40:12', 3, 267.00, 18, 18, 3, '福州市鼓楼区五四路', '福州周', '13188990011', '拍错了，取消'),
(3, '2023-10-11 10:15:28', 1, 75.00, 19, 19, 1, '南昌市东湖区阳明路', '南昌吴', '13099001122', '价格有点贵'),

-- 大额订单
(0, '2023-10-16 14:55:20', 10, 1280.50, 20, 20, 2, '济南市历下区泉城路', '济南郑', '13800990099', '送礼品卡'),
(1, '2023-10-15 19:30:44', 8, 956.80, 21, 21, 3, '哈尔滨市道里区中央大街', '哈尔滨钱', '13900880088', '急用，加急'),

-- 小额订单
(2, '2023-10-14 08:15:33', 1, 19.90, 22, 22, 1, '长春市朝阳区重庆路', '长春冯', '13600770077', '包邮就行'),

-- 不同时间段订单
(0, '2023-10-16 07:05:12', 2, 138.60, 23, 23, 2, '太原市迎泽区柳巷', '太原卫', '13500660066', '早上订单'),
(0, '2023-10-16 23:45:38', 1, 59.00, 24, 24, 3, '石家庄市长安区中山东路', '石家庄蒋', '13400550055', '半夜下单'),

-- 有特殊要求的订单
(1, '2023-10-15 09:10:25', 3, 345.70, 25, 25, 1, '兰州市城关区张掖路', '兰州沈', '13300440044', '需要安装服务'),
(2, '2023-10-14 20:55:19', 2, 198.40, 26, 26, 2, '西宁市城中区西大街', '西宁韩', '13200330033', '要求无理由退换货'),

-- 国际地址订单
(0, '2023-10-16 13:20:47', 5, 578.90, 27, 27, 3, '香港特别行政区湾仔区', '香港杨', '13811228899', '发到香港'),
(1, '2023-10-15 11:45:32', 2, 234.50, 28, 28, 1, '澳门特别行政区大堂区', '澳门朱', '13922339988', '澳门特别行政区'),

-- 偏远地区订单
(0, '2023-10-16 15:30:18', 1, 88.00, 29, 29, 2, '西藏自治区拉萨市城关区', '拉萨秦', '13633448877', '西藏地区发货慢'),
(2, '2023-10-13 17:25:14', 3, 312.60, 30, 30, 3, '新疆维吾尔自治区乌鲁木齐市天山区', '乌鲁木齐尤', '13544557766', '新疆包邮');
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
			"uid": 4,
			"pid": 7
		},
		{
			"cid": 2,
			"uid": 4,
			"pid": 8
		}
	],
	"msg": null
}
```

测试用例

```
-- 插入购物车记录
INSERT INTO cart (uid, pid) VALUES
(4, 7),   -- 用户4添加商品7
(4, 8),   -- 用户4添加商品8
(5, 9),   -- 用户5添加商品9
(5, 10),  -- 用户5添加商品10
(6, 1),   -- 用户6添加商品1
(6, 5),   -- 用户6添加商品5
(7, 3),   -- 用户7添加商品3
(7, 7),   -- 用户7添加商品7
(8, 2),   -- 用户8添加商品2
(8, 8);   -- 用户8添加商品8
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

