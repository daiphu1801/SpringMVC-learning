/**
 * JavaScript for product details page
 */
document.addEventListener("DOMContentLoaded", function() {
    const buyForm = document.querySelector(".flex-2");
    if (buyForm) {
        buyForm.addEventListener("submit", function() {
            const quantityInput = document.getElementById("quantityInput");
            const quantityHidden = document.getElementById("quantityHidden");
            if (quantityInput && quantityHidden) {
                quantityHidden.value = quantityInput.value;
            }
        });
    }
});
