package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.exceptions.ProductNotFoundException;
import com.amyfan.puzzlestore.repositories.CartItemRepository;
import com.amyfan.puzzlestore.repositories.ManufacturerRepository;
import com.amyfan.puzzlestore.repositories.OrderItemRepository;
import com.amyfan.puzzlestore.repositories.PuzzleRepository;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class PuzzleServiceImpl implements PuzzleService {
    private final PuzzleRepository puzzleRepo;
    private final ResourceLoader resourceLoader;
    private final CartItemRepository cartItemRepo;
    private final OrderItemRepository orderItemRepo;
    private final ManufacturerRepository manufacturerRepo;

    @Autowired
    public PuzzleServiceImpl(PuzzleRepository puzzleRepo, ResourceLoader resourceLoader,
                             CartItemRepository cartItemRepo, OrderItemRepository orderItemRepo,
                             ManufacturerRepository manufacturerRepo) {
        this.puzzleRepo = puzzleRepo;
        this.resourceLoader = resourceLoader;
        this.cartItemRepo = cartItemRepo;
        this.orderItemRepo = orderItemRepo;
        this.manufacturerRepo = manufacturerRepo;
    }

    public List<Integer> getPuzzleCounts() {
        return List.of((int) puzzleRepo.count(), puzzleRepo.findDistinctCategories(), puzzleRepo.findDistinctDifficulties());
    }

    public Puzzle findPuzzleById(String productId) {
        return puzzleRepo.findPuzzleByProductId(productId);
    }

    public List<Puzzle> getAllPuzzles() {
        return puzzleRepo.findAll();
    }

    public List<Puzzle> handleSearch(String name, String category, String difficulty,
                                     Double minPrice, Double maxPrice, CustomUserDetails user) {
        Specification<Puzzle> spec = Specification.unrestricted();

        if (name != null && !name.isBlank()) {
            String trimmed = name.trim().toLowerCase();
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + trimmed + "%"));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category"), category));
        }
        if (difficulty != null && !difficulty.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("difficulty"), difficulty));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price").as(Double.class), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price").as(Double.class), maxPrice));
        }

        boolean isAdmin = user != null && user.getUser().getRole().getName().equals("ADMIN");
        if (!isAdmin) {
            spec = spec.and((root, query, cb) -> cb.isTrue(root.get("active")));
        }

        return puzzleRepo.findAll(spec);
    }

    public List<String> findImages(String productId) {
        List<String> imgs = new ArrayList<>();

        for (int i = 1; i <= 200; i++) {
            String filename = productId + "-0" + i + ".jpg";

            Resource resource = resourceLoader.getResource("classpath:/static/images/" + filename);

            if (resource.exists()) {
                imgs.add(filename);
            } else {
                break;
            }
        }
        return imgs;
    }

    public void savePuzzle(Puzzle puzzle) {
        puzzle.setActive(true);
        puzzle.setManufacturer(manufacturerRepo.findManufacturerByManufacturerName(puzzle.getManufacturerName()));
        puzzleRepo.save(puzzle);
    }

    public void updatePuzzle(Puzzle puzzle) {
        puzzle.setManufacturer(manufacturerRepo.findManufacturerByManufacturerName(puzzle.getManufacturerName()));

        puzzleRepo.save(puzzle);
    }

    public List<Puzzle> findActivePuzzles() {
        return puzzleRepo.findAllByActiveTrue();
    }

    public String deletePuzzle(String productId) {
        List<String> cartPuzzles = cartItemRepo.findAll()
                        .stream().map(c -> c.getPuzzle().getProductId()).toList();
        List<String> orderPuzzles = orderItemRepo.findAll()
                        .stream().map(o -> o.getPuzzle().getProductId()).toList();

        if (cartPuzzles.contains(productId) || orderPuzzles.contains(productId)) {
            return "Cannot delete product";
        }

        puzzleRepo.deleteById(productId);

        for (int i = 1; i < 10; i++) {
            Path imagePath = Paths.get("src/main/resources/static/images/" + productId + "-0" + i + ".jpg");
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("No image at: " + imagePath);
            }
        }

        return "";
    }

    public void checkIfActive(String productId, CustomUserDetails user) {
        if (!puzzleRepo.findPuzzleByProductId(productId).getActive()
                && (user == null || user.getUser().getRole().getName().equals("CUSTOMER"))) {
            throw new ProductNotFoundException("Product either does not exist or is inactive.");
        }
    }
}
