package com.takeaway.demo.gameofthree.endpoints;

import com.takeaway.demo.gameofthree.exceptions.PartyException;
import com.takeaway.demo.gameofthree.exceptions.PartyNotFoundException;
import com.takeaway.demo.gameofthree.models.PartyInfo;
import com.takeaway.demo.gameofthree.models.PlayerMove;
import com.takeaway.demo.gameofthree.services.LocalizationService;
import com.takeaway.demo.gameofthree.services.PartyService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * GameController.
 *
 * @author Andrey Arefyev
 */

@Slf4j
@Tag(name = "parties", description = "Party management")
@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private final LocalizationService localizationService;
    private final PartyService partyService;

    public PartyController(LocalizationService localizationService, PartyService partyService) {
        this.localizationService = localizationService;
        this.partyService = partyService;}

    @Operation(description = "Creates new party ",
               parameters = @Parameter(name = "nickName", description = "game starter nickname", required = true),
               responses = {@ApiResponse(responseCode = "201", description = "Game created get party `id`")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyInfo createNewParty(@RequestParam("nickName") String starterNickName) {
        return partyService.create(starterNickName);
    }

    @Operation(description = "common information about game")
    @GetMapping("/{partyId}")
    public PartyInfo getPartyActualInfo(@PathVariable String partyId) {
        return partyService.getById(partyId);
    }

    @Operation(description = "Join the game")
    @PostMapping("/{partyId}/join")
    public PartyInfo joinParty(@RequestParam("playerNickName") String player, @PathVariable String partyId) {
        return partyService.tryJoinParty(partyId, player);
    }

    @PostMapping("/{partyId}/start")
    public PartyInfo startParty(@PathVariable String partyId) {
        return partyService.startParty(partyId);
    }

    @PostMapping("/{partyId}/move/auto")
    public PartyInfo makeAutoMove(@PathVariable String partyId, @RequestParam("playerNickName") String player) {
        return partyService.makeAutoMove(partyId, player);
    }

    @PostMapping("/{partyId}/move")
    public PartyInfo makeMove(@RequestBody PlayerMove turn, @PathVariable String partyId) {
        return partyService.makeManualMove(partyId, turn);
    }

    @ExceptionHandler(PartyNotFoundException.class)
    ResponseEntity<String> handleNotFoundParty(PartyNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(localizationService.getMessage(e.getMessage(), e.getMessageParams()));
    }

    @ExceptionHandler(PartyException.class)
    ResponseEntity<String> handlePartyException(PartyException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(localizationService.getMessage(e.getMessage(), e.getMessageParams()));
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<String> handle(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(localizationService.getMessage(e.getMessage(), e));
    }

}