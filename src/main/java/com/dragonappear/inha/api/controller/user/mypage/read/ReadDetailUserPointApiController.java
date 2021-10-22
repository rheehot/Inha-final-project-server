package com.dragonappear.inha.api.controller.user.mypage.read;

import com.dragonappear.inha.api.repository.user.UserPointQueryRepository;
import com.dragonappear.inha.service.user.UserPointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"마이페이지 유저 포인트 상세 조회 API"})
@RequiredArgsConstructor
@RestController
public class ReadDetailUserPointApiController {
    private final UserPointQueryRepository userPointQueryRepository;
    private final UserPointService userPointService;

    @ApiOperation(value = "마이페이지 유저포인트 내역 상세 조회", notes = "")
    @GetMapping("/users/mypage/points/{userId}")
    public Results getMyPageUserPointDtos(@PathVariable("userId") Long userId) {
        return getResults(userPointService.findLatestPoint(userId).getTotal().getAmount(),
                userPointQueryRepository.getMyPageUserPointDto(userId));
    }

    @Data
    static class Results<T> {
        private int count;
        private BigDecimal total;
        private List<T> items;
        @Builder
        public Results(int count, BigDecimal total, List<T> items) {
            this.count = count;
            this.total = total;
            this.items = items;
        }
    }

    public <T> Results getResults(BigDecimal total, List<T> dtos) {
        return Results.builder()
                .count(dtos.size())
                .total(total)
                .items(dtos.stream().map(dto -> (Object) dto).collect(Collectors.toList()))
                .build();
    }
}
