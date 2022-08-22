package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.api.v1.dto.ProfileDto;
import com.zor07.nofapp.api.v1.dto.RelapseLogDto;
import com.zor07.nofapp.api.v1.mapper.RelapseLogMapper;
import com.zor07.nofapp.service.RelapseLogService;
import com.zor07.nofapp.service.UserService;
import com.zor07.nofapp.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/profiles/{userId}/relapses")
@Api( tags = "Profile" )
public class RelapseLogController {

    private final RelapseLogService relapseLogService;
    private final RelapseLogMapper mapper;
    private final UserService userService;

    public RelapseLogController(final RelapseLogService relapseLogService,
                                final RelapseLogMapper mapper,
                                final UserService userService) {
        this.relapseLogService = relapseLogService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "Retrieves relapse log entries of current user", response = ProfileDto.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved relapse log entries of current user"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<List<RelapseLogDto>> getRelapseLogEntries(final @PathVariable Long userId) {
        final var logs = relapseLogService.getRelapseLogEntriesByUserId(userId)
                .stream()
                .map(e -> mapper.toDto(e, DateUtils.SYSTEM_TIMEZONE)).toList();
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/{relapseLogId}")
    @ApiOperation(value = "Removes relapse log entry")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully removed log entry"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<Void> removePostFromProfile(final @ApiIgnore Principal principal,
                                                      final @PathVariable Long userId,
                                                      final @PathVariable Long relapseLogId) {
        final var user = userService.getUser(principal);
        if (!Objects.equals(userId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        relapseLogService.deleteRelapseLog(relapseLogId, userId);
        return ResponseEntity.noContent().build();
    }

}
