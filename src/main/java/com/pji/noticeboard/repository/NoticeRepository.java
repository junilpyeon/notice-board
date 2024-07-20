package com.pji.noticeboard.repository;

import com.pji.noticeboard.entity.Notice;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n ORDER BY n.viewCount DESC")
    List<Notice> findTop5ByOrderByViewCountDesc(Pageable pageable);
    @NonNull
    Page<Notice> findAll(@NonNull Pageable pageable);

    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void incrementViewCount(Long id);
}
