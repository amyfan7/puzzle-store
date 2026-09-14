package com.amyfan.puzzlestore.controllers;

import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.entities.CartItem;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import com.amyfan.puzzlestore.services.CartService;
import com.amyfan.puzzlestore.services.PuzzleService;
import com.amyfan.puzzlestore.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PuzzleController {
    private final PuzzleService puzzleService;
    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public PuzzleController(PuzzleService puzzleService, CartService cartService, UserService userService) {
        this.puzzleService = puzzleService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHome(@RequestParam(required = false) String loginSuccess,
                          @RequestParam(required = false) String logoutSuccess,
                          Model model) {
        List<Integer> counts = puzzleService.getPuzzleCounts();
        model.addAttribute("totalCount", counts.getFirst());
        model.addAttribute("categories", counts.get(1));
        model.addAttribute("difficulties", counts.getLast());

        List<Puzzle> featured = new ArrayList<>(puzzleService.findActivePuzzles().subList(0, 4));
        model.addAttribute("puzzles", featured);

        if (loginSuccess != null)
            model.addAttribute("successMessage", "You have successfully logged in.");
        else if (logoutSuccess != null)
            model.addAttribute("successMessage", "You have successfully logged out.");

        return "home";
    }

    @GetMapping("/products")
    public String getAllProducts(@AuthenticationPrincipal CustomUserDetails user,
                                 Model model) {
        if (user != null && user.getUser().getRole().getName().equals("ADMIN")) {
            model.addAttribute("puzzles", puzzleService.getAllPuzzles());
        } else {
            model.addAttribute("puzzles", puzzleService.findActivePuzzles());
        }

        return "products";
    }

    @GetMapping("/products/search")
    public String handleProductSearch(@AuthenticationPrincipal CustomUserDetails user,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String difficulty,
                                      @RequestParam(required = false) Double minPrice,
                                      @RequestParam(required = false) Double maxPrice,
                                      Model model) {
        model.addAttribute("name", name);
        model.addAttribute("category", category);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        model.addAttribute("puzzles", puzzleService.handleSearch(name, category, difficulty,
                minPrice, maxPrice, user));
        return "products";
    }

    @GetMapping("/products/search/clear")
    public String clearSearch(@AuthenticationPrincipal CustomUserDetails user,
                              RedirectAttributes redirectAttributes) {
        if (user != null && user.getUser().getRole().getName().equals("ADMIN")) {
            redirectAttributes.addFlashAttribute("puzzles", puzzleService.getAllPuzzles());
        } else {
            redirectAttributes.addFlashAttribute("puzzles", puzzleService.findActivePuzzles());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/{productId}")
    public String getProductById(@PathVariable String productId,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 HttpServletRequest request,
                                 Model model) {
        puzzleService.checkIfActive(productId, user);

        model.addAttribute("imgs", puzzleService.findImages(productId));
        model.addAttribute("puzzle", puzzleService.findPuzzleById(productId));
        model.addAttribute("cartItem", new CartItem());
        model.addAttribute("currentUrl", request.getRequestURI());

        return "product-details";
    }

    @PostMapping("/products/{productId}")
    public String addToCart(@PathVariable String productId,
                            @Valid @ModelAttribute("cartItem") CartItem cartItem,
                            @AuthenticationPrincipal CustomUserDetails user,
                            RedirectAttributes redirectAttributes) {
        puzzleService.checkIfActive(productId, user);
        cartService.addToCart(user.getEmail(), productId, cartItem.getQuantity());

        redirectAttributes.addFlashAttribute("successMessage", "Item has been added to cart.");

        return "redirect:/products/" + productId;
    }

    @GetMapping("/products/new")
    public String addProduct(Model model,
                             @AuthenticationPrincipal CustomUserDetails user) {
        userService.validateAdmin(user);

        model.addAttribute("puzzle", new Puzzle());
        model.addAttribute("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        return "new-product";
    }

    @PostMapping("/products/new")
    public String validateNewProduct(@Valid @ModelAttribute("puzzle") Puzzle puzzle,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        List<String> puzzleIds = puzzleService.getAllPuzzles()
                .stream().map(Puzzle::getProductId).toList();
        if (puzzleIds.contains(puzzle.getProductId())) {
            result.rejectValue("productId", "", "Product ID must be unique");
            model.addAttribute("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
            return "new-product";
        }

        if (result.hasErrors()) {
            model.addAttribute("puzzle", puzzle);
            model.addAttribute("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
            return "new-product";
        }

        puzzle.setAcquiredDate(LocalDate.now());
        puzzleService.savePuzzle(puzzle);
        redirectAttributes.addFlashAttribute("successMessage", "New product has been added.");

        return "redirect:/products";
    }

    @GetMapping("/products/{productId}/edit")
    public String editProduct(@PathVariable String productId, Model model) {
        model.addAttribute("puzzle", puzzleService.findPuzzleById(productId));
        return "edit-product";
    }

    @PostMapping("/products/{productId}/edit")
    public String validateEditProduct(@PathVariable String productId,
                                      @Valid @ModelAttribute("puzzle") Puzzle puzzle,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("puzzle", puzzleService.findPuzzleById(productId));
            return "edit-product";
        }

        puzzleService.savePuzzle(puzzle);
        redirectAttributes.addFlashAttribute("successMessage", "Product has been updated.");

        return "redirect:/products/{productId}";
    }

    @PostMapping("/products/{productId}/active")
    public String changeActivation(@PathVariable String productId, Model model) {
        Puzzle puzzle = puzzleService.findPuzzleById(productId);
        puzzle.setActive(!puzzle.getActive());
        puzzleService.updatePuzzle(puzzle);

        model.addAttribute("imgs", puzzleService.findImages(productId));
        model.addAttribute("puzzle", puzzle);
        model.addAttribute("cartItem", new CartItem());

        return "redirect:/products/{productId}";
    }

    @PostMapping("/products/{productId}/delete")
    public String deletePuzzle(@PathVariable String productId,
                               RedirectAttributes redirectAttributes) {
        String message = puzzleService.deletePuzzle(productId);

        if (message.isBlank()) {
            redirectAttributes.addFlashAttribute("successMessage", "Product has been deleted");
            return "redirect:/products";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/products/{productId}";
        }
    }
}
