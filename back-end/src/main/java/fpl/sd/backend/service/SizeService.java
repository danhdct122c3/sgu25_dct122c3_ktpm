package fpl.sd.backend.service;

import fpl.sd.backend.dto.response.SizeResponse;
import fpl.sd.backend.entity.SizeChart;
import fpl.sd.backend.repository.SizeChartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SizeService {

    SizeChartRepository sizeChartRepository;


    public List<SizeResponse> getAllSizes() {
        List<SizeChart> sizes = sizeChartRepository.findAll();
        return sizes.stream()
                .map(size -> {
                    SizeResponse sizeResponse = new SizeResponse();
                    sizeResponse.setId(size.getId());
                    sizeResponse.setSizeNumber(size.getSizeNumber());
                    return sizeResponse;
                }).toList();
    }

    public void initializeDefaultSizes() {
        // Check if sizes already exist
        if (sizeChartRepository.count() > 0) {
            return; // Sizes already exist, don't duplicate
        }

        // Create standard shoe sizes (US sizes)
        List<SizeChart> defaultSizes = List.of(
            SizeChart.builder().sizeNumber(6.0).build(),
            SizeChart.builder().sizeNumber(6.5).build(),
            SizeChart.builder().sizeNumber(7.0).build(),
            SizeChart.builder().sizeNumber(7.5).build(),
            SizeChart.builder().sizeNumber(8.0).build(),
            SizeChart.builder().sizeNumber(8.5).build(),
            SizeChart.builder().sizeNumber(9.0).build(),
            SizeChart.builder().sizeNumber(9.5).build(),
            SizeChart.builder().sizeNumber(10.0).build(),
            SizeChart.builder().sizeNumber(10.5).build(),
            SizeChart.builder().sizeNumber(11.0).build(),
            SizeChart.builder().sizeNumber(11.5).build(),
            SizeChart.builder().sizeNumber(12.0).build()
        );

        sizeChartRepository.saveAll(defaultSizes);
    }

}
