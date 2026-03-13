import java.io.Serializable;

public abstract class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String description;
    protected Category category;
    protected boolean isCompleted;

    public Task(int id, String description, Category category) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    @Override
    public String toString() {
        String status = isCompleted ? "✓ DONE" : "○ ACTIVE";
        return String.format("[%d] %-20s | %s", id, description, status);
    }
}