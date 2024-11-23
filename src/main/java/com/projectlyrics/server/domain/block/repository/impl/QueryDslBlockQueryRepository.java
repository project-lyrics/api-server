package com.projectlyrics.server.domain.block.repository.impl;

import com.projectlyrics.server.domain.block.domain.Block;
import com.projectlyrics.server.domain.block.repository.BlockQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.projectlyrics.server.domain.block.domain.QBlock.block;

@Repository
@RequiredArgsConstructor
public class QueryDslBlockQueryRepository implements BlockQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Block> findNotDeletedByBlockerIdAndBlockedId(long blockerId, long blockedId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(block)
                        .where(
                                block.blocker.id.eq(blockerId),
                                block.blocked.id.eq(blockedId),
                                block.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
