package com.amyfan.puzzlestore.services;

import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.security.CustomUserDetails;

import java.util.List;

public interface PuzzleService {
    List<Integer> getPuzzleCounts();
    Puzzle findPuzzleById(String productId);
    List<Puzzle> getAllPuzzles();
    List<Puzzle> handleSearch(String name, String category, String difficulty,
                              Double minPrice, Double maxPrice, CustomUserDetails user);
    List<String> findImages(String productId);
    void savePuzzle(Puzzle puzzle);
    void updatePuzzle(Puzzle puzzle);
    List<Puzzle> findActivePuzzles();
    String deletePuzzle(String productId);
    void checkIfActive(String productId, CustomUserDetails user);
}
