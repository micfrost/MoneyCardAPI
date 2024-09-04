package com.micfro.moneycard.repository;

import com.micfro.moneycard.model.MoneyCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MoneyCardRepository extends CrudRepository<MoneyCard, Long>, PagingAndSortingRepository<MoneyCard, Long> {

  /**
   * Finds a MoneyCard by its ID and the owner's name.
   *
   * @param id    the ID of the MoneyCard
   * @param owner the name of the owner
   * @return the MoneyCard that matches the given ID and owner, or null if not found
   */
  MoneyCard findByIdAndOwner(Long id, String owner);

  /**
   * Finds a paginated list of MoneyCards by the owner's name.
   *
   * @param owner      the name of the owner
   * @param pageRequest the pagination and sorting information
   * @return a Page of MoneyCards owned by the specified owner
   */
  Page<MoneyCard> findAllByOwner(String owner, PageRequest pageRequest);
}