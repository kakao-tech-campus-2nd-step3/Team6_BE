package supernova.whokie.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.profile.ProfileVisitor;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileVisitorWriterService {
    private final ProfileVisitorRepository profileVisitorRepository;

    @Transactional
    public void saveAll(List<ProfileVisitor> list) {
        profileVisitorRepository.saveAll(list);
    }
}
