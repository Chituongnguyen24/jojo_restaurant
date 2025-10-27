package view.Ban;

import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * DatBan_Dialog - hỗ trợ cả tạo mới và chỉnh sửa phiếu đặt.
 *
 * - Nếu phieu == null: chế độ tạo mới.
 * - Nếu phieu != null: chế độ chỉnh sửa (sẽ prefill dữ liệu từ phieu).
 *
 * NOTE: This dialog handles updating existing PhieuDatBan via DatBan_DAO.updatePhieuDatBan(...).
 * For creating a new PhieuDatBan, this implementation expects that DatBan_DAO.insertPhieuDatBan handles
 * creating/looking up KhachHang or you can integrate KhachHang_DAO accordingly.
 */
public class DatBan_Dialog extends JDialog {

    private final Ban ban;
    private final PhieuDatBan existingPhieu; // null => create mode
    private final Runnable onRefresh;
    private final DatBan_DAO datBanDAO = new DatBan_DAO();

    private JTextField txtTenKhach;
    private JTextField txtSDT;
    private JSpinner spinSoNguoi;
    private JSpinner spinnerNgayGio;
    private JTextArea txtGhiChu;
    private JButton btnCancel;
    private JButton btnSave;

    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);

    // Create mode
    public DatBan_Dialog(JFrame owner, Ban ban, Runnable onRefresh) {
        this(owner, ban, null, onRefresh);
    }

    // Edit mode (pass existing PhieuDatBan)
    public DatBan_Dialog(JFrame owner, Ban ban, PhieuDatBan phieu, Runnable onRefresh) {
        super(owner, (phieu == null ? "Đặt bàn - " + ban.getMaBan() : "Chỉnh sửa đặt bàn - " + phieu.getMaPhieu()), true);
        this.ban = ban;
        this.existingPhieu = phieu;
        this.onRefresh = onRefresh;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12,12,12,12));
        root.setBackground(Color.WHITE);

        // header
        JLabel title = new JLabel(existingPhieu == null ? "Tạo đặt bàn" : "Chỉnh sửa đặt bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ten khach
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0;
        form.add(createLabel("Tên khách:"), gbc);
        gbc.gridx=1; gbc.weightx=1.0;
        txtTenKhach = new JTextField();
        styleField(txtTenKhach);
        form.add(txtTenKhach, gbc);

        // SDT
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0;
        form.add(createLabel("SĐT:"), gbc);
        gbc.gridx=1;
        txtSDT = new JTextField();
        styleField(txtSDT);
        form.add(txtSDT, gbc);

        // So nguoi
        gbc.gridx=0; gbc.gridy=2;
        form.add(createLabel("Số người:"), gbc);
        gbc.gridx=1;
        spinSoNguoi = new JSpinner(new SpinnerNumberModel(4,1,100,1));
        spinSoNguoi.setFont(FONT_FIELD);
        form.add(spinSoNguoi, gbc);

        // Thoi gian
        gbc.gridx=0; gbc.gridy=3;
        form.add(createLabel("Thời gian:"), gbc);
        gbc.gridx=1;
        spinnerNgayGio = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spinnerNgayGio.setEditor(new JSpinner.DateEditor(spinnerNgayGio, "dd/MM/yyyy HH:mm"));
        form.add(spinnerNgayGio, gbc);

        // Ghi chu
        gbc.gridx=0; gbc.gridy=4; gbc.weighty=0;
        form.add(createLabel("Ghi chú:"), gbc);
        gbc.gridx=1;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setFont(FONT_FIELD);
        form.add(new JScrollPane(txtGhiChu), gbc);

        root.add(form, BorderLayout.CENTER);

        // footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,10));
        footer.setBackground(Color.WHITE);

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(FONT_FIELD);
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton(existingPhieu == null ? "Tạo đặt" : "Lưu thay đổi");
        btnSave.setFont(FONT_FIELD);
        btnSave.addActionListener(e -> onSave());

        Dimension bs = new Dimension(140, 38);
        btnCancel.setPreferredSize(bs);
        btnSave.setPreferredSize(bs);

        footer.add(btnCancel);
        footer.add(btnSave);

        root.add(footer, BorderLayout.SOUTH);

        // prefill if edit
        if (existingPhieu != null) {
            prefillFromPhieu(existingPhieu);
        } else {
            // set default time to now + 30min
            spinnerNgayGio.setValue(Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()));
        }

        setContentPane(root);
    }

    private void prefillFromPhieu(PhieuDatBan p) {
        if (p.getKhachHang() != null) {
            txtTenKhach.setText(p.getKhachHang().getTenKhachHang());
            txtSDT.setText(p.getKhachHang().getSdt());
        } else {
            // nếu không có thông tin khách trong DB => hiển thị Khách vãng lai
            txtTenKhach.setText("Khách vãng lai");
            txtSDT.setText("");
        }
        spinSoNguoi.setValue(p.getSoNguoi());
        if (p.getThoiGianDat() != null) {
            spinnerNgayGio.setValue(Date.from(p.getThoiGianDat().atZone(ZoneId.systemDefault()).toInstant()));
        }
        txtGhiChu.setText(p.getGhiChu() != null ? p.getGhiChu() : "");
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        return l;
    }

    private void styleField(JComponent c) {
        c.setFont(FONT_FIELD);
        if (c instanceof JTextComponent || c instanceof JSpinner) {
            c.setBackground(Color.WHITE);
        }
    }

    private void onSave() {
        // minimal validation
        String ten = txtTenKhach.getText().trim();
        String sdt = txtSDT.getText().trim();
        int soNguoi = (int) spinSoNguoi.getValue();
        Date when = (Date) spinnerNgayGio.getValue();
        String ghiChu = txtGhiChu.getText().trim();

        if (ten.isEmpty()) {
            // nếu user không nhập tên => tự động đặt là "Khách vãng lai"
            ten = "Khách vãng lai";
        }

        PhieuDatBan p;
        if (existingPhieu != null) {
            p = existingPhieu;
            p.setSoNguoi(soNguoi);
            p.setThoiGianDat(LocalDateTime.ofInstant(when.toInstant(), ZoneId.systemDefault()));
            p.setGhiChu(ghiChu);
            // nếu KhachHang tồn tại, cập nhật tên/SĐT; nếu không tồn tại, tạo KhachHang mới trong DAO nếu cần
            if (p.getKhachHang() != null) {
                p.getKhachHang().setTenKhachHang(ten);
                p.getKhachHang().setSdt(sdt);
            } else {
                KhachHang kh = new KhachHang();
                kh.setTenKhachHang(ten);
                kh.setSdt(sdt);
                p.setKhachHang(kh);
            }
        } else {
            p = new PhieuDatBan();
            p.setMaPhieu(datBanDAO.generateNewID());
            p.setBan(ban);
            p.setSoNguoi(soNguoi);
            p.setThoiGianDat(LocalDateTime.ofInstant(when.toInstant(), ZoneId.systemDefault()));
            p.setGhiChu(ghiChu);
            // tạo KhachHang tạm thời: nếu user không nhập tên => "Khách vãng lai"
            KhachHang kh = new KhachHang();
            kh.setTenKhachHang(ten);
            kh.setSdt(sdt);
            p.setKhachHang(kh);
        }

        boolean ok;
        if (existingPhieu != null) {
            ok = datBanDAO.updatePhieuDatBan(p);
        } else {
            ok = datBanDAO.insertPhieuDatBan(p);
        }

        if (!ok) {
            JOptionPane.showMessageDialog(this, "Lưu thất bại. Kiểm tra kết nối CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // update table status
        datBanDAO.updateTableStatus(ban.getMaBan(), TrangThaiBan.DA_DAT);

        if (onRefresh != null) {
            try { onRefresh.run(); } catch (Exception ignored) {}
        }

        JOptionPane.showMessageDialog(this, "Lưu thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}