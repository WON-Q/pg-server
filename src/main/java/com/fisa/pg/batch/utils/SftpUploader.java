package com.fisa.pg.batch.utils;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

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
            throw new IllegalArgumentException(">>>>>>>>>>>>>[Error]: 전송할 파일이 존재하지 않습니다: " + file);
        }

        try {
            sftpRetryTemplate.execute(context -> {
                // 기존 로직
                try (SSHClient ssh = new SSHClient()) {
                    ssh.addHostKeyVerifier(new PromiscuousVerifier());
                    ssh.connect(sftpHost, sftpPort);
                    ssh.authPassword(sftpUsername, sftpPassword);

                    try (SFTPClient sftp = ssh.newSFTPClient()) {
                        sftp.put(new FileSystemFile(file), sftpRemoteDir + "/");
                        log.info(">>>>>>>>>>>>>>SFTP 전송 성공: {}", file.getName());
                    }
                } catch (IOException e) {
                    log.warn(">>>>>>>>>>>>>>SFTP 전송 재시도 중 오류 발생: {}", e.getMessage());
                    throw e;
                }
                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException(">>>>>>>>>>>>>>SFTP 최종 실패: " + file.getName(), e);
        }
    }
}
