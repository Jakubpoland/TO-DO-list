public class HobbyTask extends Task {
    private static final long serialVersionUID = 1L;

    public HobbyTask(int id, String description) {
        super(id, description, Category.HOBBY);
    }

    @Override
    public String toString() {
        return "[HOBBY] " + super.toString();
    }
}
