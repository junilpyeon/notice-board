package com.pji.noticeboard.repository;

import com.pji.noticeboard.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n ORDER BY n.viewCount DESC")
    List<Notice> findTop5ByOrderByViewCountDesc(Pageable pageable);
    Page<Notice> findAll(Pageable pageable);
}
