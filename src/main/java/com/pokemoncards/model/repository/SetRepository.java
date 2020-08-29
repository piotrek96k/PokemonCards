package com.pokemoncards.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokemoncards.model.entity.Set;

public interface SetRepository extends JpaRepository<Set, String>, CardField<Set>{

}
