package supernova.whokie.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.global.exception.EntityNotFoundException;
import supernova.whokie.redis.entity.PayToken;
import supernova.whokie.redis.infrastructure.repository.PayRepository;

@Service
@RequiredArgsConstructor
public class RedisPayService {

    private final PayRepository payRepository;

    public void saveTid(Long userId,String tid) {
        payRepository.save(PayToken.builder().id(userId).tid(tid).build());
    }
    public String getTid(Long userId){
        PayToken payToken = payRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 tid가 없습니다."));
        return payToken.getTid();
    }
    public void deleteByUserId(Long userId){
        payRepository.deleteById(userId);
    }

}
