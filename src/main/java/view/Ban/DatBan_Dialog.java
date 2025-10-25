package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;     

import entity.Ban;
import entity.KhachHang;  
import entity.NhanVien;    
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.text.NumberFormat; 
import java.util.Locale;

public class DatBan_Dialog extends JDialog {
    private JTextField txtTenKhach, txtSDT;
    private JSpinner spnNgay, spnGio;
    private JSpinner spnSoNguoi;
    private JTextField txtGhiChu;
    private JFormattedTextField txtTienCoc;
    private JButton btnXacNhan, btnHuy;

    private Ban ban;
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;
    private Runnable onSuccess;

    public DatBan_Dialog(JFrame parent, Ban ban, Runnable onSuccess) { 
        super(parent, "Đặt bàn " + ban.getMaBan(), true);
        this.ban = ban;
        this.onSuccess = onSuccess; 
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new DatBan_DAO();

        setLayout(new BorderLayout(10, 10));
        setSize(420, 380);
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridBagLayout()); // <<< ĐỔI LAYOUT
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách
        gbc.fill = GridBagConstraints.HORIZONTAL;

     // --- Hàng 1: Tên khách ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; // Cho phép giãn ngang
        txtTenKhach = new JTextField();
        form.add(txtTenKhach, gbc);

        // --- Hàng 2: SĐT ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtSDT = new JTextField();
        form.add(txtSDT, gbc);

        // --- Hàng 3: Ngày đến ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Ngày đến:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        spnNgay = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgay, "dd/MM/yyyy");
        spnNgay.setEditor(dateEditor);
        form.add(spnNgay, gbc);

        // --- Hàng 4: Giờ đến ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(new JLabel("Giờ đến:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        Date defaultTime = Date.from(Instant.now().plusSeconds(3600));
        spnGio = new JSpinner(new SpinnerDateModel(defaultTime, null, null, Calendar.HOUR_OF_DAY));
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
        spnGio.setEditor(timeEditor);
        form.add(spnGio, gbc);

        // --- Hàng 5: Số người ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(new JLabel("Số người:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        spnSoNguoi = new JSpinner(new SpinnerNumberModel(2, 1, 50, 1));
        form.add(spnSoNguoi, gbc);

        // --- HÀNG 6: TIỀN CỌC (THÊM MỚI) ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        form.add(new JLabel("Tiền cọc (VNĐ):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        // Dùng NumberFormat để định dạng tiền tệ
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setGroupingUsed(true); // Có dấu phẩy ngăn cách
        txtTienCoc = new JFormattedTextField(currencyFormat);
        txtTienCoc.setValue(0.0); // Giá trị mặc định là 0
        txtTienCoc.setColumns(15); // Độ rộng ước lượng
        form.add(txtTienCoc, gbc);
        // ===================================

        // --- Hàng 7: Ghi chú ---
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        form.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 1.0;
        txtGhiChu = new JTextField();
        form.add(txtGhiChu, gbc);

        add(form, BorderLayout.CENTER);
        
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

            //yêu cầu nhập để lưu vào ghi chú
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và SĐT!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //lấy thời gian khách đến
            Date datePart = (Date) spnNgay.getValue();
            Date timePart = (Date) spnGio.getValue();
            LocalDate localDate = datePart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = timePart.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalDateTime gioDen = LocalDateTime.of(localDate, localTime); // Đây là thoiGianDat

            int soNguoi = (int) spnSoNguoi.getValue();
            String ghiChu_form = txtGhiChu.getText().trim();
            
            double tienCoc = 0.0;
            try {
                // Lấy giá trị từ JFormattedTextField
                Number tienCocNumber = (Number) txtTienCoc.getValue();
                if (tienCocNumber != null) {
                    tienCoc = tienCocNumber.doubleValue();
                }
                if (tienCoc < 0) {
                    JOptionPane.showMessageDialog(this, "Tiền cọc không được là số âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Tiền cọc phải là một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            //validate
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

            NhanVien nv = new NhanVien("NV00001"); // Tạm thời
            
            //tạo ghi chú ghép tên + sdt
            String ghiChu_final = String.format("Khách: %s - SĐT: %s.", ten, sdt);
            if (!ghiChu_form.isEmpty()) {
                ghiChu_final += " Ghi chú: " + ghiChu_form;
            }
            
            //tạo Mã Phiếu
            String maPhieu = datBanDAO.generateNewID();

            //TẠO 2 PHIẾU: MỘT ĐỂ LƯU DB, MỘT ĐỂ GỬI VỀ VIEW

            //phiếu để luu CSDL
            KhachHang kh_db = new KhachHang("KH00000000");
            PhieuDatBan phieu_luu_db = new PhieuDatBan(maPhieu, gioDen, kh_db, nv, ban, soNguoi, 0.0, ghiChu_final);

            //bắt đầu giao dịch
            ban.setTrangThai(TrangThaiBan.DA_DAT);
            boolean updateBanSuccess = banDAO.capNhatBan(ban);

            if (updateBanSuccess) {
                //thêm phiếu_luu_db vào CSDL
                boolean createPhieuSuccess = datBanDAO.insertPhieuDatBan(phieu_luu_db);

                if (createPhieuSuccess) {
                    JOptionPane.showMessageDialog(this, "Đặt bàn " + ban.getMaBan() + " thành công cho " + ten);
                    dispose();

                    if (onSuccess != null) {
                        //gửi phiếu_gui_view về View
                        onSuccess.run(); 
                    }
                } else {
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