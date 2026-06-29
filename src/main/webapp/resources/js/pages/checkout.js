/**
 * Checkout Form Scripts
 */
document.addEventListener('DOMContentLoaded', () => {
    const paymentCards = document.querySelectorAll('.payment-card');
    paymentCards.forEach(card => {
        const radio = card.querySelector('input[type="radio"]');
        if (radio) {
            // When radio changes (or card is clicked)
            radio.addEventListener('change', () => {
                paymentCards.forEach(c => c.classList.remove('active'));
                if (radio.checked) {
                    card.classList.add('active');
                }
            });

            // Make sure the entire card click checks the radio
            card.addEventListener('click', (e) => {
                if (e.target !== radio) {
                    radio.checked = true;
                    // Trigger change event manually so the listener runs
                    radio.dispatchEvent(new Event('change'));
                }
            });
        }
    });
});
