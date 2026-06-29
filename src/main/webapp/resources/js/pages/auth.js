/**
 * Auth Form Scripts (Login/Register)
 */
document.addEventListener('DOMContentLoaded', () => {
    const demoBtn = document.getElementById('btn-demo-fill');
    if (demoBtn) {
        demoBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');
            if (usernameInput && passwordInput) {
                usernameInput.value = 'adminnn';
                passwordInput.value = '123456';
            }
        });
    }
});
