package com.handong.internationalmedia.repository;

import com.handong.internationalmedia.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(Event.EventStatus status);
    List<Event> findByEventType(Event.EventType eventType);
    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end);
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByParticipantsId(Long memberId);
    List<Event> findByEventDateAfterAndStatus(LocalDateTime date, Event.EventStatus status);
}