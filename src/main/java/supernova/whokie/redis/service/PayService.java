package supernova.whokie.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import supernova.whokie.redis.entity.PayToken;
import supernova.whokie.redis.infrastructure.repository.PayRepository;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PayRepository payRepository;

    public void saveTid(String tid) {
        payRepository.save(PayToken.builder().tid(tid).build());
    }
    public void deleteTid(String tid){
        payRepository.deleteById(tid);
    }

}
