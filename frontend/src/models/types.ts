export interface User {
  id: string;
  email: string;
  username: string;
}

export interface Flashcard {
  id: string;
  question: string;
  answer: string;
  flashcardSetId?: string;
}

export interface FlashcardSet {
  id: string;
  title: string;
  description: string;
  category: string;
  isPublic: boolean;
  createdDate: string;
  updatedDate: string;
  ownerId: string;
  owner?: { id: string; username: string };
  flashcards: Flashcard[];
}

export interface LearningSessionStart {
  sessionId: string;
  flashcardSetId: string;
  flashcards: Pick<Flashcard, 'id' | 'question' | 'answer'>[];
}

export interface UserStatistics {
  userId: string;
  totalCardsStudied: number;
  correctAnswers: number;
  incorrectAnswers: number;
  successPercentage: number;
  progressHistory: ProgressHistoryEntry[];
}

export interface ProgressHistoryEntry {
  sessionId: string;
  flashcardSetId: string;
  flashcardSetTitle: string;
  studiedAt: string;
  cardsStudied: number;
  correctAnswers: number;
  incorrectAnswers: number;
  successPercentage: number;
}

export interface AuthResponse {
  user: User;
  token: string;
}
