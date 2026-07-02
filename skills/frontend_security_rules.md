# Skill: Frontend Security and Performance Guidelines

## Metadata
- **ID**: `frontend_security_and_performance_guidelines`
- **Description**: Enforces rules for frontend development (Javascript, CSS, JSP) to prevent DOM XSS, CSS Injection, ensure proper separation of concerns (no inline styling/scripting), and optimize performance.
- **Triggers**: Executed when editing or creating JS, CSS, JSP, or Tag files in the webapp.

## Rules & Guidelines

### 1. DOM XSS Prevention
- **Rule**: Never use unsafe sinks like `innerHTML` or `document.write` to bind or render dynamic/user-controlled data.
- **Alternatives**:
  - Use `element.textContent` or `element.innerText` for plain text bindings.
  - Use `document.createTextNode` to insert dynamic text nodes safely.
  - If rendering static lists with formatting (e.g. `<b>`, `<code>`), structure the dataset as objects (e.g., `{ label: "...", text: "..." }`) and build DOM elements programmatically using `document.createElement`.
  - If raw HTML from external sources must be rendered, run it through a sanitization library (such as DOMPurify) before inserting it.

### 2. CSS Injection & Safe Styling
- **Rule**: Do not dynamically assign element styles using unvalidated string input (e.g. `element.style.cssText` or `element.setAttribute('style', userInput)`).
- **Alternatives**:
  - Prefer adding/removing predefined CSS classes from stylesheet using `element.classList.add('className')` or `element.classList.remove('className')`.
  - For dynamic properties (e.g., width, coordinates), modify properties directly with validated data types (e.g., `element.style.left = safeCoordinates + 'px'`).

### 3. Separation of Concerns (No Inline CSS/JS)
- **Rule**: No `<style>` or `<script>` tags should reside inside view files (JSPs, HTMLs), except global system config variables.
- **Rule**: No inline style attributes (`style="..."`) on HTML elements. Use classes from `global.css` or page-specific stylesheets.
- **Rule**: No inline event handlers (e.g., `onclick="..."`, `onchange="..."`) on HTML elements. Register events dynamically in external JS files via `element.addEventListener('click', handler)`.
- **Organization**:
  - Place stylesheets under `/resources/css/pages/[page-name].css` or `/resources/css/layout.css`.
  - Place javascript files under `/resources/js/pages/[page-name].js` or `/resources/js/layout.js`.
  - Link them dynamically using `<jsp:attribute name="head">` inside JSP pages to place them in the layout's `<head>`.

### 4. Content Security Policy (CSP) & HTTP Headers
- **Rule**: Standard HTTP headers must be configured on the server side to protect client-side environments.
- **Rules**:
  - Implement a strict CSP (Content Security Policy) header. Avoid `'unsafe-inline'` or `'unsafe-eval'` for script-src and style-src.
  - Apply `X-Frame-Options: SAMEORIGIN` to prevent Clickjacking.
  - Apply `X-Content-Type-Options: nosniff` to enforce correct MIME types.

### 5. Subresource Integrity (SRI) for CDNs
- **Rule**: When using third-party CDN libraries (JS/CSS), always include the `integrity` (hash verification) and `crossorigin="anonymous"` attributes to prevent tampering.

### 6. Caching & Cache Busting
- **Rule**: Use cache busting strategies for static files to allow caching while guaranteeing updates on deployments.
- **Strategy**: Append Content-hash or package/project version as query string parameters (e.g. `?v=${project.version}`) to resource URLs.

### 7. Performance & Rendering Optimization
- **Rule**: Minimize render-blocking resources.
- **Rules**:
  - Load JS asynchronously using `defer` inside the `<head>` section.
  - Set preconnect hints for third-party servers: `<link rel="preconnect" href="...">`.
  - Optimize images: Use modern formats (.webp, .avif), lazy-load offscreen images using `loading="lazy"`, and always define `width` and `height` properties to prevent Cumulative Layout Shift (CLS).

### 8. JSP XSS Prevention (Server-side)
- **Rule**: Never print dynamic user-supplied or database-retrieved variables directly onto JSP templates using raw EL expressions like `${variable}` inside HTML body text or HTML attributes.
- **Rule**: Always wrap dynamic text, values, and HTML attributes in JSTL `<c:out value="${variable}"/>` tag (which defaults to XML/HTML escaping).
- **Exceptions**: Only use raw `${variable}` for internal, trusted IDs (e.g., entity IDs in links like `/edit/${id}`) or backend-formatted numbers/dates (e.g., using `<fmt:formatNumber>` or `<fmt:formatDate>`).

### 9. Custom CSRF Protection (Server & Client side)
- **Rule**: Every data-mutating request (HTTP POST/PUT/DELETE) must be protected by validating a CSRF token.
- **Rule**: Every form using POST/post method in JSP (both HTML `<form>` and Spring `<form:form>`) must include a hidden CSRF token input field:
  `<input type="hidden" name="csrfToken" value="${csrfToken}">`
