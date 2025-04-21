create database java6
go
use java6
go

CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    status INT DEFAULT 1 -- 1: Active, 0: Inactive
);
GO
CREATE TABLE sub_categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_categories INT NOT NULL,
    sub_categories_name NVARCHAR(100) NOT NULL,
    status INT DEFAULT 1,
    FOREIGN KEY (id_categories) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);
GO
CREATE TABLE products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_subcategories INT NOT NULL,
    name NVARCHAR(100) NOT NULL,
    image NVARCHAR(MAX),
    price FLOAT NOT NULL,
    qty INT NOT NULL,
    description NVARCHAR(MAX),
    status INT DEFAULT 1, -- 1: Active, 0: Inactive
    FOREIGN KEY (id_subcategories) REFERENCES sub_categories(id) ON DELETE CASCADE ON UPDATE CASCADE,
);
GO
CREATE TABLE discounts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    discount_name NVARCHAR(max) NOT NULL,
    discount_value FLOAT NOT NULL, -- Giá trị giảm giá (% hoặc số tiền cụ thể)
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status INT DEFAULT 1 -- 1: Active, 0: Inactive
);
GO
CREATE TABLE discount_details (
    id INT IDENTITY(1,1) PRIMARY KEY,
    discount_id INT NOT NULL,
    categories_id INT NULL,         -- Giảm giá theo hãng
    subcategories_id INT NULL,      -- Giảm giá theo bộ sưu tập
    product_id INT NULL,            -- Giảm giá theo sản phẩm cụ thể
    FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (categories_id) REFERENCES categories(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (subcategories_id) REFERENCES sub_categories(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
	status INT DEFAULT 1
);
GO
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL unique,
    full_name NVARCHAR(255),
    phone NVARCHAR(11) unique not null,
    address NVARCHAR(255),
    role INT DEFAULT 0,  -- 0: User, 1: Admin
    status INT DEFAULT 1  -- 1: Active, 0: Inactive
);
go
-- Tạo bảng orders (Đơn hàng)
CREATE TABLE orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
	address NVARCHAR(max),
	phone NVARCHAR(11),
	fullname NVARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
go
-- Tạo bảng order_detail (Chi tiết đơn hàng)
CREATE TABLE order_detail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    qty INT NOT NULL,
	price float not null,
    order_date date NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ,
    FOREIGN KEY (product_id) REFERENCES products(id)  on update no action
)
go

CREATE TABLE cart (
    id INT IDENTITY(1,1) PRIMARY KEY,
	user_id INT NOT NULL,
    product_id INT NOT NULL,
    qty INT NOT NULL,
	price float not null,
	FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)  on update no action,
);
go

INSERT INTO users (username, password, email, full_name, phone, address, role, status) VALUES
(N'khanghp', N'MTIzNDU2', N'khanghp@gmail.com', N'Huỳnh Phúc Khang', N'0123456789', N'123 Thanh Niên, Ngô Mây, Quy Nhơn', 0, 1),
(N'admin', N'MTIzNDU2', N'admin@gmail.com', N'Admin User', N'0987654321', N'456 Admin, Quy Nhơn', 1, 1),
(N'tientm', N'MTIzNDU2', N'tientm@gmail.com', N'Trương Minh Tiến', N'0123789889', N'123 Xuân Lê, Nhơn Bình, Quy Nhơn', 0, 1),
(N'lyntb', N'MTIzNDU2', N'lyntb@gmail.com', N'Nguyễn Thị Bảo Ly', N'0123227892', N'12 Bùi Xuân Phái, Hải Cảng, Quy Nhơn', 0, 1),
(N'nhihu', N'MTIzNDU2', N'nhihu@gmail.com', N'Hồ Uyển Nhi', N'01244456789', N'123 Thanh Niên, Hoài Nhơn', 0, 1),
(N'nhitty', N'MTIzNDU2', N'nhitty@gmail.com', N'Trần Thị Yến Nhi', N'0123456339', N'123 Thanh Niên, Tuy Phước', 0, 1);
go

