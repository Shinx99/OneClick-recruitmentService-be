package com.onceClick.recruitmentService.infrastructure.seeder;


import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public class InputStreamMultipartFile implements MultipartFile{

    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public InputStreamMultipartFile(String filename, String contentType, InputStream inputStream)
            throws IOException {
        this.name             = "resumeFile";
        this.originalFilename = filename;
        this.contentType      = contentType;
        this.content          = inputStream.readAllBytes();
    }

    @Override public String getName()             { return name; }
    @Override public String getOriginalFilename() { return originalFilename; }
    @Override public String getContentType()      { return contentType; }
    @Override public boolean isEmpty()            { return content.length == 0; }
    @Override public long getSize()               { return content.length; }
    @Override public byte[] getBytes()            { return content; }
    @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }

    @Override
    public void transferTo(File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }

}
