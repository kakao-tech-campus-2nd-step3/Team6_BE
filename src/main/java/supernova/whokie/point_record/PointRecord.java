package supernova.whokie.point_record;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.BaseTimeEntity;

@Builder
@Entity
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
}
