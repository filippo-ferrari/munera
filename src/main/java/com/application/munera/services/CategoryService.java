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

    /**
     * Finds a category by its ID.
     *
     * @param id the ID of the category to find
     * @return an {@code Optional} containing the found category, or {@code Optional.empty()} if no category with the given ID exists
     */
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Finds all categories associated with a specific user ID.
     *
     * @param userId the ID of the user whose categories are to be retrieved
     * @return a {@code List} of categories associated with the given user ID
     */
    public List<Category> findAllByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    /**
     * Updates the provided category and associates it with a specific user ID.
     *
     * @param category the category to update
     * @param userId the ID of the user to associate with the category
     */
    public void update(Category category, Long userId) {
        category.setUserId(userId);
        categoryRepository.save(category);
    }

    /**
     * Deletes the given category.
     *
     * @param category the category to delete
     */
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    /**
     * Retrieves a paginated list of categories.
     *
     * @param pageable the pagination information
     * @return a {@code Page} containing the categories for the requested page
     */
    public Page<Category> list(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    /**
     * Counts the total number of categories.
     *
     * @return the total number of categories
     */
    public Long count() {
        return this.categoryRepository.count();
    }

    /**
     * Saves the given category to the repository.
     *
     * @param category the category to save
     * @return the saved category
     */
    public Category save(Category category) {
        return this.categoryRepository.save(category);
    }
}
