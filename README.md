# 🔐 Spring Security JWT Redis — Pengujian Kinerja & Studi Kasus Double Token

Proyek ini merupakan **lanjutan dari materi belajar JWT dan Redis** di channel [Programmer Zaman Now](https://www.youtube.com/watch?v=lVWWl0GA57g), dengan tambahan studi kasus dan pengujian sebagai berikut:

> ✅ **Studi Kasus Tambahan:**  
> Penerapan **Double Token** — Access Token bersifat **stateless**, dan Refresh Token disimpan di **Redis**.

---

## 🚀 Cara Menjalankan Proyek

### 1. Clone Project

```bash
git clone https://github.com/nama-user/spring-jwt-redis-performance.git
cd spring-jwt-redis-performance
```

### 2. Build & Test

```bash
mvn compile
mvn test
```

### 3. Setup MySQL dan Redis

-   Buat database sesuai dengan konfigurasi di `src/main/resources/application.properties`
-   Pastikan **MySQL** dan **Redis** sudah berjalan di mesin lokal kamu.

---

## 🔐 Dokumentasi Endpoint

Sebelum pengujian, **registrasi dan login terlebih dahulu** agar mendapatkan token.

### 📌 Register

-   `POST http://localhost:8080/api/users`

```json
{
    "name": "yosep",
    "username": "yosep",
    "password": "rahasia"
}
```

### 📌 Login

-   `POST http://localhost:8080/api/auth/login`

```json
{
    "username": "yosep",
    "password": "rahasia"
}
```

### 📌 Endpoint yang Diuji

-   `POST http://localhost:8080/api/hello`

Tambahkan salah satu header berikut:

-   Untuk commit **stateless JWT (terakhir)**:

```http
Authorization: Bearer <accessToken>
```

-   Untuk commit lainnya (menggunakan token dari DB atau Redis):

```http
X-API-TOKEN: <token>
```

---

## 📈 Cara Menjalankan Pengujian

### 1. Install K6 (https://k6.io/docs/getting-started/installation/)

```bash
# Mac (brew)
brew install k6

# Ubuntu/Debian
sudo apt install k6
```

### 2. Checkout Commit yang Diinginkan

Terdapat 4 pendekatan token yang diuji. Gunakan `git checkout` untuk berpindah ke commit yang sesuai:

1. **Token validasi via MySQL**
2. **Stateless JWT (tidak simpan di storage)**
3. **JWT divalidasi via Redis**
4. **Double Token: access stateless, refresh via Redis**

### 3. Ubah `script.js`

Edit file `script.js` untuk mengganti header token sesuai pendekatan:

```js
headers: {
  Authorization: 'Bearer <access_token>' // atau
  'X-API-TOKEN': '<your_token>'
}
```

### 4. Jalankan Pengujian

```bash
k6 run script.js
```

---

## 📊 Hasil Pengujian & Perbandingan

| **Pendekatan**        | 🧪 **MySQL** | ⚡ **Stateless JWT** | ⚙️ **JWT + Redis** | 🔐 **Double Token** |
| --------------------- | ------------ | -------------------- | ------------------ | ------------------- |
| **Jumlah Request**    | 381,319      | 1,179,886            | 927,576            | **1,743,024** ✅    |
| **Request/sec (RPS)** | ~5,447/s     | ~16,855/s            | ~13,251/s          | **~24,900/s** ✅    |
| **p95 Response Time** | 36.68 ms     | 15.08 ms             | 12.77 ms           | **4.89 ms** ✅      |
| **Avg Response Time** | 11.61 ms     | 3.69 ms              | 4.70 ms            | **2.46 ms** ✅      |
| **Max Response Time** | 216.98 ms    | 163.77 ms            | 75.88 ms           | **42.01 ms** ✅     |
| **Error Rate**        | 0%           | 0%                   | 0%                 | 0%                  |

---

## 🧠 Kesimpulan

-   **MySQL**: Aman, mudah revokasi token, tapi performa lambat dan tidak scalable.
-   **Stateless JWT**: Sangat cepat, cocok untuk sistem read-only public, tapi tidak bisa revoke token.
-   **JWT + Redis**: Performa baik dan mendukung revocation, cocok untuk sistem autentikasi modern.
-   **Double Token (access stateless, refresh Redis)**: 🔥 **Solusi terbaik**, menggabungkan kecepatan dan keamanan. Cocok untuk production-grade API.

---

## 📺 Referensi Pembelajaran

Tutorial awal JWT + Redis:  
🎥 [Programmer Zaman Now - Belajar JWT + Redis dengan Spring Boot](https://www.youtube.com/watch?v=lVWWl0GA57g)

---

## 🧑‍💻 Author

Proyek ini dikembangkan dan diuji oleh [Yosep Rivaldo Silaban] sebagai lanjutan dari proses belajar.
