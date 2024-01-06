package org.project.munera.services;

import lombok.NonNull;
import org.project.munera.entities.Category;
import org.project.munera.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> fetchExpenses() {
        return this.categoryRepository.findAll();
    }
    public Category addNewCategory(final @NonNull Category category) {
        return this.categoryRepository.save(category);
    }

    public void deleteCategory(final @NonNull Category category) {
        this.categoryRepository.delete(category);
    }
}
