package tcss450.uw.edu.group2project.utils;

import tcss450.uw.edu.group2project.R;

public class UITheme {
    public static final int THEME_ONE = 1;
    public static final int THEME_TWO = 2;
    public static final int THEME_THREE = 3;
    public static final int THEME_FOUR = 4;

    public static int getThemeId(int theme) {
        int themeId = 0;

        switch (theme) {
            case THEME_ONE:
                themeId = R.style.AppTheme;
                break;
            case THEME_TWO:
                themeId = R.style.AppTheme2;
                break;
            case THEME_THREE:
                themeId = R.style.AppTheme3;
                break;
            case THEME_FOUR:
                themeId = R.style.AppTheme4;
                break;
        }

        return themeId;

    }
}
