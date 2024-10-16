package supernova.whokie.profile.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.ProfileVisitor;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitorRepository;
import supernova.whokie.profile.service.dto.ProfileModel;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProfileVisitService {
    private ProfileVisitorRepository profileVisitorRepository;
    private ProfileVisitCountRepository profileVisitCountRepository;

    @Transactional
    public ProfileModel.Visited visitProfile(Long hostId, String visitorIp) {
        if (!checkVisited(hostId, visitorIp)) {
            // 방문자 증가
            updateVisitCount(hostId);
        }
        // 방문자 로그 기록
        saveVisitor(hostId, visitorIp);

        // 방문자 수 반환
        ProfileVisitCount visitCount = findProfileVisitCountByHostId(hostId);
        return ProfileModel.Visited.from(visitCount);
    }

    @Transactional(readOnly = true)
    public boolean checkVisited(Long hostId, String visitorIp) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay(); // 오늘 00:00
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999); // 오늘 23:59:59.999999

        return profileVisitorRepository.existsByHostIdAndVisitorIpAndModifiedAtBetween(hostId, visitorIp, startOfDay, endOfDay);
    }

    @Transactional
    public void saveVisitor(Long hostId, String visitorIp) {
        ProfileVisitor visitor = ProfileVisitor.builder().hostId(hostId).visitorIp(visitorIp).build();
        profileVisitorRepository.save(visitor);
    }

    @Transactional
    public void saveVisitCount(Long hostId) {
        ProfileVisitCount visitCount = ProfileVisitCount.builder().hostId(hostId).dailyVisited(0).totalVisited(0).build();
        profileVisitCountRepository.save(visitCount);
    }

    @Transactional
    public void updateVisitCount(Long hostId) {
        ProfileVisitCount visitCount = findProfileVisitCountByHostId(hostId);
        visitCount.visit();
    }

    @Transactional
    public ProfileVisitCount findProfileVisitCountByHostId(Long hostId) {
        return profileVisitCountRepository.findByHostId(hostId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 프로필입니다."));
    }
}
