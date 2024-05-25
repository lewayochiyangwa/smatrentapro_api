package com.smatpro.api.uploads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadJson {
    public String user_id;
    public String ext;
    public String file_content;
}
