

## **Version1**

**最初始阶段，新增了user表和插入的user数据**
阳阳阳---2025.12.8





新建一个users表（用户信息）
-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: ADMIN, USER',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入数据
INSERT INTO users (username, password, email, phone, status, role) VALUES
('管理员1', '123456', 'admin1@lenovo.com', '13800138000', 1, 'ADMIN'),
('小绿泡泡', '123456', 'xiaolvpaopao@163.com', '13800138001', 1, 'USER'),
('小蓝泡泡', '123456', 'xiaolanpaopao@qq.com', '13900139000', 1, 'USER'),
('小黄泡泡', '123456', 'xiaohuangpaopao@gmail.com', '13700137000', 1, 'USER'),
('管理员2', '123456', 'admin2@lenovo.com', '13400134000', 1, 'ADMIN'),
('阳', '123456', 'yang.user@example.com', '13600136000', 0, 'USER'),
('陈', '123456', 'chen.user@example.com', '13500135000', 1, 'USER'),
('冯', '123456', 'feng.user@example.com', '13300133000', 1, 'USER'),
('张', '123456', 'zhang.user@example.com', '13100131000', 0, 'USER'),
('赵', '123456', 'zhao.user@example.com', '13000130000', 1, 'USER'),
('牟', '123456', 'mou.user@example.com', '15000150000', 1, 'USER'),
('欧阳', '123456', 'ouyang.user@example.com', '13200132000', 1, 'USER');



## Version2

**增加了product表，并改变了product里面的部分属性，**

**2025.12.8---阳阳阳再找小绿泡泡**



-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色: ADMIN, USER',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入数据
INSERT INTO users (username, password, email, phone, status, role) VALUES
('管理员1', '123456', 'admin1@lenovo.com', '13800138000', 1, 'ADMIN'),
('小绿泡泡', '123456', 'xiaolvpaopao@163.com', '13800138001', 1, 'USER'),
('小蓝泡泡', '123456', 'xiaolanpaopao@qq.com', '13900139000', 1, 'USER'),
('小黄泡泡', '123456', 'xiaohuangpaopao@gmail.com', '13700137000', 1, 'USER'),
('管理员2', '123456', 'admin2@lenovo.com', '13400134000', 1, 'ADMIN'),
('阳', '123456', 'yang.user@example.com', '13600136000', 0, 'USER'),
('陈', '123456', 'chen.user@example.com', '13500135000', 1, 'USER'),
('冯', '123456', 'feng.user@example.com', '13300133000', 1, 'USER'),
('张', '123456', 'zhang.user@example.com', '13100131000', 0, 'USER'),
('赵', '123456', 'zhao.user@example.com', '13000130000', 1, 'USER'),
('牟', '123456', 'mou.user@example.com', '15000150000', 1, 'USER'),
('欧阳', '123456', 'ouyang.user@example.com', '13200132000', 1, 'USER');



-- 创建商品表
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,           -- 商品名称
    price DECIMAL(10,2) NOT NULL,         -- 价格
    image_url VARCHAR(200),               -- 图片链接
    brand VARCHAR(50),                    -- 品牌
    stock INT NOT NULL,                   -- 库存
    description TEXT,                     -- 描述
    specifications TEXT,                  -- 详细配置
    rating DOUBLE                         -- 评分
);
-- 添加数据
INSERT INTO products (name, price, image_url, brand, stock, description, rating) VALUES
('联想拯救者Y9000P', 8999.00, 'https://gw.alicdn.com/imgextra/i3/2201491548315/O1CN01XqX9Xz1re5X9Y6Q7j_!!2201491548315.jpg', '联想', 50, '16英寸游戏本，RTX 4060显卡', 4.8),
('联想小新Pro 14', 5499.00, 'https://img.alicdn.com/imgextra/i1/2201491548315/O1CN01J8Q6Xw1re5X9Y6Q7j_!!2201491548315.jpg', '联想', 120, '14英寸轻薄本，适合办公学习', 4.6),
('联想ThinkPad X1', 12999.00, 'https://img.alicdn.com/imgextra/i4/2201491548315/O1CN01Z8Q6Xw1re5X9Y6Q7j_!!2201491548315.jpg', '联想', 30, '商务旗舰本，军工品质', 4.9);

-- 给products表添加创建时间和更新时间
ALTER TABLE products ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 更新现有数据
UPDATE products SET created_at = NOW(), updated_at = NOW();

