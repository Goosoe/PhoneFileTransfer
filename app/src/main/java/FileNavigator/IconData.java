package FileNavigator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class IconData {
    public final String id;
    public final String content;
    public final String description;

    public IconData(String id, String content, String description) {
        this.id = id;
        this.content = content;
        this.description = description;
    }

    @Override
    public String toString() {
        return content;
    }
}
