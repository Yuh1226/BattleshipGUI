# 🚢 Battleship Game - Java Swing (MVC Architecture)

Dự án này là một trò chơi bắn tàu (Battleship) cổ điển được xây dựng bằng ngôn ngữ **Java**, sử dụng thư viện **Swing** để tạo giao diện đồ họa (GUI). Code được thiết kế chặt chẽ theo mô hình **MVC (Model-View-Controller)**, giúp tách biệt logic xử lý và hiển thị.

## 🌟 Tính năng nổi bật
* **Giao diện trực quan:** Hệ thống lưới 10x10 với các nút bấm tương tác mượt mà.
* **Mô hình 2 bảng:** Hiển thị đồng thời bảng của Người chơi (Hạm đội) và bảng của Đối thủ (Vùng biển bắn phá).
* **Đặt tàu ngẫu nhiên:** Tự động sắp xếp 5 loại tàu (dài 5, 4, 3, 3, 2) cho cả hai bên dựa trên logic kiểm tra va chạm và tràn viền.
* **Kiến trúc MVC:** * **Model:** Quản lý tọa độ `Node`, hạm đội `Ship` và logic bàn cờ `Board`.
    * **View:** Xử lý hiển thị lưới nút bấm qua `BoardPanel`.
    * **Controller:** Điều phối sự kiện click chuột, cập nhật logic và phản hồi lên giao diện.

## 📂 Cấu trúc thư mục (Package Structure)
Dự án được tổ chức như sau để đảm bảo tính chuyên nghiệp:

```text
BattleshipGUI/
└── src/
    ├── battleship/
    │   └── Main.java             # Khởi tạo và chạy ứng dụng
    ├── battleship.model/
    │   ├── Node.java             # Trạng thái ô vuông (SHIP, HIT, MISS,...)
    │   ├── Ship.java             # Thuộc tính tàu (length, direction, location)
    │   └── Board.java            # Logic bàn cờ và kiểm tra đặt tàu
    ├── battleship.view/
    │   └── BoardPanel.java       # Giao diện lưới 100 nút bấm
    └── battleship.controller/
        └── GameController.java   # Điều khiển lượt chơi và sự kiện
