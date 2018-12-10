package rodrigodavy.com.github.pixelartist;

public abstract class DrawerMenuItem {
    private int iconId;
    private int stringId;

    DrawerMenuItem(int id, int string) {
        iconId = id;
        stringId = string;
    }

    public abstract void execute();

    int getIconId() {
        return iconId;
    }

    int getStringId() {
        return stringId;
    }
}
