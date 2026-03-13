## TO-DO list – dokumentacja techniczna

Konsolowa aplikacja TO-DO napisana w Java (JDK 11+). Projekt działa w trybie tekstowym i pozwala na podstawowe zarządzanie zadaniami z podziałem na kategorie.

Główne możliwości:
- dodawanie zadań z przypisaną kategorią,
- oznaczanie zadań jako wykonane,
- edycja opisu i kategorii istniejących zadań,
- usuwanie zadań aktywnych lub już ukończonych,
- wyświetlanie listy zadań (oddzielnie aktywne i ukończone),
- eksport/import zadań do/z pliku tekstowego,
- automatyczny zapis stanu listy do pliku binarnego.

---

## Struktura projektu i klasy

Pliki źródłowe znajdują się w katalogu głównym repozytorium:

- Main.java – warstwa interfejsu tekstowego (CLI) i pętla główna programu,
- TaskManager.java – logika biznesowa zarządzania zadaniami oraz operacje I/O (plik binarny + TXT),
- Task.java – abstrakcyjny model pojedynczego zadania,
- Category.java – enum z listą kategorii zadań,
- PersonalTask.java, WorkTask.java, HobbyTask.java, EducationTask.java, OtherTask.java – klasy specjalizowane dziedziczące po Task.

### Category.java

Typ wyliczeniowy kategorii zadań:

```java
public enum Category {
        PERSONAL, WORK, HOBBY, EDUCATION, OTHER
}
```

Wartości:
- PERSONAL – sprawy osobiste,
- WORK – zadania zawodowe/służbowe,
- HOBBY – zadania związane z hobby,
- EDUCATION – nauka / studia / kursy,
- OTHER – pozostałe.

Enum jest używany zarówno w modelu danych (Task), jak i przy parsowaniu wejścia użytkownika oraz plików TXT.

### Task.java (model bazowy zadania)

Abstrakcyjna klasa bazowa reprezentująca dowolne zadanie.

Pola (protected):
- int id – unikalny identyfikator zadania w ramach bieżącej listy,
- String description – opis zadania,
- Category category – kategoria zadania (PERSONAL/WORK/HOBBY/EDUCATION/OTHER),
- boolean isCompleted – flaga, czy zadanie jest zakończone.

Właściwości techniczne:
- klasa implementuje Serializable,
- stałe pole `private static final long serialVersionUID = 1L;` zapewnia spójność serializacji.

Konstruktor:
- Task(int id, String description, Category category) – ustawia identyfikator, opis i kategorię; pole isCompleted domyślnie ustawiane jest na false.

Metody publiczne:
- int getId() – zwraca id zadania,
- String getDescription() / void setDescription(String description) – pobranie/zmiana opisu,
- Category getCategory() / void setCategory(Category category) – pobranie/zmiana kategorii,
- boolean isCompleted() / void setCompleted(boolean completed) – pobranie/zmiana statusu ukończenia zadania.

Metoda toString():
- buduje tekst w formacie: `[ID] OPIS | STATUS`,
- STATUS to "✓ DONE" dla isCompleted == true lub "○ ACTIVE" dla zadań aktywnych,
- klasy pochodne dodają własny prefiks (np. [WORK], [HOBBY]).

### Klasy zadań specjalizowanych

Każda z poniższych klas dziedziczy po Task, ustawia kategorię i nadpisuje toString() dodając prefiks.

#### PersonalTask.java
- konstruktor: PersonalTask(int id, String description) – wywołuje super(id, description, Category.PERSONAL),
- toString() – poprzedza wynik super.toString() prefiksem `[PERSONAL]`.

#### WorkTask.java
- konstruktor: WorkTask(int id, String description) – super(id, description, Category.WORK),
- toString() – prefiks `[WORK]`.

#### HobbyTask.java
- konstruktor: HobbyTask(int id, String description) – super(id, description, Category.HOBBY),
- toString() – prefiks `[HOBBY]`.

#### EducationTask.java
- konstruktor: EducationTask(int id, String description) – super(id, description, Category.EDUCATION),
- toString() – prefiks `[EDUCATION]`.

#### OtherTask.java
- konstruktor: OtherTask(int id, String description) – super(id, description, Category.OTHER),
- toString() – prefiks `[OTHER]`.

---

## TaskManager.java – logika biznesowa

Klasa odpowiedzialna za przechowywanie zadań w pamięci, operacje CRUD oraz zapis/odczyt z plików.

Pola:
- List<Task> tasks – lista wszystkich zadań (zarówno aktywnych, jak i ukończonych),
- String filePath = "tasks.dat" – ścieżka do pliku binarnego z zapisanymi zadaniami.

Konstruktor:
- TaskManager() – inicjalizuje obiekt i wywołuje metodę loadTasks(), która próbuje załadować stan z pliku tasks.dat.

