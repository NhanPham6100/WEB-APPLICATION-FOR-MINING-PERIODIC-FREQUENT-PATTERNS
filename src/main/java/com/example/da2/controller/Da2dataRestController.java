package com.example.da2.controller;

import com.example.da2.entity.Dataset;
import com.example.da2.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/data")
public class Da2dataRestController {
    @Autowired
    private DatasetService datasetService;
    // Endpoint to save a new dataset
    @PostMapping("/save")
    public Dataset saveDataset(@RequestBody Dataset dataset) {
        return datasetService.saveDataset(dataset);
    }
    // Endpoint to get a dataset by name
    @GetMapping("/find/{name}")
    public Dataset getDatasetByName(@PathVariable String name) {
        return datasetService.getDatasetByName(name);
    }
    // Endpoint to get all users
    @GetMapping("/all")
    public List<Dataset> getAllDatasets() {
        return datasetService.getAllDatasets();
    }
}
