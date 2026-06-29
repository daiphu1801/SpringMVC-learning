/**
 * VietQR Payment scripts
 */
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.copy-badge').forEach(badge => {
        badge.addEventListener('click', (e) => {
            e.preventDefault();
            const textToCopy = badge.getAttribute('data-copy');
            if (textToCopy) {
                navigator.clipboard.writeText(textToCopy).then(() => {
                    const originalText = badge.textContent;
                    badge.textContent = 'Đã chép!';
                    badge.style.background = '#2ecc71';
                    badge.style.color = '#fff';
                    badge.style.borderColor = '#2ecc71';
                    setTimeout(() => {
                        badge.textContent = originalText;
                        badge.style.background = '';
                        badge.style.color = '';
                        badge.style.borderColor = '';
                    }, 1500);
                }).catch(err => {
                    console.error('Không thể sao chép văn bản: ', err);
                });
            }
        });
    });
});
