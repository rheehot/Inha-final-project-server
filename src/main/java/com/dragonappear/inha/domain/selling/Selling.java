package com.dragonappear.inha.domain.selling;

import com.dragonappear.inha.domain.JpaBaseEntity;
import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.auctionitem.value.AuctionitemStatus;
import com.dragonappear.inha.domain.deal.Deal;
import com.dragonappear.inha.domain.selling.value.SellingStatus;
import com.dragonappear.inha.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.dragonappear.inha.domain.auctionitem.value.AuctionitemStatus.*;
import static com.dragonappear.inha.domain.selling.value.SellingStatus.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Selling extends JpaBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selling_id")
    private Long id;

    @Column(name ="status",nullable = false)
    @Enumerated(STRING)
    private SellingStatus sellingStatus;

    /**
     * 연관관계
     */

    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = ALL, mappedBy = "selling")
    private SellingDelivery sellingDelivery;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User seller;

    @JsonIgnore
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "auctionitem_id")
    private Auctionitem auctionitem;

    @JsonIgnore
    @OneToOne(fetch = LAZY,mappedBy = "selling")
    private Deal deal;

    /**
     * 연관관계편의메서드
     */
    public void updateSellingDelivery(SellingDelivery sellingDelivery) {
        this.sellingDelivery = sellingDelivery;
    }

    private void updateSellingSeller(User seller) {
        this.seller = seller;
        seller.getSellings().add(this);
    }

    private void updateSellingAuctionitem(Auctionitem auctionitem) {
        this.auctionitem = auctionitem;
        auctionitem.updateSellingAuctionitem(this);
    }

    /**
     * 생성자메서드
     */
    public Selling(User seller, Auctionitem auctionitem) {
        this.sellingStatus = 판매입찰중;
        if (seller != null) {
            updateSellingSeller(seller);
        }
        if (auctionitem != null) {
            updateSellingAuctionitem(auctionitem);
        }
        this.sellingDelivery = null;
    }

    /**
     * 비즈니스 로직
     */
    public void updateStatus(SellingStatus sellingStatus) {
        this.sellingStatus = sellingStatus;
        if (sellingStatus == 판매취소) {
            this.auctionitem.updateStatus(경매취소);
        } else if (sellingStatus == 판매완료) {
            this.auctionitem.updateStatus(경매완료);
        } else if (sellingStatus == 판매입찰종료) {
            this.auctionitem.updateStatus(경매기한만료);
        } else if (sellingStatus == SellingStatus.거래중) {
            this.auctionitem.updateStatus(AuctionitemStatus.거래중);
        }
    }
}
