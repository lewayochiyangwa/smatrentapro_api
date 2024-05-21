package com.smatpro.api.Property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepo extends JpaRepository<Property, Long> {

    List<Property> findAll();
    Property getPropertyById(int id);

    Property save(Property country);

    void deleteById(long Id);

}
