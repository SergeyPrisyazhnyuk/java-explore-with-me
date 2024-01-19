package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}