### Metody publiczne

#### void addTask(Task task)
- dodaje nowe zadanie do listy tasks,
- po dodaniu wywołuje saveTasks() – natychmiastowy zapis na dysk.

#### boolean markTaskAsCompleted(int id)
- wyszukuje zadanie o podanym id,
- jeśli jest znalezione – ustawia isCompleted = true, wywołuje saveTasks() i zwraca true,
- jeśli brak zadania o takim id – zwraca false.

#### boolean updateTask(int id, String description, Category category)
- odnajduje zadanie o podanym id,
- jeśli istnieje – aktualizuje opis i kategorię, zapisuje zmiany (saveTasks()) i zwraca true,
- w przeciwnym razie – zwraca false.

#### boolean deleteActiveTask(int id)
- usuwa z listy zadanie o danym id **tylko jeśli** isCompleted == false,
- przy powodzeniu zapisuje stan (saveTasks()) i zwraca true,
- jeśli nie znaleziono aktywnego zadania o tym id – false.

#### boolean deleteCompletedTask(int id)
- usuwa z listy zadanie o danym id **tylko jeśli** isCompleted == true,
- przy powodzeniu zapisuje stan (saveTasks()) i zwraca true,
- w przeciwnym razie – false.

#### void listTasks()
- dzieli listę tasks na dwie kolekcje: aktywne (isCompleted == false) i ukończone (isCompleted == true),
- wypisuje w konsoli dwie sekcje:
    - "=== Active Tasks (N) ===" – zadania aktywne lub komunikat "No active tasks.",
    - "=== Completed Tasks (M) ===" – zadania ukończone lub "No completed tasks yet.",
- do wypisywania używa toString() poszczególnych zadań.

#### int getNextId()
- zwraca kolejne id dla nowego zadania,
- jeśli lista jest pusta – 1,
- w przeciwnym razie – id ostatniego zadania + 1.

#### void exportToTxt(String filePath)
- waliduje ścieżkę: jeśli jest null lub pusta – wypisuje komunikat i przerywa,
- otwiera FileWriter i dla każdego zadania zapisuje linię w formacie:
    - `ID|OPIS|KATEGORIA|IS_COMPLETED`,
    - IS_COMPLETED – wartość boolean (true/false),
- po sukcesie wypisuje komunikat: "Tasks exported to ...".

#### void importFromTxt(String filePath)
- waliduje ścieżkę analogicznie jak exportToTxt,
- odczytuje plik linia po linii (BufferedReader),
- każdą linię dzieli po separatorze "|":
    - oczekuje dokładnie 4 elementów: id, opis, kategoria, status,
    - id – parsowane do int,
    - opis – String,
    - kategoria – Category.valueOf(...) (w razie IllegalArgumentException – linia jest pomijana),
    - status – Boolean.parseBoolean(...),
- dla poprawnych wierszy tworzone jest odpowiednie zadanie poprzez prywatną metodę createTaskByCategory(...),
- flaga ukończenia jest ustawiana na podstawie wczytanej wartości,
- zaimportowane zadania są dodawane do istniejącej listy tasks, po czym stan jest zapisywany (saveTasks()),
- na końcu wypisywany jest komunikat o zakończonym imporcie.

### Metody prywatne (TaskManager)

#### void saveTasks()
- używa ObjectOutputStream i FileOutputStream do zapisania listy tasks do pliku binarnego filePath ("tasks.dat"),
- w przypadku błędów IO wypisuje komunikat i stack trace.

#### void loadTasks()
- próbuje wczytać listę zadań z pliku tasks.dat za pomocą ObjectInputStream,
- scenariusze:
    - FileNotFoundException – brak wcześniejszego pliku; wypisywany jest komunikat o świełym starcie, lista ustawiana na nową ArrayList<>,
    - ClassNotFoundException – wykryty "legacy" lub niezgodny format; lista ustawiana na pustą, wykonywany jest backupCorruptedFile() i natychmiastowy saveTasks(),
    - inne IOException – błąd wczytywania; lista ustawiana na pustą, wypisywany stack trace.

#### void backupCorruptedFile()
- jeśli plik tasks.dat istnieje – przenosi go do pliku tasks.dat.legacy.bak (Files.move z REPLACE_EXISTING),
- ewentualne błędy IO są logowane komunikatem w konsoli.

#### Task createTaskByCategory(int id, String description, Category category)
- na podstawie przekazanej kategorii zwraca instancję odpowiedniej klasy zadań:
    - PERSONAL → PersonalTask,
    - WORK → WorkTask,
    - HOBBY → HobbyTask,
    - EDUCATION → EducationTask,
    - OTHER (lub domyślne) → OtherTask.

---

## Main.java – interfejs CLI

Main odpowiada za komunikację z użytkownikiem po stronie konsoli.

