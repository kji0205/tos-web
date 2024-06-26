package kr.go.togetherschool.tosweb.entity;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import kr.go.togetherschool.tosweb.model.SocialType;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Builder
public class CustomUserDetails implements UserDetails {

    private String id;
    private String email;
    private String password;
    private String name;
    private SocialType socialType;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    public static CustomUserDetails create(Member member) {
        return CustomUserDetails.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .password(member.getPassword())
//                .socialType(member.getSocialType())
                .authorities(AuthorityUtils.createAuthorityList(member.getRole().toString()))
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void updateAuthorities(Member member) {
        this.authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    public String getMemberId() {
        return this.id;
    }

    public String getMemberName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public SocialType getSocialType() {
        return this.socialType;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}