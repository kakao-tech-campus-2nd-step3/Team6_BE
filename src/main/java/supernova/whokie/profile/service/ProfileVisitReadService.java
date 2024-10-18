package supernova.whokie.profile.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import supernova.whokie.global.constants.MessageConstants;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.profile.ProfileVisitCount;
import supernova.whokie.profile.infrastructure.repository.ProfileVisitCountRepository;
import supernova.whokie.redis.service.dto.RedisCommand;

@Service
@AllArgsConstructor
public class ProfileVisitReadService {
    private ProfileVisitCountRepository profileVisitCountRepository;


    @Transactional(readOnly = true)
    public RedisCommand.Visited findVisitCountById(Long hostId) {
        ProfileVisitCount visitCount = profileVisitCountRepository.findByHostId(hostId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.USER_NOT_FOUND_MESSAGE));
        return RedisCommand.Visited.from(visitCount);
    }

}
