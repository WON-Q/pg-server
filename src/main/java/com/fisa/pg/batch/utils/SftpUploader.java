package com.fisa.pg.batch.utils;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.xfer.LocalSourceFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.Collections;
import java.util.List;

@Component
public class SftpUploader {

    @Value("${batch.sftp.host}")
    private String sftpHost;

    @Value("${batch.sftp.port:22}")
    private int sftpPort;

    @Value("${batch.sftp.username}")
    private String sftpUsername;

    @Value("${batch.sftp.password}")
    private String sftpPassword;

    @Value("${batch.sftp.remote-dir}")
    private String sftpRemoteDir;

    public void upload(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("전송할 파일이 존재하지 않습니다: " + file);
        }

        try (SSHClient ssh = new SSHClient()) {

            ssh.addHostKeyVerifier(new HostKeyVerifier() {
                @Override
                public boolean verify(String hostname, int port, PublicKey key) {
                    return true; // 실 서비스에서는 known_hosts 기반 검증을 해야 함
                }

                @Override
                public List<String> findExistingAlgorithms(String hostname, int port) {
                    // 안전하게 비워진 리스트 반환
                    return Collections.emptyList();
                }
            });

            ssh.connect(sftpHost, sftpPort);
            ssh.authPassword(sftpUsername, sftpPassword);

            try (SFTPClient sftp = ssh.newSFTPClient()) {

                LocalSourceFile sourceFile = new LocalSourceFile() {
                    @Override
                    public String getName() {
                        return file.getName();
                    }

                    @Override
                    public long getLength() {
                        return file.length();
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new FileInputStream(file); // 이 시점에 스트림 열기
                    }

                    @Override
                    public boolean isFile() {
                        return true;
                    }
                };

                sftp.put(sourceFile, sftpRemoteDir + "/");
            }

        } catch (IOException e) {
            throw new RuntimeException("SFTP 전송 실패: " + file.getName(), e);
        }
    }
}
