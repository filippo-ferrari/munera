package org.project.munera.repositories;

import org.project.munera.entities.PeriodicExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PeriodicExpenseRepository extends JpaRepository<PeriodicExpense, Long>, JpaSpecificationExecutor<PeriodicExpense> {

}
