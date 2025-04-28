"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { fetchDecks, deleteDeck } from "@/services/api"
import { Loader2, RefreshCw, Plus, Trash2 } from "lucide-react"
import { MainLayout } from "@/components/layout/main-layout"
import type { FlashcardDeck } from "@/types"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { toast } from "@/components/ui/use-toast"

export default function HomePage() {
  const [decks, setDecks] = useState<FlashcardDeck[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [deletingDeck, setDeletingDeck] = useState<FlashcardDeck | null>(null)
  const [isDeleting, setIsDeleting] = useState(false)

  const loadDecks = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await fetchDecks()
      setDecks(data)
    } catch (err) {
      console.error("Failed to fetch decks:", err)
      setError("낱말카드를 불러오는데 실패했습니다. 다시 시도해주세요.")
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteDeck = async () => {
    if (!deletingDeck) return

    try {
      setIsDeleting(true)
      await deleteDeck(deletingDeck.id.toString())
      setDecks(decks.filter(deck => deck.id !== deletingDeck.id))
      toast({
        title: "삭제 완료",
        description: "낱말카드가 성공적으로 삭제되었습니다.",
      })
    } catch (err) {
      console.error("Failed to delete deck:", err)
      toast({
        title: "삭제 실패",
        description: "낱말카드 삭제에 실패했습니다. 다시 시도해주세요.",
        variant: "destructive",
      })
    } finally {
      setIsDeleting(false)
      setDeletingDeck(null)
    }
  }

  useEffect(() => {
    loadDecks()
  }, [])

  return (
    <MainLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">낱말카드</h1>
            <p className="text-muted-foreground">모든 낱말카드 세트를 관리하고 학습하세요.</p>
          </div>
          <Button asChild>
            <Link href="/deck/create">
              <Plus className="mr-2 h-4 w-4" />새 낱말카드
            </Link>
          </Button>
        </div>

        {loading ? (
          <div className="flex justify-center items-center min-h-[50vh]">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : error ? (
          <div className="mb-6">
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
              <strong className="font-bold">오류!</strong>
              <span className="block sm:inline"> {error}</span>
            </div>
            <Button onClick={loadDecks} className="flex items-center gap-2">
              <RefreshCw className="h-4 w-4" />
              다시 시도
            </Button>
          </div>
        ) : decks.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-muted-foreground mb-4">아직 낱말카드가 없습니다. 새 낱말카드를 만들어보세요!</p>
            <Button asChild>
              <Link href="/deck/create">
                <Plus className="mr-2 h-4 w-4" />새 낱말카드 만들기
              </Link>
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {decks.map((deck) => (
              <Card key={deck.id}>
                <CardHeader>
                  <div className="flex justify-between items-start">
                    <div>
                      <CardTitle>{deck.title}</CardTitle>
                      <CardDescription>{deck.description}</CardDescription>
                    </div>
                    <Button 
                      variant="ghost" 
                      size="icon"
                      className="h-8 w-8 text-muted-foreground hover:text-destructive"
                      onClick={() => setDeletingDeck(deck)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </CardHeader>
                <CardContent>
                  <p className="text-sm text-muted-foreground">{deck.cardCount}개의 낱말카드</p>
                </CardContent>
                <CardFooter className="flex justify-between">
                  <Button variant="outline" asChild>
                    <Link href={`/deck/${deck.id}/edit`}>수정</Link>
                  </Button>
                  <Button asChild>
                    <Link href={`/deck/${deck.id}/study`}>학습</Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}

            <Card className="border-dashed border-2 flex flex-col items-center justify-center p-6">
              <div className="text-4xl mb-4">+</div>
              <Button variant="outline" asChild>
                <Link href="/deck/create">새 낱말카드 만들기</Link>
              </Button>
            </Card>
          </div>
        )}
      </div>

      <AlertDialog open={!!deletingDeck} onOpenChange={(open) => !open && setDeletingDeck(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>낱말카드 삭제</AlertDialogTitle>
            <AlertDialogDescription>
              정말로 이 낱말카드를 삭제하시겠습니까?
              이 작업은 되돌릴 수 없으며, 모든 카드 데이터가 영구적으로 삭제됩니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isDeleting}>취소</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteDeck}
              disabled={isDeleting}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {isDeleting ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "삭제"
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </MainLayout>
  )
}
