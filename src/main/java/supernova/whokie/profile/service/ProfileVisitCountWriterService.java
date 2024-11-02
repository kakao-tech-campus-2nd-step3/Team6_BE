package supernova.whokie.profile.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfileVisitCountWriterService {
    private ProfileVisitCountRepository profileVisitCountRepository;

    @Transactional
    public void saveAll(List<ProfileVisitCount> list) {
        profileVisitCountRepository.saveAll(list);
    }
}
