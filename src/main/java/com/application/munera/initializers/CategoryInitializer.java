package com.application.munera.initializers;

import com.application.munera.data.Category;
import com.application.munera.services.CategoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryInitializer {

    @Autowired
    private CategoryService categoryService;

    @PostConstruct
    public void init() {
        if (categoryService.count() == 0) {
            // Create and save the Food category
            Category foodCategory = new Category();
            foodCategory.setName("Food");
            foodCategory.setUserId(1L);
            foodCategory.setDescription("All expenses related to food");
            categoryService.save(foodCategory);

            // Create and save the Travel category
            Category travelCategory = new Category();
            travelCategory.setName("Travel");
            travelCategory.setUserId(1L);
            travelCategory.setDescription("Expenses related to traveling, including transport and accommodation");
            categoryService.save(travelCategory);

            // Create and save the Electronics category
            Category electronicsCategory = new Category();
            electronicsCategory.setName("Electronics");
            electronicsCategory.setUserId(1L);
            electronicsCategory.setDescription("All expenses related to electronic devices and gadgets");
            categoryService.save(electronicsCategory);

            // Create and save the Events category
            Category eventsCategory = new Category();
            eventsCategory.setName("Events");
            eventsCategory.setUserId(1L);
            eventsCategory.setDescription("Expenses related to attending or organizing events");
            categoryService.save(eventsCategory);

            // Create and save the Clothing category
            Category clothingCategory = new Category();
            clothingCategory.setName("Clothing");
            clothingCategory.setUserId(1L);
            clothingCategory.setDescription("Expenses related to clothes and accessories");
            categoryService.save(clothingCategory);

            // Create and save the Bills category
            Category billsCategory = new Category();
            billsCategory.setName("Bills");
            billsCategory.setUserId(1L);
            billsCategory.setDescription("Recurring expenses like utilities, internet, and other bills");
            categoryService.save(billsCategory);

            // Create and save the Rent category
            Category rentCategory = new Category();
            rentCategory.setName("Rent");
            rentCategory.setUserId(1L);
            rentCategory.setDescription("Expenses related to rental payments for housing or office space");
            categoryService.save(rentCategory);
        }
    }
}
