package kr.go.togetherschool.tosweb.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kr.go.togetherschool.tosweb.model.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String body;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<BookMark> bookMarks;
//
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<Like> likes;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Image> images;

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<Tag> tag;
//
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<Comment> comments;
//
//    public void updatePost(PostRequestDto.UpdatePostRequestDto requestDto) {
//        this.title = requestDto.getTitle();
//        this.body = requestDto.getBody();
//        this.postType = requestDto.getPostType();
//        this.location = requestDto.getLocation();
//    }
//
//    public void updateTags(List<Tag> newTags) {
//
//        this.tag = newTags;
//    }

    public void updateImages(List<Image> newImages) {
        this.images = newImages;
    }

}
