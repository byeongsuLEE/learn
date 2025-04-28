export interface FlashcardDeck {
  id: string
  title: string
  description: string
  category: string
  tag: string
  cardCount : number
  items: FlashcardItem[]
}

export interface FlashcardItem {
  id: string
  question: string
  answer: string
} 