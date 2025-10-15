# LINEAR EQUATIONS SOLVER

## FUNKCJONALNOŚCI

### 1. Wczytywanie danych

- Wczytywanie macierzy współczynników i wektora wyrazów wolnych z pliku
- Możliwość ręcznego wprowadzania danych przez użytkownika
- Walidacja poprawności danych (wymiary macierzy, brak wartości NaN)

### 2. Reprezentacja macierzy i wektora

- Klasa `Matrix` (przechowująca tablicę współczynników)
- Klasa `Vector` (dla wektora prawej strony równań)
- Metody pomocnicze: dodawanie, mnożenie, transpozycja, kopiowanie

### 3. Implementacja algorytmów

- Metoda Gaussa
- Metoda Jacobiego (iteracyjna)
- Metoda Gaussa-Seidla (iteracyjna)
- Klasa bazowa `LinearSolver` z metodą abstrakcyjną `solve(Matrix A, Vector b)` i klasy pochodne: `GaussSolver`, `JacobiSolver` i `GaussSeidelSolver`

### 4. Obsługa różnych przypadków

- Układy oznaczone, nieoznaczone, sprzeczne
- Macierze kwadratowe i prostokątne
- Obsługa macierzy osobliwych

### 5. Analiza układu

- Obliczanie wyznacznika macierzy
- Sprawdzanie diagonalnej dominacji (dla metod iteracyjnych)
- Wyznaczanie rzędu macierzy

### 6. Konfiguracja rozwiązywania

- Wybór metody rozwiązania przez użytkownika
- Ustawienie parametrów (dokładność, maksymalna liczba iteracji)
- Możliwość wyboru elementu głównego (pivoting) w metodzie Gaussa

### 7. Prezentacja wyników

- Wyświetlanie rozwiązania (wektor rozwiązań)
- Informacja o liczbie wykonanych iteracji
- Czas wykonania obliczeń
- Informacja o zbieżności lub błędzie obliczeń

### 8. Zapisywanie wyników

- Zapis wyników do pliku tekstowego
- Eksport raportu z pełną analizą
- Zapis macierzy do pliku w formacie czytelnym

### 9. Interfejs użytkownika

- Prosty interfejs graficzny z menu

### 10. Testy i walidacja

- Generowanie losowych układów do testów
- Automatyczne testy poprawności rozwiązań
- Porównanie efektywności różnych metod

## STRUKTURA KLAS

- `Matrix` - reprezentacja macierzy i operacje podstawowe, klasa bazowa dla `Vector`
- `Vector` - reprezentacja wektora wyrazów wolnych
- `LinearSystem` - reprezentacja układu równań
- `LinearSolver` - klasa bazowa dla metod rozwiązywania
- `GaussSolver`, `JacobiSolver`, `GaussSeidelSolver` - implementacje algorytmów
- `FileHandler` - obsługa plików
- `Result` - przechowywanie i prezentacja wyników
- `Validator` - walidacja danych i warunków
