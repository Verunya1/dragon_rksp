package com.verarchik.dragonsrksp.controller;

import com.verarchik.dragonsrksp.entity.Dragon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dragons")
public class RequestResponseController {
    private final RSocketRequester rSocketRequester;
    @Autowired
    public RequestResponseController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }
    @GetMapping("/{id}")
    public Mono<Dragon> getDragon(@PathVariable Long id) {
        return rSocketRequester
                .route("getDragon")
                .data(id)
                .retrieveMono(Dragon.class);
    }
    @PostMapping
    public Mono<Dragon> addDragon(@RequestBody Dragon dragon) {
        return rSocketRequester
                .route("addDragon")
                .data(dragon)
                .retrieveMono(Dragon.class);
    }
}