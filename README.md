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
└───com
│   │   │   │                   └───example
│   │   │   │                       └───moodify
│   │   │   │                           ├───auth
│   │   │   │                           │       LastPlayedManager.dex
│   │   │   │                           │       SpotifyAuthUtil$1.dex
│   │   │   │                           │       SpotifyAuthUtil$TokenCallback.dex
│   │   │   │                           │       SpotifyAuthUtil.dex
│   │   │   │                           │       SpotifyTokenExchange$1.dex
│   │   │   │                           │       SpotifyTokenExchange$TokenCallback.dex
│   │   │   │                           │       SpotifyTokenExchange.dex
│   │   │   │                           │       SpotifyTokenRefresher$1.dex
│   │   │   │                           │       SpotifyTokenRefresher$RefreshCallback.dex
│   │   │   │                           │       SpotifyTokenRefresher.dex
│   │   │   │                           │       TokenManager.dex
│   │   │   │                           │
│   │   │   │                           ├───home
│   │   │   │                           │       CollectionFragment$1.dex
│   │   │   │                           │       CollectionFragment$2.dex
│   │   │   │                           │       CollectionFragment$3.dex
│   │   │   │                           │       CollectionFragment$4.dex
│   │   │   │                           │       CollectionFragment.dex
│   │   │   │                           │       GenreFragment$1.dex
│   │   │   │                           │       GenreFragment$2.dex
│   │   │   │                           │       GenreFragment.dex
│   │   │   │                           │       HomeFragment$1.dex
│   │   │   │                           │       HomeFragment$2.dex
│   │   │   │                           │       HomeFragment$3.dex
│   │   │   │                           │       HomeFragment$4.dex
│   │   │   │                           │       HomeFragment.dex
│   │   │   │                           │       LastPlayedActivity.dex
│   │   │   │                           │       LoginFragment.dex
│   │   │   │                           │       MainActivity$1.dex
│   │   │   │                           │       MainActivity$2.dex
│   │   │   │                           │       MainActivity$3.dex
│   │   │   │                           │       MainActivity.dex
│   │   │   │                           │       MusicPlayerActivity$1.dex
│   │   │   │                           │       MusicPlayerActivity.dex
│   │   │   │                           │       PlaylistFragment$1.dex
│   │   │   │                           │       PlaylistFragment$2.dex
│   │   │   │                           │       PlaylistFragment$3.dex
│   │   │   │                           │       PlaylistFragment$4$1.dex
│   │   │   │                           │       PlaylistFragment$4.dex
│   │   │   │                           │       PlaylistFragment.dex
│   │   │   │                           │       SearchFragment$1.dex
│   │   │   │                           │       SearchFragment$2.dex
│   │   │   │                           │       SearchFragment$3.dex
│   │   │   │                           │       SearchFragment$4.dex
│   │   │   │                           │       SearchFragment.dex
│   │   │   │                           │
│   │   │   │                           ├───response
│   │   │   │                           │       GenreItem.dex
│   │   │   │                           │       PlaylistItem.dex
│   │   │   │                           │       SongItem.dex
│   │   │   │                           │
│   │   │   │                           ├───ui
│   │   │   │                           │       GenreAdapter$GenreViewHolder.dex
│   │   │   │                           │       GenreAdapter$OnGenreClickListener.dex
│   │   │   │                           │       GenreAdapter.dex
│   │   │   │                           │       PlaylistAdapter$OnItemClickListener.dex
│   │   │   │                           │       PlaylistAdapter$PlaylistViewHolder.dex
│   │   │   │                           │       PlaylistAdapter.dex
│   │   │   │                           │       SongAdapter$SongViewHolder.dex
│   │   │   │                           │       SongAdapter.dex
│   │   │   │                           │
│   │   │   │                           └───util
│   │   │   │                                   DummyFeaturedPlaylists.dex
│   │   │   │                                   DummyGenrePlaylists.dex
│   │   │   │                                   DummyGenres.dex
│   │   │   │                                   DummyTopCharts.dex


---

## 🧑‍💻 Kontributor

- H071231046 – Chelsea Shelin Purnaria

---

## 📄 Lisensi

Project ini dibuat untuk keperluan tugas praktikum dan tidak dimaksudkan untuk distribusi komersial.
