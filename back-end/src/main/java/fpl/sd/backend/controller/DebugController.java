package fpl.sd.backend.controller;

import fpl.sd.backend.entity.ShoeImage;
import fpl.sd.backend.repository.ShoeImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DebugController {
    ShoeImageRepository shoeImageRepository;

    @GetMapping("/shoe/{id}/images")
    public List<ShoeImage> getShoeImages(@PathVariable("id") Integer id) {
        return shoeImageRepository.findAllByShoeId(id);
    }
}
