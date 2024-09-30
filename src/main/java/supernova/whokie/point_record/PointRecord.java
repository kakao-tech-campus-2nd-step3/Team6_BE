package supernova.whokie.point_record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;
import supernova.whokie.global.entity.BaseTimeEntity;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointRecord extends BaseTimeEntity {

    @Id
    private Long id;

    private Long userId;

    private Integer point;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PointRecordOption option;

    private String description;

    public static PointRecord create(Long userId, Integer point, PointRecordOption option, String description) {
        return PointRecord.builder()
                .userId(userId)
                .point(point)
                .option(option)
                .description(description).build();
    }
}
