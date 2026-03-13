public class PersonalTask extends Task {
    private static final long serialVersionUID = 1L;

    public PersonalTask(int id, String description) {
        super(id, description, Category.PERSONAL);
    }

    @Override
    public String toString() {
        return "[PERSONAL] " + super.toString();
    }
}
