package com.examp.springmvc.shared.domain.task;

import java.util.List;
import java.util.Optional;

public interface ExcelTaskRepository {

    void save(ExcelTask task);

    Optional<ExcelTask> findById(String id);

    List<ExcelTask> findRecentTasks(int limit);
}
