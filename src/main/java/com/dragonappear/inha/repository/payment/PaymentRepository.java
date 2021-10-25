package com.dragonappear.inha.repository.payment;

import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.payment.value.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Query("select p from Payment p where p.user.id=:userId")
    List<Payment> findByUserId( @Param("userId") Long userId);

    @Query("select p from Payment p where p.auctionitem.id=:auctionItemId")
    List<Payment> findByAuctionItemId(@Param("auctionItemId") Long auctionItemId);

    @Query("select p from Payment p where p.auctionitem.item.itemName=:itemName")
    List<Payment> findByItemName(@Param("itemName") String itemName);

    @Query("select p from Payment p where p.auctionitem.item.itemName=:itemName and p.paymentStatus=:status")
    List<Payment> findByCompletedItemName(@Param("itemName") String itemName, @Param("status") PaymentStatus status);

}
