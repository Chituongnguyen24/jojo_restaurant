package view.Ban;

import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import entity.ChiTietPhieuDatBan;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChiTietPhieuDatBan_View extends JPanel {
    private JTable table;
    private JTextField txtMaPhieu, txtTenKH, txtSoBan, txtNgayDat, txtGioDat, txtSoNguoi, txtGhiChu;
    private PhieuDatBan phieu;
    private DatBan_DAO daoDatBan = new DatBan_DAO();

    public ChiTietPhieuDatBan_View(PhieuDatBan phieu) {
        this.phieu = phieu;
        setLayout(new BorderLayout());
        setBackground(new Color(252, 249, 244));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Chi tiết phiếu đặt bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(40, 30, 20));

        JLabel subtitle = new JLabel("Xem thông tin chi tiết và món ăn đã đặt");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 90, 80));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subtitlePanel.setOpaque(false);
        subtitlePanel.add(subtitle);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(titlePanel);
        titleContainer.add(subtitlePanel);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setBackground(new Color(180, 100, 50));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnBack.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose());

        header.add(titleContainer, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);


        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.CENTER);


        JPanel tablePanel = createMonAnPanel();
        add(tablePanel, BorderLayout.SOUTH);


        JPanel footer = createFooterPanel();
        add(footer, BorderLayout.PAGE_END);
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 20, 15));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(200, 150, 100), 2), "Thông tin phiếu", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 30, 20)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        txtMaPhieu = createTextField(phieu.getMaPhieu());
        txtTenKH = createTextField(phieu.getKhachHang().getTenKhachHang());
        txtSoBan = createTextField(phieu.getBan().getMaBan());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String thoiGianStr = phieu.getThoiGianDat().format(formatter);
        txtNgayDat = createTextField(thoiGianStr.split(" ")[0]); // Date part
        txtGioDat = createTextField(thoiGianStr.split(" ")[1]); // Time part
        txtSoNguoi = createTextField("N/A"); // Placeholder if not available
        txtGhiChu = createTextField("N/A"); // Placeholder if not available

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

        return infoPanel;
    }

    private JPanel createMonAnPanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(new Color(200, 150, 100), 2), "Danh sách món ăn đã đặt", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 30, 20)),
                new EmptyBorder(20, 30, 20, 30)
        ));

        List<ChiTietPhieuDatBan> chiTietList = daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu());
        String[] cols = {"Mã món", "Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        Object[][] data = new Object[chiTietList.size()][5];
        double tongTien = 0.0;
        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietPhieuDatBan ct = chiTietList.get(i);
            MonAn mon = ct.getMonAn() != null ? ct.getMonAn() : new MonAn();
            data[i][0] = mon.getMaMonAn() != null ? mon.getMaMonAn() : "N/A";
            data[i][1] = mon.getTenMonAn() != null ? mon.getTenMonAn() : "N/A";
            data[i][2] = ct.getSoLuongMonAn();
            data[i][3] = String.format("%,.0f VNĐ", ct.getDonGia());
            double thanhTien = ct.tinhTongTien();
            data[i][4] = String.format("%,.0f VNĐ", thanhTien);
            tongTien += thanhTien;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 220, 200));
        table.getTableHeader().setForeground(new Color(40, 30, 20));
        table.setGridColor(new Color(200, 150, 100));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 150, 100), 1));
        tablePanel.add(scroll, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 30, 20, 30));

        List<ChiTietPhieuDatBan> chiTietList = daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu());
        double tongTien = 0.0;
        for (ChiTietPhieuDatBan ct : chiTietList) {
            tongTien += ct.tinhTongTien();
        }

        JLabel lblTong = new JLabel("Tổng tiền: " + String.format("%,.0f VNĐ", tongTien));
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTong.setForeground(new Color(180, 60, 30));

        JButton btnInPhieu = new JButton("🖨 In phiếu");
        btnInPhieu.setBackground(new Color(60, 140, 60));
        btnInPhieu.setForeground(Color.WHITE);
        btnInPhieu.setFocusPainted(false);
        btnInPhieu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInPhieu.setBorder(new EmptyBorder(12, 25, 12, 25));
        btnInPhieu.addActionListener(e -> JOptionPane.showMessageDialog(this, "In phiếu thành công! (Placeholder)"));

        footer.add(lblTong, BorderLayout.WEST);
        footer.add(btnInPhieu, BorderLayout.EAST);

        return footer;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(70, 50, 30));
        return lbl;
    }

    private JTextField createTextField(String text) {
        JTextField txt = new JTextField(text);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setEditable(false);
        txt.setBackground(new Color(255, 250, 240));
        txt.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 150, 100), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }
}