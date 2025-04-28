"use client"

import { useEffect, useState } from "react"
import { useParams, useRouter } from "next/navigation"
import { fetchDeck } from "@/services/api"
import { FlashcardDeck } from "@/types"
import { MainLayout } from "@/components/layout/main-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Loader2 } from "lucide-react"
import Link from "next/link"

export default function StudyPage() {
  const params = useParams()
  const router = useRouter()
  const [deck, setDeck] = useState<FlashcardDeck | null>(null)
  const [currentCardIndex, setCurrentCardIndex] = useState(0)
  const [isFlipped, setIsFlipped] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const loadDeck = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await fetchDeck(params.id as string)
        setDeck(data)
      } catch (err) {
        console.error("Failed to fetch deck:", err)
        setError("낱말카드를 불러오는데 실패했습니다.")
      } finally {
        setLoading(false)
      }
    }

    loadDeck()
  }, [params.id])

  const handleNext = () => {
    if (deck && currentCardIndex < deck.items.length - 1) {
      setCurrentCardIndex(prev => prev + 1)
      setIsFlipped(false)
    }
  }

  const handlePrev = () => {
    if (currentCardIndex > 0) {
      setCurrentCardIndex(prev => prev - 1)
      setIsFlipped(false)
    }
  }

  const handleFlip = () => {
    setIsFlipped(!isFlipped)
  }

  if (loading) {
    return (
      <MainLayout>
        <div className="flex justify-center items-center min-h-[50vh]">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </MainLayout>
    )
  }

  if (error || !deck) {
    return (
      <MainLayout>
        <div className="text-center py-12">
          <p className="text-red-500">{error || "낱말카드를 찾을 수 없습니다."}</p>
        </div>
      </MainLayout>
    )
  }

  if (deck.items.length === 0) {
    return (
      <MainLayout>
        <div className="space-y-6">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">{deck.title}</h1>
            <p className="text-muted-foreground">{deck.description}</p>
          </div>
          <div className="text-center py-12">
            <p className="text-muted-foreground mb-4">아직 카드가 없습니다. 카드를 추가해주세요!</p>
            <Button asChild>
              <Link href={`/deck/${deck.id}/edit`}>카드 추가하기</Link>
            </Button>
          </div>
        </div>
      </MainLayout>
    )
  }

  const currentCard = deck.items[currentCardIndex]

  return (
    <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">{deck.title}</h1>
          <p className="text-muted-foreground">{deck.description}</p>
        </div>

        <div className="flex flex-col items-center space-y-4">
          <div className="w-full max-w-2xl">
            <Card className="h-64 cursor-pointer" onClick={handleFlip}>
              <CardContent className="flex items-center justify-center h-full text-center p-6">
                <p className="text-2xl">
                  {isFlipped ? currentCard.answer : currentCard.question}
                </p>
              </CardContent>
            </Card>
          </div>

          <div className="flex space-x-4">
            <Button onClick={handlePrev} disabled={currentCardIndex === 0}>
              이전
            </Button>
            <Button onClick={handleFlip}>뒤집기</Button>
            <Button
              onClick={handleNext}
              disabled={currentCardIndex === deck.items.length - 1}
            >
              다음
            </Button>
          </div>

          <p className="text-sm text-muted-foreground">
            {currentCardIndex + 1} / {deck.items.length}
          </p>
        </div>
      </div>
    </MainLayout>
  )
} 