package com.eco.volunteer.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eco.volunteer.model.Announcement;
import com.eco.volunteer.model.AnnouncementStatus;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a WHERE a.status = :status ORDER BY a.isTop DESC, a.topTime DESC NULLS LAST, a.createTime DESC")
    List<Announcement> findByStatus(AnnouncementStatus status);

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.isTop = true ORDER BY a.topTime DESC")
    List<Announcement> findTopAnnouncements(AnnouncementStatus status);

    @Query("SELECT COUNT(a) > 0 FROM Announcement a WHERE a.status = :status AND a.updateTime > :lastCheckTime")
    boolean hasUpdates(AnnouncementStatus status, LocalDateTime lastCheckTime);
} 