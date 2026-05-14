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
  sleep(1);
}

export function handleSummary(data) {
  // 측정된 TPS (초당 요청 수) 가져오기
  const tps = data.metrics.http_reqs.values.rate; 
  
  return {
    'docs/perf/results/summary.json': JSON.stringify(data, null, 2),
    // 응답 시간과 함께 TPS 수치도 콘솔(stdout)에 같이 출력하도록 수정
    stdout: `[응답 시간]\n${JSON.stringify(data.metrics['http_req_duration'], null, 2)}\n\n[측정된 TPS]\n${tps.toFixed(2)} 요청/초\n`,
  };
}