-- 联想电脑商城 - 新增27个商品（不会与现有3个重复）
INSERT INTO `products` (`name`, `price`, `description`, `image_url`, `brand`, `stock`, `specifications`, `rating`) VALUES
('联想拯救者R7000', 6599, '高性价比游戏笔记本', 'https://img.lenovo.com.cn/product1.jpg', '联想', 65, '处理器：AMD Ryzen 7 5800H，显卡：RTX 3050 Ti，内存：16GB DDR4，硬盘：512GB NVMe SSD', 4.7),
('联想小新Air 15', 5299, '大屏轻薄办公笔记本', 'https://img.lenovo.com.cn/product2.jpg', '联想', 110, '处理器：Intel Core i5-1135G7，内存：16GB，硬盘：512GB SSD，屏幕：15.6英寸全高清', 4.5),
('联想ThinkPad T14s', 8999, '高端商务轻薄笔记本', 'https://img.lenovo.com.cn/product3.jpg', '联想', 40, '处理器：Intel Core i7-1165G7，内存：16GB LPDDR4x，硬盘：1TB SSD，重量：1.28kg', 4.8),
('联想YOGA 14c', 6999, '360度翻转触控笔记本', 'https://img.lenovo.com.cn/product4.jpg', '联想', 55, '处理器：Intel Core i5-1135G7，内存：16GB，硬盘：512GB SSD，触控屏，支持手写笔', 4.6),
('联想拯救者Y7000P', 8299, '专业电竞游戏笔记本', 'https://img.lenovo.com.cn/product5.jpg', '联想', 45, '处理器：Intel Core i7-11800H，显卡：RTX 3060，内存：16GB，硬盘：1TB SSD，144Hz屏幕', 4.9),
('联想小新Pro 16', 6499, '高性能创作笔记本', 'https://img.lenovo.com.cn/product6.jpg', '联想', 70, '处理器：AMD Ryzen 7 5800H，显卡：RTX 3050，内存：16GB，屏幕：2.5K 120Hz高色域', 4.7),
('联想ThinkPad X13', 7499, '便携商务笔记本', 'https://img.lenovo.com.cn/product7.jpg', '联想', 60, '处理器：AMD Ryzen 7 PRO 5850U，内存：16GB，硬盘：512GB SSD，军工级认证', 4.6),
('联想IdeaPad 3', 3999, '入门级学习笔记本', 'https://img.lenovo.com.cn/product8.jpg', '联想', 180, '处理器：AMD Ryzen 5 5500U，内存：8GB，硬盘：256GB SSD，适合学生上网课', 4.2),
('联想YOGA Duet 7i', 7999, '二合一平板笔记本', 'https://img.lenovo.com.cn/product9.jpg', '联想', 35, '处理器：Intel Core i5-1135G7，内存：16GB，硬盘：512GB SSD，可拆卸键盘，带手写笔', 4.5),
('联想拯救者R9000X', 9499, '轻薄设计游戏笔记本', 'https://img.lenovo.com.cn/product10.jpg', '联想', 30, '处理器：AMD Ryzen 7 5800H，显卡：RTX 3060，内存：32GB，重量：1.9kg，2K屏幕', 4.8),
('联想小新Pad Pro', 2999, '高性能安卓平板', 'https://img.lenovo.com.cn/product11.jpg', '联想', 125, '屏幕：11.5英寸2.5K OLED，内存：6GB+128GB，电池：8600mAh，四扬声器', 4.4),
('联想ThinkCentre M75q', 3499, '超小型台式电脑', 'https://img.lenovo.com.cn/product12.jpg', '联想', 80, '处理器：AMD Ryzen 5 PRO 5650GE，内存：8GB，硬盘：256GB SSD，体积仅1L', 4.3),
('联想天逸510S', 4299, '家用办公台式机', 'https://img.lenovo.com.cn/product13.jpg', '联想', 95, '处理器：Intel Core i5-10400，内存：8GB，硬盘：1TB HDD+256GB SSD，23.8英寸显示器', 4.4),
('联想拯救者刃7000K', 8999, '电竞游戏台式机', 'https://img.lenovo.com.cn/product14.jpg', '联想', 50, '处理器：Intel Core i7-11700F，显卡：RTX 3060 Ti，内存：16GB DDR4，硬盘：512GB SSD+1TB HDD', 4.8),
('联想YOGA 27', 9999, '可旋转一体机', 'https://img.lenovo.com.cn/product15.jpg', '联想', 25, '处理器：AMD Ryzen 7 5800H，内存：16GB，硬盘：1TB SSD，屏幕：27英寸4K，支持重力旋转', 4.7),
('联想ThinkStation P350', 12999, '专业工作站', 'https://img.lenovo.com.cn/product16.jpg', '联想', 20, '处理器：Intel Xeon W-1350，显卡：NVIDIA T1000，内存：32GB ECC，硬盘：1TB SSD', 4.9),
('联想昭阳K4e', 5699, '企业办公笔记本', 'https://img.lenovo.com.cn/product17.jpg', '联想', 85, '处理器：Intel Core i5-1135G7，内存：16GB，硬盘：512GB SSD，指纹识别，TPM安全芯片', 4.5),
('联想异能者Co-40', 6499, '设计师台式电脑', 'https://img.lenovo.com.cn/product18.jpg', '联想', 40, '处理器：AMD Ryzen 7 5700G，内存：16GB，显卡：RTX 3050，硬盘：512GB SSD，水冷散热', 4.6),
('联想小新Mini', 3299, '迷你主机', 'https://img.lenovo.com.cn/product19.jpg', '联想', 110, '处理器：Intel Core i5-11320H，内存：16GB，硬盘：512GB SSD，体积0.5L，静音设计', 4.3),
('联想ThinkBook 14p', 6299, '商务性能笔记本', 'https://img.lenovo.com.cn/product20.jpg', '联想', 75, '处理器：AMD Ryzen 7 5800H，内存：16GB，硬盘：512GB SSD，屏幕：2.2K 100% sRGB', 4.7),
('联想拯救者电竞手机', 3499, '游戏智能手机', 'https://img.lenovo.com.cn/product21.jpg', '联想', 150, '处理器：骁龙888，内存：12GB+256GB，屏幕：6.92英寸144Hz，电池：5500mAh', 4.5),
('联想智能时钟', 499, '智能家居设备', 'https://img.lenovo.com.cn/product22.jpg', '联想', 300, '屏幕：4英寸IPS，支持Google助手，蓝牙音箱，USB充电接口，语音控制', 4.1),
('联想ThinkVision显示器', 1899, '27英寸专业显示器', 'https://img.lenovo.com.cn/product23.jpg', '联想', 65, '屏幕：27英寸2K IPS，色域：99% sRGB，接口：HDMI+DP+USB-C，可旋转升降', 4.4),
('联想来酷智能投影仪', 2399, '便携智能投影', 'https://img.lenovo.com.cn/product24.jpg', '联想', 45, '亮度：600流明，分辨率：1080P，系统：Android 9.0，支持自动对焦，内置电池', 4.2),
('联想USB-C扩展坞', 399, '多接口扩展设备', 'https://img.lenovo.com.cn/product25.jpg', '联想', 500, '接口：HDMI+VGA+USB3.0×3+千兆网口+PD充电，支持4K输出，即插即用', 4.3),
('联想拯救者散热支架', 299, '笔记本电脑散热器', 'https://img.lenovo.com.cn/product26.jpg', '联想', 400, '材质：金属网格，风扇：双120mm风扇，角度：六档可调，USB供电，RGB灯效', 4.0),
('联想无线鼠标', 129, '蓝牙无线鼠标', 'https://img.lenovo.com.cn/product27.jpg', '联想', 600, '连接：蓝牙5.0+2.4GHz无线，DPI：2400，电池：AAA×1，续航：12个月，静音设计', 4.2);



