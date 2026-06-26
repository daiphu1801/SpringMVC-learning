/**
 * Javascript handling shopping cart actions and interactions.
 */

function decrementQty(btn) {
    const form = btn.closest('form');
    const input = form.querySelector('input[name="quantity"]');
    let val = parseInt(input.value);
    if (val > 1) {
        input.value = val - 1;
        form.submit();
    } else if (val === 1) {
        if (confirm('Xóa sản phẩm này khỏi giỏ?')) {
            // Submit with quantity 0 to trigger automatic deletion
            input.value = 0;
            form.submit();
        }
    }
}

function incrementQty(btn) {
    const form = btn.closest('form');
    const input = form.querySelector('input[name="quantity"]');
    let val = parseInt(input.value);
    if (val < 99) {
        input.value = val + 1;
        form.submit();
    }
}
