"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import { Loader2 } from "lucide-react"

export default function LogoutPage() {
  const router = useRouter()

  useEffect(() => {
    const logout = async () => {
      try {
        // 여기에 실제 로그아웃 로직 구현
        // 예: await signOut()

        // 모의 로그아웃 처리 (1초 지연)
        await new Promise((resolve) => setTimeout(resolve, 1000))

        // 로그인 페이지로 리다이렉트
        router.push("/auth/login")
      } catch (error) {
        console.error("Logout failed:", error)
        // 오류가 발생해도 로그인 페이지로 리다이렉트
        router.push("/auth/login")
      }
    }

    logout()
  }, [router])

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <Loader2 className="mx-auto h-8 w-8 animate-spin text-primary" />
        <p className="mt-4 text-lg">로그아웃 중입니다...</p>
      </div>
    </div>
  )
}
