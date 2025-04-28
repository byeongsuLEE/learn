"use client"

import { useEffect, useState } from "react"
import { useParams, useRouter } from "next/navigation"
import { fetchDeck, updateDeck, createCard, deleteCard } from "@/services/api"
import { FlashcardDeck, FlashcardItem } from "@/types"
import { MainLayout } from "@/components/layout/main-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2, Plus, Trash2 } from "lucide-react"

export default function EditPage() {
  const params = useParams()
  const router = useRouter()
  const [deck, setDeck] = useState<FlashcardDeck | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)

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

  const handleUpdateDeck = async () => {
    if (!deck) return

    try {
      setSaving(true)
      await updateDeck(deck)
      router.refresh()
    } catch (err) {
      console.error("Failed to update deck:", err)
      setError("낱말카드 수정에 실패했습니다.")
    } finally {
      setSaving(false)
    }
  }

  const handleAddCard = async () => {
    if (!deck) return

    try {
      setSaving(true)
      const newCard = await createCard(deck.id, {
        question: "",
        answer: ""
      })
      setDeck({
        ...deck,
        items: [...deck.items, newCard]
      })
    } catch (err) {
      console.error("Failed to add card:", err)
      setError("카드 추가에 실패했습니다.")
    } finally {
      setSaving(false)
    }
  }

  const handleDeleteCard = async (cardId: string) => {
    if (!deck) return

    try {
      setSaving(true)
      await deleteCard(deck.id, cardId)
      setDeck({
        ...deck,
        items: deck.items.filter(item => item.id !== cardId)
      })
    } catch (err) {
      console.error("Failed to delete card:", err)
      setError("카드 삭제에 실패했습니다.")
    } finally {
      setSaving(false)
    }
  }

  const handleUpdateCard = (index: number, field: keyof FlashcardItem, value: string) => {
    if (!deck) return

    const updatedItems = [...deck.items]
    updatedItems[index] = {
      ...updatedItems[index],
      [field]: value
    }

    setDeck({
      ...deck,
      items: updatedItems
    })
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

  return (
    <MainLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">낱말카드 수정</h1>
            <p className="text-muted-foreground">낱말카드의 내용을 수정하세요.</p>
          </div>
          <Button onClick={handleUpdateDeck} disabled={saving}>
            {saving ? <Loader2 className="h-4 w-4 animate-spin" /> : "저장"}
          </Button>
        </div>

        <div className="space-y-4">
          <div className="grid gap-4">
            <div className="space-y-2">
              <Label htmlFor="title">제목</Label>
              <Input
                id="title"
                value={deck.title}
                onChange={e => setDeck({ ...deck, title: e.target.value })}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="description">설명</Label>
              <Input
                id="description"
                value={deck.description}
                onChange={e => setDeck({ ...deck, description: e.target.value })}
              />
            </div>
          </div>

          <div className="space-y-4">
            {deck.items.map((card, index) => (
              <Card key={card.id}>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <h3 className="text-sm font-medium">카드 {index + 1}</h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => handleDeleteCard(card.id)}
                    disabled={saving}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor={`question-${index}`}>질문</Label>
                    <Input
                      id={`question-${index}`}
                      value={card.question}
                      onChange={e => handleUpdateCard(index, "question", e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor={`answer-${index}`}>답변</Label>
                    <Input
                      id={`answer-${index}`}
                      value={card.answer}
                      onChange={e => handleUpdateCard(index, "answer", e.target.value)}
                    />
                  </div>
                </CardContent>
              </Card>
            ))}

            <Button
              variant="outline"
              className="w-full"
              onClick={handleAddCard}
              disabled={saving}
            >
              <Plus className="mr-2 h-4 w-4" />
              새 카드 추가
            </Button>
          </div>
        </div>
      </div>
    </MainLayout>
  )
} 