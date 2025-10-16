package view.Ban;

import dao.DatBan_DAO;
import entity.PhieuDatBan;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DatBan_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private DatBan_DAO datBanDAO;

    public DatBan_View() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        datBanDAO = new DatBan_DAO();

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        loadDataToTable();
    }

    private JPanel createTitlePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐẶT BÀN", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnl.add(lblTitle, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createTablePanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(new TitledBorder("Danh sách phiếu đặt bàn"));

        String[] cols = {"Mã phiếu", "Tên khách hàng", "Mã bàn", "Thời gian đặt", "Tiền cọc"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(table);
        pnl.add(scroll, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createControlPanel() {
        JPanel pnl = new JPanel(new BorderLayout());

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tìm kiếm");
        pnlSearch.add(new JLabel("Tìm khách hàng:"));
        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnl.add(pnlSearch, BorderLayout.WEST);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        pnl.add(pnlButtons, BorderLayout.EAST);

        btnLamMoi.addActionListener(e -> loadDataToTable());
        btnTimKiem.addActionListener(e -> timKiemPhieuDatBan());

        return pnl;
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        List<PhieuDatBan> ds = datBanDAO.getAllPhieuDatBan();
        for (PhieuDatBan p : ds) {
            model.addRow(new Object[]{
                    p.getMaPhieu(),
                    p.getKhachHang().getTenKhachHang(),
                    p.getBan().getMaBan(),
                    p.getThoiGianDat(),
                    p.getTienCoc()
            });
        }
    }

    private void timKiemPhieuDatBan() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên khách hàng để tìm kiếm!");
            return;
        }

        List<PhieuDatBan> ds = datBanDAO.timTheoTenKhachHang(keyword);
        model.setRowCount(0);
        for (PhieuDatBan p : ds) {
            model.addRow(new Object[]{
                    p.getMaPhieu(),
                    p.getKhachHang().getTenKhachHang(),
                    p.getBan().getMaBan(),
                    p.getThoiGianDat(),
                    p.getTienCoc()
            });
        }
    }
}
