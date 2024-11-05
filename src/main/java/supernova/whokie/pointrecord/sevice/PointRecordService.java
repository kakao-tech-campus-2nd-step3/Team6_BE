package supernova.whokie.pointrecord.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.pointrecord.PointRecord;
import supernova.whokie.pointrecord.event.PointRecordEventDto;

@Service
@RequiredArgsConstructor
public class PointRecordService {

    private final PointRecordWriterService pointRecordWriterService;
    private final PayApiCaller payApiCaller;
    private final UserReaderService userReaderService;
    private final PayService payService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
                event.option(), event.message());
        pointRecordWriterService.save(pointRecord);
    }
    public PointRecordModel.ReadyInfo readyPurchasePoint(Long userId, int point){
        Users user = userReaderService.getUserById(userId);

        PayReadyInfoResponse payReadyInfoResponse = payApiCaller.payReady(point, Constants.PRODUCT_NAME_POINT);

        payService.saveTid(userId, payReadyInfoResponse.tid());

        return PointRecordModel.ReadyInfo.from(payReadyInfoResponse);
    }

    @Transactional
    public void approvePurchasePoint(Long userId, String pgToken){
        Users user = userReaderService.getUserById(userId);

        // 레디스db에서 tid를 읽어오고 바로 삭제
        String tid = payService.getTid(userId);
        payService.deleteByUserId(userId);

        PayApproveInfoResponse payApproveInfoResponse = payApiCaller.payApprove(tid, pgToken);

        int purchasedPoint = payApproveInfoResponse.amount().total();
        user.increasePoint(purchasedPoint);
        var event = PointRecordEventDto.Earn.toDto(userId, purchasedPoint, purchasedPoint,
                PointRecordOption.CHARGED,
                Constants.POINT_PURCHASE_MESSAGE);

        eventPublisher.publishEvent(event);
    }
}
