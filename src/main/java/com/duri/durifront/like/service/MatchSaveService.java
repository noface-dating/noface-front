package com.duri.durifront.like.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.like.entity.UserMatch;
import com.duri.durifront.like.repository.UserMatchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserMatch 저장을 REQUIRES_NEW 트랜잭션으로 분리.
 *
 * LikeService(부모 트랜잭션) 안에서 DataIntegrityViolationException이 발생하면
 * 부모 트랜잭션 전체가 rollback-only로 마킹되는 문제를 방지한다.
 * REQUIRES_NEW를 사용하면 중복 저장 시 내부 트랜잭션만 롤백되고
 * 부모 트랜잭션은 정상 유지된다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchSaveService {

    private final UserMatchRepository userMatchRepository;

    /**
     * UserMatch 저장 시도.
     *
     * @return true: 신규 매칭 저장 성공 / false: 이미 존재하는 매칭 (중복)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryCreateMatch(String fromUserId, String toUserId, UUID roomId) {
        try {
            userMatchRepository.save(UserMatch.of(fromUserId, toUserId, roomId));
            return true;
        } catch (DataIntegrityViolationException e) {
            log.warn("[중복 매칭 방지] 이미 처리된 매칭입니다. fromUser={}, toUser={}", fromUserId, toUserId);
            return false;
        }
    }
}