INSERT INTO categories (name, status) VALUES
(N'Versace', 1),
( N'Julius', 1),
(N'Gucci', 1),
( N'Orient', 1),
( N'Burberry', 1),
( N'Knot', 1),
( N'Citizen', 1),
( N'Casio', 1),
( N'Daniel Wellington', 1),
( N'SevenFriday', 1);
GO
INSERT INTO sub_categories VALUES
(1, N'Đồng hồ nam', 1),
(2, N'Đồng hồ nam', 1),
(3, N'Đồng hồ nam', 1),
(4, N'Đồng hồ nam', 1),
(5, N'Đồng hồ nam', 1),
(6, N'Đồng hồ nam', 1),
(7, N'Đồng hồ nam', 1),
(8, N'Đồng hồ nam', 1),
(9, N'Đồng hồ nam', 1),
(10, N'Đồng hồ nam', 1),
(1, N'Đồng hồ nữ', 1),
(2, N'Đồng hồ nữ', 1),
(3, N'Đồng hồ nữ', 1),
(4, N'Đồng hồ nữ', 1),
(5, N'Đồng hồ nữ', 1),
(6, N'Đồng hồ nữ', 1),
(7, N'Đồng hồ nữ', 1),
(8, N'Đồng hồ nữ', 1),
(9, N'Đồng hồ nữ', 1),
(10, N'Đồng hồ nữ', 1);
GO


INSERT INTO products(id_subcategories, name, image, price, qty, description, status) VALUES
-- Đồng hồ Versace dành cho nam (id_subcategories = 1)
(1, N'Versace V-Race GMT', N'versace_v_race_gmt.jpg', 5000000,  10, N'Versace V-Race GMT, thiết kế mạnh mẽ, phong cách nam tính.', 1),
(1, N'Versace Urban Chrono', N'versace_urban_chrono.jpg', 6500000,  8, N'Versace Urban Chrono, đồng hồ đa chức năng với thiết kế hiện đại.', 1),
(1, N'Versace V-Palazzo Empire', N'versace_v_palazzo_empire.jpg', 8500000,  5, N'Versace V-Palazzo Empire, biểu tượng của sự sang trọng.', 1),
(1, N'Versace Mystique Sport', N'versace_mystique_sport.jpg', 7200000,  7, N'Versace Mystique Sport, phong cách thể thao dành cho phái mạnh.', 1),
(1, N'Versace Hellenyium', N'versace_hellenyium.jpg', 5500000,  12, N'Versace Hellenyium, thiết kế thanh lịch cho các quý ông.', 1),
(1, N'Versace Aion', N'versace_aion.jpg', 7800000,  6, N'Versace Aion, phong cách cổ điển kết hợp hiện đại.', 1),
(1, N'Versace Dylos Icon', N'versace_dylos_icon.jpg', 9900000,  4, N'Versace Dylos Icon, đồng hồ cao cấp cho phái mạnh.', 1),
(1, N'Versace Greca Chrono', N'versace_greca_chrono.jpg', 8200000,  9, N'Versace Greca Chrono, thiết kế độc đáo và táo bạo.', 1),
(1, N'Versace Chrono Active', N'versace_chrono_active.jpg', 8800000,  6, N'Versace Chrono Active, đồng hồ dành cho các quý ông năng động.', 1),
(1, N'Versace Virtus', N'versace_virtus.jpg', 7600000,  7, N'Versace Virtus, đồng hồ cao cấp với thiết kế tinh tế.', 1),
-- Đồng hồ Versace dành cho nữ (id_subcategories = 11)
(11, N'Versace V-Motif', N'versace_v_motif.jpg', 5200000,  10, N'Versace V-Motif, thiết kế thanh lịch cho phái đẹp.', 1),
(11, N'Versace Virtus Infinity', N'versace_virtus_infinity.jpg', 7500000, 8, N'Versace Virtus Infinity, phong cách hiện đại và sang trọng.', 1),
(11, N'Versace Palazzo Empire', N'versace_palazzo_empire.jpg', 8700000,  5, N'Versace Palazzo Empire, biểu tượng của sự quý phái.', 1),
(11, N'Versace Greca Logo', N'versace_greca_logo.jpg', 7200000, 7, N'Versace Greca Logo, thiết kế hiện đại với họa tiết đặc trưng.', 1),
(11, N'Versace Medusa Frame', N'versace_medusa_frame.jpg', 6200000,  9, N'Versace Medusa Frame, biểu tượng Medusa sang trọng.', 1),
(11, N'Versace Aion Lady', N'versace_aion_lady.jpg', 7800000,  6, N'Versace Aion Lady, phong cách cổ điển dành cho nữ.', 1),
(11, N'Versace Mystique Foulard', N'versace_mystique_foulard.jpg', 8200000,  4, N'Versace Mystique Foulard, đồng hồ cao cấp với thiết kế tinh xảo.', 1),
(11, N'Versace Urban Lady', N'versace_urban_lady.jpg', 6800000,  11, N'Versace Urban Lady, dành cho phái đẹp năng động.', 1),
(11, N'Versace Hellenyium Lady', N'versace_hellenyium_lady.jpg', 8400000,  6, N'Versace Hellenyium Lady, biểu tượng của sự thanh lịch.', 1),
(11, N'Versace V-Race Lady', N'versace_v_race_lady.jpg', 7900000,  7, N'Versace V-Race Lady, phong cách hiện đại cho phụ nữ.', 1),


