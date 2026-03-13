# Dokumentacja techniczna projektu TO-DO

## 1. Cel projektu
Aplikacja TO-DO to konsolowy menedżer zadań napisany w Java (JDK 11), służący do:
- dodawania zadań,
- oznaczania zadań jako wykonane,
- edycji opisów i kategorii,
- usuwania zadań aktywnych lub wykonanych,
- przeglądania listy aktywnych i wykonanych,
- eksportu/importu danych do/z pliku tekstowego,
- zapisu stanu aplikacji do pliku binarnego.

## 2. Struktura modułu
Projekt znajduje się w katalogu:
`Rok_1/Semestr_2/Programowanie_w_języku_JAVA/TO-DO`

Najważniejsze pliki źródłowe:
- `Main.java` – warstwa interfejsu CLI i pętla programu,
- `TaskManager.java` – logika biznesowa i operacje I/O,
- `Task.java` – abstrakcyjny model zadania,
- `Category.java` – enum kategorii,
- `PersonalTask.java`, `WorkTask.java`, `HobbyTask.java`, `OtherTask.java` – klasy wyspecjalizowane,
- `run.sh` – skrypt kompilacji i uruchamiania.

## 3. Model danych

### 3.1. `Task` (abstrakcyjna klasa bazowa)
Pola:
- `id: int`
- `description: String`
- `category: Category`
- `isCompleted: boolean`

Interfejs publiczny:
- gettery/settery dla opisu, kategorii i flagi wykonania,
- metoda `toString()` zawierająca identyfikator, opis i status (`(DONE)` / `(TODO)`).

Właściwości techniczne:
- klasa implementuje `Serializable`,
- zdefiniowane `serialVersionUID = 1L`.

### 3.2. Kategorie (`Category`)
Dostępne wartości enum:
- `PERSONAL`
- `WORK`
- `HOBBY`
- `OTHER`

### 3.3. Klasy specjalizowane
Każda klasa (`PersonalTask`, `WorkTask`, `HobbyTask`, `OtherTask`):
- dziedziczy po `Task`,
- ustawia odpowiednią kategorię w konstruktorze,
- nadpisuje `toString()` przez prefiks, np. `[WORK] ...`.

## 4. Logika biznesowa (`TaskManager`)

### 4.1. Przechowywanie danych
- Kolekcja zadań: `ArrayList<Task>`.
- Licznik ID: `nextId` (inkrementowany przy dodawaniu).
- Plik utrwalania binarnego: `tasks.dat`.

### 4.2. Operacje na zadaniach
- `addTask(description, category)` – tworzy zadanie odpowiedniego typu i zapisuje stan.
- `markTaskAsCompleted(id)` – ustawia `isCompleted = true` dla zadania aktywnego.
- `updateTask(id, newDescription, newCategory)` – modyfikuje dane wskazanego zadania.
- `deleteActiveTask(id)` – usuwa tylko zadanie aktywne.
- `deleteCompletedTask(id)` – usuwa tylko zadanie wykonane.
- `listTasks()` – wyświetla osobno sekcję aktywną i wykonaną.

### 4.3. Utrwalanie i zgodność danych
- `saveTasks()` zapisuje listę zadań przez `ObjectOutputStream`.
- `loadTasks()` odtwarza dane z `tasks.dat`.

Mechanizm zgodności wstecznej:
- jeśli podczas odczytu wystąpi `ClassNotFoundException` (np. stary pakiet klas),
  - lista zadań jest resetowana,
  - stary plik jest przenoszony do kopii `tasks.dat.legacy.bak`,
  - tworzony jest świeży zapis aktualnego formatu.

Dzięki temu aplikacja nie kończy pracy wyjątkiem przy migracji struktury klas.

### 4.4. Import/eksport TXT
Format linii (separator `|`):
`ID|OPIS|KATEGORIA|STATUS`

Przykład:
`3|Kupić mleko|PERSONAL|DONE`

Zasady:
- eksport zapisuje wszystkie zadania,
- import czyści obecną listę i ładuje dane z pliku,
- po imporcie `nextId` ustawiany jest na `max(ID)+1`,
- wiersze z niepoprawną kategorią są pomijane,
- pusta nazwa pliku jest odrzucana (walidacja wejścia).

## 5. Interfejs użytkownika (`Main`)

### 5.1. Menu główne
1. Dodaj zadanie
2. Oznacz zadanie jako wykonane
3. Aktualizuj zadanie
4. Usuń zadanie
5. Lista zadań
6. Eksportuj do TXT
7. Importuj z TXT
8. Wyjście

### 5.2. Walidacja kategorii
Wprowadzanie kategorii obsługuje:
- liczby `1..4` (mapowane na kategorie),
- teksty nazw kategorii (np. `work`, `PERSONAL`).

Niepoprawne dane nie przerywają programu – użytkownik jest proszony o ponowne podanie wartości.

### 5.3. Rozdzielenie usuwania
W opcji usuwania użytkownik wybiera typ kasowania:
- usunięcie zadania aktywnego,
- usunięcie zadania wykonanego.

Zapewnia to wyraźne rozróżnienie między „oznacz jako zrobione” a „fizycznie usuń”.

## 6. Obsługa błędów
W projekcie zastosowano obsługę wyjątków i walidację dla:
- błędów odczytu/zapisu plików,
- niezgodnych danych serializowanych,
- pustej ścieżki przy imporcie/eksporcie,
- niepoprawnych kategorii podczas importu i wejścia z konsoli,
- błędnego typu danych liczbowych z wejścia (`InputMismatchException`).

## 7. Kompilacja i uruchamianie

### 7.1. Skrypt
Uruchom:
```bash
cd Rok_1/Semestr_2/Programowanie_w_języku_JAVA/TO-DO
./run.sh
```

Skrypt:
- kompiluje wszystkie `*.java` do katalogu `/workspaces/huj/.vscode/bin`,
- uruchamia aplikację komendą `java -cp /workspaces/huj/.vscode/bin Main`.

### 7.2. Ręcznie
```bash
cd Rok_1/Semestr_2/Programowanie_w_języku_JAVA/TO-DO
mkdir -p /workspaces/huj/.vscode/bin
javac -d /workspaces/huj/.vscode/bin *.java
java -cp /workspaces/huj/.vscode/bin Main
```

## 8. Ograniczenia i uwagi
- Aplikacja jest jednowątkowa i konsolowa.
- Utrwalanie binarne zależy od zgodności klas Java; dla zmian modelu utrzymywana jest awaryjna ścieżka migracyjna (backup pliku legacy).
- Import TXT zakłada poprawny separator `|`; nieprawidłowe wiersze są pomijane.

## 9. Rekomendacje dalszego rozwoju
- Dodać testy jednostkowe dla `TaskManager`.
- Rozszerzyć walidację danych wejściowych (np. minimalna długość opisu).
- Wydzielić warstwę I/O i logikę domenową do osobnych pakietów przy dalszej rozbudowie.
