package view.HoaDon;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import view.HoaDon.HoaDon_Printer; // Cần import lớp này

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.List;

public class HoaDon_ThanhToan_Dialog extends JDialog {
    private JComboBox<String> cbxPhuongThuc;
    private JButton btnSave, btnCancel;
    private HoaDon hoaDon;
    private HoaDon_DAO hoaDonDAO;
    private double tongTienHoaDon;

    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO, double tongTien) {
        super(owner, "Xác nhận thanh toán", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;
        this.tongTienHoaDon = tongTien;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Xác nhận thanh toán", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Mã Hóa Đơn:"));
        formPanel.add(new JLabel(hoaDon.getMaHoaDon()));
        formPanel.add(new JLabel("Khách hàng:"));
        String tenKH = "Khách lẻ";
        if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTenKhachHang() != null) {
            tenKH = hoaDon.getKhachHang().getTenKhachHang();
        }
        formPanel.add(new JLabel(tenKH));
        formPanel.add(new JLabel("Tổng tiền:"));
        JLabel lblTongTien = new JLabel(String.format("%,.0f VNĐ", tongTien));
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(200, 80, 70));
        formPanel.add(lblTongTien);
        formPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc() != null ? hoaDon.getPhuongThuc() : "Tiền mặt");
        formPanel.add(cbxPhuongThuc);
        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("Xác nhận & In Hóa Đơn");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.addActionListener(this::savePayment);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

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
                    List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDon.getMaHoaDon());
                    String invoiceText = HoaDon_Printer.generateInvoiceText(hoaDon, chiTietList);
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