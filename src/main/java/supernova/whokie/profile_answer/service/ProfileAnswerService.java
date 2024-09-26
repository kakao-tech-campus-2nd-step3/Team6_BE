package supernova.whokie.profile_answer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import supernova.whokie.profile_answer.ProfileAnswer;
import supernova.whokie.profile_answer.infrastructure.repository.ProfileAnswerRepository;
import supernova.whokie.profile_answer.service.dto.ProfileAnswerModel;

@Service
@RequiredArgsConstructor
public class ProfileAnswerService {

    private final ProfileAnswerRepository profileAnswerRepository;

    public Page<ProfileAnswerModel.Info> getProfileAnswers(Long userId, Pageable pageable) {
        Page<ProfileAnswer> profileAnswers = profileAnswerRepository.findAllByUserId(userId,
            pageable);
        return profileAnswers.map(ProfileAnswerModel.Info::from);
    }
}
