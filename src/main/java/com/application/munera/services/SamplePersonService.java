package com.application.munera.services;

import com.application.munera.data.Expense;
import com.application.munera.data.SamplePersonRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SamplePersonService {

    private final SamplePersonRepository repository;

    public SamplePersonService(SamplePersonRepository repository) {
        this.repository = repository;
    }

    public Optional<Expense> get(Long id) {
        return repository.findById(id);
    }

    public Expense update(Expense entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Expense> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Expense> list(Pageable pageable, Specification<Expense> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
