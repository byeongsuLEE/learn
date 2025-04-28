import { FlashcardDeck, FlashcardItem } from "@/types"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api/user-service'

// 백엔드 응답 형식에 맞는 타입 정의
interface ApiResponse<T> {
  status: number
  message: string
  data: T
  success: boolean
}

// 타입 정의
export interface User {
  id: number
  username: string
  email: string
}

export interface CreateDeckRequest {
  title: string
  description: string
  items: Omit<FlashcardItem, "id">[]
}

export interface UpdateDeckRequest {
  id: number
  title: string
  description: string
  items: FlashcardItem[]
}

// 모의 데이터 (API 서버가 없을 때 사용)
const MOCK_DATA = {
  decks: [
    {
      id: 1,
      title: "한국어 기초 단어",
      description: "기초 한국어 단어와 표현",
      createdAt: "2023-01-01T00:00:00Z",
      updatedAt: "2023-01-01T00:00:00Z",
      items: [
        { id: 1, term: "안녕하세요", definition: "Hello", position: 0 },
        { id: 2, term: "감사합니다", definition: "Thank you", position: 1 },
        { id: 3, term: "미안합니다", definition: "I'm sorry", position: 2 },
        { id: 4, term: "이름", definition: "Name", position: 3 },
        { id: 5, term: "학생", definition: "Student", position: 4 },
      ],
    },
    {
      id: 2,
      title: "프로그래밍 용어",
      description: "자주 사용되는 프로그래밍 용어",
      createdAt: "2023-01-02T00:00:00Z",
      updatedAt: "2023-01-02T00:00:00Z",
      items: [
        { id: 6, term: "변수", definition: "Variable", position: 0 },
        { id: 7, term: "함수", definition: "Function", position: 1 },
        { id: 8, term: "클래스", definition: "Class", position: 2 },
        { id: 9, term: "객체", definition: "Object", position: 3 },
      ],
    },
  ],
}

// 개발 환경인지 확인
const isDevelopment = process.env.NODE_ENV === "development" || !process.env.NODE_ENV

// API 호출 래퍼 함수 - 오류 처리 및 모의 데이터 지원
async function apiCall<T>(url: string, options?: RequestInit, mockResponse?: T): Promise<T> {
  // Check if we're in a preview environment or if the API URL is localhost
  const isPreviewOrProduction = typeof window !== "undefined" && !window.location.hostname.includes("localhost")
  const isLocalhost = API_BASE_URL.includes("localhost")

  // If we're in preview/production and trying to access localhost, use mock data immediately
  if (isPreviewOrProduction && isLocalhost && mockResponse !== undefined) {
    console.log("Preview environment detected with localhost API. Using mock data directly.")
    return mockResponse
  }

  try {
    // Actual API call attempt
    const response = await fetch(url, {
      ...options,
      // Reduce timeout to 3 seconds to fail faster
      signal: AbortSignal.timeout(3000),
    })

    if (!response.ok) {
      throw new Error(`API error: ${response.status} ${response.statusText}`)
    }

    const apiResponse: ApiResponse<T> = await response.json()

    if (!apiResponse.success) {
      throw new Error(`API error: ${apiResponse.message}`)
    }

    return apiResponse.data
  } catch (error) {
    // Improved error logging
    if (error instanceof TypeError && error.message.includes("fetch")) {
      console.warn(`Network error connecting to API: ${url}. Using mock data instead.`)
    } else {
      console.error(`API call failed: ${url}`, error)
    }

    // Return mock data if provided
    if (mockResponse !== undefined) {
      return mockResponse
    }

    throw error
  }
}

// API 함수들
export const fetchDecks = async (): Promise<FlashcardDeck[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck?page=0&size=10&sort=id,desc`)
    if (!response.ok) {
      throw new Error('덱 목록을 가져오는데 실패했습니다.')
    }
    const data = await response.json()
    return data.data.content.map((deck: any) => ({
      id: deck.id,
      title: deck.title,
      description: deck.description,
      cardCount: deck.cardCount,
      items: deck.cards?.map((card: any) => ({
        id: card.id,
        question: card.question,
        answer: card.answer
      })) || []
    }))
  } catch (error) {
    console.error('Error fetching decks:', error)
    throw error
  }
}

export const fetchDeck = async (id: string): Promise<FlashcardDeck> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${id}`)
    if (!response.ok) {
      throw new Error('덱을 가져오는데 실패했습니다.')
    }
    const data = await response.json()
    return {
      id: data.data.id,
      title: data.data.title,
      description: data.data.desc,
      category: data.data.category,
      tag: data.data.tag,
      cardCount: data.data.cardCount,
      items: data.data.cards?.map((card: any) => ({
        id: card.id,
        question: card.title,
        answer: card.desc
      })) || []
    }
  } catch (error) {
    console.error('Error fetching deck:', error)
    throw error
  }
}

export const createDeck = async (deck: Omit<FlashcardDeck, 'id'>): Promise<FlashcardDeck> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        title: deck.title,
        description: deck.description
      }),
    })
    if (!response.ok) {
      throw new Error('덱 생성에 실패했습니다.')
    }
    const data = await response.json()
    return {
      id: data.data.id,
      title: data.data.title,
      description: data.data.description,
      category: data.data.category,
      tag: data.data.tag,
      cardCount: data.data.cardCount,
      items: []
    }
  } catch (error) {
    console.error('Error creating deck:', error)
    throw error
  }
}

export const updateDeck = async (deck: FlashcardDeck): Promise<FlashcardDeck> => {
  try {
    console.log(deck)
    const response = await fetch(`${API_BASE_URL}/deck/update`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: deck.id,
        title: deck.title,
        desc: deck.description,
        category: deck.category,
        tag: deck.tag,
        cards: deck.items.map((item) => ({
          cardId: item.id,
          title: item.question,
          desc: item.answer
        }))
      }),
    })
    if (!response.ok) {
      throw new Error('덱 수정에 실패했습니다.')
    }
    const data = await response.json()
    console.log(data)
    return {
      id: data.data.id,
      title: data.data.title,
      description: data.data.desc,
      category: data.data.category,
      tag: data.data.tag,
      cardCount: data.data.cardCount,
      items: deck.items
    }
  } catch (error) {
    console.error('Error updating deck:', error)
    throw error
  }
}

export const deleteDeck = async (id: string): Promise<void> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${id}/delete`, {
      method: 'DELETE',
    })
    if (!response.ok) {
      throw new Error('덱 삭제에 실패했습니다.')
    }
  } catch (error) {
    console.error('Error deleting deck:', error)
    throw error
  }
}

export const createCard = async (deckId: string, card: Omit<FlashcardItem, 'id'>): Promise<FlashcardItem> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${deckId}/cards`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        question: card.question,
        answer: card.answer
      }),
    })
    if (!response.ok) {
      throw new Error('카드 생성에 실패했습니다.')
    }
    const data = await response.json()
    return {
      id: data.data.id,
      question: data.data.question,
      answer: data.data.answer
    }
  } catch (error) {
    console.error('Error creating card:', error)
    throw error
  }
}

export const deleteCard = async (deckId: string, cardId: string): Promise<void> => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${deckId}/delete/${cardId}`, {
      method: 'DELETE',
    })
    if (!response.ok) {
      throw new Error('카드 삭제에 실패했습니다.')
    }
  } catch (error) {
    console.error('Error deleting card:', error)
    throw error
  }
}
