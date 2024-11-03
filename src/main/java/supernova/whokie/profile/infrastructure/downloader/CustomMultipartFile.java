package supernova.whokie.profile.infrastructure.downloader;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CustomMultipartFile implements MultipartFile {
    private final String name;
    private final byte[] content;
    private final String contentType;

    public CustomMultipartFile(String name, byte[] content, String contentType) {
        this.name = name;
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return name; // 원래 파일 이름
    }

    @Override
    public String getContentType() {
        return contentType; // 실제 콘텐츠 타입
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("Transfer to file is not supported.");
    }
}