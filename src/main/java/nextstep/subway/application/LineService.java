package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final SectionMapper sectionMapper;

    public LineService(LineRepository lineRepository, SectionMapper sectionMapper) {
        this.lineRepository = lineRepository;
        this.sectionMapper = sectionMapper;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Section section = sectionMapper.from(lineRequest);

        Line persistLine = lineRepository.save(lineRequest.toLine(section));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long lineId) {
        Line findLine = findById(lineId);

        return LineResponse.of(findLine);
    }

    private Line findById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException("해당 지하철 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest request) {
        Line findLine = findById(lineId);

        findLine.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public Section addSection(Long lineId, SectionRequest request) {
        Line findLine = findById(lineId);
        Section section = sectionMapper.from(request);

        findLine.addSection(section);
        return section;
    }
}
