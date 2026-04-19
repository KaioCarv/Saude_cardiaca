package com.saudecardiaca.repository;

import com.saudecardiaca.model.HeartHealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HeartHealthRecordRepository extends JpaRepository<HeartHealthRecord, Long> {

    List<HeartHealthRecord> findByUserIdOrderByRecordedAtDesc(Long userId);

    List<HeartHealthRecord> findByUserIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    List<HeartHealthRecord> findByUserIdAndRecordedAtGreaterThanEqualOrderByRecordedAtDesc(
            Long userId, LocalDateTime start);

    List<HeartHealthRecord> findByUserIdAndRecordedAtLessThanEqualOrderByRecordedAtDesc(
            Long userId, LocalDateTime end);
}
