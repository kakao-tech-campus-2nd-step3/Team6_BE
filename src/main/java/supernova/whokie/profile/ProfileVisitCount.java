package supernova.whokie.profile;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class ProfileVisitCount extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hostId;

    @Column(nullable = false)
    private int dailyVisited;

    @Column(nullable = false)
    private int totalVisited;

    public void visit() {
        dailyVisited += 1;
        totalVisited += 1;
    }

    public Long getHostId() {
        return hostId;
    }

    public int getDailyVisited() {
        return dailyVisited;
    }

    public int getTotalVisited() {
        return totalVisited;
    }
}
