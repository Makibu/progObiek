# Linear System Solver

Aplikacja webowa do rozwiązywania układów równań liniowych przy użyciu różnych metod numerycznych, z frontendem w Next.js i backendem w Javie.

## Funkcje

- **Trzy metody rozwiązywania:**
  - Eliminacja Gaussa (z częściowym wyborem elementu podstawowego)
  - Metoda iteracyjna Jacobiego
  - Metoda iteracyjna Gaussa-Seidela
- **Interaktywne wprowadzanie macierzy:**
  - Regulowany rozmiar macierzy (1-10)
  - Ręczne wprowadzanie przez siatkę pól
  - Wsparcie dla wczytywania z pliku (format TXT)
- **Walidacja w czasie rzeczywistym** danych wejściowych
- **Szczegółowe wyniki** obejmujące:
  - Wektor rozwiązania
  - Normę residuum
  - Liczbę iteracji
  - Czas obliczeń

## Struktura projektu

### Frontend (React/TypeScript)
- `page.tsx` - Główny komponent React z interfejsem i obsługą danych wejściowych
- Wykorzystuje komponenty Shadcn UI dla czystego interfejsu

### Backend (Java)
- `Main.java` - Serwer HTTP (port 8080)
- `LinearSystemController.java` - Obsługa żądań i koordynacja solverów
- Solvery:
  - `GaussSolver.java` - Eliminacja Gaussa
  - `JacobiSolver.java` - Metoda iteracyjna Jacobiego
  - `GaussSeidelSolver.java` - Metoda iteracyjna Gaussa-Seidela
- Klasy rdzeniowe:
  - `LinearSystem.java` - Reprezentacja układu równań
  - `Matrix.java` - Operacje na macierzach
  - `Vector.java` - Operacje na wektorach
  - `Result.java` - Kontener wyników
  - `Validator.java` - Walidacja i analiza danych wejściowych

## Wymagania wstępne

- **Node.js** (wersja 16+)
- **Java JDK** (wersja 11+)
- **npm** lub **yarn** (menedżer pakietów)

## Instalacja i uruchomienie

### 1. Backend (Serwer Java)
```bash
# Kompilacja plików Java
javac -cp ".;json.jar" api/**/*.java api/*.java

# Uruchomienie serwera
java -cp ".;json.jar" api.Main
```
Serwer zostanie uruchomiony pod adresem `http://localhost:8080`

**Uwaga:** Potrzebna jest biblioteka `json.jar` do obsługi JSON w Javie.

### 2. Frontend (Aplikacja React)
```bash
# Instalacja zależności
npm install

# Uruchomienie serwera deweloperskiego
npm run dev
```
Frontend będzie dostępny pod adresem `http://localhost:3000`

## Jak używać

### Metoda 1: Ręczne wprowadzanie
1. Wybierz solver z listy rozwijanej
2. Wybierz rozmiar macierzy (n × n)
3. Wypełnij wartości macierzy w siatce
4. Wprowadź wartości wektora b
5. Kliknij "Solve" (Rozwiąż)

### Metoda 2: Wczytanie z pliku
1. Przygotuj plik TXT w formacie:
- Pierwsza linia: rozmiar macierzy `n` (liczba całkowita)
- Kolejne `n` linii: wiersze macierzy, każdy z `n` liczbami
- Kolejne `n` linii: elementy wektora b

Przykład dla układu 3×3:
```
3
2 1 -1
-3 -1 2
-2 1 2
8
-11
-3
```

2. Załaduj plik
3. Wybierz solver i kliknij "Solve" (Rozwiąż)

## Szczegóły solverów

### Eliminacja Gaussa
- Metoda bezpośrednia rozwiązywania układów liniowych
- Zawiera częściowy wybór elementu podstawowego dla stabilności numerycznej
- Najlepsza dla małych i średnich układów

### Metoda Jacobiego
- Metoda iteracyjna dla dużych układów
- Wymaga dominacji diagonalnej dla zbieżności
- Równoległe aktualizowanie zmiennych
- Pokazuje liczbę iteracji

### Metoda Gaussa-Seidela
- Ulepszona metoda iteracyjna
- Szybsza zbieżność niż metoda Jacobiego
- Sekwencyjne aktualizowanie z użyciem najnowszych wartości
- Sprawdza dominację diagonalną

## Ważne uwagi

1. **Ostrzeżenia o zbieżności** mogą pojawić się dla metod iteracyjnych, jeśli macierz nie jest diagonalnie dominująca
2. **Wartości NaN/Nieskończoność** wskazują na niestabilność numeryczną
3. **Maksymalna liczba iteracji** domyślnie ograniczona do 1001
4. **Tolerancja** dla metod iteracyjnych ustawiona na 1e-10

## Rozwiązywanie problemów

- **"Server error"**: Upewnij się, że backend Java działa na porcie 8080
- **"Failed to fetch"**: Sprawdź ustawienia CORS i połączenie sieciowe
- **"Matrix may not converge"**: Spróbuj użyć eliminacji Gaussa zamiast metod iteracyjnych
- **Nieprawidłowy format pliku**: Sprawdź, czy plik TXT dokładnie odpowiada formatowi

## Licencja

Ten projekt jest przeznaczony do celów edukacyjnych. Można go swobodnie modyfikować i rozszerzać w celu nauki metod numerycznych i rozwoju full-stack.
