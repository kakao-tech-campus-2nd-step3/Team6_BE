package supernova.whokie.point_record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer point;

    private Integer amount;

    @Column(name = "`option`") // mysql 예약어로 인해 백틱으로 감쌈
    @Enumerated(EnumType.STRING)
    private PointRecordOption option;

    private String description;

    public static PointRecord create(Long userId, Integer point, Integer amount,
                                     PointRecordOption option, String description) {
        return PointRecord.builder()
                .userId(userId)
                .point(point)
                .amount(amount)
                .option(option)
                .description(description).build();
    }
}
