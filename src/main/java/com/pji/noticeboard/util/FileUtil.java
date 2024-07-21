package com.pji.noticeboard.util;

import com.pji.noticeboard.exception.ErrorCode;
import com.pji.noticeboard.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileUtil {

    @Value("${file.upload.base-path}")
    private String basePath;

    /**
     * 파일 목록을 처리하고 파일 시스템에 저장합니다.
     *
     * @param files 처리할 MultipartFile 객체의 목록
     * @param title 로깅을 위한 공지사항 제목
     * @return 저장된 파일 경로의 목록
     */
    public List<String> processFiles(List<MultipartFile> files, String title) {
        return files.stream()
                .map(file -> {
                    try {
                        return saveFile(file);
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid file provided for notice with TITLE {}. Reason: {}", title, e.getMessage());
                        throw new ServiceException(String.format("Invalid file provided for notice with TITLE %s. Reason: %s", title, e.getMessage()), ErrorCode.INVALID_FILE_PROVIDED, e);
                    } catch (Exception e) {
                        log.error("Failed to saveFile with TITLE {}", title, e);
                        throw new ServiceException(String.format("Failed to saveFile with TITLE %s", title), ErrorCode.SAVE_FILE_FAILED, e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 파일 시스템에 파일을 저장합니다.
     *
     * @param file 저장할 MultipartFile 객체
     * @return 파일이 저장된 경로
     * @throws IOException I/O 오류가 발생한 경우
     */
    private  String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !FileExtension.isValid(getFileExtension(originalFilename))) {
            throw new IllegalArgumentException("Invalid file type");
        }

        LocalDateTime now = LocalDateTime.now();
        String dateFolder = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        File uploadDir = new File(basePath, dateFolder);

        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + uploadDir.getAbsolutePath());
        }

        String fileName = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + originalFilename;
        File destinationFile = new File(uploadDir, fileName);

        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new IOException("Failed to save file", e);
        }

        return Paths.get(dateFolder, fileName).toString();
    }

    /**
     * 파일의 확장자를 가져옵니다.
     *
     * @param filename 파일 이름
     * @return 파일 확장자
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
