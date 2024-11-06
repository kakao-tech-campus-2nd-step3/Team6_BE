package supernova.whokie.pointrecord.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.pointrecord.PointRecord;
import supernova.whokie.pointrecord.PointRecordOption;
import supernova.whokie.pointrecord.constants.PointConstants;
import supernova.whokie.pointrecord.event.PointRecordEventDto;
import supernova.whokie.pointrecord.infrastructure.apicaller.PayApiCaller;
import supernova.whokie.pointrecord.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.pointrecord.infrastructure.apicaller.dto.PayReadyInfoResponse;
import supernova.whokie.pointrecord.sevice.dto.PointRecordCommand;
import supernova.whokie.pointrecord.sevice.dto.PointRecordModel;
import supernova.whokie.redis.service.RedisPayService;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class PointRecordService {

    private final PointRecordWriterService pointRecordWriterService;
    private final PayApiCaller payApiCaller;
    private final UserReaderService userReaderService;
    private final RedisPayService redisPayService;
    private final ApplicationEventPublisher eventPublisher;
    private final PointRecordReaderService pointRecordReaderService;

    @Transactional
    public void recordEarnPoint(PointRecordEventDto.Earn event) {
        PointRecord pointRecord = PointRecord.create(event.userId(), event.point(), event.amount(),
                event.option(), event.message());
        pointRecordWriterService.save(pointRecord);
    }

    @Transactional
    public PointRecordModel.ReadyInfo readyPurchasePoint(Long userId, int point){
        PayReadyInfoResponse payReadyInfoResponse = payApiCaller.payReady(point, userId, PointConstants.PRODUCT_NAME_POINT);

        redisPayService.saveTid(userId, payReadyInfoResponse.tid());

        return PointRecordModel.ReadyInfo.from(payReadyInfoResponse);
    }

    @Transactional
    public void approvePurchasePoint(Long userId, String pgToken){
        Users user = userReaderService.getUserById(userId);

        // 레디스db에서 tid를 읽어오기
        String tid = redisPayService.getTid(userId);

        PayApproveInfoResponse payApproveInfoResponse = payApiCaller.payApprove(tid, userId, pgToken);

        int purchasedPoint = payApproveInfoResponse.amount().total();
        user.increasePoint(purchasedPoint);
        var event = PointRecordEventDto.Earn.toDto(userId, purchasedPoint, purchasedPoint,
                PointRecordOption.CHARGED,
                PointConstants.POINT_PURCHASE_MESSAGE);

        eventPublisher.publishEvent(event);

        // 레디스 DB에서 tid 삭제
        redisPayService.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<PointRecordModel.Record> getRecordsPaging(
            Long userId,
            PointRecordCommand.Record command,
            Pageable pageable
    ) {
        if (command.option() == PointRecordOption.ALL) {
            return pointRecordReaderService.getRecordsByUserId(
                            userId, command.startDateTime(), command.endDate().atTime(LocalTime.MAX), pageable)
                    .map(PointRecordModel.Record::from);
        }

        return pointRecordReaderService.getRecordsByUserIdAndOption(
                        userId, command.option(), command.startDateTime(), command.endDateTime(), pageable)
                .map(PointRecordModel.Record::from);
    }
}
