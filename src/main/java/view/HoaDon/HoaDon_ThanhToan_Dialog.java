package view.HoaDon;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang; // Thêm KhachHang
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat; 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;

public class HoaDon_ThanhToan_Dialog extends JDialog {
    private JComboBox<String> cbxPhuongThuc;
    private JButton btnSave, btnCancel;
    private HoaDon hoaDon;
    private HoaDon_DAO hoaDonDAO;
    private List<ChiTietHoaDon> chiTietHoaDonList; // Lưu lại list để dùng khi in

    // Định dạng tiền tệ
    private static final DecimalFormat moneyFormatter = new DecimalFormat("###,### VNĐ");
    private static final DecimalFormat itemPriceFormatter = new DecimalFormat("###,###"); // Dùng cho JList

    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO,
                                   double tongTienMonAn, double tienGiam, double tienThue, double tongThanhToan, 
                                   List<ChiTietHoaDon> chiTietList) {
        super(owner, "Xác nhận thanh toán", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;
        this.chiTietHoaDonList = chiTietList; 

        setSize(500, 600); 
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Xác nhận thanh toán", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        mainPanel.setOpaque(false);

        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 8)); 
        infoPanel.setOpaque(false);

        infoPanel.add(new JLabel("Mã Hóa Đơn:"));
        infoPanel.add(new JLabel(hoaDon.getMaHD())); // SỬA: getMaHD

        infoPanel.add(new JLabel("Khách hàng:"));
        String tenKH = "Khách lẻ";
        if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTenKH() != null) { // SỬA: getTenKH
            tenKH = hoaDon.getKhachHang().getTenKH(); // SỬA: getTenKH
        }
        infoPanel.add(new JLabel(tenKH));

        // --- Các dòng tiền ---
        infoPanel.add(new JLabel("Tổng tiền (món ăn):"));
        infoPanel.add(new JLabel(moneyFormatter.format(tongTienMonAn)));

        infoPanel.add(new JLabel("Khuyến mãi:"));
        JLabel lblGiamGia = new JLabel("-" + moneyFormatter.format(tienGiam));
        lblGiamGia.setForeground(Color.BLUE); 
        infoPanel.add(lblGiamGia);

        infoPanel.add(new JLabel("Thuế VAT:")); 
        JLabel lblThue = new JLabel(moneyFormatter.format(tienThue));
        infoPanel.add(lblThue);

        infoPanel.add(new JLabel("Tổng thanh toán:"));
        JLabel lblTongThanhToan = new JLabel(moneyFormatter.format(tongThanhToan));
        lblTongThanhToan.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongThanhToan.setForeground(new Color(200, 80, 70)); 
        infoPanel.add(lblTongThanhToan);
        // --- Kết thúc thêm dòng tiền ---

        infoPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThucThanhToan() != null ? hoaDon.getPhuongThucThanhToan() : "Tiền mặt"); // SỬA: getPhuongThucThanhToan
        infoPanel.add(cbxPhuongThuc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Danh sách món ăn (JList)
        JList<String> listMonAn = new JList<>(createMonAnVector(chiTietList)); 
        listMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMonAn.setLayoutOrientation(JList.VERTICAL);
        listMonAn.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPaneMonAn = new JScrollPane(listMonAn);
        scrollPaneMonAn.setBorder(BorderFactory.createTitledBorder("Chi tiết món ăn đã gọi"));

        mainPanel.add(scrollPaneMonAn, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Panel nút bấm (giữ nguyên)
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);
        btnSave = new JButton("Xác nhận & In Hóa Đơn");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13)); btnSave.addActionListener(this::savePayment);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70)); 
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13)); btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Hàm tạo Vector cho JList (cập nhật hiển thị)
    private Vector<String> createMonAnVector(List<ChiTietHoaDon> chiTietList) {
        Vector<String> vector = new Vector<>();
        if (chiTietList == null || chiTietList.isEmpty()) {
            vector.add("  (Chưa có món nào trong hóa đơn)");
            return vector;
        }
        // Thêm tiêu đề cột cho JList
        vector.add(String.format("%-4s %-25s %8s %15s", "STT", "Tên món", "SL", "Giá/SL"));
        vector.add("-------------------------------------------------------");

        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietHoaDon cthd = chiTietList.get(i);
            String tenMon = "N/A";
            double donGiaBan = 0;
            if(cthd.getMonAn() != null) {
                tenMon = cthd.getMonAn().getTenMonAn() != null ? cthd.getMonAn().getTenMonAn() : "Lỗi tên món";
                donGiaBan = cthd.getDonGiaBan(); // SỬA: getDonGiaBan
            }

            // Cắt bớt tên món nếu quá dài
            if (tenMon.length() > 24) {
                tenMon = tenMon.substring(0, 21) + "...";
            }

            vector.add(String.format("%-4d %-25s %8d %15s",
                                     i + 1,
                                     tenMon,
                                     cthd.getSoLuong(),
                                     itemPriceFormatter.format(donGiaBan))); // Format đơn giá bán
        }
        return vector;
    }

    // Hàm lưu thanh toán (thay đổi cách gọi HoaDonPrinter)
    private void savePayment(ActionEvent e) {
        try {
            String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();
            if (phuongThuc == null || phuongThuc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán!");
                return;
            }
            hoaDon.setPhuongThucThanhToan(phuongThuc); // SỬA: setPhuongThucThanhToan
            hoaDon.setDaThanhToan(true);
            hoaDon.setGioRa(LocalDateTime.now());
            // LƯU Ý: updateHoaDon cần TongTienTruocThue và TongGiamGia
            hoaDon.setTongTienTruocThue(0.0);
            hoaDon.setTongGiamGia(0.0);
            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);

            if (updated) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                try {
                    // Dùng chiTietHoaDonList đã lưu từ constructor
                    String invoiceText = HoaDon_Printer.generateInvoiceText(hoaDon, this.chiTietHoaDonList);
                    showInvoicePreview(invoiceText);
                } catch (Exception printEx) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công nhưng có lỗi khi tạo hóa đơn:\n" + printEx.getMessage(),
                                                  "Lỗi In Hóa Đơn", JOptionPane.WARNING_MESSAGE);
                     printEx.printStackTrace();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thanh toán: " + ex.getMessage());
        }
    }

    // Hàm hiển thị hóa đơn (giữ nguyên)
    private void showInvoicePreview(String invoiceText) {
        JDialog previewDialog = new JDialog(this, "Xem trước Hóa Đơn", true);
        previewDialog.setSize(450, 600);
        previewDialog.setLocationRelativeTo(this);
        JTextArea textArea = new JTextArea(invoiceText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton btnOk = new JButton("Đóng");
        btnOk.addActionListener(e -> previewDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnOk);
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.setVisible(true);
    }
}