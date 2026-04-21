# 🚢 Battleship Game - JavaFX Edition 🚀

Dự án Battleship (Bắn Tàu) cổ điển đã được nâng cấp toàn diện từ Java Swing sang **JavaFX 17** với giao diện hiện đại, tính năng phong phú, tích hợp Gradle và hệ thống AI hoàn chỉnh.

## 🌟 Tính năng nổi bật
* **Giao diện Modern Naval:** Các màn hình (Menu, Setup, Battle) được thiết kế hiện đại bằng CSS và các Node tùy chỉnh.
* **Trải nghiệm kéo thả (Drag & Drop):** Hỗ trợ kéo thuyền từ bến đỗ thả trực tiếp lên sa bàn.
* **Xoay tàu linh hoạt:** Tính năng **Click Đúp (Double-Click)** cho phép bạn xoay 90 độ chiếc tàu ngay tại đúng khớp nối (điểm pivot) mà bạn vừa click vào.
* **Mô hình AI đa mức độ:** Tích hợp Bot đối thủ với 3 mức độ ngắm bắn thông minh (Easy, Medium, Hard).
* **Kiến trúc hướng sự kiện:** Tách biệt triệt để Logic cốt lõi (Model) và Giao diện UI (Screens/Components) thông qua các Listener.

## 📂 Cấu trúc thư mục

```text
BattleshipGUI/
├── src/main/resources/        # Tài nguyên tĩnh (app.css, hình ảnh)
├── src/battleship/            # Cấu trúc mã nguồn chính
│   ├── model/                 # Logic lõi, kết cấu bàn cờ
│   ├── ai/                    # Khối dữ liệu AI thông minh
│   └── fx/                    # Hệ thống hiển thị bằng JavaFX
```

## 🛠️ Yêu cầu môi trường
* **Java SDK:** Tối thiểu phiên bản 23 (Hoặc các bản Java hỗ trợ JavaFX mới).
* Mạng Internet để Gradle tự động kéo các gói dependencies lần đầu.

## 🚀 Hướng dẫn chạy (How to run)

Ứng dụng được cấu hình đóng gói thông qua công cụ build **Gradle**. Bạn không cần cài đặt cấu hình IDE lằng nhằng, chỉ cần làm theo bước sau.

Mở Terminal (hoặc Powershell/Command Prompt) trỏ tại thư mục gốc của dự án.

**1. Đối với hệ điều hành Windows:**
(Nếu máy tính bạn đã cài sẵn `gradle` toàn cầu)
```powershell
gradle run
```
(Nếu sử dụng trình Wrapper đính kèm)
```powershell
.\gradlew run
```

**2. Đối với Linux / macOS:**
```bash
./gradlew run
```

Lệnh trên sẽ tự động tải thư viện liên quan của OpenJFX, biên dịch mã nguồn và khởi động trò chơi ngay lập tức. Chúc bạn chơi game vui vẻ!
