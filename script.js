import http from 'k6/http';
import { check, sleep } from 'k6';

// Mendefinisikan opsi untuk skenario pengetesan beban
export const options = {
    stages: [
        { duration: '10s', target: 50 },  // Dalam 10 detik, tingkatkan Virtual User (VU) menjadi 50
        { duration: '50s', target: 100 }, // Dalam 50 detik, tingkatkan VU menjadi 100
        { duration: '10s', target: 0 },   // Dalam 10 detik, turunkan VU menjadi 0 (ramp down)
    ],
    // Menentukan ambang batas (thresholds) untuk metrik pengetesan
    // Contoh: Pastikan 95% permintaan selesai dalam waktu kurang dari 500ms
    thresholds: {
        'http_req_duration': ['p(95)<500'], // 95% permintaan harus kurang dari 500ms
        'http_req_failed': ['rate<0.01'],    // Tingkat kegagalan permintaan HTTP harus kurang dari 1%
    },
};

// Fungsi utama yang akan dijalankan oleh setiap Virtual User
export default function () {
    // Mendefinisikan URL API yang akan diuji
    const url = 'http://localhost:8080/api/hello';

    // Mendefinisikan header untuk permintaan
    const params = {
        headers: {
            'X-API-TOKEN': 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Inlvc2VwIiwibmFtZSI6Inlvc2VwIiwiZXhwIjoxNzU2Mjg0NzUzfQ.wvobjWopEwDIV1iF9NA13h-64rWHfsuooZlIVySsWzk',
        },
    };

    // Melakukan permintaan GET ke API
    const res = http.get(url, params);

    // Melakukan pengecekan (check) pada respons
    // Memastikan status respons adalah 200 OK
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}