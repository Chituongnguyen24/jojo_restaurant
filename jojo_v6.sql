-- =================================================================================
-- PHẦN 1: TẠO VÀ CẤU HÌNH CƠ SỞ DỮ LIỆU
-- =================================================================================
USE [master]
GO
CREATE DATABASE [PTUD-JOJO-Restaurant]
 CONTAINMENT = NONE
 ON  PRIMARY 
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
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  ENABLE_BROKER 
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
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  MULTI_USER 
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

-- =================================================================================
-- PHẦN 2: TẠO CÁC BẢNG
-- =================================================================================
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
	[makhuVuc] [nchar](10) NOT NULL,
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
	[tienCoc] [money] NULL,
 CONSTRAINT [PK_PHIEUDATBAN] PRIMARY KEY CLUSTERED ([maPhieu] ASC)
) ON [PRIMARY]
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
	[ngayLap] [date] NOT NULL,
	[phuongThuc] [nvarchar](50) NOT NULL,
	[maKhuyenMai] [nchar](10) NULL,
	[maThue] [nchar](10) NOT NULL,
	[gioVao] [datetime] NOT NULL,
	[gioRa] [datetime] NOT NULL,
	[maNhanVien] [nchar](7) NOT NULL,
	[maPhieu] [nchar](10) NULL,
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

-- =================================================================================
-- PHẦN 3: TẠO RÀNG BUỘC KHÓA NGOẠI
-- =================================================================================
ALTER TABLE [dbo].[BAN]  WITH CHECK ADD  CONSTRAINT [FK_BAN_KHUVUC] FOREIGN KEY([makhuVuc])
REFERENCES [dbo].[KHUVUC] ([maKhuVuc])
GO
ALTER TABLE [dbo].[BAN] CHECK CONSTRAINT [FK_BAN_KHUVUC]
GO

ALTER TABLE [dbo].[TAIKHOAN]  WITH CHECK ADD  CONSTRAINT [FK_TAIKHOAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[TAIKHOAN] CHECK CONSTRAINT [FK_TAIKHOAN_NHANVIEN]
GO

ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_BAN] FOREIGN KEY([maBan])
REFERENCES [dbo].[BAN] ([maBan])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_BAN]
GO
ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_KHACHhang]
GO
ALTER TABLE [dbo].[PHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_PHIEUDATBAN_NHANVIEN] FOREIGN KEY([maNV])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[PHIEUDATBAN] CHECK CONSTRAINT [FK_PHIEUDATBAN_NHANVIEN]
GO

ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN] CHECK CONSTRAINT [FK_CHITIETPHIEUDATBAN_MONAN]
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETPHIEUDATBAN_PHIEUDATBAN] FOREIGN KEY([maPhieu])
REFERENCES [dbo].[PHIEUDATBAN] ([maPhieu])
GO
ALTER TABLE [dbo].[CHITIETPHIEUDATBAN] CHECK CONSTRAINT [FK_CHITIETPHIEUDATBAN_PHIEUDATBAN]
GO

ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHACHHANG] FOREIGN KEY([maKhachHang])
REFERENCES [dbo].[KHACHHANG] ([maKhachHang])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_KHACHHANG]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_KHUYENMAI] FOREIGN KEY([maKhuyenMai])
REFERENCES [dbo].[KHUYENMAI] ([maKhuyenMai])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_KHUYENMAI]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_NHANVIEN] FOREIGN KEY([maNhanVien])
REFERENCES [dbo].[NHANVIEN] ([maNV])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_NHANVIEN]
GO
ALTER TABLE [dbo].[HOADON]  WITH CHECK ADD  CONSTRAINT [FK_HOADON_THUE] FOREIGN KEY([maThue])
REFERENCES [dbo].[THUE] ([maSoThue])
GO
ALTER TABLE [dbo].[HOADON] CHECK CONSTRAINT [FK_HOADON_THUE]
GO

ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_HOADON] FOREIGN KEY([maHoaDon])
REFERENCES [dbo].[HOADON] ([maHoaDon])
GO
ALTER TABLE [dbo].[CHITIETHOADON] CHECK CONSTRAINT [FK_CHITIETHOADON_HOADON]
GO
ALTER TABLE [dbo].[CHITIETHOADON]  WITH CHECK ADD  CONSTRAINT [FK_CHITIETHOADON_MONAN] FOREIGN KEY([maMonAn])
REFERENCES [dbo].[MONAN] ([maMonAn])
GO
ALTER TABLE [dbo].[CHITIETHOADON] CHECK CONSTRAINT [FK_CHITIETHOADON_MONAN]
GO

