-- =================================================================================
-- PHẦN 1: TẠO VÀ CẤU HÌNH CƠ SỞ DỮ LIỆU NHÀ HÀNG JOJO
-- =================================================================================
USE [master]
GO
DROP DATABASE IF EXISTS [PTUD-JOJO-Restaurant]
GO
CREATE DATABASE [PTUD-JOJO-Restaurant]
 CONTAINMENT = NONE
 ON PRIMARY
( NAME = N'PTUD-JOJO-Restaurant', FILENAME = N'D:\University\HK1-25-26\PTUD\CSDL_JOJO\PTUD-JOJO-Restaurant.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON
( NAME = N'PTUD-JOJO-Restaurant_log', FILENAME = N'D:\University\HK1-25-26\PTUD\CSDL_JOJO\PTUD-JOJO-Restaurant_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO

USE [PTUD-JOJO-Restaurant];
GO

/*
-- =================================================================================
-- PHẦN 2: TẠO CÁC BẢNG (ĐÃ THÊM GioiTinh VÀO KHACHHANG)
-- =================================================================================
*/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- 1. Bảng KHUVUC
CREATE TABLE [dbo].[KHUVUC](
	[maKhuVuc] [nchar](10) PRIMARY KEY NOT NULL,
	[tenKhuVuc] [nvarchar](50) NOT NULL,
	[moTa] [nvarchar](max) NULL,
	[trangThai] [bit] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

-- 2. Bảng KHACHHANG (ĐÃ SỬA: Thêm GioiTinh)
CREATE TABLE [dbo].[KHACHHANG](
	[MaKH] [nchar](10) PRIMARY KEY NOT NULL,
	[TenKH] [nvarchar](50) NOT NULL,
	[SoDienThoai] [varchar](11) NOT NULL,
	[Email] [varchar](100) NULL,
	[NgaySinh] [date] NULL,
    [GioiTinh] [bit] NULL, 
    [DiemTichLuy] [int] NOT NULL,
    [LaThanhVien] [bit] NOT NULL
) ON [PRIMARY]
GO

-- 3. Bảng NHANVIEN
CREATE TABLE [dbo].[NHANVIEN](
	[maNhanVien] [varchar](11) PRIMARY KEY NOT NULL,
	[hoTen] [nvarchar](50) NOT NULL,
	[ngaySinh] [date] NULL,
	[NgayVaoLam] [date] NULL,
	[SoCCCD] [varchar](12) NOT NULL,
	[GioiTinh] [bit] NULL,
	[SoDienThoai] [varchar](15) NULL,
	[Email] [varchar](100) NULL,
	[ChucVu] [nvarchar](50) NOT NULL,
	[TrangThai] [nvarchar](20) NOT NULL
) ON [PRIMARY]
GO

-- 4. Bảng TAIKHOAN
CREATE TABLE [dbo].[TAIKHOAN](
	[userID] [int] IDENTITY(1,1) NOT NULL,
	[tenDangNhap] [nvarchar](100) NOT NULL,
	[matKhau] [nvarchar](100) NOT NULL,
	[vaiTro] [nvarchar](50) NOT NULL,
	[trangThai] [bit] NULL,
	[maNhanVien] [varchar](11) NULL,
	PRIMARY KEY CLUSTERED ([userID] ASC),
	CONSTRAINT [FK_TAIKHOAN_NHANVIEN] FOREIGN KEY([maNhanVien]) REFERENCES [dbo].[NHANVIEN] ([maNhanVien])
) ON [PRIMARY]
GO

-- 5. Bảng BAN
CREATE TABLE [dbo].[BAN](
	[maBan] [nchar](10) PRIMARY KEY NOT NULL,
	[soCho] [int] NOT NULL,
	[maKhuVuc] [nchar](10) NOT NULL,
	[loaiBan] [nvarchar](20) NOT NULL,
	[trangThai] [nvarchar](20) NOT NULL,
	CONSTRAINT [FK_BAN_KHUVUC] FOREIGN KEY([maKhuVuc]) REFERENCES [dbo].[KHUVUC] ([maKhuVuc])
) ON [PRIMARY]
GO

-- 6. Bảng MONAN
CREATE TABLE [dbo].[MONAN](
	[maMonAn] [nchar](10) PRIMARY KEY NOT NULL,
	[tenMonAn] [nvarchar](50) NOT NULL,
	[donGia] [float] NOT NULL, 
	[trangThai] [bit] NOT NULL,
	[imagePath] [nvarchar](max) NULL,
    [loaiMonAn] [nvarchar](50) NULL
) ON [PRIMARY]
GO

-- 7. Bảng THUE
CREATE TABLE [dbo].[THUE](
	[maSoThue] [nchar](10) PRIMARY KEY NOT NULL,
	[tenThue] [nvarchar](50) NOT NULL,
	[tyLeThue] [float] NOT NULL,
	[moTa] [nvarchar](max) NULL,
	[trangThai] [bit] NOT NULL
) ON [PRIMARY]
GO

-- 8. Bảng KHUYENMAI
CREATE TABLE [dbo].[KHUYENMAI](
	[MaKM] [varchar](15) PRIMARY KEY NOT NULL,
	[MoTa] [nvarchar](100) NULL,
	[NgayApDung] [date] NULL,
	[NgayHetHan] [date] NULL,
	[MucKM] [decimal](3, 2) NULL,
	[trangThaiKM] [bit] NULL,
	[LoaiKM] [nvarchar](50) NULL
) ON [PRIMARY]
GO

-- 9. Bảng PHIEUDATBAN
CREATE TABLE [dbo].[PHIEUDATBAN](
	[maPhieu] [nchar](10) PRIMARY KEY NOT NULL,
	[thoiGianDenHen] [smalldatetime] NOT NULL,
	[thoiGianNhanBan] [smalldatetime] NULL,
	[thoiGianTraBan] [smalldatetime] NULL,
	[maKhachHang] [nchar](10) NOT NULL,
	[maNV] [varchar](11) NOT NULL,
	[maBan] [nchar](10) NOT NULL,
	[soNguoi] [int] NOT NULL,
	[ghiChu] [nvarchar](max) NULL,
    [trangThaiPhieu] [nvarchar](20) NOT NULL, -- Đã đến, Không đến, Chưa đến
	CONSTRAINT [FK_PDB_KH] FOREIGN KEY([maKhachHang]) REFERENCES [dbo].[KHACHHANG] ([MaKH]),
	CONSTRAINT [FK_PDB_NV] FOREIGN KEY([maNV]) REFERENCES [dbo].[NHANVIEN] ([maNhanVien]),
	CONSTRAINT [FK_PDB_BAN] FOREIGN KEY([maBan]) REFERENCES [dbo].[BAN] ([maBan]),
	CONSTRAINT [CK_PDB_TrangThai] CHECK ([trangThaiPhieu] IN (N'Đã đến', N'Không đến', N'Chưa đến', N'Hoàn thành')) -- Ràng buộc kiểm tra
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

-- 10. Bảng CHITIETPHIEUDATBAN
CREATE TABLE [dbo].[CHITIETPHIEUDATBAN](
	[maMonAn] [nchar](10) NOT NULL,
	[maPhieu] [nchar](10) NOT NULL,
	[soLuongMonAn] [int] NOT NULL,
	[DonGiaBan] [float] NOT NULL,
	[ghiChu] [nvarchar](max) NULL,
	PRIMARY KEY CLUSTERED ([maMonAn] ASC,	[maPhieu] ASC),
	CONSTRAINT [FK_CTPDB_MONAN] FOREIGN KEY([maMonAn]) REFERENCES [dbo].[MONAN] ([maMonAn]),
	CONSTRAINT [FK_CTPDB_PHIEUDATBAN] FOREIGN KEY([maPhieu]) REFERENCES [dbo].[PHIEUDATBAN] ([maPhieu])
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

-- 11. Bảng HOADON
CREATE TABLE [dbo].[HOADON](
	[MaHD] [varchar](15) PRIMARY KEY NOT NULL,
	[MaNV] [varchar](11) NOT NULL,
	[MaKH] [nchar](10) NOT NULL,
	[maBan] [nchar](10) NOT NULL,
	[NgayLapHoaDon] [date] NOT NULL,
	[GioVao] [smalldatetime] NOT NULL,
	[GioRa] [smalldatetime] NOT NULL,
	[phuongThucThanhToan] [nvarchar](50) NOT NULL,
	[MaKM] [varchar](15) NULL,
	[MaThue] [nchar](10) NOT NULL,
	[MaPhieu] [nchar](10) NULL,
	[TongTienTruocThue] [float] NOT NULL,
	[TongGiamGia] [float] NOT NULL,
	[DaThanhToan] [bit] NOT NULL,
	CONSTRAINT [FK_HD_NV] FOREIGN KEY([MaNV]) REFERENCES [dbo].[NHANVIEN] ([maNhanVien]),
	CONSTRAINT [FK_HD_KH] FOREIGN KEY([MaKH]) REFERENCES [dbo].[KHACHHANG] ([MaKH]),
	CONSTRAINT [FK_HD_KM] FOREIGN KEY([MaKM]) REFERENCES [dbo].[KHUYENMAI] ([MaKM]),
	CONSTRAINT [FK_HD_THUE] FOREIGN KEY([MaThue]) REFERENCES [dbo].[THUE] ([maSoThue]),
	CONSTRAINT [FK_HD_BAN] FOREIGN KEY([maBan]) REFERENCES [dbo].[BAN] ([maBan]),
	CONSTRAINT [FK_HD_PDB] FOREIGN KEY([MaPhieu]) REFERENCES [dbo].[PHIEUDATBAN] ([maPhieu])
) ON [PRIMARY]
GO

-- 12. Bảng CHITIETHOADON
CREATE TABLE [dbo].[CHITIETHOADON](
	[MaHD] [varchar](15) NOT NULL,
	[MaMonAn] [nchar](10) NOT NULL,
	[DonGiaBan] [float] NOT NULL,
	[SoLuong] [int] NOT NULL,
	PRIMARY KEY CLUSTERED ([MaHD] ASC, [MaMonAn] ASC),
	CONSTRAINT [FK_CTHD_HD] FOREIGN KEY([MaHD]) REFERENCES [dbo].[HOADON] ([MaHD]),
	CONSTRAINT [FK_CTHD_MA] FOREIGN KEY([MaMonAn]) REFERENCES [dbo].[MONAN] ([maMonAn])
) ON [PRIMARY]
GO

-- =================================================================================
-- PHẦN 4: CHÈN DỮ LIỆU (ĐÃ MỞ RỘNG BÀN, HOADON, PDB)
-- =================================================================================
USE [PTUD-JOJO-Restaurant]
GO

-- 7 & 8. THUE & KHUYENMAI (Giữ nguyên)
INSERT INTO [dbo].[KHUYENMAI] ([MaKM], [MoTa], [NgayApDung], [NgayHetHan], [MucKM], [trangThaiKM], [LoaiKM]) VALUES
(N'KM00000000', N'Không áp dụng', '2025-01-01', '2029-12-31', 0.00, 1, N'Không'), 
(N'KM25TV01', N'Ưu đãi Thành viên Đồng (5%)', '2025-01-01', '2025-12-31', 0.05, 1, N'Thành viên'),
(N'KM25TV02', N'Ưu đãi Thành viên Bạc (10%)', '2025-01-01', '2025-12-31', 0.10, 1, N'Thành viên'), 
(N'KM25TV03', N'Ưu đãi Thành viên Vàng (15%)', '2025-01-01', '2025-12-31', 0.15, 1, N'Thành viên'),
(N'KMSN01', N'Sinh nhật Đồng (10%)', '2025-01-01', '2025-12-31', 0.10, 1, N'Sinh nhật'), 
(N'KMSN02', N'Sinh nhật Bạc (15%)', '2025-01-01', '2025-12-31', 0.15, 1, N'Sinh nhật'),
(N'KMSN03', N'Sinh nhật Vàng (20%)', '2025-01-01', '2025-12-31', 0.20, 1, N'Sinh nhật'), 
(N'KMTHANG10', N'Voucher ưu đãi tháng 10 (10%)', '2025-10-01', '2025-10-31', 0.10, 0, N'Voucher'),
(N'KMTHANG11', N'Voucher ưu đãi tháng 11 (15%)', '2025-11-01', '2025-11-30', 0.15, 1, N'Voucher')
GO
INSERT INTO [dbo].[THUE] ([maSoThue], [tenThue], [tyLeThue], [moTa], [trangThai]) VALUES
(N'VAT08', N'VAT 8%', 0.08, N'Thuế giá trị gia tăng 8%', 1), 
(N'PHIPK5', N'Phí phục vụ 5%', 0.05, N'Phí dịch vụ tại nhà hàng 5%', 1)
GO

-- 1 & 6. KHUVUC & MONAN (Giữ nguyên)
INSERT INTO [dbo].[KHUVUC] ([maKhuVuc], [tenKhuVuc], [moTa], [trangThai]) VALUES
(N'TRET', N'Tầng trệt', N'Khu vực máy lạnh, gần quầy lễ tân', 1),
(N'TANG2', N'Tầng 2', N'Khu vực máy lạnh, yên tĩnh', 1),
(N'VIP', N'Phòng VIP', N'Phòng riêng tư, sang trọng', 1),
(N'SANTHUONG', N'Sân thượng', N'Không gian mở, thoáng đãng', 1),
(N'SANVUON', N'Sân vườn', N'Không gian xanh, gần gũi thiên nhiên', 1)
GO
INSERT INTO [dbo].[MONAN] ([maMonAn], [tenMonAn], [donGia], [trangThai], [imagePath], [loaiMonAn]) VALUES
(N'MA0001', N'Lẩu Thái chua cay', 350000.00, 1, N'images/mon an/lau-thai-chua-cay.jpg', N'Lẩu'),
(N'MA0002', N'Lẩu riêu cua đồng', 320000.00, 1, N'images/mon an/lau-rieu-cua-16x9.jpg', N'Lẩu'),
(N'MA0003', N'Lẩu kim chi cay nồng', 330000.00, 1, N'images/mon an/lau-kim-chi.jpg', N'Lẩu'),
(N'MA0004', N'Lẩu nấm thanh mát', 300000.00, 1, N'images/mon an/lau-nam-chay-880.jpg', N'Lẩu'),
(N'MA0005', N'Ba chỉ heo nướng', 120000.00, 1, N'images/mon an/ba-chi-heo-nuong.jpg', N'Nướng'),
(N'MA0006', N'Sườn bò Mỹ nướng', 180000.00, 1, N'images/mon an/de-suon-bo-nuong.png', N'Nướng'),
(N'MA0007', N'Gà ướp sate nướng', 150000.00, 1, N'images/mon an/ga-uop-sate-nuong.jpg', N'Nướng'),
(N'MA0008', N'Tôm nướng muối ớt', 160000.00, 1, N'images/mon an/tom-nuong-muoi-ot.png', N'Nướng'),
(N'MA0009', N'Mực nướng sa tế', 140000.00, 1, N'images/mon an/muc-nuong-sate.jpg', N'Nướng'),
(N'MA0010', N'Cơm chiên tỏi', 50000.00, 1, N'images/mon an/com-chien-toi.jpg', N'Ăn kèm'),
(N'MA0011', N'Mì xào hải sản', 85000.00, 1, N'images/mon an/mi-xao-hai-san.jpg', N'Ăn kèm'),
(N'MA0012', N'Salad dầu giấm', 60000.00, 1, N'images/mon an/salad-dau-giam.jpg', N'Ăn kèm'),
(N'MA0013', N'Súp cua', 45000.00, 1, N'images/mon an/sup-cua.jpg', N'Ăn kèm'),
(N'MA0014', N'Coca-Cola', 25000.00, 1, N'images/mon an/coca-cola.jpg', N'Đồ uống'),
(N'MA0015', N'Bia Tiger', 30000.00, 0, N'images/mon an/bia-tiger.jpg', N'Đồ uống')
GO

-- 5. Bảng BAN (ĐÃ MỞ RỘNG THÊM 10 BÀN)
INSERT INTO [dbo].[BAN] ([maBan], [soCho], [maKhuVuc], [loaiBan], [trangThai]) VALUES
-- Tầng trệt (20 bàn)
(N'B001', 4, N'TRET', N'THUONG', N'Trống'), (N'B002', 4, N'TRET', N'THUONG', N'Trống'), (N'B003', 6, N'TRET', N'THUONG', N'Trống'), (N'B004', 2, N'TRET', N'THUONG', N'Trống'), (N'B005', 8, N'TRET', N'THUONG', N'Trống'),
(N'B006', 4, N'TRET', N'THUONG', N'Trống'), (N'B007', 4, N'TRET', N'THUONG', N'Trống'), (N'B008', 6, N'TRET', N'THUONG', N'Trống'), (N'B009', 2, N'TRET', N'THUONG', N'Trống'), (N'B010', 8, N'TRET', N'THUONG', N'Trống'),
(N'B011', 4, N'TRET', N'THUONG', N'Trống'), (N'B012', 4, N'TRET', N'THUONG', N'Trống'), (N'B013', 6, N'TRET', N'THUONG', N'Trống'), (N'B014', 2, N'TRET', N'THUONG', N'Trống'), (N'B015', 8, N'TRET', N'THUONG', N'Trống'),
(N'B016', 4, N'TRET', N'THUONG', N'Trống'), (N'B017', 4, N'TRET', N'THUONG', N'Trống'), (N'B018', 6, N'TRET', N'THUONG', N'Trống'), (N'B019', 2, N'TRET', N'THUONG', N'Trống'), (N'B020', 8, N'TRET', N'THUONG', N'Trống'),
-- Tầng 2 (20 bàn)
(N'B201', 4, N'TANG2', N'THUONG', N'Trống'), (N'B202', 4, N'TANG2', N'THUONG', N'Trống'), (N'B203', 6, N'TANG2', N'THUONG', N'Trống'), (N'B204', 2, N'TANG2', N'THUONG', N'Trống'), (N'B205', 8, N'TANG2', N'THUONG', N'Trống'),
(N'B206', 4, N'TANG2', N'THUONG', N'Trống'), (N'B207', 4, N'TANG2', N'THUONG', N'Trống'), (N'B208', 6, N'TANG2', N'THUONG', N'Trống'), (N'B209', 2, N'TANG2', N'THUONG', N'Trống'), (N'B210', 8, N'TANG2', N'THUONG', N'Trống'),
(N'B211', 4, N'TANG2', N'THUONG', N'Trống'), (N'B212', 4, N'TANG2', N'THUONG', N'Trống'), (N'B213', 6, N'TANG2', N'THUONG', N'Trống'), (N'B214', 2, N'TANG2', N'THUONG', N'Trống'), (N'B215', 8, N'TANG2', N'THUONG', N'Trống'),
(N'B216', 4, N'TANG2', N'THUONG', N'Trống'), (N'B217', 4, N'TANG2', N'THUONG', N'Trống'), (N'B218', 6, N'TANG2', N'THUONG', N'Trống'), (N'B219', 2, N'TANG2', N'THUONG', N'Trống'), (N'B220', 8, N'TANG2', N'THUONG', N'Trống'),
-- Phòng VIP (15 bàn - THÊM 5)
(N'VIP01', 10, N'VIP', N'VIP', N'Trống'), (N'VIP02', 12, N'VIP', N'VIP', N'Trống'), (N'VIP03', 15, N'VIP', N'VIP', N'Trống'), (N'VIP04', 8, N'VIP', N'VIP', N'Trống'), (N'VIP05', 10, N'VIP', N'VIP', N'Trống'),
(N'VIP06', 12, N'VIP', N'VIP', N'Trống'), (N'VIP07', 15, N'VIP', N'VIP', N'Trống'), (N'VIP08', 8, N'VIP', N'VIP', N'Trống'), (N'VIP09', 10, N'VIP', N'VIP', N'Trống'), (N'VIP10', 12, N'VIP', N'VIP', N'Trống'),
(N'VIP11', 15, N'VIP', N'VIP', N'Trống'), (N'VIP12', 8, N'VIP', N'VIP', N'Trống'), (N'VIP13', 10, N'VIP', N'VIP', N'Trống'), (N'VIP14', 12, N'VIP', N'VIP', N'Trống'), (N'VIP15', 15, N'VIP', N'VIP', N'Trống'),
-- Sân Thượng (15 bàn)
(N'ST01', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST02', 6, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST03', 2, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST04', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST05', 6, N'SANTHUONG', N'SAN_THUONG', N'Trống'),
(N'ST06', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST07', 2, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST08', 6, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST09', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST10', 8, N'SANTHUONG', N'SAN_THUONG', N'Trống'),
(N'ST11', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST12', 6, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST13', 2, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST14', 4, N'SANTHUONG', N'SAN_THUONG', N'Trống'), (N'ST15', 6, N'SANTHUONG', N'SAN_THUONG', N'Trống'),
-- Sân Vườn (20 bàn - THÊM 5)
(N'SV01', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV02', 6, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV03', 2, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV04', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV05', 6, N'SANVUON', N'SAN_VUON', N'Trống'),
(N'SV06', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV07', 2, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV08', 6, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV09', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV10', 8, N'SANVUON', N'SAN_VUON', N'Trống'),
(N'SV11', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV12', 6, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV13', 2, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV14', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV15', 6, N'SANVUON', N'SAN_VUON', N'Trống'),
(N'SV16', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV17', 2, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV18', 6, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV19', 4, N'SANVUON', N'SAN_VUON', N'Trống'), (N'SV20', 8, N'SANVUON', N'SAN_VUON', N'Trống')
GO

-- 2 & 3 & 4. KHACHHANG & NHANVIEN & TAIKHOAN (Giữ nguyên)
INSERT INTO [dbo].[KHACHHANG] ([MaKH], [TenKH], [SoDienThoai], [Email], [NgaySinh], [GioiTinh], [DiemTichLuy], [LaThanhVien]) VALUES
--                                                                                    (Ngày sinh)   (Giới tính) (Điểm TL) (TV)
(N'KH00000000', N'Khách vãng lai', N'0000000000', NULL, NULL, NULL, 0, 0), 
(N'KH25000001', N'Trần Văn Nam', N'0987654321', N'nam.tran@email.com', '1990-05-15', 1, 150, 1),
(N'KH25000002', N'Nguyễn Thị Hoa', N'0986543210', N'hoa.nguyen@email.com', '1995-10-25', 0, 85, 1), 
(N'KH25000003', N'Lê Văn Long', N'0975432109', N'long.le@email.com', '1985-12-01', 1, 0, 0),
(N'KH25000004', 'Phạm Thị Mai', N'0914321098', N'mai.pham@email.com', '1988-03-08', 0, 250, 1), 
(N'KH25000005', N'Đặng Hoàng Quân', N'0933210987', N'quan.dang@email.com', '1992-07-20', 1, 410, 1),
(N'KH25000006', N'Vũ Bích Thảo', N'0942109876', N'thao.vu@email.com', '1998-09-10', 0, 520, 1), 
(N'KH25000007', N'Hồ Minh Tâm', N'0981098765', N'tam.ho@email.com', '1975-01-01', 1, 800, 1),
(N'KH25000008', N'Ngô Gia Bảo', '0979876543', N'bao.ngo@email.com', '2000-02-29', 1, 30, 1), 
(N'KH25000009', 'Đinh Yến Nhi', '0968765432', N'nhi.dinh@email.com', '2001-11-11', 0, 0, 0),
(N'KH25000010', 'Lý Gia Hân', '0918765432', N'han.ly@email.com', '1980-04-05', 0, 480, 1)
GO
INSERT INTO [dbo].[NHANVIEN] ([maNhanVien], [hoTen], [ngaySinh], [NgayVaoLam], [SoCCCD], [GioiTinh], [SoDienThoai], [Email], [ChucVu], [TrangThai]) VALUES
(N'NVQL001', N'Nguyễn Văn An', '1985-01-01', '2018-01-10', N'079085001234', 1, N'0901234567', N'an.nvql@jojo.com', N'NVQL', N'Đang làm'), 
(N'NVQL002', N'Trần Thị Bích', '1990-03-08', '2019-05-20', N'079090001235', 0, N'0902345678', N'bich.nvql@jojo.com', N'NVQL', N'Đang làm'),
(N'NVQL003', N'Lê Minh Cường', '1992-07-20', '2020-08-15', N'079092001236', 1, N'0903456789', N'cuong.nvql@jojo.com', N'NVQL', N'Đang làm'), 
(N'NVQL004', N'Phạm Thuỳ Dung', '1995-11-25', '2021-11-01', N'079095001237', 0, N'0904567890', N'dung.nvql@jojo.com', N'NVQL', N'Đang làm'),
(N'NVQL005', N'Hoàng Tiến Hải', '1998-02-19', '2022-04-10', N'079098001238', 1, N'0905678901', N'hai.nvql@jojo.com', N'NVQL', N'Đang làm'), 
(N'NVTT001', N'Đỗ Gia Hân', '2000-09-10', '2023-01-05', N'079100001239', 0, N'0906789012', N'han.nvtt@jojo.com', N'NVTT', N'Đang làm'),
(N'NVTT002', N'Võ Quốc Khánh', '1997-04-05', '2023-03-15', N'079097001240', 1, N'0907890123', N'khanh.nvtt@jojo.com', N'NVTT', N'Đang làm'), 
(N'NVTT003', N'Bùi Thị Lan', '1999-06-01', '2023-06-20', N'079099001241', 0, N'0908901234', N'lan.nvtt@jojo.com', N'NVTT', N'Đang làm'),
(N'NVTT004', 'Lý Hùng Mạnh', '1996-10-28', '2023-09-01', N'079096001242', 1, N'0909012345', N'manh.nvtt@jojo.com', N'NVTT', N'Đang làm'), 
(N'NVTT005', N'Trịnh Ngọc Oanh', '2002-12-05', '2024-01-15', N'079102001243', 0, N'0912112233', N'oanh.nvtt@jojo.com', N'NVTT', N'Đang làm'),
(N'NVTT006', N'Đinh Công Tráng', '1994-08-22', '2024-03-01', N'079094001244', 1, N'0912345678', N'trang.nvtt@jojo.com', N'NVTT', N'Đang làm'), 
(N'NVTT007', N'Mai Anh Thư', '1996-01-10', '2024-05-10', N'079096001245', 0, N'0987123456', N'thu.nvtt@jojo.com', N'NVTT', N'Đang làm'),
(N'NVTT008', N'Trần Hoàng Phúc', '1999-03-15', '2024-07-20', N'079099001246', 1, N'0905556677', N'phuc.nvtt@jojo.com', N'NVTT', N'Đang làm'), 
(N'NVTT009', N'Lê Thị Kiều', '2001-05-30', '2024-10-01', N'079101001247', 0, N'0977889900', N'kieu.nvtt@jojo.com', N'NVTT', N'Đang làm'),
(N'NVTT010', 'Giang A Pháo', '1993-11-18', '2025-01-10', N'079093001248', 1, N'0966554433', N'phao.nvtt@jojo.com', N'NVTT', N'Đang làm')
GO
SET IDENTITY_INSERT [dbo].[TAIKHOAN] ON
INSERT INTO [dbo].[TAIKHOAN] ([userID], [tenDangNhap], [matKhau], [vaiTro], [trangThai], [maNhanVien]) VALUES
(1, N'1', N'1', N'NVQL', 1, N'NVQL001'), 
(2, N'annguyen', N'123456', N'NVQL', 1, N'NVQL001'), 
(3, N'bichtran', N'123456', N'NVQL', 1, N'NVQL002'), 
(4, N'minhcuong', N'123456', N'NVQL', 1, N'NVQL003'),
(5, N'thuyduong', N'123456', N'NVQL', 1, N'NVQL004'), 
(6, N'tienhai', N'123456', N'NVQL', 1, N'NVQL005'), 
(7, N'giahango', N'123456', N'NVTT', 1, N'NVTT001'), 
(8, N'quockhanh', N'123456', N'NVTT', 1, N'NVTT002'),
(9, N'thilan', N'123456', N'NVTT', 1, N'NVTT003'), 
(10, N'hungmanh', N'123456', N'NVTT', 1, N'NVTT004'), 
(11, N'ngocoanh', N'123456', N'NVTT', 1, N'NVTT005'), 
(12, N'congtrang', N'123456', N'NVTT', 1, N'NVTT006'),
(13, N'anhthu', N'123456', N'NVTT', 1, N'NVTT007'), 
(14, N'hoangphuc', N'123456', N'NVTT', 1, N'NVTT008'), 
(15, N'thikieu', N'123456', N'NVTT', 1, N'NVTT009'), 
(16, N'aphao', N'123456', N'NVTT', 1, N'NVTT010')
SET IDENTITY_INSERT [dbo].[TAIKHOAN] OFF
GO

-- 9. PHIEUDATBAN (Mở rộng dữ liệu lịch sử)
INSERT INTO [dbo].[PHIEUDATBAN] ([maPhieu], [thoiGianDenHen], [thoiGianNhanBan], [thoiGianTraBan], [maKhachHang], [maNV], [maBan], [soNguoi], [ghiChu], [trangThaiPhieu]) VALUES
-- PHIẾU ĐÃ KẾT THÚC (Tháng 9)
(N'PDB00011', '2025-09-05 18:30', '2025-09-05 18:30', '2025-09-05 20:00', N'KH25000005', N'NVTT001', N'B001', 4, N'Bàn gần cửa sổ', N'Hoàn thành'),
(N'PDB00012', '2025-09-10 19:00', '2025-09-10 19:00', '2025-09-10 21:00', N'KH25000006', N'NVTT003', N'B205', 8, N'Tiệc công ty', N'Hoàn thành'),
(N'PDB00013', '2025-09-15 12:00', '2025-09-15 12:00', '2025-09-15 13:30', N'KH25000001', N'NVTT005', N'ST03', 2, NULL, N'Hoàn thành'),
(N'PDB00014', '2025-09-20 19:30', NULL, NULL, N'KH25000007', N'NVTT007', N'VIP02', 12, N'Khách không đến', N'Không đến'),
(N'PDB00015', '2025-09-25 18:00', '2025-09-25 18:00', '2025-09-25 19:45', N'KH25000004', N'NVTT009', N'SV08', 6, N'Bàn ngoài trời', N'Hoàn thành'),
-- PHIẾU ĐÃ KẾT THÚC (Tháng 10 - Dữ liệu cũ)
(N'PDB000001', '2025-10-25 18:00', '2025-10-25 18:05', '2025-10-25 19:30', N'KH25000004', N'NVTT001', N'B005', 4, N'4 người lớn, bàn yên tĩnh', N'Hoàn thành'),
(N'PDB000002', '2025-10-25 19:30', '2025-10-25 19:40', '2025-10-25 21:00', N'KH25000006', N'NVTT002', N'VIP01', 10, N'Tiệc sinh nhật', N'Hoàn thành'),
(N'PDB000003', '2025-10-28 12:00', '2025-10-28 12:00', '2025-10-28 13:15', N'KH25000001', N'NVTT003', N'B201', 2, N'Cần bàn có sạc điện thoại', N'Hoàn thành'),
(N'PDB000004', '2025-10-28 18:30', NULL, NULL, N'KH25000007', N'NVTT004', N'ST01', 4, N'Khách không đến nhận bàn', N'Không đến'),
(N'PDB000005', '2025-10-30 11:30', '2025-10-30 11:45', '2025-10-30 13:00', N'KH00000000', N'NVTT005', N'B007', 6, NULL, N'Hoàn thành'),
-- PHIẾU TRONG TƯƠNG LAI (Tháng 11)
(N'PDB000006', '2025-11-01 18:00', NULL, NULL, N'KH25000003', N'NVTT006', N'VIP03', 15, N'Họp mặt đối tác', N'Hoàn thành'), 
(N'PDB000007', '2025-11-05 10:00', NULL, NULL, N'KH25000008', N'NVTT007', N'SV03', 2, N'Ưu tiên bàn gần cây xanh', N'Chưa đến'),
(N'PDB000008', '2025-11-15 19:00', NULL, NULL, N'KH25000010', N'NVTT008', N'B203', 6, N'Khách đặt trước món', N'Chưa đến'),
(N'PDB000009', '2025-11-20 18:00', NULL, NULL, N'KH25000005', N'NVTT009', N'SV01', 4, NULL, N'Chưa đến'),
(N'PDB000010', '2025-11-25 12:30', NULL, NULL, N'KH25000002', N'NVTT010', N'ST05', 6, N'Bàn cho gia đình có trẻ em', N'Chưa đến')
GO


-- 11. HOADON (24 bản ghi)
INSERT INTO [dbo].[HOADON] ([MaHD], [MaNV], [MaKH], [maBan], [NgayLapHoaDon], [GioVao], [GioRa], [phuongThucThanhToan], [MaKM], [MaThue], [MaPhieu], [TongTienTruocThue], [TongGiamGia], [DaThanhToan]) VALUES

(N'HD2509050001', N'NVTT001', N'KH25000005', N'B001', '2025-09-05', '2025-09-05 18:30', '2025-09-05 20:00', N'Tiền mặt', N'KM25TV01', N'VAT08', N'PDB00011', 300000.00, 15000.00, 1), 
(N'HD2509100001', N'NVTT003', N'KH25000006', N'B205', '2025-09-10', '2025-09-10 19:00', '2025-09-10 21:00', N'Chuyển khoản', N'KM25TV03', N'PHIPK5', N'PDB00012', 1240000.00, 186000.00, 1), 
(N'HD2509150001', N'NVTT005', N'KH25000001', N'ST03', '2025-09-15', '2025-09-15 12:00', '2025-09-15 13:30', N'Thẻ tín dụng', N'KM25TV01', N'VAT08', N'PDB00013', 475000.00, 23750.00, 1),
(N'HD2509250001', N'NVTT009', N'KH25000004', N'SV08', '2025-09-25', '2025-09-25 18:00', '2025-09-25 19:45', N'Tiền mặt', N'KM25TV02', N'VAT08', N'PDB00015', 690000.00, 69000.00, 1),
(N'HD2510250001', N'NVTT001', N'KH25000004', N'B005', '2025-10-25', '2025-10-25 18:05', '2025-10-25 19:30', N'Thẻ tín dụng', N'KM25TV02', N'VAT08', N'PDB000001', 760000.00, 76000.00, 1), 
(N'HD2510250002', N'NVTT002', N'KH25000006', N'VIP01', '2025-10-25', '2025-10-25 19:40', '2025-10-25 21:00', N'Chuyển khoản', N'KM25TV03', N'PHIPK5', N'PDB000002', 1990000.00, 298500.00, 1), 
(N'HD2510280001', N'NVTT003', N'KH25000001', N'B201', '2025-10-28', '2025-10-28 12:00', '2025-10-28 13:15', N'Tiền mặt', N'KM25TV01', N'PHIPK5', N'PDB000003', 520000.00, 26000.00, 1),
(N'HD2510300001', N'NVTT005', N'KH00000000', N'B007', '2025-10-30', '2025-10-30 11:45', '2025-10-30 13:00', N'Tiền mặt', N'KM00000000', N'VAT08', N'PDB000005', 700000.00, 0.00, 1),
(N'HD2510300002', N'NVTT005', N'KH00000000', N'ST02', '2025-10-30', '2025-10-30 20:00', '2025-10-30 20:45', N'Tiền mặt', N'KM00000000', N'VAT08', NULL, 605000.00, 0.00, 1),

(N'HD2509180001', N'NVTT002', N'KH25000002', N'B018', '2025-09-18', '2025-09-18 20:00', '2025-09-18 21:00', N'Tiền mặt', N'KM25TV01', N'VAT08', NULL, 430000.00, 21500.00, 1),
(N'HD2509280001', N'NVTT008', N'KH25000007', N'VIP10', '2025-09-28', '2025-09-28 18:00', '2025-09-28 20:30', N'Chuyển khoản', N'KM25TV03', N'PHIPK5', NULL, 1320000.00, 198000.00, 1),
(N'HD2510020001', N'NVTT006', N'KH25000009', N'ST12', '2025-10-02', '2025-10-02 18:30', '2025-10-02 20:00', N'Tiền mặt', N'KM00000000', N'VAT08', NULL, 835000.00, 0.00, 1),
(N'HD2510070001', N'NVTT009', N'KH25000003', N'B015', '2025-10-07', '2025-10-07 19:45', '2025-10-07 21:15', N'Chuyển khoản', N'KM00000000', N'VAT08', NULL, 750000.00, 0.00, 1),
(N'HD2510100001', N'NVTT008', N'KH25000005', N'SV15', '2025-10-10', '2025-10-10 12:00', '2025-10-10 13:30', N'Tiền mặt', N'KM25TV03', N'PHIPK5', NULL, 640000.00, 96000.00, 1),
(N'HD2510140001', N'NVTT005', N'KH25000008', N'B202', '2025-10-14', '2025-10-14 11:45', '2025-10-14 13:00', N'Tiền mặt', N'KM25TV01', N'VAT08', NULL, 470000.00, 23500.00, 1),
(N'HD2510180001', N'NVTT003', N'KH25000001', N'B013', '2025-10-18', '2025-10-18 19:00', '2025-10-18 20:45', N'Thẻ tín dụng', N'KMSN01', N'VAT08', NULL, 610000.00, 61000.00, 1),
(N'HD2510200001', N'NVTT007', N'KH25000006', N'VIP12', '2025-10-20', '2025-10-20 12:30', '2025-10-20 14:00', N'Chuyển khoản', N'KM25TV03', N'PHIPK5', NULL, 755000.00, 113250.00, 1),
(N'HD2510220001', N'NVTT009', N'KH25000004', N'SV18', '2025-10-22', '2025-10-22 19:30', '2025-10-22 21:00', N'Tiền mặt', N'KM25TV02', N'VAT08', NULL, 620000.00, 62000.00, 1),
(N'HD2510240001', N'NVTT002', N'KH25000003', N'B219', '2025-10-24', '2025-10-24 10:00', '2025-10-24 11:00', N'Tiền mặt', N'KM00000000', N'VAT08', NULL, 170000.00, 0.00, 1),
(N'HD2510250003', N'NVTT004', N'KH25000001', N'B012', '2025-10-25', '2025-10-25 13:00', '2025-10-25 14:15', N'Chuyển khoản', N'KM25TV01', N'VAT08', NULL, 685000.00, 34250.00, 1),
(N'HD2510280002', N'NVTT004', N'KH25000002', N'B009', '2025-10-28', '2025-10-28 19:30', '2025-10-28 20:30', N'Tiền mặt', N'KM25TV01', N'PHIPK5', NULL, 200000.00, 10000.00, 1),
(N'HD2510290001', N'NVTT007', N'KH25000010', N'SV16', '2025-10-29', '2025-10-29 18:30', '2025-10-29 19:45', N'Chuyển khoản', N'KMSN03', N'VAT08', NULL, 630000.00, 126000.00, 1),
(N'HD2510300003', N'NVTT006', N'KH00000000', N'B214', '2025-10-30', '2025-10-30 11:00', '2025-10-30 12:00', N'Tiền mặt', N'KM00000000', N'VAT08', NULL, 220000.00, 0.00, 1),
(N'HD2510300004', N'NVTT008', N'KH25000005', N'VIP15', '2025-10-30', '2025-10-30 19:00', '2025-10-30 21:30', N'Chuyển khoản', N'KM25TV03', N'PHIPK5', NULL, 1320000.00, 198000.00, 1);
GO

-- 12. CHITIETHOADON (Mở rộng dữ liệu lịch sử)
INSERT INTO [dbo].[CHITIETHOADON] ([MaHD], [MaMonAn], [DonGiaBan], [SoLuong]) VALUES
-- 9 HD CŨ (Đã chuyển mã)
(N'HD2509050001', N'MA0005', 120000.00, 1), (N'HD2509050001', N'MA0006', 180000.00, 1),
(N'HD2509100001', N'MA0002', 320000.00, 2), (N'HD2509100001', N'MA0007', 150000.00, 4),
(N'HD2509150001', N'MA0001', 350000.00, 1), (N'HD2509150001', N'MA0013', 45000.00, 2), (N'HD2509150001', N'MA0014', 25000.00, 1),
(N'HD2509250001', N'MA0004', 300000.00, 1), (N'HD2509250001', N'MA0008', 160000.00, 2), (N'HD2509250001', N'MA0014', 25000.00, 3),
(N'HD2510250001', N'MA0001', 350000.00, 1), (N'HD2510250001', N'MA0005', 120000.00, 3), (N'HD2510250001', N'MA0014', 25000.00, 2),
(N'HD2510250002', N'MA0006', 180000.00, 5), (N'HD2510250002', N'MA0009', 140000.00, 5), (N'HD2510250002', N'MA0011', 85000.00, 4),
(N'HD2510280001', N'MA0007', 150000.00, 1), (N'HD2510280001', N'MA0010', 50000.00, 1), (N'HD2510280001', N'MA0002', 320000.00, 1),
(N'HD2510300001', N'MA0008', 160000.00, 3), (N'HD2510300001', N'MA0012', 60000.00, 2), (N'HD2510300001', N'MA0014', 25000.00, 4),
(N'HD2510300002', N'MA0003', 330000.00, 1), (N'HD2510300002', N'MA0007', 150000.00, 1), (N'HD2510300002', N'MA0010', 50000.00, 1), (N'HD2510300002', N'MA0014', 25000.00, 3),

(N'HD2509180001', N'MA0003', 330000.00, 1), (N'HD2509180001', N'MA0010', 50000.00, 2),
(N'HD2509280001', N'MA0006', 180000.00, 5), (N'HD2509280001', N'MA0009', 140000.00, 3),
(N'HD2510020001', N'MA0004', 300000.00, 1), (N'HD2510020001', N'MA0007', 150000.00, 3), (N'HD2510020001', N'MA0011', 85000.00, 1),
(N'HD2510070001', N'MA0002', 320000.00, 2), (N'HD2510070001', N'MA0011', 85000.00, 1), (N'HD2510070001', N'MA0014', 25000.00, 3),
(N'HD2510100001', N'MA0008', 160000.00, 3), (N'HD2510100001', N'MA0014', 25000.00, 4), (N'HD2510100001', N'MA0012', 60000.00, 1),
(N'HD2510140001', N'MA0001', 350000.00, 1), (N'HD2510140001', N'MA0012', 60000.00, 2),
(N'HD2510180001', N'MA0003', 330000.00, 1), (N'HD2510180001', N'MA0009', 140000.00, 2),
(N'HD2510200001', N'MA0006', 180000.00, 3), (N'HD2510200001', N'MA0011', 85000.00, 2), (N'HD2510200001', N'MA0013', 45000.00, 1),
(N'HD2510220001', N'MA0008', 160000.00, 2), (N'HD2510220001', N'MA0004', 300000.00, 1),
(N'HD2510240001', N'MA0005', 120000.00, 1), (N'HD2510240001', N'MA0014', 25000.00, 2),
(N'HD2510250003', N'MA0002', 320000.00, 2), (N'HD2510250003', N'MA0013', 45000.00, 1),
(N'HD2510280002', N'MA0007', 150000.00, 1), (N'HD2510280002', N'MA0010', 50000.00, 1),
(N'HD2510290001', N'MA0003', 330000.00, 1), (N'HD2510290001', N'MA0005', 120000.00, 2), (N'HD2510290001', N'MA0012', 60000.00, 1),
(N'HD2510300003', N'MA0010', 50000.00, 2), (N'HD2510300003', N'MA0012', 60000.00, 2),
(N'HD2510300004', N'MA0006', 180000.00, 5), (N'HD2510300004', N'MA0009', 140000.00, 3);
GO


-- =================================================================================
-- PHẦN 5: HOÀN TẤT
-- =================================================================================
USE [master]
GO
ALTER DATABASE [PTUD-JOJO-Restaurant] SET READ_WRITE 
GO