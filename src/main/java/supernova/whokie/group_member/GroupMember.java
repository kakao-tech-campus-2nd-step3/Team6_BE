package supernova.whokie.group_member;


import jakarta.persistence.*;
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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups groups;

    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;

    @Enumerated(EnumType.STRING)
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
}
