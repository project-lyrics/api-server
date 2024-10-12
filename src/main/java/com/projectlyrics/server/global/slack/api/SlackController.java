package com.projectlyrics.server.global.slack.api;

import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineCreateException;
import com.projectlyrics.server.domain.discipline.service.DisciplineCommandService;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.report.domain.ApprovalStatus;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.exception.ReportNotFoundException;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import com.projectlyrics.server.global.slack.exception.SlackFeedbackFailureException;
import com.projectlyrics.server.global.slack.exception.SlackInteractionFailureException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.ContentCachingRequestWrapper;


@RestController
@RequestMapping("/api/v1/slack/interactive")
@RequiredArgsConstructor
public class SlackController {

    @Value("${slack.token}")
    private String token;

    @Value("${slack.channel.id}")
    private String channelId;

    private final ReportCommandService reportCommandService;
    private final DisciplineCommandService disciplineCommandService;
    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보내기 위한 RestTemplate
    private static final Logger logger = LoggerFactory.getLogger(SlackController.class);

    @PostMapping
    public ResponseEntity<Void> handleInteractiveMessage(HttpServletRequest request) {
        try {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String payload = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
            // Slack에서 보낸 payload 디코딩 및 JSON 변환
            String decodedPayload = URLDecoder.decode(payload, StandardCharsets.UTF_8.name());
            JSONObject json = new JSONObject(decodedPayload.substring("payload=".length()));

            // 액션 정보 추출
            JSONObject action = json.getJSONArray("actions").getJSONObject(0);
            String actionId = action.getString("action_id");
            JSONObject valueJson = new JSONObject(action.getString("value"));

            // 스레드 타임스탬프 추출
            String threadTs = json.getJSONObject("container").optString("message_ts");  // Extract thread_ts if present

            // 메시지 블록 생성
            JSONArray blocks = new JSONArray();
            // actionId에 따라 처리
            if (actionId.startsWith("report")) {
                blocks = new JSONArray();
                Long reportId = valueJson.getLong("reportId");
                ApprovalStatus approvalStatus = ApprovalStatus.valueOf(valueJson.getString("approvalStatus"));
                Boolean isFalseReport = valueJson.getBoolean("isFalseReport");

                reportCommandService.resolve(ReportResolveRequest.of(approvalStatus, isFalseReport), reportId);
                // 메시지 블록에 추가
                blocks.put(new JSONObject()
                        .put("type", "section")
                        .put("text", new JSONObject()
                                .put("type", "mrkdwn")
                                .put("text", ":white_check_mark: *승인 여부*: " + approvalStatus + "  *허위 신고 여부*: " + isFalseReport)
                        )
                );

                if (actionId.contains("accept")) {
                    Long userId = valueJson.getLong("userId");
                    Long artistId = valueJson.getLong("artistId");
                    addDisciplineForAcceptance(userId, reportId, artistId, blocks);
                }
                else if (actionId.contains("fake")) {
                    Long userId = valueJson.getLong("userId");
                    Long artistId = valueJson.getLong("artistId");
                    addDisciplineForFalseReport(userId, reportId, artistId, blocks);
                }
            }
            else if (actionId.startsWith("discipline")) {

                System.out.println("-------------------------------------");
                System.out.println("json = " + json);
                System.out.println("--------------------------------------");
                blocks = new JSONArray();
                JSONArray actions = json.getJSONArray("actions");

                // 시작 날짜 가져오기
                String startDateString = json.getJSONObject("state").getJSONObject("values")
                        .getJSONObject("start").getJSONObject("discipline_start").getString("selected_date");

                // 문자열을 LocalDate로 변환
                LocalDate startDate = LocalDate.parse(startDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // 나머지 입력값 추출
                JSONArray selectedDisciplineType = json.getJSONObject("state").getJSONObject("values")
                        .getJSONObject("type").getJSONObject("discipline_type").getJSONArray("selected_options");
                JSONArray selectedDisciplineReason = json.getJSONObject("state").getJSONObject("values")
                        .getJSONObject("reason").getJSONObject("discipline_reason").getJSONArray("selected_options");
                String content = json.getJSONObject("state").getJSONObject("values")
                        .getJSONObject("content").getJSONObject("discipline_content").getString("value");

                // 필요한 값으로 변수 초기화
                DisciplineType disciplineType = DisciplineType.valueOf(selectedDisciplineType.getJSONObject(0).getString("value"));
                DisciplineReason disciplineReason = DisciplineReason.valueOf(selectedDisciplineReason.getJSONObject(0).getString("value"));

                Long userId = valueJson.getLong("userId");
                Long artistId = valueJson.getLong("artistId");
                Long reportId = valueJson.getLong("reportId");
                LocalDateTime startTime = startDate.atStartOfDay();
                String notificationContent = content;

                System.out.println("------------------------------------");
                System.out.println("userId = " + userId);
                System.out.println("artistId = " + artistId);
                System.out.println("reportId = " + reportId);
                System.out.println("disciplineReason = " + disciplineReason);
                System.out.println("disciplineType = " + disciplineType);
                System.out.println("startTime = " + startTime);
                System.out.println("notificationContent = " + notificationContent);
                System.out.println("-------------------------------------");

                // 데이터 검증
                if (userId == null || artistId == null || reportId == null || disciplineReason == null || disciplineType == null || startTime == null || notificationContent == null) {
                    throw new InvalidDisciplineCreateException();
                }

                Discipline discipline = disciplineCommandService.create(DisciplineCreateRequest.of(userId, artistId, disciplineReason, disciplineType, startTime, notificationContent));

                // 조치가 들어오면 (허위 신고가 아닌 건에 한해) 해당 노트/댓글 삭제
                if (disciplineReason != DisciplineReason.FAKE_REPORT) {
                    reportCommandService.deleteReportedTarget(reportId);
                }

                blocks.put(new JSONObject()
                        .put("type", "section")
                        .put("text", new JSONObject()
                                .put("type", "mrkdwn")
                                .put("text", ":mega: *사용자*: " + userId + "에 대한 조치가 완료되었습니다.*: \n*조치 사유:* " + disciplineReason.getDescription() + "\n*조치 내용:* " + disciplineType.getDescription() + "\n*조치 기간: *" + discipline.getStartTime() + " ~ " + discipline.getEndTime())
                        )
                );
            }

            sendFeedbackToSlack(blocks, threadTs);

            return ResponseEntity.ok().build();
        } catch (ReportNotFoundException |
                 InvalidNoteDeletionException | InvalidCommentDeletionException | InvalidDisciplineCreateException e) {
            throw e;
        } catch (JSONException e) {
            System.out.println("JSON Parsing Error: " + e.getMessage());
            throw new SlackInteractionFailureException();
        }
        catch (Exception e) {
            logger.error("Failed to send message to Slack", e);
            System.out.println(e);
            throw new SlackInteractionFailureException();
        }
    }

    private void sendFeedbackToSlack(JSONArray blocks, String threadTs) {
        try {
            // 최상위 JSONObject 생성
            JSONObject responseJson = new JSONObject();
            responseJson.put("channel", channelId);  // 슬랙 채널 ID 설정
            responseJson.put("blocks", blocks);  // blocks는 JSONArray로 전달

            // 스레드에 답장할 경우 thread_ts 포함
            if (threadTs != null && !threadTs.isEmpty()) {
                responseJson.put("thread_ts", threadTs);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);  // 슬랙 봇 토큰

            HttpEntity<String> entity = new HttpEntity<>(responseJson.toString(), headers);
            String slackApiUrl = "https://slack.com/api/chat.postMessage";

            restTemplate.postForEntity(slackApiUrl, entity, String.class);
        } catch (Exception e) {
            System.out.println(e);
            throw new SlackFeedbackFailureException();
        }
    }

    private void addDisciplineForAcceptance(Long userId, Long reportId, Long artistId, JSONArray blocks) {
        JSONArray disciplineReason = new JSONArray()
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.INAPPROPRIATE_CONTENT.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.INAPPROPRIATE_CONTENT.name())
                )
                .put(new JSONObject()
                    .put("text", new JSONObject()
                            .put("type", "plain_text")
                            .put("text", DisciplineReason.DEFAMATION.getDescription())
                            .put("emoji", true)
                    )
                    .put("value", DisciplineReason.DEFAMATION.name())
                )
                .put(new JSONObject()
                    .put("text", new JSONObject()
                            .put("type", "plain_text")
                            .put("text", DisciplineReason.EXPLICIT_CONTENT.getDescription())
                            .put("emoji", true)
                    )
                    .put("value", DisciplineReason.EXPLICIT_CONTENT.name())
                )
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.COMMERCIAL_ADS.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.COMMERCIAL_ADS.name())
                )
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.INFO_DISCLOSURE.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.INFO_DISCLOSURE.name())
                )
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.POLITICAL_RELIGIOUS.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.POLITICAL_RELIGIOUS.name())
                )
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.OTHER.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.OTHER.name())
                );
        addDiscipline(userId, reportId, artistId, blocks, disciplineReason);
    }

    private void addDisciplineForFalseReport(Long userId, Long reportId, Long artistId, JSONArray blocks) {
        JSONArray disciplineReason = new JSONArray()
                .put(new JSONObject()
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", DisciplineReason.FAKE_REPORT.getDescription())
                                .put("emoji", true)
                        )
                        .put("value", DisciplineReason.FAKE_REPORT.name())
                );
        addDiscipline(userId, reportId, artistId, blocks, disciplineReason);
    }

    private void addDiscipline(Long userId, Long reportId, Long artistId, JSONArray blocks, JSONArray disciplineReason) {
        // 구분선 추가
        blocks.put(new JSONObject()
                .put("type", "divider")
        );

        // 제목 추가
        blocks.put(new JSONObject()
                .put("type", "section")
                .put("text", new JSONObject()
                        .put("type", "mrkdwn")
                        .put("text", "*조치 관련 설정*")  // 제목에 대한 마크다운 형식
                )
        );

        //시작 날짜 선택 폼 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("block_id", "start")
                .put("element", new JSONObject()
                        .put("type", "datepicker")  // 슬랙에서 날짜 선택을 할 수 있는 Date Picker 사용
                        .put("initial_date", LocalDate.now().toString())  // 기본 값은 오늘 날짜
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "시작 날짜를 선택하세요")
                                .put("emoji", true)
                        )
                        .put("action_id", "discipline_start")  // 이 action_id로 선택된 날짜를 처리
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":calendar: 조치 시작 날짜")
                        .put("emoji", true)
                )
        );

        // 조치 선택 폼 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("block_id", "type")
                .put("element", new JSONObject()
                        .put("type", "multi_static_select")
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "옵션을 선택하세요")
                                .put("emoji", true)
                        )
                        .put("options", new JSONArray()
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.WARNING.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.WARNING.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_3DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_3DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_14DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_14DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_30DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_30DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ARTIST_3MONTHS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ARTIST_3MONTHS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_3DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_3DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_14DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_14DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_30DAYS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_30DAYS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.ALL_3MONTHS.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.ALL_3MONTHS.name())
                                )
                                .put(new JSONObject()
                                        .put("text", new JSONObject()
                                                .put("type", "plain_text")
                                                .put("text", DisciplineType.FORCED_WITHDRAWAL.getDescription())
                                                .put("emoji", true)
                                        )
                                        .put("value", DisciplineType.FORCED_WITHDRAWAL.name())
                                )
                        )
                        .put("action_id", "discipline_type")
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":pencil2: 사용자 " + userId + "에 대한 조치")
                        .put("emoji", true)
                )
        );

        // 징계 이유 선택 폼 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("block_id", "reason")
                .put("element", new JSONObject()
                        .put("type", "multi_static_select")
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "이유를 선택하세요")
                                .put("emoji", true)
                        )
                        .put("options", disciplineReason)
                        .put("action_id", "discipline_reason")
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":closed_book: 사용자 " + userId + "에 대한 조치 이유")
                        .put("emoji", true)
                )
        );

        // 사용자 알림 메세지 입력 폼 추가
        blocks.put(new JSONObject()
                .put("type", "input")
                .put("block_id", "content")
                .put("element", new JSONObject()
                        .put("type", "plain_text_input")
                        .put("action_id", "discipline_content")
                        .put("placeholder", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "{시작시간}, {종료시간}을 포함해서 알림 메세지를 작성해보세요 (단 영구 탈퇴와 경고의 경우는 제외)")
                                .put("emoji", true)
                        )
                )
                .put("label", new JSONObject()
                        .put("type", "plain_text")
                        .put("text", ":memo:사용자에 전달될 조치 알림 메세지")
                        .put("emoji", true)
                )
        );

        // 제출 버튼 추가
        blocks.put(new JSONObject()
                .put("type", "section")
                .put("text", new JSONObject()
                        .put("type", "mrkdwn")
                        .put("text", "사용자에 대한 조치 및 이유를 선택하고 제출해주세요.")
                )
                .put("accessory", new JSONObject()
                        .put("type", "button")
                        .put("text", new JSONObject()
                                .put("type", "plain_text")
                                .put("text", "제출")
                                .put("emoji", true)
                        )
                        .put("value", new JSONObject() // userId와 artistId를 value에 담음
                                .put("userId", userId)
                                .put("reportId", reportId)
                                .put("artistId", artistId)
                                .toString() // JSON 객체를 문자열로 변환
                        )
                        .put("action_id", "discipline_submit")
                )
        );
    }
}
