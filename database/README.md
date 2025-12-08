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

-- 查看插入的数据
SELECT id, username, password, email, role, status FROM users ORDER BY role DESC, username;
