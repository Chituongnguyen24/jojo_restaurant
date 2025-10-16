package view.Ban;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChiTietPhieuDatBan_View extends JPanel {
    private JTable table;
    private JTextField txtMaPhieu, txtTenKH, txtSoBan, txtNgayDat, txtGioDat, txtSoNguoi, txtGhiChu;

    public ChiTietPhieuDatBan_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(252, 249, 244));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Chi tiết phiếu đặt bàn");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(40, 30, 20));

        JLabel subtitle = new JLabel("Xem thông tin chi tiết và món ăn đã đặt");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 90, 80));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setBackground(new Color(180, 100, 50));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Arial", Font.BOLD, 13));
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== THÔNG TIN CHUNG PHIẾU =====
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 15, 10));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(200, 150, 100)), "Thông tin phiếu", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(10, 20, 10, 20)
        ));

        txtMaPhieu = createTextField("PD001");
        txtTenKH = createTextField("Nguyễn Văn A");
        txtSoBan = createTextField("Bàn 5");
        txtNgayDat = createTextField("2025-10-09");
        txtGioDat = createTextField("18:30");
        txtSoNguoi = createTextField("4 người");
        txtGhiChu = createTextField("Sinh nhật - yêu cầu bánh kem");

        infoPanel.add(createLabel("Mã phiếu:"));
        infoPanel.add(txtMaPhieu);
        infoPanel.add(createLabel("Tên khách hàng:"));
        infoPanel.add(txtTenKH);
        infoPanel.add(createLabel("Số bàn:"));
        infoPanel.add(txtSoBan);
        infoPanel.add(createLabel("Ngày đặt:"));
        infoPanel.add(txtNgayDat);
        infoPanel.add(createLabel("Giờ đặt:"));
        infoPanel.add(txtGioDat);
        infoPanel.add(createLabel("Số người:"));
        infoPanel.add(txtSoNguoi);
        infoPanel.add(createLabel("Ghi chú:"));
        infoPanel.add(txtGhiChu);

        add(infoPanel, BorderLayout.CENTER);

        // ===== DANH SÁCH MÓN ĂN =====
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(200, 150, 100)), "Danh sách món ăn đã đặt", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(10, 20, 20, 20)
        ));

        String[] cols = {"Mã món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        Object[][] data = {
                {"MN001", "Gỏi cuốn tôm thịt", 2, "30,000", "60,000"},
                {"MN002", "Lẩu thái hải sản", 1, "200,000", "200,000"},
                {"MN003", "Trà đào cam sả", 4, "25,000", "100,000"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho chỉnh sửa
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 220, 200));

        JScrollPane scroll = new JScrollPane(table);
        tablePanel.add(scroll, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.SOUTH);

        // ===== FOOTER (TỔNG TIỀN + NÚT) =====
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTong = new JLabel("Tổng tiền: 360,000 VNĐ");
        lblTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblTong.setForeground(new Color(180, 60, 30));

        JButton btnInPhieu = new JButton("🖨 In phiếu");
        btnInPhieu.setBackground(new Color(60, 140, 60));
        btnInPhieu.setForeground(Color.WHITE);
        btnInPhieu.setFocusPainted(false);
        btnInPhieu.setFont(new Font("Arial", Font.BOLD, 13));
        btnInPhieu.setBorder(new EmptyBorder(8, 15, 8, 15));

        footer.add(lblTong, BorderLayout.WEST);
        footer.add(btnInPhieu, BorderLayout.EAST);

        add(footer, BorderLayout.PAGE_END);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(new Color(70, 50, 30));
        return lbl;
    }

    private JTextField createTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font("Arial", Font.PLAIN, 13));
        txt.setEditable(false);
        txt.setBackground(Color.WHITE);
        txt.setBorder(new LineBorder(new Color(210, 180, 140)));
        return txt;
    }
}
