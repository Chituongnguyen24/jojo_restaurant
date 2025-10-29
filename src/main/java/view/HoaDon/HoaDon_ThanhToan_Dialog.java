package view.HoaDon;

import dao.HoaDon_DAO;
import dao.Ban_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import enums.TrangThaiBan;
import view.HoaDon.HoaDon_Printer;

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
    private Ban_DAO banDAO;
    private double tongTienHoaDon;
    private Runnable onSuccessCallback;

    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO, double tongTien) {
        this(owner, hoaDon, hoaDonDAO, tongTien, null);
    }

    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO, double tongTien, Runnable onSuccessCallback) {
        super(owner, "Xác nhận thanh toán", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;
        this.banDAO = new Ban_DAO();
        this.tongTienHoaDon = tongTien;
        this.onSuccessCallback = onSuccessCallback;

        setSize(450, 350);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Xác nhận thanh toán", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(createLabel("Mã Hóa Đơn:"));
        formPanel.add(createValueLabel(hoaDon.getMaHoaDon()));
        
        formPanel.add(createLabel("Bàn:"));
        formPanel.add(createValueLabel(hoaDon.getBan() != null ? hoaDon.getBan().getMaBan() : "N/A"));
        
        formPanel.add(createLabel("Khách hàng:"));
        String tenKH = "Khách vãng lai";
        if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTenKhachHang() != null) {
            tenKH = hoaDon.getKhachHang().getTenKhachHang();
        }
        formPanel.add(createValueLabel(tenKH));
        
        formPanel.add(createLabel("Tổng tiền:"));
        JLabel lblTongTien = new JLabel(String.format("%,.0f VNĐ", tongTien));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(220, 53, 69));
        formPanel.add(lblTongTien);
        
        formPanel.add(createLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc() != null ? hoaDon.getPhuongThuc() : "Tiền mặt");
        cbxPhuongThuc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cbxPhuongThuc);
        
        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("✓ Xác nhận & In Hóa Đơn");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSave.addActionListener(this::savePayment);
        
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(100, 116, 139));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(15, 23, 42));
        return label;
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
                // Cập nhật trạng thái bàn về TRỐNG
                if (hoaDon.getBan() != null) {
                    hoaDon.getBan().setTrangThai(TrangThaiBan.TRONG);
                    banDAO.capNhatBan(hoaDon.getBan());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Thanh toán thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // In hóa đơn
                try {
                    List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDon.getMaHoaDon());
                    String invoiceText = HoaDon_Printer.generateInvoiceText(hoaDon, chiTietList);
                    showInvoicePreview(invoiceText);
                } catch (Exception printEx) {
                    JOptionPane.showMessageDialog(this, 
                        "Thanh toán thành công nhưng có lỗi khi tạo hóa đơn:\n" + printEx.getMessage(),
                        "Lỗi In Hóa Đơn", 
                        JOptionPane.WARNING_MESSAGE);
                    printEx.printStackTrace();
                }
                
                // Gọi callback nếu có
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
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
        previewDialog.setSize(500, 650);
        previewDialog.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea(invoiceText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        
        JButton btnOk = new JButton("✓ Đóng");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnOk.setBackground(new Color(40, 167, 69));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.setBorder(new EmptyBorder(10, 30, 10, 30));
        btnOk.addActionListener(e -> previewDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        buttonPanel.add(btnOk);
        
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.setVisible(true);
    }
}