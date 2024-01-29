package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select * from compilations c where :pinned is null or :pinned = c.pinned")
    List<Compilation> findAllByPinnedOrNot(@Param("pinned") Boolean pinned, PageRequest pageRequest);
}
