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
        byte[] magicBytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        setupFile(name, contentType, size, magicBytes);
    }

    private void setupFile(String name, String contentType, long size, byte[] magicBytes) {
        try {
            org.mockito.Mockito.lenient().when(file.isEmpty()).thenReturn(false);
            org.mockito.Mockito.lenient().when(file.getOriginalFilename()).thenReturn(name);
            org.mockito.Mockito.lenient().when(file.getContentType()).thenReturn(contentType);
            org.mockito.Mockito.lenient().when(file.getSize()).thenReturn(size);
            org.mockito.Mockito.lenient()
                    .when(file.getInputStream())
                    .thenReturn(new java.io.ByteArrayInputStream(magicBytes));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
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

        byte[] magicBytes;
        if (filename.endsWith("png")) {
            magicBytes = new byte[] {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, 0, 0, 0, 0, 0, 0, 0, 0};
        } else if (filename.endsWith("gif")) {
            magicBytes = new byte[] {(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, 0, 0, 0, 0, 0, 0, 0, 0};
        } else if (filename.endsWith("webp")) {
            magicBytes = new byte[] {
                (byte) 0x52,
                (byte) 0x49,
                (byte) 0x46,
                (byte) 0x46,
                0,
                0,
                0,
                0,
                (byte) 0x57,
                (byte) 0x45,
                (byte) 0x42,
                (byte) 0x50
            };
        } else if (filename.endsWith("avif")) {
            magicBytes = new byte[] {
                0,
                0,
                0,
                0,
                (byte) 0x66,
                (byte) 0x74,
                (byte) 0x79,
                (byte) 0x70,
                (byte) 0x61,
                (byte) 0x76,
                (byte) 0x69,
                (byte) 0x66
            };
        } else {
            magicBytes = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        setupFile(filename, mime, 1024, magicBytes);
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

    @Test
    @DisplayName("Should reject file when magic bytes do not match expected image signatures")
    void shouldRejectInvalidMagicBytes() {
        byte[] badMagic = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        setupFile("photo.jpg", "image/jpeg", 1024, badMagic);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> ImageFileValidator.validate(file));
        assertTrue(ex.getMessage().contains("Magic Bytes không khớp"));
    }
}
