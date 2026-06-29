/**
 * JavaScript for user list page
 */
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", function(event) {
            if (!confirm("Bạn có chắc chắn muốn xóa?")) {
                event.preventDefault();
            }
        });
    });
});
