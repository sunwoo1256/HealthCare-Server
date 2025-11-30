package cerberus.HealthCare.user.controller;

import cerberus.HealthCare.global.common.BaseResponse;
import cerberus.HealthCare.global.security.CustomUserDetails;
import cerberus.HealthCare.global.security.JwtToken;
import cerberus.HealthCare.user.dto.LoginRequest;
import cerberus.HealthCare.user.dto.SleepPatternRequest;
import cerberus.HealthCare.user.dto.SleepPatternResponse;
import cerberus.HealthCare.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 인증", description = "사용자 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", description = "사용자 로그인")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호가 일치하지 않습니다.")
    })
    @PostMapping("/sleep-pattern")
    public ResponseEntity<BaseResponse<SleepPatternResponse>> createSleepPattern(
        @RequestBody SleepPatternRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
            SleepPatternResponse sleepPatternResponse = userService.addSleepPattern(userDetails.getUsername(), request.getPattern());
            return BaseResponse.ok("로그인 성공", sleepPatternResponse);
        }catch (Exception e){
            return BaseResponse.unauthorized("이메일 또는 비밀번호가 일치하지 않습니다.", null);
        }
    }
}
