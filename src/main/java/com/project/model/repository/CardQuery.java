package com.project.model.repository;

import java.util.List;
import java.util.Optional;

import com.project.model.entity.Card;
import com.project.model.entity.Rarity;
import com.project.model.entity.Set;
import com.project.model.entity.Type;
import com.project.model.service.SortType;
import com.project.model.service.SortType.OrderType;

public interface CardQuery {

	public int getNumberOfPages(List<Rarity> rarities, List<Set> sets, List<Type> types, Optional<String> search);

	public List<Card> getCards(int page, SortType sortType, OrderType orderType, List<Rarity> rarities, List<Set> sets,
			List<Type> types, Optional<String> search);

}
