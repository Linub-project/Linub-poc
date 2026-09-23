package org.example.linubpoc;

import org.example.linubpoc.practice.PdfTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LinubPocApplicationTests {

    @Test
    void contextLoads() {
        PdfTest pdfTest = new PdfTest();
    }

}
