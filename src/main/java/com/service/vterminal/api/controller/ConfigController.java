package com.service.vterminal.api.controller;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.vterminal.ConfigUtils;
import com.service.vterminal.api.dto.ConfigResponse;
import com.service.vterminal.model.ConfigValidationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.service.vterminal.ConfigUtils.FILE_PATH;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private final Logger logger = Logger.getLogger(ConfigController.class.getName());

    @Autowired
    ObjectMapper objectMapper;

    @Value("${upload.filepath}")
    private String filePath;



    @GetMapping(path = "/get-config-data")
    public ResponseEntity<ConfigResponse> getConfigData() {
        try {
            File file = Paths.get(filePath).toFile();
            if (!file.exists()) {
                return new ResponseEntity<>(new ConfigResponse("Config not found"), HttpStatus.NOT_FOUND);
            } else {
                ConfigValidationSchema configValidationSchema = objectMapper.readValue(file, ConfigValidationSchema.class);
                ConfigResponse res = ConfigUtils.buildConfigResponse(configValidationSchema, file.getAbsolutePath());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        } catch (IOException e) {
            String safeErrorMsg = "Failed to read config file";
            logger.log(Level.SEVERE, safeErrorMsg, e);
            return new ResponseEntity<>(new ConfigResponse(safeErrorMsg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/set-config-data")
    public ResponseEntity<ConfigResponse> setConfigData(@RequestBody ConfigValidationSchema configValidationSchema) {
        try {
            File file = Paths.get(filePath).toFile();
            if (!file.exists()) {
                return new ResponseEntity<>(new ConfigResponse("Config not found"), HttpStatus.NOT_FOUND);
            }
            objectMapper.writeValue(file, configValidationSchema);
            ConfigResponse res = ConfigUtils.buildConfigResponse(configValidationSchema, file.getAbsolutePath());
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        }catch (JsonMappingException exc) {
            String safeJSONDeserializeErrorMsg = "Bad Request";
            logger.log(Level.SEVERE, safeJSONDeserializeErrorMsg, exc);
            return new ResponseEntity<>(new ConfigResponse(safeJSONDeserializeErrorMsg), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            String safeErrorMsg = "Failed to write config to json";
            logger.log(Level.SEVERE, safeErrorMsg, e);
            return new ResponseEntity<>(new ConfigResponse(safeErrorMsg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
