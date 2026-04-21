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

        // Settings
        add("lang_label", "Language / Ngôn ngữ:", "Ngôn ngữ:");
        add("audio_label", "Audio / Âm thanh:", "Âm thanh:");
        add("enable_sound", "Enable Sound Effects", "Bật hiệu ứng âm thanh");
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
