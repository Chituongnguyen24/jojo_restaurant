package view.ThucDon;

import dao.Ban_DAO;
import dao.DatBan_DAO;
import entity.Ban;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MonAn_View extends JPanel {
    
    // DAOs
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;

    // Data
    private PhieuDatBan phieuDatBanHienTai; 

    // UI Components
    private JTable tblBan;
    private JTable tblDonDatMon;
    private DefaultTableModel modelBan;
    private DefaultTableModel modelDonDatMon;
    
    private JButton btnDatMon, btnHoanThanhMon, btnHuyMon, btnLamMoi;

    public MonAn_View() {
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new DatBan_DAO();

        setLayout(new BorderLayout(0, 15)); // Khoảng cách dọc giữa các phần
        setBackground(new Color(251, 248, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); 

        JLabel titleLabel = new JLabel("Quản lý Đặt món ăn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(30, 30, 30));

        JLabel subtitleLabel = new JLabel("Chọn bàn và gọi món cho khách hàng");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        JPanel titleContainer = new JPanel();
        titleContainer.setOpaque(false);
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createVerticalStrut(3)); 
        titleContainer.add(subtitleLabel);

        // Nút Làm mới ở góc 
        btnLamMoi = createStyledButton("Làm mới", new Color(34, 139, 230), Color.WHITE);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLamMoi.setPreferredSize(new Dimension(120, 40));

        headerPanel.add(titleContainer, BorderLayout.WEST);
        headerPanel.add(btnLamMoi, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        //2 bảng trên và bảng chi tiết dưới
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15)); // Khoảng cách dọc
        contentPanel.setOpaque(false);

        //Bàn và Món Tổng hợp
        JPanel topTablesPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // Khoảng cách ngang
        topTablesPanel.setOpaque(false);

        //panel Bảng Bàn
        modelBan = new DefaultTableModel(new String[]{"Mã bàn", "Số chỗ"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
            	return false;
            }
        };
        tblBan = createStyledTable(modelBan);
        JScrollPane scrollBan = new JScrollPane(tblBan);
        scrollBan.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); // Viền nhẹ
        JPanel banWrapper = new RoundedPanel(12, Color.WHITE); // Panel bo tròn trắng
        banWrapper.setLayout(new BorderLayout());
        banWrapper.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding bên trong panel bo tròn
        banWrapper.add(scrollBan, BorderLayout.CENTER);
        JLabel banTitle = new JLabel("Chọn bàn (Đã đặt / Có khách)");
        banTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        banTitle.setBorder(new EmptyBorder(0, 0, 8, 0)); // Khoảng cách dưới title
        JPanel pnlBanContainer = new JPanel(new BorderLayout());
        pnlBanContainer.setOpaque(false);
        pnlBanContainer.add(banTitle, BorderLayout.NORTH);
        pnlBanContainer.add(banWrapper, BorderLayout.CENTER);
        topTablesPanel.add(pnlBanContainer);

        //panel Bảng Món Tổng hợp
        JPanel pnlMonTongHop = new JPanel(new BorderLayout());
        pnlMonTongHop.setOpaque(false);
        pnlMonTongHop.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                " Món đã gọi (Tổng hợp)", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), new Color(50, 50, 50)));
        pnlMonTongHop.add(new JLabel("Tính năng đang phát triển...", SwingConstants.CENTER), BorderLayout.CENTER);
        topTablesPanel.add(pnlMonTongHop);

        JPanel topWrapper = new JPanel(new BorderLayout()); // Wrapper dùng BorderLayout
        topWrapper.setOpaque(false); // Nền trong suốt
        topWrapper.add(topTablesPanel, BorderLayout.NORTH);

        //chi tiết Đơn đặt món
        modelDonDatMon = new DefaultTableModel(
            new String[]{"Mã món", "Tên món", "Số lượng", "Ghi chú", "Trạng thái", "Thời điểm"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
            	return false;
            }
        };
        tblDonDatMon = createStyledTable(modelDonDatMon);
        
        JScrollPane scrollDonDat = new JScrollPane(tblDonDatMon);
        scrollDonDat.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JPanel donDatWrapper = new RoundedPanel(12, Color.WHITE); 
        donDatWrapper.setLayout(new BorderLayout());
        donDatWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        donDatWrapper.add(scrollDonDat, BorderLayout.CENTER);
        JLabel donDatTitle = new JLabel("Chi tiết đơn đặt món cho bàn đã chọn");
        donDatTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        donDatTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        JPanel pnlDonDatContainer = new JPanel(new BorderLayout());
        pnlDonDatContainer.setOpaque(false);
        pnlDonDatContainer.add(donDatTitle, BorderLayout.NORTH);
        pnlDonDatContainer.add(donDatWrapper, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(
    		JSplitPane.VERTICAL_SPLIT,
            topWrapper, // <<< Sử dụng topWrapper thay vì topTablesPanel
            pnlDonDatContainer
        );
            // splitPane.setResizeWeight(0.45); // Có thể bỏ dòng này hoặc giảm giá trị (vd: 0.1)
        splitPane.setResizeWeight(0.1); // Ưu tiên panel dưới, panel trên chỉ lấy chiều cao cần thiết
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        splitPane.setDividerSize(10);
        // ===============================================

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(splitPane, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.CENTER);

        //Nút bấm
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(5, 0, 0, 0)); // Padding trên

        btnDatMon = createStyledButton("Đặt món", new Color(76, 175, 80), Color.WHITE);
        btnHoanThanhMon = createStyledButton("Hoàn thành món", new Color(34, 139, 230), Color.WHITE); 
        btnHuyMon = createStyledButton("Hủy món", new Color(244, 67, 54), Color.WHITE);

        Dimension buttonSize = new Dimension(160, 40);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        btnDatMon.setPreferredSize(buttonSize);
        btnDatMon.setFont(buttonFont);
        btnHoanThanhMon.setPreferredSize(buttonSize);
        btnHoanThanhMon.setFont(buttonFont);
        btnHoanThanhMon.setEnabled(false); 
        btnHuyMon.setPreferredSize(buttonSize);
        btnHuyMon.setFont(buttonFont);

        footerPanel.add(btnDatMon);
        footerPanel.add(btnHoanThanhMon);
        footerPanel.add(btnHuyMon);

        add(footerPanel, BorderLayout.SOUTH);

        tblBan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblBan.getSelectedRow() != -1) {
                xuLyChonBan();
            }
        });
        btnDatMon.addActionListener(e -> moDialogDatMon());
        btnHuyMon.addActionListener(e -> xuLyHuyMon());
        btnLamMoi.addActionListener(e -> taiDanhSachBan());

        taiDanhSachBan();
    }


    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(40); // Chiều cao hàng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255)); // Màu khi chọn hàng
        table.setGridColor(new Color(235, 235, 235)); // Màu đường kẻ
        table.setIntercellSpacing(new Dimension(0, 0)); // Không khoảng cách giữa ô
        table.setShowGrid(true); // Hiển thị đường kẻ ngang dọc

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font tiêu đề
        header.setBackground(new Color(248, 249, 250)); // Màu nền tiêu đề
        header.setForeground(new Color(50, 50, 50)); // Màu chữ tiêu đề
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Chiều cao tiêu đề
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220))); // Viền dưới tiêu đề

        //căn giữa tiêu đề
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    //tạo JButton bo tròn với màu sắc
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Bo tròn nhẹ
                g2.dispose();
                super.paintComponent(g); // Vẽ chữ lên trên
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false); 
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private void taiDanhSachBan() {
        modelBan.setRowCount(0);
        modelDonDatMon.setRowCount(0); 
        phieuDatBanHienTai = null;
        
        List<Ban> dsBan = banDAO.getBanDangHoatDong();
        for (Ban ban : dsBan) {
            modelBan.addRow(new Object[]{
                ban.getMaBan().trim(),
                ban.getSoCho() 
            });
        }
    }
    
    //xử lý khi click vào 1 bàn
    private void xuLyChonBan() {
        int row = tblBan.getSelectedRow();
        if (row == -1) return;
        
        String maBan = (String) modelBan.getValueAt(row, 0);
        
        //lấy phiếu đặt bàn tương ứng với bàn này
        this.phieuDatBanHienTai = datBanDAO.getPhieuByBan(maBan);
        
        if (this.phieuDatBanHienTai != null) {
            taiDonDatMon(this.phieuDatBanHienTai.getMaPhieu());
        } else {
            modelDonDatMon.setRowCount(0);
            JOptionPane.showMessageDialog(this, 
                "Bàn này đang 'Có khách' nhưng không tìm thấy Phiếu đặt bàn (PDB).\n" +
                "Tính năng gọi món cho khách vãng lai (không PDB) chưa được hỗ trợ.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //tải chi tiết món đã đặt của phiếu lên JTable
    private void taiDonDatMon(String maPhieu) {
        modelDonDatMon.setRowCount(0);
        List<Object[]> dsMon = datBanDAO.getChiTietTheoMaPhieu(maPhieu);
        
        String thoiDiemGia = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String trangThaiGia = "Chưa hoàn thành";

        for (Object[] row : dsMon) {
            modelDonDatMon.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                row[3],
                trangThaiGia, // Dữ liệu giả
                thoiDiemGia   // Dữ liệu giả
            });
        }
    }
    
    // Mở dialog chọn món
    private void moDialogDatMon() {
        if (phieuDatBanHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn đang có phiếu đặt!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mở dialog chọn món
        ChonMon_Dialog dialog = new ChonMon_Dialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            phieuDatBanHienTai
        );
        dialog.setVisible(true);
        
        // Sau khi dialog đóng, tải lại danh sách món đã đặt
        taiDonDatMon(phieuDatBanHienTai.getMaPhieu());
    }
    
    // Xử lý hủy món
    private void xuLyHuyMon() {
        int selectedRow = tblDonDatMon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong 'Đơn đặt món' để hủy!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phieuDatBanHienTai == null) return;

        String maMonAn = (String) modelDonDatMon.getValueAt(selectedRow, 0);
        String tenMonAn = (String) modelDonDatMon.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn HỦY món: " + tenMonAn + "?", 
            "Xác nhận hủy", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = datBanDAO.deleteChiTiet(phieuDatBanHienTai.getMaPhieu(), maMonAn);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hủy món thành công!");
                taiDonDatMon(phieuDatBanHienTai.getMaPhieu()); // Tải lại bảng
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi hủy món!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            cornerRadius = radius;
            bgColor = color;
            setOpaque(false); // Quan trọng để thấy màu nền bo tròn
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); // Sử dụng màu nền được truyền vào
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}