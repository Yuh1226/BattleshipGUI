package battleship.fx;

import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {
    public enum Language {
        EN("English"),
        VI("Tiếng Việt");

        private final String displayName;
        Language(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private static Language currentLanguage = Language.EN;
    private static final Map<String, Map<Language, String>> translations = new HashMap<>();

    static {
        // Common
        add("back", "Back", "Quay lại");
        add("back_to_menu", "Main Menu", "Menu chính");
        add("cancel", "Cancel", "Hủy");
        add("continue", "Continue", "Tiếp tục");
        add("settings", "Settings", "Cài đặt");
        add("return_hq", "Main Menu", "Menu chính");
        
        // Menu
        add("start_mission", "START MISSION", "BẮT ĐẦU CHIẾN DỊCH");
        add("tactical_warfare", "Tactical Naval Warfare", "Chiến tranh hạm đội chiến thuật");
        
        // Difficulty
        add("choose_difficulty", "Select Difficulty", "Chọn độ khó");
        add("easy", "Easy", "Dễ");
        add("normal", "Medium", "Trung bình");
        add("hard", "Hard", "Khó");
        
        // Setup
        add("fleet_deployment", "FLEET DEPLOYMENT", "TRIỂN KHAI HẠM ĐỘI");
        add("position_assets", "Position your naval assets on the grid.", "Sắp xếp các chiến hạm của bạn lên bản đồ.");
        add("auto_deploy", "Auto-Deploy", "Tự động sắp xếp");
        add("clear_board", "Clear Board", "Xóa bàn chơi");
        add("rotate_h", "Rotate: Horizontal", "Xoay: Ngang");
        add("rotate_v", "Rotate: Vertical", "Xoay: Dọc");
        add("start_battle", "START BATTLE", "BẮT ĐẦU TRẬN ĐÁU");
        add("available_fleet", "AVAILABLE FLEET - DRAG OR CLICK TO SELECT", "HẠM ĐỘI HIỆN CÓ - KÉO HOẶC CLICK ĐỂ CHỌN");
        add("place_all_ships", "Place all 5 ships to continue.", "Hãy đặt tất cả 5 tàu để bắt đầu.");
        
        // Battle
        add("your_turn", "Your turn", "Lượt của bạn");
        add("enemy_turn", "Enemy turn", "Lượt đối thủ");
        add("pick_target", "Pick a target.", "Chọn mục tiêu tấn công.");
        add("hit", "Hit!", "Trúng mục tiêu!");
        add("miss", "Miss.", "Trượt.");
        add("enemy_thinking", "Enemy is thinking...", "Đối thủ đang suy nghĩ...");
        add("win", "You win!", "Chiến thắng!");
        add("lose", "You lose.", "Thất bại.");
        
        // Game Over
        add("mission_complete", "MISSION COMPLETE", "CHIẾN DỊCH KẾT THÚC");
        add("new_operation", "New Operation", "Chiến dịch mới");
        add("total_shots", "TOTAL SHOTS", "TỔNG SỐ LƯỢT BẮN");
        add("hits_confirmed", "HITS CONFIRMED", "SỐ PHÁT TRÚNG");
        add("accuracy", "ACCURACY", "ĐỘ CHÍNH XÁC");
        add("ships_sunk", "SHIPS SUNK", "SỐ TÀU ĐÃ CHÌM");
        add("match_duration", "MATCH DURATION", "THỜI GIAN TRẬN ĐẤU");
        add("ai_difficulty", "AI DIFFICULTY", "ĐỘ KHÓ AI");

        // Status Texts - Setup
        add("status_place_all", "Place all 5 ships to continue.", "Hãy đặt tất cả 5 tàu để tiếp tục.");
        add("status_auto_done", "Ships placed automatically. Ready to battle.", "Hạm đội đã sẵn sàng. Trận đấu bắt đầu.");
        add("status_need_all", "You must place all ships before starting.", "Bạn phải đặt hết tàu trước khi bắt đầu.");
        add("status_moving", "Moving ship (Length: %d)", "Đang di chuyển tàu (Độ dài: %d)");
        add("status_move_cancelled", "Ship placement cancelled. Returned to fleet.", "Hủy di chuyển. Tàu đã quay về hạm đội.");
        add("status_move_success", "Ship moved successfully.", "Di chuyển tàu thành công.");
        add("status_invalid_pos", "Invalid position! Ship returned to fleet.", "Vị trí không hợp lệ! Tàu quay về hạm đội.");
        add("status_no_rotate_space", "Cannot rotate: Not enough space!", "Không đủ chỗ để xoay tàu!");
        add("status_ship_removed", "Ship removed and returned to fleet.", "Đã gỡ tàu và đưa về hạm đội.");
        add("status_ship_rotated", "Ship rotated.", "Đã xoay tàu.");
        add("status_rotate_fail", "Cannot rotate: Collision or out of bounds.", "Không thể xoay: Va chạm hoặc ngoài biên.");
        add("status_invalid_touch", "Invalid position: Ships must not touch each other.", "Vị trí sai: Tàu không được chạm nhau.");
        add("status_all_placed", "All ships placed. Press Continue.", "Đã đặt xong. Nhấn Tiếp tục để bắt đầu.");
        add("status_next_ship", "Ship placed. Next ship length: %d", "Đã đặt tàu. Độ dài tàu tiếp theo: %d");

        // Status Texts - Battle
        add("status_pick_target", "Pick a target.", "Chọn mục tiêu tấn công.");
        add("status_hit_sunk", "Hit! Enemy ship sunk.", "Trúng! Đã tiêu diệt tàu địch.");
        add("status_hit", "Hit!", "Trúng mục tiêu!");
        add("status_miss_thinking", "Miss. Enemy is thinking...", "Trượt. Đối thủ đang suy nghĩ...");
        add("status_enemy_hit", "Enemy hit your ship.", "Đối thủ bắn trúng tàu bạn.");
        add("status_enemy_miss", "Enemy missed.", "Đối thủ bắn trượt.");

        // Battle Log Events
        add("log_battle_started", "Battle started!", "Trận đấu bắt đầu!");
        add("log_time_out", "TIME OUT!", "HẾT GIỜ!");
        add("log_player_sunk", "YOU SUNK A SHIP (Size: %d) at %s", "BẠN ĐÃ HẠ TÀU ĐỐI THỦ (Cỡ %d) tại %s");
        add("log_player_hit", "Player hit at %s", "Bạn bắn trúng tại %s");
        add("log_player_miss", "Player missed at %s", "Bạn bắn trượt tại %s");
        add("log_enemy_hit", "Enemy hit your ship at %s", "Đối thủ bắn trúng tàu bạn tại %s");
        add("log_enemy_sunk", "ENEMY SUNK YOUR SHIP (Size: %d)!", "ĐỐI THỦ ĐÃ HẠ TÀU CỦA BẠN (Cỡ %d)!");
        add("log_enemy_miss", "Enemy missed at %s", "Đối thủ bắn trượt tại %s");

        // Turn Headers
        add("turn_player", "Your Turn", "Lượt của bạn");
        add("turn_enemy", "Enemy Turn", "Lượt đối thủ");
        add("turn_game_over", "Game Over", "Kết thúc");

        // Settings
        add("lang_label", "Language / Ngôn ngữ:", "Ngôn ngữ:");
        add("audio_label", "Audio / Âm thanh:", "Âm thanh:");
        add("enable_sound", "Enable Sound Effects", "Bật hiệu ứng âm thanh");
        add("music_volume", "Music Volume", "Âm lượng nhạc nền");
        add("sfx_volume", "SFX Volume", "Âm lượng hiệu ứng");

        // Help
        add("help", "Help", "Hướng dẫn");
        add("rules_title", "BASIC RULES", "LUẬT CHƠI CƠ BẢN");
        add("rules_content", "1. Each player has a 10x10 grid and 5 ships.\n" +
                             "2. Ships are placed horizontally or vertically.\n" +
                             "3. Players take turns firing at the enemy grid.\n" +
                             "4. A hit allows another shot (optional rule applied).\n" +
                             "5. First to sink all 5 enemy ships wins!", 
                             "1. Mỗi người chơi có bàn cờ 10x10 và 5 tàu.\n" +
                             "2. Tàu được đặt theo chiều ngang hoặc dọc.\n" +
                             "3. Người chơi luân phiên bắn vào bàn cờ đối thủ.\n" +
                             "4. Bắn trúng sẽ được thêm lượt bắn.\n" +
                             "5. Ai hạ gục toàn bộ 5 tàu đối phương trước sẽ thắng!");
        add("placement_title", "HOW TO PLACE SHIPS", "CÁCH ĐẶT TÀU");
        add("placement_content", "- Drag ships from the dock to the grid.\n" +
                                "- Left-Click on a placed ship to rotate it.\n" +
                                "- Right-Click on a placed ship to remove it.\n" +
                                "- Drag a placed ship to change its position.",
                                "- Kéo tàu từ hạm đội thả vào bàn cờ.\n" +
                                "- Click chuột trái vào tàu để xoay.\n" +
                                "- Click chuột phải vào tàu để gỡ bỏ.\n" +
                                "- Kéo thả tàu đã đặt để thay đổi vị trí.");
    }

    private static void add(String key, String en, String vi) {
        Map<Language, String> map = new HashMap<>();
        map.put(Language.EN, en);
        map.put(Language.VI, vi);
        translations.put(key, map);
    }

    public static String get(String key) {
        Map<Language, String> map = translations.get(key);
        if (map != null) {
            return map.get(currentLanguage);
        }
        return key;
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setLanguage(Language language) {
        currentLanguage = language;
    }
}
