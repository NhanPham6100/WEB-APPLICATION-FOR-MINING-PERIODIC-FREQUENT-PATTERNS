package com.example.da2.service;

import com.example.da2.entity.Dataset;
import com.example.da2.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;
//    public Optional<Dataset> getDatasetById(Long id) {
//        return datasetRepository.findById(id);
//    }

    public void deleteDataset(Long id) {
        datasetRepository.deleteById(id);
    }
    public Dataset saveDataset(Dataset dataset) {
        return datasetRepository.save(dataset);
    }
    public Dataset getDatasetByName(String datasetName) {
        return datasetRepository.findByDatasetName(datasetName);
    }
    // Get all datasets
    public List<Dataset> getAllDatasets() {
        // Retrieve all datasets from the database
        return datasetRepository.findAll();
    }
}
