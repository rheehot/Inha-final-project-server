package com.dragonappear.inha.repository.buying;

import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.buying.BidBuying;
import com.dragonappear.inha.domain.buying.Buying;
import com.dragonappear.inha.domain.item.value.Category;
import com.dragonappear.inha.domain.item.Item;
import com.dragonappear.inha.domain.item.value.Manufacturer;
import com.dragonappear.inha.domain.item.product.Notebook;
import com.dragonappear.inha.domain.item.value.CategoryName;
import com.dragonappear.inha.domain.item.value.ManufacturerName;
import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.user.Role;
import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.domain.user.UserAddress;
import com.dragonappear.inha.domain.value.Address;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.repository.auctionitem.AuctionitemRepository;
import com.dragonappear.inha.repository.item.CategoryRepository;
import com.dragonappear.inha.repository.item.ItemRepository;
import com.dragonappear.inha.repository.item.ManufacturerRepository;
import com.dragonappear.inha.repository.payment.PaymentRepository;
import com.dragonappear.inha.repository.user.UserAddressRepository;
import com.dragonappear.inha.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class BuyingRepositoryTest {
    @Autowired PaymentRepository paymentRepository;
    @Autowired UserRepository userRepository;
    @Autowired AuctionitemRepository auctionitemRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired ManufacturerRepository manufacturerRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired UserAddressRepository userAddressRepository;
    @Autowired BuyingRepository buyingRepository;

    @Test
    public void 구매생성_테스트() throws Exception{
        //given
        Category newCategory = new Category(CategoryName.노트북);
        categoryRepository.save(newCategory);

        Manufacturer newManufacturer = new Manufacturer(ManufacturerName.삼성);
        manufacturerRepository.save(newManufacturer);

        Item newItem = new Notebook("맥북", "serial1", LocalDate.of(2021, 5, 21)
                ,"미스틱 실버"
                ,  Money.wons(1_000_000L),   newCategory,newManufacturer);
        itemRepository.save(newItem);

        Auctionitem auctionitem = new Auctionitem(newItem, Money.wons(10_000_000_000L));
        auctionitemRepository.save(auctionitem);

        User newUser = new User("사용자1", "yyh", "사용자1@naver.com","010-1234-5678","1234",new HashSet<>(Arrays.asList(Role.builder()
                .roleName("ROLE_USER")
                .roleDesc("사용자")
                .build())));
        userRepository.save(newUser);

        UserAddress newAddress = new UserAddress(newUser, new Address("yyh","010-1111-1111","incehon", "inharo", "127", "22207"));
        userAddressRepository.save(newAddress);

        Payment newPayment = new Payment("카카오페이"
                , "imp_"+ new Random().nextLong()
                ,"merchant_"+new Random().nextLong()
                ,auctionitem.getPrice()
                ,Money.wons(0L)
                ,newUser, 1L,newItem);
        paymentRepository.save(newPayment);

        Buying newBuying = new BidBuying(newPayment, LocalDateTime.now());
        buyingRepository.save(newBuying);
        //when
        Buying findBuying = buyingRepository.findById(newBuying.getId()).get();
        //then

        assertThat(findBuying).isEqualTo(newBuying);
        assertThat(findBuying.getId()).isEqualTo(newBuying.getId());
        assertThat(findBuying.getBuyingStatus()).isEqualTo(newBuying.getBuyingStatus());
        assertThat(findBuying.getPayment()).isEqualTo(newBuying.getPayment());
    }

}