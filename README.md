# 🔒 LockIn - Anti Doomscroll App

LockIn adalah aplikasi produktivitas berbasis Android yang dirancang untuk membantu pengguna mengatasi kecanduan *doomscrolling* dan mengembalikan fokus mereka. Dengan sistem proteksi yang tangguh dan desain antarmuka *Dark Space* yang modern, LockIn memastikan kamu tidak terperangkap dalam jebakan scrolling tanpa batas di media sosial.

## ✨ Fitur Utama

*   **⚡ Proteksi Anti-Bypass (Zero-Delay Block):** Menggunakan `AccessibilityService` yang sangat ringan, aplikasi yang diblokir akan langsung tertutup di layar tanpa jeda sedetikpun.
*   **🛡️ Hard-Lock & Crash Recovery:** Proteksi ini menyimpan *state* secara langsung ke memori (RAM) dan juga di-backup ke dalam database internal (Room DB). Walaupun aplikasi LockIn ditutup paksa (force close) dari recent apps atau HP di-restart, proteksi tetap akan berjalan.
*   **🎨 Deep Space Glassmorphism UI:** Antarmuka dengan tema *Dark Space* (Navy/Indigo) yang dikombinasikan dengan efek *glassmorphism* untuk kesan premium dan futuristik.
*   **🎮 Focus Tap Challenge (Mini Game):** Daripada hanya memblokir, LockIn menantang pengguna untuk bermain mini-game (uji ketangkasan & fokus) jika mereka benar-benar butuh akses darurat selama 5 menit.
*   **📊 Lockout Analytics (Stats):** Pantau sudah berapa lama kamu berhasil menjaga fokus dan dapatkan *Achievements* menarik seperti *Doomscroll Slayer*.

## 🛠️ Tech Stack

Aplikasi ini dibangun menggunakan teknologi native Android terbaru:
*   **Bahasa:** Kotlin (100%)
*   **UI Toolkit:** Jetpack Compose
*   **Architecture:** Clean Architecture (MVVM)
*   **Dependency Injection:** Dagger Hilt
*   **Local Storage:** DataStore (Preferences) & Room Database
*   **Core Logic:** AccessibilityService (untuk melacak Window State dan App Package secara real-time)

## 🚀 Cara Instalasi (Development)

1.  Clone repositori ini:
    ```bash
    git clone https://github.com/yourusername/lockin.git
    ```
2.  Buka proyek menggunakan **Android Studio** (Koala atau versi lebih baru direkomendasikan).
3.  Pastikan device/emulator kamu menjalankan minimal **Android 12 (API 31)**.
4.  Jalankan aplikasi (Shift + F10) atau jalankan command ini:
    ```bash
    ./gradlew installDebug
    ```

## ⚙️ Cara Setup Proteksi di HP Kamu

Agar LockIn bisa memblokir aplikasi secara efektif, lakukan langkah ini di awal instalasi:
1.  Buka **Settings** HP kamu.
2.  Masuk ke menu **Accessibility (Aksesibilitas)** > **Installed Services**.
3.  Cari **LockIn** lalu aktifkan *toggle* menjadi **ON** (Berikan izin).
4.  Buka aplikasi LockIn > Tap **Set Up Protection** > Pilih aplikasi yang ingin dibatasi (misal: TikTok, Instagram).
5.  Set batas waktu pemakaian, lalu klik **Activate Anti-Doomscroll**.

---

*LockIn: Because your focus is your most valuable asset.*

