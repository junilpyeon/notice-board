package com.pji.noticeboard.util;

import java.util.Arrays;

public enum FileExtension {
    // 이미지 파일 확장자
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif"),
    BMP("bmp"),
    WEBP("webp"),

    // 문서 파일 확장자
    PDF("pdf"),
    DOC("doc"),
    DOCX("docx"),
    XLS("xls"),
    XLSX("xlsx"),
    PPT("ppt"),
    PPTX("pptx"),
    TXT("txt"),

    // 압축 파일 확장자
    ZIP("zip"),
    RAR("rar"),
    TAR("tar"),
    GZ("gz"),
    SEVEN_Z("7z");


    private final String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isValid(String extension) {
        return Arrays.stream(values())
                .anyMatch(e -> e.getExtension().equalsIgnoreCase(extension));
    }
}