-- =================================================================================
-- PHẦN 4: CHÈN DỮ LIỆU
-- =================================================================================
-- Bảng: KHUVUC
INSERT INTO [dbo].[KHUVUC] ([maKhuVuc], [tenKhuVuc], [moTa], [trangThai]) VALUES
(N'TRET', N'Tầng trệt', N'Khu vực máy lạnh, gần quầy lễ tân', 1),
(N'TANG2', N'Tầng 2', N'Khu vực máy lạnh, yên tĩnh', 1),
(N'VIP', N'Phòng VIP', N'Phòng riêng tư, sang trọng', 1),
(N'SANTHUONG', N'Sân thượng', N'Không gian mở, thoáng đãng', 1),
(N'SANVUON', N'Sân vườn', N'Không gian xanh, gần gũi thiên nhiên', 1)
GO

-- Bảng: BAN
INSERT INTO [dbo].[BAN] ([maBan], [soCho], [makhuVuc], [trangThai]) VALUES
(N'B01', 4, N'TRET', N'Trống'),
(N'B02', 4, N'TRET', N'Có khách'),
(N'B03', 6, N'TRET', N'Trống'),
(N'B04', 2, N'TRET', N'Trống'),
(N'B11', 4, N'TANG2', N'Đã đặt'),
(N'B12', 6, N'TANG2', N'Có khách'),
(N'B13', 8, N'TANG2', N'Trống'),
(N'B14', 2, N'TANG2', N'Trống'),
(N'B15', 4, N'TANG2', N'Trống'),
(N'ST01', 4, N'SANTHUONG', N'Có khách'),
(N'ST02', 6, N'SANTHUONG', N'Trống'),
(N'ST03', 2, N'SANTHUONG', N'Trống'),
(N'ST04', 4, N'SANTHUONG', N'Có khách'),
(N'SV01', 4, N'SANVUON', N'Trống'),
(N'SV02', 6, N'SANVUON', N'Đã đặt'),
(N'SV03', 2, N'SANVUON', N'Trống'),
(N'TANG206', 10, N'TANG2', N'Trống'),
(N'TRET05', 8, N'TRET', N'Trống'),
(N'VIP01', 10, N'VIP', N'Đã đặt'),
(N'VIP02', 12, N'VIP', N'Trống'),
(N'VIP03', 15, N'VIP', N'Trống')
GO

