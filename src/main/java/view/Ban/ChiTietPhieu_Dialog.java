package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Dialog hiển thị chi tiết phiếu đặt, cho phép:
 * - Đóng
 * - Chỉnh sửa (nếu bàn không đang "Có khách")
 * - Hủy đặt bàn (nếu bàn không đang "Có khách")
 *
 * Hiển thị "Khách vãng lai" nếu KhachHang rỗng/không có tên.
 */
public class ChiTietPhieu_Dialog extends JDialog {

    private final PhieuDatBan phieu;
    private final Runnable onRefresh; // nullable
    private final DatBan_DAO datBanDAO = new DatBan_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();

    public ChiTietPhieu_Dialog(JFrame owner, PhieuDatBan phieu, Runnable onRefresh) {
        super(owner, "Chi tiết phiếu - " + (phieu != null ? phieu.getMaPhieu() : ""), true);
        this.phieu = phieu;
        this.onRefresh = onRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 14, 12, 14));
        root.setBackground(Color.WHITE);

        JLabel title = new JLabel("Chi tiết phiếu đặt");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(33, 37, 41));
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(8, 8, 8, 8));

        if (phieu != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            addLine(content, "Mã phiếu:", phieu.getMaPhieu());
            addLine(content, "Bàn:", phieu.getBan() != null ? phieu.getBan().getMaBan() : "N/A");

            KhachHang kh = phieu.getKhachHang();
            String khTen = (kh != null && kh.getTenKhachHang() != null && !kh.getTenKhachHang().trim().isEmpty())
                    ? kh.getTenKhachHang()
                    : "Khách vãng lai";
            addLine(content, "Khách hàng:", khTen);

            String sdt = (kh != null && kh.getSdt() != null && !kh.getSdt().trim().isEmpty())
                    ? kh.getSdt() : "-";
            addLine(content, "SĐT:", sdt);

            addLine(content, "Số người:", String.valueOf(phieu.getSoNguoi()));
            addLine(content, "Thời gian đặt:", phieu.getThoiGianDat() != null ? sdf.format(java.sql.Timestamp.valueOf(phieu.getThoiGianDat())) : "N/A");
            addLine(content, "Tiền cọc:", String.format("%.0f", phieu.getTienCoc()));
            addLine(content, "Ghi chú:", phieu.getGhiChu() != null ? phieu.getGhiChu() : "");
        } else {
            JLabel l = new JLabel("Không có thông tin phiếu.");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(new Color(100, 100, 100));
            content.add(l);
        }

        root.add(content, BorderLayout.CENTER);

        // Buttons area
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(Color.WHITE);

        // Always have Close
        ButtonPill btnClose = new ButtonPill("Đóng", new Color(240,240,240), new Color(210,210,210));
        btnClose.addActionListener(e -> dispose());

        // Buttons that may be hidden when table status == CO_KHACH
        ButtonPill btnEdit = new ButtonPill("Chỉnh sửa", new Color(0, 123, 255), new Color(0, 100, 210));
        btnEdit.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            try {
                DatBan_Dialog editDialog = new DatBan_Dialog(parent, phieu.getBan(), phieu, onRefresh);
                editDialog.setVisible(true);
                dispose();
            } catch (Exception ex) {
                DatBan_Dialog d = new DatBan_Dialog((JFrame) SwingUtilities.getWindowAncestor(this), phieu.getBan(), onRefresh);
                d.setVisible(true);
                dispose();
            }
        });

        ButtonPill btnCancel = new ButtonPill("Hủy đặt bàn", new Color(220, 53, 69), new Color(190, 45, 60));
        btnCancel.addActionListener(e -> onCancelBooking());
        btnCancel.setEnabled(phieu != null && phieu.getMaPhieu() != null);

        // Determine whether the associated table is currently CO_KHACH
        boolean isTableHasCustomer = false;
        if (phieu != null && phieu.getBan() != null) {
            Ban b = phieu.getBan();
            isTableHasCustomer = b.getTrangThai() == TrangThaiBan.CO_KHACH;
        }

        // If table has customers (CO_KHACH), only show Close button per request
        if (isTableHasCustomer) {
            footer.add(btnClose);
        } else {
            // Otherwise show Close + Edit (if phieu exists) + Cancel
            footer.add(btnClose);
            if (phieu != null) footer.add(btnEdit);
            footer.add(btnCancel);
        }

        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addLine(JPanel parent, String label, String value) {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setPreferredSize(new Dimension(120, 20));
        JLabel v = new JLabel(value != null ? value : "");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        v.setForeground(new Color(50, 50, 50));
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        parent.add(p);
        parent.add(Box.createVerticalStrut(6));
    }

    private void onCancelBooking() {
        if (phieu == null || phieu.getMaPhieu() == null) {
            JOptionPane.showMessageDialog(this, "Không có phiếu để hủy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn hủy đặt bàn này? Hành động sẽ xóa phiếu và trả bàn về trạng thái 'Trống'.",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = datBanDAO.deletePhieuDatBan(phieu.getMaPhieu());
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Hủy đặt bàn thất bại. Vui lòng thử lại hoặc kiểm tra CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cập nhật trạng thái bàn về TRONG (nếu có)
        try {
            Ban ban = phieu.getBan();
            if (ban != null) {
                ban.setTrangThai(TrangThaiBan.TRONG);
                banDAO.capNhatBan(ban);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (onRefresh != null) {
            try { onRefresh.run(); } catch (Exception ignored) {}
        }

        JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // --- simple rounded button that paints itself to avoid LAF overrides ---
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
            if (!isEnabled()) fill = new Color(240,240,240);
            else fill = over ? hover : base;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            g2.setColor(fill.darker().darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w-3, h-3, arc, arc);
            // text
            g2.setColor(isEnabled() ? getForeground() : new Color(120,120,120));
            FontMetrics fm = g2.getFontMetrics(getFont());
            String s = getText();
            int tx = (w - fm.stringWidth(s)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(s, tx, ty);
            g2.dispose();
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width += 24;
            d.height = Math.max(d.height, 36);
            return d;
        }

        private static Color contrastColor(Color bg) {
            double luminance = (0.299*bg.getRed() + 0.587*bg.getGreen() + 0.114*bg.getBlue())/255;
            return luminance > 0.6 ? new Color(34,34,34) : Color.WHITE;
        }
    }
}