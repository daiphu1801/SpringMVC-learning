/**
 * JavaScript for order history page
 */
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", function(event) {
            if (!confirm("Bạn chắc chắn muốn huỷ đơn hàng này?")) {
                event.preventDefault();
            }
        });
    });
});
