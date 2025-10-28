package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.ZoneId;

public class DatBan_Dialog extends JDialog {
    private JTextField txtTenKhach, txtSDT, txtGhiChu;
    private JSpinner spnNgay, spnGio, spnSoNguoi;
    private JFormattedTextField txtTienCoc;
    private JButton btnXacNhan, btnHuy;

    private Ban ban;
    private PhieuDatBan phieuDatCanSua;
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;
    private Runnable onSuccess;

    private static final Color MAU_NEN_DIALOG = Color.WHITE;
    private static final Color MAU_CHU_TIEU_DE = new Color(33, 37, 41);
    private static final Color MAU_CHU_LABEL = new Color(50, 50, 50);
    private static final Color MAU_VIEN_INPUT = new Color(200, 200, 200);
    private static final Color MAU_NUT_XANH = new Color(0, 123, 255);
    private static final Color MAU_NUT_XANH_HOVER = new Color(0, 100, 210);
    private static final Color MAU_NUT_XAM = new Color(240, 240, 240);
    private static final Color MAU_NUT_XAM_HOVER = new Color(210, 210, 210);

    public DatBan_Dialog(JFrame parent, Ban ban, Runnable onSuccess) {
        super(parent, "Đặt bàn - " + ban.getMaBan().trim(), true);
        this.ban = ban;
        this.phieuDatCanSua = null;
        this.onSuccess = onSuccess;
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new DatBan_DAO();
        initComponents();
        setupDialogProperties(parent);
    }

    public DatBan_Dialog(JFrame parent, PhieuDatBan phieuToEdit, Runnable onSuccess) {
        super(parent, "Chỉnh sửa đặt bàn - " + phieuToEdit.getMaPhieu().trim(), true);
        this.phieuDatCanSua = phieuToEdit;
        this.ban = phieuToEdit.getBan();
        this.onSuccess = onSuccess;
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new DatBan_DAO();
        initComponents();
        loadPhieuDatBanData();
        setupDialogProperties(parent);
        btnXacNhan.setText("✓ Cập nhật");
    }

