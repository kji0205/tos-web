package kr.go.togetherschool.tosweb.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String isbn;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonManagedReference
    private Author author;

//    @JsonBackReference
//    @OneToMany(mappedBy = "book",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Lend> lends;
}