-- Bảng: MONAN
-- Bảng: MONAN
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
INSERT INTO [dbo].[NHANVIEN] ([maNV], [tenNhanVien], [gioiTinh], [sdt], [email]) VALUES
(N'NV00001', N'Nguyễn Văn An', 1, N'0901234567', N'an.nguyen@jojo.com'),
(N'NV00002', N'Trần Thị Bích', 0, N'0902345678', N'bich.tran@jojo.com'),
(N'NV00003', N'Lê Minh Cường', 1, N'0903456789', N'cuong.le@jojo.com'),
(N'NV00004', N'Phạm Thuỳ Dung', 0, N'0904567890', N'dung.pham@jojo.com'),
(N'NV00005', N'Hoàng Tiến Hải', 1, N'0905678901', N'hai.hoang@jojo.com'),
(N'NV00006', N'Đỗ Gia Hân', 0, N'0906789012', N'han.do@jojo.com'),
(N'NV00007', N'Võ Quốc Khánh', 1, N'0907890123', N'khanh.vo@jojo.com'),
(N'NV00008', N'Bùi Thị Lan', 0, N'0908901234', N'lan.bui@jojo.com'),
(N'NV00009', N'Lý Hùng Mạnh', 1, N'0909012345', N'manh.ly@jojo.com'),
(N'NV00010', N'Trịnh Ngọc Oanh', 0, N'0912112233', N'oanh.trinh@jojo.com'),
(N'NV00011', N'Đinh Công Tráng', 1, N'0912345678', N'trang.dinh@jojo.com'),
(N'NV00012', N'Mai Anh Thư', 0, N'0987123456', N'thu.mai@jojo.com'),
(N'NV00013', N'Trần Hoàng Phúc', 1, N'0905556677', N'phuc.tran@jojo.com'),
(N'NV00014', N'Lê Thị Kiều', 0, N'0977889900', N'kieu.le@jojo.com'),
(N'NV00015', N'Giang A Pháo', 1, N'0966554433', N'phao.giang@jojo.com')
GO

-- Bảng: TAIKHOAN
INSERT INTO [dbo].[TAIKHOAN] ([maNV], [tenDangNhap], [matKhau], [vaiTro]) VALUES
(N'NV00001', N'an.nguyen', N'Admin@123', N'NVQL'),
(N'NV00002', N'bich.tran', N'Nhanvien@1', N'NVTT'),
(N'NV00003', N'cuong.le', N'Nhanvien@2', N'NVTT'),
(N'NV00004', N'dung.pham', N'Nhanvien@3', N'NVTT'),
(N'NV00005', N'hai.hoang', N'Admin@456', N'NVQL'),
(N'NV00006', N'han.do', N'Nhanvien@4', N'NVTT'),
(N'NV00007', N'khanh.vo', N'Nhanvien@5', N'NVTT'),
(N'NV00008', N'lan.bui', N'Nhanvien@6', N'NVTT'),
(N'NV00009', N'manh.ly', N'Nhanvien@7', N'NVTT'),
(N'NV00010', N'oanh.trinh', N'Nhanvien@8', N'NVTT'),
(N'NV00011', N'trang.dinh', N'Nhanvien@9', N'NVTT'),
(N'NV00012', N'thu.mai', N'Nhanvien@10', N'NVTT'),
(N'NV00013', N'phuc.tran', N'Nhanvien@11', N'NVTT'),
(N'NV00014', N'kieu.le', N'Nhanvien@12', N'NVTT'),
(N'NV00015', N'phao.giang', N'Nhanvien@13', N'NVTT')
GO

-- Bảng: KHACHHANG
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
(N'KH25000020', N'Bùi Anh Khoa', N'0912121212', N'khoa.bui@email.com', 600, 1)
GO

-- Bảng: PHIEUDATBAN
INSERT INTO [dbo].[PHIEUDATBAN] ([maPhieu], [thoiGianDat], [maKhachHang], [maNV], [maBan], [tienCoc]) VALUES
(N'PDB000001', '2025-10-14 18:00:00', N'KH25000001', N'NV00002', N'B11', 100000),
(N'PDB000002', '2025-10-15 19:30:00', N'KH25000004', N'NV00004', N'VIP01', 500000),
(N'PDB000003', '2025-10-16 12:00:00', N'KH25000007', N'NV00002', N'B13', 200000),
(N'PDB000004', '2025-10-16 20:00:00', N'KH25000006', N'NV00004', N'ST02', 100000),
(N'PDB000005', '2025-10-18 11:30:00', N'KH25000005', N'NV00010', N'SV01', 100000),
(N'PDB000006', '2025-10-20 18:30:00', N'KH25000008', N'NV00002', N'B01', 100000),
(N'PDB000007', '2025-10-22 19:00:00', N'KH25000002', N'NV00004', N'B03', 100000),
(N'PDB000008', '2025-10-25 12:30:00', N'KH25000001', N'NV00010', N'ST02', 100000),
(N'PDB000009', '2025-11-01 19:00:00', N'KH25000010', N'NV00002', N'VIP02', 200000),
(N'PDB000010', '2025-11-05 20:00:00', N'KH25000005', N'NV00004', N'SV01', 100000),
(N'PDB000011', '2025-10-17 19:00:00', N'KH25000011', N'NV00012', N'SV02', 150000),
(N'PDB000012', '2025-10-18 20:00:00', N'KH25000013', N'NV00014', N'VIP03', 600000),
(N'PDB000013', '2025-10-19 18:30:00', N'KH25000015', N'NV00011', N'B14', 0),
(N'PDB000014', '2025-10-21 12:00:00', N'KH25000020', N'NV00013', N'TANG206', 300000),
(N'PDB000015', '2025-10-23 11:00:00', N'KH25000018', N'NV00015', N'TRET05', 200000)
GO

