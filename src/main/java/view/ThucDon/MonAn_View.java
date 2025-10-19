package view.ThucDon;

import javax.swing.*;
import java.awt.*;

public class MonAn_View extends JFrame {
    public MonAn_View() {
        setTitle("Quản lý món ăn");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== Nội dung giao diện =====
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));

        JLabel title = new JLabel("QUẢN LÝ MÓN ĂN", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(title, BorderLayout.NORTH);

        // Ví dụ: khu vực danh sách món ăn
        JTable table = new JTable(new Object[][]{
                {"MA001", "Cơm chiên", "Cơm", 45000},
                {"MA002", "Phở bò", "Phở", 50000},
        }, new Object[]{"Mã món", "Tên món", "Loại", "Giá"});

        JScrollPane scroll = new JScrollPane(table);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Các nút thao tác
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MonAn_View().setVisible(true));
    }
}

