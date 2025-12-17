import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// 1. LẤY THAM SỐ TỪ TERMINAL
// Nếu không nhập gì, mặc định chạy 50 user trong 2 phút
const TARGET_VUS = __ENV.VUS ? parseInt(__ENV.VUS) : 50;
const DURATION_TIME = __ENV.DURATION || '2m';

export const options = {
  stages: [
    { duration: '30s', target: TARGET_VUS },      // 30s để tăng tốc lên mức mong muốn
    { duration: DURATION_TIME, target: TARGET_VUS }, // Giữ ổn định ở mức đó (QUAN TRỌNG NHẤT)  
    { duration: '10s', target: 0 },               // Giảm tốc
  ],
  
  // Ngưỡng chấp nhận (SLA)
  thresholds: {
    http_req_duration: ['p(95)<5000'], // Mong muốn < 5s
    http_req_failed: ['rate<0.05'],    // Lỗi < 5%
  },
};

const BASE_URL = 'http://localhost:8080/api/v1';

export default function () {
  // Tạo user unique
  const uniqueUsername = `iso_user_${__VU}_${Math.random().toString(36).substring(7)}`;
  const password = 'password123';
  const email = `${uniqueUsername}@example.com`;

  // --- BƯỚC 1: ĐĂNG KÝ ---
  const registerRes = http.post(`${BASE_URL}/users/register`, JSON.stringify({
    username: uniqueUsername,
    password: password,
    email: email
  }), { headers: { 'Content-Type': 'application/json' } });

  // --- BƯỚC 2: ĐĂNG NHẬP ---
  const loginRes = http.post(`${BASE_URL}/auth/token`, JSON.stringify({
    username: uniqueUsername,
    password: password
  }), { headers: { 'Content-Type': 'application/json' } });

  const token = loginRes.json('result.token');
  
  if (!token) return; // Nếu lỗi login thì bỏ qua vòng này

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  group('Shopping Flow', function () {
    // --- BƯỚC 3: XEM HÀNG ---
    let shoesRes = http.get(`${BASE_URL}/shoes`);
    check(shoesRes, { 'Status 200': (r) => r.status === 200 });

    const shoes = shoesRes.json('result');
    
    if (shoes && shoes.length > 0) {
      const randomShoe = shoes[randomIntBetween(0, shoes.length - 1)];
      sleep(0.5); // Giả lập suy nghĩ

      if (randomShoe.variants && randomShoe.variants.length > 0) {
        const randomVariant = randomShoe.variants[randomIntBetween(0, randomShoe.variants.length - 1)];
        
        // --- BƯỚC 4: MUA HÀNG ---
        const payload = JSON.stringify({ variantId: randomVariant.id, quantity: 1 });
        let cartRes = http.post(`${BASE_URL}/cart/add`, payload, params);
        
        check(cartRes, { 'Cart Updated': (r) => r.status === 200 });
      }
    }
  });

  sleep(0.5); 
}