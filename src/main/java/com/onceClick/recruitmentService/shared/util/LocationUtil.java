package com.onceClick.recruitmentService.shared.util;

public class LocationUtil {

    /**
     * Dịch từ tên tiếng Việt (từ Frontend) sang mã Province Code lưu trong DB
     */
    public static String getDbCodeFromName(String provinceName) {
        if (provinceName == null || provinceName.trim().isEmpty() || provinceName.equalsIgnoreCase("Tất cả địa điểm")) {
            return null;
        }

        return switch (provinceName) {
            case "Hà Nội" -> "100000";
            case "TP. Hồ Chí Minh" -> "700000";
            case "Đà Nẵng" -> "500000";
            case "Bình Dương" -> "750000";
            case "Đồng Nai" -> "760000";
            case "Hải Phòng" -> "180000";
            case "Cần Thơ" -> "940000";
            case "Khánh Hòa" -> "570000";
            // Thêm các mapping khác nếu Database của bạn có thêm mã (ví dụ: case "Cần Thơ" -> "900000")
            default -> provinceName; // Nếu không có trong danh sách trên thì trả về nguyên gốc để DB tự xử lý
        };
    }
}