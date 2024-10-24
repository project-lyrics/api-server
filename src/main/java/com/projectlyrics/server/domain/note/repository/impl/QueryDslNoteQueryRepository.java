package com.projectlyrics.server.domain.note.repository.impl;

import static com.projectlyrics.server.domain.artist.entity.QArtist.artist;
import static com.projectlyrics.server.domain.bookmark.domain.QBookmark.bookmark;
import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

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
    public Slice<Note> findAllByUserId(boolean hasLyrics, Long artistId, Long userId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist, artist).fetchJoin()
                .leftJoin(note.comments).fetchJoin()
                .where(
                        QueryDslUtils.hasLyrics(hasLyrics),
                        artistId == null ? null : artist.id.eq(artistId),
                        note.publisher.id.eq(userId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllByArtistIds(boolean hasLyrics, List<Long> artistsIds, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .leftJoin(note.comments).fetchJoin()
                .where(
                        QueryDslUtils.hasLyrics(hasLyrics),
                        note.song.artist.id.in(artistsIds),
                        note.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllByArtistId(boolean hasLyrics, Long artistId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .leftJoin(note.comments).fetchJoin()
                .where(
                        QueryDslUtils.hasLyrics(hasLyrics),
                        note.song.artist.id.eq(artistId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllBySongId(boolean hasLyrics, Long songId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .leftJoin(note.comments).fetchJoin()
                .where(
                        QueryDslUtils.hasLyrics(hasLyrics),
                        note.song.id.eq(songId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, note.id)
                )
                .orderBy(note.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Note> findAllBookmarkedAndByArtistId(boolean hasLyrics, Long artistId, Long userId, Long cursorId, Pageable pageable) {
        List<Note> content = jpaQueryFactory
                .selectFrom(note)
                .leftJoin(note.lyrics).fetchJoin()
                .join(note.publisher).fetchJoin()
                .join(note.song).fetchJoin()
                .join(song.artist).fetchJoin()
                .leftJoin(note.bookmarks, bookmark).fetchJoin()
                .where(
                        bookmark.user.id.eq(userId)
                                .and(bookmark.deletedAt.isNull()),
                        QueryDslUtils.hasLyrics(hasLyrics),
                        artistId == null ? null : note.song.artist.id.eq(artistId),
                        note.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, note.id)
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
