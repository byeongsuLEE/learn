import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    ramping: {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 100 },
        { duration: '2m', target: 200 },
        { duration: '30s', target: 0 },
      ],
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

const CATEGORIES = ['수학', '영어', '과학', '국어', '사회'];
const BASE = __ENV.BASE_URL || 'http://evil55.cloud/api/user-service';

export default function () {
  const cat = CATEGORIES[Math.floor(Math.random() * CATEGORIES.length)];
  const page = Math.floor(Math.random() * 50);
  const res = http.get(
    `${BASE}/deck/search?category=${encodeURIComponent(cat)}&sort=createdDate,desc&page=${page}&size=20`
  );
  check(res, { 'status 200': (r) => r.status === 200 });
}

export function handleSummary(data) {
  return {
    'docs/perf/results/summary.json': JSON.stringify(data, null, 2),
    stdout: JSON.stringify(data.metrics['http_req_duration'], null, 2),
  };
}
