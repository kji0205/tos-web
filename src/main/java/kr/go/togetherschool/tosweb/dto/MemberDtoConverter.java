package kr.go.togetherschool.tosweb.dto;


import kr.go.togetherschool.tosweb.entity.Image;
import kr.go.togetherschool.tosweb.entity.Member;
import kr.go.togetherschool.tosweb.model.InterestedType;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberDtoConverter {

    public static MemberResponseDto.MemberTaskResultResponseDto convertToMemberTaskDto(Member member) {
        return MemberResponseDto.MemberTaskResultResponseDto.builder()
                .idx(member.getIdx())
                .email(member.getEmail())
                .nickName(member.getNickName())
                .build();

    }

    public static MemberResponseDto.MemberInfoResponseDto convertToInfoResponseDto (Member member) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Image> images = member.getImages();
        Image profileImage = images.stream().filter(Image::isProfileImage).findAny().orElse(null);
        Image blogTitleImage = images.stream().filter(Image::isBlogTitleImage).findAny().orElse(null);

        return MemberResponseDto.MemberInfoResponseDto.builder()
                .idx(member.getIdx())
                .memberId(member.getMemberId())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .profileImageUrl(profileImage != null ? profileImage.getAccessUri() : null)
                .blogName(member.getBlogName())
                .blogIntroduce(member.getBlogIntroduce())
                .blogTitleImgUrl(blogTitleImage != null ? blogTitleImage.getAccessUri() : null)
                .role(member.getRole().getTitle())
                .activeStatus(member.getActiveState().name())
                .socialType(member.getSocialType().getSocialName())
                .interestedTypes(member.getInterestedTypes().stream().map(InterestedType::getTitle).toList())
                .build();
    }

    public static MemberResponseDto.MemberFollowResponseDto convertToFollowResponseDto(Member member, Member followingMember) {
        return MemberResponseDto.MemberFollowResponseDto.builder()
                .idx(member.getIdx())
                .memberId(member.getMemberId())
                .nickName(member.getNickName())
                .followingMemberIdx(followingMember.getIdx())
                .followingMemberId(followingMember.getMemberId())
                .followingMemberNickName(followingMember.getNickName())
                .isSuccess(true)
                .build();
    }

    public static MemberResponseDto.FollowMemberInfoDto convertToFollowMemberInfoDto(Member member) {
        return MemberResponseDto.FollowMemberInfoDto.builder()
                .idx(member.getIdx())
                .memberId(member.getMemberId())
                .nickName(member.getNickName())
                .build();
    }
}

