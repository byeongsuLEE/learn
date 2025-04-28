import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { MainLayout } from "@/components/layout/main-layout"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { BookOpen, BarChart, Clock, Award } from "lucide-react"

export default function Dashboard() {
  return (
    <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">대시보드</h1>
          <p className="text-muted-foreground">학습 현황과 최근 활동을 확인하세요.</p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">총 낱말카드</CardTitle>
              <BookOpen className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">12</div>
              <p className="text-xs text-muted-foreground">총 낱말카드 세트 수</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">학습 완료</CardTitle>
              <Award className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">8</div>
              <p className="text-xs text-muted-foreground">완료한 낱말카드 세트 수</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">학습 시간</CardTitle>
              <Clock className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">5.2h</div>
              <p className="text-xs text-muted-foreground">이번 주 총 학습 시간</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">정확도</CardTitle>
              <BarChart className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">87%</div>
              <p className="text-xs text-muted-foreground">평균 정답률</p>
            </CardContent>
          </Card>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>최근 학습한 낱말카드</CardTitle>
              <CardDescription>최근에 학습한 낱말카드 세트입니다.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">한국어 기초 단어</p>
                    <p className="text-sm text-muted-foreground">2일 전 학습</p>
                  </div>
                  <Button size="sm" variant="outline" asChild>
                    <Link href="/cards/1/study">계속 학습</Link>
                  </Button>
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">프로그래밍 용어</p>
                    <p className="text-sm text-muted-foreground">5일 전 학습</p>
                  </div>
                  <Button size="sm" variant="outline" asChild>
                    <Link href="/cards/2/study">계속 학습</Link>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>추천 낱말카드</CardTitle>
              <CardDescription>당신에게 추천하는 낱말카드 세트입니다.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">영어 비즈니스 용어</p>
                    <p className="text-sm text-muted-foreground">32개 단어</p>
                  </div>
                  <Button size="sm" asChild>
                    <Link href="/cards/3/study">학습하기</Link>
                  </Button>
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">일본어 여행 표현</p>
                    <p className="text-sm text-muted-foreground">45개 단어</p>
                  </div>
                  <Button size="sm" asChild>
                    <Link href="/cards/4/study">학습하기</Link>
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </MainLayout>
  )
}
