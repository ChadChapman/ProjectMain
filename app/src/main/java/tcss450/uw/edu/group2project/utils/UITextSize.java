package tcss450.uw.edu.group2project.utils;

import tcss450.uw.edu.group2project.R;

public class UITextSize {
    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_LARGE = 3;

    public static int getSizeId(int size) {
        int textSizeId = 0;

        switch (size) {
            case SIZE_SMALL:
                textSizeId = R.style.TextSizeSmall;
                break;
            case SIZE_MEDIUM:
                textSizeId = R.style.TextSizeMedium;
                break;
            case SIZE_LARGE:
                textSizeId = R.style.TextSizeLarge;
                break;
        }

        return textSizeId;

    }
}
