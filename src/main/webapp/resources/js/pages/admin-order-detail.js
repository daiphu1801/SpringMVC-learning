/**
 * JavaScript for admin order details page
 */
document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll(".cancel-order-form").forEach(form => {
        form.addEventListener("submit", function(event) {
            if (!confirm("Huỷ đơn hàng này?")) {
                event.preventDefault();
            }
        });
    });
});
