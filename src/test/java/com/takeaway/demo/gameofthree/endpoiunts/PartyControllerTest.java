package com.takeaway.demo.gameofthree.endpoiunts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static java.lang.Math.log;
import static java.lang.Math.round;
import static java.lang.Math.toIntExact;

import com.takeaway.demo.gameofthree.models.PartyInfo;
import com.takeaway.demo.gameofthree.models.PlayerMove;
import com.takeaway.demo.gameofthree.services.InitialValueGenerator;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * @see PartyController
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartyControllerTest {
    public static final String ROOT_PATH = "/api/parties";
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private InitialValueGenerator initialValueGenerator;

    @BeforeEach
    void prepareTest() {
        when(initialValueGenerator.generate()).thenAnswer(inv -> RandomUtils.nextInt(90, 100));
    }

    @Test
    void testPartyCreation() {
        var player1Id = RandomStringUtils.randomAlphanumeric(6);

        var response = restCreateParty(player1Id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getPlayers().size()).isEqualTo(1);
        assertThat(response.getBody().getPlayers().get(0).getNickName()).isEqualTo(player1Id);

    }

    @Test
    void testJoinParty() {
        var player1Id = RandomStringUtils.randomAlphanumeric(6);
        var player2Id = RandomStringUtils.randomAlphanumeric(6);
        var creationResponse = restCreateParty(player1Id);
        String partyId = creationResponse.getBody().getId();

        var response = restJoinParty(partyId, player2Id, PartyInfo.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isStarted()).isFalse();
        assertThat(response.getBody().getPlayers()).isNotNull();
        assertThat(response.getBody().getPlayers().get(0).getNickName()).isEqualTo(player1Id);
        assertThat(response.getBody().getPlayers().get(1).getNickName()).isEqualTo(player2Id);
    }

    @Test
    void testJoinParty_partyStarter_fails2Join() {
        var player1Id = RandomStringUtils.randomAlphanumeric(6);
        var creationResponse = restCreateParty(player1Id);
        String partyId = creationResponse.getBody().getId();

        var response = restJoinParty(partyId, player1Id, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("error.no.party.self.play." + player1Id);
    }


    @Test
    void testPartyStart() {
        int partyInitialValue = RandomUtils.nextInt(90, 100);
        when(initialValueGenerator.generate()).thenReturn(partyInitialValue);
        var player1Id = RandomStringUtils.randomAlphanumeric(6);
        var player2Id = RandomStringUtils.randomAlphanumeric(6);
        var creationResponse = restCreateParty(player1Id);
        String partyId = creationResponse.getBody().getId();

        restJoinParty(partyId, player2Id, PartyInfo.class);

        var response = restStartParty(partyId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPlayers()).isNotNull();
        assertThat(response.getBody().isStarted()).isTrue();
        assertThat(response.getBody().getCurrentValue()).isEqualTo(partyInitialValue);
        assertThat(response.getBody().getCurrentPlayerId()).isEqualTo(player2Id);
        assertThat(response.getBody().getPlayers().get(0).getNickName()).isEqualTo(player1Id);
        assertThat(response.getBody().getPlayers().get(1).getNickName()).isEqualTo(player2Id);
    }


    @Test
    void testAutoParty() {
        var player1Id = RandomStringUtils.randomAlphanumeric(6);
        var player2Id = RandomStringUtils.randomAlphanumeric(6);
        var creationResponse = restCreateParty(player1Id);
        String partyId = creationResponse.getBody().getId();

        var players = new String[]{player1Id, player2Id};

        restJoinParty(partyId, player2Id, PartyInfo.class);

//        int[] stepValues = {99, 33, 11, 4, 1};

        int initialValue = RandomUtils.nextInt(90, 100);
        when(initialValueGenerator.generate()).thenReturn(initialValue);

        restStartParty(partyId);

        var step1Response = restAutoMove(partyId, player2Id, PartyInfo.class);
        assertThat(step1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(step1Response.getBody().getCurrentPlayerId()).isEqualTo(player1Id);
        assertThat(step1Response.getBody().getCurrentValue()).isIn(initialValue/3 - 1, initialValue/3, initialValue/3 + 1);
        assertThat(step1Response.getBody().getTurns()).hasSize(1);

        var party = step1Response.getBody();
        var turnsCount = 1;

        log.info("CV " + party.getCurrentValue() + " - " + initialValue + " " + log(party.getCurrentValue()));
        int iterationsLimit = toIntExact(round(log(party.getCurrentValue()) / log(3))) + 1;
        while (!party.isOver()) {
            assertThat(party.getCurrentPlayerId())
                    .withFailMessage("step %d", turnsCount)
                    .isEqualTo(players[(turnsCount + 1) % 2]);
            var stepResponse = restAutoMove(partyId, party.getCurrentPlayerId(), PartyInfo.class);
            turnsCount++;
            party = stepResponse.getBody();
            assertThat(stepResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(party.getTurns()).hasSize(turnsCount);
            assertThat(turnsCount).isLessThanOrEqualTo(iterationsLimit);
        }

    }

    @Test
    void testManualParty() {
        var player1Id = RandomStringUtils.randomAlphanumeric(6);
        var player2Id = RandomStringUtils.randomAlphanumeric(6);
        int[] stepAdditions = generateAdditions();
        int[] stepValues = generateCheckLine(stepAdditions);

        when(initialValueGenerator.generate()).thenReturn(stepValues[0]);

        var creationResponse = restCreateParty(player1Id);
        String partyId = creationResponse.getBody().getId();

        var players = new String[]{player1Id, player2Id};

        restJoinParty(partyId, player2Id, PartyInfo.class);

        restStartParty(partyId);

        var step1Response = restManualMove(partyId, player2Id, stepAdditions[0], PartyInfo.class);
        assertThat(step1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(step1Response.getBody().getCurrentPlayerId()).isEqualTo(player1Id);
        assertThat(step1Response.getBody().getTurns()).hasSize(1);

        var party = step1Response.getBody();
        var turnsCount = 1;

        int iterationsLimit = toIntExact(round(log(party.getCurrentValue()) / log(3))) + 1;
        while (!party.isOver()) {
            assertThat(party.getCurrentPlayerId())
                    .isEqualTo(players[(turnsCount + 1) % 2]);
            var stepResponse = restManualMove(partyId, party.getCurrentPlayerId(), stepAdditions[turnsCount], PartyInfo.class);
            turnsCount++;
            party = stepResponse.getBody();
            assertThat(stepResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(stepResponse.getBody().getCurrentValue()).isEqualTo(stepValues[turnsCount]);
            assertThat(party.getTurns()).hasSize(turnsCount);
            assertThat(turnsCount).isLessThanOrEqualTo(iterationsLimit);
        }

    }

    private int[] generateAdditions() {
        int n = 15;
        int[] additions = new int [n];
        for (int i = 0; i < n; i++) {
            additions[i] = RandomUtils.nextInt(0, 3) - 1;
        }
        return additions;
    }

    private int[] generateCheckLine(int[] stepAdditions) {
        int[] stepValues = new int [stepAdditions.length + 1];
        stepValues[stepValues.length - 1] = 1;
        for (int i = stepAdditions.length - 1; i >= 0 ; i--) {
            stepValues[i] = stepValues[i + 1] * 3 - stepAdditions[i];
            System.out.println("SDF [" + i + "] " + stepValues[i] + " " + (-1) * stepAdditions[i]);
        }
        return stepValues;
    }

    /**
     * @see PartyController#makeAutoMove(String, String)
     */
    private <T> ResponseEntity<T> restAutoMove(String party1Id, String playerId, Class<T> clazz) {
        var uriComponents = UriComponentsBuilder.fromPath(ROOT_PATH)
                .pathSegment(party1Id)
                .pathSegment("move")
                .pathSegment("auto")
                .queryParam("playerNickName", playerId).build();
        return restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, new HttpEntity<>(null), clazz);
    }

    /**
     * @see PartyController#makeMove(PlayerMove, String)
     */
    private <T> ResponseEntity<T> restManualMove(String party1Id, String playerId, int addition, Class<T> clazz) {
        var uriComponents = UriComponentsBuilder.fromPath(ROOT_PATH)
                .pathSegment(party1Id)
                .pathSegment("move").build();
        var payload = new PlayerMove()
                .setAddition(addition)
                .setPlayerId(playerId);
        return restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, new HttpEntity<>(payload), clazz);
    }

    private ResponseEntity<PartyInfo> restCreateParty(String player1Id) {
        var uriComponents = UriComponentsBuilder.fromPath(ROOT_PATH).queryParam("nickName", player1Id).build();
        return restTemplate.exchange(uriComponents.toUri(), HttpMethod.POST, new HttpEntity<>(null),  PartyInfo.class);
    }

    private <T> ResponseEntity<T> restJoinParty(String party1Id, String player1Id, Class<T> clazz) {
        var uriComponents = UriComponentsBuilder.fromPath(ROOT_PATH)
                .pathSegment(party1Id)
                .pathSegment("join")
                .queryParam("playerNickName", player1Id).build();
        return restTemplate.<T>exchange(uriComponents.toUri(), HttpMethod.POST, new HttpEntity<>(null), clazz);
    }

    private ResponseEntity<PartyInfo> restStartParty(String partyId) {
        var uri = UriComponentsBuilder.fromPath(ROOT_PATH)
                .pathSegment(partyId)
                .pathSegment("start")
                .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(null),  PartyInfo.class);
    }

}