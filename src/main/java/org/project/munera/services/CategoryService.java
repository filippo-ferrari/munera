package org.project.munera.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.project.munera.entities.Category;
import org.project.munera.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> fetchExpenses() {
        return this.categoryRepository.findAll();
    }
    public Category addNewCategory(final @NonNull Category category) {
        return this.categoryRepository.save(category);
    }

    @Transactional
    public Category patchCategory(@NonNull Category categoryToPatch, Category patchedCategory){
        return this.categoryRepository.findById(categoryToPatch.getId())
                .map(existingCategory -> {
                    existingCategory = patchedCategory;
                    return categoryRepository.save(existingCategory);
                })
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + categoryToPatch.getId() + "not found."));
    }

    public void deleteCategory(final @NonNull Category category) {
        this.categoryRepository.delete(category);
    }
}
