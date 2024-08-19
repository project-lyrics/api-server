package com.projectlyrics.server;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

@RequiredArgsConstructor
public class NoJoinQueryDslNoteQueryRepository implements NoteQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Note> findById(Long id) {
        return Optional.empty();
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
        return null;
    }

    @Override
    public Slice<Note> findAllByArtistId(Long artistId, Long cursorId, Pageable pageable) {
        return null;
    }

    @Override
    public Slice<Note> findAllByArtistIdAndHasLyrics(Long artistId, Long cursorId, Pageable pageable) {
        return null;
    }

    @Override
    public long countDraftNotesByUserId(Long userId) {
        return 0;
    }
}
