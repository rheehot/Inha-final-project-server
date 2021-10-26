package com.dragonappear.inha.repository.selling;


import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.selling.QSelling;
import com.dragonappear.inha.domain.selling.Selling;
import com.dragonappear.inha.domain.selling.value.SellingStatus;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.service.item.ItemService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dragonappear.inha.domain.auctionitem.QAuctionitem.auctionitem;
import static com.dragonappear.inha.domain.selling.QSelling.*;

@RequiredArgsConstructor
public class SellingRepositoryCustomImpl implements SellingRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Object,Object> findLowestSellingPrice(Long itemId) {
        Map<Object, Object> map = new HashMap<>();

        List<Selling> list = jpaQueryFactory.selectFrom(selling)
                .leftJoin(selling.auctionitem, auctionitem)
                .where(selling.sellingStatus.eq(SellingStatus.판매입찰중).and(auctionitem.item.id.eq(itemId)))
                .orderBy(auctionitem.price.amount.asc())
                .fetch();
        try{
            if(list.size()==0){
                throw new Exception();
            }
            Auctionitem auctionitem = list.get(0).getAuctionitem();
            map.put("auctionitemId", auctionitem.getId());
            map.put("amount",auctionitem.getPrice().getAmount().toString());
            return map;
        }catch (Exception e){
            map.put("auctionitemId", "해당 아이템 판매입찰이 존재하지 않습니다");
            map.put("amount", "0");
            return map;
        }
    }
}
