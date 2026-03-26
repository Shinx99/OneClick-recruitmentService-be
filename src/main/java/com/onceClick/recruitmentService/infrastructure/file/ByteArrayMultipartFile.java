package com.onceClick.recruitmentService.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public class ByteArrayMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final boolean empty;

    public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.content = content;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.empty = content == null || content.length == 0;
    }

    @Override public String getName() { return name; }
    @Override public String getOriginalFilename() { return originalFilename; }
    @Override public String getContentType() { return contentType; }
    @Override public boolean isEmpty() { return empty; }
    @Override public long getSize() { return content.length; }
    @Override public byte[] getBytes() throws IOException { return content; }
    @Override public InputStream getInputStream() throws IOException { 
        return new ByteArrayInputStream(content); 
    }
    @Override public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}