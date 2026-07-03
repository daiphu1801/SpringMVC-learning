/**
 * JavaScript for user list page - Handles Excel Import/Export triggers and navigation
 */
document.addEventListener("DOMContentLoaded", function() {
    // 1. Delete confirmation
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", function(event) {
            if (!confirm("Bạn có chắc chắn muốn xóa?")) {
                event.preventDefault();
            }
        });
    });

    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/users"));
    const csrfToken = document.getElementById("csrfToken-field") ? document.getElementById("csrfToken-field").value : "";

    // 2. Modals Elements
    const importModal = document.getElementById("modal-import-excel");
    const btnOpenImport = document.getElementById("btn-open-import");
    const btnCloseImport = document.getElementById("btn-close-import-modal");
    const formImport = document.getElementById("form-import-excel");
    const btnTriggerExport = document.getElementById("btn-trigger-export");

    // Open/Close Import Modal
    if (btnOpenImport) {
        btnOpenImport.addEventListener("click", () => {
            if (importModal) importModal.classList.add("active");
        });
    }
    if (btnCloseImport) {
        btnCloseImport.addEventListener("click", () => {
            if (importModal) importModal.classList.remove("active");
        });
    }

    // Submit Import Form
    if (formImport) {
        formImport.addEventListener("submit", function(e) {
            e.preventDefault();
            const fileInput = document.getElementById("excel-file");
            if (!fileInput.files || fileInput.files.length === 0) {
                alert("Vui lòng chọn một tệp Excel.");
                return;
            }

            const formData = new FormData();
            formData.append("file", fileInput.files[0]);

            const url = contextPath + "/users/excel/import?csrfToken=" + encodeURIComponent(csrfToken);
            fetch(url, {
                method: "POST",
                body: formData
            })
            .then(res => {
                if (res.status === 202) {
                    return res.json();
                } else {
                    return res.json().then(err => { throw new Error(err.error || "Lỗi không xác định"); });
                }
            })
            .then(data => {
                if (importModal) importModal.classList.remove("active");
                formImport.reset();
                // Redirect to excel dashboard to view progress
                window.location.href = contextPath + "/users/excel/dashboard";
            })
            .catch(err => {
                alert("Lỗi khi import: " + err.message);
            });
        });
    }

    // Trigger Export
    if (btnTriggerExport) {
        btnTriggerExport.addEventListener("click", function() {
            const url = contextPath + "/users/excel/export?csrfToken=" + encodeURIComponent(csrfToken);
            fetch(url, {
                method: "POST"
            })
            .then(res => {
                if (res.status === 202) {
                    return res.json();
                } else {
                    return res.json().then(err => { throw new Error(err.error || "Lỗi không xác định"); });
                }
            })
            .then(data => {
                // Redirect to excel dashboard to view progress
                window.location.href = contextPath + "/users/excel/dashboard";
            })
            .catch(err => {
                alert("Lỗi khi trigger export: " + err.message);
            });
        });
    }
});
