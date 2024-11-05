package supernova.whokie.point_record.sevice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import supernova.whokie.pointrecord.constants.PointConstants;
import supernova.whokie.pointrecord.infrastructure.apicaller.PayApiCaller;
import supernova.whokie.pointrecord.infrastructure.apicaller.dto.PayApproveInfoResponse;
import supernova.whokie.pointrecord.infrastructure.apicaller.dto.PayReadyInfoResponse;
import supernova.whokie.pointrecord.sevice.PointRecordService;
import supernova.whokie.pointrecord.sevice.dto.PointRecordModel;
import supernova.whokie.redis.service.PayService;
import supernova.whokie.user.Gender;
import supernova.whokie.user.Role;
import supernova.whokie.user.Users;
import supernova.whokie.user.service.UserReaderService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointRecordServiceTest {

    @Mock
    private UserReaderService userReaderService;

    @Mock
    private PayApiCaller payApiCaller;

    @Mock
    private PayService payService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PointRecordService pointService;

    @Test
    @DisplayName("ReadyPurchase 테스트")
    public void testReadyPurchasePoint() {
        // given
        Long userId = 1L;
        int point = 100;
        Users user = createUser();
        PayReadyInfoResponse mockResponse = new PayReadyInfoResponse("test-tid", "testUrl");

        when(userReaderService.getUserById(userId)).thenReturn(user);
        when(payApiCaller.payReady(point, PointConstants.PRODUCT_NAME_POINT)).thenReturn(mockResponse);

        // when
        PointRecordModel.ReadyInfo result = pointService.readyPurchasePoint(userId, point);

        // then
        verify(payService).saveTid(userId, "test-tid");
        assertThat(result).isNotNull();
        assertThat(result.nextRedirectPcUrl()).isEqualTo("testUrl");
    }

    @Test
    @DisplayName("ApprovePurchase 테스트")
    public void testApprovePurchasePoint() {
        // given
        Long userId = 1L;
        String pgToken = "test-pg-token";
        Users user = createUser();

        PayApproveInfoResponse.Amount mockAmount = new PayApproveInfoResponse.Amount(
                1000, 0, 0, 0, 0, 0);
        PayApproveInfoResponse mockApproveResponse = new PayApproveInfoResponse(
                "test-aid", "test-tid", "test-cid", "order-123", "user-123",
                "item-name", 1, mockAmount, "CARD", "2024-11-05T10:00:00", "2024-11-05T10:05:00"
        );

        when(userReaderService.getUserById(userId)).thenReturn(user);
        when(payService.getTid(userId)).thenReturn("test-tid");
        when(payApiCaller.payApprove("test-tid", pgToken)).thenReturn(mockApproveResponse);

        // when
        pointService.approvePurchasePoint(userId, pgToken);

        // then
        verify(payService).deleteByUserId(userId);
        assertThat(user.getPoint()).isEqualTo(1100);
    }

    public Users createUser(){
        return Users.builder()
                .name("Test User 1")
                .email("test@example.com")
                .point(100)
                .age(20)
                .kakaoId(1234567890L)
                .gender(Gender.M)
                .imageUrl("default_image_url.jpg")
                .role(Role.USER)
                .build();
    }
}