package com.dragonappear.inha.service.inspection.fail;

import com.dragonappear.inha.domain.auctionitem.Auctionitem;
import com.dragonappear.inha.domain.buying.BidBuying;
import com.dragonappear.inha.domain.buying.Buying;
import com.dragonappear.inha.domain.deal.Deal;
import com.dragonappear.inha.domain.inspection.Inspection;
import com.dragonappear.inha.domain.inspection.fail.FailInspection;
import com.dragonappear.inha.domain.item.value.Category;
import com.dragonappear.inha.domain.item.Item;
import com.dragonappear.inha.domain.item.value.Manufacturer;
import com.dragonappear.inha.domain.item.product.Notebook;
import com.dragonappear.inha.domain.payment.Payment;
import com.dragonappear.inha.domain.selling.BidSelling;
import com.dragonappear.inha.domain.selling.Selling;
import com.dragonappear.inha.domain.user.User;
import com.dragonappear.inha.domain.user.UserAddress;
import com.dragonappear.inha.domain.value.Address;
import com.dragonappear.inha.domain.value.Money;
import com.dragonappear.inha.repository.auctionitem.AuctionitemRepository;
import com.dragonappear.inha.repository.buying.BuyingRepository;
import com.dragonappear.inha.repository.deal.DealRepository;
import com.dragonappear.inha.repository.inspection.InspectionRepository;
import com.dragonappear.inha.repository.item.CategoryRepository;
import com.dragonappear.inha.repository.item.ItemRepository;
import com.dragonappear.inha.repository.item.ManufacturerRepository;
import com.dragonappear.inha.repository.payment.PaymentRepository;
import com.dragonappear.inha.repository.selling.SellingRepository;
import com.dragonappear.inha.repository.user.UserAddressRepository;
import com.dragonappear.inha.repository.user.UserRepository;
import com.dragonappear.inha.service.inspection.InspectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

import static com.dragonappear.inha.domain.item.value.CategoryName.노트북;
import static com.dragonappear.inha.domain.item.value.ManufacturerName.삼성;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback
class FailInspectionServiceTest {
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
    @Autowired InspectionService inspectionService;


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

        Item item = new Notebook("맥북1", "modelNumber1", LocalDate.of(2021, 5, 21)
                ,"미스틱 실버"
                , Money.wons(10000L),
                category,manufacturer);
        itemRepository.save(item);

        Auctionitem auctionitem = new Auctionitem(item, Money.wons(4_000_000L));
        auctionitemRepository.save(auctionitem);

        Selling selling = new BidSelling(user1, auctionitem, LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(1));
        sellingRepository.save(selling);

        Payment payment1 = new Payment("카카오페이"
                , "imp_"+ new Random().nextLong()
                ,"merchant_"+new Random().nextLong()
                , auctionitem.getPrice()
                ,Money.wons(0L)
                , user1
                ,1L,item);
        paymentRepository.save(payment1);

        Buying buying = new BidBuying(payment1,LocalDateTime.now());
        buyingRepository.save(buying);

        Deal deal = new Deal(buying, selling);
        dealRepository.save(deal);

    }

    @Test
    public void 탈락검수_생성_테스트() throws Exception{
        //given
        Deal deal = dealRepository.findAll().get(0);
        Inspection inspection = new FailInspection(deal);
        inspectionService.save(inspection);
        //when
        FailInspection find = (FailInspection)inspectionRepository.findById(inspection.getId()).get();
        //then
        assertThat(find).isEqualTo(inspection);
        assertThat(find.getId()).isEqualTo(inspection.getId());
    }

    @Test
    public void 탈락검수_조회_테스트() throws Exception{
        //given
        Deal deal = dealRepository.findAll().get(0);
        Inspection inspection = new FailInspection(deal);
        inspectionRepository.save(inspection);
        //when
        FailInspection find = (FailInspection)inspectionService.findById(inspection.getId());
        //then
        assertThat(find).isEqualTo(inspection);
    }
}