    private void setupDialogProperties(JFrame parent) {
        pack();
        setMinimumSize(new Dimension(500, getHeight()));
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 14, 12, 14));
        root.setBackground(MAU_NEN_DIALOG);

        JLabel title = new JLabel((phieuDatCanSua == null ? "Đặt bàn - " : "Chỉnh sửa - ") + ban.getMaBan().trim());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(MAU_CHU_TIEU_DE);
        root.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(MAU_NEN_DIALOG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(8, 0, 8, 0));

        txtTenKhach = new JTextField(); styleTextField(txtTenKhach);
        txtSDT = new JTextField(); styleTextField(txtSDT);

        Date ngayHienTai = new Date();
        spnNgay = new JSpinner(new SpinnerDateModel(ngayHienTai, null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgay, "dd/MM/yyyy");
        spnNgay.setEditor(dateEditor); styleSpinner(spnNgay);

        Date gioMacDinh;
        if (phieuDatCanSua == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ngayHienTai);
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);
             if (currentHour < 7) {
                 cal.set(Calendar.HOUR_OF_DAY, 8);
                 cal.set(Calendar.MINUTE, 0);
             } else {
                 cal.add(Calendar.HOUR_OF_DAY, 1);
                 cal.set(Calendar.MINUTE, 0);
             }
            gioMacDinh = cal.getTime();
        } else {
            gioMacDinh = ngayHienTai;
        }
        spnGio = new JSpinner(new SpinnerDateModel(gioMacDinh, null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
        spnGio.setEditor(timeEditor); styleSpinner(spnGio);

        spnSoNguoi = new JSpinner(new SpinnerNumberModel(2, 1, ban.getSoCho() + 10, 1)); styleSpinner(spnSoNguoi);
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setGroupingUsed(true);
        txtTienCoc = new JFormattedTextField(currencyFormat);
        txtTienCoc.setValue(0.0); styleTextField(txtTienCoc);
        txtGhiChu = new JTextField(); styleTextField(txtGhiChu);

        content.add(createInputLine("Tên khách hàng:", txtTenKhach));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Số điện thoại:", txtSDT));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Ngày đến:", spnNgay));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Giờ đến:", spnGio));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Số người:", spnSoNguoi));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Tiền cọc:", txtTienCoc));
        content.add(Box.createVerticalStrut(10));
        content.add(createInputLine("Ghi chú:", txtGhiChu));
        content.add(Box.createVerticalStrut(15));

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(MAU_NEN_DIALOG);
        footer.setBorder(new EmptyBorder(5, 0, 0, 0));
        btnHuy = new ButtonPill("Hủy", MAU_NUT_XAM, MAU_NUT_XAM_HOVER);
        btnHuy.addActionListener(e -> dispose());
        btnXacNhan = new ButtonPill("Xác nhận", MAU_NUT_XANH, MAU_NUT_XANH_HOVER);
        btnXacNhan.addActionListener(e -> xuLyLuu());
        footer.add(btnHuy);
        footer.add(btnXacNhan);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadPhieuDatBanData() {
        if (phieuDatCanSua == null) return;

        KhachHang kh = phieuDatCanSua.getKhachHang();
        if (kh != null && !"KH00000000".equals(kh.getMaKhachHang().trim())) {
             txtTenKhach.setText(kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "");
             txtSDT.setText(kh.getSdt() != null ? kh.getSdt() : "");
        } else {
             txtTenKhach.setText("");
             txtSDT.setText("");
             String ghiChu = phieuDatCanSua.getGhiChu();
             if (ghiChu != null) {
                  try {
                    String[] parts = ghiChu.split(" - SĐT: ");
                    if (parts.length >= 2) {
                        String namePart = parts[0];
                        if (namePart.startsWith("Khách: ")) {
                            txtTenKhach.setText(namePart.substring(7).trim());
                        }
                        String phoneAndNote = parts[1];
                        String[] phoneParts = phoneAndNote.split("\\. Ghi chú: ");
                        txtSDT.setText(phoneParts[0].trim());
                    } else if (ghiChu.startsWith("Khách: ")) {
                         txtTenKhach.setText(ghiChu.substring(7).trim());
                    }
                } catch (Exception e) {}
             }
        }

        if (phieuDatCanSua.getThoiGianDat() != null) {
            LocalDateTime thoiGian = phieuDatCanSua.getThoiGianDat();
            Date date = Date.from(thoiGian.atZone(ZoneId.systemDefault()).toInstant());
            spnNgay.setValue(date);
            spnGio.setValue(date);
        }

        spnSoNguoi.setValue(phieuDatCanSua.getSoNguoi());
        txtTienCoc.setValue(phieuDatCanSua.getTienCoc());

        String ghiChuDisplay = "";
         if (phieuDatCanSua.getGhiChu() != null) {
             int noteIndex = phieuDatCanSua.getGhiChu().indexOf(". Ghi chú: ");
             if (noteIndex != -1) {
                 ghiChuDisplay = phieuDatCanSua.getGhiChu().substring(noteIndex + 10).trim();
             } else if (!phieuDatCanSua.getGhiChu().startsWith("Khách: ")) {
                 ghiChuDisplay = phieuDatCanSua.getGhiChu();
             }
         }
        txtGhiChu.setText(ghiChuDisplay);
    }

    private JPanel createInputLine(String label, JComponent input) {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setBackground(MAU_NEN_DIALOG);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(MAU_CHU_LABEL);
        l.setPreferredSize(new Dimension(120, 30));
        p.add(l, BorderLayout.WEST);
        p.add(input, BorderLayout.CENTER);
        p.setBorder(new EmptyBorder(0, 0, 5, 0));
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    private void styleTextField(JTextComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(new CompoundBorder(
                new LineBorder(MAU_VIEN_INPUT, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 32));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(Color.WHITE);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setBorder(new EmptyBorder(5, 8, 5, 8));
        }
        spinner.setBorder(new LineBorder(MAU_VIEN_INPUT, 1));
        spinner.setPreferredSize(new Dimension(spinner.getPreferredSize().width, 32));
    }

    private void xuLyLuu() {
        if (phieuDatCanSua != null) {
            capNhatPhieuDat();
        } else {
            datBanMoi();
        }
    }

    private void datBanMoi() {
        try {
            String ten = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();
            if (ten.isEmpty() || sdt.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và SĐT!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();
            LocalDateTime gioDen = LocalDateTime.ofInstant(datePart.toInstant(), ZoneId.systemDefault()).with(LocalTime.from(timePart.toInstant().atZone(ZoneId.systemDefault())));
            int soNguoi = (int) spnSoNguoi.getValue();
            String ghiChu_form = txtGhiChu.getText().trim();
            double tienCoc = ((Number)txtTienCoc.getValue()).doubleValue();
            if (tienCoc < 0) {
                 JOptionPane.showMessageDialog(this, "Tiền cọc không âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            if (gioDen.isBefore(LocalDateTime.now().plusMinutes(10))) {
                JOptionPane.showMessageDialog(this, "Giờ đến phải sau hiện tại ít nhất 10 phút!", "Lỗi thời gian", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            if (soNguoi > ban.getSoCho()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bàn chỉ có " + ban.getSoCho() + " chỗ. Vẫn đặt " + soNguoi + " người?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) return;
            }

            NhanVien nv = new NhanVien("NV00001");
            KhachHang kh_db = new KhachHang("KH00000000");
            String ghiChu_final = String.format("Khách: %s - SĐT: %s", ten, sdt);
            if (!ghiChu_form.isEmpty()) { ghiChu_final += ". Ghi chú: " + ghiChu_form; }
            String maPhieu = datBanDAO.generateNewID();
            PhieuDatBan phieu_moi = new PhieuDatBan(maPhieu, gioDen, kh_db, nv, ban, soNguoi, tienCoc, ghiChu_final);

            ban.setTrangThai(TrangThaiBan.DA_DAT);
            boolean updateBanSuccess = banDAO.capNhatBan(ban);
            if (updateBanSuccess) {
                boolean createPhieuSuccess = datBanDAO.insertPhieuDatBan(phieu_moi);
                if (createPhieuSuccess) {
                    JOptionPane.showMessageDialog(this, "Đặt bàn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    if (onSuccess != null) { onSuccess.run(); }
                } else {
                    ban.setTrangThai(TrangThaiBan.TRONG); banDAO.capNhatBan(ban);
                    JOptionPane.showMessageDialog(this, "Không thể tạo phiếu!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                ban.setTrangThai(TrangThaiBan.TRONG);
                JOptionPane.showMessageDialog(this, "Không thể cập nhật bàn!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatPhieuDat() {
        try {
            String ten = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();
            if (ten.isEmpty() || sdt.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và SĐT!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                 return;
            }
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();
            LocalDateTime gioDen = LocalDateTime.ofInstant(datePart.toInstant(), ZoneId.systemDefault()).with(LocalTime.from(timePart.toInstant().atZone(ZoneId.systemDefault())));
            int soNguoi = (int) spnSoNguoi.getValue();
            String ghiChu_form = txtGhiChu.getText().trim();
            double tienCoc = ((Number)txtTienCoc.getValue()).doubleValue();
             if (tienCoc < 0) {
                 JOptionPane.showMessageDialog(this, "Tiền cọc không âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
             }
            if (soNguoi > ban.getSoCho()) {
                 int confirm = JOptionPane.showConfirmDialog(this, "Bàn chỉ có " + ban.getSoCho() + " chỗ. Vẫn đặt " + soNguoi + " người?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) return;
             }

            NhanVien nv = new NhanVien("NV00001");
            KhachHang kh_db = new KhachHang("KH00000000");
            String ghiChu_final = String.format("Khách: %s - SĐT: %s", ten, sdt);
            if (!ghiChu_form.isEmpty()) { ghiChu_final += ". Ghi chú: " + ghiChu_form; }

            PhieuDatBan phieu_cap_nhat = new PhieuDatBan(
                phieuDatCanSua.getMaPhieu(),
                gioDen,
                kh_db,
                nv,
                ban,
                soNguoi,
                tienCoc,
                ghiChu_final
            );

            boolean success = datBanDAO.updatePhieuDatBan(phieu_cap_nhat);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu đặt thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) { onSuccess.run(); }
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phiếu đặt thất bại!", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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