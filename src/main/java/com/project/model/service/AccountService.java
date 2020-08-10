package com.project.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.model.entity.Account;
import com.project.model.entity.AccountId;
import com.project.model.entity.QuantityCard;
import com.project.model.entity.Role;
import com.project.model.entity.RoleEnum;
import com.project.model.repository.AccountRepository;
import com.project.model.repository.CardRepository;
import com.project.model.repository.RoleRepository;
import com.project.model.service.SortType.OrderType;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CardRepository cardRepository;

	public void addAccount(Account account) {
		Role role = roleRepository.getOne(RoleEnum.ROLE_USER.toString());
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		account.setRoles(roles);
		account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));
		account.setEnabled(true);
		account.setCoins(1000);
		accountRepository.save(account);
	}

	public List<QuantityCard> getUserCards(int page) {
		Function<AccountId, List<QuantityCard>> function = accountId -> {
			List<QuantityCard> cards = accountRepository.getAccountCardsListByPage(accountId.getUsername(), page);
			cards.forEach(card -> card.setCost(card.getCost() / 2));
			return cards;
		};
		return operateOnAccount(function, () -> new ArrayList<QuantityCard>());
	}

	public int getUserCardsNumberOfPages() {
		return operateOnAccount(accountId -> accountRepository.getNumberOfPages(accountId.getUsername()), () -> 1);
	}

	public List<QuantityCard> getGalleryCards(int page, SortType sortType, OrderType orderType) {
		return getGalleryCards(() -> cardRepository.getQuantityCardsByPageOrderByValue(page, sortType, orderType));
	}

	public List<QuantityCard> getGalleryCardsWithSelectedRarities(int page, SortType sortType, OrderType orderType,
			List<String> rarities) {
		return getGalleryCards(() -> cardRepository.getQuantityCardsByPageOrderByValueWithSelectedRarities(page,
				sortType, orderType, rarities));
	}

	public List<QuantityCard> getGalleryCardsWithSearch(int page, SortType sortType, OrderType orderType,
			String search) {
		return getGalleryCards(
				() -> cardRepository.getQuantityCardsByPageOrderByValueWithSearch(page, sortType, orderType, search));
	}

	public List<QuantityCard> getGalleryCardsWithSelectedRaritiesWithSearch(int page, SortType sortType,
			OrderType orderType, List<String> rarities, String search) {
		return getGalleryCards(() -> cardRepository.getQuantityCardsByPageOrderByValueWithSelectedRaritiesWithSearch(
				page, sortType, orderType, rarities, search));
	}

	private List<QuantityCard> getGalleryCards(Supplier<List<QuantityCard>> supplier) {
		Function<AccountId, List<QuantityCard>> function = accountId -> {
			List<QuantityCard> cards = supplier.get();
			for (QuantityCard card : cards)
				card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), card.getId()));
			return cards;
		};
		return operateOnAccount(function, () -> new ArrayList<QuantityCard>());
	}

	public int getCoins() {
		return operateOnAccount(accountId -> accountRepository.getCoins(accountId.getUsername()), () -> 0);
	}

	public QuantityCard getQuantityCard(String id) {
		Function<AccountId, QuantityCard> function = accountId -> {
			QuantityCard card = new QuantityCard(cardRepository.getOne(id));
			card.setQuantity(accountRepository.countUserCardsByCardId(accountId.getUsername(), id));
			return card;
		};
		return operateOnAccount(function, () -> new QuantityCard(cardRepository.getOne(id)));
	}

	public QuantityCard getQuantityCardToSell(String id) {
		QuantityCard card = getQuantityCard(id);
		card.setCost(card.getCost() / 2);
		return card;
	}

	public boolean addCard(String id) {
		Function<AccountId, Boolean> function = accountId -> {
			int cost = cardRepository.getCost(id);
			int coins = accountRepository.getCoins(accountId.getUsername());
			if (coins >= cost) {
				accountRepository.addCard(accountId.getUsername(), accountId.getEmail(), id);
				accountRepository.updateUserCoins(accountId.getUsername(), coins - cost);
				return true;
			}
			return false;
		};
		return operateOnAccount(function, () -> false);
	}

	public boolean removeCard(String id) {
		Function<AccountId, Boolean> function = accountId -> {
			int cost = cardRepository.getCost(id) / 2;
			int coins = accountRepository.getCoins(accountId.getUsername());
			accountRepository.removeCard(accountId.getUsername(), id);
			accountRepository.updateUserCoins(accountId.getUsername(), coins + cost);
			return true;
		};
		return operateOnAccount(function, () -> false);
	}

	private <T> T operateOnAccount(Function<AccountId, T> function, Supplier<T> supplier) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails user = (UserDetails) principal;
			AccountId accountId = accountRepository.getAccountId(user.getUsername());
			return function.apply(accountId);
		}
		return supplier.get();
	}

}