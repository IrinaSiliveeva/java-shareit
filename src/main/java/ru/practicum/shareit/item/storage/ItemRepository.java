package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(User owner);

    @Query("select i from Item i where (upper(i.name) like upper(concat('%',:keyword,'%') ) or " +
            "upper(i.description) like upper(concat('%',:keyword,'%') ) and i.available = true )")
    List<Item> searchItems(@Param("keyword") String keyword);
}