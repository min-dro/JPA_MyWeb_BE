package com.kh.myweb.common.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

class FileRenamePolicyTest {

    @Test
    void saveFile_shouldWriteUploadedFileAndReturnRenamedFileName() throws IOException {
        File tempDir = Files.createTempDirectory("file-rename-policy").toFile();
        tempDir.deleteOnExit();

        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getRealPath("/board_upfiles/")).thenReturn(tempDir.getAbsolutePath() + File.separator);

        HttpSession session = mock(HttpSession.class);
        when(session.getServletContext()).thenReturn(servletContext);

        MultipartFile file = new MockMultipartFile("file", "sample.txt", "text/plain", "hello".getBytes());

        String fileName = FileRenamePolicy.saveFile(file, session, "/board_upfiles/");

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".txt"));
        assertTrue(new File(tempDir, fileName).exists());
        assertEquals("sample.txt", file.getOriginalFilename());
    }
}
