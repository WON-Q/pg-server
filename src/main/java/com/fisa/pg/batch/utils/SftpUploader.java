package com.fisa.pg.batch.utils;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

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
            ssh.addHostKeyVerifier((hostname, port, key) -> true); // TODO: 실 운영 시엔 known_hosts 적용
            ssh.connect(sftpHost, sftpPort);
            ssh.authPassword(sftpUsername, sftpPassword);

            try (SFTPClient sftp = ssh.newSFTPClient();
                 FileInputStream fis = new FileInputStream(file)) {

                sftp.put(fis, sftpRemoteDir + "/" + file.getName());
            }

        } catch (IOException e) {
            throw new RuntimeException("SFTP 전송 실패: " + file.getName(), e);
        }
    }
}