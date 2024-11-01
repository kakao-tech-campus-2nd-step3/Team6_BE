package supernova.whokie.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;
import supernova.whokie.redis.entity.ConnectedUser;
import supernova.whokie.redis.infrastructure.repository.RedisConnectedUserRepository;

@Service
@RequiredArgsConstructor
public class RedisConnectedUserService {
    private final RedisConnectedUserRepository redisConnectedUserRepository;

    public void saveConnectedUser(Long userId, FluxSink<String> sink) {
        redisConnectedUserRepository.save(ConnectedUser.builder().userId(userId).sink(sink).build());
    }

    public void deleteConnectedUser(Long userId) {
        redisConnectedUserRepository.deleteById(userId);
    }

    public ConnectedUser getConnectedUser(Long userId) {
        return redisConnectedUserRepository.findById(userId).orElse(null);
    }
}
