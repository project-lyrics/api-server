package com.projectlyrics.server.domain.note.repository.impl;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.projectlyrics.server.domain.note.entity.QNote.*;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

@Repository
@RequiredArgsConstructor
public class QueryDslNoteQueryRepository implements NoteQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(note)
                        .leftJoin(note.lyrics).fetchJoin()
                        .join(note.publisher).fetchJoin()
                        .join(note.song).fetchJoin()
                        .join(song.artist).fetchJoin()
                        .leftJoin(note.comments).fetchJoin()
                        .where(
                                note.id.eq(id),
                                note.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Slice<Note> findAllByUserId(Long userId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .where(
                        note.publisher.id.eq(userId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllByArtistIds(List<Long> artistsIds, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .where(
                        note.song.artist.id.in(artistsIds),
                        note.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllByArtistId(Long artistId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .where(
                        note.song.artist.id.eq(artistId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllByArtistIdAndHasLyrics(Long artistId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .join(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .where(
                        note.song.artist.id.eq(artistId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public long countDraftNotesByUserId(Long userId) {
        return jpaQueryFactory
                .select(note.count())
                .from(note)
                .where(
                        note.publisher.id.eq(userId),
                        note.noteStatus.eq(NoteStatus.DRAFT),
                        note.deletedAt.isNull()
                )
                .fetchOne();
    }
}
