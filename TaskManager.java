import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private final String filePath = "tasks.dat";

    public TaskManager() {
        loadTasks();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public boolean markTaskAsCompleted(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setCompleted(true);
                saveTasks();
                return true;
            }
        }
        return false;
    }

    public boolean updateTask(int id, String description, Category category) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                task.setDescription(description);
                task.setCategory(category);
                saveTasks();
                return true;
            }
        }
        return false;
    }

    public boolean deleteActiveTask(int id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id && !task.isCompleted());
        if (removed) {
            saveTasks();
        }
        return removed;
    }

    public boolean deleteCompletedTask(int id) {
        boolean removed = tasks.removeIf(task -> task.getId() == id && task.isCompleted());
        if (removed) {
            saveTasks();
        }
        return removed;
    }

    public void listTasks() {
        List<Task> activeTasks = new ArrayList<>();
        List<Task> completedTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            } else {
                activeTasks.add(task);
            }
        }

        System.out.println("\n=== Active Tasks (" + activeTasks.size() + ") ===");
        if (activeTasks.isEmpty()) {
            System.out.println("No active tasks.");
        } else {
            activeTasks.forEach(System.out::println);
        }

        System.out.println("\n=== Completed Tasks (" + completedTasks.size() + ") ===");
        if (completedTasks.isEmpty()) {
            System.out.println("No completed tasks yet.");
        } else {
            completedTasks.forEach(System.out::println);
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(tasks);
        } catch (IOException e) {
            System.out.println("Error saving tasks.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            tasks = (List<Task>) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous tasks found. Starting fresh.");
        } catch (ClassNotFoundException e) {
            System.out.println("Detected legacy/incompatible tasks file. Starting with empty list.");
            tasks = new ArrayList<>();
            backupCorruptedFile();
            saveTasks();
        } catch (IOException e) {
            System.out.println("Error loading tasks. Starting with empty list.");
            tasks = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void backupCorruptedFile() {
        try {
            Path source = Path.of(filePath);
            if (Files.exists(source)) {
                Path backup = Path.of(filePath + ".legacy.bak");
                Files.move(source, backup, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.out.println("Could not create backup of legacy tasks file.");
        }
    }

    public int getNextId() {
        return tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
    }

    // Export tasks to TXT file
    public void exportToTxt(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("File path cannot be empty.");
            return;
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            for (Task task : tasks) {
                String line = String.format("%d|%s|%s|%s%n",
                        task.getId(),
                        task.getDescription(),
                        task.getCategory().name(),
                        task.isCompleted());
                writer.write(line);
            }
            System.out.println("Tasks exported to " + filePath);
        } catch (IOException e) {
            System.out.println("Error exporting tasks to file.");
            e.printStackTrace();
        }
    }

    // Import tasks from TXT file
    public void importFromTxt(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("File path cannot be empty.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<Task> importedTasks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String description = parts[1];
                    Category category;
                    try {
                        category = Category.valueOf(parts[2]);
                    } catch (IllegalArgumentException ex) {
                        continue;
                    }
                    boolean isCompleted = Boolean.parseBoolean(parts[3]);
                    
                    Task task = createTaskByCategory(id, description, category);
                    task.setCompleted(isCompleted);
                    importedTasks.add(task);
                }
            }
            tasks.addAll(importedTasks);
            saveTasks();
            System.out.println("Tasks imported from " + filePath);
        } catch (IOException e) {
            System.out.println("Error importing tasks from file.");
            e.printStackTrace();
        }
    }

    // Helper method to create appropriate task by category
    private Task createTaskByCategory(int id, String description, Category category) {
        switch (category) {
            case PERSONAL:
                return new PersonalTask(id, description);
            case WORK:
                return new WorkTask(id, description);
            case HOBBY:
                return new HobbyTask(id, description);
            case EDUCATION:
                return new EducationTask(id, description);
            case OTHER:
            default:
                return new OtherTask(id, description);
        }
    }
}