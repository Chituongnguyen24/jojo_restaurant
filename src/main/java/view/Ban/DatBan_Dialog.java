package view.Ban;

import dao.Ban_DAO; // Thêm import
import entity.Ban; // Thêm import
import enums.TrangThaiBan; // Thêm import

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

public class DatBan_Dialog extends JDialog {
    private JTextField txtTenKhach, txtSDT;
    private JSpinner spnNgay, spnGio;
    private JButton btnXacNhan, btnHuy;

    // === SỬA LỖI 1: Dùng entity.Ban thay vì TableInfo ===
    private Ban ban; 
    private Ban_DAO banDAO;
    private Runnable onSuccess; // Callback để làm mới Ban_View

    // === SỬA LỖI 2: Thay đổi constructor ===
    public DatBan_Dialog(JFrame parent, Ban ban, Runnable onSuccess) {
        super(parent, "Đặt bàn " + ban.getMaBan(), true);
        this.ban = ban;
        this.onSuccess = onSuccess;
        this.banDAO = new Ban_DAO(); // Khởi tạo DAO

        setLayout(new BorderLayout(10, 10));
        setSize(380, 260);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ====== Họ tên ======
        form.add(new JLabel("Tên khách hàng:"));
        txtTenKhach = new JTextField();
        form.add(txtTenKhach);

        // ====== SĐT ======
        form.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        form.add(txtSDT);

        // ====== Ngày ======
        form.add(new JLabel("Ngày đến:"));
        spnNgay = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgay, "dd/MM/yyyy");
        spnNgay.setEditor(dateEditor);
        form.add(spnNgay);

        // ====== Giờ ======
        form.add(new JLabel("Giờ đến:"));
        // Lấy giờ hiện tại + 1 tiếng làm giờ mặc định
        Date defaultTime = Date.from(Instant.now().plusSeconds(3600));
        spnGio = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
        spnGio.setEditor(timeEditor);
        form.add(spnGio);

        add(form, BorderLayout.CENTER);

        // ====== Nút bấm ======
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

    private void datBan() {
        try {
            String ten = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();

            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!sdt.matches("\\d{9,11}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải là 9–11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy giá trị ngày + giờ từ spinner
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();

            LocalDate localDate = datePart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = timePart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime gioDen = LocalDateTime.of(localDate, localTime);

            if (gioDen.isBefore(LocalDateTime.now().plusMinutes(10))) { // Phải đặt trước ít nhất 10 phút
                JOptionPane.showMessageDialog(this, "Thời gian đến phải sau thời điểm hiện tại ít nhất 10 phút!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // (Lý tưởng: Ở đây bạn nên tạo một PhieuDatBan và lưu vào CSDL)
            // Tạm thời, chúng ta sẽ chỉ cập nhật trạng thái bàn

            // === SỬA LỖI 3: Cập nhật trạng thái và lưu vào CSDL ===
            ban.setTrangThai(TrangThaiBan.DA_DAT);
            boolean success = banDAO.capNhatBan(ban); // Gọi DAO

            if (success) {
                // === SỬA LỖI 4: Xóa bỏ lời gọi đến QuanLy_DatBan ===
                // QuanLy_DatBan.scheduleStatusUpdate(table, gioDen); // Dòng này bị xóa
                
                JOptionPane.showMessageDialog(this, "Đặt bàn " + ban.getMaBan() + " thành công cho " + ten);
                dispose();

                // Gọi callback để Ban_View tải lại dữ liệu và cập nhật UI
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                // Rollback nếu lỗi CSDL
                ban.setTrangThai(TrangThaiBan.TRONG);
                JOptionPane.showMessageDialog(this, "Lỗi! Không thể cập nhật trạng thái bàn trong CSDL.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}