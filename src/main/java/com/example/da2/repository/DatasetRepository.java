package com.example.da2.repository;

import com.example.da2.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    // Custom queries can be added here if needed
    Dataset findByDatasetName(String datasetName);
}
