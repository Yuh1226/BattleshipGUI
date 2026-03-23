🚢 Battleship Game - Java Swing (MVC Architecture)
Dự án này là một trò chơi bắn tàu (Battleship) cổ điển được xây dựng bằng ngôn ngữ Java, sử dụng thư viện Swing cho giao diện đồ họa (GUI). Code được tổ chức theo mô hình kiến trúc MVC (Model-View-Controller) để đảm bảo tính dễ mở rộng và bảo trì.

🌟 Tính năng nổi bật
Giao diện trực quan: Sử dụng lưới 10x10 với các nút bấm (JButton) để tương tác.

Chế độ 2 bảng: Hiển thị song song bảng hạm đội của người chơi và vùng biển của đối thủ.

Sắp xếp tàu ngẫu nhiên: Tàu của cả hai bên được đặt tự động và ngẫu nhiên dựa trên các quy tắc logic (không chồng lấn, không tràn viền).

Kiến trúc chuẩn: Tách biệt hoàn toàn giữa logic tính toán (Model) và hiển thị đồ họa (View).

📂 Cấu trúc dự án
Dự án được chia thành các package chuyên biệt:

battleship: Chứa file Main.java để khởi chạy ứng dụng.

battleship.model: Quản lý dữ liệu và logic cốt lõi.

Node.java: Định nghĩa trạng thái từng ô vuông (Trống, Tàu, Trúng, Trượt).

Ship.java: Quản lý thông tin tàu (độ dài, hướng, tọa độ).

Board.java: Xử lý logic bàn cờ, đặt tàu và kiểm tra va chạm.

battleship.view: Chứa các thành phần giao diện.

BoardPanel.java: Vẽ lưới 100 nút bấm và cập nhật màu sắc hiển thị.

battleship.controller: Cầu nối giữa Model và View.

GameController.java: Lắng nghe sự kiện click chuột và điều phối lượt chơi.

🛠 Hướng dẫn cài đặt và chạy (Eclipse)
Tạo Project: Mở Eclipse, chọn File > New > Java Project, đặt tên là BattleshipGUI.

Tạo Packages: Chuột phải vào thư mục src, tạo các package theo cấu trúc trên.

Thêm mã nguồn: Copy các file .java tương ứng vào từng package.

Chạy Game: Mở file Main.java trong package battleship, chuột phải chọn Run As > Java Application.

🚀 Công nghệ sử dụng
Ngôn ngữ: Java (JDK 17+).

Giao diện: Java Swing & AWT.

IDE: Eclipse.
