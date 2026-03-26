package battleship.view;

public class GameWindow {
	//Chuyển phần giao diện tạm thời bên main sửa sang bên này 
	// Thêm phần giao diện ở đây
	
	//Controller làm việc của Model (Tính toán dữ liệu): Hàm setUpShip() sử dụng Random để dò tìm vị trí và đặt tàu. Theo lý thuyết, việc khởi tạo trạng thái dữ liệu (đặt tàu ở đâu) phải nằm trong Model (Board.java). Controller chỉ nên gọi p1board.autoPlaceShips().

	//Controller làm việc của View (Xử lý giao diện): Hàm showP1Ships() trực tiếp gọi setBackground(Color.DARK_GRAY) và setText("Tàu"). Nguyên tắc là Controller không được phép biết màu sắc hay text hiển thị là gì. Lẽ ra View phải tự có hàm drawShipAt(x, y) và Controller chỉ gọi hàm đó.

	//Chứa thư viện UI: Việc Controller trực tiếp gọi JOptionPane.showMessageDialog khiến nó bị dính chặt với thư viện giao diện Swing.
}
