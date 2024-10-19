package com.verarchik.dragonsrksp.controller;

import com.verarchik.dragonsrksp.entity.Dragon;
import com.verarchik.dragonsrksp.list.DragonListWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/dragons")
public class ChannelController {
    private final RSocketRequester rSocketRequester;

    @Autowired
    public ChannelController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @PostMapping("/exp")
    public Flux<Dragon> addDragonsMultiple(@RequestBody DragonListWrapper catListWrapper) {
        List<Dragon> dragonList = catListWrapper.getDragons();
        Flux<Dragon> dragons = Flux.fromIterable(dragonList);
        return rSocketRequester
                .route("dragonChannel")
                .data(dragons)
                .retrieveFlux(Dragon.class);
    }
}