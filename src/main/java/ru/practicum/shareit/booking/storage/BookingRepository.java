package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking getFirstByItem_IdOrderByStartAsc(Long itemId);

    Booking getFirstByItem_IdOrderByEndDesc(Long itemId);

    boolean existsAllByBooker_IdAndEndBefore(Long booker_id, LocalDateTime endTime);

    List<Booking> findAllByBooker_IdOrderByEndDesc(Long userId);

    List<Booking> findAllByBooker_IdAndStatusOrderByEndDesc(Long userId, Status status);

    @Query("select b from Booking b join Item i on i.id = b.item.id" +
            " where i.owner.id = :ownerId order by b.end desc ")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("select b from Booking b join Item i on i.id = b.item.id" +
            " where i.owner.id = :ownerId and b.status = :status order by b.end desc ")
    List<Booking> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") Status status);
}