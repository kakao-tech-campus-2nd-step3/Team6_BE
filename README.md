## 5주차 리뷰
### 권다운
- conroller에서 service 2개의 메서드를 실행시키고 싶을 때 그 2개의 메서드가 원자성을 보장하게 하기 위해서는 어떻게 짜야할지 고민입니다.
  원자성을 포기하고 contorller에서 2개의 메서드를 실행하게 짜야할지, 아니면 다른 서비스에서 해야할 역할을 직접 만들어서 구현할지 고민입니다! 예전에는 파사드패턴을 사용하여 해결했는데 구조가 너무 복잡해 질것 같아 보류했습니다. 
### 김건
```

public class AnswerService {

    private final AnswerRepository answerRepository;
    private final FriendRepository friendRepository;
    private final UsersRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PointRecordRepository pointRecordRepository;
    private final ApplicationEventPublisher eventPublisher;
    
```
이러한 레포지토리 주입이 많아지면서 서비스 클래스가 지저분해진다는 느낌을 받았는데, 앞으로도 더 많은 레포지토리 주입이 확장될수도 있다고 생각하는데 이걸 하나의 클래스로 따로 빼서 모아두는 방식으로 리팩터링하는게 좋을지 아니면 그대로 놔둬도 괜찮을지 궁금합니다. 
### 신형진
* 비동기로 수행되는 메서드들의 성공을 보장하기 위해서 비동기 로그 테이블을 생성하려고 합니다. 그러면 비동기 리스너 1개당 하나의 로그 테이블이 생성되어야 하는건가요? 그렇지 않다면 일반적으로 비동기로 처리되는 로직의 성공을 보장하기 위해서 어떤 방법을 사용하나요?

### 유승욱
- 현재 GroupMemberService에서 가독성을 높이기 위해 delegateLeader 내부의 로직의 일부분을 여러 개의 메서드로 나누어 구현하였는데, 여기서 드는 의문점이 이왕 메서드를 나누어 작성하였으면
```
 GroupMember leader = groupMemberRepository.findByUserIdAndGroupId(command.pastLeaderId(),
                command.groupId())
            .orElseThrow(() -> new EntityNotFoundException("그룹 내에 해당 유저가 존재하지 않습니다."));
```
같은 부분도 전부 메서드로 만들어 일관성을 지키는게 나은지 궁금합니다. 그리고 가독성을 챙기기 위한 메서드가 많아지니 서비스 클래스가 지저분해진다는 느낌을 받아 좋지 않은 방식인가? 라는 생각을 했는데  제가 진행한 방식이 괜찮은 방식인지 궁금합니다. 
