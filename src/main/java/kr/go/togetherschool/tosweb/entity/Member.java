package kr.go.togetherschool.tosweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kr.go.togetherschool.tosweb.model.ActiveState;
import kr.go.togetherschool.tosweb.model.InterestedType;
import kr.go.togetherschool.tosweb.model.Role;
import kr.go.togetherschool.tosweb.model.SocialType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idx")
    private Long idx;

    private String memberId;

    private String password;

    private String nickName;

    //    @Column(nullable = false, unique = true)
    private String email;

    private String blogName;

    private String blogIntroduce;

    @Column(length = 1000)
    private String refreshToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts;

//    @OneToMany(mappedBy = "member")
//    @JsonIgnore
//    private List<BookMark> bookMarks;

//    @OneToMany(mappedBy = "member")
//    @JsonIgnore
//    private List<Like> likes;
//
//    @OneToMany(mappedBy = "member")
//    @JsonIgnore
//    private List<MemberBadge> memberBadges;

//    @OneToMany(mappedBy = "member")
//    @JsonIgnore
//    private List<MemberFollow> memberFollows;
    //
//    @OneToMany(mappedBy = "member")
//    @JsonIgnore
//    private List<MemberTicket> memberTickets;
//
    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ActiveState activeState;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<InterestedType> interestedTypes = new ArrayList<>();

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<Comment> comments;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    public void setSocialType(SocialType type) {
        this.socialType = type;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateBlogName(String blogName) {
        this.blogName = blogName;
    }

    public void updateBlogIntroduce(String blogIntroduce) {
        this.blogIntroduce = blogIntroduce;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateInfo(String nickName, String blogName, String blogIntroduce) {
        this.nickName = nickName;
        this.blogName = blogName;
        this.blogIntroduce = blogIntroduce;
    }

}
