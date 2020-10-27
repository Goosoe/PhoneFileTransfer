package FileNavigator;

import java.io.Serializable;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class ListElementData implements Serializable {
    public final String id;
    public final String fileName;
    public final String filePath;

    public ListElementData(String id, String fileName, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
    }


}