-- Bảng: CHITIETPHIEUDATBAN
INSERT INTO [dbo].[CHITIETPHIEUDATBAN] ([maMonAn], [maPhieu], [soLuongMonAn], [ghiChu]) VALUES
(N'MA0001', N'PDB000002', 1, N'Ít cay'),
(N'MA0004', N'PDB000009', 1, NULL),
(N'MA0005', N'PDB000009', 2, N'Thêm tóp mỡ'),
(N'MA0006', N'PDB000002', 2, N'Nướng tái'),
(N'MA0010', N'PDB000002', 1, NULL)
GO

-- Bảng: KHUYENMAI
INSERT INTO [dbo].[KHUYENMAI] ([maKhuyenMai], [tenKhuyenMai], [giaTri], [thoiGianBatDau], [thoiGianKetThuc]) VALUES
(N'KM00000000', N'Không áp dụng', 0, '2025-01-01', '2029-12-31'),
(N'KM25010001', N'Ưu đãi Hạng Đồng', 0.05, '2025-01-01', '2025-12-31'),
(N'KM25010002', N'Sinh nhật 10% Đồng', 0.1, '2025-01-01', '2025-12-31'),
(N'KM25010003', N'Sinh nhật 15% Bạc', 0.15, '2025-01-01', '2025-12-31'),
(N'KM25020001', N'Ưu đãi sinh nhật 20% Vàng', 0.2, '2025-01-01', '2025-12-31'),
(N'KM25030001', N'Quốc tế Phụ nữ 8/3', 0.08, '2025-03-08', '2025-03-08'),
(N'KM25100001', N'Ưu đãi Hạng Bạc', 0.1, '2025-01-01', '2025-12-31'),
(N'KM25100002', N'Ưu đãi Hạng Vàng', 0.15, '2025-01-01', '2025-12-31'),
(N'KM25100003', N'Voucher tháng 10 - Giảm 50k', 50000, '2025-10-01', '2025-10-31'),
(N'KM25110001', N'Voucher tháng 11 - Đi 4 tính 3', 0.25, '2025-11-01', '2025-11-30')
GO

-- Bảng: THUE
INSERT INTO [dbo].[THUE] ([maSoThue], [tenThue], [tyLeThue], [moTa], [trangThai]) VALUES
(N'0100109106', N'VAT', 0.08, N'Thuế giá trị gia tăng', 1),
(N'PHUPHI0001', N'Phí phục vụ', 0.05, N'Phí dịch vụ tại nhà hàng', 1)
GO