- **Rule**: On the backend, `SecurityInterceptor` must generate a secure CSRF token (using `UUID`) for each session, expose it as a request attribute (`csrfToken`), and validate it for all POST requests. Invalid/missing tokens must result in a `403 Forbidden` error.

### 10. Session Cookie Security
- **Rule**: Session cookies must be configured with `HttpOnly=true` and `SameSite=Lax` at application startup via `SessionCookieConfig`.
- **Rule**: The `Secure` flag must be set to `true` in production environments (HTTPS only). It should be `false` in development (HTTP localhost) to avoid breaking the login flow.
- **Implementation**: Set these flags in `WebAppInitializer.onStartup()` through `servletContext.getSessionCookieConfig()`.

### 11. Additional HTTP Security Headers
- **Rule**: `SecurityInterceptor.preHandle()` must set all of the following response headers on every request:

| Header | Value | Purpose |
|--------|-------|---------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | Enforces HTTPS for 1 year |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Limits URL leakage through Referer header |
| `Permissions-Policy` | `camera=(), microphone=(), geolocation=()` | Disables unused browser APIs |

### 12. Third-party Library Control (SRI)
- **Principle**: Always prefer **self-hosting** JS/CSS libraries rather than loading them from external CDNs.
- **Reason**: If a CDN server is compromised and a library file is replaced with malicious code, every application using that CDN is silently infected (Supply Chain Attack). Self-hosting eliminates this risk entirely.
- **When a CDN is unavoidable**: Add the `integrity` (SRI hash) and `crossorigin="anonymous"` attributes to every `<script>` and `<link>` tag loaded from a CDN:
  ```html
  <script src="https://cdn.example.com/lib.js"
          integrity="sha384-<base64-hash>"
          crossorigin="anonymous"></script>
  ```
  The browser computes the hash of the downloaded file and refuses to execute it if it does not match.
- **Current status**: The project **does not load JS/CSS from any CDN** — all resources are self-hosted. SRI is not required. Google Fonts is font-only (cannot run JS logic) and is already governed by the CSP `style-src` directive.

### 13. No Secrets in the Frontend
- **Rule**: Never place API keys, API secrets, passwords, database credentials, or any other sensitive data in:
  - JavaScript files (`.js`)
  - JSP/HTML files sent to the browser
  - `localStorage` / `sessionStorage`
  - HTML `<input type="hidden">` fields (except the CSRF token)
- **Reason**: Everything delivered to the browser is public — any user can inspect it via DevTools (F12 → Sources / Network). The browser is an untrusted environment.
- **Where to store secrets**: Server-side in `application-local.properties` (excluded from version control), environment variables, or a dedicated secret manager.
- **Current status**: ✅ Safe. Cloudinary credentials (`api-key`, `api-secret`) exist only in `application.properties` on the server and do not appear in any JS or JSP file. No `localStorage` usage exists anywhere in the codebase.

### 14. Safe File Uploads (MIME Spoofing & Path Traversal Mitigation)
- **Rule**: Never trust client-provided file MIME types (via Content-Type header) or original filenames (`getOriginalFilename()`).
- **MIME Spoofing Mitigation**: Read the file's binary header (Magic Bytes / File Signature) using an `InputStream` on the server side to verify the actual format.
- **Path Traversal Mitigation**: Generate a randomized UUID-based filename on the server (e.g. `<UUID>.<ext>`) instead of saving with the client-provided name, to prevent directory traversal attacks (e.g. `../../etc/passwd`).
- **File Validation**: Validate the file size and verify that the extension matches both the allowed list and the verified magic byte signature.

---

## Success & Exit Criteria
- Zero instances of `innerHTML` or `document.write` used to bind unchecked variable data in JS.
- Zero instances of inline `<style>` and `<script>` in user-facing views.
- Zero inline styles (`style="..."`) or inline event handlers (`onclick="..."`) in any `.jsp` or `.tag` file.
- All CDN-based **JavaScript/CSS** must have `integrity` (SRI) and `crossorigin="anonymous"` attributes. Prefer self-hosting.
- All static assets utilize cache-busting version strings.
- All dynamic/user-supplied text and attributes in JSP views are escaped using `<c:out>` to prevent Stored/Reflected XSS.
- All POST forms in JSP views include the hidden `csrfToken` input field.
- All POST requests are verified for a valid CSRF token in the `SecurityInterceptor`.
- Session cookies are configured with `HttpOnly=true`, `SameSite=Lax`, and `Secure=true` (production).
- All 6 security response headers (CSP, X-Frame-Options, X-Content-Type-Options, HSTS, Referrer-Policy, Permissions-Policy) are set on every response.
- Zero API keys, secrets, or credentials present in any `.js`, `.jsp`, `.html` file or `localStorage`.
- All file upload functionalities validate Magic Bytes and use server-generated UUID filenames.
- All code passes checkstyle checks without violation.

