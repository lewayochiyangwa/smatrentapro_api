package com.smatpro.api.Invoices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailParams {

    String clientID;
    String email;

    String title;
}

