package com.unifind.backend.repository;

import com.unifind.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByStatus(Item.ItemStatus status);
    List<Item> findByType(Item.ItemType type);
    List<Item> findByReportedById(Long userId);
    List<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String desc);
}