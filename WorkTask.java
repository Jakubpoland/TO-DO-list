public class WorkTask extends Task {
    private static final long serialVersionUID = 1L;

    public WorkTask(int id, String description) {
        super(id, description, Category.WORK);
    }

    @Override
    public String toString() {
        return "[WORK] " + super.toString();
    }
}
