package org.example.linubpoc.service;

import lombok.Getter;

import java.io.InputStream;
import java.io.OutputStream;

@Getter
public class TerminalSession {

    private final Process process;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TerminalSession(Process process) {
        this.process = process;
        this.inputStream = process.getInputStream();
        this.outputStream = process.getOutputStream();
    }

    public void close() {
        process.destroy();
    }
}
