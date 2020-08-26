package com.pokemoncards.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.pokemoncards.annotation.OnRegister;
import com.pokemoncards.model.component.PasswordRepeatData;
import com.pokemoncards.model.entity.Account;
import com.pokemoncards.model.service.AccountService;

@Controller
public class RegisterController {

	@Autowired
	private AccountService accountService;

	@GetMapping(value = "/register")
	public String registerForm(Model model) {
		model.addAttribute("account", new Account());
		model.addAttribute("pswRepeat", new PasswordRepeatData());
		return "register";
	}

	@PostMapping(value = "/register")
	public String registerUser(@Validated(OnRegister.class) Account account, @Valid PasswordRepeatData pswData) {
		accountService.addAccount(account);
		return "registersucces";
	}

	@ExceptionHandler(BindException.class)
	public String handleValidationExceptions(Model model, BindException exception) {
		List<String> errors = new ArrayList<String>();
		exception.getAllErrors().forEach(error->errors.add(error.getDefaultMessage()));
		model.addAttribute("errors",errors);
		return registerForm(model);
	}

}