const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8081/api/user-service';

export const fetchDecks = async (page: number = 0, size: number = 10) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck?page=${page}&size=${size}&sort=id,desc`);
    if (!response.ok) {
      throw new Error('덱 목록을 가져오는데 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching decks:', error);
    throw error;
  }
};

export const fetchDeck = async (id: number) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${id}`);
    if (!response.ok) {
      throw new Error('덱을 가져오는데 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching deck:', error);
    throw error;
  }
};

export const createDeck = async (deckData: any) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(deckData),
    });
    if (!response.ok) {
      throw new Error('덱 생성에 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error creating deck:', error);
    throw error;
  }
};

export const updateDeck = async (deckData: any) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/update`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(deckData),
    });
    if (!response.ok) {
      throw new Error('덱 수정에 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error updating deck:', error);
    throw error;
  }
};

export const deleteDeck = async (id: number) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${id}/delete`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('덱 삭제에 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error deleting deck:', error);
    throw error;
  }
};

export const createCard = async (deckId: number, cardData: any) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${deckId}/cards`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(cardData),
    });
    if (!response.ok) {
      throw new Error('카드 생성에 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error creating card:', error);
    throw error;
  }
};

export const deleteCard = async (deckId: number, cardId: number) => {
  try {
    const response = await fetch(`${API_BASE_URL}/deck/${deckId}/delete/${cardId}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('카드 삭제에 실패했습니다.');
    }
    return await response.json();
  } catch (error) {
    console.error('Error deleting card:', error);
    throw error;
  }
}; 