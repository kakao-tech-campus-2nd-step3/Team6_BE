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
public class ProfileVisitor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String visitorIp;

    @Column(nullable = false)
    private Long hostId;

    public Long getId() {
        return id;
    }

    public Long getHostId() {
        return hostId;
    }

    public String getVisitorIp() {
        return visitorIp;
    }
}
