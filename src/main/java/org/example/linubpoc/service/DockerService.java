package org.example.linubpoc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DockerService {

    public Process startShell(String containerName) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker",
                "exec",
                "-i",
                containerName,
                "bash"
        );

        processBuilder.redirectErrorStream(true);

        return processBuilder.start();
    }
}