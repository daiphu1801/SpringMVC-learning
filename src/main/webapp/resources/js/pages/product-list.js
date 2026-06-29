/**
 * JavaScript for admin product list page
 */
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".delete-product-form").forEach(form => {
        form.addEventListener("submit", function(event) {
            if (!confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
                event.preventDefault();
            }
        });
    });
});
