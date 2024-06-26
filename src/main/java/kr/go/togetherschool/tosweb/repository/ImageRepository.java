package kr.go.togetherschool.tosweb.repository;

import kr.go.togetherschool.tosweb.entity.Image;
import kr.go.togetherschool.tosweb.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPost(Post post);

    Image findByImgUrlAndPost(String url, Post post);
}
