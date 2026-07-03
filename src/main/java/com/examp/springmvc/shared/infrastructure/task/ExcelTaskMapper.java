package com.examp.springmvc.shared.infrastructure.task;

import com.examp.springmvc.shared.domain.task.ExcelTask;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ExcelTaskMapper {

    void insert(ExcelTask task);

    void update(ExcelTask task);

    ExcelTask findById(@Param("id") String id);

    List<ExcelTask> findRecentTasks(@Param("limit") int limit);
}
