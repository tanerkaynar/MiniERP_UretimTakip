package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.IslemLogi;
import com.tanerkaynar.nexuserp.repository.IslemLogiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loglar")
public class LogController {

    @Autowired
    private IslemLogiRepository repository;

    @GetMapping
    public List<IslemLogi> getAll() {
        return repository.findTop100ByOrderByLogidDesc();
    }

    @PostMapping
    public ResponseEntity<IslemLogi> create(@RequestBody IslemLogi log) {
        log.setLogid(null);
        return ResponseEntity.ok(repository.save(log));
    }
}