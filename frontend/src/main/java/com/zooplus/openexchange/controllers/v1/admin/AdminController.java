package com.zooplus.openexchange.controllers.v1.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zooplus.openexchange.controllers.v1.Version;
import com.zooplus.openexchange.protocol.v1.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
class AdminController extends Version {
    private final static String VCAP_APPLICATION = "VCAP_APPLICATION";

    @Autowired
    private Environment environment;

    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = STATUS_PATH)
    @ResponseBody
    ResponseEntity<Status> get() throws IOException {
        return new ResponseEntity<>(new ObjectMapper().readValue(environment.getProperty(VCAP_APPLICATION), Status.class), HttpStatus.OK);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Error occurred!")
    @ExceptionHandler({IOException.class, NullPointerException.class})
    public void errorHandler() {}
}