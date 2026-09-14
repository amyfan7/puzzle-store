package com.amyfan.puzzlestore.controllers;

import com.amyfan.puzzlestore.entities.User;
import com.amyfan.puzzlestore.dtos.Name;
import com.amyfan.puzzlestore.dtos.Password;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import com.amyfan.puzzlestore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam("confirmPassword") String confirmPassword,
                           RedirectAttributes redirectAttributes) {

        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Passwords do not match");
        }

        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "", "Email already exists");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.addUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Account successfully created");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("user", new User());

        if (error != null) {
            model.addAttribute("errorMessage", "Username or password is incorrect");
        }

        return "login";
    }

    @GetMapping("/account")
    public String getAccount(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        User freshUser = userService.findUserByEmail(user.getEmail());
        model.addAttribute("user", freshUser);
        model.addAttribute("orders", freshUser.getOrderHistory());

        return "account";
    }

    @GetMapping("/account/edit")
    public String editAccount(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("name", new Name());
        model.addAttribute("user", user.getUser());

        return "edit-account";
    }

    @PostMapping("/account/edit")
    public String validateEditAccount(@Valid @ModelAttribute("name") Name name,
                                      BindingResult result,
                                      @AuthenticationPrincipal CustomUserDetails user,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("name", name);
            model.addAttribute("user", user.getUser());
            return "edit-account";
        }

        userService.updateName(user.getEmail(), name.getFirst(), name.getLast());

        User updatedUser = userService.findUserByEmail(user.getEmail());
        CustomUserDetails updatedDetails = new CustomUserDetails(updatedUser);

        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        updatedDetails,
                        currentAuth.getCredentials(),
                        updatedDetails.getAuthorities()
                );

        newAuth.setDetails(currentAuth.getDetails());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage", "Account has been updated.");

        return "redirect:/account";
    }

    @GetMapping("/account/password")
    public String changePassword(Model model) {
        model.addAttribute("password", new Password());

        return "change-password";
    }

    @PostMapping("/account/password")
    public String validateChangePassword(@Valid @ModelAttribute("password") Password password,
                                         BindingResult result,
                                         @AuthenticationPrincipal CustomUserDetails user,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        model.addAttribute("password", password);

        if (result.hasErrors()) {
            return "change-password";
        }

        if (!userService.validatePassword(user.getEmail(), password.getCurrent())) {
            result.rejectValue("current", "error.password", "Current password does not match");
            return "change-password";
        }

        if (!password.getNewPassword().equals(password.getConfirm())) {
            result.rejectValue("confirm", "error.password", "Passwords do not match");
            return "change-password";
        }

        if (password.getCurrent().equals(password.getNewPassword())) {
            result.rejectValue("newPassword", "error.password", "New password must be different from current");
            return "change-password";
        }

        userService.updatePassword(user.getEmail(), password.getNewPassword());
        redirectAttributes.addFlashAttribute("successMessage", "Password has been updated.");

        return "redirect:/account";
    }
}
