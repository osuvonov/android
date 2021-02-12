package org.telegram.irooms;

public class RoomsTheme {
    private boolean isDark = false;
    private static RoomsTheme instance;

    public static RoomsTheme getInstance() {
        if (instance == null) {
            instance = new RoomsTheme();
        }
        return instance;
    }

    public void setTheme(boolean toDark) {
        isDark = toDark;
    }






}
