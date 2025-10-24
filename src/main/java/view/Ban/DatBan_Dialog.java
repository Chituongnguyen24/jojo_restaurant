package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;     // (Giữ)
// import dao.KhachHang_DAO;  // <<< BỎ IMPORT NÀY
import entity.Ban;
import entity.KhachHang;   // (Giữ)
import entity.NhanVien;    // (Giữ)
import entity.PhieuDatBan; // (Giữ)
import enums.TrangThaiBan;
// import utils.AuthService; // (Lớp giả định)

import javax.swing.*;
import java.awt.*;
// ... (các import khác giữ nguyên) ...
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class DatBan_Dialog extends JDialog {
    // ... (txtTenKhach, txtSDT, spnNgay, spnGio, spnSoNguoi, txtGhiChu giữ nguyên) ...
    private JTextField txtTenKhach, txtSDT;
    private JSpinner spnNgay, spnGio;
    private JSpinner spnSoNguoi;
    private JTextField txtGhiChu;
    private JButton btnXacNhan, btnHuy;

    private Ban ban;
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;
    // private KhachHang_DAO khachHangDAO; // <<< BỎ BIẾN NÀY
    private Runnable onSuccess;

    // Sửa lại Constructor
    public DatBan_Dialog(JFrame parent, Ban ban, Runnable onSuccess) {
        super(parent, "Đặt bàn " + ban.getMaBan(), true);
        this.ban = ban;
        this.onSuccess = onSuccess;
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new DatBan_DAO();
        // this.khachHangDAO = new KhachHang_DAO(); // <<< BỎ DÒNG NÀY

        setLayout(new BorderLayout(10, 10));
        setSize(420, 340);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ... (Giữ nguyên các trường Tên, SĐT, Ngày, Giờ) ...
        form.add(new JLabel("Tên khách hàng:"));
        txtTenKhach = new JTextField();
        form.add(txtTenKhach);

        form.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        form.add(txtSDT);
        
        form.add(new JLabel("Ngày đến:"));
        spnNgay = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgay, "dd/MM/yyyy");
        spnNgay.setEditor(dateEditor);
        form.add(spnNgay);

        form.add(new JLabel("Giờ đến:"));
        Date defaultTime = Date.from(Instant.now().plusSeconds(3600));
        spnGio = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
        spnGio.setEditor(timeEditor);
        form.add(spnGio);

        form.add(new JLabel("Số người:"));
        spnSoNguoi = new JSpinner(new SpinnerNumberModel(2, 1, 50, 1));
        form.add(spnSoNguoi);

        form.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField();
        form.add(txtGhiChu);


        add(form, BorderLayout.CENTER);
        
        // ... (Giữ nguyên phần Nút bấm) ...
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220,220,220)));
        btnXacNhan = new JButton("Xác nhận đặt");
        btnXacNhan.setBackground(new Color(0, 123, 255));
        btnXacNhan.setForeground(Color.WHITE);
        btnHuy = new JButton("Hủy");
        buttons.add(btnHuy);
        buttons.add(btnXacNhan);
        add(buttons, BorderLayout.SOUTH);

        btnXacNhan.addActionListener(e -> datBan());
        btnHuy.addActionListener(e -> dispose());
    }

    /**
     * SỬA LẠI LOGIC CỦA PHƯƠNG THỨC NÀY
     */
    private void datBan() {
        try {
            String ten = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();

            // Vẫn yêu cầu nhập Tên và SĐT để lưu vào ghi chú
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và SĐT!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy giá trị ngày + giờ (Thời gian khách đến)
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();
            LocalDate localDate = datePart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = timePart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime gioDen = LocalDateTime.of(localDate, localTime); // Đây là thoiGianDat

            int soNguoi = (int) spnSoNguoi.getValue();
            String ghiChu_form = txtGhiChu.getText().trim();
            
            // Validate (Giữ nguyên)
            if (gioDen.isBefore(LocalDateTime.now().plusMinutes(10))) { 
                JOptionPane.showMessageDialog(this, "Thời gian đến phải sau thời điểm hiện tại ít nhất 10 phút!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (soNguoi > ban.getSoCho()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bàn " + ban.getMaBan() + " chỉ có " + ban.getSoCho() + " chỗ. Bạn vẫn muốn đặt " + soNguoi + " người?",
                    "Cảnh báo", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            // === LOGIC MỚI: GÁN CHO KHÁCH VÃNG LAI ===

            // 1. Gán cho Khách vãng lai (từ CSDL jojo_v6.sql)
            KhachHang kh = new KhachHang("KH00000000"); 

            // 2. Lấy Nhân viên (Giả định)
            // !!! BẠN PHẢI THAY THẾ DÒNG NÀY bằng logic lấy NV đăng nhập
            NhanVien nv = new NhanVien("NV00001"); // Tạm thời
            // NhanVien nv = AuthService.getLoggedInNhanVien(); 
            if (nv == null) {
                 JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy phiên đăng nhập của nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // 3. Tạo Ghi chú (Ghép Tên + SĐT vào)
            String ghiChu_final = String.format("Khách: %s - SĐT: %s.", ten, sdt);
            if (!ghiChu_form.isEmpty()) {
                ghiChu_final += " Ghi chú: " + ghiChu_form;
            }
            
            // 4. Tạo Phiếu Đặt Bàn
            String maPhieu = datBanDAO.generateNewID(); // Vẫn cần hàm này trong DatBan_DAO
            
            // Constructor: (maPhieu, thoiGianDat, kh, nv, ban, soNguoi, tienCoc, ghiChu)
            PhieuDatBan phieu = new PhieuDatBan(maPhieu, gioDen, kh, nv, ban, soNguoi, 0.0, ghiChu_final);
            
            // 5. Bắt đầu "Giao dịch"
            ban.setTrangThai(TrangThaiBan.DA_DAT);
            boolean updateBanSuccess = banDAO.capNhatBan(ban);

            if (updateBanSuccess) {
                // 6. Thêm phiếu vào CSDL
                boolean createPhieuSuccess = datBanDAO.insertPhieuDatBan(phieu); // Cần hàm này trong DatBan_DAO

                if (createPhieuSuccess) {
                    // THÀNH CÔNG
                    JOptionPane.showMessageDialog(this, "Đặt bàn " + ban.getMaBan() + " thành công cho " + ten);
                    dispose();

                    if (onSuccess != null) {
                        onSuccess.run(); // Gọi callback để View tải lại
                    }
                } else {
                    // LỖI: Rollback trạng thái bàn
                    ban.setTrangThai(TrangThaiBan.TRONG);
                    banDAO.capNhatBan(ban); 
                    JOptionPane.showMessageDialog(this, "Lỗi! Không thể tạo phiếu đặt bàn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi! Không thể cập nhật trạng thái bàn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}