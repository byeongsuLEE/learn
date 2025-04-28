"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { createDeck } from "@/services/api"
import { MainLayout } from "@/components/layout/main-layout"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Loader2, Plus } from "lucide-react"

export default function CreatePage() {
  const router = useRouter()
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      setSaving(true)
      setError(null)
      const deck = await createDeck({
        title,
        description,
        items: []
      })
      router.push(`/deck/${deck.id}/edit`)
    } catch (err) {
      console.error("Failed to create deck:", err)
      setError("낱말카드 생성에 실패했습니다.")
    } finally {
      setSaving(false)
    }
  }

  return (
    <MainLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">새 낱말카드</h1>
          <p className="text-muted-foreground">새로운 낱말카드 세트를 만드세요.</p>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative">
            <strong className="font-bold">오류!</strong>
            <span className="block sm:inline"> {error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <Card>
            <CardContent className="pt-6 space-y-4">
              <div className="space-y-2">
                <Label htmlFor="title">제목</Label>
                <Input
                  id="title"
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="description">설명</Label>
                <Input
                  id="description"
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  required
                />
              </div>
            </CardContent>
          </Card>

          <Button type="submit" disabled={saving} className="w-full">
            {saving ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <>
                <Plus className="mr-2 h-4 w-4" />
                낱말카드 만들기
              </>
            )}
          </Button>
        </form>
      </div>
    </MainLayout>
  )
} 