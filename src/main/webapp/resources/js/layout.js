/**
 * Global layout and navigation scripts
 */
document.addEventListener('DOMContentLoaded', () => {
    // Add layout interaction logic if needed here
    console.log("Spring MVC layout loaded.");

    // Handle logout link click to submit POST request
    const logoutLinks = document.querySelectorAll('.btn-logout');
    const logoutForm = document.getElementById('logoutForm');
    if (logoutLinks && logoutForm) {
        logoutLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                logoutForm.submit();
            });
        });
    }
});
