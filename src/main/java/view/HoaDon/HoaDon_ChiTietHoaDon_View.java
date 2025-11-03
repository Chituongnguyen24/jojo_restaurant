package view.HoaDon;

import dao.Ban_DAO; // SỬA: Thêm Ban_DAO
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.PhieuDatBan_DAO; // SỬA: Thêm PhieuDatBan_DAO
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.PhieuDatBan;
import enums.TrangThaiBan; // SỬA: Thêm Enum

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

public class HoaDon_ChiTietHoaDon_View extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private Ban_DAO banDAO; // SỬA: Thêm Ban_DAO
    private PhieuDatBan_DAO pbdDAO; // SỬA: Thêm PhieuDatBan_DAO

    // SỬA: Thêm các components
    private HoaDon hoaDonHienTai;
    private JButton btnThanhToan, btnDong;
    private JComboBox<String> cbxPhuongThucTT;

    // Các label hiển thị tổng tiền
    private JLabel lblTongTienMon, lblTongGiamGia, lblTongThue, lblTongThanhToan;
    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("###,### VNĐ", symbols);
    }

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi Tiết Hóa Đơn: " + hoaDon.getMaHD(), true);
        
        this.hoaDonHienTai = hoaDon; // SỬA: Lưu lại hóa đơn
        this.hoaDonDAO = new HoaDon_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.banDAO = new Ban_DAO(); // SỬA: Khởi tạo
        this.pbdDAO = new PhieuDatBan_DAO(); // SỬA: Khởi tạo

        setSize(1000, 750);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        
        // --- Main Panel ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHeader(hoaDon), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // --- Button Panel (SỬA: Thêm mới) ---
        add(createButtonPanel(), BorderLayout.SOUTH);

        // --- Load Data ---
        loadChiTietData(hoaDon.getMaHD());
        tinhVaHienThiTongTien(hoaDon);
        
        // --- Events (SỬA: Thêm sự kiện) ---
        btnDong.addActionListener(e -> dispose());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        
        // SỬA: Vô hiệu hóa nút thanh toán nếu đã thanh toán rồi
        if (hoaDon.isDaThanhToan()) {
            btnThanhToan.setEnabled(false);
            btnThanhToan.setText("Đã Thanh Toán");
            cbxPhuongThucTT.setEnabled(false);
            if (hoaDon.getPhuongThucThanhToan() != null) {
                cbxPhuongThucTT.setSelectedItem(hoaDon.getPhuongThucThanhToan());
            }
        }
    }
    
    // ===== SỬA: HÀM MỚI (Tạo Button Panel) =====
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
        btnDong.setPreferredSize(new Dimension(120, 40));

        btnThanhToan = new JButton("Xác nhận Thanh toán");
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThanhToan.setBackground(new Color(34, 197, 94));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setPreferredSize(new Dimension(200, 40));

        rightPanel.add(btnDong);
        rightPanel.add(btnThanhToan);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);

        return panel;
    }

 // Sửa trong HoaDon_ChiTietHoaDon_View.java - Method xuLyThanhToan() (Set trạng thái PDB thành "Hoàn thành")

    private void xuLyThanhToan() {
        String phuongThuc = (String) cbxPhuongThucTT.getSelectedItem();
        if (phuongThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
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

            // 1. Cập nhật HD
            boolean successHD = hoaDonDAO.thanhToanHoaDon(hoaDonHienTai.getMaHD(), phuongThuc);
            if (!successHD) {
                throw new SQLException("Thanh toán HD thất bại");
            }

            // 2. Cập nhật bàn
            if (hoaDonHienTai.getBan() != null) {
                boolean successBan = banDAO.capNhatTrangThaiBan(hoaDonHienTai.getBan().getMaBan(), TrangThaiBan.TRONG);
                if (!successBan) {
                    throw new SQLException("Cập nhật trạng thái bàn thất bại");
                }
            }

            // 3. Cập nhật PDB thành "Hoàn thành"
            PhieuDatBan pdb = hoaDonHienTai.getPhieuDatBan();
            if (pdb != null) {
                PhieuDatBan pdbFull = pbdDAO.getPhieuDatBanById(pdb.getMaPhieu());
                if (pdbFull != null) {
                    pdbFull.setTrangThaiPhieu("Hoàn thành");  // SỬA: Set trạng thái mới (khớp CHECK)
                    pdbFull.setThoiGianTraBan(LocalDateTime.now());
                    System.out.println("Debug: Cập nhật PDB " + pdbFull.getMaPhieu() + " sang Hoàn thành");
                    boolean successPDB = pbdDAO.updatePhieuDatBan(pdbFull);
                    if (!successPDB) {
                        throw new SQLException("Cập nhật PDB thất bại");
                    }
                } else {
                    throw new SQLException("Không tìm thấy PDB liên kết");
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Hóa đơn đã hoàn tất từ phiếu đặt.");
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


    // ===== CÁC HÀM HIỂN THỊ (Giữ nguyên, chỉ sửa 1 chút) =====
    
    private JPanel createHeader(HoaDon hoaDon) {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // ... (Thông tin hóa đơn)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        infoPanel.add(createHeaderLabel("Mã HĐ: " + hoaDon.getMaHD()));
        infoPanel.add(createHeaderLabel("Giờ vào: " + hoaDon.getGioVao()));
        infoPanel.add(createHeaderLabel("Bàn: " + (hoaDon.getBan() != null ? hoaDon.getBan().getMaBan() : "N/A")));

        // ... (Thông tin khách hàng)
        JPanel khachPanel = new JPanel();
        khachPanel.setLayout(new BoxLayout(khachPanel, BoxLayout.Y_AXIS));
        khachPanel.setOpaque(false);
        
        KhachHang kh = khachHangDAO.getKhachHangById(hoaDon.getKhachHang().getMaKH());
        String tenKH = "Khách vãng lai";
        String sdtKH = "N/A";
        if (kh != null && !"KH00000000".equals(kh.getMaKH())) {  // SỬA: So sánh đúng
            tenKH = kh.getTenKH();
            sdtKH = kh.getSoDienThoai();
        } else if (kh == null) {
            System.err.println("Lỗi: KH " + hoaDon.getKhachHang().getMaKH() + " không tồn tại trong DB!");
        }

        khachPanel.add(createHeaderLabel("Khách hàng: " + tenKH));
        khachPanel.add(createHeaderLabel("SĐT: " + sdtKH));
        khachPanel.add(createHeaderLabel("Nhân viên: " + (hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getMaNhanVien() : "N/A")));

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(khachPanel, BorderLayout.CENTER);
        return panel;
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

        // ... (Custom renderers cho bảng)
        CurrencyRenderer currencyRenderer = new CurrencyRenderer(CURRENCY_FORMAT);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // SL

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

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(15, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;

        // ... (Labels)
        gbc.gridx = 0; gbc.gridy = 0; footer.add(createTotalLabel("Tổng tiền món:", false), gbc);
        gbc.gridx = 0; gbc.gridy = 1; footer.add(createTotalLabel("Tổng giảm giá (KM):", false), gbc);
        gbc.gridx = 0; gbc.gridy = 2; footer.add(createTotalLabel("Thuế & Phí dịch vụ:", false), gbc);
        gbc.gridx = 0; gbc.gridy = 3; footer.add(createTotalLabel("TỔNG THANH TOÁN:", true), gbc);

        // ... (Values)
        gbc.anchor = GridBagConstraints.LINE_END; // Căn phải
        lblTongTienMon = createTotalLabel("0 VNĐ", false);
        lblTongGiamGia = createTotalLabel("0 VNĐ", false);
        lblTongThue = createTotalLabel("0 VNĐ", false);
        lblTongThanhToan = createTotalLabel("0 VNĐ", true);

        gbc.gridx = 1; gbc.gridy = 0; footer.add(lblTongTienMon, gbc);
        gbc.gridx = 1; gbc.gridy = 1; footer.add(lblTongGiamGia, gbc);
        gbc.gridx = 1; gbc.gridy = 2; footer.add(lblTongThue, gbc);
        gbc.gridx = 1; gbc.gridy = 3; footer.add(lblTongThanhToan, gbc);
        
        // Filler
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 1.0;
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
        // Dùng hàm lấy chi tiết cho việc in (có tên món)
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(maHoaDon);
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietList) {
            double donGia = ct.getDonGiaBan();
            double thanhTien = ct.getSoLuong() * donGia;

            modelChiTiet.addRow(new Object[]{
                    stt++,
                    ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "N/A",
                    ct.getSoLuong(),
                    donGia, // Render sẽ format
                    thanhTien // Render sẽ format
            });
        }
    }
    
    // ===== HÀM ĐÃ SỬA (Lấy logic tính tiền từ DAO) =====
    private void tinhVaHienThiTongTien(HoaDon hoaDon) {
        // Lấy tổng tiền món (đã có sẵn trong HĐ khi tạo)
        double tongTienMon = hoaDon.getTongTienTruocThue();
        double tongGiamGia = hoaDon.getTongGiamGia();

        // SỬA: Gọi hàm DAO để tính tổng cuối cùng (bao gồm thuế)
        double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDon.getMaHD());
        
        // Tính tiền thuế = Tổng cuối - (Tiền món - Tiền giảm)
        double tienThueVaPhi = tongThanhToan - (tongTienMon - tongGiamGia);

        // Hiển thị
        lblTongTienMon.setText(CURRENCY_FORMAT.format(tongTienMon));
        lblTongGiamGia.setText(CURRENCY_FORMAT.format(tongGiamGia));
        lblTongThue.setText(CURRENCY_FORMAT.format(tienThueVaPhi));
        lblTongThanhToan.setText(CURRENCY_FORMAT.format(tongThanhToan));
    }

    // ===== CLASS HỖ TRỢ RENDER TIỀN TỆ (Giữ nguyên) =====
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
}