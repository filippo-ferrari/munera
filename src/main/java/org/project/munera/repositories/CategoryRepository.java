package org.project.munera.repositories;

import org.project.munera.entities.Category;
import org.project.munera.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Expense> {
}