-- Bảng: HOADON
INSERT INTO [dbo].[HOADON] ([maHoaDon], [maKhachHang], [ngayLap], [phuongThuc], [maKhuyenMai], [maThue], [gioVao], [gioRa], [maNhanVien], [maPhieu]) VALUES
(N'HD000001', N'KH25000002', '2025-10-12', N'Tiền mặt', N'KM25010001', N'0100109106', '2025-10-12 18:05:10', '2025-10-12 19:30:25', N'NV00003', NULL),
(N'HD000002', N'KH25000004', '2025-10-12', N'Thẻ tín dụng', N'KM25100001', N'0100109106', '2025-10-12 19:00:00', '2025-10-12 20:15:40', N'NV00008', NULL),
(N'HD000003', N'KH00000000', '2025-10-13', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-13 11:30:00', '2025-10-13 12:15:00', N'NV00006', NULL),
(N'HD000004', N'KH25000007', '2025-10-13', N'Chuyển khoản', N'KM25100002', N'0100109106', '2025-10-13 20:00:15', '2025-10-13 22:05:00', N'NV00003', NULL),
(N'HD000005', N'KH25000005', '2025-10-14', N'Tiền mặt', N'KM25100003', N'0100109106', '2025-10-14 12:10:00', '2025-10-14 13:00:30', N'NV00007', NULL),
(N'HD000006', N'KH25000001', '2025-10-14', N'Thẻ tín dụng', N'KM00000000', N'0100109106', '2025-10-14 18:00:00', '2025-10-14 19:45:00', N'NV00008', N'PDB000001'),
(N'HD000007', N'KH00000000', '2025-10-15', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-15 09:45:00', '2025-10-15 10:20:10', N'NV00006', NULL),
(N'HD000008', N'KH25000003', '2025-10-15', N'Chuyển khoản', N'KM00000000', N'0100109106', '2025-10-15 14:00:00', '2025-10-15 15:30:00', N'NV00009', NULL),
(N'HD000009', N'KH25000010', '2025-10-15', N'Tiền mặt', N'KM25100002', N'0100109106', '2025-10-15 18:30:00', '2025-10-15 20:00:00', N'NV00003', NULL),
(N'HD000010', N'KH25000008', '2025-10-15', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-15 19:00:00', '2025-10-15 19:45:00', N'NV00007', NULL),
(N'HD000011', N'KH25000011', '2025-10-16', N'Tiền mặt', N'KM25100001', N'0100109106', '2025-10-16 11:00:00', '2025-10-16 12:30:00', N'NV00011', NULL),
(N'HD000012', N'KH00000000', '2025-10-16', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-16 12:05:00', '2025-10-16 12:55:00', N'NV00012', NULL),
(N'HD000013', N'KH25000014', '2025-10-16', N'Thẻ tín dụng', N'KM00000000', N'0100109106', '2025-10-16 18:00:00', '2025-10-16 19:15:00', N'NV00013', NULL),
(N'HD000014', N'KH25000016', '2025-10-17', N'Chuyển khoản', N'KM25010001', N'0100109106', '2025-10-17 19:00:00', '2025-10-17 21:00:00', N'NV00012', N'PDB000011'),
(N'HD000015', N'KH25000012', '2025-10-17', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-17 20:10:00', '2025-10-17 21:30:00', N'NV00014', NULL),
(N'HD000016', N'KH00000000', '2025-10-18', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-18 11:30:00', '2025-10-18 12:10:00', N'NV00015', NULL),
(N'HD000017', N'KH25000020', '2025-10-18', N'Thẻ tín dụng', N'KM25100002', N'0100109106', '2025-10-18 19:30:00', '2025-10-18 22:00:00', N'NV00005', NULL),
(N'HD000018', N'KH25000013', '2025-10-18', N'Chuyển khoản', N'KM25100001', N'0100109106', '2025-10-18 20:00:00', '2025-10-18 22:30:00', N'NV00014', N'PDB000012'),
(N'HD000019', N'KH25000017', '2025-10-19', N'Tiền mặt', N'KM25100001', N'0100109106', '2025-10-19 12:00:00', '2025-10-19 13:45:00', N'NV00001', NULL),
(N'HD000020', N'KH25000019', '2025-10-19', N'Thẻ tín dụng', N'KM00000000', N'0100109106', '2025-10-19 18:30:00', '2025-10-19 19:30:00', N'NV00011', N'PDB000013'),
(N'HD000021', N'KH25000018', '2025-10-20', N'Tiền mặt', N'KM25100001', N'PHUPHI0001', '2025-10-20 18:00:00', '2025-10-20 19:00:00', N'NV00003', NULL),
(N'HD000022', N'KH00000000', '2025-10-21', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-21 11:45:00', '2025-10-21 12:30:00', N'NV00007', NULL),
(N'HD000023', N'KH25000020', '2025-10-21', N'Chuyển khoản', N'KM25100002', N'0100109106', '2025-10-21 12:00:00', '2025-10-21 14:00:00', N'NV00013', N'PDB000014'),
(N'HD000024', N'KH00000000', '2025-10-22', N'Tiền mặt', N'KM00000000', N'0100109106', '2025-10-22 13:00:00', '2025-10-22 13:30:00', N'NV00002', NULL),
(N'HD000025', N'KH25000006', '2025-10-22', N'Thẻ tín dụng', N'KM25100002', N'0100109106', '2025-10-22 19:00:00', '2025-10-22 20:30:00', N'NV00004', NULL)
GO

-- Bảng: CHITIETHOADON
INSERT INTO [dbo].[CHITIETHOADON] ([maHoaDon], [maMonAn], [soLuong], [donGia]) VALUES
(N'HD000001', N'MA0002', 1, 320000),
(N'HD000001', N'MA0005', 1, 120000),
(N'HD000001', N'MA0014', 4, 25000),
(N'HD000002', N'MA0001', 1, 350000),
(N'HD000002', N'MA0006', 2, 180000),
(N'HD000003', N'MA0011', 1, 85000),
(N'HD000003', N'MA0014', 1, 25000),
(N'HD000004', N'MA0003', 2, 330000),
(N'HD000004', N'MA0008', 2, 160000),
(N'HD000004', N'MA0009', 2, 140000),
(N'HD000005', N'MA0004', 1, 300000),
(N'HD000005', N'MA0012', 1, 60000),
(N'HD000006', N'MA0001', 1, 350000),
(N'HD000006', N'MA0010', 1, 50000),
(N'HD000007', N'MA0013', 2, 45000),
(N'HD000008', N'MA0007', 2, 150000),
(N'HD000009', N'MA0002', 1, 320000),
(N'HD000010', N'MA0005', 2, 120000),
(N'HD000011', N'MA0010', 2, 50000),
(N'HD000011', N'MA0011', 1, 85000),
(N'HD000011', N'MA0014', 3, 25000),
(N'HD000012', N'MA0013', 2, 45000),
(N'HD000013', N'MA0005', 2, 120000),
(N'HD000013', N'MA0007', 1, 150000),
(N'HD000014', N'MA0002', 2, 320000),
(N'HD000014', N'MA0006', 1, 180000),
(N'HD000014', N'MA0012', 1, 60000),
(N'HD000015', N'MA0008', 2, 160000),
(N'HD000016', N'MA0010', 1, 50000),
(N'HD000017', N'MA0001', 2, 350000),
(N'HD000017', N'MA0003', 1, 330000),
(N'HD000017', N'MA0009', 3, 140000),
(N'HD000018', N'MA0004', 3, 300000),
(N'HD000018', N'MA0006', 4, 180000),
(N'HD000019', N'MA0007', 2, 150000),
(N'HD000019', N'MA0011', 2, 85000),
(N'HD000020', N'MA0012', 1, 60000),
(N'HD000021', N'MA0005', 3, 120000),
(N'HD000021', N'MA0014', 5, 25000),
(N'HD000022', N'MA0013', 1, 45000),
(N'HD000023', N'MA0001', 2, 350000),
(N'HD000023', N'MA0010', 3, 50000),
(N'HD000024', N'MA0011', 1, 85000),
(N'HD000025', N'MA0002', 1, 320000),
(N'HD000025', N'MA0008', 2, 160000)
GO

-- =================================================================================
-- PHẦN 5: HOÀN TẤT
-- =================================================================================
USE [master]
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET  READ_WRITE 
GO