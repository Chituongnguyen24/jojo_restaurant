package view.HoaDon;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.KhuyenMai_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;

public class HoaDon_View extends JPanel implements ActionListener {
    
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private KhuyenMai_DAO khuyenMaiDAO= new KhuyenMai_DAO();
    
    private JTable tblDanhSachHD;
    private DefaultTableModel modelDanhSachHD;
    
    private JTextField txtTimNhanh;

    private JButton btnIn, btnXemChiTiet; 
    
    private HoaDon hoaDonDuocChon = null;
    
    private JLabel lblTongHD, lblDoanhThu;

    // SỬA: Định dạng ngày giờ trong Entity mới
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);


    private static final Color COLOR_TONG_HD = new Color(34, 139, 230);
    private static final Color COLOR_DA_TT = new Color(76, 175, 80);
    private static final Color COLOR_DOANH_THU = new Color(255, 152, 0);

    public HoaDon_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);

        ganSuKien();
         
        JPanel pnlHeader = taoPanelHeader();
        
        JPanel pnlThongKe = taoPanelThongKe();
        JPanel pnlTopWrapper = new JPanel(new BorderLayout());
        pnlTopWrapper.setOpaque(false);
        pnlTopWrapper.add(pnlHeader, BorderLayout.NORTH);
        pnlTopWrapper.add(pnlThongKe, BorderLayout.CENTER);
        add(pnlTopWrapper, BorderLayout.NORTH);
        
        JPanel pnlTable = taoPanelTimKiemVaDanhSach();
        add(pnlTable, BorderLayout.CENTER); 
         
        loadHoaDon();
        loadThongKe();
         
        xoaRong();
    }
     
    private void ganSuKien() {
        btnIn = new RoundedButton("In hóa đơn", new Color(76, 175, 80), COLOR_WHITE); 
        btnXemChiTiet = new RoundedButton("Xem Chi Tiết Món", new Color(255, 152, 0), COLOR_WHITE);

        btnIn.addActionListener(this);
        btnXemChiTiet.addActionListener(this); 
    }
     
    private JPanel taoPanelHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout(20, 0));
        panelHeader.setBackground(BG_VIEW);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
         
        JLabel lblTitle = new JLabel("Quản lý hóa đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 60, 60));
        panelHeader.add(lblTitle, BorderLayout.WEST);
         
        btnIn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelHeader.add(btnIn, BorderLayout.EAST);
         
        return panelHeader;
    }
    
    private JPanel taoPanelThongKe() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 30, 25, 30)); 

        lblTongHD = createStatLabel("0");
        lblDoanhThu = createStatLabel("0 VNĐ");

        statsPanel.add(createStatBox(lblTongHD, "Tổng hóa đơn", COLOR_TONG_HD));
        statsPanel.add(createStatBox(lblDoanhThu, "Doanh thu", COLOR_DOANH_THU));
         
        return statsPanel;
    }
     
    private JPanel taoPanelTimKiemVaDanhSach() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 20, 20, 20)); 

        JPanel pnlTopControl = taoPanelTimKiem(); 
         
        // CÁC CỘT MỚI: MaHD, NgayLapHoaDon, GioVao, MaBan, MaPhieu, KhachHang, TongTien, TrangThai
        String[] columns = {"Mã HD", "Ngày Lập", "Giờ Vào", "Mã Bàn", "P. Đặt Bàn", "Khách Hàng", "Tổng Tiền", "Trạng Thái"};
        modelDanhSachHD = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblDanhSachHD = createStyledTable(modelDanhSachHD);
        tblDanhSachHD.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblDanhSachHD.getSelectedRow();
                if (row != -1) {
                    String maHD = modelDanhSachHD.getValueAt(row, 0).toString();
                    hoaDonDuocChon = hoaDonDAO.findByMaHD(maHD);
                    capNhatTrangThaiNut(); 
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblDanhSachHD);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(new LineBorder(MAU_VIEN, 1));
        scroll.setBackground(COLOR_WHITE);
        scroll.getViewport().setBackground(COLOR_WHITE);

        pnlWrapper.add(pnlTopControl, BorderLayout.NORTH); 
        pnlWrapper.add(scroll, BorderLayout.CENTER); 
        
        return pnlWrapper;
    }
     
    private JPanel taoPanelTimKiem() {
        JPanel pnlTimKiemWrapper = new RoundedPanel(15, COLOR_WHITE, new BorderLayout(10, 10));
        pnlTimKiemWrapper.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlSearch.setOpaque(false);
        
        txtTimNhanh = new JTextField(15);
        txtTimNhanh.setFont(FONT_CHU);
        txtTimNhanh.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MAU_VIEN, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
         
        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtTimNhanh);
        
        ActionListener filterAction = e -> loadHoaDon();
        txtTimNhanh.addActionListener(filterAction);
        
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlButton.setOpaque(false);
        
        pnlButton.add(btnXemChiTiet);
        
        pnlTimKiemWrapper.add(pnlSearch, BorderLayout.WEST);
        pnlTimKiemWrapper.add(pnlButton, BorderLayout.EAST);
        
        return pnlTimKiemWrapper;
    }
     
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_CHU);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(new Color(0, 0, 0)); // Thêm màu chữ đen khi chọn
        table.setGridColor(MAU_VIEN);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(COLOR_TITLE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Cột Trạng Thái (cột cuối cùng)
        table.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(COLOR_DA_TT);
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        // Căn giữa Mã Bàn và PDB
        int maBanCol = 3; 
        int maPDBCol = 4;
        table.getColumnModel().getColumn(maBanCol).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(maPDBCol).setCellRenderer(centerRenderer);
        
        return table;
    }
     
    private void xoaRong() {
        hoaDonDuocChon = null;
        capNhatTrangThaiNut();
    }

    private void capNhatTrangThaiNut() {
        boolean selected = (hoaDonDuocChon != null);
        
        btnXemChiTiet.setEnabled(selected);
    }
    
    public void loadHoaDon() {
        modelDanhSachHD.setRowCount(0);
        String keyword = "";
        if (txtTimNhanh != null && txtTimNhanh.getText() != null) {
            keyword = txtTimNhanh.getText().trim().toLowerCase();
        }
        
        // Lấy Map hóa đơn
        Map<HoaDon, Object[]> hoaDonMap = hoaDonDAO.getHoaDonWithDetailsForView();
        
        // Biến để tính lại tổng doanh thu thực tế
        BigDecimal totalDoanhThu = BigDecimal.ZERO; 
        int countHD = 0;

        for (Map.Entry<HoaDon, Object[]> entry : hoaDonMap.entrySet()) {
            HoaDon hd = entry.getKey();
            Object[] details = entry.getValue(); 
            
            String tenKH = (String) details[0];
            // KHÔNG DÙNG details[1] VÌ NÓ CHỨA CẢ TIỀN MÓN HỦY
            // double tongTienMon = (double) details[1]; 
            
            String maHD = (hd != null && hd.getMaHD() != null) ? hd.getMaHD() : "Lỗi-MaHD";

            boolean matchKeyword = keyword.isEmpty() 
                || maHD.toLowerCase().contains(keyword) 
                || tenKH.toLowerCase().contains(keyword);
            
            if (matchKeyword) {
                // --- SỬA LỖI: TÍNH LẠI TỔNG TIỀN MÓN TỪ DANH SÁCH HỢP LỆ ---
                // Gọi hàm getChiTietHienTaiChoIn vì hàm này (như file trước bạn gửi) 
                // đã lọc bỏ món hủy để in hóa đơn.
                List<ChiTietHoaDon> dsChiTiet = hoaDonDAO.getChiTietHienTaiChoIn(maHD);
                
                double tongTienMon = 0;
                if (dsChiTiet != null) {
                    for (ChiTietHoaDon ct : dsChiTiet) {
                        tongTienMon += ct.getSoLuong() * ct.getDonGiaBan();
                    }
                }
                // -----------------------------------------------------------

                // --- BẮT ĐẦU TÍNH TOÁN THUẾ PHÍ (Logic cũ) ---
                
                // 1. Lấy mức khuyến mãi
                double tiLeKM = 0;
                if (hd.getKhuyenMai() != null) {
                    String maKM = hd.getKhuyenMai().getMaKM();
                    if (maKM != null && !maKM.trim().equals("KM00000000")) {
                        try {
                            KhuyenMai kmFull = khuyenMaiDAO.getKhuyenMaiById(maKM);
                            if (kmFull != null) {
                                tiLeKM = kmFull.getMucKM();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // 2. Tính giảm giá
                BigDecimal bdTong = BigDecimal.valueOf(tongTienMon);
                BigDecimal bdGiam = bdTong.multiply(BigDecimal.valueOf(tiLeKM)).setScale(0, RoundingMode.HALF_UP);
                
                // 3. Tính sau giảm
                BigDecimal bdSauGiam = bdTong.subtract(bdGiam);
                if (bdSauGiam.compareTo(BigDecimal.ZERO) < 0) bdSauGiam = BigDecimal.ZERO;

                // 4. Tính Phí Dịch Vụ (5%)
                BigDecimal bdPhi = bdSauGiam.multiply(BigDecimal.valueOf(0.05)).setScale(0, RoundingMode.HALF_UP);
                
                // 5. Tính VAT (8%) - Dựa trên (Sau giảm + Phí)
                BigDecimal bdVAT = bdSauGiam.add(bdPhi).multiply(BigDecimal.valueOf(0.08)).setScale(0, RoundingMode.HALF_UP);
                
                // 6. Tổng thanh toán cuối cùng
                BigDecimal bdThanhToan = bdSauGiam.add(bdPhi).add(bdVAT).setScale(0, RoundingMode.HALF_UP);
                
                // --- KẾT THÚC TÍNH TOÁN ---

                // Cộng dồn doanh thu
                totalDoanhThu = totalDoanhThu.add(bdThanhToan);
                countHD++;

                String maBan = (hd.getBan() != null && hd.getBan().getMaBan() != null) ? hd.getBan().getMaBan().trim() : "N/A";
                String maPDB = (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null) ? hd.getPhieuDatBan().getMaPhieu().trim() : "Không";
                
                modelDanhSachHD.addRow(new Object[]{
                    maHD,
                    hd.getNgayLapHoaDon().format(DATE_FORMATTER),
                    hd.getGioVao().format(TIME_FORMATTER),
                    maBan, 
                    maPDB, 
                    tenKH, 
                    String.format("%,.0f₫", bdThanhToan.doubleValue()), // Hiển thị số tiền chuẩn
                    "Đã thanh toán"
                });
            }
        }
        
        // Cập nhật lại label thống kê
        lblTongHD.setText(String.valueOf(countHD));
        lblDoanhThu.setText(String.format("%,.0f VNĐ", totalDoanhThu.doubleValue()));
    }
    
    private void loadThongKe() {
        // SỬ DỤNG PHƯƠNG THỨC MỚI TỐI ƯU - Chỉ 1 query thay vì N queries
        double[] stats = hoaDonDAO.getThongKeNhanh();
        int tongSoHD = (int) stats[0];
        double doanhThu = stats[1];
        
        lblTongHD.setText(String.valueOf(tongSoHD));
        lblDoanhThu.setText(String.format("%,.0f VNĐ", doanhThu));
    }
     
    private void xuLyXemChiTiet() {
        if (hoaDonDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        HoaDon hd = hoaDonDAO.findByMaHD(hoaDonDuocChon.getMaHD());
        if (hd != null) {
             HoaDon_ChiTietHoaDon_View dialog = new HoaDon_ChiTietHoaDon_View(parentFrame, hd); 
             dialog.setVisible(true);
        } else {
             JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết hóa đơn này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
    	Object o = e.getSource();
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this); 
         
        if (o == btnIn) {
            if (hoaDonDuocChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                HoaDon hd = hoaDonDuocChon;

                // SỬ DỤNG PHƯƠNG THỨC MỚI – ĐẢM BẢO DỮ LIỆU CHÍNH XÁC NHẤT
                List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHienTaiChoIn(hd.getMaHD());

                if (chiTietList == null || chiTietList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Hóa đơn này không có món ăn để in!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                HoaDon_Printer.showPreview(parentFrame, hd, chiTietList);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi chuẩn bị in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (o == btnXemChiTiet) { 
            xuLyXemChiTiet();
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        private Color borderColor = new Color(220, 220, 220);
        private Color bgColor;

        public RoundedPanel(int radius, Color color, LayoutManager layout) {
            super(layout);
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g); 
        }
         
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor); 
            g2.setStroke(new BasicStroke(1)); 
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    private class RoundedButton extends JButton {
        private int cornerRadius = 20;
        private Color bg;
        private Color fgColor; // Lưu màu chữ gốc

        public RoundedButton(String text, Color bgColor, Color fg) {
            super(text);
            this.bg = bgColor;
            this.fgColor = fg; // Lưu lại màu chữ
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentColor = bg;
            
            // XỬ LÝ DISABLE: Background màu xám, text giữ nguyên
            if (!isEnabled()) {
                currentColor = new Color(200, 200, 200); // Màu xám nhạt cho background
                setForeground(fgColor); // Giữ màu chữ gốc
            } else {
                setForeground(fgColor); // Màu chữ bình thường
                if (getModel().isPressed()) {
                    currentColor = bg.darker();
                } else if (getModel().isRollover()) {
                    currentColor = bg.brighter();
                }
            }
             
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
             
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private JLabel createStatLabel(String text) {
        JLabel l = new JLabel(text, JLabel.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 32));
        l.setForeground(COLOR_WHITE);
        return l;
    }

    private JPanel createStatBox(JLabel valueLabel, String label, Color accent) {
        JPanel box = new RoundedPanel(15, accent, new BorderLayout());
        box.setLayout(new BorderLayout());
        box.setBorder(new EmptyBorder(20, 22, 20, 22));
        JLabel textLabel = new JLabel(label, JLabel.LEFT);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(COLOR_WHITE);
        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 5));
        inner.setOpaque(false);
        inner.add(valueLabel);
        inner.add(textLabel);
        box.add(inner, BorderLayout.CENTER);
        return box;
    }

	public void refreshTableData() {
		loadHoaDon();
		
	}
}