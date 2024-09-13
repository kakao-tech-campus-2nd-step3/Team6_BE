package supernova.whokie.point_record.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.controller.dto.PointPurchaseRequest;
import supernova.whokie.point_record.controller.dto.PointRecordResponse;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/point")
public class PointRecordController {

    @PostMapping("/purchase")
    public GlobalResponse purchasePoint(
            @RequestBody PointPurchaseRequest.Purchase request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @GetMapping("/record")
    public PagingResponse<PointRecordResponse.Record> getChargedList(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("day") int day,
            @RequestParam("option") PointRecordOption option,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        if (option == PointRecordOption.ALL) {
            // 전체 기록
            return new PagingResponse<>(
                    List.of(
                            new PointRecordResponse.Record(1L, 100, PointRecordOption.CHARGED, LocalDate.now()),
                            new PointRecordResponse.Record(2L, 100, PointRecordOption.USED, LocalDate.now())),
                    2, 1, 1, 0
            );
        } else if (option == PointRecordOption.USED) {
            // 사용 기록
            return new PagingResponse<>(
                    List.of(
                            new PointRecordResponse.Record(1L, 100, PointRecordOption.USED, LocalDate.now()),
                            new PointRecordResponse.Record(2L, 100, PointRecordOption.USED, LocalDate.now())),
                    2, 1, 1, 0
            );
        } else if (option == PointRecordOption.CHARGED) {
            // 충전 기록
            return new PagingResponse<>(
                    List.of(
                            new PointRecordResponse.Record(1L, 100, PointRecordOption.CHARGED, LocalDate.now()),
                            new PointRecordResponse.Record(2L, 100, PointRecordOption.CHARGED, LocalDate.now())),
                    2, 1, 1, 0
            );
        } else {
            // 오류 ??
            return new PagingResponse<>(
                    List.of(),
                    0, 0, 0, 0
            );
        }
    }
}
