package com.kakao.together.event;

import com.kakao.together.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberEventListener {

    private final CacheService cacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSignUpCompleted(MemberSignupCompleteEvent event) {
        cacheService.deleteData(event.getKey());
    }
}
