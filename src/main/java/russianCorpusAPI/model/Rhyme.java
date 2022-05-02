package russianCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "rhymes")
public class Rhyme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_rhymes;
    @Column
    private String rhyme_sort;
    @Column
    private String rhyme_kind;
    @Column
    private String rhyme_gender;
    @Column
    private String grammatical;
    @Column
    private String clause;

    @OneToOne
    @JoinColumn(name = "id_metrical_line")
    @JsonBackReference//important to prevent infinite loop of references
    private LineOfPoem lineOfPoem;

    public Rhyme() {
    }

    public Rhyme(String rhyme_sort, String rhyme_kind, String rhyme_gender, String grammatical, String clause, LineOfPoem lineOfPoem) {
        this.rhyme_sort = rhyme_sort;
        this.rhyme_kind = rhyme_kind;
        this.rhyme_gender = rhyme_gender;
        this.grammatical = grammatical;
        this.clause = clause;
        this.lineOfPoem = lineOfPoem;
    }

    public int getId_rhymes() {
        return id_rhymes;
    }

    public String getRhyme_sort() {
        return rhyme_sort;
    }

    public String getRhyme_kind() {
        return rhyme_kind;
    }

    public String getRhyme_gender() {
        return rhyme_gender;
    }

    public String getGrammatical() {
        return grammatical;
    }

    public String getClause() {
        return clause;
    }

    public LineOfPoem getLineOfPoem() {
        return lineOfPoem;
    }

    @Override
    public String toString() {
        return "Rhyme{" +
                "id_rhymes=" + id_rhymes +
                ", rhyme_srt='" + rhyme_sort + '\'' +
                ", rhyme_kin='" + rhyme_kind + '\'' +
                ", rhyme_chr='" + rhyme_gender + '\'' +
                ", grammatical='" + grammatical + '\'' +
                ", clause='" + clause + '\'' +
                ", lineOfPoem=" + lineOfPoem.getId_metrical_line() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rhyme)) return false;
        Rhyme rhyme = (Rhyme) o;
        return getId_rhymes() == rhyme.getId_rhymes();
    }

    @Override
    public int hashCode() {
        return getId_rhymes();
    }
}
