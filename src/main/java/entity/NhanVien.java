package entity;

import java.time.LocalDate;

public class NhanVien {

    private static final String SDT_REGEX = "^0\\d{9}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    
    private String maNV;
    private String tenNhanVien;
    private boolean gioiTinh;
    private String sdt;
    private String email;
    private LocalDate ngaySinh;
    private TaiKhoan taiKhoan;

    public NhanVien() {}
    
    public NhanVien(String maNV) {
		super();
		this.maNV = maNV;
	}

	public NhanVien(String maNV, String tenNhanVien, boolean gioiTinh, LocalDate ngaySinh, String sdt, String email, TaiKhoan taiKhoan) {
        this.maNV = maNV;
        setTenNhanVien(tenNhanVien);
        setGioiTinh(gioiTinh);
        setNgaySinh(ngaySinh);
        setSdt(sdt);
        setEmail(email);
        setTaiKhoan(taiKhoan);
    }

    public String getMaNV() {
        return maNV;
    }
    
    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
    
    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public String getSdt() {
        return sdt;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống.");
        }
        this.maNV = maNV;
    }
    
    public void setTenNhanVien(String tenNhanVien) {
        if (tenNhanVien == null || tenNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống.");
        }
        this.tenNhanVien = tenNhanVien;
    }
    
    public void setSdt(String sdt) {
        if (sdt == null || !sdt.matches(SDT_REGEX)) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 0).");
        }
        this.sdt = sdt;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches(EMAIL_REGEX)) {
                throw new IllegalArgumentException("Email không đúng định dạng.");
            }
        }
        this.email = email;
    }
    
    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống.");
        }
        if (ngaySinh.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không thể là ngày ở tương lai.");
        }
        if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Nhân viên phải đủ 18 tuổi.");
        }
        this.ngaySinh = ngaySinh;
    }
    
    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
    
    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

	@Override
	public String toString() {
		return "NhanVien [maNV=" + maNV + ", tenNhanVien=" + tenNhanVien + ", gioiTinh=" + gioiTinh + ", sdt=" + sdt
				+ ", email=" + email + ", ngaySinh=" + ngaySinh + ", taiKhoan=" + taiKhoan + "]";
	}

   
}