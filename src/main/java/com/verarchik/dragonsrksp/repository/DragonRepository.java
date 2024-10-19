package com.verarchik.dragonsrksp.repository;

import com.verarchik.dragonsrksp.entity.Dragon;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DragonRepository extends JpaRepository<Dragon, Long> {
    Dragon findDragonById(Long id) ;
}
