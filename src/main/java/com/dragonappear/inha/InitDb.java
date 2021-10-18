package com.dragonappear.inha;


import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.auctionitem.BidAuctionitem;
import com.dragonappear.inha.domain.buying.Buying;
import com.dragonappear.inha.domain.deal.Deal;
import com.dragonappear.inha.domain.item.*;
import com.dragonappear.inha.domain.item.value.CategoryName;
import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.selling.Selling;
import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.domain.user.UserAddress;
import com.dragonappear.inha.domain.user.value.UserRole;
import com.dragonappear.inha.domain.value.Address;
import com.dragonappear.inha.domain.value.Image;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.repository.item.CategoryManufacturerRepository;
import com.dragonappear.inha.repository.item.CategoryRepository;
import com.dragonappear.inha.repository.item.ManufacturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.dragonappear.inha.domain.item.value.CategoryName.노트북;
import static com.dragonappear.inha.domain.item.value.CategoryName.태블릿;
import static com.dragonappear.inha.domain.item.value.ManufacturerName.삼성;
import static com.dragonappear.inha.domain.item.value.ManufacturerName.애플;

@Profile("init")
@RequiredArgsConstructor
@Component
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit0();
        initService.dbInit1();
        initService.dbInit2();
        initService.dbInit3();
    }

    @RequiredArgsConstructor
    @Transactional
    @Component
    static class InitService {
        private final EntityManager em;
        private final CategoryRepository categoryRepository;
        private final ManufacturerRepository manufacturerRepository;
        private final CategoryManufacturerRepository categoryManufacturerRepository;

        public void dbInit0() {
            Category category = new Category(노트북);
            Category category1 = new Category(태블릿);
            em.persist(category);
            em.persist(category1);
            Manufacturer manufacturer = new Manufacturer(애플);
            Manufacturer manufacturer1 = new Manufacturer(삼성);
            em.persist(manufacturer);
            em.persist(manufacturer1);
            em.persist(new CategoryManufacturer(category, manufacturer));
            em.persist(new CategoryManufacturer(category, manufacturer1));
            em.persist(new CategoryManufacturer(category1, manufacturer));
            em.persist(new CategoryManufacturer(category1, manufacturer1));
        }


        public void dbInit1() {
            Category category = categoryRepository.findByCategoryName(노트북).get();
            Manufacturer manufacturer = manufacturerRepository.findByManufacturerName(애플).get();
            Item item1 = new Item("13인치 MacBook Pro", "modelNumber1", LocalDate.of(2020, 5, 4),"space-gray",Money.wons(1_690_000),
                    Money.wons(1_400_000),category,manufacturer);
            Item item2 = new Item("13인치 MacBook Air", "modelNumber2",LocalDate.of(2020, 5, 4),"space-gray", Money.wons(1_690_000),
                    Money.wons(1_400_000),category,manufacturer);
            em.persist(item1);
            em.persist(item2);
            Image image1 = new Image("2020 macbook13 pro", "macbook_pro_13_2020.png", "/Users/dragonappear/Documents/study/inha_document/컴퓨터종합설계/code/inha/src/main/resources/static/items/");
            Image image2 = new Image("2020 macbook13 air", "macbook_air_13_2020.png", "/Users/dragonappear/Documents/study/inha_document/컴퓨터종합설계/code/inha/src/main/resources/static/items/");
            ItemImage itemImage1 = new ItemImage(item1, image1);
            ItemImage itemImage2 = new ItemImage(item2, image1);
            em.persist(itemImage1);
            em.persist(itemImage2);
        }
        public void dbInit2() {
            Category category = categoryRepository.findByCategoryName(노트북).get();
            Manufacturer manufacturer = manufacturerRepository.findByManufacturerName(삼성).get();
            Item item1 = new Item("Galaxy Book Pro"
                    , "modelNumber3"
                    ,LocalDate.of(2021, 5, 21)
                    ,"미스틱 실버"
                    , Money.wons(2_470_000)
                    , Money.wons(1_500_000),category,manufacturer);

            Item item2 = new Item("Galaxy Book"
                    , "modelNumber4"
                    ,LocalDate.of(2021, 5, 21)
                    ,"미스틱 실버"
                    , Money.wons(1_790_000)
                    , Money.wons(1_400_000),category,manufacturer);

            em.persist(item1);
            em.persist(item2);
            Image image1 = new Image("2021 Galaxy Book Pro", "samsung_galaxybook_pro_2021.jpg", "/Users/dragonappear/Documents/study/inha_document/컴퓨터종합설계/code/inha/src/main/resources/static/items/");
            ItemImage itemImage1 = new ItemImage(item1, image1);
            Image image2 = new Image("2021 Galaxy Book", "samsung_galaxybook_2021.jpg", "/Users/dragonappear/Documents/study/inha_document/컴퓨터종합설계/code/inha/src/main/resources/static/items/");
            ItemImage itemImage2 = new ItemImage(item2, image2);
            em.persist(itemImage1);
            em.persist(itemImage2);
        }

        public void dbInit3() {
            User admin = new User("admin", "admin@auctionhelp.com", UserRole.ADMIN, null);
            User user1 = new User("user1", "nickname1", "user1@naver.com", "010-3141-3519");

            em.persist(admin);
            em.persist(user1);

            UserAddress userAddress = new UserAddress(user1, new Address("city1", "steet1", "detail1", "zipcode1"));
            em.persist(userAddress);

            Item item = em.find(Item.class, 1L);
            Auctionitem auctionitem = new BidAuctionitem(item, Money.wons(10_000_000L),LocalDateTime.now().plusDays(10));
            em.persist(auctionitem);
            Selling selling = new Selling(admin, auctionitem);
            em.persist(selling);

            Payment payment = new Payment(item.getItemName()
                    , auctionitem.getPrice()
                    , user1.getUsername()
                    , user1.getEmail()
                    , user1.getUserTel()
                    , user1.getUserAddresses().get(0).getUserAddress()
                    , user1
                    , auctionitem);
            em.persist(payment);
            Buying buying = new Buying(payment);
            em.persist(buying);

            Deal deal = new Deal(buying, selling);
            em.persist(deal);
        }

    }
}
