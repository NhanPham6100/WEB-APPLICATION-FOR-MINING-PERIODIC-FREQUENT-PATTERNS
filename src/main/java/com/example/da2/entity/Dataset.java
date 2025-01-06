package com.example.da2.entity;


import jakarta.persistence.*;

@Entity
public class Dataset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generates a value
    private Long id;
    @Column(name = "dataset_name", length = 255)
    private String datasetName;


    @Column(name = "dataset", columnDefinition = "mediumblob")
    private byte[] dataset;

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    // Constructor
    public Dataset() {}

    public Dataset(String datasetName, byte[] dataset) {
        this.datasetName = datasetName;
        this.dataset= dataset;
    }
    public Dataset(Long id, String datasetName, byte[] dataset){
        this.id = id;
        this.datasetName = datasetName;
        this.dataset = dataset;
    }

    // Getters
    public Long getId(){
        return id;
    }

    public byte[] getDataset(){
        return dataset;
    }

    // Setters
    public void setId(Long id){
        this.id = id;
    }
    public void setDataset(byte[] dataset){
        this.dataset =dataset;
    }
}
