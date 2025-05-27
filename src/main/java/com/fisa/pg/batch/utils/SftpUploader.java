package com.fisa.pg.batch.utils;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.LocalFileFilter;
import net.schmizz.sshj.xfer.LocalSourceFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

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

            // 모든 호스트 키를 허용하는 검증기 (운영 시엔 보안상 적절한 HostKeyVerifier로 대체 필요)
            ssh.addHostKeyVerifier(new PromiscuousVerifier());

            ssh.connect(sftpHost, sftpPort);
            ssh.authPassword(sftpUsername, sftpPassword);

            try (SFTPClient sftp = ssh.newSFTPClient()) {
                sftp.put(new LocalSourceFile() {
                    @Override
                    public String getName() {
                        return file.getName();
                    }

                    @Override
                    public long getLength() {
                        return file.length();
                    }

                    @Override
                    public InputStream getInputStream() {
                        try {
                            return new FileInputStream(file);
                        } catch (IOException e) {
                            throw new UncheckedIOException("파일 스트림 생성 실패", e);
                        }
                    }

                    @Override
                    public boolean isFile() {
                        return true;
                    }

                    @Override
                    public boolean isDirectory() {
                        return false;
                    }

                    @Override
                    public Iterable<? extends LocalSourceFile> getChildren(LocalFileFilter localFileFilter) throws IOException {
                        return null;
                    }

                    @Override
                    public boolean providesAtimeMtime() {
                        return false;
                    }

                    @Override
                    public long getLastAccessTime() throws IOException {
                        return 0;
                    }

                    @Override
                    public long getLastModifiedTime() throws IOException {
                        return 0;
                    }

                    @Override
                    public int getPermissions() {
                        return 0644; // rw-r--r--
                    }
                }, sftpRemoteDir + "/");
            }

        } catch (IOException e) {
            throw new RuntimeException("SFTP 전송 실패: " + file.getName(), e);
        }
    }
}
