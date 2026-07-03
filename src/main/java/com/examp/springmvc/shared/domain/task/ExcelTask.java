package com.examp.springmvc.shared.domain.task;

import java.time.LocalDateTime;

public final class ExcelTask {

    private String id;
    private ExcelTaskType type;
    private ExcelTaskStatus status;
    private int progress;
    private Integer totalRows;
    private Integer successRows;
    private Integer failedRows;
    private String errorSummary;
    private String resultUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExcelTask() {}

    public ExcelTask(
            String id,
            ExcelTaskType type,
            ExcelTaskStatus status,
            int progress,
            Integer totalRows,
            Integer successRows,
            Integer failedRows,
            String errorSummary,
            String resultUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.progress = progress;
        this.totalRows = totalRows;
        this.successRows = successRows;
        this.failedRows = failedRows;
        this.errorSummary = errorSummary;
        this.resultUrl = resultUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ExcelTaskType getType() {
        return type;
    }

    public void setType(ExcelTaskType type) {
        this.type = type;
    }

    public ExcelTaskStatus getStatus() {
        return status;
    }

    public void setStatus(ExcelTaskStatus status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getSuccessRows() {
        return successRows;
    }

    public void setSuccessRows(Integer successRows) {
        this.successRows = successRows;
    }

    public Integer getFailedRows() {
        return failedRows;
    }

    public void setFailedRows(Integer failedRows) {
        this.failedRows = failedRows;
    }

    public String getErrorSummary() {
        return errorSummary;
    }

    public void setErrorSummary(String errorSummary) {
        this.errorSummary = errorSummary;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