## Version3

**2025.12.10  ——沐衡**

**根据讨论结果喂给d老师创建数据库并建立了其中的八个表的雏形。**
2025.12.10---羊羊羊

因为order是mysql的关键词，所以将order改名为ordered

```sql
-- 1. 用户表 user
CREATE TABLE user (
    uid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常，0禁用',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(10) COMMENT '角色：admin/user',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 2. 商品表 product
CREATE TABLE product (
    pid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    description TEXT COMMENT '描述',
    CPU VARCHAR(50) COMMENT '处理器',
    GPU VARCHAR(50) COMMENT '显卡',
    storage VARCHAR(50) COMMENT '内存',
    size VARCHAR(20) COMMENT '屏幕尺寸',
    type VARCHAR(50) COMMENT '产品类型',
    picture VARCHAR(255) COMMENT '默认图片',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    rating INT DEFAULT 0 COMMENT '评分（推荐度）'
) COMMENT '商品表';

-- 3. 商家表 business
CREATE TABLE business (
    bid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '商家ID',
    bname VARCHAR(100) NOT NULL COMMENT '商家名称',
    password VARCHAR(255) NOT NULL COMMENT '密码'
) COMMENT '商家表';

-- 4. 商家-商品关联表 business_product
CREATE TABLE business_product (
    bpid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    bid INT NOT NULL COMMENT '商家ID',
    pid INT NOT NULL COMMENT '商品ID',
    FOREIGN KEY (bid) REFERENCES business(bid),
    FOREIGN KEY (pid) REFERENCES product(pid)
) COMMENT '商家-商品关联表';

-- 5. 身份表 identity
CREATE TABLE identity (
    iid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '身份ID',
    level INT DEFAULT 0 COMMENT '等级：0~5',
    student TINYINT DEFAULT 0 COMMENT '是否学生：1学生，0普通',
    points INT DEFAULT 0 COMMENT '积分：1元1分，5000分一段',
    uid INT COMMENT '用户ID',
    FOREIGN KEY (uid) REFERENCES user(uid)
) COMMENT '身份表';

-- 6. 购物车表 cart
CREATE TABLE cart (
    cid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    uid INT COMMENT '用户ID',
    pid INT COMMENT '商品ID',
    FOREIGN KEY (uid) REFERENCES user(uid),
    FOREIGN KEY (pid) REFERENCES product(pid)
) COMMENT '购物车表';


-- 7. 评论表 feedback
CREATE TABLE feedback (
    fid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    uid INT COMMENT '用户ID',
    pid INT COMMENT '商品ID',
    feed_time VARCHAR(50) COMMENT '评论时间',
    star INT DEFAULT 5 COMMENT '商品评级',
    comment TEXT COMMENT '评论内容',
    FOREIGN KEY (uid) REFERENCES user(uid),
    FOREIGN KEY (pid) REFERENCES product(pid)
) COMMENT '评论表';

--因为原来的订单表有点问题，所以把订单表给改了一下，分为了订单表和订单详情表。可以把原来的ordered表删除，再执行下面的语句。

-- 8.订单表 (ordered)
CREATE TABLE ordered (
    oid INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    status INT DEFAULT 0 COMMENT '状态',
    order_time DATETIME COMMENT '订单时间',
    amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '实付款',
    uid INT COMMENT '用户ID',
    receiver_address VARCHAR(255) COMMENT '联系地址',
    receiver_name VARCHAR(50) COMMENT '联系人',
    receiver_phone VARCHAR(20) COMMENT '联系电话',
    remark TEXT COMMENT '备注',
    FOREIGN KEY (uid) REFERENCES user(uid) ON DELETE RESTRICT ON UPDATE CASCADE
) COMMENT '订单表';

-- 9.订单详情表 (orderItem)
CREATE TABLE orderItem (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '主键',
    oid INT NOT NULL COMMENT '订单ID',
    pid INT NOT NULL COMMENT '商品编号',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    bid INT NOT NULL COMMENT '商家ID',
    FOREIGN KEY (oid) REFERENCES ordered(oid) ON DELETE CASCADE,
    FOREIGN KEY (pid) REFERENCES product(pid) ON DELETE RESTRICT,
    FOREIGN KEY (bid) REFERENCES business(bid) ON DELETE RESTRICT
) COMMENT '订单详情表';
```

