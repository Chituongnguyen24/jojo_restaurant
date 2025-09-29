-- Script tạo database JOJO Restaurant và sinh dữ liệu mẫu cho kiểm thử nghiệp vụ
USE [master]
GO
/****** Object:  Database [PTUD-JOJO-Restaurant]    Script Date: 2025-09-29 12:00:00 AM ******/
CREATE DATABASE [PTUD-JOJO-Restaurant]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'PTUD-JOJO-Restaurant', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\PTUD-JOJO-Restaurant.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'PTUD-JOJO-Restaurant_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\PTUD-JOJO-Restaurant_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO

USE [PTUD-JOJO-Restaurant]
GO

-- Tạo các bảng dữ liệu
CREATE TABLE [dbo].[KHUVUC](
	[maKhuVuc] [nchar](10) NOT NULL,
	[tenKhuVuc] [varchar](50) NULL,
	[moTa] [varchar](max) NULL,
	[trangThai] [bit] NULL,
 CONSTRAINT [PK_KHUVUC] PRIMARY KEY CLUSTERED ([maKhuVuc] ASC)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[BAN](
	[maBan] [nchar](10) NOT NULL,
	[soCho] [int] NULL,
	[makhuVuc] [nchar](10) NULL,
	[trangThai] [bit] NULL,
 CONSTRAINT [PK_BAN] PRIMARY KEY CLUSTERED ([maBan] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[MONAN](
	[maMonAn] [nchar](10) NOT NULL,
	[tenMonAn] [nvarchar](50) NULL,
	[donGia] [int] NULL,
	[trangThai] [bit] NULL,
 CONSTRAINT [PK_MONAN] PRIMARY KEY CLUSTERED ([maMonAn] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[KHACHHANG](
	[maKhachHang] [nchar](10) NOT NULL,
	[tenKhachHang] [nvarchar](50) NULL,
	[sdt] [nchar](10) NULL,
	[email] [nvarchar](50) NULL,
	[diemTichLuy] [int] NULL,
	[laThanhVien] [bit] NULL,
 CONSTRAINT [PK_KHACHHANG] PRIMARY KEY CLUSTERED ([maKhachHang] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[NHANVIEN](
	[maNV] [nchar](7) NOT NULL,
	[tenNhanVien] [nvarchar](50) NULL,
	[chucVu] [varchar](20) NULL,
	[gioiTinh] [bit] NULL,
	[sdt] [nchar](10) NULL,
	[email] [varchar](50) NULL,
 CONSTRAINT [PK_NHANVIEN] PRIMARY KEY CLUSTERED ([maNV] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TAIKHOAN](
	[tenDangNhap] [nchar](10) NULL,
	[maNV] [nchar](7) NOT NULL,
	[matKhau] [nvarchar](50) NULL,
	[vaiTro] [nchar](10) NULL,
 CONSTRAINT [PK_TAIKHOAN] PRIMARY KEY CLUSTERED ([maNV] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[THUE](
	[maSoThue] [nchar](10) NOT NULL,
	[tenThue] [nchar](10) NULL,
	[tyLeThue] [float] NULL,
 CONSTRAINT [PK_THUE] PRIMARY KEY CLUSTERED ([maSoThue] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[KHUYENMAI](
	[maKhuyenMai] [nchar](10) NOT NULL,
	[giaTri] [float] NULL,
	[thoiGianBatDau] [date] NULL,
	[thoiGianKetThuc] [date] NULL,
 CONSTRAINT [PK_KHUYENMAI] PRIMARY KEY CLUSTERED ([maKhuyenMai] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[PHIEUDATBAN](
	[maPhieu] [nchar](10) NOT NULL,
	[thoiGianDat] [datetime] NULL,
	[maKhachHang] [nchar](10) NOT NULL,
	[maNV] [nchar](7) NULL,
	[maBan] [nchar](10) NOT NULL,
 CONSTRAINT [PK_PHIEUDATBAN] PRIMARY KEY CLUSTERED ([maPhieu] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[CHITIETPHIEUDATBAN](
	[soLuongMonAn] [int] NULL,
	[ghiChu] [nvarchar](max) NULL,
	[maMonAn] [nchar](10) NOT NULL,
	[maPhieu] [nchar](10) NOT NULL,
 CONSTRAINT [PK_CHITIETPHIEUDATBAN] PRIMARY KEY CLUSTERED ([maMonAn] ASC, [maPhieu] ASC)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[HOADON](
	[maHoaDon] [nchar](10) NOT NULL,
	[maKhachHang] [nchar](10) NULL,
	[ngayLap] [date] NULL,
	[phuongThuc] [varchar](50) NULL,
	[maKhuyenMai] [nchar](10) NULL,
	[maThue] [nchar](10) NULL,
	[gioVao] [datetime] NULL,
	[gioRa] [datetime] NULL,
	[maNhanVien] [nchar](7) NULL,
	[maPhieu] [nchar](10) NULL,
 CONSTRAINT [PK_HOADON] PRIMARY KEY CLUSTERED ([maHoaDon] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[CHITIETHOADON](
	[maHoaDon] [nchar](10) NOT NULL,
	[maMonAn] [nchar](10) NOT NULL,
	[soLuong] [int] NULL,
	[donGia] [float] NULL,
 CONSTRAINT [PK_CHITIETHOADON_1] PRIMARY KEY CLUSTERED ([maHoaDon] ASC, [maMonAn] ASC)
) ON [PRIMARY]
GO

-- Tạo các khóa ngoại
ALTER TABLE [dbo].[BAN]  WITH CHECK ADD  CONSTRAINT [FK_BAN_KHUVUC] FOREIGN KEY([makhuVuc])
REFERENCES [dbo].[KHUVUC] ([maKhuVuc])
GO

ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO

ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_PHIEUDATBAN] FOREIGN KEY([maPhieu])
REFERENCES [dbo].[PHIEUDATBAN] ([maPhieu])
GO

ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_HOADON] FOREIGN KEY([maHoaDon])
REFERENCES [dbo].[HOADON] ([maHoaDon])
GO

ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHUYENMAI] FOREIGN KEY([maKhuyenMai])
REFERENCES [dbo].[KHUYENMAI] ([maKhuyenMai])
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_NHANVIEN] FOREIGN KEY([maNhanVien])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_THUE] FOREIGN KEY([maThue])
REFERENCES [dbo].[THUE] ([maSoThue])
GO

ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_BAN] FOREIGN KEY([maBan])
REFERENCES [dbo].[BAN] ([maBan])
GO

ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO

ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO

ALTER TABLE [dbo].[TAIKHOAN]  WITH CHECK ADD  CONSTRAINT [FK_TAIKHOAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO

-- Dữ liệu mẫu cho các bảng
-- 1. Khu vực
INSERT INTO KHUVUC(maKhuVuc, tenKhuVuc, moTa, trangThai) VALUES
('KV001', N'Tầng trệt', N'Bàn máy lạnh, đông khách gia đình, nhóm nhỏ', 1),
('KV002', N'VIP', N'Phòng riêng sang trọng, từ 8-12 khách', 1),
('KV003', N'Tầng 2', N'Bàn máy lạnh, yên tĩnh', 1),
('KV004', N'Sân thượng', N'Bàn ngoài trời, thoáng mát, view đẹp', 1),
('KV005', N'Sân vườn', N'Bàn giữa cây xanh, trải nghiệm thư giãn', 1);

-- 2. Bàn ăn
INSERT INTO BAN(maBan, soCho, makhuVuc, trangThai) VALUES
('B001', 6, 'KV001', 1),
('B002', 6, 'KV001', 1),
('B003', 6, 'KV001', 0),
('B004', 6, 'KV001', 1),
('B005', 6, 'KV001', 1),
('B006', 6, 'KV001', 1),
('B007', 10, 'KV002', 1),
('B008', 12, 'KV002', 1),
('B009', 8, 'KV002', 1),
('B010', 6, 'KV003', 1),
('B011', 6, 'KV003', 1),
('B012', 6, 'KV003', 0),
('B013', 6, 'KV003', 1),
('B014', 6, 'KV003', 1),
('B015', 6, 'KV003', 1),
('B016', 4, 'KV004', 1),
('B017', 4, 'KV004', 1),
('B018', 4, 'KV004', 1),
('B019', 4, 'KV004', 1),
('B020', 6, 'KV005', 1),
('B021', 6, 'KV005', 1),
('B022', 6, 'KV005', 1);

-- 3. Nhân viên
INSERT INTO NHANVIEN(maNV, tenNhanVien, chucVu, gioiTinh, sdt, email) VALUES
('NV0001', N'Nguyễn Lâm Chí Tường', 'QuanLy', 1, '0911111111', 'tuong@jojo.com'),
('NV0002', N'Phan Minh Khang', 'LeTan', 1, '0922222222', 'khang@jojo.com'),
('NV0003', N'Chìu Kim Thi', 'ThuNgan', 0, '0933333333', 'thi@jojo.com'),
('NV0004', N'Lê Văn Phục', 'PhucVu', 1, '0944444444', 'phuc@jojo.com'),
('NV0005', N'Trần Thị Nga', 'PhucVu', 0, '0955555555', 'nga@jojo.com');

-- 4. Tài khoản
INSERT INTO TAIKHOAN(tenDangNhap, maNV, matKhau, vaiTro) VALUES
('tuong', 'NV0001', N'123456', 'QuanLy'),
('khang', 'NV0002', N'123456', 'LeTan'),
('thi', 'NV0003', N'123456', 'ThuNgan'),
('phuc', 'NV0004', N'123456', 'PhucVu'),
('nga', 'NV0005', N'123456', 'PhucVu');

-- 5. Khách hàng
INSERT INTO KHACHHANG(maKhachHang, tenKhachHang, sdt, email, diemTichLuy, laThanhVien) VALUES
('KH0001', N'Nguyễn Văn A', '0968888888', 'vana@gmail.com', 120, 1),
('KH0002', N'Trần Thị B', '0977777777', 'thib@gmail.com', 350, 1),
('KH0003', N'Lê Văn C', '0986666666', 'vanc@gmail.com', 500, 1),
('KH0004', N'Phạm Thị D', '0965555555', 'thid@gmail.com', 80, 0),
('KH0005', N'Ngô Văn E', '0964444444', 'vane@gmail.com', 0, 0);

-- 6. Thuế
INSERT INTO THUE(maSoThue, tenThue, tyLeThue) VALUES
('T0001', N'VAT', 0.08);

-- 7. Khuyến mãi
INSERT INTO KHUYENMAI(maKhuyenMai, giaTri, thoiGianBatDau, thoiGianKetThuc) VALUES
('KM0001', 0.05, '2025-09-01', '2025-09-30'),
('KM0002', 0.10, '2025-10-01', '2025-10-31');

-- 8. Món ăn
INSERT INTO MONAN(maMonAn, tenMonAn, donGia, trangThai) VALUES
('MA001', N'Lẩu Thái chua cay', 350000, 1),
('MA002', N'Lẩu riêu cua đồng', 320000, 1),
('MA003', N'Lẩu kim chi', 340000, 1),
('MA004', N'Lẩu nấm', 330000, 1),
('MA005', N'Ba chỉ heo nướng', 120000, 1),
('MA006', N'Sườn bò nướng', 180000, 1),
('MA007', N'Gà ướp sate', 110000, 1),
('MA008', N'Tôm nướng', 160000, 1),
('MA009', N'Mực nướng', 145000, 1),
('MA010', N'Bạch tuộc nướng', 155000, 1),
('MA011', N'Cá kèo nướng', 135000, 1),
('MA012', N'Cơm chiên', 70000, 1),
('MA013', N'Mì xào', 80000, 1),
('MA014', N'Salad', 60000, 1),
('MA015', N'Soup', 58000, 1),
('MA016', N'Trái cây', 50000, 1),
('MA017', N'Bánh ngọt', 55000, 1);

-- 9. Phiếu đặt bàn
INSERT INTO PHIEUDATBAN(maPhieu, thoiGianDat, maKhachHang, maNV, maBan) VALUES
('PD0001', '2025-10-01 18:00:00', 'KH0001', 'NV0002', 'B003'),
('PD0002', '2025-10-02 19:00:00', 'KH0002', 'NV0002', 'B008'),
('PD0003', '2025-10-03 18:30:00', 'KH0003', 'NV0002', 'B017'),
('PD0004', '2025-10-04 17:30:00', 'KH0004', 'NV0002', 'B021');

-- 10. Chi tiết phiếu đặt bàn (khách đặt món trước)
INSERT INTO CHITIETPHIEUDATBAN(soLuongMonAn, ghiChu, maMonAn, maPhieu) VALUES
(1, N'Ít cay', 'MA001', 'PD0001'),
(1, N'Không cay', 'MA002', 'PD0002'),
(2, N'Cho trẻ em', 'MA014', 'PD0002'),
(1, N'Thêm bạch tuộc', 'MA010', 'PD0003');

-- 11. Hóa đơn
INSERT INTO HOADON(maHoaDon, maKhachHang, ngayLap, phuongThuc, maKhuyenMai, maThue, gioVao, gioRa, maNhanVien, maPhieu) VALUES
('HD0001', 'KH0001', '2025-10-01', 'TienMat', 'KM0001', 'T0001', '2025-10-01 18:10:00', '2025-10-01 20:00:00', 'NV0003', 'PD0001'),
('HD0002', 'KH0002', '2025-10-02', 'ChuyenKhoan', 'KM0002', 'T0001', '2025-10-02 19:05:00', '2025-10-02 21:00:00', 'NV0003', 'PD0002');

-- 12. Chi tiết hóa đơn
INSERT INTO CHITIETHOADON(maHoaDon, maMonAn, soLuong, donGia) VALUES
('HD0001', 'MA001', 1, 350000),
('HD0001', 'MA005', 2, 120000),
('HD0001', 'MA012', 1, 70000),
('HD0002', 'MA002', 1, 320000),
('HD0002', 'MA006', 2, 180000),
('HD0002', 'MA013', 1, 80000);

-- Kết thúc script tạo DB và sinh dữ liệu mẫu cho JOJO Restaurant