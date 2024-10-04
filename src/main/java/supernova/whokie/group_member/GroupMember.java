package supernova.whokie.group_member;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.group.Groups;
import supernova.whokie.user.Users;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class GroupMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @NotNull
    private Groups group;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GroupRole groupRole;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GroupStatus groupStatus;

    public boolean isLeader() {
        return this.groupRole == GroupRole.LEADER;
    }

    public boolean isApproved() {
        return this.groupStatus == GroupStatus.APPROVED;
    }

    public void changeRole() {
        if (isLeader()) {
            groupRole = GroupRole.MEMBER;
        } else {
            groupRole = GroupRole.LEADER;
        }
    }

    public GroupRole getGroupRole() {
        return groupRole;
    }

    public Long getId() {
        return id;
    }
}
