package kr.go.togetherschool.tosweb.dto;


import lombok.*;

import java.util.List;

public class MemberRequestDto {

    @Builder
    @Data
    public static class CreateMemberRequestDto {
        private String memberId;
        private String password;
        private String email;
    }

    @Builder
    @Data
    public static class CommonCreateMemberRequestDto {
        private ImageDto profileImage;
        private String nickName;
        private String blogName;
        private String blogIntroduce;
    }


    @Builder
    @Data
    public static class ChangePasswordRequestDto {
        private String email;
        private String newPassword;
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateInterestedTypesRequestDto {
        private List<String> koreanInterestedTypes;
    }
}