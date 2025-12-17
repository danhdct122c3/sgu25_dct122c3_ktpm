import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// --- CONFIGURATION VIA ENVIRONMENT VARIABLES ---
// Default to 150 Users if not specified
const TARGET_VUS = __ENV.VUS ? parseInt(__ENV.VUS) : 150;
// Default to 3 minutes if not specified
const DURATION_TIME = __ENV.DURATION || '3m';

export const options = {
  // LOAD TESTING CONFIGURATION
  // Goal: Prove system stability under significant load
  stages: [
    // Stage 1: Ramp-up (Warm up)
    // Dynamic target based on input
    { duration: '1m', target: TARGET_VUS }, 
    
    // Stage 2: Steady State (Crucial Phase)
    // Maintain load for the specified duration
    { duration: DURATION_TIME, target: TARGET_VUS }, 
    
    // Stage 3: Ramp-down (Cool down)
    { duration: '30s', target: 0 }, 
  ],

  // Service Level Agreement (SLA) / Success Criteria
  thresholds: {
    // 95% of requests must complete within 1 second (1000ms)
    http_req_duration: ['p(95)<1000'], 
    // Error rate must be less than 1%
    http_req_failed: ['rate<0.01'], 
  },
};

const BASE_URL = 'http://localhost:8080/api/v1';

export default function () {
  // Generate unique user data for each iteration
  const uniqueUsername = `load_flex_u${__VU}_i${__ITER}_${Math.random().toString(36).substring(7)}`;
  const password = 'password123';
  const email = `${uniqueUsername}@example.com`;

  // --- STEP 1: REGISTER (Write Operation) ---
  const registerRes = http.post(`${BASE_URL}/users/register`, JSON.stringify({
    username: uniqueUsername,
    password: password,
    email: email
  }), { headers: { 'Content-Type': 'application/json' } });

  // --- STEP 2: LOGIN (High CPU Task) ---
  const loginRes = http.post(`${BASE_URL}/auth/token`, JSON.stringify({
    username: uniqueUsername,
    password: password
  }), { headers: { 'Content-Type': 'application/json' } });

  const token = loginRes.json('result.token');
  
  if (!token) {
    return;
  }

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  group('User Scenario: Shopping Journey', function () {
    
    // --- STEP 3: BROWSE PRODUCTS ---
    let shoesRes = http.get(`${BASE_URL}/shoes`);
    check(shoesRes, {
      'Get shoes status is 200': (r) => r.status === 200,
    });

    const shoes = shoesRes.json('result');
    
    if (shoes && shoes.length > 0) {
      const randomShoe = shoes[randomIntBetween(0, shoes.length - 1)];
      
      // Simulate "Think Time" (0.5s to 1s)
      sleep(randomIntBetween(0.5, 1)); 

      if (randomShoe.variants && randomShoe.variants.length > 0) {
        const randomVariant = randomShoe.variants[randomIntBetween(0, randomShoe.variants.length - 1)];
        
        // --- STEP 4: ADD TO CART ---
        const payload = JSON.stringify({
          variantId: randomVariant.id,
          quantity: 1
        });

        let cartRes = http.post(`${BASE_URL}/cart/add`, payload, params);
        
        check(cartRes, {
          'Add to cart status is 200': (r) => r.status === 200,
          'Cart update confirmed': (r) => r.json('result.items') !== undefined,
        });
      }
    }
  });

  sleep(1); 
}