package russianCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_author;
    @Column
    private String authors_name;

    @OneToMany(targetEntity = BookSource.class, mappedBy = "author", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    //@JsonIgnore
    private Set<BookSource> bookSources = new HashSet<>();// foreign key in database. One Author = many Book_sources


    public Author() {
    }

    public Author(String authors_name) {
        this.authors_name = authors_name;
    }

    public int getId_author() {
        return id_author;
    }

    public Set<BookSource> getBookSources() {
        return bookSources;
    }

    public String getAuthors_name() {
        return authors_name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id_author=" + id_author +
                ", authors_name='" + authors_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return getAuthors_name().equals(author.getAuthors_name());
    }

    @Override
    public int hashCode() {
        return getAuthors_name().length();
    }
}
