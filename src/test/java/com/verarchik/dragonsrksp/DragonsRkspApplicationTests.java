package com.verarchik.dragonsrksp;

import com.verarchik.dragonsrksp.entity.Dragon;
import com.verarchik.dragonsrksp.repository.DragonRepository;
import io.rsocket.frame.decoder.PayloadDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DragonsRkspApplicationTests {
    @Autowired
    private DragonRepository dragonRepository;
    private RSocketRequester requester;
    @BeforeEach
    public void setup() {
        requester = RSocketRequester.builder()
                .rsocketStrategies(builder -> builder.decoder(new Jackson2JsonDecoder()))
                .rsocketStrategies(builder -> builder.encoder(new Jackson2JsonEncoder()))
                .rsocketConnector(connector -> connector
                        .payloadDecoder(PayloadDecoder.ZERO_COPY)
                        .reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))))
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", 5200);
    }
    @AfterEach
    public void cleanup() {
        requester.dispose();
    }
    @Test
    public void testGetDragon() {
        Dragon dragon = new Dragon();
        dragon.setName("TestDragon");
        dragon.setAge(2);
        dragon.setColor("Black");
        dragon.setBreed("Siamese");
        Dragon savedDragon = dragonRepository.save(dragon);
        Mono<Dragon> result = requester.route("getDragon")
                .data(savedDragon.getId())
                .retrieveMono(Dragon.class);
        assertNotNull(result.block());
    }
    @Test
    public void testAddDragon() {
        Dragon dragon = new Dragon();
        dragon.setName("TestDragon");
        dragon.setAge(2);
        dragon.setColor("Black");
        dragon.setBreed("Siamese");
        Mono<Dragon> result = requester.route("addDragon")
                .data(dragon)
                .retrieveMono(Dragon.class);

        Dragon savedDragon = result.block();
        assertNotNull(savedDragon);
        assertNotNull(savedDragon.getId());
        assertTrue(savedDragon.getId() > 0);
    }
    @Test
    public void testGetDragons() {
        Flux<Dragon> result = requester.route("getDragons")
                .retrieveFlux(Dragon.class);
        assertNotNull(result.blockFirst());
    }
    @Test
    public void testDeleteDragon() {
        Dragon dragon = new Dragon();
        dragon.setName("TestDragon");
        dragon.setAge(2);
        dragon.setColor("Black");
        dragon.setBreed("Siamese");
        Dragon savedDragon = dragonRepository.save(dragon);
        Mono<Void> result = requester.route("deleteDragon")
                .data(savedDragon.getId())
                .send();
        result.block();
        Dragon deletedDragon = dragonRepository.findDragonById(savedDragon.getId());
        assertNotSame(deletedDragon, savedDragon);
    }
}
