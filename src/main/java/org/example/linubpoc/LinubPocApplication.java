package org.example.linubpoc;

import lombok.extern.slf4j.Slf4j;
import org.example.linubpoc.util.FontLoader;
import org.openpdf.text.*;
import org.openpdf.text.pdf.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class LinubPocApplication {
    private static final Path DIR = Paths.get("C:/junsu");

    public static void main(String[] args) {
        SpringApplication.run(LinubPocApplication.class, args);

        printString("한글 인코딩 테스트");
//        printTable();
//        printTwoParagraph();
    }

    private static void printString(String str) {
        Path filePath = DIR.resolve("test.pdf");

        BaseFont boldFont = FontLoader.getFont(true);
        BaseFont baseFont = FontLoader.getFont(false);
        Font n1 = new Font(baseFont, 12);
        Font n2 = new Font(baseFont, 12);
        Font f1 = new Font(baseFont, 12, Font.NORMAL);
        Font f2 = new Font(baseFont, 12, Font.BOLD);
        Font bf1 = new Font(boldFont, 12, Font.NORMAL);
        Font bf2 = new Font(boldFont, 12, Font.BOLD);


        if(filePath.toFile().exists()) {
            filePath.toFile().delete();
        }

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {

            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            document.add(new Paragraph(str, n1));
            document.add(new Paragraph(str, n2));
            document.add(new Paragraph(str, f1));
            document.add(new Paragraph(str, f2));
            document.add(new Paragraph(str, bf1));
            document.add(new Paragraph(str, bf2));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTable() {
        Path filePath = DIR.resolve("table_test.pdf");

        if(filePath.toFile().exists()) {
            filePath.toFile().delete();
            log.info("delete existing file >> {}", filePath.toFile().exists());
        }

        // step 1
        Document document = new Document(PageSize.A4);
        log.info("create new document");

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            log.info("set width & height to ${} ${}", width, height);

            // step 3
            document.open();
            log.info("document.open()");

            // step 4
            float[] columnDefinitionSize = {33.33F, 33.33F, 33.33F};

            float pos = height / 2;
            PdfPTable table = null;
            PdfPCell cell = null;

            table = new PdfPTable(columnDefinitionSize);
            table.getDefaultCell().setBorder(0);
            table.setHorizontalAlignment(0);
            table.setTotalWidth(width - 72);
            table.setLockedWidth(true);

            cell = new PdfPCell(new Phrase("Table added with document.add()"));
            cell.setColspan(columnDefinitionSize.length);
            table.addCell(cell);
            table.addCell(new Phrase("Louis Pasteur"));
            table.addCell(new Phrase("Albert Einstein"));
            table.addCell(new Phrase("Isaac Newton"));
            table.addCell(new Phrase("8, Rabic street"));
            table.addCell(new Phrase("2 Photons Avenue"));
            table.addCell(new Phrase("32 Gravitation Court"));
            table.addCell(new Phrase("39100 Dole France"));
            table.addCell(new Phrase("12345 Ulm Germany"));
            table.addCell(new Phrase("45789 Cambridge  England"));

            document.add(table);

            table = new PdfPTable(columnDefinitionSize);
            table.getDefaultCell().setBorder(0);
            table.setHorizontalAlignment(0);
            table.setTotalWidth(width - 72);
            table.setLockedWidth(true);

            cell = new PdfPCell(new Phrase("Table added with writeSelectedRows"));
            cell.setColspan(columnDefinitionSize.length);
            table.addCell(cell);
            table.addCell(new Phrase("Louis Pasteur"));
            table.addCell(new Phrase("Albert Einstein"));
            table.addCell(new Phrase("Isaac Newton"));
            table.addCell(new Phrase("8, Rabic street"));
            table.addCell(new Phrase("2 Photons Avenue"));
            table.addCell(new Phrase("32 Gravitation Court"));
            table.addCell(new Phrase("39100 Dole France"));
            table.addCell(new Phrase("12345 Ulm Germany"));
            table.addCell(new Phrase("45789 Cambridge  England"));

            table.writeSelectedRows(0, -1, 50, pos, writer.getDirectContent());

            document.close();
            log.info("document.close()");
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }
    }

    private static void printTwoParagraph() {
        Path filePath = DIR.resolve("two_paragraph.pdf");

        if(filePath.toFile().exists()) {
            filePath.toFile().delete();
            log.info("delete existing file");
        }

        BaseFont baseFont = FontLoader.getFont(true);
        Font font = new Font(baseFont, 12, Font.NORMAL);

        Document document = new Document(PageSize.A4);

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // 1. 페이지 크기 및 중앙 좌표 계산
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            float centerX = width / 2;

            // 2. [가운데 수직선 그리기]
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(1f);               // 선 두께 설정
            cb.setRGBColorStroke(128, 128, 128); // 선 색상 (회색)

            // 여백(Margin)을 고려하여 위에서 아래로 선을 그음 (위아래 30포인트 뗌)
            cb.moveTo(centerX, 30);
            cb.lineTo(centerX, height - 30);
            cb.stroke();

            // 3. [2단 텍스트 레이아웃 설정]
            ColumnText ct = new ColumnText(cb);

            // 여백 및 간격 정의
            float margin = 40;       // 문서 바깥쪽 여백
            float gap = 20;          // 가운데 선과 텍스트 사이의 간격

            // 왼쪽 단 영역 (LLX, LLY, URX, URY)
            float left_llx = margin;
            float left_lly = margin;
            float left_urx = centerX - gap;
            float left_ury = height - margin;

            // 오른쪽 단 영역 (LLX, LLY, URX, URY)
            float right_llx = centerX + gap;
            float right_lly = margin;
            float right_urx = width - margin;
            float right_ury = height - margin;

            // 4. 시험지 내용 예시 추가 (질문 및 보기)
            for (int i = 1; i <= 10; i++) {
                ct.addElement(new Paragraph("\n[Q. " + i + "] 한글 인코딩 테스트"));
                ct.addElement(new Paragraph("① multiple 1"));
                ct.addElement(new Paragraph("② multiple 2"));
                ct.addElement(new Paragraph("③ multiple 3"));
                ct.addElement(new Paragraph("④ multiple 4"));
            }

            // 5. 텍스트를 단에 채워 넣기 (왼쪽 단 -> 다 차면 오른쪽 단)
            int currentColumn = 0; // 0: 왼쪽, 1: 오른쪽
            ct.setSimpleColumn(left_llx, left_lly, left_urx, left_ury);

            while (true) {
                // ColumnText.go()는 단이 꽉 차면 멈추고 특수 비트를 반환합니다.
                int status = ct.go();

                // 더 이상 배치할 텍스트가 없으면 종료
                if ((status & ColumnText.NO_MORE_TEXT) != 0) {
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

            document.add(new Paragraph("한글 인코딩 테스트", font));

            document.close();
            System.out.println("시험지 형식의 2단 PDF가 생성되었습니다!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
