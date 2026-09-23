package org.example.linubpoc.practice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfTest {
    Path dir = Paths.get("c:/junsu/file");
    static final String FONT_PATH = "src/main/resources/fonts/nanum.TTF";
    static final String BOLD_FONT_PATH = "src/main/resources/fonts/nanum_bold.TTF";

    @Test
    void createPdf() {
        Path path = dir.resolve("create_pdf.pdf");

        Document document = new Document(PageSize.A4);

        try (OutputStream os = Files.newOutputStream(path)) {
            PdfWriter instance = PdfWriter.getInstance(document, os);

            document.open();
            document.add(new Paragraph("Hello World!"));
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void divideParagraph() {
        Path path = dir.resolve("divide_paragraph.pdf");

        if(Files.exists(path)) {
            Assertions.assertTrue(path.toFile().delete());
        }

        BaseFont baseFont = null;
        BaseFont boldFont = null;
        try {
            baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            boldFont = BaseFont.createFont(BOLD_FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Font f = new Font(baseFont, 12);
        Font bf = new Font(boldFont, 11);

        Document document = new Document(PageSize.A4);

        try (OutputStream os = Files.newOutputStream(path)) {
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();

            // 1. 페이지 크기 및 중앙 좌표 계산
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            float centerX = width / 2;

            // center line setting
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(1f);               // 선 두께 설정
            cb.setRGBColorStroke(128, 128, 128); // 선 색상 (회색)

            // stroke center
            cb.moveTo(centerX, 20);
            cb.lineTo(centerX, height - 20);
            cb.stroke();

            // 3. [2단 텍스트 레이아웃 설정]
            ColumnText ct = new ColumnText(cb);

            // 여백 및 간격 정의
            float margin = 40;       // 문서 바깥쪽 여백
            float gap = 20;          // 가운데 선과 텍스트 사이의 간격

            // 왼쪽 단 영역 (LLX, LLY, URX, URY)
            float left_llx = margin; // lower left x
            float left_lly = margin; // lower left y
            float left_urx = centerX - gap; // upper right x
            float left_ury = height - margin; // upper right y

            // 오른쪽 단 영역 (LLX, LLY, URX, URY)
            float right_llx = centerX + gap;
            float right_lly = margin;
            float right_urx = width - margin;
            float right_ury = height - margin;

            // 4. 시험지 내용 예시 추가 (질문 및 보기)
            for (int i = 1; i <= 10; i++) {
                ct.addElement(new Paragraph("\n[Q. " + i + "] 한글 인코딩 테스트", bf));
                ct.addElement(new Paragraph("① multiple 1", f));
                ct.addElement(new Paragraph("② multiple 2", f));
                ct.addElement(new Paragraph("③ multiple 3", f));
                ct.addElement(new Paragraph("④ multiple 4", f));
                for(int j = 0;  j < 2; j++) {
                    ct.addElement(new Paragraph("\n"));
                }
            }

            // 5. 텍스트를 단에 채워 넣기 (왼쪽 단 -> 다 차면 오른쪽 단)
            int currentColumn = 0; // 0: 왼쪽, 1: 오른쪽
            ct.setSimpleColumn(left_llx, left_lly, left_urx, left_ury);

            while (true) {
                // ColumnText.go()는 단이 꽉 차면 멈추고 특수 비트를 반환합니다.
                // 더 이상 배치할 텍스트가 없으면 종료
                if ((ct.go() & ColumnText.NO_MORE_TEXT) != 0) {
                    break;
                }

                // 단이 다 찼을 때 (NO_MORE_COLUMN)
                if (currentColumn == 0) {
                    // 왼쪽 단이 다 찼으므로 오른쪽 단으로 영역 변경
                    currentColumn = 1;
                    ct.setSimpleColumn(right_llx, right_lly, right_urx, right_ury);
                } else {
                    // 오른쪽 단까지 다 찼으므로 새 페이지 생성
                    document.newPage();

                    // 새 페이지에도 수직선을 다시 그려줌
                    cb.setLineWidth(1f);
                    cb.setRGBColorStroke(128, 128, 128);
                    cb.moveTo(centerX, 30);
                    cb.lineTo(centerX, height - 30);
                    cb.stroke();

                    // 다시 왼쪽 단부터 시작
                    currentColumn = 0;
                    ct.setSimpleColumn(left_llx, left_lly, left_urx, left_ury);
                }
            }

            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
