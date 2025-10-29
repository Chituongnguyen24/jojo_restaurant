package view.HoaDon;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat; // Import DecimalFormat
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

    // === CẬP NHẬT CONSTRUCTOR ===
    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO,
                                   double tongTienMonAn, double tienGiam, double tienThue, double tongThanhToan, // Các giá trị tiền
                                   List<ChiTietHoaDon> chiTietList) {
        super(owner, "Xác nhận thanh toán", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;
        this.chiTietHoaDonList = chiTietList; // Lưu lại danh sách chi tiết

        setSize(500, 600); // Tăng chiều cao để chứa thêm thông tin
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

        // === CẬP NHẬT PANEL THÔNG TIN ===
        // Tăng số hàng của GridLayout
        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 8)); // Tăng lên 7 hàng, giảm khoảng cách dọc
        infoPanel.setOpaque(false);

        infoPanel.add(new JLabel("Mã Hóa Đơn:"));
        infoPanel.add(new JLabel(hoaDon.getMaHoaDon()));

        infoPanel.add(new JLabel("Khách hàng:"));
        String tenKH = "Khách lẻ";
        if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTenKhachHang() != null) {
            tenKH = hoaDon.getKhachHang().getTenKhachHang();
        }
        infoPanel.add(new JLabel(tenKH));

        // --- Thêm các dòng tiền ---
        infoPanel.add(new JLabel("Tổng tiền (món ăn):"));
        infoPanel.add(new JLabel(moneyFormatter.format(tongTienMonAn)));

        infoPanel.add(new JLabel("Khuyến mãi:"));
        JLabel lblGiamGia = new JLabel("-" + moneyFormatter.format(tienGiam));
        lblGiamGia.setForeground(Color.BLUE); // Màu xanh cho giảm giá
        infoPanel.add(lblGiamGia);

        infoPanel.add(new JLabel("Thuế VAT:")); // Hoặc tên thuế cụ thể
        JLabel lblThue = new JLabel(moneyFormatter.format(tienThue));
        infoPanel.add(lblThue);

        infoPanel.add(new JLabel("Tổng thanh toán:"));
        JLabel lblTongThanhToan = new JLabel(moneyFormatter.format(tongThanhToan));
        lblTongThanhToan.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongThanhToan.setForeground(new Color(200, 80, 70)); // Màu đỏ cho tổng cuối
        infoPanel.add(lblTongThanhToan);
        // --- Kết thúc thêm dòng tiền ---

        infoPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc() != null ? hoaDon.getPhuongThuc() : "Tiền mặt");
        infoPanel.add(cbxPhuongThuc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Danh sách món ăn (JList)
        JList<String> listMonAn = new JList<>(createMonAnVector(chiTietList)); // Dùng chiTietList truyền vào
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
        btnSave.setFont(new Font("Arial", Font.BOLD, 13)); btnSave.addActionListener(this::savePayment);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70)); 
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13)); btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Hàm tạo Vector cho JList (giữ nguyên)
    private Vector<String> createMonAnVector(List<ChiTietHoaDon> chiTietList) {
        Vector<String> vector = new Vector<>();
        if (chiTietList == null || chiTietList.isEmpty()) {
            vector.add("  (Chưa có món nào trong hóa đơn)");
            return vector;
        }
        // Thêm tiêu đề cột cho JList
        vector.add(String.format("%-4s %-25s %8s %15s", "STT", "Tên món", "SL", "Đơn giá"));
        vector.add("-------------------------------------------------------"); 

        DecimalFormat itemPriceFormatter = new DecimalFormat("###,###"); 

        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietHoaDon cthd = chiTietList.get(i);
            String tenMon = "N/A";
            double donGia = 0;
            if(cthd.getMonAn() != null) {
                tenMon = cthd.getMonAn().getTenMonAn() != null ? cthd.getMonAn().getTenMonAn() : "Lỗi tên món";
                donGia = cthd.tinhThanhTien();
            }

            // Cắt bớt tên món nếu quá dài
            if (tenMon.length() > 24) {
                tenMon = tenMon.substring(0, 21) + "...";
            }

            vector.add(String.format("%-4d %-25s %8d %15s",
                                     i + 1,
                                     tenMon,
                                     cthd.getSoLuong(),
                                     itemPriceFormatter.format(donGia))); // Format đơn giá
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
            hoaDon.setPhuongThuc(phuongThuc);
            hoaDon.setDaThanhToan(true);
            hoaDon.setGioRa(LocalDateTime.now());
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