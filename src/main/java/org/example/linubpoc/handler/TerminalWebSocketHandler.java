package org.example.linubpoc.handler;

import lombok.RequiredArgsConstructor;
import org.example.linubpoc.service.DockerService;
import org.example.linubpoc.service.TerminalSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TerminalWebSocketHandler extends TextWebSocketHandler {

    private final DockerService dockerService;

    private final Map<String, TerminalSession> terminalSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) throws Exception {

        String containerName = "linub-poc";

        Process process = dockerService.startShell(containerName);

        TerminalSession terminalSession =
                new TerminalSession(process);

        terminalSessions.put(
                session.getId(),
                terminalSession
        );

        startOutputReader(
                session,
                terminalSession
        );
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) throws Exception {

        TerminalSession terminalSession =
                terminalSessions.get(session.getId());

        if (terminalSession == null) {
            return;
        }

        String input = message.getPayload();

        terminalSession
                .getOutputStream()
                .write(
                        input.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        terminalSession
                .getOutputStream()
                .flush();
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) throws Exception {

        TerminalSession terminalSession =
                terminalSessions.remove(session.getId());

        if (terminalSession != null) {
            terminalSession.close();
        }
    }

    private void startOutputReader(
            WebSocketSession webSocketSession,
            TerminalSession terminalSession
    ) {

        Thread thread = new Thread(() -> {

            try {
                byte[] buffer = new byte[1024];

                int length;

                while (
                        (length =
                                terminalSession
                                        .getInputStream()
                                        .read(buffer)
                        ) != -1
                ) {

                    String output =
                            new String(
                                    buffer,
                                    0,
                                    length,
                                    StandardCharsets.UTF_8
                            );

                    if (webSocketSession.isOpen()) {
                        synchronized (webSocketSession) {
                            webSocketSession.sendMessage(
                                    new TextMessage(output)
                            );
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}