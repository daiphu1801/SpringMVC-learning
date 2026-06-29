/**
 * Shopping Cart Event Listeners and Logic
 */
document.addEventListener('DOMContentLoaded', () => {
    // Minus buttons
    document.querySelectorAll('.qty-btn-minus').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const form = btn.closest('form');
            const input = form.querySelector('input[name="quantity"]');
            let val = parseInt(input.value) || 1;
            if (val > 1) {
                input.value = val - 1;
                form.submit();
            } else if (val === 1) {
                if (confirm('Xóa sản phẩm này khỏi giỏ?')) {
                    // Send request to remove
                    const row = btn.closest('tr');
                    const removeForm = row ? row.querySelector('.remove-item-form') : null;
                    if (removeForm) {
                        removeForm.submit();
                    } else {
                        input.value = 0;
                        form.submit();
                    }
                }
            }
        });
    });

    // Plus buttons
    document.querySelectorAll('.qty-btn-plus').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const form = btn.closest('form');
            const input = form.querySelector('input[name="quantity"]');
            let val = parseInt(input.value) || 1;
            if (val < 99) {
                input.value = val + 1;
                form.submit();
            }
        });
    });

    // Quantity inputs change
    document.querySelectorAll('.qty-control-input').forEach(input => {
        input.addEventListener('change', () => {
            const form = input.closest('form');
            let val = parseInt(input.value);
            if (isNaN(val) || val < 1) {
                input.value = 1;
            } else if (val > 99) {
                input.value = 99;
            }
            form.submit();
        });
    });

    // Remove button confirmation
    document.querySelectorAll('.remove-item-form').forEach(form => {
        form.addEventListener('submit', (e) => {
            if (!confirm('Xóa sản phẩm này khỏi giỏ?')) {
                e.preventDefault();
            }
        });
    });
});
