//package view.Ban;
//
//import dao.Ban_DAO;
//import dao.DatBan_DAO;
//import dao.HoaDon_DAO;
//import entity.Ban;
//import entity.KhachHang;
//import entity.PhieuDatBan;
//import entity.HoaDon;
//import entity.Thue;
//import enums.TrangThaiBan;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.geom.RoundRectangle2D;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//public class ChiTietPhieu_Dialog extends JDialog {
//
//    private final PhieuDatBan phieu;
//    private final Runnable onRefresh;
//    private final DatBan_DAO datBanDAO = new DatBan_DAO();
//    private final Ban_DAO banDAO = new Ban_DAO();
//    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
//
//    public ChiTietPhieu_Dialog(JFrame owner, PhieuDatBan phieu, Runnable onRefresh) {
//        super(owner, "Chi tiết phiếu - " + (phieu != null && phieu.getMaPhieu() != null ? phieu.getMaPhieu().trim() : "N/A"), true);
//        this.phieu = phieu;
//        this.onRefresh = onRefresh;
//        initComponents();
//        pack();
//        setMinimumSize(new Dimension(450, getHeight()));
//        setLocationRelativeTo(owner);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//        setResizable(false);
//    }
//
//    private void initComponents() {
//        JPanel root = new JPanel(new BorderLayout(12, 12));
//        root.setBorder(new EmptyBorder(12, 14, 12, 14));
//        root.setBackground(Color.WHITE);
//
//        JLabel title = new JLabel("Chi tiết phiếu đặt");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        title.setForeground(new Color(33, 37, 41));
//        root.add(title, BorderLayout.NORTH);
//
//        JPanel content = new JPanel();
//        content.setBackground(Color.WHITE);
//        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
//        content.setBorder(new EmptyBorder(8, 8, 8, 8));
//
//        if (phieu != null) {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//            addLine(content, "Mã phiếu:", phieu.getMaPhieu() != null ? phieu.getMaPhieu().trim() : "N/A");
//            addLine(content, "Bàn:", phieu.getBan() != null && phieu.getBan().getMaBan() != null ? phieu.getBan().getMaBan().trim() : "N/A");
//
//            KhachHang kh = phieu.getKhachHang();
//            String khTen = "Khách vãng lai";
//            String sdt = "-";
//            
//            if (kh != null && !"KH00000000".equals(kh.getMaKhachHang().trim())) {
//                khTen = (kh.getTenKhachHang() != null && !kh.getTenKhachHang().trim().isEmpty()) ? kh.getTenKhachHang() : khTen;
//                sdt = (kh.getSdt() != null && !kh.getSdt().trim().isEmpty()) ? kh.getSdt() : sdt;
//            } else if (phieu.getGhiChu() != null) {
//                String ghiChu = phieu.getGhiChu();
//                try {
//                    String[] parts = ghiChu.split(" - SĐT: ");
//                    if (parts.length >= 2) {
//                        String namePart = parts[0];
//                        if (namePart.startsWith("Khách: ")) {
//                            khTen = namePart.substring(7).trim();
//                        }
//                        String phoneAndNote = parts[1];
//                        String[] phoneParts = phoneAndNote.split("\\. Ghi chú: ");
//                        sdt = phoneParts[0].trim();
//                    } else if (ghiChu.startsWith("Khách: ")) {
//                        khTen = ghiChu.substring(7).trim();
//                    }
//                } catch (Exception e) {
//                    // Keep defaults
//                }
//            }
//
//            addLine(content, "Khách hàng:", khTen);
//            addLine(content, "SĐT:", sdt);
//            addLine(content, "Số người:", String.valueOf(phieu.getSoNguoi()));
//            addLine(content, "Thời gian đặt:", phieu.getThoiGianDat() != null ? sdf.format(java.sql.Timestamp.valueOf(phieu.getThoiGianDat())) : "N/A");
//            addLine(content, "Tiền cọc:", String.format("%,.0f VNĐ", phieu.getTienCoc()));
//            
//            String ghiChuDisplay = "";
//            if (phieu.getGhiChu() != null) {
//                int noteIndex = phieu.getGhiChu().indexOf(". Ghi chú: ");
//                if (noteIndex != -1) {
//                    ghiChuDisplay = phieu.getGhiChu().substring(noteIndex + 10).trim();
//                } else if (!phieu.getGhiChu().startsWith("Khách: ")) {
//                    ghiChuDisplay = phieu.getGhiChu();
//                }
//            }
//            addLine(content, "Ghi chú:", !ghiChuDisplay.isEmpty() ? ghiChuDisplay : "-");
//
//        } else {
//            JLabel l = new JLabel("Không có thông tin phiếu.");
//            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            l.setForeground(new Color(100, 100, 100));
//            content.add(l);
//        }
//
//        root.add(content, BorderLayout.CENTER);
//
//        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
//        footer.setBackground(Color.WHITE);
//
//        ButtonPill btnClose = new ButtonPill("Đóng", new Color(240,240,240), new Color(210,210,210));
//        btnClose.addActionListener(e -> dispose());
//
//        ButtonPill btnEdit = new ButtonPill("Chỉnh sửa", new Color(0, 123, 255), new Color(0, 100, 210));
//        btnEdit.addActionListener(e -> {
//            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
//            if (this.phieu != null) {
//                DatBan_Dialog editDialog = new DatBan_Dialog(parent, this.phieu, onRefresh);
//                editDialog.setVisible(true);
//                dispose();
//            } else {
//                JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin phiếu để chỉnh sửa.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        ButtonPill btnCancel = new ButtonPill("Hủy đặt bàn", new Color(220, 53, 69), new Color(190, 45, 60));
//        btnCancel.addActionListener(e -> onCancelBooking());
//        btnCancel.setEnabled(phieu != null && phieu.getMaPhieu() != null);
//
//        // Nút "Khách đã đến" - chuyển sang trạng thái CO_KHACH
//        ButtonPill btnCustomerArrived = new ButtonPill("✓ Khách đã đến", new Color(40, 167, 69), new Color(30, 140, 55));
//        btnCustomerArrived.addActionListener(e -> onCustomerArrived());
//        btnCustomerArrived.setEnabled(phieu != null && phieu.getMaPhieu() != null);
//
//        boolean isTableHasCustomer = false;
//        if (phieu != null && phieu.getBan() != null) {
//            Ban b = phieu.getBan();
//            isTableHasCustomer = b.getTrangThai() == TrangThaiBan.CO_KHACH;
//        }
//
//        if (isTableHasCustomer) {
//            footer.add(btnClose);
//        } else {
//            footer.add(btnClose);
//            if (phieu != null) {
//                footer.add(btnEdit);
//                footer.add(btnCustomerArrived); // Thêm nút "Khách đã đến"
//            }
//            footer.add(btnCancel);
//        }
//
//        root.add(footer, BorderLayout.SOUTH);
//        setContentPane(root);
//    }
//
//    private void addLine(JPanel parent, String label, String value) {
//        JPanel p = new JPanel(new BorderLayout(8, 4));
//        p.setBackground(Color.WHITE);
//        JLabel l = new JLabel(label);
//        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        l.setForeground(new Color(50, 50, 50));
//        l.setPreferredSize(new Dimension(120, 20));
//
//        JTextArea v = new JTextArea(value != null ? value : "");
//        v.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        v.setForeground(new Color(30, 30, 30));
//        v.setEditable(false);
//        v.setLineWrap(true);
//        v.setWrapStyleWord(true);
//        v.setOpaque(false);
//        v.setBorder(null);
//
//        p.add(l, BorderLayout.WEST);
//        p.add(v, BorderLayout.CENTER);
//        parent.add(p);
//        parent.add(Box.createVerticalStrut(8));
//    }
//
//    private void onCustomerArrived() {
//        if (phieu == null || phieu.getMaPhieu() == null || phieu.getBan() == null) {
//            JOptionPane.showMessageDialog(this, "Không có thông tin phiếu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(
//                this,
//                "Xác nhận khách đã đến và chuyển bàn sang trạng thái 'Có khách'?",
//                "Xác nhận",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.QUESTION_MESSAGE
//        );
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        try {
//            Ban ban = phieu.getBan();
//            
//            // Cập nhật trạng thái bàn sang CO_KHACH
//            ban.setTrangThai(TrangThaiBan.CO_KHACH);
//            boolean updateBanSuccess = banDAO.capNhatBan(ban);
//            
//            if (!updateBanSuccess) {
//                JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            // Tạo hóa đơn mới cho bàn này
//            String maHD = generateMaHoaDon();
//            LocalDate ngayLap = LocalDate.now();
//            LocalDateTime gioVao = LocalDateTime.now();
//            
//            HoaDon hoaDon = new HoaDon(
//                maHD,
//                phieu.getKhachHang(),
//                phieu.getNhanVien(),
//                ban,
//                phieu, 
//                null, 
//                new Thue("T001"), 
//                ngayLap,
//                gioVao,
//                null, 
//                "Tiền mặt", 
//                false 
//            );
//            
//            boolean createHoaDonSuccess = hoaDonDAO.addHoaDon(hoaDon);
//            
//            if (createHoaDonSuccess) {
//                JOptionPane.showMessageDialog(this, 
//                    "Khách đã đến! Bàn chuyển sang trạng thái 'Có khách'.\nHóa đơn đã được tạo.", 
//                    "Thành công", 
//                    JOptionPane.INFORMATION_MESSAGE);
//                
//                dispose();
//                if (onRefresh != null) {
//                    onRefresh.run();
//                }
//                
//                // Mở dialog gọi món
//                moDialogGoiMon(ban);
//                
//            } else {
//                // Rollback trạng thái bàn
//                ban.setTrangThai(TrangThaiBan.DA_DAT);
//                banDAO.capNhatBan(ban);
//                JOptionPane.showMessageDialog(this, "Không thể tạo hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//            
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private String generateMaHoaDon() {
//        String newID = "HD00001";
//        try (java.sql.Connection conn = connectDB.ConnectDB.getConnection();
//             java.sql.Statement stmt = conn.createStatement();
//             java.sql.ResultSet rs = stmt.executeQuery("SELECT TOP 1 maHoaDon FROM HOADON ORDER BY maHoaDon DESC")) {
//            
//            if (rs.next()) {
//                String lastID = rs.getString("maHoaDon");
//                if (lastID != null && lastID.matches("HD\\d{5}")) {
//                    int num = Integer.parseInt(lastID.substring(2)) + 1;
//                    newID = String.format("HD%05d", num);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return newID;
//    }
//
//    private void moDialogGoiMon(Ban ban) {
//        // Mở trang gọi món - bạn cần implement trang này
//        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        if (parentFrame != null) {
//            // Giả sử bạn có view.GoiMon_View
//            // view.GoiMon_View goiMonView = new view.GoiMon_View(ban);
//            // JDialog goiMonDialog = new JDialog(parentFrame, "Gọi món - " + ban.getMaBan(), true);
//            // goiMonDialog.setContentPane(goiMonView);
//            // goiMonDialog.setSize(900, 700);
//            // goiMonDialog.setLocationRelativeTo(parentFrame);
//            // goiMonDialog.setVisible(true);
//            
//            JOptionPane.showMessageDialog(this, 
//                "Tính năng gọi món đang được phát triển.\nBạn có thể gọi món từ màn hình chính.", 
//                "Thông báo", 
//                JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
//
//    private void onCancelBooking() {
//        if (phieu == null || phieu.getMaPhieu() == null) {
//            JOptionPane.showMessageDialog(this, "Không có phiếu để hủy.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(
//                this,
//                "Bạn có chắc muốn hủy đặt bàn này?",
//                "Xác nhận hủy",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE
//        );
//
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        boolean ok = datBanDAO.deletePhieuDatBan(phieu.getMaPhieu());
//        if (!ok) {
//            JOptionPane.showMessageDialog(this, "Hủy đặt bàn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Cập nhật trạng thái bàn về TRỐNG
//        if (phieu.getBan() != null) {
//            Ban ban = phieu.getBan();
//            ban.setTrangThai(TrangThaiBan.TRONG);
//            banDAO.capNhatBan(ban);
//        }
//
//        if (onRefresh != null) {
//            try { onRefresh.run(); } catch (Exception ignored) {}
//        }
//
//        JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//        dispose();
//    }
//
//    private static class ButtonPill extends JButton {
//        private final Color base;
//        private final Color hover;
//        private boolean over = false;
//
//        public ButtonPill(String text, Color base, Color hover) {
//            super(text);
//            this.base = base;
//            this.hover = hover;
//            setContentAreaFilled(false);
//            setFocusPainted(false);
//            setOpaque(false);
//            setForeground(contrastColor(base));
//            setFont(new Font("Segoe UI", Font.PLAIN, 13));
//            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            setBorder(new EmptyBorder(5, 15, 5, 15));
//
//            addMouseListener(new java.awt.event.MouseAdapter() {
//                public void mouseEntered(java.awt.event.MouseEvent evt) { over = true; repaint(); }
//                public void mouseExited(java.awt.event.MouseEvent evt) { over = false; repaint(); }
//            });
//            addPropertyChangeListener("enabled", evt -> repaint());
//        }
//
//        @Override protected void paintComponent(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            int w = getWidth(), h = getHeight(), arc = 8;
//            Color fill;
//            if (!isEnabled()) fill = new Color(220, 220, 220);
//            else fill = over ? hover : base;
//            g2.setColor(fill);
//            g2.fillRoundRect(0, 0, w, h, arc, arc);
//
//            g2.setColor(isEnabled() ? getForeground() : new Color(150, 150, 150));
//            FontMetrics fm = g2.getFontMetrics(getFont());
//            String s = getText();
//            int tx = (w - fm.stringWidth(s)) / 2;
//            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
//            g2.drawString(s, tx, ty);
//            g2.dispose();
//        }
//
//        @Override public Dimension getPreferredSize() {
//            Dimension d = super.getPreferredSize();
//            d.width = Math.max(d.width + 24, 80);
//            d.height = Math.max(d.height, 36);
//            return d;
//        }
//
//        private static Color contrastColor(Color bg) {
//            if (bg == null) return Color.BLACK;
//            double luminance = (0.299*bg.getRed() + 0.587*bg.getGreen() + 0.114*bg.getBlue())/255;
//            return luminance > 0.5 ? Color.BLACK : Color.WHITE;
//        }
//    }
//}