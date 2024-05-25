package com.smatpro.api.Helpers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginReturner {
    public String email;
    public String accessToken;
    public String refreshToken;
}
