package com.fisa.pg.batch.utils;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.LocalFileFilter;
import net.schmizz.sshj.xfer.LocalSourceFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
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

    private final RetryTemplate sftpRetryTemplate;

    public SftpUploader(RetryTemplate sftpRetryTemplate) {
        this.sftpRetryTemplate = sftpRetryTemplate;
    }

    public void upload(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("전송할 파일이 존재하지 않습니다: " + file);
        }

        try {
            sftpRetryTemplate.execute((RetryCallback<Void, IOException>) context -> {
                try (SSHClient ssh = new SSHClient()) {
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
                            public InputStream getInputStream() throws IOException {
                                return new FileInputStream(file);
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
                            public Iterable<? extends LocalSourceFile> getChildren(LocalFileFilter localFileFilter) {
                                return null;
                            }

                            @Override
                            public boolean providesAtimeMtime() {
                                return false;
                            }

                            @Override
                            public long getLastAccessTime() {
                                return 0;
                            }

                            @Override
                            public long getLastModifiedTime() {
                                return 0;
                            }

                            @Override
                            public int getPermissions() {
                                return 0644;
                            }
                        }, sftpRemoteDir + "/");

                        log.info(">>>>>>>>>>>SFTP 전송 성공: {}", file.getName());
                    }
                } catch (IOException e) {
                    log.warn(">>>>>>>>>>>SFTP 전송 재시도 중 오류 발생: {}", e.getMessage());
                    throw e;
                }

                return null;
            });
        } catch (IOException finalEx) {
            throw new RuntimeException(">>>>>>>>>>>SFTP 최종 실패: " + file.getName(), finalEx);
        }
    }
}
