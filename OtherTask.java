public class OtherTask extends Task {
    private static final long serialVersionUID = 1L;

    public OtherTask(int id, String description) {
        super(id, description, Category.OTHER);
    }

    @Override
    public String toString() {
        return "[OTHER] " + super.toString();
    }
}
