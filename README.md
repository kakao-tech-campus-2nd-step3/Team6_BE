6주차 리뷰
## 다운
## 건
## 형진
* 현재 kakao api를 호출하는 메서드는 각 도메인의 infrastructure 패키지의 apiCaller에 위치하고 있습니다. 지금의 구조가 괜찮을까요? 다른 도메인에서 동일한 메서드가 필요한데 전체적인 구조를 변경해야 할까요?
* kakao에서 가져온 accesstoken을 redis에 저장하고 호출하는 메서드는 어떤 도메인에 저장하는게 맞을까요? redisRepository에 접근하는 기능이 공통적으로 필요한데 Global에서 정의하는게 맞을까요?
## 승욱
