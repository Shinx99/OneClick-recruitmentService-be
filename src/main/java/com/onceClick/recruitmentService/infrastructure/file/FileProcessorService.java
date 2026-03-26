package com.onceClick.recruitmentService.infrastructure.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
public class FileProcessorService implements FileTextExtractor{

    public String extractText(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);

        log.info("=== FileProcessorService.extractTextFromFile ===");
        log.info("File: {}, Type: {}, Size: {} bytes",
                fileName, fileType, file.getSize());
        log.info("Content type: {}", file.getContentType());

        switch (fileType.toLowerCase()) {
            case "pdf":
                log.info("Processing as PDF...");
                return extractTextFromPDF(file);
            case "docx":
                log.info("Processing as DOCX...");
                return extractTextFromDOCX(file);
            case "doc":
                log.info("Processing as DOC...");
                return extractTextFromDOC(file);
            case "txt":
            default:
                log.info("Processing as TEXT...");
                return new String(file.getBytes(), "UTF-8");
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws Exception {
        log.info("=== extractTextFromPDF ===");

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            log.info("PDF loaded successfully");
            log.info("Number of pages: {}", document.getNumberOfPages());
            log.info("Is encrypted: {}", document.isEncrypted());

            if (document.isEncrypted()) {
                log.warn("PDF is encrypted, may not extract text properly");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);
            log.info("PDF text extracted: {} characters", text.length());

            if (text.length() == 0) {
                log.warn("No text extracted from PDF!");
                log.warn("This might be a scanned PDF (image-based)");
                return "PDF appears to be scanned or image-based. No text could be extracted.";
            }

            // Log first 200 characters for debugging
            String preview = text.substring(0, Math.min(200, text.length()));
            log.info("First 200 chars: {}", preview);

            return text;
        } catch (Exception e) {
            log.error("Error extracting text from PDF: ", e);
            throw new Exception("Failed to extract text from PDF: " + e.getMessage());
        }
    }

    private String extractTextFromDOCX(MultipartFile file) throws Exception {
        log.info("=== extractTextFromDOCX ===");

        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

            String text = extractor.getText();
            log.info("DOCX text extracted: {} characters", text.length());

            if (text.length() == 0) {
                log.warn("No text extracted from DOCX!");
            }

            return text;
        } catch (Exception e) {
            log.error("Error extracting text from DOCX: ", e);
            throw new Exception("Failed to extract text from DOCX: " + e.getMessage());
        }
    }


    private String extractTextFromDOC(MultipartFile file) throws Exception {
        log.info("=== extractTextFromDOC ===");
        log.warn(".doc format has limited support. Consider converting to .docx or .pdf");

        return "File .doc detected. Please convert to .docx or .pdf for better results.\n" +
                "Uploaded file name: " + file.getOriginalFilename();
    }

    @Override
    public String extractTextFromS3(String s3Url) throws Exception {
        throw new UnsupportedOperationException(
                "FileProcessorService không hỗ trợ S3 URL. Dùng S3FileProcessorService");
    }

    @Override
    public String getFileType(String fileName) {
        if (fileName == null) {
            log.warn("File name is null");
            return "txt";
        }

        String lowerName = fileName.toLowerCase();
        log.info("Detecting file type for: {}", lowerName);

        if (lowerName.endsWith(".pdf")) return "pdf";
        if (lowerName.endsWith(".docx")) return "docx";
        if (lowerName.endsWith(".doc")) return "doc";
        if (lowerName.endsWith(".txt")) return "txt";

        log.warn("Unknown file type: {}", fileName);
        return "txt";
    }

    @Override
    public boolean isSupportedFile(String fileName) {
        String fileType = getFileType(fileName);
        boolean supported = fileType.equals("pdf") || fileType.equals("docx") ||
                fileType.equals("doc") || fileType.equals("txt");

        log.info("File {} is supported: {}", fileName, supported);
        return supported;
    }




}