package kr.go.togetherschool.tosweb.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

public class MemberResponseDto {

    @Data
    @Builder
    public static class MemberTaskResultResponseDto {
        private Long idx;
        private String email;
        private String nickName;
    }

    @Data
    @Builder
    public static class IsNewMemberResponseDto {
        private Long idx;
        private String provider;
        private String memberId;
        private String email;
        private boolean isNewMember;
    }

    @Data
    @Builder
    public static class MemberInfoResponseDto{
        private Long idx;
        private String memberId;
        private String nickName;
        private String email;
        private String profileImageUrl;
        private String blogName;
        private String blogTitleImgUrl;
        private String blogIntroduce;
        private String activeStatus;
        private String role;
        private String socialType;
        private List<String> interestedTypes;

    }

    @Data
    @Builder
    public static class IsDuplicatedDto {
        private String message;
        private boolean isDuplicated;
    }

    @Data
    @Builder
    public static class MemberFollowResponseDto {
        private Long idx;
        private String memberId;
        private String nickName;
        private Long followingMemberIdx;
        private String followingMemberId;
        private String followingMemberNickName;
        private boolean isSuccess;

    }

    @Data
    @Builder
    public static class FollowMemberInfoDto {
        private Long idx;
        private String memberId;
        private String nickName;
//        private String profileImageUrl;
    }

    @Data
    @Builder
    public static class MemberFollowerResponseDto {
        private List<FollowMemberInfoDto> followers;
    }

    @Data
    @Builder
    public static class MemberFollowingResponseDto {
        private List<FollowMemberInfoDto> followings;
    }

    @Data
    @Builder
    public static class MemberTaskSuccessResponseDto {
        private boolean isSuccess;
    }

    @Data
    @Builder
    public static class EmailResponseDto {
        private String email;
    }

    @Data
    @Builder
    public static class BookMarkResponseDto {
        private List<Long> bookMarkList;
    }

}

