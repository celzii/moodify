# Moodify 🎵

Moodify adalah aplikasi pemutar musik yang dirancang dengan antarmuka mirip Spotify. Aplikasi ini memungkinkan pengguna menjelajahi musik berdasarkan suasana hati dan menyimpan lagu-lagu favorit mereka ke dalam koleksi pribadi.

---

## 🚀 Fitur Utama

- Menampilkan daftar lagu berdasarkan mood.
- Fitur **Search Lagu** seperti Spotify.
- Halaman **Collection** mempelrihatkan lagu favorit.
- Tampilan **Music Player** yang sederhana.
- Halaman **Playlist** dengan cover dan daftar lagu.

---

## 📱 Cara Penggunaan

1. Buka aplikasi Moodify.
2. Gunakan menu bawah untuk navigasi: Home, Search, Collection.
3. Klik lagu untuk memutar.

---

## 🛠️ Implementasi Teknis

- **Platform**: Android Studio
- **Bahasa**: Java
- **Arsitektur**: Fragment-based Navigation
- **Fitur UI**:
  - Custom Fragment seperti Search (mirip Spotify)
  - RecyclerView untuk daftar lagu dan playlist
  - MediaPlayer untuk pemutaran lagu
  - Fragment untuk Music Player dengan cover art, judul, dan artis

---

## 📂 Struktur Project
app/src/main/java/com/example/moodify/
├── auth/
│   ├── LastPlayedManager.dex
│   ├── SpotifyAuthUtil.dex
│   ├── SpotifyTokenExchange.dex
│   ├── SpotifyTokenRefresher.dex
│   └── TokenManager.dex
│   (plus beberapa inner class seperti $1, $TokenCallback, dll.)
│
├── home/
│   ├── CollectionFragment.dex
│   ├── GenreFragment.dex
│   ├── HomeFragment.dex
│   ├── LoginFragment.dex
│   ├── LastPlayedActivity.dex
│   ├── MainActivity.dex
│   ├── MusicPlayerActivity.dex
│   ├── PlaylistFragment.dex
│   └── SearchFragment.dex
│   (plus beberapa inner class $1, $2, dll.)
│
├── response/
│   ├── GenreItem.dex
│   ├── PlaylistItem.dex
│   └── SongItem.dex
│
├── ui/
│   ├── GenreAdapter.dex
│   ├── PlaylistAdapter.dex
│   └── SongAdapter.dex
│   (plus inner class seperti ViewHolder dan Listener)
│
└── util/
    ├── DummyFeaturedPlaylists.dex
    ├── DummyGenrePlaylists.dex
    ├── DummyGenres.dex
    └── DummyTopCharts.dex



---

## 🧑‍💻 Kontributor

- H071231046 – Chelsea Shelin Purnaria

---

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas praktikum dan tidak dimaksudkan untuk distribusi komersial.
