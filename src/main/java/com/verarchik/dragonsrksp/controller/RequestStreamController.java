package com.verarchik.dragonsrksp.controller;

import com.verarchik.dragonsrksp.entity.Dragon;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dragons")
public class RequestStreamController {
    private final RSocketRequester rSocketRequester;
    @Autowired
    public RequestStreamController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }
    @GetMapping
    public Publisher<Dragon> getDragons() {
        return rSocketRequester
                .route("getDragons")
                .data(new Dragon())
                .retrieveFlux(Dragon.class);
    }
}