package view.HoaDon;

import dao.Ban_DAO; 
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.PhieuDatBan_DAO; 
import dao.KhuyenMai_DAO; 
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai; 
import entity.PhieuDatBan;
import enums.TrangThaiBan; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HoaDon_ChiTietHoaDon_View extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO pbdDAO; 
    private KhuyenMai_DAO khuyenMaiDAO; 

    private HoaDon hoaDonHienTai;
    private JButton btnThanhToan, btnDong;
    
    private JComboBox<KhachHang> cbxKhachHang; 
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    
    private JComboBox<String> cbxPhuongThucTT;

    private JLabel lblTongTienMon, lblTongGiamGia, lblTongThue, lblTongThanhToan;
    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("###,### VNĐ", symbols);
    }

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi Tiết Hóa Đơn: " + hoaDon.getMaHD(), true);
        
        this.hoaDonHienTai = hoaDon;
        this.hoaDonDAO = new HoaDon_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.banDAO = new Ban_DAO();
        this.pbdDAO = new PhieuDatBan_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO(); 

        setSize(1000, 750);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        mainPanel.add(createHeader(hoaDon, khachHangDAO), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(hoaDon, khuyenMaiDAO), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);

        // === SỬA LỖI: Gắn sự kiện (Listener) SAU KHI TẤT CẢ components đã được khởi tạo ===
        cbxKhachHang.addActionListener(e -> capNhatGiaTienXemTruoc());
        cbxKhuyenMai.addActionListener(e -> capNhatGiaTienXemTruoc());
        // === KẾT THÚC SỬA LỖI ===

        loadChiTietData(hoaDon.getMaHD());
        tinhVaHienThiTongTien(hoaDon);
        
        btnDong.addActionListener(e -> dispose());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        
        if (hoaDon.isDaThanhToan()) {
            btnThanhToan.setEnabled(false);
            btnThanhToan.setText("Đã Thanh Toán");
            cbxPhuongThucTT.setEnabled(false);
            
            cbxKhachHang.setEnabled(false);
            cbxKhuyenMai.setEnabled(false);
            
            if (hoaDon.getPhuongThucThanhToan() != null) {
                cbxPhuongThucTT.setSelectedItem(hoaDon.getPhuongThucThanhToan());
            }
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(0, 20, 15, 20));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        JLabel lblPTTT = new JLabel("Phương thức thanh toán:");
        lblPTTT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbxPhuongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cbxPhuongThucTT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        leftPanel.add(lblPTTT);
        leftPanel.add(cbxPhuongThucTT);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDong.setBackground(Color.RED);
        btnDong.setPreferredSize(new Dimension(120, 40));

        btnThanhToan = new JButton("Xác nhận Thanh toán");
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThanhToan.setBackground(Color.GREEN);
        btnThanhToan.setPreferredSize(new Dimension(200, 40));

        
        rightPanel.add(btnDong);
        rightPanel.add(btnThanhToan);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }


    private void xuLyThanhToan() {
        
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHD());
        if (chiTietList == null || chiTietList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không có món ăn để tạo hóa đơn", 
                "Hóa Đơn Rỗng", 
                JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        String phuongThuc = (String) cbxPhuongThucTT.getSelectedItem();
        if (phuongThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang khachHangMoi = (KhachHang) cbxKhachHang.getSelectedItem();
        KhuyenMai khuyenMaiMoi = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        
        KhachHang khachHangCu = hoaDonHienTai.getKhachHang();
        KhuyenMai khuyenMaiCu = hoaDonHienTai.getKhuyenMai();

        boolean canUpdate = false;
        
        if (khachHangMoi != null && !Objects.equals(khachHangMoi.getMaKH(), khachHangCu.getMaKH())) {
            canUpdate = true;
        }
        
        String maKMCu = (khuyenMaiCu != null) ? khuyenMaiCu.getMaKM() : null;
        String maKMMoi = (khuyenMaiMoi != null) ? khuyenMaiMoi.getMaKM() : null;
        if (maKMMoi == null && maKMCu != null && maKMCu.equals("KM00000000")) {
             // Bỏ qua (Do "Không áp dụng" có mã null hoặc KM00000000)
        } else if (!Objects.equals(maKMMoi, maKMCu)) {
            canUpdate = true;
        }

        if (canUpdate) {
            int confirmUpdate = JOptionPane.showConfirmDialog(this, 
                "Phát hiện thay đổi thông tin Khách hàng/Khuyến mãi.\nBạn có muốn lưu các thay đổi này trước khi thanh toán?", 
                "Xác nhận cập nhật", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmUpdate == JOptionPane.YES_OPTION) {
                boolean updateOK = hoaDonDAO.updateKhachHangVaKhuyenMai(
                    hoaDonHienTai.getMaHD(), 
                    khachHangMoi.getMaKH(), 
                    maKMMoi
                );
                
                if (updateOK) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                    this.hoaDonHienTai = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
                    tinhVaHienThiTongTien(this.hoaDonHienTai);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận thanh toán cho hóa đơn " + hoaDonHienTai.getMaHD() + "?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            boolean successHD = hoaDonDAO.thanhToanHoaDon(hoaDonHienTai.getMaHD(), phuongThuc);
            if (!successHD) {
                throw new SQLException("Thanh toán HD thất bại");
            }

            if (hoaDonHienTai.getBan() != null) {
                boolean successBan = banDAO.capNhatTrangThaiBan(hoaDonHienTai.getBan().getMaBan(), TrangThaiBan.TRONG);
                if (!successBan) {
                    throw new SQLException("Cập nhật trạng thái bàn thất bại");
                }
            }

            PhieuDatBan pdb = hoaDonHienTai.getPhieuDatBan();
            if (pdb != null) {
                PhieuDatBan pdbFull = pbdDAO.getPhieuDatBanById(pdb.getMaPhieu());
                if (pdbFull != null) {
                    pdbFull.setTrangThaiPhieu("Hoàn thành");
                    pdbFull.setThoiGianTraBan(LocalDateTime.now());
                    boolean successPDB = pbdDAO.updatePhieuDatBan(pdbFull);
                    if (!successPDB) {
                        throw new SQLException("Cập nhật PDB thất bại");
                    }
                } else {
                    throw new SQLException("Không tìm thấy PDB liên kết");
                }
            }

            conn.commit();
            
            try {
                KhachHang kh = hoaDonHienTai.getKhachHang();
                
                if (kh != null && !kh.getMaKH().trim().equalsIgnoreCase("KH00000000")) {
                    
                    double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDonHienTai.getMaHD());
                    
                    int diemCongThem = (int) (tongThanhToan / 10000); 
                    
                    if (diemCongThem > 0) {
                        boolean congDiemOK = khachHangDAO.congDiemTichLuy(kh.getMaKH(), diemCongThem);
                        if (congDiemOK) {
                            System.out.println("Debug: Đã cộng " + diemCongThem + " điểm cho KH " + kh.getMaKH());
                        } else {
                             System.err.println("Lỗi: Không cộng được điểm cho KH " + kh.getMaKH());
                        }
                    }
                }
            } catch (Exception e_diem) {
                System.err.println("Lỗi ngoài lề: Không thể cộng điểm tích lũy. " + e_diem.getMessage());
            }
            
            try {
                Frame owner = (Frame) this.getOwner();
                
                HoaDon hoaDonDaThanhToan = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
                
                HoaDon_Printer.showPreview(owner, hoaDonDaThanhToan, chiTietList); 
                
            } catch (Exception e_print) {
                e_print.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi mở bản xem trước: " + e_print.getMessage());
            }
            
            
            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Hóa đơn đã hoàn tất.");
            dispose();
            
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage() + "\n(Kiểm tra console để debug)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


    private JPanel createHeader(HoaDon hoaDon, KhachHang_DAO khachHangDAO) {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        infoPanel.add(createHeaderLabel("Mã HĐ: " + hoaDon.getMaHD()));
        infoPanel.add(createHeaderLabel("Giờ vào: " + hoaDon.getGioVao()));
        infoPanel.add(createHeaderLabel("Bàn: " + (hoaDon.getBan() != null ? hoaDon.getBan().getMaBan() : "N/A")));

        JPanel khachPanel = new JPanel();
        khachPanel.setLayout(new BoxLayout(khachPanel, BoxLayout.Y_AXIS));
        khachPanel.setOpaque(false);
        
        cbxKhachHang = new JComboBox<>();
        cbxKhachHang.setRenderer(new KhachHangRenderer()); 
        cbxKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxKhachHang.setPreferredSize(new Dimension(220, 30));
        
        // === SỬA LỖI: Xóa Listener ở đây ===
        // cbxKhachHang.addActionListener(e -> capNhatGiaTienXemTruoc());
        
        
        KhachHang khachHangDaChon = null;
        try {
            List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();
            for (KhachHang kh : dsKH) {
                cbxKhachHang.addItem(kh);
                if (kh.getMaKH().equals(hoaDon.getKhachHang().getMaKH())) {
                    khachHangDaChon = kh;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (khachHangDaChon != null) {
            cbxKhachHang.setSelectedItem(khachHangDaChon); 
        } else {
            cbxKhachHang.setSelectedIndex(0); // Mặc định chọn khách lẻ
        }
        
        khachPanel.add(createHeaderLabel("Khách hàng:"));
        khachPanel.add(cbxKhachHang);
        khachPanel.add(Box.createVerticalStrut(5)); 
        khachPanel.add(createHeaderLabel("Nhân viên: " + (hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getMaNhanVien() : "N/A")));

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(khachPanel, BorderLayout.CENTER);
        return panel;
    }


    private void capNhatGiaTienXemTruoc() {
        // Kiểm tra null (an toàn)
        if (cbxKhuyenMai == null || lblTongTienMon == null) {
            return;
        }
    
    	double tongTienMon = hoaDonHienTai.getTongTienTruocThue();
    	
    	KhuyenMai kmDuocChon = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        double tienGiamMoi = 0.0;
        
        if (kmDuocChon != null && kmDuocChon.getMaKM() != null && !kmDuocChon.getMaKM().equalsIgnoreCase("KM00000000")) {
            tienGiamMoi = tongTienMon * kmDuocChon.getMucKM();
            if (tienGiamMoi > tongTienMon) {
                tienGiamMoi = tongTienMon;
            }
        }
        
        double tienSauGiamGia = tongTienMon - tienGiamMoi;
        if (tienSauGiamGia < 0) tienSauGiamGia = 0;
        
        // === SỬA LỖI: Tính toán thuế & phí áp cứng ===
        double tyLePhiDichVu = 0.05; // 5%
        double tyLeVAT = 0.08;       // 8%

        double tienPhiDichVu = tienSauGiamGia * tyLePhiDichVu;
        double soTienDeTinhVAT = tienSauGiamGia + tienPhiDichVu;
        double tienVAT = soTienDeTinhVAT * tyLeVAT;
        
        double tongTienPhaiTra = tienSauGiamGia + tienPhiDichVu + tienVAT;
        double tongThueVaPhi = tienPhiDichVu + tienVAT;
        
        lblTongTienMon.setText(CURRENCY_FORMAT.format(tongTienMon));
        lblTongGiamGia.setText(CURRENCY_FORMAT.format(tienGiamMoi));
        lblTongThue.setText(CURRENCY_FORMAT.format(tongThueVaPhi)); // Cập nhật tiền thuế
        lblTongThanhToan.setText(CURRENCY_FORMAT.format(Math.round(tongTienPhaiTra)));
	}

	private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 116, 139));
        label.setBorder(new EmptyBorder(2, 0, 2, 0));
        return label;
    }

    private JScrollPane createTablePanel() {
        modelChiTiet = new DefaultTableModel(
                new String[]{"STT", "Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(40);
        tableChiTiet.setGridColor(new Color(226, 232, 240));

        CurrencyRenderer currencyRenderer = new CurrencyRenderer(CURRENCY_FORMAT);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 

        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(350);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(50);
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(226, 232, 240)));
        return scrollPane;
    }

    private JPanel createFooterPanel(HoaDon hoaDon, KhuyenMai_DAO khuyenMaiDAO) {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(15, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;

        cbxKhuyenMai = new JComboBox<>();
        cbxKhuyenMai.setRenderer(new KhuyenMaiRenderer()); 
        cbxKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // === SỬA LỖI: Xóa Listener ở đây ===
        // cbxKhuyenMai.addActionListener(e -> capNhatGiaTienXemTruoc());
        
        KhuyenMai kmDaChon = null;
        try {
            List<KhuyenMai> dsKM = khuyenMaiDAO.getAllActiveKhuyenMai(); 
            for (KhuyenMai km : dsKM) {
                cbxKhuyenMai.addItem(km);
                if (hoaDon.getKhuyenMai() != null && hoaDon.getKhuyenMai().getMaKM() != null &&
                    km.getMaKM() != null && km.getMaKM().equals(hoaDon.getKhuyenMai().getMaKM())) {
                    kmDaChon = km;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (kmDaChon != null) {
            cbxKhuyenMai.setSelectedItem(kmDaChon);
        } else {
            cbxKhuyenMai.setSelectedIndex(0); 
        }
        
        gbc.gridx = 0; gbc.gridy = 0; footer.add(createTotalLabel("Áp dụng Khuyến mãi:", false), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START; footer.add(cbxKhuyenMai, gbc);
        gbc.anchor = GridBagConstraints.EAST; 

        gbc.gridx = 0; gbc.gridy = 1; footer.add(createTotalLabel("Tổng tiền món:", false), gbc);
        gbc.gridx = 0; gbc.gridy = 2; footer.add(createTotalLabel("Tổng giảm giá (KM):", false), gbc);
        
        // === SỬA: Đổi nhãn thuế cho thẩm mỹ ===
        gbc.gridx = 0; gbc.gridy = 3; footer.add(createTotalLabel("Thuế & Phí (VAT, P.vụ):", false), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; footer.add(createTotalLabel("THÀNH TIỀN (Tổng phải trả):", true), gbc);

        gbc.anchor = GridBagConstraints.LINE_END; 
        lblTongTienMon = createTotalLabel("0 VNĐ", false);
        lblTongGiamGia = createTotalLabel("0 VNĐ", false);
        lblTongThue = createTotalLabel("0 VNĐ", false);
        lblTongThanhToan = createTotalLabel("0 VNĐ", true);

        gbc.gridx = 1; gbc.gridy = 1; footer.add(lblTongTienMon, gbc);
        gbc.gridx = 1; gbc.gridy = 2; footer.add(lblTongGiamGia, gbc);
        gbc.gridx = 1; gbc.gridy = 3; footer.add(lblTongThue, gbc);
        gbc.gridx = 1; gbc.gridy = 4; footer.add(lblTongThanhToan, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 1.0;
        footer.add(Box.createHorizontalStrut(1), gbc);

        return footer;
    }

    private JLabel createTotalLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        if (isBold) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setForeground(new Color(239, 68, 68));
        } else {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(new Color(51, 65, 85));
        }
        label.setBorder(new EmptyBorder(3, 10, 3, 10));
        return label;
    }

    private void loadChiTietData(String maHoaDon) {
        modelChiTiet.setRowCount(0);
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(maHoaDon);
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietList) {
            double donGia = ct.getDonGiaBan();
            double thanhTien = ct.getSoLuong() * donGia;

            modelChiTiet.addRow(new Object[]{
                    stt++,
                    ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "N/A",
                    ct.getSoLuong(),
                    donGia, 
                    thanhTien 
            });
        }
    }
    
    private void tinhVaHienThiTongTien(HoaDon hoaDon) {
        
        double tongTienMon = hoaDon.getTongTienTruocThue();
        double tongGiamGia = hoaDon.getTongGiamGia();

        double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDon.getMaHD());
        
        // === SỬA LỖI: Cập nhật lại logic tính thuế hiển thị ===
        // (Logic này phải khớp với logic xem trước)
        double tienSauGiamGia = tongTienMon - tongGiamGia;
        if (tienSauGiamGia < 0) tienSauGiamGia = 0;
        
        double tyLePhiDichVu = 0.05; // 5%
        double tyLeVAT = 0.08;       // 8%
        double tienPhiDichVu = tienSauGiamGia * tyLePhiDichVu;
        double soTienDeTinhVAT = tienSauGiamGia + tienPhiDichVu;
        double tienVAT = soTienDeTinhVAT * tyLeVAT;
        double tongThueVaPhi = tienPhiDichVu + tienVAT;

        lblTongTienMon.setText(CURRENCY_FORMAT.format(tongTienMon));
        lblTongGiamGia.setText(CURRENCY_FORMAT.format(tongGiamGia));
        // Hiển thị tổng thuế (VAT + Phí)
        lblTongThue.setText(CURRENCY_FORMAT.format(tongThueVaPhi)); 
        // Hiển thị tổng tiền cuối cùng (đã làm tròn trong DAO)
        lblTongThanhToan.setText(CURRENCY_FORMAT.format(tongThanhToan));
    }

    static class CurrencyRenderer extends DefaultTableCellRenderer {
        private DecimalFormat formatter;
        public CurrencyRenderer(DecimalFormat formatter) {
            this.formatter = formatter;
            setHorizontalAlignment(JLabel.RIGHT);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                setText(formatter.format(value));
            }
            if (isSelected) {
                cell.setBackground(table.getSelectionBackground());
                cell.setForeground(table.getSelectionForeground());
            } else {
                cell.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                cell.setForeground(table.getForeground());
            }
            return cell;
        }
    }
    
    class KhachHangRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof KhachHang) {
                KhachHang kh = (KhachHang) value;
                setText(kh.getTenKH() + " (" + kh.getMaKH().trim() + ")");
            }
            return this;
        }
    }
    
    class KhuyenMaiRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof KhuyenMai) {
                KhuyenMai km = (KhuyenMai) value;
                if (km.getMaKM() == null || km.getMaKM().trim().isEmpty() || km.getMaKM().trim().equalsIgnoreCase("KM00000000")) {
                     setText("Không áp dụng");
                } else {
                     setText(km.getMoTa());
                }
            }
            return this;
        }
    }
}