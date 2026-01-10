# LINEAR EQUATIONS SOLVER

## WYMAGANIA FUNKCJONALNE

### 1. Wczytywanie danych

- Przyjmowanie danych poprzez interfejs przeglądarkowy
- Automatyczne sprawdzanie wymiarów, wykrywanie wartości NaN
- Odczyt układów równań z plików tekstowych

### 2. Reprezentacja danych

- Klasa `Matrix` - pełna implementacja operacji na macierzach:
  - Tworzenie, kopiowanie, transpozycja
  - Mnożenie, dodawanie macierzy
- Klasa `Vector` - rozszerza klasę `Matrix`, specjalizacja dla wektorów:
  - Iloczyn skalarny
  - Dodawanie, odejmowanie, mnożenie przez skalar
  - Konwersje do/z tablic
- Reprezentacja układu równań liniowych Ax = b

### 3. Algorytmy rozwiązywania

- Metoda Gaussa (`GaussSolver`):
  - Eliminacja Gaussa z częściowym wyborem elementu głównego (pivoting)
  - Obsługa macierzy osobliwych i prawie osobliwych
  - Podstawienie wstecz
- Metoda Jacobiego (`JacobiSolver`):
  - Iteracyjna metoda dla macierzy diagonalnie dominujących
  - Detekcja rozbieżności i dzielenia przez zero
  - Kontrola maksymalnej liczby iteracji
- Metoda Gaussa-Seidla (`GaussSeidelSolver`):
  - Iteracyjna metoda z natychmiastowym wykorzystaniem nowych wartości
  - Wymaganie diagonalnej dominacji dla gwarancji zbieżności

### 4. Obsługa różnych przypadków układów

- Układy kwadratowe - wymagane przez wszystkie zaimplementowane solvery
- Automatyczne sprawdzanie zgodności A i b
- Obsługa macierzy osobliwych - wykrywanie i odpowiednie komunikaty błędów

### 5. Analiza i walidacja układu

- Diagonalna dominacja - sprawdzanie warunku zbieżności metod iteracyjnych
- Obliczanie residuum - norma |Ax - b| dla weryfikacji rozwiązania

### 6. Konfiguracja solverów

- Wybór metody (Gauss, Jacobi, Gauss-Seidel)
- Parametry iteracyjne:
  - Maksymalna liczba iteracji (domyślnie 1000)
  - Tolerancja zbieżności (domyślnie 1e-10)

### 7. Prezentacja wyników

- Wypisanie obliczonych rozwiązań
- Informacje diagnostyczne:
  - Status zbieżności/niezbieżności
  - Liczba wykonanych iteracji
  - Czas obliczeń w milisekundach
  - Norma residuum dla oceny dokładności

### 8. Interfejs użytkownika

- Prosty interfejs przeglądarkowy

## STRUKTURA KLAS

- `Matrix` - reprezentacja macierzy i operacje podstawowe, klasa bazowa dla `Vector`
- `Vector` - reprezentacja wektora wyrazów wolnych
- `LinearSystem` - reprezentacja układu równań
- `LinearSolver` - klasa bazowa dla metod rozwiązywania
- `GaussSolver`, `JacobiSolver`, `GaussSeidelSolver` - implementacje algorytmów
- `FileHandler` - obsługa plików
- `Result` - przechowywanie i prezentacja wyników
- `Validator` - walidacja danych i warunków