```
-- 1.在订单表中增加省份
ALTER TABLE ordered 
ADD COLUMN province VARCHAR(50) DEFAULT NULL 
COMMENT '省份';

-- 2.在订单详情表中增加小计金额
ALTER TABLE orderItem 
ADD COLUMN subtotal DECIMAL(12, 2) GENERATED ALWAYS AS (price * quantity) STORED
COMMENT '小计金额';

-- 3. 修改评论表（feedback）
-- 修改feed_time字段类型为DATETIME，并设置默认值
ALTER TABLE feedback 
MODIFY COLUMN feed_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间';

-- 4. 改进身份表
ALTER TABLE identity 
MODIFY COLUMN student BOOLEAN DEFAULT FALSE COMMENT '是否学生：true学生，false普通';

-- 5. 修改订单表（ordered）
-- 将order_time字段设置为默认当前时间
ALTER TABLE ordered 
MODIFY COLUMN order_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '订单时间';

-- 6. 完善购物车表，添加必要字段
-- 6.1 先清空cart表中的数据
TRUNCATE TABLE cart;

-- 6.2. 然后再执行ALTER TABLE添加字段和外键
ALTER TABLE cart 
ADD COLUMN quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
ADD COLUMN bid INT NOT NULL COMMENT '商家ID',
ADD FOREIGN KEY (bid) REFERENCES business(bid);

```