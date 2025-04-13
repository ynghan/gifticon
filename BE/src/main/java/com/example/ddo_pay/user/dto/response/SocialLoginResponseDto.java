
package com.example.ddo_pay.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SocialLoginResponseDto {
    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    private String name;
    // 추가: 전화번호가 없는 경우 프론트에 알려주기 위한 플래그
    private boolean phoneNumberMissing;
}
