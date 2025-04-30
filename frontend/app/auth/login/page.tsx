"use client"

import type React from "react"

import { useState } from "react"
import Link from "next/link"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"
import { Loader2 } from "lucide-react"

export default function LoginPage() {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [email, setEmail] = useState<string>("")
  const [password, setPassword] = useState<string>("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email || !password) {
      toast({
        title: "입력 오류",
        description: "이메일과 비밀번호를 모두 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    try {
      setIsLoading(true)

      // 여기에 실제 로그인 로직 구현
      // 예: const response = await signIn(email, password)

      // 모의 로그인 처리 (2초 지연)
      await new Promise((resolve) => setTimeout(resolve, 2000))

      toast({
        title: "로그인 성공",
        description: "환영합니다!",
      })

      router.push("/dashboard")
    } catch (error) {
      console.error("Login failed:", error)
      toast({
        title: "로그인 실패",
        description: "이메일 또는 비밀번호가 올바르지 않습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleGoogleLogin = async () => {
    try {
      setIsLoading(true)
      // 구글 로그인 로직 구현
      // 예: await signInWithGoogle()
      await new Promise((resolve) => setTimeout(resolve, 1500))
      toast({
        title: "구글 로그인",
        description: "구글 계정으로 로그인 중입니다...",
      })
      router.push("/dashboard")
    } catch (error) {
      console.error("Google login failed:", error)
      toast({
        title: "로그인 실패",
        description: "구글 로그인 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleNaverLogin = async () => {
    try {
      setIsLoading(true)
      // 네이버 로그인 로직 구현
      // 예: await signInWithNaver()
      await new Promise((resolve) => setTimeout(resolve, 1500))
      toast({
        title: "네이버 로그인",
        description: "네이버 계정으로 로그인 중입니다...",
      })
      router.push("/dashboard")
    } catch (error) {
      console.error("Naver login failed:", error)
      toast({
        title: "로그인 실패",
        description: "네이버 로그인 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl font-bold">로그인</CardTitle>
          <CardDescription>이메일과 비밀번호를 입력하여 로그인하세요</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                placeholder="name@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label htmlFor="password">비밀번호</Label>
                <Link href="/auth/forgot-password" className="text-sm text-primary hover:underline">
                  비밀번호 찾기
                </Link>
              </div>
              <Input
                id="password"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading}
                required
              />
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  로그인 중...
                </>
              ) : (
                "로그인"
              )}
            </Button>
          </form>

          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t" />
            </div>
            <div className="relative flex justify-center text-xs uppercase">
              <span className="bg-card px-2 text-muted-foreground">또는 소셜 계정으로 로그인</span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Button
              variant="outline"
              onClick={handleGoogleLogin}
              disabled={isLoading}
              className="flex items-center justify-center gap-2"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-red-500"
              >
                <circle cx="12" cy="12" r="10" />
                <path d="M8 12h8" />
                <path d="M12 8v8" />
              </svg>
              구글
            </Button>
            <Button
              variant="outline"
              onClick={handleNaverLogin}
              disabled={isLoading}
              className="flex items-center justify-center gap-2 bg-[#03C75A] text-white hover:bg-[#03C75A]/90"
            >
              <span className="font-bold">N</span>
              네이버
            </Button>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col">
          <p className="text-center text-sm text-muted-foreground">
            계정이 없으신가요?{" "}
            <Link href="/auth/register" className="text-primary hover:underline">
              회원가입
            </Link>
          </p>
        </CardFooter>
      </Card>
    </div>
  )
}
