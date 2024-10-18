package supernova.whokie.group_member;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.global.exception.ForbiddenException;
import supernova.whokie.global.exception.InvalidEntityException;
import supernova.whokie.group.Groups;
import supernova.whokie.user.Users;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
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

    public static GroupMember CreateLeader(Users user, Groups group) {
        return GroupMember.builder()
                .user(user)
                .group(group)
                .groupRole(GroupRole.LEADER)
                .groupStatus(GroupStatus.APPROVED)
                .build();
    }

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

    public void validateLeader() {
        if (!isLeader()) {
            throw new ForbiddenException("리더만 권한을 위임할 수 있습니다.");
        }
    }

    public void validateApprovalStatus() {
        if (!isApproved()) {
            throw new InvalidEntityException("승인되지 않은 멤버입니다.");
        }
    }
}
