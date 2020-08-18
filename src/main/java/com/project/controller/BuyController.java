package com.project.controller;

import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.repository.CardRepository;
import com.project.model.service.AccountService;

@Controller
@Scope(value = "session")
public class BuyController extends CardsController {

	@Autowired
	private CardRepository cardRepository;

	@RestController
	public static class BuyRestController {

		@Autowired
		private AccountService accountService;

		@GetMapping("/buy/bought")
		public String bought(@RequestParam(value = "id", required = true) String id) {
			accountService.addCard(id);
			JsonObject json = Json.createObjectBuilder()
					.add("coins", accountService.getFormattedInteger(accountService.getCoins()))
					.add("quantity", accountService.getFormattedInteger(accountService.countUserCardsByCardId(id)))
					.build();
			return json.toString();
		}

	}

	@GetMapping("/buy")
	public String buyPage(Model model, @RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search) {
		addSelectedAttributes(model, page, rarities, sets, types, search);
		return "buy";
	}

	@Override
	@PostMapping("/buy")
	public ModelAndView searchSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@ModelAttribute(value = "search") String search) {
		return super.searchSelection(page, rarities, sets, types, search);
	}

	@Override
	@PostMapping(value = "/buy/sort")
	public ModelAndView sortSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "sort") String selectedSort) {
		return super.sortSelection(page, rarities, sets, types, search, selectedSort);
	}

	@Override
	@PostMapping(value = "/buy/order")
	public ModelAndView orderSelection(@RequestParam(value = "page") Optional<Integer> page,
			@RequestParam(value = "rarity") Optional<String> rarities,
			@RequestParam(value = "set") Optional<String> sets, @RequestParam(value = "type") Optional<String> types,
			@RequestParam(value = "search") Optional<String> search,
			@ModelAttribute(value = "order") String selectedOrder) {
		return super.orderSelection(page, rarities, sets, types, search, selectedOrder);
	}

	@Override
	protected int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search) {
		return cardRepository.getNumberOfPages(rarities, sets, types, search);
	}

	@Override
	protected String getLink() {
		return "/buy";
	}

	@Override
	protected List<Card> getCards(int page, List<Rarity> rarities, List<Set> sets, List<Type> types,
			Optional<String> search) {
		return accountService.getCards(page, sessionData.getSortType(), sessionData.getOrderType(), rarities, sets,
				types, search);
	}

	@Override
	protected ModelAndView redirectToCardsPage(ModelMap model, Optional<Integer> page, Optional<String> rarities,
			Optional<String> sets, Optional<String> types, Optional<String> search) {
		ModelAndView modelAndView = super.redirectToCardsPage(model, page, rarities, sets, types, search);
		modelAndView.setViewName("redirect:/buy");
		return modelAndView;
	}

}