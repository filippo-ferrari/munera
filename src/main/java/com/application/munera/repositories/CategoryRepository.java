package com.application.munera.repositories;

import com.application.munera.data.Category;
import com.application.munera.data.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Expense> {
}
