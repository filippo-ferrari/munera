package com.application.munera.services;

import com.application.munera.data.Event;
import com.application.munera.repositories.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(final EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public void update(Event event) {
        eventRepository.save(event);
    }

    public void delete(Event event) {
        eventRepository.delete(event);
    }

    public Page<Event> list(Pageable pageable){
        return eventRepository.findAll(pageable);
    }
}
