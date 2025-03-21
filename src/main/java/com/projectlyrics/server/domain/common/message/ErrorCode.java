package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "00000", "Invalid request."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "00001", "일부 필드가 잘못된 값으로 입력되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00002", "서버 내부에 오류가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "00003", "Not Found"),
    NULL_FIELD(HttpStatus.BAD_REQUEST, "00004", "Some field is missing."),
    EMPTY_FIELD(HttpStatus.BAD_REQUEST, "00005", "Some field is empty."),
    INVALID_URL_PREFIX(HttpStatus.BAD_REQUEST, "00006", "URL should start with http:// or https://."),
    //RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "00007", "Resource not found."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "00008", "이메일 형식이 유효하지 않습니다."),
    UPDATE_REQUIRED(HttpStatus.UPGRADE_REQUIRED, "00009", "최신 버전이 아니므로 업데이트가 필요합니다."),
    VERSION_HEADER_NOT_FOUND(HttpStatus.BAD_REQUEST, "00010", "클라이언트 버전 헤더가 누락되었습니다."),
    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "00011", "필수 헤더가 누락되었습니다."),

    // Auth
    USER_FORCED_WITHDRAWAL(HttpStatus.FORBIDDEN, "01000", "강제 탈퇴 이력이 있는 유저입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "01001", "토큰이 만료되었습니다."),
    // WRONG_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "01002", "잘못된 토큰 형식으로 입력되었습니다."),
    // UNSUPPORTED_AUTH_PROVIDER(HttpStatus.BAD_REQUEST, "01003", "It is unsupported authentication provider"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "01004", "토큰이 유효하지 않습니다."),
    INVALID_KEY(HttpStatus.UNAUTHORIZED, "01005", "The key is not valid."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "01006", "이미 존재하는 유저입니다."),
    NOT_AGREE_TO_TERMS(HttpStatus.BAD_REQUEST, "01007", "약관에 동의하지 않았습니다."),
    NO_TOKEN_PROVIDED(HttpStatus.UNAUTHORIZED, "01008", "인증 토큰이 존재하지 않습니다."),
    INVALID_TOKEN_PREFIX(HttpStatus.BAD_REQUEST, "01009", "Bearer 인증 형식이 아닙니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "01010", "유효하지 않은 소셜 인증 토큰입니다."),
    AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "01011", "해당 인증 정보를 찾을 수 없습니다."),
    NOT_REGISTERED_DEVICE(HttpStatus.NOT_FOUND, "01012", "타 기기에서 로그인했거나 등록되지 않은 기기입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "02000", "해당 유저가 존재하지 않습니다."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "02001", "The nickname must be at least one character long and include letters, numbers, or a combination of both."),
    INVALID_AGE(HttpStatus.BAD_REQUEST, "02002", "The age must 14 years or older."),
    INVALID_PROFILE_CHARACTER(HttpStatus.BAD_REQUEST, "02003", "올바르지 않은 프로필 캐릭터입니다."),
    FAILED_TO_UPDATE_PROFILE(HttpStatus.BAD_REQUEST, "02004", "사용자 프로필을 수정하는 데 실패했습니다."),
    UNCHANGED_USERNAME(HttpStatus.BAD_REQUEST, "02005", "동일한 닉네임으로 프로필을 수정할 수 없습니다."),

    // Artist
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "03000", "해당 아티스트를 조회할 수 없습니다."),
    ARTIST_UPDATE_NOT_VALID(HttpStatus.BAD_REQUEST, "03001", "The data to be updated failed validation."),

    // Record
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "04000", "The record data could not be found."),

    // Note
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "05000", "해당 노트를 조회할 수 없습니다."),
    INVALID_NOTE_BACKGROUND(HttpStatus.BAD_REQUEST, "05001", "올바르지 않은 노트 배경입니다."),
    INVALID_NOTE_STATUS(HttpStatus.BAD_REQUEST, "05002", "올바르지 않은 노트 상태입니다."),
    INVALID_NOTE_DELETION(HttpStatus.BAD_REQUEST, "05003", "해당 노트를 삭제할 수 없습니다."),
    INVALID_NOTE_UPDATE(HttpStatus.BAD_REQUEST, "05004", "해당 노트를 수정할 수 없습니다."),
    TOO_MANY_DRAFT_NOTE(HttpStatus.BAD_REQUEST, "05005", "임시저장 노트의 개수가 초과되었습니다."),

    // Song
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "06000", "해당 노래를 조회할 수 없습니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "07000", "해당 댓글을 조회할 수 없습니다."),
    INVALID_COMMENT_UPDATE(HttpStatus.BAD_REQUEST, "07001", "해당 댓글을 수정할 수 없습니다."),
    INVALID_COMMENT_DELETION(HttpStatus.BAD_REQUEST, "07002", "해당 댓글을 삭제할 수 없습니다."),

    // Like
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "08000", "해당 좋아요를 조회할 수 없습니다."),
    LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "08001", "이미 좋아요를 누른 상태입니다."),

    // Favorite Artist
    FAVORITE_ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "09000", "해당 즐겨찾는 아티스트를 조회할 수 없습니다."),
    FAVORITE_ARTIST_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "09001", "이미 즐겨찾는 아티스트를 추가한 상태입니다."),

    // Bookmark
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "10000", "해당 북마크를 조회할 수 없습니다."),
    BOOKMARK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "10001", "이미 북마크를 추가한 상태입니다."),

    // Notification
    UNKNOWN_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "11000", "알 수 없는 알림 타입입니다."),
    NOTIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "11001", "알림 전송에 실패했습니다."),
    NOTIFICATION_RECEIVER_UNMATCH(HttpStatus.BAD_REQUEST, "11002", "해당 알림의 수신자가 아닙니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "11003", "해당 알림을 조회할 수 없습니다."),

    // Report
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "12000", "해당 신고를 조회할 수 없습니다."),
    REPORT_TARGET_CONFLICT(HttpStatus.BAD_REQUEST, "12001", "신고 대상은 Note와 Comment 중 하나여야 합니다."),
    REPORT_TARGET_MISSING(HttpStatus.BAD_REQUEST, "12002", "신고 대상(Note 또는 Comment)가 필요합니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "12003", "이미 신고가 완료된 대상입니다."),

    // Discipline
    DISCIPLINE_NOT_FOUND(HttpStatus.NOT_FOUND, "13000", "해당 조치를 조회할 수 없습니다."),
    INVALID_DISCIPLINE_CREATE(HttpStatus.BAD_REQUEST, "13001", "조치 생성에 실패했습니다."),
    INVALID_DISCIPLINE_ACTION(HttpStatus.FORBIDDEN, "13002", "징계로 인해 해당 작업을 수행할 수 없습니다."),

    // Slack
    SLACK_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "14000", "슬랙 메세지 전송에 실패했습니다."),
    SLACK_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "14001", "슬랙 메세지 전송 중 에러가 발생했습니다."),
    SLACK_INTERACTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "14002", "슬랙 상호작용 인터렉션에 문제가 발생했습니다."),
    SLACK_FEEDBACK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"14003", "슬랙에 피드백 메세지 제공에 실패했습니다."),
    SLACK_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "14004", "서버에서 받아들일 수 없는 요청이 슬랙을 통해 전달되었습니다."),
    FAILED_TO_READ_SLACK_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "14005", "서버에서 슬랙의 요청을 제대로 읽는 데 실패했습니다."),
    FALIED_TO_MATCH_SLACK_SIGNATURE_EXCEPTION(HttpStatus.BAD_REQUEST, "14006", "슬랙 요청의 시그니쳐가 잘못된 값입니다."),

    // Block
    BLOCK_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "15000", "이미 차단을 추가한 상태입니다."),
    BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "15001", "해당 차단을 조회할 수 없습니다."),

    // Search
    FAILED_TO_INDEX(HttpStatus.INTERNAL_SERVER_ERROR, "16000", "데이터를 인덱싱하는 데 실패했습니다."),
    FAILED_TO_SEARCH(HttpStatus.INTERNAL_SERVER_ERROR, "16001", "데이터를 검색하는 데 실패했습니다."),

    // Event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "17000", "해당 이벤트를 조회할 수 없습니다."),
    EVENT_REFUSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "17001", "해당 이벤트 수신 거절 내역을 조회할 수 없습니다."),

    //Banner
    BANNER_NOT_FOUND(HttpStatus.NOT_FOUND, "18000", "해당 배너를 조회할 수 없습니다."),
    ;

    private final HttpStatus responseStatus;
    private final String errorCode;
    private final String errorMessage;
}
