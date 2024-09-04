package com.micfro.moneycard.controller;


import com.micfro.moneycard.model.MoneyCard;
import com.micfro.moneycard.repository.MoneyCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping("/moneycards")
public class MoneyCardController {

  // DI
  private final MoneyCardRepository moneyCardRepository;

  public MoneyCardController(MoneyCardRepository moneyCardRepository) {
    this.moneyCardRepository = moneyCardRepository;
  }


  // CRUD

  // CREATE

  @PostMapping
  private ResponseEntity<Void> createMoneyCard(
      @RequestBody MoneyCard newMoneyCardRequest, // Binds the request body to a MoneyCard object
      UriComponentsBuilder uriComponentsBuilder,  // Used to construct the URI for the newly created MoneyCard
      Principal principal
  ) {

    MoneyCard moneyCardWithOwner = new MoneyCard(null, newMoneyCardRequest.amount(), principal.getName());

    // Save the new MoneyCard instance to the repository
    MoneyCard savedMoneyCard = moneyCardRepository.save(moneyCardWithOwner);

    // Build the URI for the newly created MoneyCard using its ID
    URI locationOfNewMoneyCard = uriComponentsBuilder
        .path("moneycards/{id}") // Specifies the URI template
        .buildAndExpand(savedMoneyCard.id()) // Replaces the placeholder with the actual ID
        .toUri(); // Converts the UriComponents into a URI object

    // Return a ResponseEntity with a 201 Created status and the location of the new MoneyCard
    return ResponseEntity.created(locationOfNewMoneyCard).build();
  }


  // READ ALL
  @GetMapping
  private ResponseEntity<List<MoneyCard>> findAllByOwner(Pageable pageable, Principal principal) {
    Page<MoneyCard> page = moneyCardRepository.findAllByOwner(principal.getName(),
        PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
        ));
    return ResponseEntity.ok(page.getContent());
  }


  // READ BY ID
  @GetMapping("/{requestedId}")
  private ResponseEntity<MoneyCard> findByIdAndOwner(@PathVariable Long requestedId, Principal principal) {

    MoneyCard moneyCard = findMoneyCardByIdAndOwner(requestedId, principal);

    if (moneyCard != null) {
      return ResponseEntity.ok(moneyCard);
    } else {
      return ResponseEntity.notFound().build();
    }
  }


  // UPDATE BY ID
  @PutMapping("/{requestedId}")
  private ResponseEntity<Void> putMoneyCard(@PathVariable Long requestedId, @RequestBody MoneyCard moneyCardUpdate, Principal principal) {
    MoneyCard moneyCard = findMoneyCardByIdAndOwner(requestedId, principal);

    if (moneyCard != null) {
      MoneyCard updatedMoneyCard = new MoneyCard(moneyCard.id(), moneyCardUpdate.amount(), principal.getName());
      moneyCardRepository.save(updatedMoneyCard);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }


  // DELETE BY ID
  @DeleteMapping("/{requestedId}")
  private ResponseEntity<Void> deleteMoneyCardByIdAndOwner(@PathVariable Long requestedId, Principal principal) {

    MoneyCard moneyCard = findMoneyCardByIdAndOwner(requestedId, principal);
    if (moneyCard != null) {
      moneyCardRepository.deleteById(moneyCard.id());
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }


  private MoneyCard findMoneyCardByIdAndOwner(Long requestedId, Principal principal) {
    return moneyCardRepository.findByIdAndOwner(requestedId, principal.getName());
  }

}
