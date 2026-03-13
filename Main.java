import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nTo-Do List Application");
            System.out.println("1. Add Task");
            System.out.println("2. Mark Task as Completed");
            System.out.println("3. Update Task");
            System.out.println("4. Delete Task");
            System.out.println("5. List Tasks");
            System.out.println("6. Export Tasks to TXT");
            System.out.println("7. Import Tasks from TXT");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    markTaskAsCompleted();
                    break;
                case 3:
                    updateTask();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    taskManager.listTasks();
                    break;
                case 6:
                    exportTasks();
                    break;
                case 7:
                    importTasks();
                    break;
                case 8:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please select again.");
                    break;
            }
        }
    }

    private static void addTask() {
        System.out.println("Adding a new task:");
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        Category category = readCategoryFromInput();
        
        Task task = createTaskByCategory(taskManager.getNextId(), description, category);
        taskManager.addTask(task);
        System.out.println("Task added successfully.");
    }

    private static void updateTask() {
        System.out.println("Updating an existing task:");
        System.out.print("Enter task ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new description: ");
        String description = scanner.nextLine();
        Category category = readCategoryFromInput();

        boolean updated = taskManager.updateTask(id, description, category);
        if (updated) {
            System.out.println("Task updated successfully.");
        } else {
            System.out.println("Task not found.");
        }
    }

    private static void markTaskAsCompleted() {
        System.out.println("Marking a task as completed:");
        System.out.print("Enter task ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean updated = taskManager.markTaskAsCompleted(id);
        if (updated) {
            System.out.println("Task marked as completed.");
        } else {
            System.out.println("Task not found.");
        }
    }


    private static void deleteTask() {
        System.out.println("Deleting an existing task:");
        System.out.println("1. Delete active task");
        System.out.println("2. Delete completed task");
        System.out.print("Choose delete type: ");
        int deleteChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter task ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean deleted;
        if (deleteChoice == 1) {
            deleted = taskManager.deleteActiveTask(id);
        } else if (deleteChoice == 2) {
            deleted = taskManager.deleteCompletedTask(id);
        } else {
            System.out.println("Invalid delete type.");
            return;
        }

        if (deleted) {
            System.out.println("Task deleted successfully.");
        } else {
            System.out.println("Task not found in selected group.");
        }
    }

    private static void exportTasks() {
        System.out.print("Enter file path to export (e.g., tasks.txt): ");
        String filePath = scanner.nextLine();
        taskManager.exportToTxt(filePath);
    }

    private static void importTasks() {
        System.out.print("Enter file path to import (e.g., tasks.txt): ");
        String filePath = scanner.nextLine();
        taskManager.importFromTxt(filePath);
    }

    private static Task createTaskByCategory(int id, String description, Category category) {
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

    private static Category readCategoryFromInput() {
        while (true) {
            System.out.print("Enter category (1-PERSONAL, 2-WORK, 3-HOBBY, 4-EDUCATION, 5-OTHER or name): ");
            String rawInput = scanner.nextLine().trim();

            if (rawInput.equals("1")) return Category.PERSONAL;
            if (rawInput.equals("2")) return Category.WORK;
            if (rawInput.equals("3")) return Category.HOBBY;
            if (rawInput.equals("4")) return Category.EDUCATION;
            if (rawInput.equals("5")) return Category.OTHER;

            try {
                return Category.valueOf(rawInput.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category. Allowed: PERSONAL, WORK, HOBBY, EDUCATION, OTHER (or 1-5).");
            }
        }
    }
}