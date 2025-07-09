package com.handong.internationalmedia.service;

import com.handong.internationalmedia.entity.Event;
import com.handong.internationalmedia.entity.Member;
import com.handong.internationalmedia.repository.EventRepository;
import com.handong.internationalmedia.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public List<Event> getEventsByType(Event.EventType eventType) {
        return eventRepository.findByEventType(eventType);
    }

    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventDateBetween(start, end);
    }

    public List<Event> getEventsByOrganizer(Long organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public List<Event> getEventsByParticipant(Long memberId) {
        return eventRepository.findByParticipantsId(memberId);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateAfterAndStatus(LocalDateTime.now(), Event.EventStatus.UPCOMING);
    }

    @Transactional
    public Optional<Event> updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id)
                .map(event -> {
                    event.setTitle(updatedEvent.getTitle());
                    event.setDescription(updatedEvent.getDescription());
                    event.setEventDate(updatedEvent.getEventDate());
                    event.setLocation(updatedEvent.getLocation());
                    event.setEventType(updatedEvent.getEventType());
                    event.setStatus(updatedEvent.getStatus());
                    event.setMaxParticipants(updatedEvent.getMaxParticipants());
                    event.setRegistrationUrl(updatedEvent.getRegistrationUrl());
                    return eventRepository.save(event);
                });
    }

    @Transactional
    public Optional<Event> setEventOrganizer(Long eventId, Long organizerId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Member> memberOpt = memberRepository.findById(organizerId);

        if (eventOpt.isPresent() && memberOpt.isPresent()) {
            Event event = eventOpt.get();
            Member organizer = memberOpt.get();
            event.setOrganizer(organizer);
            return Optional.of(eventRepository.save(event));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Event> addParticipantToEvent(Long eventId, Long memberId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (eventOpt.isPresent() && memberOpt.isPresent()) {
            Event event = eventOpt.get();
            Member participant = memberOpt.get();
            
            if (event.getMaxParticipants() != null && 
                event.getParticipants().size() >= event.getMaxParticipants()) {
                return Optional.empty();
            }
            
            event.getParticipants().add(participant);
            return Optional.of(eventRepository.save(event));
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Event> removeParticipantFromEvent(Long eventId, Long memberId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (eventOpt.isPresent() && memberOpt.isPresent()) {
            Event event = eventOpt.get();
            Member participant = memberOpt.get();
            event.getParticipants().remove(participant);
            return Optional.of(eventRepository.save(event));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}