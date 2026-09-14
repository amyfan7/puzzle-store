package com.amyfan.puzzlestore.initializers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.amyfan.puzzlestore.entities.Manufacturer;
import com.amyfan.puzzlestore.entities.Puzzle;
import com.amyfan.puzzlestore.repositories.ManufacturerRepository;
import com.amyfan.puzzlestore.repositories.PuzzleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PuzzleInitializer {
    private final PuzzleRepository puzzleRepo;
    private final ManufacturerRepository manufacturerRepo;

    @Autowired
    public PuzzleInitializer(PuzzleRepository puzzleRepo, ManufacturerRepository manufacturerRepo) {
        this.puzzleRepo = puzzleRepo;
        this.manufacturerRepo = manufacturerRepo;
    }

    @PostConstruct
    public void init() throws IOException, CsvValidationException {
        if (manufacturerRepo.count() != 0) {
            System.out.println("Data already present - not executing manufacturer initializer.");
        } else {
            String path = "src/main/resources/static/data/wooden_puzzle_manufacturers.csv";

            CSVReader reader = new CSVReader(new FileReader(path));
            String[] line;
            List<Manufacturer> m = new ArrayList<>();
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                m.add(new Manufacturer(line[0], line[1], line[2], line[3]));
            }

            manufacturerRepo.saveAll(m);
            reader.close();
        }

        if (puzzleRepo.count() != 0) {
            System.out.println("Data already present - not executing puzzle initializer.");
            return;
        }

        String path = "src/main/resources/static/data/wooden_puzzle_products.csv";

        CSVReader reader = new CSVReader(new FileReader(path));
        String[] line;
        List<Puzzle> p = new ArrayList<>();
        reader.readNext();

        while ((line = reader.readNext()) != null) {
            String id = line[0];
            String name = line[1];
            String manufacturer = line[2];
            Integer pieces = Integer.parseInt(line[3]);
            String difficulty = line[4];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(line[5].substring(1)));
            BigDecimal wholesaleCost = BigDecimal.valueOf(Double.parseDouble(line[6].substring(1)));
            String category = line[7];
            String shortDescription = line[8];
            String longDescription = line[9];

            p.add(new Puzzle(id, name, manufacturer, manufacturerRepo.findManufacturerByManufacturerName(manufacturer),
                    pieces, difficulty, price, wholesaleCost, category, shortDescription, longDescription));
        }

        puzzleRepo.saveAll(p);
        reader.close();
    }
}
