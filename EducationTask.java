public class EducationTask extends Task {
    private static final long serialVersionUID = 1L;

    public EducationTask(int id, String description) {
        super(id, description, Category.EDUCATION);
    }

    @Override
    public String toString() {
        return "[EDUCATION] " + super.toString();
    }
}
