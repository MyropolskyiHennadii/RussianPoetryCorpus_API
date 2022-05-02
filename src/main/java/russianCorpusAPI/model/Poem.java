package russianCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "poems")
public class Poem {

    @Column
    double coefficient_monotone;
    @Column
    String dedicated_to;
    @Column
    String epigraph;
    @Column
    int max_number_of_stress_in_lines;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_verse;
    @Column
    private int old_id;
    @Column
    private String translate_from;
    @Column
    private String meter_group;
    @Column
    private String poem_title;
    @Column
    private String poem_info;
    @Column
    private String poem_link;
    @Column
    private String poem_date;
    @Column
    private int poem_year;
    @ManyToOne
    @JoinColumn(name = "id_book_source")
    @JsonBackReference//important to prevent infinite loop of references
    private BookSource book_source;//foreign key in database

    @OneToMany(targetEntity = LineOfPoem.class, mappedBy = "poem", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    //@JsonIgnore
    private Set<LineOfPoem> linesOfPoem = new HashSet<>();// foreign key in database. One Book = many Poems

    @Transient
    private List<LineOfPoem> linesOfPoemList = new ArrayList<>();

    public Poem() {
    }

    public Poem(int old_id, String translate_from, String meter_group,
                String poem_title, String poem_info, String poem_link,
                double coefficient_monotone,
                String dedicated_to,
                String epigraph,
                String poem_date,
                int max_number_of_stress_in_lines,
                int poem_year,
                BookSource book_source) {
        this.old_id = old_id;
        this.translate_from = translate_from;
        this.meter_group = meter_group;
        this.poem_title = poem_title;
        this.poem_info = poem_info;
        this.poem_link = poem_link;
        this.coefficient_monotone = coefficient_monotone;
        this.dedicated_to = dedicated_to;
        this.epigraph = epigraph;
        this.poem_date = poem_date;
        this.poem_year = poem_year;
        this.max_number_of_stress_in_lines = max_number_of_stress_in_lines;
        this.book_source = book_source;
    }

    public Poem(int old_id, String translate_from, String meter_group,
                String poem_title, String poem_info, String poem_link,
                double coefficient_monotone, String poem_date,
                int max_number_of_stress_in_lines,
                BookSource book_source) {
        this.old_id = old_id;
        this.translate_from = translate_from;
        this.meter_group = meter_group;
        this.poem_title = poem_title;
        this.poem_info = poem_info;
        this.poem_link = poem_link;
        this.coefficient_monotone = coefficient_monotone;
        this.poem_date = poem_date;
        this.max_number_of_stress_in_lines = max_number_of_stress_in_lines;
        this.book_source = book_source;
    }

    public List<LineOfPoem> getLinesOfPoemList() {
        return linesOfPoemList;
    }

    public void setLinesOfPoemList(List<LineOfPoem> linesOfPoemList) {
        this.linesOfPoemList = linesOfPoemList;
    }

    public String getPoem_date() {
        return poem_date;
    }

    public void setPoem_date(String poem_date) {
        this.poem_date = poem_date;
    }

    public int getId_verse() {
        return id_verse;
    }

    public void setId_verse(int id_verse) {
        this.id_verse = id_verse;
    }

    public int getOld_id() {
        return old_id;
    }

    public void setOld_id(int old_id) {
        this.old_id = old_id;
    }

    public String getTranslate_from() {
        return translate_from;
    }

    public void setTranslate_from(String translate_from) {
        this.translate_from = translate_from;
    }

    public String getMeter_group() {
        return meter_group;
    }

    public void setMeter_group(String meter_group) {
        this.meter_group = meter_group;
    }

    public int getMax_number_of_stress_in_lines() {
        return max_number_of_stress_in_lines;
    }

    public void setMax_number_of_stress_in_lines(int max_number_of_stress_in_lines) {
        this.max_number_of_stress_in_lines = max_number_of_stress_in_lines;
    }

    public String getPoem_title() {
        return poem_title;
    }

    public void setPoem_title(String poem_title) {
        this.poem_title = poem_title;
    }

    public String getPoem_info() {
        return poem_info;
    }

    public void setPoem_info(String poem_info) {
        this.poem_info = poem_info;
    }

    public String getPoem_link() {
        return poem_link;
    }

    public void setPoem_link(String poem_link) {
        this.poem_link = poem_link;
    }

    public double getCoefficient_monotone() {
        return coefficient_monotone;
    }

    public void setCoefficient_monotone(double coefficient_monotone) {
        this.coefficient_monotone = coefficient_monotone;
    }

    public String getDedicated_to() {
        return dedicated_to;
    }

    public void setDedicated_to(String dedicated_to) {
        this.dedicated_to = dedicated_to;
    }

    public String getEpigraph() {
        return epigraph;
    }

    public void setEpigraph(String epigraph) {
        this.epigraph = epigraph;
    }

    public BookSource getBook_source() {
        return book_source;
    }

    public void setBook_source(BookSource book_source) {
        this.book_source = book_source;
    }

    public Set<LineOfPoem> getLinesOfPoem() {
        return linesOfPoem;
    }

    public void setLinesOfPoem(Set<LineOfPoem> linesOfPoem) {
        this.linesOfPoem = linesOfPoem;
    }

    public int getPoem_year() {
        return poem_year;
    }

    public void setPoem_year(int poem_year) {
        this.poem_year = poem_year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poem)) return false;
        Poem poem = (Poem) o;
        return id_verse == poem.id_verse;
    }

    @Override
    public int hashCode() {
        return id_verse;
    }

    @Override
    public String toString() {
        return "Poem{" +
                "id_verse=" + id_verse +
                ", old_id=" + old_id +
                ", translate_from='" + translate_from + '\'' +
                ", meter_group='" + meter_group + '\'' +
                ", poem_title='" + poem_title + '\'' +
                ", poem_info='" + poem_info + '\'' +
                ", poem_link='" + poem_link + '\'' +
                ", book_source=" + book_source +
                '}';
    }
}
