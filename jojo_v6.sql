/*
-- =================================================================================
-- PHẦN 1: TẠO VÀ CẤU HÌNH CƠ SỞ DỮ LIỆU
-- =================================================================================
*/
USE [master]
GO
DROP DATABASE IF EXISTS [PTUD-JOJO-Restaurant]
GO
CREATE DATABASE [PTUD-JOJO-Restaurant]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'PTUD-JOJO-Restaurant', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\PTUD-JOJO-Restaurant.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'PTUD-JOJO-Restaurant_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\PTUD-JOJO-Restaurant_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [PTUD-JOJO-Restaurant].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ARITHABORT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  ENABLE_BROKER 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  MULTI_USER 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET DB_CHAINING OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET QUERY_STORE = ON
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  READ_WRITE 
GO

/*
-- =================================================================================
-- PHẦN 2: TẠO CÁC BẢNG
-- =================================================================================
*/
USE [PTUD-JOJO-Restaurant]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[KHUVUC](
	[maKhuVuc] [nchar](10) NOT NULL,
	[tenKhuVuc] [nvarchar](50) NOT NULL,
	[moTa] [nvarchar](max) NULL,
	[trangThai] [bit] NOT NULL,
 CONSTRAINT [PK_KHUVUC] PRIMARY KEY CLUSTERED ([maKhuVuc] ASC)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[BAN](
	[maBan] [nchar](10) NOT NULL,
	[soCho] [int] NOT NULL,
	[maKhuVuc] [nchar](10) NOT NULL,
	[loaiBan] [nvarchar](20) NOT NULL,
	[trangThai] [nvarchar](20) NOT NULL, 
 CONSTRAINT [PK_BAN] PRIMARY KEY CLUSTERED ([maBan] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[MONAN](
	[maMonAn] [nchar](10) NOT NULL,
	[tenMonAn] [nvarchar](50) NOT NULL,
	[donGia] [money] NOT NULL,
	[trangThai] [bit] NOT NULL,
	[imagePath] [nvarchar](max) NULL,
 CONSTRAINT [PK_MONAN] PRIMARY KEY CLUSTERED ([maMonAn] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[NHANVIEN](
	[maNV] [nchar](7) NOT NULL,
	[tenNhanVien] [nvarchar](50) NOT NULL,
	[gioiTinh] [bit] NOT NULL,
	[ngaySinh] [date] NOT NULL,
	[sdt] [nchar](10) NOT NULL,
	[email] [nvarchar](50) NULL,
 CONSTRAINT [PK_NHANVIEN] PRIMARY KEY CLUSTERED ([maNV] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[TAIKHOAN](
	[maNV] [nchar](7) NOT NULL,
	[tenDangNhap] [nchar](10) UNIQUE NOT NULL,
	[matKhau] [nvarchar](50) NOT NULL, 
	[vaiTro] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_TAIKHOAN] PRIMARY KEY CLUSTERED ([maNV] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[KHACHHANG](
	[maKhachHang] [nchar](10) NOT NULL,
	[tenKhachHang] [nvarchar](50) NOT NULL,
	[sdt] [nchar](10) NOT NULL,
	[email] [nvarchar](50) NULL,
	[ngaySinh] [date] NULL,
	[diemTichLuy] [int] NOT NULL,
	[laThanhVien] [bit] NOT NULL,
 CONSTRAINT [PK_KHACHHANG] PRIMARY KEY CLUSTERED ([maKhachHang] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[PHIEUDATBAN](
	[maPhieu] [nchar](10) NOT NULL,
	[thoiGianDat] [datetime] NOT NULL,
	[maKhachHang] [nchar](10) NOT NULL,
	[maNV] [nchar](7) NOT NULL,
	[maBan] [nchar](10) NOT NULL,
	[soNguoi] [int] NOT NULL,
	[tienCoc] [money] NULL,
	[ghiChu] [nvarchar](max) NULL,
 CONSTRAINT [PK_PHIEUDATBAN] PRIMARY KEY CLUSTERED ([maPhieu] ASC)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[CHITIETPHIEUDATBAN](
	[maMonAn] [nchar](10) NOT NULL,
	[maPhieu] [nchar](10) NOT NULL,
	[soLuongMonAn] [int] NOT NULL,
	[ghiChu] [nvarchar](max) NULL,
 CONSTRAINT [PK_CHITIETPHIEUDATBAN] PRIMARY KEY CLUSTERED ([maMonAn] ASC,	[maPhieu] ASC)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

CREATE TABLE [dbo].[KHUYENMAI](
	[maKhuyenMai] [nchar](10) NOT NULL,
	[tenKhuyenMai] [nvarchar](50) NULL,
	[giaTri] [float] NULL,
	[thoiGianBatDau] [date] NOT NULL,
	[thoiGianKetThuc] [date] NOT NULL,
 CONSTRAINT [PK_KHUYENMAI] PRIMARY KEY CLUSTERED ([maKhuyenMai] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[THUE](
	[maSoThue] [nchar](10) NOT NULL,
	[tenThue] [nvarchar](50) NOT NULL,
	[tyLeThue] [float] NOT NULL,
	[moTa] [nvarchar](max) NULL,
	[trangThai] [bit] NOT NULL,
 CONSTRAINT [PK_THUE] PRIMARY KEY CLUSTERED ([maSoThue] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[HOADON](
	[maHoaDon] [nchar](10) NOT NULL,
	[maKhachHang] [nchar](10) NOT NULL,
	[maBan] [nchar](10) NOT NULL, 
	[ngayLap] [date] NOT NULL,
	[phuongThuc] [nvarchar](50) NOT NULL,
	[maKhuyenMai] [nchar](10) NULL,
	[maThue] [nchar](10) NOT NULL,
	[gioVao] [datetime] NOT NULL,
	[gioRa] [datetime] NOT NULL,
	[maNhanVien] [nchar](7) NOT NULL,
	[maPhieu] [nchar](10) NULL,
	[daThanhToan] [bit] NOT NULL,
 CONSTRAINT [PK_HOADON] PRIMARY KEY CLUSTERED ([maHoaDon] ASC)
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[CHITIETHOADON](
	[maHoaDon] [nchar](10) NOT NULL,
	[maMonAn] [nchar](10) NOT NULL,
	[soLuong] [int] NOT NULL,
	[donGia] [money] NOT NULL,
 CONSTRAINT [PK_CHITIETHOADON_1] PRIMARY KEY CLUSTERED ([maHoaDon] ASC, [maMonAn] ASC)
) ON [PRIMARY]
GO

/*
-- =================================================================================
-- PHẦN 3: TẠO RÀNG BUỘC KHÓA NGOẠI
-- =================================================================================
*/
ALTER TABLE [dbo].[BAN]  WITH CHECK ADD  CONSTRAINT [FK_BAN_KHUVUC] FOREIGN KEY([maKhuVuc])
REFERENCES [dbo].[KHUVUC] ([maKhuVuc])
GO
ALTER TABLE [dbo].[BAN] CHECK CONSTRAINT [FK_BAN_KHUVUC]
GO

ALTER TABLE [dbo].[TAIKHOAN]  WITH CHECK ADD  CONSTRAINT [FK_TAIKHOAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[TAIKHOAN] CHECK CONSTRAINT [FK_TAIKHOAN_NHANVIEN]
GO

ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_BAN] FOREIGN KEY([maBan])
REFERENCES [dbo].[BAN] ([maBan])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_BAN]
GO
ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_KHACHHANG]
GO
ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_NHANVIEN]
GO

ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN] CHECK CONSTRAINT [FK_CHITIETPHIEUDATBAN_MONAN]
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_PHIEUDATBAN] FOREIGN KEY([maPhieu])
REFERENCES [dbo].[PHIEUDATBAN] ([maPhieu])
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN] CHECK CONSTRAINT [FK_CHITIETPHIEUDATBAN_PHIEUDATBAN]
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_KHACHHANG]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHUYENMAI] FOREIGN KEY([maKhuyenMai])
REFERENCES [dbo].[KHUYENMAI] ([maKhuyenMai])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_KHUYENMAI]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_NHANVIEN] FOREIGN KEY([maNhanVien])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_NHANVIEN]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_THUE] FOREIGN KEY([maThue])
REFERENCES [dbo].[THUE] ([maSoThue])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_THUE]
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_BAN] FOREIGN KEY([maBan])
REFERENCES [dbo].[BAN] ([maBan])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_BAN]
GO

ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_HOADON] FOREIGN KEY([maHoaDon])
REFERENCES [dbo].[HOADON] ([maHoaDon])
GO
ALTER TABLE [dbo].[CHITIETHOADON] CHECK CONSTRAINT [FK_CHITIETHOADON_HOADON]
GO
ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO
ALTER TABLE [dbo].[CHITIETHOADON] CHECK CONSTRAINT [FK_CHITIETHOADON_MONAN]
GO

/*
-- =================================================================================
-- PHẦN 4: CHÈN DỮ LIỆU MASTER (DỮ LIỆU NGUỒN)
-- =================================================================================
*/

INSERT INTO [dbo].[KHUVUC] ([maKhuVuc], [tenKhuVuc], [moTa], [trangThai]) VALUES
(N'TRET', N'Tầng trệt', N'Khu vực máy lạnh, gần quầy lễ tân', 1),
(N'TANG2', N'Tầng 2', N'Khu vực máy lạnh, yên tĩnh', 1),
(N'VIP', N'Phòng VIP', N'Phòng riêng tư, sang trọng', 1),
(N'SANTHUONG', N'Sân thượng', N'Không gian mở, thoáng đãng', 1),
(N'SANVUON', N'Sân vườn', N'Không gian xanh, gần gũi thiên nhiên', 1)
GO

INSERT INTO [dbo].[BAN] ([maBan], [soCho], [maKhuVuc], [loaiBan], [trangThai]) VALUES
(N'B01', 4, N'TRET', N'THUONG', N'Trống'),
(N'B02', 4, N'TRET', N'THUONG', N'Trống'),
(N'B03', 6, N'TRET', N'THUONG', N'Trống'),
(N'B04', 2, N'TRET', N'THUONG', N'Trống'),
(N'B11', 4, N'TANG2', N'THUONG', N'Trống'),
(N'B12', 6, N'TANG2', N'THUONG', N'Trống'),
(N'B13', 8, N'TANG2', N'THUONG', N'Trống'),
(N'B14', 2, N'TANG2', N'THUONG', N'Trống'),
(N'B15', 4, N'TANG2', N'THUONG', N'Trống'),
(N'ST01', 4, N'SANTHUONG', N'THUONG', N'Trống'),
(N'ST02', 6, N'SANTHUONG', N'THUONG', N'Trống'),
(N'ST03', 2, N'SANTHUONG', N'THUONG', N'Trống'),
(N'ST04', 4, N'SANTHUONG', N'THUONG', N'Trống'),
(N'SV01', 4, N'SANVUON', N'THUONG', N'Trống'),
(N'SV02', 6, N'SANVUON', N'THUONG', N'Trống'),
(N'SV03', 2, N'SANVUON', N'THUONG', N'Trống'),
(N'TANG206', 10, N'TANG2', N'THUONG', N'Trống'),
(N'TRET05', 8, N'TRET', N'THUONG', N'Trống'),
(N'VIP01', 10, N'VIP', N'VIP', N'Trống'),
(N'VIP02', 12, N'VIP', N'VIP', N'Trống'),
(N'VIP03', 15, N'VIP', N'VIP', N'Trống')
GO

INSERT INTO [dbo].[MONAN] ([maMonAn], [tenMonAn], [donGia], [trangThai], [imagePath]) VALUES
(N'MA0001', N'Lẩu Thái chua cay', 350000, 1, N'images/mon an/lau-thai-chua-cay.jpg'),
(N'MA0002', N'Lẩu riêu cua đồng', 320000, 1, N'images/mon an/lau-rieu-cua-16x9.jpg'),
(N'MA0003', N'Lẩu kim chi cay nồng', 330000, 1, N'images/mon an/lau-kim-chi.jpg'),
(N'MA0004', N'Lẩu nấm thanh mát', 300000, 1, N'images/mon an/lau-nam-chay-880.jpg'),
(N'MA0005', N'Ba chỉ heo nướng', 120000, 1, N'images/mon an/ba-chi-heo-nuong.jpg'),
(N'MA0006', N'Sườn bò Mỹ nướng', 180000, 1, N'images/mon an/de-suon-bo-nuong.png'),
(N'MA0007', N'Gà ướp sate nướng', 150000, 1, N'images/mon an/ga-uop-sate-nuong.jpg'),
(N'MA0008', N'Tôm nướng muối ớt', 160000, 1, N'images/mon an/tom-nuong-muoi-ot.png'),
(N'MA0009', N'Mực nướng sa tế', 140000, 1, N'images/mon an/muc-nuong-sate.jpg'),
(N'MA0010', N'Cơm chiên tỏi', 50000, 1, N'images/mon an/com-chien-toi.jpg'),
(N'MA0011', N'Mì xào hải sản', 85000, 1, N'images/mon an/mi-xao-hai-san.jpg'),
(N'MA0012', N'Salad dầu giấm', 60000, 1, N'images/mon an/salad-dau-giam.jpg'),
(N'MA0013', N'Súp cua', 45000, 1, N'images/mon an/sup-cua.jpg'),
(N'MA0014', N'Coca-Cola', 25000, 1, N'images/mon an/coca-cola.jpg'),
(N'MA0015', N'Bia Tiger', 30000, 0, N'images/mon an/bia-tiger.jpg')
GO

-- Bảng: NHANVIEN
INSERT INTO [dbo].[NHANVIEN] ([maNV], [tenNhanVien], [gioiTinh], [ngaySinh], [sdt], [email]) VALUES
(N'NV00001', N'Nguyễn Văn An', 1, '1990-05-15', N'0901234567', N'an.nguyen@jojo.com'),
(N'NV00002', N'Trần Thị Bích', 0, '1992-08-20', N'0902345678', N'bich.tran@jojo.com'),
(N'NV00003', N'Lê Minh Cường', 1, '1988-11-01', N'0903456789', N'cuong.le@jojo.com'),
(N'NV00004', N'Phạm Thuỳ Dung', 0, '1995-02-10', N'0904567890', N'dung.pham@jojo.com'),
(N'NV00005', N'Hoàng Tiến Hải', 1, '1985-07-30', N'0905678901', N'hai.hoang@jojo.com'),
(N'NV00006', N'Đỗ Gia Hân', 0, '2000-01-25', N'0906789012', N'han.do@jojo.com'),
(N'NV00007', N'Võ Quốc Khánh', 1, '1998-09-12', N'0907890123', N'khanh.vo@jojo.com'),
(N'NV00008', N'Bùi Thị Lan', 0, '1993-04-05', N'0908901234', N'lan.bui@jojo.com'),
(N'NV00009', N'Lý Hùng Mạnh', 1, '1997-03-18', N'0909012345', N'manh.ly@jojo.com'),
(N'NV00010', N'Trịnh Ngọc Oanh', 0, '1999-12-07', N'0912112233', N'oanh.trinh@jojo.com'),
(N'NV00011', N'Đinh Công Tráng', 1, '1991-06-22', N'0912345678', N'trang.dinh@jojo.com'),
(N'NV00012', N'Mai Anh Thư', 0, '1996-10-14', N'0987123456', N'thu.mai@jojo.com'),
(N'NV00013', N'Trần Hoàng Phúc', 1, '1994-08-08', N'0905556677', N'phuc.tran@jojo.com'),
(N'NV00014', N'Lê Thị Kiều', 0, '1998-05-03', N'0977889900', N'kieu.le@jojo.com'),
(N'NV00015', N'Giang A Pháo', 1, '1990-02-19', N'0966554433', N'phao.giang@jojo.com')
GO

-- Bảng: TAIKHOAN
INSERT INTO [dbo].[TAIKHOAN] ([maNV], [tenDangNhap], [matKhau], [vaiTro]) VALUES
(N'NV00001', N'annguyen', N'Admin@123', N'NVQL'),
(N'NV00002', N'bichtran', N'Nhanvien@1', N'NVTT'),
(N'NV00003', N'cuongle', N'Nhanvien@2', N'NVTT'),
(N'NV00004', N'dungpham', N'Nhanvien@3', N'NVTT'),
(N'NV00005', N'haihoang', N'Admin@456', N'NVQL'),
(N'NV00006', N'hando', N'Nhanvien@4', N'NVTT'),
(N'NV00007', N'khanhvo', N'Nhanvien@5', N'NVTT'),
(N'NV00008', N'lanbui', N'Nhanvien@6', N'NVTT'),
(N'NV00009', N'manhly', N'Nhanvien@7', N'NVTT'),
(N'NV00010', N'oanhtrinh', N'Nhanvien@8', N'NVTT'),
(N'NV00011', N'trangdinh', N'Nhanvien@9', N'NVTT'),
(N'NV00012', N'thumai', N'Nhanvien@10', N'NVTT'),
(N'NV00013', N'phuctran', N'Nhanvien@11', N'NVTT'),
(N'NV00014', N'kieule', N'Nhanvien@12', N'NVTT'),
(N'NV00015', N'phaogiang', N'Nhanvien@13', N'NVTT')
GO

INSERT INTO [dbo].[THUE] ([maSoThue], [tenThue], [tyLeThue], [moTa], [trangThai]) VALUES
('VAT10', N'Thuế GTGT 10%', 0.1, N'Thuế giá trị gia tăng 10%', 1)
GO

INSERT INTO [dbo].[KHUYENMAI] ([maKhuyenMai], [tenKhuyenMai], [giaTri], [thoiGianBatDau], [thoiGianKetThuc]) VALUES
('KM_SEP', N'Back to School', 0.05, '2025-09-01', '2025-09-10'),
('KM_NOV', N'Chào tháng 11', 0.1, '2025-11-01', '2025-11-30'),
('KM_XMAS', N'Giáng Sinh An Lành', 0.15, '2025-12-20', '2025-12-25'),
('KM_DONG', N'Ưu đãi thẻ Đồng', 0.05, '2025-01-01', '2026-12-31'),
('KM_BAC', N'Ưu đãi thẻ Bạc', 0.1, '2025-01-01', '2026-12-31'),
('KM_VANG', N'Ưu đãi thẻ Vàng', 0.15, '2025-01-01', '2026-12-31'),
('KM_SN_D', N'Sinh nhật Đồng', 0.1, '2025-01-01', '2026-12-31'),
('KM_SN_B', N'Sinh nhật Bạc', 0.15, '2025-01-01', '2026-12-31'),
('KM_SN_V', N'Sinh nhật Vàng', 0.2, '2025-01-01', '2026-12-31')
GO

INSERT INTO [dbo].[KHACHHANG] ([maKhachHang], [tenKhachHang], [sdt], [email], [diemTichLuy], [laThanhVien]) VALUES
(N'KH00000000', N'Khách vãng lai', N'0000000000', NULL, 0, 0),
(N'KH25000001', N'Trần Văn Nam', N'0987654321', N'nam.tran@email.com', 150, 1),
(N'KH25000002', N'Nguyễn Thị Hoa', N'0986543210', N'hoa.nguyen@email.com', 85, 1),
(N'KH25000003', N'Lê Văn Long', N'0975432109', N'long.le@email.com', 0, 0),
(N'KH25000004', N'Phạm Thị Mai', N'0914321098', N'mai.pham@email.com', 250, 1),
(N'KH25000005', N'Đặng Hoàng Quân', N'0933210987', N'quan.dang@email.com', 410, 1),
(N'KH25000006', N'Vũ Bích Thảo', N'0942109876', N'thao.vu@email.com', 520, 1),
(N'KH25000007', N'Hồ Minh Tâm', N'0981098765', N'tam.ho@email.com', 800, 1),
(N'KH25000008', N'Ngô Gia Bảo', N'0979876543', N'bao.ngo@email.com', 30, 1),
(N'KH25000009', N'Đinh Yến Nhi', N'0968765432', N'nhi.dinh@email.com', 0, 0),
(N'KH25000010', N'Lý Gia Hân', N'0918765432', N'han.ly@email.com', 480, 1),
(N'KH25000011', N'Nguyễn Hoàng Anh', N'0911223344', N'anh.nguyen@email.com', 120, 1),
(N'KH25000012', N'Trần Minh Tuấn', N'0922334455', N'tuan.tran@email.com', 45, 1),
(N'KH25000013', N'Lê Thị Mỹ Lệ', N'0933445566', N'le.le@email.com', 330, 1),
(N'KH25000014', N'Phạm Gia Huy', N'0944556677', N'huy.pham@email.com', 50, 1),
(N'KH25000015', N'Huỳnh Ngọc Trâm', N'0955667788', N'tram.huynh@email.com', 0, 0),
(N'KH25000016', N'Võ Thành Danh', N'0966778899', N'danh.vo@email.com', 95, 1),
(N'KH25000017', N'Đỗ Thị Bích Trâm', N'0977889900', N'tram.do@email.com', 180, 1),
(N'KH25000018', N'Ngô Thanh Phong', N'0988990011', N'phong.ngo@email.com', 200, 1),
(N'KH25000019', N'Lý Thảo Chi', N'0999001122', N'chi.ly@email.com', 0, 0),
(N'KH25000020', N'Bùi Anh Khoa', N'0912121212', N'khoa.bui@email.com', 600, 1),
(N'KH25000021', N'Hoàng Minh Nhật', N'0913141516', N'nhat.hoang@email.com', 220, 1),
(N'KH25000022', N'Vương Tuấn Khải', N'0917181920', N'khai.vuong@email.com', 0, 0),
(N'KH25000023', N'Trần Bảo An', N'0921222324', N'an.tran@email.com', 50, 1),
(N'KH25000024', N'Lê Phương Thảo', N'0925262728', N'thao.le@email.com', 780, 1),
(N'KH25000025', N'Đỗ Hùng Dũng', N'0929303132', N'dung.do@email.com', 110, 1),
(N'KH25000026', N'Nguyễn Quang Hải', N'0933343536', N'hai.nguyen@email.com', 0, 0),
(N'KH25000027', N'Phan Văn Đức', N'0937383940', N'duc.phan@email.com', 40, 1),
(N'KH25000028', N'Đặng Văn Lâm', N'0941424344', N'lam.dang@email.com', 900, 1),
(N'KH25000029', N'Bùi Tiến Dũng', N'0945464748', N'dung.bui@email.com', 0, 0),
(N'KH25000030', N'Đoàn Văn Hậu', N'0949505152', N'hau.doan@email.com', 300, 1),
(N'KH25000031', N'Hà Đức Chinh', N'0953545556', N'chinh.ha@email.com', 55, 1),
(N'KH25000032', N'Lương Xuân Trường', N'0957585960', N'truong.luong@email.com', 125, 1),
(N'KH25000033', N'Vũ Văn Thanh', N'0961626364', N'thanh.vu@email.com', 0, 0),
(N'KH25000034', N'Nguyễn Công Phượng', N'0965666768', N'phuong.nguyen@email.com', 70, 1),
(N'KH25000035', N'Nguyễn Văn Toàn', N'0969707172', N'toan.nguyen@email.com', 190, 1),
(N'KH25000036', N'Trịnh Văn Quyết', N'0915123456', N'quyet.trinh@email.com', 120, 1),
(N'KH25000037', N'Mai Phương Thúy', N'0988123456', N'thuy.mai@email.com', 450, 1),
(N'KH25000038', N'Nguyễn Xuân Phúc', N'0977123456', N'phuc.nguyen@email.com', 0, 0),
(N'KH25000039', N'Võ Văn Thưởng', N'0966123456', N'thuong.vo@email.com', 80, 1),
(N'KH25000040', N'Phạm Minh Chính', N'0955123456', N'chinh.pham@email.com', 600, 1),
(N'KH25000041', N'Vương Đình Huệ', N'0944123456', N'hue.vuong@email.com', 210, 1),
(N'KH25000042', N'Tô Lâm', N'0933123456', N'lam.to@email.com', 0, 0),
(N'KH25000043', N'Trương Thị Mai', N'0922123456', N'mai.truong@email.com', 330, 1),
(N'KH25000044', N'Phan Văn Giang', N'0911123456', N'giang.phan@email.com', 50, 1),
(N'KH25000045', N'Bùi Thị Minh Hoài', N'0989123456', N'hoai.bui@email.com', 0, 0),
(N'KH25000046', N'Trần Cẩm Tú', N'0978123456', N'tu.tran@email.com', 90, 1),
(N'KH25000047', N'Đỗ Văn Chiến', N'0967123456', N'chien.do@email.com', 150, 1),
(N'KH25000048', N'Nguyễn Hòa Bình', N'0956123456', N'binh.nguyen@email.com', 0, 0),
(N'KH25000049', N'Trần Thanh Mẫn', N'0945123456', N'man.tran@email.com', 400, 1),
(N'KH25000050', N'Nguyễn Văn Nên', N'0934123456', N'nen.nguyen@email.com', 720, 1)
GO

/*
-- =================================================================================
-- PHẦN 5: CHÈN DỮ LIỆU GIAO DỊCH (TỰ ĐỘNG 100 HÓA ĐƠN)
-- =H- =================================================================================
*/
SET NOCOUNT ON;
GO

DECLARE @Counter INT = 1;
DECLARE @TotalInvoices INT = 100;
DECLARE @TotalBookings INT = 80;
DECLARE @CurrentDate DATETIME;
DECLARE @GioVao DATETIME;
DECLARE @GioRa DATETIME;

DECLARE @MaHoaDon NCHAR(10);
DECLARE @MaPhieu NCHAR(10);
DECLARE @MaKhachHang NCHAR(10);
DECLARE @MaNhanVien NCHAR(7);
DECLARE @MaBan NCHAR(10);
DECLARE @MaKhuyenMai NCHAR(10);
DECLARE @PhuongThuc NVARCHAR(50);

DECLARE @CustomerBirthDay INT;
DECLARE @CustomerBirthMonth INT;
DECLARE @CustomerPoints INT;
DECLARE @CustomerTier NVARCHAR(10);

DECLARE @SoMon INT;
DECLARE @MonCounter INT;
DECLARE @MaMonAn NCHAR(10);
DECLARE @SoLuongMon INT;
DECLARE @DonGiaMon MONEY;

-- ==== BIẾN MỚI CHO CÁC YÊU CẦU ====
DECLARE @TienCoc MONEY;
DECLARE @GhiChuPhieu NVARCHAR(MAX);
DECLARE @SoMonDat INT;
DECLARE @MonDatCounter INT;
DECLARE @MaMonAnDat NCHAR(10);
DECLARE @SoLuongMonDat INT;
DECLARE @GhiChuMonDat NVARCHAR(MAX); -- Biến mới cho ghi chú món đặt
DECLARE @DaThanhToan BIT; -- Biến mới cho trạng thái thanh toán
-- ==================================

WHILE @Counter <= @TotalInvoices
BEGIN
    SET @MaHoaDon = 'HD' + RIGHT('000' + CAST(@Counter AS VARCHAR(3)), 3);
    
    SET @CurrentDate = DATEADD(DAY, CAST((RAND() * 150) AS INT), '2025-08-01');
    SET @GioVao = DATEADD(HOUR, 11 + CAST((RAND() * 9) AS INT), @CurrentDate);
    SET @GioVao = DATEADD(MINUTE, CAST((RAND() * 59) AS INT), @GioVao);
    SET @GioRa = DATEADD(MINUTE, 45 + CAST((RAND() * 120) AS INT), @GioVao);
    
    SELECT TOP 1 @MaKhachHang = maKhachHang, @CustomerPoints = diemTichLuy, 
                   @CustomerBirthDay = DAY(ngaySinh), @CustomerBirthMonth = MONTH(ngaySinh)
    FROM [dbo].[KHACHHANG] ORDER BY NEWID();

    SELECT TOP 1 @MaNhanVien = maNV FROM [dbo].[NHANVIEN] ORDER BY NEWID();
    
    SELECT TOP 1 @MaBan = maBan FROM [dbo].[BAN] ORDER BY NEWID();

    IF @CustomerPoints >= 450
        SET @CustomerTier = N'Vàng';
    ELSE IF @CustomerPoints >= 200
        SET @CustomerTier = N'Bạc';
    ELSE
        SET @CustomerTier = N'Đồng';

    SET @MaKhuyenMai = NULL;

    IF @CustomerBirthDay IS NOT NULL AND @CustomerBirthDay = DAY(@CurrentDate) AND @CustomerBirthMonth = MONTH(@CurrentDate)
    BEGIN
        IF @CustomerTier = N'Vàng' SET @MaKhuyenMai = 'KM_SN_V';
        ELSE IF @CustomerTier = N'Bạc' SET @MaKhuyenMai = 'KM_SN_B';
        ELSE IF @CustomerPoints > 0 SET @MaKhuyenMai = 'KM_SN_D';
    END
    ELSE IF MONTH(@CurrentDate) = 12 AND DAY(@CurrentDate) BETWEEN 20 AND 25
    BEGIN
        SET @MaKhuyenMai = 'KM_XMAS';
    END
    ELSE IF MONTH(@CurrentDate) = 11
    BEGIN
        SET @MaKhuyenMai = 'KM_NOV';
    END
    ELSE IF MONTH(@CurrentDate) = 9 AND DAY(@CurrentDate) BETWEEN 1 AND 10
    BEGIN
        SET @MaKhuyenMai = 'KM_SEP';
    END
    ELSE IF @CustomerPoints > 0
    BEGIN
        IF @CustomerTier = N'Vàng' SET @MaKhuyenMai = 'KM_VANG';
        ELSE IF @CustomerTier = N'Bạc' SET @MaKhuyenMai = 'KM_BAC';
        ELSE SET @MaKhuyenMai = 'KM_DONG';
    END;

    IF RAND() < 0.5 SET @PhuongThuc = N'Tiền mặt';
    ELSE IF RAND() < 0.8 SET @PhuongThuc = N'Thẻ';
    ELSE SET @PhuongThuc = N'Chuyển khoản';
    
    SET @MaPhieu = NULL;
    IF @Counter <= @TotalBookings
    BEGIN
        SET @MaPhieu = 'PD' + RIGHT('000' + CAST(@Counter AS VARCHAR(3)), 3);

		SET @TienCoc = 0;
		SET @GhiChuPhieu = NULL;
		
		IF RAND() < 0.3 -- 30% phiếu sẽ có cọc
		BEGIN
			SET @TienCoc = 100000;
		END

		IF RAND() < 0.2 -- 20% phiếu sẽ có ghi chú
		BEGIN
			SET @GhiChuPhieu = N'Khách dặn chuẩn bị ghế em bé';
		END

        INSERT INTO [dbo].[PHIEUDATBAN] 
            ([maPhieu], [thoiGianDat], [maKhachHang], [maNV], [maBan], [soNguoi], [tienCoc], [ghiChu])
        VALUES
            (@MaPhieu, DATEADD(HOUR, -2, @GioVao), @MaKhachHang, @MaNhanVien, @MaBan, 2 + CAST(RAND()*6 AS INT), @TienCoc, @GhiChuPhieu);

		SET @SoMonDat = CAST((RAND() * 2) + 1 AS INT); 
        SET @MonDatCounter = 0;

        WHILE @MonDatCounter < @SoMonDat
        BEGIN
            SELECT TOP 1 @MaMonAnDat = maMonAn 
            FROM [dbo].[MONAN] WHERE trangThai = 1 ORDER BY NEWID();
                    
            SET @SoLuongMonDat = CAST((RAND() * 1) + 1 AS INT);

			-- ==== CẬP NHẬT: RANDOM GHI CHÚ CHO MÓN ĐẶT ====
			SET @GhiChuMonDat = NULL;
			DECLARE @RandNote FLOAT = RAND();
			IF @RandNote < 0.1
				SET @GhiChuMonDat = N'Không hành';
			ELSE IF @RandNote < 0.15
				SET @GhiChuMonDat = N'Ít cay';
			ELSE IF @RandNote < 0.2
				SET @GhiChuMonDat = N'Cay nhiều';
			ELSE IF @RandNote < 0.25
				SET @GhiChuMonDat = N'Không tiêu';
			ELSE IF @RandNote < 0.3
				SET @GhiChuMonDat = N'Không nghệ';
			-- 70% còn lại sẽ là NULL (không có ghi chú)
			-- ============================================

            IF NOT EXISTS (SELECT 1 FROM [dbo].[CHITIETPHIEUDATBAN] WHERE maPhieu = @MaPhieu AND maMonAn = @MaMonAnDat)
            BEGIN
                INSERT INTO [dbo].[CHITIETPHIEUDATBAN]
                    ([maPhieu], [maMonAn], [soLuongMonAn], [ghiChu])
                VALUES
                    (@MaPhieu, @MaMonAnDat, @SoLuongMonDat, @GhiChuMonDat); -- Sử dụng biến @GhiChuMonDat
                        
                SET @MonDatCounter = @MonDatCounter + 1;
            END;
        END;
    END;
    
	-- ==== CẬP NHẬT: RANDOM TRẠNG THÁI THANH TOÁN ====
	IF RAND() < 0.8 
		SET @DaThanhToan = 1; -- 80% đã thanh toán
	ELSE 
		SET @DaThanhToan = 0; -- 20% chưa thanh toán
	-- ============================================

    INSERT INTO [dbo].[HOADON]
        ([maHoaDon], [maKhachHang], [maBan], [ngayLap], [phuongThuc], [maKhuyenMai], [maThue], [gioVao], [gioRa], [maNhanVien], [maPhieu], [daThanhToan])
    VALUES
        (@MaHoaDon, @MaKhachHang, @MaBan, CONVERT(DATE, @CurrentDate), @PhuongThuc, @MaKhuyenMai, 'VAT10', @GioVao, @GioRa, @MaNhanVien, @MaPhieu, @DaThanhToan); -- Sử dụng biến @DaThanhToan

    SET @SoMon = CAST((RAND() * 4) + 1 AS INT);
    SET @MonCounter = 0;
    WHILE @MonCounter < @SoMon
    BEGIN
        SELECT TOP 1 @MaMonAn = maMonAn, @DonGiaMon = donGia 
        FROM [dbo].[MONAN] WHERE trangThai = 1 ORDER BY NEWID();
        
        SET @SoLuongMon = CAST((RAND() * 2) + 1 AS INT);

        IF NOT EXISTS (SELECT 1 FROM [dbo].[CHITIETHOADON] WHERE maHoaDon = @MaHoaDon AND maMonAn = @MaMonAn)
        BEGIN
            INSERT INTO [dbo].[CHITIETHOADON] 
                ([maHoaDon], [maMonAn], [soLuong], [donGia])
            VALUES
                (@MaHoaDon, @MaMonAn, @SoLuongMon, @DonGiaMon);
            
            SET @MonCounter = @MonCounter + 1;
        END;
    END;
    
    SET @Counter = @Counter + 1;
END;
GO

SET NOCOUNT OFF;
GO