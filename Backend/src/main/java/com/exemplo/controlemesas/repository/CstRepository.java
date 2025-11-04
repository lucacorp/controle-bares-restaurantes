package com.exemplo.controlemesas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exemplo.controlemesas.model.CST;

public interface CstRepository extends JpaRepository<CST, Long> {
}
