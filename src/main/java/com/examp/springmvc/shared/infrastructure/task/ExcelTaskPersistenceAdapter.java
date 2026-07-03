package com.examp.springmvc.shared.infrastructure.task;

import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ExcelTaskPersistenceAdapter implements ExcelTaskRepository {

    private final ExcelTaskMapper excelTaskMapper;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ExcelTaskPersistenceAdapter(ExcelTaskMapper excelTaskMapper) {
        this.excelTaskMapper = excelTaskMapper;
    }

    @Override
    public void save(ExcelTask task) {
        if (task == null || task.getId() == null) {
            return;
        }
        ExcelTask existing = excelTaskMapper.findById(task.getId());
        if (existing == null) {
            excelTaskMapper.insert(task);
        } else {
            excelTaskMapper.update(task);
        }
    }

    @Override
    public Optional<ExcelTask> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(excelTaskMapper.findById(id));
    }

    @Override
    public List<ExcelTask> findRecentTasks(int limit) {
        return excelTaskMapper.findRecentTasks(limit);
    }
}
