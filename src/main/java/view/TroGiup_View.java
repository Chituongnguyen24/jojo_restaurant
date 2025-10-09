package view;

import javax.swing.*;
import java.awt.*;

public class TroGiup_View extends JPanel {

    private JLabel lblTitle;
    private JTextArea txtNoiDung;
    private JScrollPane scrollPane;

    public TroGiup_View() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        lblTitle = new JLabel("🛈 Trợ Giúp", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 102, 163));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // ======= Nội dung =======
        txtNoiDung = new JTextArea();
        txtNoiDung.setEditable(false);
        txtNoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setText(
        	    "💡 Hướng dẫn sử dụng hệ thống:\n\n" +
        	    "1️⃣ Chọn chức năng ở thanh menu bên trái để thực hiện công việc.\n" +
        	    "2️⃣ Các mục bao gồm:\n" +
        	    "   • Quản lý bàn\n" +
        	    "   • Quản lý nhân viên\n" +
        	    "   • Quản lý món ăn\n" +
        	    "   • Quản lý hóa đơn\n" +
        	    "   • Báo cáo thống kê\n" +
        	    "3️⃣ Nếu cần hỗ trợ thêm, vui lòng liên hệ bộ phận kỹ thuật."
        	);


        scrollPane = new JScrollPane(txtNoiDung);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        // ======= Thêm vào layout =======
        add(lblTitle, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}
