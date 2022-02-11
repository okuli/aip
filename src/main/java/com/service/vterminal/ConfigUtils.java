package com.service.vterminal;

import com.service.vterminal.api.dto.ConfigResponse;
import com.service.vterminal.model.ConfigValidationSchema;

public class ConfigUtils {
    public static final String FILE_PATH = "config.json";

    public static ConfigResponse buildConfigResponse(ConfigValidationSchema schema, String filePath) {
        return ConfigResponse.builder()
                .configValidationSchema(schema)
                .filePath(filePath)
                .build();
    }
}