Pola statyczne:
- Scanner scanner – do odczytu wejścia użytkownika (System.in),
- TaskManager taskManager – główny komponent logiki biznesowej.

### Metoda main(String[] args)

Główna pętla programu:
- wyświetla menu:
    1. Add Task
    2. Mark Task as Completed
    3. Update Task
    4. Delete Task
    5. List Tasks
    6. Export Tasks to TXT
    7. Import Tasks from TXT
    8. Exit
- pobiera wybór użytkownika (int),
- w zależności od opcji wywołuje jedną z metod prywatnych:
    - addTask(), markTaskAsCompleted(), updateTask(), deleteTask(), exportTasks(), importTasks(),
- w przypadku wyboru 8 – wypisuje komunikat "Exiting..." i kończy działanie,
- dla niepoprawnej opcji drukuje komunikat o błędzie.

### addTask()
- prosi użytkownika o opis zadania (String),
- wywołuje readCategoryFromInput() w celu pobrania kategorii,
- tworzy instancję odpowiedniej klasy zadania poprzez createTaskByCategory(taskManager.getNextId(), description, category),
- dodaje zadanie do TaskManager (addTask(task)),
- wypisuje komunikat o sukcesie.

### updateTask()
- prosi o id zadania (int),
- następnie o nowy opis (String) i kategorię (readCategoryFromInput()),
- wywołuje taskManager.updateTask(id, description, category),
- na podstawie wartości zwrotnej drukuje "Task updated successfully." lub "Task not found.".

### markTaskAsCompleted()
- prosi o id zadania (int),
- wywołuje taskManager.markTaskAsCompleted(id),
- dla true – "Task marked as completed.",
- dla false – "Task not found.".

### deleteTask()
- wyświetla podmenu:
    - 1. Delete active task
    - 2. Delete completed task
- prosi o wybór typu usuwania (int),
- prosi o id zadania (int),
- w zależności od wyboru wywołuje deleteActiveTask(id) lub deleteCompletedTask(id) w TaskManager,
- na podstawie wyniku wypisuje odpowiedni komunikat (sukces / brak zadania w danej grupie).

### exportTasks()
- prosi użytkownika o ścieżkę pliku (np. tasks.txt),
- wywołuje taskManager.exportToTxt(filePath).

### importTasks()
- prosi użytkownika o ścieżkę pliku (np. tasks.txt),
- wywołuje taskManager.importFromTxt(filePath).

### createTaskByCategory(int id, String description, Category category)
- lokalna wersja metody fabrykującej obiekt zadania; mapa kategorii na odpowiednią klasę (analogicznie jak w TaskManager),
- używana przy tworzeniu nowych zadań z wejścia użytkownika.

### readCategoryFromInput()
- w pętli prosi użytkownika o podanie kategorii w formie liczby (1–5) lub nazwy,
- jeśli użytkownik poda:
    - "1" → PERSONAL,
    - "2" → WORK,
    - "3" → HOBBY,
    - "4" → EDUCATION,
    - "5" → OTHER,
- w przeciwnym wypadku próbuje sparsować tekst jako nazwę enum (Category.valueOf(rawInput.toUpperCase())),
- przy błędzie (IllegalArgumentException) wypisuje informację o dozwolonych wartościach i ponawia pytanie.

---

## Format danych i obsługa błędów

- Dane binarne są przechowywane w pliku tasks.dat przy użyciu mechanizmu serializacji Javy (ObjectOutputStream / ObjectInputStream).
- Przy zmianach modelu (np. różnice w strukturze klas) może wystąpić ClassNotFoundException – projekt obsługuje to jako przypadek "legacy":
    - bieżąca lista jest resetowana,
    - plik jest przenoszony do tasks.dat.legacy.bak,
    - zapisywana jest nowa, pusta lista zadań.
- Operacje na plikach TXT są odporne na niepoprawne wiersze:
    - linie o złej liczbie pól są pomijane,
    - linie z nieznaną kategorią są pomijane,
    - pusta ścieżka pliku powoduje wypisanie komunikatu i przerwanie operacji.

---

## Wymagania i uruchomienie

Wymagania:
- Java Development Kit (JDK) 11+,
- system z konsolą (macOS, Linux, Windows).

Uruchomienie z katalogu projektu:

```bash
javac *.java
java Main
```

W katalogu roboczym zostanie utworzony plik tasks.dat z zapisanymi zadaniami.

---

## Możliwe kierunki rozwoju

- dodanie testów jednostkowych (np. dla TaskManager),
- rozbudowa walidacji danych wejściowych (długość opisu, znaki specjalne itp.),
- podział projektu na pakiety (model, service, ui, persistence),
- dodanie warstwy graficznej (np. JavaFX),
- udostępnienie funkcjonalności jako REST API.
