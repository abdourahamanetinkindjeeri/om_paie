package com.odc.om.paie.authenticated.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.odc.om.paie.authenticated.Constants.APP_ROOT;

@RestController
@RequestMapping(APP_ROOT+"auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
}
