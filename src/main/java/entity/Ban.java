package entity;

public class Ban {
    private String maBan;
    private int soCho;
    private KhuVuc khuVuc;
    private String loaiBan;
    private String trangThai;

    public Ban() {
    }

    public Ban(String maBan) {
        this.maBan = maBan;
    }

    public Ban(String maBan, int soCho, KhuVuc khuVuc, String loaiBan, String trangThai) {
        this.maBan = maBan;
        this.soCho = soCho;
        this.khuVuc = khuVuc;
        this.loaiBan = loaiBan;
        this.trangThai = trangThai;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public int getSoCho() {
        return soCho;
    }

    public void setSoCho(int soCho) {
        this.soCho = soCho;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        this.khuVuc = khuVuc;
    }

    public String getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(String loaiBan) {
        this.loaiBan = loaiBan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return maBan;
    }

    @Override
    public int hashCode() {
        return maBan.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ban other = (Ban) obj;
        return maBan.equals(other.maBan);
    }
}