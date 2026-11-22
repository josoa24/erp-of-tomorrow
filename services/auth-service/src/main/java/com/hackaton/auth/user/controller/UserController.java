package com.hackaton.auth.user.controller;

import com.hackaton.auth.common.identity.IdentityHeaders;
import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.dto.UserPage;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.dto.UserRolesUpdateRequest;
import com.hackaton.auth.user.dto.UserSearchCriteria;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.service.creator.UserCreator;
import com.hackaton.auth.user.service.reader.UserReader;
import com.hackaton.auth.user.service.updater.UserUpdater;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final int MAX_SEARCH = 120;

    public static final String TOTAL_COUNT = "X-Total-Count";

    private final UserCreator userCreator;
    private final UserReader userReader;
    private final UserUpdater userUpdater;

    @Operation(summary = "Liste les comptes (filtres et pagination facultatifs, total dans X-Total-Count)")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(@RequestParam(required = false) String search,
                                                      @RequestParam(name = "role", required = false) List<Role> roles,
                                                      @RequestParam(required = false) Boolean enabled,
                                                      @RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer size) {
        UserSearchCriteria criteria = new UserSearchCriteria(search == null || search.length() <= MAX_SEARCH ? search : search.substring(0, MAX_SEARCH), roles, enabled, page, size);
        if (criteria.isUnfiltered()) {
            List<UserResponse> all = userReader.findAll();
            return withTotal(all, all.size());
        }
        UserPage result = userReader.search(criteria);
        return withTotal(result.items(), result.total());
    }

    private ResponseEntity<List<UserResponse>> withTotal(List<UserResponse> items, long total) {
        return ResponseEntity.ok().header(TOTAL_COUNT, String.valueOf(total)).body(items);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreator.create(request));
    }

    @PutMapping("/{id}")
    public UserResponse changeRoles(@PathVariable Long id, @Valid @RequestBody UserRolesUpdateRequest request,
                                   @Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long actorId) {
        return userUpdater.changeRoles(id, request.effectiveRoles(), actorId);
    }

    @PostMapping("/{id}/disable")
    public UserResponse disable(@PathVariable Long id,
                                @Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long actorId) {
        return userUpdater.setEnabled(id, false, actorId);
    }

    @PostMapping("/{id}/enable")
    public UserResponse enable(@PathVariable Long id,
                               @Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long actorId) {
        return userUpdater.setEnabled(id, true, actorId);
    }
}
