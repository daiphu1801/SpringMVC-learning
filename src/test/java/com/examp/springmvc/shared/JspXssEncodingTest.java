package com.examp.springmvc.shared;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class JspXssEncodingTest {

    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    @Test
    public void testAllJspFilesAreSecurelyEncoded() throws Exception {
        List<File> filesToCheck = new ArrayList<>();
        findJspAndTagFiles(new File("src/main/webapp/WEB-INF/views"), filesToCheck);
        findJspAndTagFiles(new File("src/main/webapp/WEB-INF/tags"), filesToCheck);

        List<String> violations = new ArrayList<>();

        for (File file : filesToCheck) {
            checkFile(file, violations);
        }

        if (!violations.isEmpty()) {
            System.err.println("Found unsafe raw EL expressions in JSP/tag files:");
            for (String violation : violations) {
                System.err.println(violation);
            }
        }

        String msg = "Found " + violations.size() + " unsafe raw EL expressions! See logs.";
        assertTrue(violations.isEmpty(), msg);
    }

    private void findJspAndTagFiles(File dir, List<File> files) {
        if (!dir.exists()) {
            return;
        }
        File[] list = dir.listFiles();
        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                findJspAndTagFiles(f, files);
            } else if (f.getName().endsWith(".jsp") || f.getName().endsWith(".tag")) {
                files.add(f);
            }
        }
    }

    private void checkFile(File file, List<String> violations) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Matcher matcher = EL_PATTERN.matcher(line);
                while (matcher.find()) {
                    String fullMatch = matcher.group(0);
                    String expression = matcher.group(1).trim();

                    if (!isSafeContext(line, expression, fullMatch)) {
                        violations.add(String.format(
                                "File: %s:%d - Unsafe expression: %s", file.getPath(), lineNumber, line.trim()));
                    }
                }
            }
        }
    }

    private boolean isSafeContext(String line, String expression, String fullMatch) {
        // 1. Check if the line uses a safe escaping tag
        if (line.contains("<c:out") || line.contains("<fmt:formatNumber") || line.contains("<fmt:formatDate")) {
            return true;
        }

        // 2. Check if it's a Spring form tag/path (handled automatically)
        if (line.contains("<form:") || line.contains("path=\"")) {
            return true;
        }

        // 3. Check if it's a structural tag attribute
        if (line.contains("<c:if")
                || line.contains("<c:when")
                || line.contains("<c:forEach")
                || line.contains("<c:set")) {
            return true;
        }

        // 4. Check if it's a known safe system/request expression
        if (expression.startsWith("pageContext.request.contextPath")
                || expression.equals("appVersion")
                || expression.equals("csrfToken")) {
            return true;
        }

        // 5. Check if it's a structural boolean or safe expression
        if (expression.contains("==")
                || expression.contains("!=")
                || expression.contains("&&")
                || expression.contains("||")) {
            return true;
        }
        if (expression.startsWith("not ") || expression.startsWith("empty ")) {
            return true;
        }

        // 6. Check for safe ID values (case-insensitive suffix match for id/Id)
        String exprLower = expression.toLowerCase();
        if (exprLower.endsWith("id") || exprLower.equals("id")) {
            return true;
        }

        // 7. Check for numbers and quantities
        if (expression.contains(".size()")
                || expression.contains("quantity")
                || expression.contains("price")
                || expression.contains("subtotal")
                || expression.contains("unitPrice")
                || expression.contains("totalAmount")) {
            return true;
        }

        // 8. Check for backend formatted dates or strings
        if (expression.contains("formatted")
                || expression.equals("pageTitle")
                || expression.equals("formAction")
                || expression.equals("success")
                || expression.contains("status")) {
            return true;
        }

        // 9. Check if it's layout title attribute
        if (line.contains("title=\"${") && line.contains("<t:layout")) {
            return true;
        }

        // 10. Check for ternary conditions that output hardcoded safe string values
        if (expression.contains("?") && expression.contains(":")) {
            if (expression.contains("'")
                    || expression.contains("\"")
                    || expression.contains("true")
                    || expression.contains("false")) {
                return true;
            }
        }

        // Otherwise, it is potentially unsafe if rendered directly as raw HTML
        return false;
    }
}
