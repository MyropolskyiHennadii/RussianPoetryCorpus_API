package russianCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books_source")
public class BookSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_book_source;

    @Column
    private String book_title;
    @Column
    private String book_data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_author")
    @JsonBackReference//important to prevent infinite loop of references
    private Author author;//foreign key in database

    @OneToMany(targetEntity = Poem.class, mappedBy = "book_source", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    //@JsonIgnore
    private Set<Poem> poems = new HashSet<>();// foreign key in database. One Book = many Poems

    public BookSource() {
    }

    public BookSource(String book_title, String book_data, Author author) {
        this.book_title = book_title;
        this.book_data = book_data;
        this.author = author;
    }

    public Set<Poem> getPoems() {
        return poems;
    }

    public int getId_book_source() {
        return id_book_source;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_data() {
        return book_data;
    }

    public Author getAuthor() {
        return author;
    }

    public Set<Poem> getBookSources() {
        return poems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookSource)) return false;
        BookSource that = (BookSource) o;
        return getId_book_source() == that.getId_book_source();
    }

    @Override
    public int hashCode() {
        return getId_book_source();
    }

    @Override
    public String toString() {
        return "BookSource{" +
                "id_book_source=" + id_book_source +
                ", author=" + author + '\'' +
                ", book_title='" + book_title + '\'' +
                ", book_data='" + book_data +
                '}';
    }
}
