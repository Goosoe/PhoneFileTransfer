package FileNavigator;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class IconData {
    public final String id;
    public final String content;
    public final String filePath;

    public IconData(String id, String content, String filePath) {
        this.id = id;
        this.content = content;
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return content;
    }
}
