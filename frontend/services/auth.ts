export interface User {
  id: string
  username: string
  email: string
}

// 로그인 함수
export async function signIn(email: string, password: string): Promise<User> {
  // 실제 구현에서는 API 호출을 통해 로그인 처리
  // 예시 코드이므로 모의 응답 반환
  return {
    id: "1",
    username: "사용자",
    email: email,
  }
}

// 회원가입 함수
export async function register(email: string, password: string, username: string): Promise<User> {
  // 실제 구현에서는 API 호출을 통해 회원가입 처리
  // 예시 코드이므로 모의 응답 반환
  return {
    id: "1",
    username: username,
    email: email,
  }
}

// 소셜 로그인 함수 - 구글
export async function signInWithGoogle(): Promise<User> {
  // 실제 구현에서는 OAuth 프로세스를 통해 구글 로그인 처리
  // 예시 코드이므로 모의 응답 반환
  return {
    id: "g-123456",
    username: "구글사용자",
    email: "google@example.com",
  }
}

// 소셜 로그인 함수 - 네이버
export async function signInWithNaver(): Promise<User> {
  // 실제 구현에서는 OAuth 프로세스를 통해 네이버 로그인 처리
  // 예시 코드이므로 모의 응답 반환
  return {
    id: "n-123456",
    username: "네이버사용자",
    email: "naver@example.com",
  }
}

// 로그아웃 함수
export async function signOut(): Promise<void> {
  // 실제 구현에서는 API 호출을 통해 로그아웃 처리
  // 예시 코드이므로 빈 Promise 반환
  return Promise.resolve()
}

// 비밀번호 재설정 이메일 발송 함수
export async function sendPasswordResetEmail(email: string): Promise<void> {
  // 실제 구현에서는 API 호출을 통해 비밀번호 재설정 이메일 발송
  // 예시 코드이므로 빈 Promise 반환
  return Promise.resolve()
}
