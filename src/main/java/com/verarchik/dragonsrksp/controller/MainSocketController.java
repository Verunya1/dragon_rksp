package com.verarchik.dragonsrksp.controller;

import com.verarchik.dragonsrksp.entity.Dragon;

import com.verarchik.dragonsrksp.repository.DragonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class MainSocketController {

    private final DragonRepository dragonRepository;
    @Autowired
    public MainSocketController(DragonRepository dragonRepository) {
        this.dragonRepository = dragonRepository;
    }
    @MessageMapping("getDragon")
    public Mono<Dragon> getDragon(Long id) {
        return Mono.justOrEmpty(dragonRepository.findDragonById(id));
    }
    @MessageMapping("addDragon")
    public Mono<Dragon> addDragon(Dragon Dragon) {
        return Mono.justOrEmpty(dragonRepository.save(Dragon));
    }
    @MessageMapping("getDragons")
    public Flux<Dragon> getDragons() {
        return Flux.fromIterable(dragonRepository.findAll());
    }
    @MessageMapping("deleteDragon")
    public Mono<Void> deleteDragon(Long id){
        Dragon Dragon = dragonRepository.findDragonById(id);
        dragonRepository.delete(Dragon);
        return Mono.empty();
    }
    @MessageMapping("dragonChannel")
    public Flux<Dragon> DragonChannel(Flux<Dragon> Dragons){
// block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-http-nio-3
// return Flux.fromIterable(DragonRepository.saveAll(Dragons.collectList().block()));
// Используем Mono.fromCallable, чтобы асинхронно вызвать метод DragonRepository::save для каждого кота и вернуть результаты как Flux.
        return Dragons.flatMap(Dragon -> Mono.fromCallable(() -> dragonRepository.save(Dragon)))
                .collectList()
                .flatMapMany(savedDragons -> Flux.fromIterable(savedDragons));
    }
}