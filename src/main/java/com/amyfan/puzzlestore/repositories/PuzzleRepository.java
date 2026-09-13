package com.amyfan.puzzlestore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.amyfan.puzzlestore.entities.Puzzle;

import java.util.List;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, String>, JpaSpecificationExecutor<Puzzle> {
    @Query("SELECT COUNT(DISTINCT p.category) FROM Puzzle p")
    int findDistinctCategories();
    @Query("SELECT COUNT(DISTINCT p.difficulty) FROM Puzzle p")
    int findDistinctDifficulties();
    Puzzle findPuzzleByProductId(String productId);
    List<Puzzle> findAllByActiveTrue();
}
