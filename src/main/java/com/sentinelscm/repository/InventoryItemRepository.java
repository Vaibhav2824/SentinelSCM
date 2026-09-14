package com.sentinelscm.repository;

import com.sentinelscm.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
    List<InventoryItem> findAllByOrderByIdAsc();
    long countByQuantityLessThan(int threshold);
}
