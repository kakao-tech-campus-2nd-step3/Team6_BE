package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;

@Service
@RequiredArgsConstructor
public class ProfileVisitWriterService {

    private final ProfileVisitCountRepository profileVisitCountRepository;

    @Transactional
    public void save(Long hostId) {
        ProfileVisitCount visitCount = ProfileVisitCount.builder()
            .hostId(hostId)
            .dailyVisited(0)
            .totalVisited(0)
            .build();
        profileVisitCountRepository.save(visitCount);
    }

}
