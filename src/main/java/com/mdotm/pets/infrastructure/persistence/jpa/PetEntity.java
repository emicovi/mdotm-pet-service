package com.mdotm.pets.infrastructure.persistence.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String species;

    private Integer age;

    private String ownerName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
