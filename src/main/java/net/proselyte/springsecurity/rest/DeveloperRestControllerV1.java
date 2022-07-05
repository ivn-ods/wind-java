package net.proselyte.springsecurity.rest;

import net.proselyte.springsecurity.model.Developer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/full")
public class DeveloperRestControllerV1 {
    private final List<Developer> DEVELOPERS = Stream.of(
            new Developer(1L, "Ivan", "Ivanov"),
            new Developer(2L, "Kolya", "Ivanov"),
            new Developer(3L, "Sergey", "Sergeev")
    ).collect(Collectors.toList());

    @GetMapping
    public List<Developer> getAll() {
        return DEVELOPERS;
    }



}
