package supernova.whokie.point_record.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import supernova.whokie.global.annotation.Authenticate;
import supernova.whokie.global.dto.PagingResponse;
import supernova.whokie.point_record.PointRecordOption;
import supernova.whokie.point_record.controller.dto.PointRecordRequest;
import supernova.whokie.point_record.controller.dto.PointRecordResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayReadyInfoResponse;
import supernova.whokie.point_record.sevice.PointRecordReaderService;
import supernova.whokie.point_record.sevice.PointRecordService;
import supernova.whokie.point_record.sevice.dto.PointRecordCommand;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/point")
@AllArgsConstructor
@Validated
public class PointRecordController {

    private final PointRecordService pointRecordService;
    private final PointRecordReaderService pointRecordReaderService;

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchasePoint(
            @Authenticate Long userId,
            @RequestBody @Valid PointRecordRequest.Purchase request,
            HttpSession session
    ) {
        PayReadyInfoResponse payReadyInfoResponse = pointRecordService.purchasePoint(request.point());

        session.setAttribute("tid", payReadyInfoResponse.tid());
        session.setAttribute("userId", userId);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("location", payReadyInfoResponse.nextRedirectPcUrl())
                .build();
    }

    @GetMapping("/purchase/approve")
    public PayApproveInfoResponse payApproved(
            @RequestParam("pg_token") String pgToken,
            HttpSession session
    ) {
        String tid = (String) session.getAttribute("tid");
        Long userId = (Long) session.getAttribute("userId");

        PayApproveInfoResponse payApproveInfoResponse = pointRecordService.approvePoint(userId, tid, pgToken);
        return payApproveInfoResponse;
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
}
