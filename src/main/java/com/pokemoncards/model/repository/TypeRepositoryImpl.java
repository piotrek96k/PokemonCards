package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.pokemoncards.model.entity.Card;
import com.pokemoncards.model.entity.Type;

public class TypeRepositoryImpl extends CardFieldRepositoryImpl<Type>{

	@Override
	public List<Type> findAll(Optional<String> username) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Type> criteriaQuery = criteriaBuilder.createQuery(Type.class);
		Root<Card> card = criteriaQuery.from(Card.class);
		Join<Card, Type> type = card.join("types");
		joinAccountIfUsernameNotEmpty(criteriaBuilder, criteriaQuery, card, username,type);
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}
