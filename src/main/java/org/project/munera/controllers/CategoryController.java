package org.project.munera.controllers;

import org.project.munera.entities.Category;
import org.project.munera.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public List<Category> list() {
        return this.categoryService.fetchExpenses();
    }

    @GetMapping("/categories/{category}")
    public Category reference(@PathVariable Category category) {
        return category;
    }

    @PostMapping("/categories")
    public Category create(@RequestBody Category category) {
        return this.categoryService.addNewCategory(category);
    }

    @PatchMapping("/categories/{category}")
    public Category patch(@PathVariable("category") Category categoryToPatch, @RequestBody Category categoryPatched){
        return this.categoryService.patchCategory(categoryToPatch, categoryPatched);
    }

    @DeleteMapping("/categories/{category}")
    public void delete(@PathVariable Category category) {
        this.categoryService.deleteCategory(category);
    }
}
