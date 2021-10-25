package com.dragonappear.inha.service.inspection;

import com.dragonappear.inha.domain.auctionitem.BidAuctionitem;
import com.dragonappear.inha.domain.buying.Buying;
import com.dragonappear.inha.domain.deal.Deal;
import com.dragonappear.inha.domain.inspection.Inspection;
import com.dragonappear.inha.domain.inspection.InspectionImage;
import com.dragonappear.inha.domain.item.Category;
import com.dragonappear.inha.domain.item.Item;
import com.dragonappear.inha.domain.item.Manufacturer;
import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.selling.Selling;
import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.domain.user.UserAddress;
import com.dragonappear.inha.domain.value.Address;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.repository.auctionitem.AuctionitemRepository;
import com.dragonappear.inha.repository.buying.BuyingRepository;
import com.dragonappear.inha.repository.deal.DealRepository;
import com.dragonappear.inha.repository.inspection.InspectionImageRepository;
import com.dragonappear.inha.repository.inspection.InspectionRepository;
import com.dragonappear.inha.repository.item.CategoryRepository;
import com.dragonappear.inha.repository.item.ItemRepository;
import com.dragonappear.inha.repository.item.ManufacturerRepository;
import com.dragonappear.inha.repository.payment.PaymentRepository;
import com.dragonappear.inha.repository.selling.SellingRepository;
import com.dragonappear.inha.repository.user.UserAddressRepository;
import com.dragonappear.inha.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.dragonappear.inha.domain.item.value.CategoryName.노트북;
import static com.dragonappear.inha.domain.item.value.ManufacturerName.삼성;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class InspectionImageServiceTest {
    @Autowired BuyingRepository buyingRepository;
    @Autowired AuctionitemRepository auctionitemRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ManufacturerRepository manufacturerRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired UserRepository userRepository;
    @Autowired PaymentRepository paymentRepository;
    @Autowired UserAddressRepository userAddressRepository;
    @Autowired SellingRepository sellingRepository;
    @Autowired DealRepository dealRepository;
    @Autowired InspectionRepository inspectionRepository;
    @Autowired InspectionImageRepository inspectionImageRepository;
    @Autowired InspectionImageService inspectionImageService;

    @BeforeEach
    public void setUp() {
        User user1 = new User("name1", "nickname1", "email1", "userTel1");
        userRepository.save(user1);
        UserAddress userAddress = new UserAddress(user1, new Address("yyh","010-1111-1111","city", "street", "detail", "zipcode"));
        userAddressRepository.save(userAddress);

        Category category = new Category(노트북);
        Manufacturer manufacturer = new Manufacturer(삼성);
        categoryRepository.save(category);
        manufacturerRepository.save(manufacturer);

        Item item = new Item("맥북1", "modelNumber1", LocalDate.of(2021, 5, 21)
                ,"미스틱 실버"
                , Money.wons(10000L),
                Money.wons(20000L),category,manufacturer);
        itemRepository.save(item);

        BidAuctionitem bidAuctionitem = new BidAuctionitem(item, Money.wons(4_000_000L), LocalDateTime.now().plusHours(1));
        auctionitemRepository.save(bidAuctionitem);

        Selling selling = new Selling(user1, bidAuctionitem);
        sellingRepository.save(selling);

        Payment payment1 = new Payment("카카오페이"
                , "imp_"+ new Random().nextLong()
                ,"merchant_"+new Random().nextLong()
                ,bidAuctionitem.getPrice()
                , user1
                , bidAuctionitem);
        paymentRepository.save(payment1);

        Buying buying = new Buying(payment1);
        buyingRepository.save(buying);

        Deal deal = new Deal(buying, selling);
        dealRepository.save(deal);

        Inspection inspection = new Inspection(deal);
        inspectionRepository.save(inspection);
    }

    @Test
    public void 검수이미지_생성_테스트() throws Exception{
        //given
        Inspection inspection = inspectionRepository.findAll().get(0);
        List<InspectionImage> list = Arrays.asList(
                new InspectionImage("filename1", "fileoriname1", "fileurl1", inspection)
                , new InspectionImage("filename2", "fileoriname2", "fileurl2", inspection)
                , new InspectionImage("filename3", "fileoriname3", "fileurl3", inspection)
        );
        //when
        inspectionImageService.save(list);
        List<InspectionImage> all = inspectionImageRepository.findAll();
        //then
        assertThat(all.size()).isEqualTo(3);
        list.stream().forEach(image -> {
            assertThat(all).contains(image);
        });
    }

    @Test
    public void 검수이미지조회_이미지아이디로_테스트() throws Exception{
        //given
        Inspection inspection = inspectionRepository.findAll().get(0);
        List<InspectionImage> list = Arrays.asList(
                new InspectionImage("filename1", "fileoriname1", "fileurl1", inspection)
                , new InspectionImage("filename2", "fileoriname2", "fileurl2", inspection)
                , new InspectionImage("filename3", "fileoriname3", "fileurl3", inspection)
        );
        for (InspectionImage inspectionImage : list) {
            inspectionImageRepository.save(inspectionImage);
        }
       //when
        List<InspectionImage> all = inspectionImageService.findByInspectionId(inspection.getId());
        //then
        assertThat(all.size()).isEqualTo(3);
        list.stream().forEach(image -> {
            assertThat(all).contains(image);
        });
    }
}