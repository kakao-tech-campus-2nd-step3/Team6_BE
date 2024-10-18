package supernova.whokie.point_record.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.GlobalResponse;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.controller.dto.PointRecordRequest;
import supernova.whokie.point_record.controller.dto.PointRecordResponse;
import supernova.whokie.point_record.sevice.PointRecordReaderService;
import supernova.whokie.point_record.sevice.dto.PointRecordCommand;

@RestController
@RequestMapping("/api/point")
@AllArgsConstructor
public class PointRecordController {

    private final PointRecordReaderService pointRecordReaderService;

    @PostMapping("/purchase")
    public GlobalResponse purchasePoint(
        @RequestBody @Valid PointRecordRequest.Purchase request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

    @GetMapping("/record")
    public ResponseEntity<PagingResponse<PointRecordResponse.Record>> getChargedList(
        @Authenticate Long userId,
        @RequestParam(name = "start-date", defaultValue = "1900-01-01") LocalDate startDate,
        @RequestParam(name = "end-date", defaultValue = "2100-01-01") LocalDate endDate,
        @RequestParam(name = "option") @NotNull PointRecordOption option,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PointRecordCommand.Record command = new PointRecordCommand.Record(startDate, endDate,
            option);
        Page<PointRecordResponse.Record> response = pointRecordReaderService.getRecordsPaging(
                userId, command, pageable)
            .map(PointRecordResponse.Record::from);

        return ResponseEntity.ok().body(PagingResponse.from(response));
    }

    @PatchMapping("/earn")
    public GlobalResponse earnPoint(
        @RequestBody @Valid PointRecordRequest.Earn request
    ) {
        return GlobalResponse.builder().message("message").build();
    }

}
