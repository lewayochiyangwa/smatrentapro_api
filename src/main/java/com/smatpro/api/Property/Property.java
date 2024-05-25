package com.smatpro.api.Property;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Property")
public class Property {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public long id;
    public String name;
    public String address;
    public String description1;
    public double amount;

}
