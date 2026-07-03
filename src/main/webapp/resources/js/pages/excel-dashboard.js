/**
 * JavaScript for Excel Dashboard page - Handles Task Polling, statistics update and error log view
 */
document.addEventListener("DOMContentLoaded", function() {
    const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/users"));
    
    // Elements
    const taskContainer = document.getElementById("task-list-container");
    const errorModal = document.getElementById("modal-error-report");
    const btnCloseErrors = document.getElementById("btn-close-errors-modal");
    const errorLogContent = document.getElementById("excel-error-log-content");
    const btnRefresh = document.getElementById("btn-refresh-tasks");

    // Stats Elements
    const statTotal = document.getElementById("stat-total-tasks");
    const statActive = document.getElementById("stat-active-tasks");
    const statCompleted = document.getElementById("stat-completed-tasks");

    let pollingInterval = null;

    // Close error modal
    if (btnCloseErrors) {
        btnCloseErrors.addEventListener("click", () => {
            if (errorModal) errorModal.classList.remove("active");
        });
    }

    // Refresh tasks manually
    if (btnRefresh) {
        btnRefresh.addEventListener("click", () => {
            fetchTasks();
        });
    }

    // Polling Functions
    function startPolling() {
        if (pollingInterval) clearInterval(pollingInterval);
        fetchTasks();
        pollingInterval = setInterval(fetchTasks, 1500);
    }

    function stopPolling() {
        if (pollingInterval) {
            clearInterval(pollingInterval);
            pollingInterval = null;
        }
    }

    function fetchMetrics() {
        const url = contextPath + "/users/excel/metrics";
        fetch(url)
        .then(res => res.json())
        .then(data => {
            if (!data) return;
            
            // Reader Pool
            const reader = data.readerPool;
            const rActive = document.getElementById("reader-active-threads");
            const rQueue = document.getElementById("reader-queue-size");
            const rMax = document.getElementById("reader-max-threads");
            const rComp = document.getElementById("reader-completed-tasks");
            if (rActive) rActive.textContent = reader.activeCount;
            if (rQueue) rQueue.textContent = reader.queueSize;
            if (rMax) rMax.textContent = reader.maxPoolSize;
            if (rComp) rComp.textContent = reader.completedTaskCount;
            
            const readerPulse = document.getElementById("reader-pulse");
            const readerStatusText = document.getElementById("reader-status-text");
            if (reader.activeCount > 0 || reader.queueSize > 0) {
                if (readerPulse) readerPulse.classList.add("active");
                if (readerStatusText) readerStatusText.textContent = "Đang chạy";
            } else {
                if (readerPulse) readerPulse.classList.remove("active");
                if (readerStatusText) readerStatusText.textContent = "Đang rảnh";
            }

            // Writer Pool
            const writer = data.writerPool;
            const wActive = document.getElementById("writer-active-threads");
            const wQueue = document.getElementById("writer-queue-size");
            const wMax = document.getElementById("writer-max-threads");
            const wComp = document.getElementById("writer-completed-tasks");
            if (wActive) wActive.textContent = writer.activeCount;
            if (wQueue) wQueue.textContent = writer.queueSize;
            if (wMax) wMax.textContent = writer.maxPoolSize;
            if (wComp) wComp.textContent = writer.completedTaskCount;

            const writerPulse = document.getElementById("writer-pulse");
            const writerStatusText = document.getElementById("writer-status-text");
            if (writer.activeCount > 0 || writer.queueSize > 0) {
                if (writerPulse) writerPulse.classList.add("active");
                if (writerStatusText) writerStatusText.textContent = "Đang ghi...";
            } else {
                if (writerPulse) writerPulse.classList.remove("active");
                if (writerStatusText) writerStatusText.textContent = "Đang rảnh";
            }
        })
        .catch(err => {
            console.error("Error fetching metrics:", err);
        });
    }

    function fetchTasks() {
        const url = contextPath + "/users/excel/tasks";
        fetch(url)
        .then(res => res.json())
        .then(tasks => {
            // Update stats indicators
            updateStats(tasks);
            
            // Update Thread Pool metrics
            fetchMetrics();
 
            if (!tasks || tasks.length === 0) {
                if (taskContainer) {
                    taskContainer.innerHTML = '<div class="text-center text-muted py-4">Chưa có tiến trình nào được thực hiện gần đây.</div>';
                }
                stopPolling();
                return;
            }
 
            renderTasks(tasks);
 
            // Check if there are active tasks
            const hasRunningTask = tasks.some(t => t.status === "PENDING" || t.status === "PROCESSING");
            if (!hasRunningTask) {
                stopPolling();
            } else if (!pollingInterval) {
                // If there are running tasks and we are not polling, start it
                startPolling();
            }
        })
        .catch(err => {
            console.error("Error fetching tasks:", err);
            if (taskContainer) {
                taskContainer.innerHTML = '<div class="text-center text-danger py-4">Không thể kết nối tới server để lấy tiến trình.</div>';
            }
        });
    }

    function updateStats(tasks) {
        if (!tasks) return;
        const total = tasks.length;
        const active = tasks.filter(t => t.status === "PENDING" || t.status === "PROCESSING").length;
        const completed = tasks.filter(t => t.status === "COMPLETED" || t.status === "COMPLETED_WITH_ERRORS").length;

        if (statTotal) statTotal.textContent = total;
        if (statActive) statActive.textContent = active;
        if (statCompleted) statCompleted.textContent = completed;
    }

    function renderTasks(tasks) {
        if (!taskContainer) return;
        taskContainer.innerHTML = "";

        tasks.forEach(task => {
            const item = document.createElement("div");
            item.className = "task-item status-" + task.status.toLowerCase();

            // Card structure: Header -> Progress -> Footer details
            
            // 1. Header info
            const header = document.createElement("div");
            header.className = "task-item-header";

            const info = document.createElement("div");
            info.className = "task-info";
            const iconSvg = task.type === "IMPORT" 
                ? `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="vertical-align: middle; margin-right: 4px;"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>`
                : `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="vertical-align: middle; margin-right: 4px;"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>`;
            
            info.innerHTML = `${iconSvg} <strong>Loại:</strong> ${task.type === "IMPORT" ? "Nhập dữ liệu (Import)" : "Xuất dữ liệu (Export)"} <span style="margin: 0 8px; color: var(--border-color);">|</span> <strong>ID:</strong> <code class="sku-code" style="font-size: 0.8rem; padding: 2px 6px;">${escapeHtml(task.id)}</code>`;

            const badge = document.createElement("span");
            badge.className = `badge-task ${getStatusBadgeClass(task.status)}`;
            badge.textContent = getStatusText(task.status);

            header.appendChild(info);
            header.appendChild(badge);
            item.appendChild(header);

            // 2. Progress bar
            const progWrapper = document.createElement("div");
            progWrapper.className = "task-progress-wrapper";

            const progText = document.createElement("div");
            progText.className = "task-progress-text";
            progText.innerHTML = `<span>Tiến độ hoàn thành</span> <strong>${task.progress}%</strong>`;

            const barBg = document.createElement("div");
            barBg.className = "task-progress-bar-bg";

            const barFill = document.createElement("div");
            barFill.className = "task-progress-bar-fill";
            barFill.style.width = `${task.progress}%`;

            barBg.appendChild(barFill);
            progWrapper.appendChild(progText);
            progWrapper.appendChild(barBg);
            item.appendChild(progWrapper);

            // 3. Details grid (Stats + Actions)
            const detailsGrid = document.createElement("div");
            detailsGrid.className = "task-details-grid";

            const stats = document.createElement("div");
            stats.className = "task-stats";
            stats.innerHTML = `Tổng số: <strong>${task.totalRows}</strong> dòng <span style="margin: 0 4px; color: var(--border-color);">•</span> Thành công: <strong class="text-success">${task.successRows}</strong> <span style="margin: 0 4px; color: var(--border-color);">•</span> Thất bại: <strong class="text-danger">${task.failedRows}</strong>`;
            
            const actions = document.createElement("div");
            actions.className = "task-actions";

            if (task.type === "EXPORT" && task.status === "COMPLETED" && task.resultUrl) {
                const btnDownload = document.createElement("a");
                btnDownload.href = contextPath + task.resultUrl;
                btnDownload.className = "btn btn-sm";
                btnDownload.style.textDecoration = "none";
                btnDownload.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 4px; vertical-align: middle;"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg> Tải Excel`;
                actions.appendChild(btnDownload);
            }

            if (task.status === "COMPLETED_WITH_ERRORS" || task.status === "FAILED") {
                const btnErrors = document.createElement("button");
                btnErrors.type = "button";
                btnErrors.className = "btn-secondary btn-sm";
                btnErrors.innerHTML = `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right: 4px; vertical-align: middle;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg> Xem lỗi chi tiết`;
                btnErrors.addEventListener("click", () => showErrors(task.id));
                actions.appendChild(btnErrors);
            }

            detailsGrid.appendChild(stats);
            detailsGrid.appendChild(actions);
            item.appendChild(detailsGrid);

            taskContainer.appendChild(item);
        });
    }

    function showErrors(taskId) {
        const url = contextPath + "/users/excel/tasks/" + taskId + "/errors";
        if (errorLogContent) {
            errorLogContent.textContent = "Đang tải báo cáo lỗi...";
        }
        if (errorModal) {
            errorModal.classList.add("active");
        }
        
        fetch(url)
        .then(res => res.text())
        .then(text => {
            if (errorLogContent) {
                errorLogContent.textContent = text;
            }
        })
        .catch(err => {
            if (errorLogContent) {
                errorLogContent.textContent = "Không thể tải báo cáo lỗi: " + err.message;
            }
        });
    }

    function getStatusBadgeClass(status) {
        switch (status) {
            case "PENDING": return "badge-pending";
            case "PROCESSING": return "badge-processing";
            case "COMPLETED": return "badge-completed";
            case "COMPLETED_WITH_ERRORS": return "badge-warning";
            case "FAILED": return "badge-failed";
            default: return "badge-pending";
        }
    }

    function getStatusText(status) {
        switch (status) {
            case "PENDING": return "Đang chờ";
            case "PROCESSING": return "Đang xử lý";
            case "COMPLETED": return "Thành công";
            case "COMPLETED_WITH_ERRORS": return "Hoàn thành có lỗi";
            case "FAILED": return "Thất bại";
            default: return status;
        }
    }

    function escapeHtml(str) {
        if (!str) return "";
        return str.replace(/&/g, "&amp;")
                  .replace(/</g, "&lt;")
                  .replace(/>/g, "&gt;")
                  .replace(/"/g, "&quot;")
                  .replace(/'/g, "&#039;");
    }

    // Start fetching on load
    fetchTasks();
});
