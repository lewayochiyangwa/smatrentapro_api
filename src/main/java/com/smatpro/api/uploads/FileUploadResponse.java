package com.smatpro.api.uploads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileUploadResponse {
    public String id;
    public String fileName;
}