insert into discounts(discount_value,discount_name,start_date,end_date,status) VALUES ('20', 'Lễ 10-3', '2025-03-26', '2025-03-31', '1');
INSERT INTO discount_details(discount_id, product_id, categories_id, subcategories_id, status) VALUES ('1', '157', NULL, NULL, '1');
INSERT INTO discount_details(discount_id, product_id, categories_id, subcategories_id, status) VALUES ('1', '186', NULL, NULL, '1');
INSERT INTO discount_details(discount_id, product_id, categories_id, subcategories_id, status) VALUES ('1', '146', NULL, NULL, '1');

CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(MAX),
    status INT DEFAULT 1 -- 1: Active, 0: Inactive
);
GO
CREATE TABLE sub_categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    id_categories INT NOT NULL,
    sub_categories_name NVARCHAR(100) NOT NULL,
    status INT DEFAULT 1,
    FOREIGN KEY (id_categories) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO categories (name, status) VALUES
(N'Versace', 1),
( N'Julius', 1),
(N'Gucci', 1),
( N'Orient', 1),
( N'Burberry', 1),
( N'Knot', 1),
( N'Citizen', 1),
( N'Casio', 1),
( N'Daniel Wellington', 1),
( N'SevenFriday', 1);
GO
INSERT INTO sub_categories VALUES
(1, N'Đồng hồ nam', 1),
(2, N'Đồng hồ nam', 1),
(3, N'Đồng hồ nam', 1),
(4, N'Đồng hồ nam', 1),
(5, N'Đồng hồ nam', 1),
(6, N'Đồng hồ nam', 1),
(7, N'Đồng hồ nam', 1),
(8, N'Đồng hồ nam', 1),
(9, N'Đồng hồ nam', 1),
(10, N'Đồng hồ nam', 1),
(1, N'Đồng hồ nữ', 1),
(2, N'Đồng hồ nữ', 1),
(3, N'Đồng hồ nữ', 1),
(4, N'Đồng hồ nữ', 1),
(5, N'Đồng hồ nữ', 1),
(6, N'Đồng hồ nữ', 1),
(7, N'Đồng hồ nữ', 1),
(8, N'Đồng hồ nữ', 1),
(9, N'Đồng hồ nữ', 1),
(10, N'Đồng hồ nữ', 1);

