package com.smatpro.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString
public class CustomMessage {
    private String messageId;
    private String message;
    private Date messageDate;
}
