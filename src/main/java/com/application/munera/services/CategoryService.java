package com.application.munera.services;

import com.application.munera.data.Category;
import com.application.munera.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(final CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> findAllByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    public void update(Category category) {
        categoryRepository.save(category);
    }

    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    public Page<Category> list(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    public Long count() {
        return this.categoryRepository.count();
    }

    public Category save(Category category) {
        return this.categoryRepository.save(category);
    }
}
