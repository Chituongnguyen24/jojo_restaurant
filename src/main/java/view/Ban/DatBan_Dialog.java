package view.Ban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DatBan_Dialog extends JDialog {
    private JTextField txtTenKhach, txtSDT;
    private JSpinner spnNgay, spnGio;
    private JButton btnXacNhan, btnHuy;
    private Ban_View.TableInfo table;
    private Runnable onSuccess;

    public DatBan_Dialog(JFrame parent, Ban_View.TableInfo table, Runnable onSuccess) {
        super(parent, "Đặt bàn " + table.name, true);
        this.table = table;
        this.onSuccess = onSuccess;

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
        spnGio = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
        spnGio.setEditor(timeEditor);
        form.add(spnGio);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        btnXacNhan = new JButton("Xác nhận");
        btnHuy = new JButton("Hủy");
        buttons.add(btnXacNhan);
        buttons.add(btnHuy);
        add(buttons, BorderLayout.SOUTH);

        btnXacNhan.addActionListener(e -> datBan());
        btnHuy.addActionListener(e -> dispose());
    }

    private void datBan() {
        try {
            String ten = txtTenKhach.getText().trim();
            String sdt = txtSDT.getText().trim();

            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (!sdt.matches("\\d{9,11}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải là 9–11 chữ số!");
                return;
            }

            // Lấy giá trị ngày + giờ từ spinner
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();

            LocalDate localDate = datePart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = timePart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime gioDen = LocalDateTime.of(localDate, localTime);

            if (gioDen.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Thời gian đến phải sau thời điểm hiện tại!");
                return;
            }

            // Cập nhật trạng thái
            table.status = Ban_View.TableStatus.DA_DUOC_DAT;
            QuanLy_DatBan.scheduleStatusUpdate(table, gioDen);

            JOptionPane.showMessageDialog(this, "Đặt bàn thành công cho " + ten);
            dispose();

            if (onSuccess != null) onSuccess.run();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
