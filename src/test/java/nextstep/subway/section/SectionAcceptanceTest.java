package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.LineAcceptanceTemplate.id_추출;
import static nextstep.subway.line.LineAcceptanceTest.LineAcceptanceTemplate.지하철_노선_생성;
import static nextstep.subway.section.SectionAcceptanceTest.SectionAcceptanceTemplate.*;
import static nextstep.subway.station.StationAcceptanceTest.StationAcceptanceTemplate.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능 구현")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Long 양재역_id;
    private Long 정자역_id;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        양재역_id = id_추출(지하철역_생성("양재역"));
        정자역_id = id_추출(지하철역_생성("정자역"));

        LineRequest 노선 = new LineRequest("신분당선", "red", 양재역_id, 정자역_id, 10);
        신분당선 = 지하철_노선_생성(노선).as(LineResponse.class);
    }

    /**
     * Given 새로운 구간에 대한 지하철역을 생성하고
     * When 기존에 존재하는 지하철 노선에 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 노선에_구간을_등록한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 역 사이에 새로운 역을 등록하고
     * When 기존에 존재하는 지하철 노선에 새로운 구간을 등록하면
     * Then 새로운 구간 길이를 뺀 나머지 구간 길이가 새롭게 추가된 역과의 길이로 설정된다.
     */
    @Test
    void 역_사이에_새로운_구간을_등록한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        신규_구간과_분리된_구간_길이의_합이_원래_구간의_길이와_같다(신규_구간이_등록된_노선, 10);
    }

    /**
     * Given 새로운 역을 상행 종점으로 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 새로운_역을_상행_종점으로_등록한다() {
        // given
        Long 강남역_id = id_추출(지하철역_생성("강남역"));
        SectionRequest 신규_구간 = new SectionRequest(강남역_id, 양재역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 새로운 역을 하행 종점으로 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    void 새로운_역을_하행_종점으로_등록한다() {
        // given
        Long 광교역_id = id_추출(지하철역_생성("광교역"));
        SectionRequest 신규_구간 = new SectionRequest(정자역_id, 광교역_id, 3);

        // when
        ExtractableResponse<Response> 신규_구간이_등록된_노선 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_정상_등록된다(신규_구간이_등록된_노선);
    }

    /**
     * Given 기존 역 사이 길이보다 크거나 같은 새로운 역을 등록하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록되지 않는다.
     */
    @Test
    void 기존_역_사이_길이보다_크거나_같은_구간은_등록할_수_없다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 10);

        // when
        ExtractableResponse<Response> 신규_구간이_등록되지_않음 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_등록되지_않는다(신규_구간이_등록되지_않음);
    }

    /**
     * Given 기존에 등록된 상행역과 하행역을 생성하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록되지 않는다.
     */
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있으면_구간을_등록할_수_없다() {
        // given
        SectionRequest 신규_구간 = new SectionRequest(양재역_id, 정자역_id, 10);

        // when
        ExtractableResponse<Response> 신규_구간이_등록되지_않음 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_등록되지_않는다(신규_구간이_등록되지_않음);
    }

    /**
     * Given 노선에 포함되지 않은 역을 생성하고
     * When 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록되지 않는다.
     */
    @Test
    void 상행역과_하행역_둘_중_하나도_포함되어_있지_않으면_구간을_등록할_수_없다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        Long 청계산입구역_id = id_추출(지하철역_생성("청계산입구역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 청계산입구역_id, 10);

        // when
        ExtractableResponse<Response> 신규_구간이_등록되지_않음 = 노선에_신규_구간을_등록(신분당선, 신규_구간);

        // then
        노선에_신규_구간이_등록되지_않는다(신규_구간이_등록되지_않음);
    }

    /**
     * Given 2개의 구간을 생성하고
     * When 구간의 종점을 삭제하면
     * Then 지하철 노선에 종점이 삭제된다.
     */
    @Test
    void 구간의_종점을_삭제한다() {
        // given
        Long 광교역_id = id_추출(지하철역_생성("광교역"));
        SectionRequest 신규_구간 = new SectionRequest(정자역_id, 광교역_id, 10);
        노선에_신규_구간을_등록(신분당선, 신규_구간);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(신분당선, 광교역_id);

        // then
        구간이_삭제된다(구간_삭제_응답);
    }

    /**
     * Given 2개의 구간을 생성하고
     * When 구간의 중간역을 삭제하면
     * Then 지하철 노선에 종점이 삭제된다.
     */
    @Test
    void 구간의_중간역을_삭제한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));
        SectionRequest 신규_구간 = new SectionRequest(판교역_id, 정자역_id, 5);
        노선에_신규_구간을_등록(신분당선, 신규_구간);

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(신분당선, 판교역_id);

        // then
        구간이_삭제된다(구간_삭제_응답);
    }

    /**
     * Given 새로운 역을 생성하고
     * When 노선의 등록되지 않은 새로운 역을 삭제하면
     * Then 구간이 삭제되지 않는다.
     */
    @Test
    void 노선에_등록되지_않은_역을_삭제한다() {
        // given
        Long 판교역_id = id_추출(지하철역_생성("판교역"));

        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(신분당선, 판교역_id);

        // then
        구간이_삭제되지_않는다(구간_삭제_응답);
    }

    /**
     * When 구간이 하나인 노선의 마지막_역을_삭제하면
     * Then 구간이 삭제되지 않는다.
     */
    @Test
    void 구간이_하나인_노선의_마지막_역을_삭제한다() {
        // when
        ExtractableResponse<Response> 구간_삭제_응답 = 구간을_삭제한다(신분당선, 정자역_id);

        // then
        구간이_삭제되지_않는다(구간_삭제_응답);
    }

    public static class SectionAcceptanceTemplate {
        public static void 노선에_신규_구간이_정상_등록된다(ExtractableResponse<Response> 신규_구간이_등록된_노선) {
            assertThat(신규_구간이_등록된_노선.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        public static ExtractableResponse<Response> 노선에_신규_구간을_등록(LineResponse 신분당선, SectionRequest 신규_구간) {
            return RestAssured
                    .given().log().all()
                    .body(신규_구간)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("lines/" + 신분당선.getId() + "/sections")
                    .then().log().all()
                    .extract();
        }

        public static void 신규_구간과_분리된_구간_길이의_합이_원래_구간의_길이와_같다(ExtractableResponse<Response> 신규_구간이_등록된_노선, int totalDistance) {
            List<Integer> 구간들 = 신규_구간이_등록된_노선.jsonPath().getList("sections.distance");
            int 구간_길이의_합 = 구간들.get(0) + 구간들.get(1);
            assertThat(구간_길이의_합).isEqualTo(totalDistance);
        }

        public static void 노선에_신규_구간이_등록되지_않는다(ExtractableResponse<Response> 신규_구간이_등록되지_않음) {
            assertThat(신규_구간이_등록되지_않음.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        public static void 구간이_삭제된다(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        public static ExtractableResponse<Response> 구간을_삭제한다(LineResponse lineResponse, Long stationId) {
            return RestAssured
                    .given().log().all()
                    .param("stationId", stationId)
                    .when().delete("/lines/" + lineResponse.getId() + "/sections")
                    .then().log().all()
                    .extract();
        }

        public static void 구간이_삭제되지_않는다(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }
}
