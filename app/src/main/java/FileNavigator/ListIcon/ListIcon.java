package FileNavigator.ListIcon;

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
public class ListIcon {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<IconData> ITEMS = new ArrayList<IconData>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, IconData> ITEM_MAP = new HashMap<String, IconData>();

//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createListIcon(i));
//        }
//    }
//
//    private static void addItem(IconData item) {
//        ITEMS.add(item);
//        ITEM_MAP.put(item.id, item);
//    }
//
//    private static IconData createListIcon(int position) {
//        return new IconData(String.valueOf(position), "Item " + position, );
//    }

//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore details information here.");
//        }
//        return builder.toString();
//    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class IconData {
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
}