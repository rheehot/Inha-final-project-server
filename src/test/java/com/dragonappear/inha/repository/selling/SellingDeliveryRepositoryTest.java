package com.dragonappear.inha.repository.selling;


import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.item.value.Category;
import com.dragonappear.inha.domain.item.Item;
import com.dragonappear.inha.domain.item.value.Manufacturer;
import com.dragonappear.inha.domain.item.product.Notebook;
import com.dragonappear.inha.domain.item.value.CategoryName;
import com.dragonappear.inha.domain.item.value.ManufacturerName;
import com.dragonappear.inha.domain.selling.InstantSelling;
import com.dragonappear.inha.domain.selling.Selling;
import com.dragonappear.inha.domain.selling.SellingDelivery;
import com.dragonappear.inha.domain.user.Role;
import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.domain.value.CourierName;
import com.dragonappear.inha.domain.value.Delivery;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.repository.auctionitem.AuctionitemRepository;
import com.dragonappear.inha.repository.item.CategoryRepository;
import com.dragonappear.inha.repository.item.ItemRepository;
import com.dragonappear.inha.repository.item.ManufacturerRepository;
import com.dragonappear.inha.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class SellingDeliveryRepositoryTest {
    @Autowired UserRepository userRepository;
    @Autowired SellingRepository sellingRepository;
    @Autowired AuctionitemRepository auctionitemRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired ManufacturerRepository manufacturerRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired SellingDeliveryRepository sellingDeliveryRepository;


    @Test
    public void 판매배송생성_테스트() throws Exception{
        //given
        User newUser = new User("사용자1", "yyh", "사용자1@naver.com","010-1234-5678","1234",new HashSet<>(Arrays.asList(Role.builder()
                .roleName("ROLE_USER")
                .roleDesc("사용자")
                .build())));
        userRepository.save(newUser);
        Category newCategory = new Category(CategoryName.노트북);
        categoryRepository.save(newCategory);
        Manufacturer newManufacturer = new Manufacturer(ManufacturerName.삼성);
        manufacturerRepository.save(newManufacturer);
        Item newItem = new Notebook("맥북", "serial1", LocalDate.of(2021, 5, 21)
                ,"미스틱 실버"
                ,  Money.wons(1_000_000L),   newCategory,newManufacturer);
        itemRepository.save(newItem);
        Auctionitem auctionitem = new Auctionitem(newItem,Money.wons(10_000_000_000L));
        auctionitemRepository.save(auctionitem);

        Selling newSelling = new InstantSelling(newUser, auctionitem);
        sellingRepository.save(newSelling);

        SellingDelivery newDelivery = new SellingDelivery(newSelling, new Delivery(CourierName.CJ대한통운, "1234-1234"));
        sellingDeliveryRepository.save(newDelivery);
        //when
        SellingDelivery findDelivery = sellingDeliveryRepository.findById(newDelivery.getId()).get();
        //then
        assertThat(findDelivery).isEqualTo(newDelivery);
        assertThat(findDelivery.getId()).isEqualTo(newDelivery.getId());
        assertThat(findDelivery.getSelling()).isEqualTo(newDelivery.getSelling());
        assertThat(findDelivery.getDelivery()).isEqualTo(newDelivery.getDelivery());
    }
}