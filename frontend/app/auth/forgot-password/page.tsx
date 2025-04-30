"use client"

import type React from "react"

import { useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "@/components/ui/use-toast"
import { Loader2, ArrowLeft } from "lucide-react"

export default function ForgotPasswordPage() {
  const [isLoading, setIsLoading] = useState<boolean>(false)
  const [email, setEmail] = useState<string>("")
  const [submitted, setSubmitted] = useState<boolean>(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email) {
      toast({
        title: "입력 오류",
        description: "이메일을 입력해주세요.",
        variant: "destructive",
      })
      return
    }

    try {
      setIsLoading(true)

      // 여기에 실제 비밀번호 재설정 이메일 발송 로직 구현
      // 예: await sendPasswordResetEmail(email)

      // 모의 처리 (2초 지연)
      await new Promise((resolve) => setTimeout(resolve, 2000))

      setSubmitted(true)
      toast({
        title: "이메일 발송 완료",
        description: "비밀번호 재설정 링크가 이메일로 발송되었습니다.",
      })
    } catch (error) {
      console.error("Password reset failed:", error)
      toast({
        title: "이메일 발송 실패",
        description: "비밀번호 재설정 이메일 발송 중 오류가 발생했습니다.",
        variant: "destructive",
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <div className="flex items-center">
            <Link href="/auth/login" className="mr-2">
              <Button variant="ghost" size="icon">
                <ArrowLeft className="h-4 w-4" />
              </Button>
            </Link>
            <CardTitle className="text-2xl font-bold">비밀번호 찾기</CardTitle>
          </div>
          <CardDescription>가입한 이메일을 입력하시면 비밀번호 재설정 링크를 보내드립니다.</CardDescription>
        </CardHeader>
        <CardContent>
          {!submitted ? (
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
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    처리 중...
                  </>
                ) : (
                  "비밀번호 재설정 링크 받기"
                )}
              </Button>
            </form>
          ) : (
            <div className="space-y-4">
              <div className="rounded-lg bg-green-50 p-4 text-green-800">
                <p className="text-center">
                  비밀번호 재설정 링크가 <strong>{email}</strong>로 발송되었습니다. 이메일을 확인해주세요.
                </p>
              </div>
              <Button asChild className="w-full">
                <Link href="/auth/login">로그인 페이지로 돌아가기</Link>
              </Button>
            </div>
          )}
        </CardContent>
        <CardFooter className="flex justify-center">
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
