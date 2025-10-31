package view.Ban;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDatBan_DAO; 
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuDatBan;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.PhieuDatBan;
import entity.HoaDon;
import entity.Thue;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

public class ChiTietPhieu_Dialog extends JDialog {

    private final PhieuDatBan phieu;
    private final Runnable onRefresh;
    private final PhieuDatBan_DAO phieuDatBanDAO = new PhieuDatBan_DAO(); 
    private final Ban_DAO banDAO = new Ban_DAO();
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BOLD_VALUE = new Font("Segoe UI", Font.BOLD, 14);
    private static final Color COLOR_TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color COLOR_SUCCESS = new Color(40, 167, 69);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);
    private static final Color COLOR_WARNING = new Color(255, 193, 7);

    public ChiTietPhieu_Dialog(JFrame owner, PhieuDatBan phieu, Runnable onRefresh) {
        super(owner, "Chi tiết phiếu - " + (phieu != null && phieu.getMaPhieu() != null ? phieu.getMaPhieu().trim() : "N/A"), true);
        this.phieu = phieu;
        this.onRefresh = onRefresh;
        initComponents();
        pack();
        setMinimumSize(new Dimension(500, 500)); 
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 14, 12, 14));
        root.setBackground(Color.WHITE);

        JLabel title = new JLabel("Chi tiết phiếu đặt");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(8, 8, 8, 8));

        if (phieu != null) {
            String trangThai = phieu.getTrangThaiPhieu();
            Color statusColor;
            
            switch(trangThai) {
                case "Đã đến":
                    statusColor = COLOR_SUCCESS;
                    break;
                case "Không đến":
                    statusColor = COLOR_DANGER;
                    break;
                case "Chưa đến":
                default:
                    statusColor = COLOR_WARNING;
                    break;
            }

            // --- 1. THÔNG TIN KHÁCH HÀNG (Parse từ Ghi chú nếu là Khách vãng lai) ---
            KhachHang kh = phieu.getKhachHang();
            String khTen = "Khách vãng lai";
            String sdt = "-";
            String ghiChuDisplay = phieu.getGhiChu() != null ? phieu.getGhiChu().trim() : "";

            if (kh != null && "KH00000000".equals(kh.getMaKH().trim())) {
                // Parse thông tin khách từ Ghi chú
                if (ghiChuDisplay.startsWith("Khách: ")) {
                    String[] parts = ghiChuDisplay.split(" - SĐT: ");
                    if (parts.length >= 2) {
                        khTen = parts[0].substring(7).trim();
                        String phoneAndNote = parts[1];
                        String[] phoneParts = phoneAndNote.split("\\. Ghi chú: ");
                        sdt = phoneParts[0].trim();
                        if (phoneParts.length > 1) {
                            ghiChuDisplay = phoneParts[1].trim();
                        } else {
                            ghiChuDisplay = ""; 
                        }
                    } else if (ghiChuDisplay.startsWith("Khách: ")) {
                        khTen = ghiChuDisplay.substring(7).trim();
                        ghiChuDisplay = "";
                    }
                }
            } else if (kh != null) {
                khTen = kh.getTenKH() != null ? kh.getTenKH() : "N/A"; 
                sdt = kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "N/A";
                ghiChuDisplay = phieu.getGhiChu() != null ? phieu.getGhiChu().trim() : "";
            }
            
            // --- 2. THÔNG TIN PHIẾU VÀ TRẠNG THÁI ---
            addLine(content, "Mã phiếu:", phieu.getMaPhieu().trim());
            addLine(content, "Bàn:", phieu.getBan() != null && phieu.getBan().getMaBan() != null ? phieu.getBan().getMaBan().trim() : "N/A");
            
            addLine(content, "Trạng thái phiếu:", trangThai, statusColor);

            addLine(content, "Khách hàng:", khTen);
            addLine(content, "SĐT:", sdt);
            addLine(content, "Số người:", String.valueOf(phieu.getSoNguoi()));

            // --- 3. THÔNG TIN THỜI GIAN (SỬ DỤNG LocalDateTime) ---
            addLine(content, "Thời gian hẹn đến:", phieu.getThoiGianDenHen() != null ? phieu.getThoiGianDenHen().format(DATETIME_FORMATTER) : "N/A");
            addLine(content, "Thời gian nhận bàn:", phieu.getThoiGianNhanBan() != null ? phieu.getThoiGianNhanBan().format(DATETIME_FORMATTER) : "-");
            addLine(content, "Thời gian trả bàn:", phieu.getThoiGianTraBan() != null ? phieu.getThoiGianTraBan().format(DATETIME_FORMATTER) : "-");
            
            // --- 4. GHI CHÚ ---
            addLine(content, "Ghi chú:", !ghiChuDisplay.isEmpty() ? ghiChuDisplay : "-");

        } else {
            JLabel l = new JLabel("Không có thông tin phiếu.");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(new Color(100, 100, 100));
            content.add(l);
        }

        root.add(content, BorderLayout.CENTER);

        // --- 5. FOOTER VÀ NÚT BẤM ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(Color.WHITE);

        ButtonPill btnClose = createButtonPill("Đóng", new Color(240,240,240), new Color(210,210,210), COLOR_TEXT_PRIMARY);
        btnClose.addActionListener(e -> dispose());
        
        ButtonPill btnEdit = createButtonPill("Chỉnh sửa", new Color(0, 123, 255), new Color(0, 100, 210), Color.WHITE);
        btnEdit.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (this.phieu != null) {
                // CHUYỂN SANG DATBAN_DIALOG
                DatBan_Dialog editDialog = new DatBan_Dialog(parent, this.phieu, onRefresh);
                editDialog.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin phiếu để chỉnh sửa.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        ButtonPill btnCancel = createButtonPill("Hủy đặt bàn", COLOR_DANGER, COLOR_DANGER.darker(), Color.WHITE);
        btnCancel.addActionListener(e -> onCancelBooking());

        ButtonPill btnCustomerArrived = createButtonPill("Khách đã đến", COLOR_SUCCESS, COLOR_SUCCESS.darker(), Color.WHITE);
        btnCustomerArrived.addActionListener(e -> onCustomerArrived());
        
        btnCancel.setEnabled(phieu != null && phieu.getMaPhieu() != null && phieu.getTrangThaiPhieu().equals("Chưa đến"));
        btnCustomerArrived.setEnabled(phieu != null && phieu.getMaPhieu() != null && phieu.getTrangThaiPhieu().equals("Chưa đến"));
        btnEdit.setEnabled(phieu != null && phieu.getMaPhieu() != null && phieu.getTrangThaiPhieu().equals("Chưa đến"));

        // Logic hiển thị nút
        if (phieu != null && phieu.getTrangThaiPhieu().equals("Chưa đến")) {
            footer.add(btnEdit);
            footer.add(btnCustomerArrived);
            footer.add(btnCancel);
            footer.add(btnClose);
        } else {
            footer.add(btnClose);
        }

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void addLine(JPanel parent, String label, String value) {
        addLine(parent, label, value, new Color(50, 50, 50));
    }
    
    private void addLine(JPanel parent, String label, String value, Color valueColor) {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(50, 50, 50));
        l.setPreferredSize(new Dimension(150, 20));

        JTextArea v = new JTextArea(value != null ? value : "");
        v.setFont(FONT_BOLD_VALUE);
        v.setForeground(valueColor);
        v.setEditable(false);
        v.setLineWrap(true);
        v.setWrapStyleWord(true);
        v.setOpaque(false);
        v.setBorder(null);
        
        Dimension d = v.getPreferredSize();
        v.setPreferredSize(new Dimension(280, d.height));

        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        parent.add(p);
        parent.add(Box.createVerticalStrut(8));
    }

    private ButtonPill createButtonPill(String text, Color base, Color hover, Color fg) {
        ButtonPill btn = new ButtonPill(text, base, hover);
        btn.setForeground(fg);
        return btn;
    }

    private void onCustomerArrived() {
        if (phieu == null || phieu.getMaPhieu() == null || phieu.getBan() == null) {
            JOptionPane.showMessageDialog(this, "Không có thông tin phiếu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xác nhận khách đã đến và chuyển bàn " + phieu.getBan().getMaBan().trim() + " sang trạng thái 'Có khách'?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Bước 1: Cập nhật trạng thái phiếu thành "Đã đến" và ghi nhận giờ nhận bàn
            phieu.setThoiGianNhanBan(LocalDateTime.now());
            phieu.setTrangThaiPhieu("Đã đến");
            
            boolean updatePhieuSuccess = phieuDatBanDAO.updatePhieuDatBan(phieu);
            
            if (!updatePhieuSuccess) {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái phiếu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Bước 2: Cập nhật trạng thái bàn sang CO_KHACH
            boolean updateBanSuccess = banDAO.capNhatTrangThaiBan(phieu.getBan().getMaBan().trim(), TrangThaiBan.CO_KHACH);
            
            if (!updateBanSuccess) {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn! (Bàn có thể đã được người khác sử dụng)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                // Rollback trạng thái phiếu về "Chưa đến"
                phieu.setTrangThaiPhieu("Chưa đến");
                phieuDatBanDAO.updatePhieuDatBan(phieu);
                return;
            }

            // Bước 3: Tạo hóa đơn mới (sử dụng logic mới)
            String maHD = hoaDonDAO.generateNewID(); 
            LocalDateTime gioVao = phieu.getThoiGianNhanBan();
            
            // Lấy thông tin thuế mặc định (Tạo Entity Thue chỉ với mã)
            Thue thueDefault = new Thue("VAT08"); 

            // TẠO HÓA ĐƠN VỚI 15 THAM SỐ (Entity HoaDon mới)
            HoaDon hoaDon = new HoaDon(
                maHD,
                phieu.getNhanVien(),
                phieu.getKhachHang(),
                phieu.getBan(),
                phieu.getThoiGianNhanBan().toLocalDate(),
                gioVao,
                null, 
                "Tiền mặt",
                new KhuyenMai("KM00000000"), // KM Mặc định
                thueDefault, 
                phieu,
                0.0, // TongTienTruocThue mặc định
                0.0, // TongGiamGia mặc định
                false 
            );
            
            boolean createHoaDonSuccess = hoaDonDAO.addHoaDon(hoaDon);
            
            if (!createHoaDonSuccess) {
                // Rollback trạng thái bàn và phiếu
                banDAO.capNhatTrangThaiBan(phieu.getBan().getMaBan().trim(), TrangThaiBan.DA_DAT);
                phieu.setTrangThaiPhieu("Chưa đến");
                phieu.setThoiGianNhanBan(null);
                phieuDatBanDAO.updatePhieuDatBan(phieu);
                JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Bước 4: Copy món ăn từ ChiTietPhieuDatBan sang ChiTietHoaDon
            boolean copySuccess = hoaDonDAO.copyChiTietPhieuDatToHoaDon(phieu.getMaPhieu(), maHD);
            
            if (copySuccess) {
                JOptionPane.showMessageDialog(this, 
                    "Khách đã đến! Bàn chuyển sang trạng thái 'Có khách' và Hóa đơn " + maHD + " đã được tạo.", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, 
                    "Khách đã đến, nhưng không có món ăn nào được sao chép vào hóa đơn!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
            dispose();
            if (onRefresh != null) {
                onRefresh.run();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelBooking() {
        if (phieu == null || phieu.getMaPhieu() == null) {
            JOptionPane.showMessageDialog(this, "Không có phiếu để hủy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn hủy đặt bàn " + phieu.getBan().getMaBan().trim() + "?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Bước 1: Cập nhật trạng thái phiếu thành "Không đến" và ghi thời gian trả bàn
            phieu.setThoiGianTraBan(LocalDateTime.now());
            phieu.setTrangThaiPhieu("Không đến");
            phieuDatBanDAO.updatePhieuDatBan(phieu); // Cập nhật phiếu

            // Bước 2: Cập nhật trạng thái bàn về TRỐNG
            boolean updateBanSuccess = banDAO.capNhatTrangThaiBan(phieu.getBan().getMaBan().trim(), TrangThaiBan.TRONG);
            
            if (updateBanSuccess) {
                JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onRefresh != null) { onRefresh.run(); }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Đã hủy phiếu đặt, nhưng xảy ra lỗi khi cập nhật trạng thái bàn về 'Trống'.",
                    "Lỗi Cập Nhật Bàn", JOptionPane.ERROR_MESSAGE);
                 if (onRefresh != null) { onRefresh.run(); }
                 dispose(); 
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hủy đặt bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ButtonPill extends JButton {
        private final Color base;
        private final Color hover;
        private boolean over = false;

        public ButtonPill(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(contrastColor(base));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(5, 15, 5, 15));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { over = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent evt) { over = false; repaint(); }
            });
            addPropertyChangeListener("enabled", evt -> repaint());
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), arc = 8;
            Color fill;
            if (!isEnabled()) fill = new Color(220, 220, 220);
            else fill = over ? hover : base;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            g2.setColor(isEnabled() ? getForeground() : new Color(150, 150, 150));
            FontMetrics fm = g2.getFontMetrics(getFont());
            String s = getText();
            int tx = (w - fm.stringWidth(s)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(s, tx, ty);
            g2.dispose();
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width = Math.max(d.width + 24, 80);
            d.height = Math.max(d.height, 36);
            return d;
        }

        private static Color contrastColor(Color bg) {
            if (bg == null) return Color.BLACK;
            double luminance = (0.299*bg.getRed() + 0.587*bg.getGreen() + 0.114*bg.getBlue())/255;
            return luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }
    }
}