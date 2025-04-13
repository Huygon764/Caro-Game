# Caro Game - Dự Án Trò Chơi Cờ Caro Đa Nền Tảng
Một trò chơi cờ Caro (Gomoku) đa nền tảng với giao diện hiện đại, hỗ trợ chơi với AI hoặc chơi 2 người. Ứng dụng được phát triển cho cả nền tảng Desktop (Java Swing) và Android (Jetpack Compose).

# Các Tính Năng Chính:
- ✅ Hai chế độ chơi: Player vs CPU và Player vs Player
- ✅ Hẹn giờ cho mỗi lượt đánh
- ✅ Tính năng hoàn tác (Undo) không giới hạn
- ✅ Thuật toán AI thông minh dựa trên Minimax và Alpha-Beta Pruning
- ✅ Hiệu ứng hover để xem trước nước đi

# Cấu Trúc Dự Án
caro-game-project/ \
├── caro-core/                  # Module chia sẻ, chứa logic game \
│                   ├── ai/                  # Thuật toán AI \
│                   ├── event/               # Hệ thống sự kiện \
│                   ├── logic/               # Logic trò chơi \
│                   ├── model/               # Các lớp dữ liệu \
│                   │   ├── Board.java       # Biểu diễn bàn cờ \
│                   │   ├── GameSettings.java # Cài đặt game \
│                   │   ├── GameState.java   # Trạng thái game \
│                   │   └── Player.java      # Thông tin người chơi \
│                   └── utils/               # Tiện ích \
│                       └── GameConstants.java # Các hằng số \
│                       
├── caro-desktop/              # Module ứng dụng desktop (Java Swing) \
│                   ├── controller/ \
│                   │   └── DesktopGameController.java \
│                   ├── ui/ \
│                   │   └── CaroFrame.java \
│                   └── CaroDesktopApp.java # Entry point cho ứng dụng desktop \
│                   
└── caro-android/             # Module ứng dụng Android (Jetpack Compose) \
            │       ├── controller/\
            │       │   └── AndroidGameController.kt\
            │       ├── ui/\
            │       │   ├── component/\
            │       │   │   └── GameScreen.kt\
            │       │   └── theme/\
            │       └── MainActivity.kt # entry point cho app

# Cách Hoạt Động Của AI
AI trong game được xây dựng dựa trên thuật toán Minimax với tối ưu hóa Alpha-Beta Pruning: \

## Thuật Toán Minimax 
Nguyên lý cơ bản: Minimax là thuật toán tìm kiếm đệ quy giúp AI xác định nước đi tối ưu bằng cách mô phỏng tất cả khả năng trong một số lượt đi nhất định. 

## Maximizing và Minimizing Players:

- AI (maximizing player) tìm cách tối đa hóa điểm số
- Đối thủ (minimizing player) tìm cách tối thiểu hóa điểm số
- Độ sâu tìm kiếm: AI xem xét trước 3-4 nước đi để đưa ra quyết định.

## Alpha-Beta Pruning
Kỹ thuật tối ưu hóa giúp loại bỏ các nhánh tìm kiếm không cần thiết, giảm thời gian tính toán mà không ảnh hưởng đến kết quả.

## Thứ tự ưu tiên
- Tìm nước thắng ngay lập tức
- Chặn nước thắng của đối thủ
- Tạo thế "bốn mở hai đầu"
- Chặn thế "bốn mở hai đầu" của đối thủ
- Tạo nhiều thế "ba mở hai đầu"
- Sử dụng Minimax cho các trường hợp còn lại

# Cài Đặt Và Chạy Ứng Dụng
## Yêu Cầu Hệ Thống
- Java Development Kit (JDK) 11 trở lên
- Android Studio Flamingo (2022.2.1) trở lên
- Gradle 7.4+
- Android SDK 24+ (cho phiên bản Android)
## Biên Dịch Và Chạy
### Desktop Version
`./gradlew :caro-desktop:run`
### Android Version
- Mở dự án trong Android Studio
- Chọn device hoặc emulator
- Nhấn Run

# Các Chức Năng Có Thể Phát Triển Thêm
- Nhiều cấp độ AI: Thêm các mức độ dễ, trung bình, khó
- Lưu và tải game: Khả năng lưu trạng thái game và tiếp tục sau
- Phát triển bản online: Cho phép người chơi đấu qua mạng
- Bảng xếp hạng: Lưu thống kê và hiển thị bảng xếp hạng người chơi
- Hiệu ứng âm thanh: Thêm âm thanh khi đánh cờ và thắng/thua
- Tuỳ chỉnh giao diện: Cho phép người dùng chọn chủ đề và màu sắc
- Hỗ trợ nhiều kích thước bàn cờ: Cho phép thay đổi kích thước bàn cờ (15x15, 19x19, vv.)
- Chế độ thách đấu: AI tạo các thử thách đặc biệt cho người chơi
- Tích hợp Machine Learning: Cải thiện AI bằng học máy từ các ván đấu trước
- Chế độ xem lại: Cho phép xem lại các nước đi trong một ván cờ đã chơi
