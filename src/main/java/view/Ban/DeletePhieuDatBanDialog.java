package view.Ban;

import dao.DatBan_DAO;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeletePhieuDatBanDialog extends JDialog {
    private DatBan_DAO datBanDAO = new DatBan_DAO();
    private JFrame parentFrame;
    private String maPhieu;

    public DeletePhieuDatBanDialog(JFrame parent, String maPhieu) {
        super(parent, "Xóa phiếu đặt bàn", true);
        this.parentFrame = parent;
        this.maPhieu = maPhieu;
        initializeComponents();
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Confirmation message
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JLabel lblConfirm = new JLabel("Bạn có chắc chắn muốn xóa phiếu đặt bàn mã: " + maPhieu + "?");
        lblConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(lblConfirm);

        JLabel lblReason = new JLabel("Lý do xóa:");
        lblReason.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePanel.add(lblReason);

        JTextField txtReason = new JTextField(20);
        txtReason.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePanel.add(txtReason);

        add(messagePanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirm = new JButton("Xóa");
        btnConfirm.setBackground(new Color(220, 53, 69));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> confirmDelete(txtReason.getText()));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(Color.GRAY);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void confirmDelete(String lyDo) {
        if (datBanDAO.cancelPhieuDatBan(maPhieu, lyDo)) { // Use cancel method for soft delete
            JOptionPane.showMessageDialog(this, "Xóa phiếu thành công!");
            dispose();
            // Refresh parent
            if (parentFrame.getContentPane() instanceof DatBan_View) {
                ((DatBan_View) parentFrame.getContentPane()).updateTablesDisplay();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Xóa phiếu thất bại!");
        }
    }

    // Static method to show dialog
    public static void showDeleteDialog(JFrame parent, DatBan_DAO dao, String maPhieu) {
        PhieuDatBan phieu = dao.getPhieuDatBanById(maPhieu);
        if (phieu == null) {
            JOptionPane.showMessageDialog(parent, "Không tìm thấy phiếu!");
            return;
        }
        new DeletePhieuDatBanDialog(parent, maPhieu).setVisible(true);
    }
}