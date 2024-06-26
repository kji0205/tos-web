package kr.go.togetherschool.tosweb.service;


import kr.go.togetherschool.tosweb.common.ErrorStatus;
import kr.go.togetherschool.tosweb.entity.Image;
import kr.go.togetherschool.tosweb.dto.MemberDtoConverter;
import kr.go.togetherschool.tosweb.dto.MemberRequestDto;
import kr.go.togetherschool.tosweb.dto.MemberResponseDto;
import kr.go.togetherschool.tosweb.entity.CustomUserDetails;
import kr.go.togetherschool.tosweb.entity.Member;
import kr.go.togetherschool.tosweb.handler.ErrorHandler;
import kr.go.togetherschool.tosweb.model.ActiveState;
import kr.go.togetherschool.tosweb.model.ImageType;
import kr.go.togetherschool.tosweb.model.Role;
import kr.go.togetherschool.tosweb.model.SocialType;
import kr.go.togetherschool.tosweb.repository.ImageRepository;
import kr.go.togetherschool.tosweb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
//    private final MemberFollowRepository memberFollowRepository;
//    private final MailService mailService;
    private final ImageRepository imageRepository;
//    private final OracleImageService oracleImageService;

    private static final String DEFAULT_BLOG_SUFFIX = ".blog";

    public MemberResponseDto.MemberTaskResultResponseDto signUp(MemberRequestDto.CreateMemberRequestDto requestDto) {
        String randomNickName = requestDto.getEmail() + UUID.randomUUID().toString().substring(0, 9);
        if (isExistByEmail(requestDto.getEmail())) {
            throw new ErrorHandler(ErrorStatus.MEMBER_EMAIL_ALREADY_EXIST);
        }

        if (isExistByMemberId(requestDto.getMemberId())) {
            throw new ErrorHandler(ErrorStatus.MEMBER_ID_ALREADY_EXIST);
        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Member member = Member.builder()
                .memberId(requestDto.getMemberId())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickName(randomNickName)
                .email(requestDto.getEmail())
                .activeState(ActiveState.ACTIVE)
                .role(Role.ROLE_GUEST)
                .socialType(SocialType.LOCAL)
                .build();
        memberRepository.save(member);
        log.info("로컬 회원가입에 성공하였습니다. memberIdx = {}, memberId = {}, nickName = {}", member.getIdx(), member.getMemberId(), member.getNickName());

        // 개발용 관리자 계정
        if (member.getEmail().equals("dh1010a@gmail.com")) {
            member.setRole(Role.ROLE_ADMIN);
        }

        return MemberDtoConverter.convertToMemberTaskDto(member);
    }

    public Member createDefaultOAuth2Member(CustomUserDetails oAuth2User) {
        String randomNickName = oAuth2User.getMemberName().substring(1, 3) + UUID.randomUUID().toString().substring(0, 9);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Member member = Member.builder()
                .memberId(oAuth2User.getMemberId())
                .password(oAuth2User.getPassword())
                .nickName(randomNickName)
                .email(oAuth2User.getEmail())
                .role(Role.ROLE_GUEST)
                .activeState(ActiveState.ACTIVE)
                .socialType(oAuth2User.getSocialType())
                .build();
        log.info("신규 소셜 회원입니다. 등록을 진행합니다. memberId = {}, email = {}, socialType = {}", member.getMemberId(), member.getEmail(), member.getSocialType().getSocialName());
        memberRepository.save(member);
        return member;
    }

    public MemberResponseDto.MemberTaskResultResponseDto commonSignUp(MemberRequestDto.CommonCreateMemberRequestDto requestDto, String memberId) throws Exception{
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        checkProfileImageExistsAndDelete(member);
        Image image = Image.builder()
                .accessUri(requestDto.getProfileImage().getAccessUri())
                .authenticateId(requestDto.getProfileImage().getAuthenticateId())
                .imgUrl(requestDto.getProfileImage().getImgUrl())
                .imageType(ImageType.PROFILE)
                .member(member)
                .build();
        imageRepository.save(image);

        member.updateNickName(requestDto.getNickName());
        member.updateBlogName(requestDto.getBlogName());
        member.updateBlogIntroduce(requestDto.getBlogIntroduce());
        member.setRole(Role.ROLE_MEMBER);
        log.info("공통 회원가입이 완료되었습니다. memberId = {}, nickName = {}, blogName = {}, blogIntroduce = {}, profileImgUrl = {}", member.getMemberId(), member.getNickName(), member.getBlogName(), member.getBlogIntroduce(), image.getAccessUri());
        return MemberDtoConverter.convertToMemberTaskDto(member);

    }

    public MemberResponseDto.IsNewMemberResponseDto isNewMember(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        boolean isNewMember = member.getRole() == Role.ROLE_GUEST;

        return MemberResponseDto.IsNewMemberResponseDto.builder()
                .idx(member.getIdx())
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .isNewMember(isNewMember)
                .build();
    }

    public MemberResponseDto.MemberInfoResponseDto getMyInfo(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberDtoConverter.convertToInfoResponseDto(member);
    }


    public MemberResponseDto.MemberTaskSuccessResponseDto changePassword(MemberRequestDto.ChangePasswordRequestDto requestDto, String code) {
        log.info("비밀번호 변경 요청이 들어왔습니다. email = {}", requestDto.getEmail());
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        if (mailService.checkEmail(requestDto.getEmail(), code).isSuccess()) {
//            member.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
//            mailService.finishCheckEmail(code);
//        } else {
//            throw new ErrorHandler(ErrorStatus._FORBIDDEN);
//        }
        return MemberResponseDto.MemberTaskSuccessResponseDto.builder()
                .isSuccess(true)
                .build();

    }

    public void checkProfileImageExistsAndDelete(Member member) throws Exception{
        List<Image> images = member.getImages();
        if (!images.isEmpty()) {
            Image profileImage = images.stream().filter(Image::isProfileImage).findAny().orElse(null);
            if (profileImage != null) {
//                oracleImageService.deleteImg(profileImage.getId());
                imageRepository.delete(profileImage);
            }
        }
    }

    public MemberResponseDto.EmailResponseDto findEmailByNickName(String nickName) {
        Member member = memberRepository.findByNickName(nickName).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return MemberResponseDto.EmailResponseDto.builder()
                .email(member.getEmail())
                .build();
    }

    public String getSocialTypeByEmail(String email) {
        if (!isExistByEmail(email)) {
            return null;
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return member.getSocialType().getSocialName();
    }

    public String getSocialTypeByMemberId(String memberId) {
        if (!isExistByMemberId(memberId)) {
            return null;
        }
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        return member.getSocialType().getSocialName();
    }

    public MemberResponseDto.BookMarkResponseDto getBookmarkList(String memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Long> bookmarkList = new ArrayList<>();
//        if (!member.getBookMarks().isEmpty()) {
//            for (BookMark x : member.getBookMarks()) {
//                bookmarkList.add(x.getId());
//            }
//        }
        return MemberResponseDto.BookMarkResponseDto.builder()
                .bookMarkList(bookmarkList)
                .build();
    }

    public boolean isExistByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isExistByNickName(String nickName) {
        return memberRepository.existsByNickName(nickName);
    }

    public boolean isExistByMemberId(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    public boolean isExistByBlogName(String blogName) {
        return memberRepository.existsByBlogName(blogName);
    }

    public String deleteByMemberId(String memberId) {
        if (!memberRepository.existsByMemberId(memberId)) {
            return "그런 회원 없어요 씌파!";
        }
        memberRepository.deleteByMemberId(memberId);
        return "삭제 완료";
    }

    // 관리자용
    public String deleteAllMember() {
        memberRepository.deleteAll();
        return "전체 삭제 완료";
    }
}
