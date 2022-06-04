package nextstep.subway.ui;

import nextstep.subway.application.LineService;
import nextstep.subway.dto.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineResponse lineResponse = lineService.saveLine(request);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAllLine());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok().body(lineService.findLineById(lineId));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable("id") Long lineId,
                                           @RequestBody LineUpdateRequest request) {
        lineService.updateLine(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionsResponse> addSection(@PathVariable("lineId") Long lineId,
                                                       @RequestBody SectionRequest request) {
        SectionsResponse sectionsResponse = lineService.addSection(lineId, request);

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionsResponse.findSectionId()))
                .body(sectionsResponse);
    }

    @DeleteMapping("/lines//{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable Long lineId,
                                              @RequestParam Long stationId) {
        lineService.removeSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
