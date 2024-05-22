package com.smatpro.api.uploads;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileUploadRequest {
    public String iD;
    public String fileName;
}
