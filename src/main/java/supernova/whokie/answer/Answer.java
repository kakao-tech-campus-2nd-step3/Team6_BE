package supernova.whokie.answer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import supernova.whokie.global.constants.Constants;
import supernova.whokie.global.entity.BaseTimeEntity;
import supernova.whokie.question.Question;
import supernova.whokie.user.Users;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
public class Answer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // question id

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picker_id", nullable = false)
    private Users picker; // picker id

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picked_id", nullable = false)
    private Users picked; // picked id

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private Integer hintCount;

    public static Answer create(Question question, Users picker, Users picked, Integer hintCount) {
        return Answer.builder()
                .question(question)
                .picker(picker)
                .picked(picked)
                .hintCount(hintCount)
                .build();
    }

    public void increaseHintCount() {
        this.hintCount++;
    }

    public boolean isNotPicked(Users user){
        return !(this.picked.getId().equals(user.getId()));
    }

    public String getPickerInfoByHintCount(int hintCount, boolean valid){
        if(valid){
            String gender;
            if((String.valueOf(this.picker.getGender())).equals("M")){
                gender = "남자";
            }else{
                gender = "여자";
            }
            switch(hintCount){
                case 1 -> {
                    return gender;
                }
                case 2 -> {
                    return String.valueOf(this.picker.getAge());
                }
                case 3 -> {
                    return getInitials(this.picker.getName());
                }
            }
        }
        return null;

    }

    private String getInitials(String name) {

        StringBuilder initials = new StringBuilder();

        for (char ch : name.toCharArray()) {
            if (ch >= '가' && ch <= '힣') {  // 한글인지 확인
                int unicode = ch - '가';
                int choSungIndex = unicode / (21 * 28);  // 초성 인덱스 계산
                initials.append(Constants.CHO_SUNG[choSungIndex]);
            } else {
                initials.append(ch);  // 한글이 아니면 그대로 추가
            }
        }
        return initials.toString();
    }

}
