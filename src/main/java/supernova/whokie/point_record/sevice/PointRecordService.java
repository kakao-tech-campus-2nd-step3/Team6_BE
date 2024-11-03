package supernova.whokie.point_record.sevice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.point_record.infrastructure.apicaller.PayApiCaller;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayReadyInfoResponse;
import supernova.whokie.redis.service.PayService;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class PointRecordService {

    private final PayApiCaller payApiCaller;
    private final UserReaderService userReaderService;
    private final PayService payService;

    public PayReadyInfoResponse readyPurchasePoint(Long userId, int point){
        Users user = userReaderService.getUserById(userId);

        PayReadyInfoResponse payReadyInfoResponse = payApiCaller.payReady(point);

        payService.saveTid(userId, payReadyInfoResponse.tid());

        return payReadyInfoResponse;
    }

    @Transactional
    public PayApproveInfoResponse approvePurchasePoint(Long userId, String pgToken){
        Users user = userReaderService.getUserById(userId);

        // 레디스db에서 tid를 읽어오고 바로 삭제
        String tid = payService.getTid(userId);
        payService.deleteByUserId(userId);

        PayApproveInfoResponse payApproveInfoResponse = payApiCaller.payApprove(tid, pgToken);

        user.increasePoint(payApproveInfoResponse.amount().total());

        return payApproveInfoResponse;
    }
}
