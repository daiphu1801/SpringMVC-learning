package com.examp.springmvc.shared.presentation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageFileValidatorTest {

    @Mock
    private MultipartFile file;

    private void setupFile(String name, String contentType, long size) {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(name);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getSize()).thenReturn(size);
    }

    // ── Happy-path tests ──────────────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(strings = {"photo.jpg", "img.jpeg", "banner.png", "anim.gif", "product.webp", "modern.avif"})
    @DisplayName("Should accept all valid image extensions")
    void shouldAcceptValidImages(String filename) {
        String mime = filename.endsWith("gif")
                ? "image/gif"
                : filename.endsWith("png")
                        ? "image/png"
                        : filename.endsWith("webp")
                                ? "image/webp"
                                : filename.endsWith("avif") ? "image/avif" : "image/jpeg";
        setupFile(filename, mime, 1024);
        assertDoesNotThrow(() -> ImageFileValidator.validate(file));
    }

    // ── Blocked extension tests ───────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(
            strings = {
                "virus.exe",
                "shell.sh",
                "page.html",
                "script.js",
                "hack.php",
                "archive.zip",
                "data.pdf",
                "config.xml"
            })
    @DisplayName("Should reject dangerous / non-image extensions")
    void shouldRejectDangerousExtensions(String filename) {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(filename);
        when(file.getSize()).thenReturn(1024L);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("không được phép"));
    }

    // ── Blocked MIME type tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should reject mismatched MIME type (extension ok, MIME bad)")
    void shouldRejectMismatchedMimeType() {
        // filename looks like an image but MIME type is application/octet-stream
        setupFile("photo.jpg", "application/octet-stream", 1024);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("không được phép"));
    }

    @Test
    @DisplayName("Should reject null / empty MIME type")
    void shouldRejectNullMimeType() {
        setupFile("photo.jpg", null, 1024);

        assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
    }

    // ── File size tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should reject file exceeding 5 MB limit")
    void shouldRejectOversizedFile() {
        long sixMb = 6L * 1024 * 1024;
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(sixMb);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("kích thước"));
    }

    @Test
    @DisplayName("Should accept file exactly at 5 MB limit")
    void shouldAcceptFileSizeAtLimit() {
        long fiveMb = 5L * 1024 * 1024;
        setupFile("photo.jpg", "image/jpeg", fiveMb);
        assertDoesNotThrow(() -> ImageFileValidator.validate(file));
    }

    // ── Empty / null file tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should reject empty file")
    void shouldRejectEmptyFile() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
    }

    @Test
    @DisplayName("Should reject null file")
    void shouldRejectNullFile() {
        assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(null));
    }

    // ── Edge case: file has no extension ─────────────────────────────────────

    @Test
    @DisplayName("Should reject file with no extension")
    void shouldRejectFileWithNoExtension() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("imagewithoutextension");
        when(file.getSize()).thenReturn(1024L);

        assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
    }

    @Test
    @DisplayName("Should escape HTML in filename extension to prevent XSS")
    void shouldEscapeHtmlInFilenameExtension() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("photo.<script>alert(1)</script>");
        when(file.getSize()).thenReturn(1024L);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
    }

    @Test
    @DisplayName("Should escape HTML in content type to prevent XSS")
    void shouldEscapeHtmlInContentType() {
        setupFile("photo.jpg", "<script>alert(1)</script>", 1024);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("&lt;script&gt;alert(1)&lt;/script&gt;"));
    }
}
