package org.example.linubpoc.util;

import org.openpdf.text.pdf.BaseFont;

import java.io.IOException;

public class FontLoader {
    private static final String FONT_PATH = "src/main/resources/fonts/nanum.TTF";
    private static final String BOLD_FONT_PATH = "src/main/resources/fonts/nanum_bold.TTF";


    public static BaseFont getFont(boolean bold) {
        try {
            return BaseFont.createFont(bold ? BOLD_FONT_PATH : FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            throw new RuntimeException(e);
        }
    }
}
