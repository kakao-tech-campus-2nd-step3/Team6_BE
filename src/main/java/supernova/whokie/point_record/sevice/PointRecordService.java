package supernova.whokie.point_record.sevice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.point_record.infrastructure.apicaller.PayApiCaller;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.point_record.infrastructure.apicaller.dto.PayReadyInfoResponse;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

@Service
@RequiredArgsConstructor
public class PointRecordService {

    private final PayApiCaller payApiCaller;
    private final UserReaderService userReaderService;

    public PayReadyInfoResponse readyPurchasePoint(Long userId, int point){
        Users user = userReaderService.getUserById(userId);

        return payApiCaller.payReady(point);
    }

    @Transactional
    public PayApproveInfoResponse approvePurchasePoint(Long userId, String tid, String pgToken){
        PayApproveInfoResponse payApproveInfoResponse = payApiCaller.payApprove(tid, pgToken);

        Users user = userReaderService.getUserById(userId);
        user.increasePoint(payApproveInfoResponse.amount().total());
        System.out.println("Point : " + payApproveInfoResponse.amount().total());

        return payApproveInfoResponse;
    }
}
