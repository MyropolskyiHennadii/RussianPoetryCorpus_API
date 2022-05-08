package ruCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "strophes")
public class Strophe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_strophes;
    @Column
    private String rhyme_type;
    @Column
    private String strophe_type;
    @Column
    private String solid_form;

    @OneToOne
    @JoinColumn(name = "id_metrical_line")
    @JsonBackReference//important to prevent infinite loop of references
    private LineOfPoem lineOfPoem;

    public Strophe() {
    }

    public Strophe(String rhyme_type, String strophe_type, String solid_form, LineOfPoem lineOfPoem) {
        this.rhyme_type = rhyme_type;
        this.strophe_type = strophe_type;
        this.solid_form = solid_form;
        this.lineOfPoem = lineOfPoem;
    }

    public int getId_strophes() {
        return id_strophes;
    }

    public String getRhyme_type() {
        return rhyme_type;
    }

    public String getStrophe_type() {
        return strophe_type;
    }

    public String getSolid_form() {
        return solid_form;
    }

    public LineOfPoem getLineOfPoem() {
        return lineOfPoem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Strophe)) return false;
        Strophe strophe = (Strophe) o;
        return getId_strophes() == strophe.getId_strophes();
    }

    @Override
    public int hashCode() {
        return getId_strophes();
    }

    @Override
    public String toString() {
        return "Strophe{" +
                "id_strophes=" + id_strophes +
                ", rhyme_type='" + rhyme_type + '\'' +
                ", strophe_type='" + strophe_type + '\'' +
                ", solid_form='" + solid_form + '\'' +
                ", lineOfPoem=" + lineOfPoem.getId_metrical_line() +
                '}';
    }

    /**
     * composes json-representation for book-exemplar
     */
    public JSONObject composeJsonObject() {
        JSONObject jsonStrophe = new JSONObject();
        jsonStrophe.put("id_strophes", id_strophes);
        jsonStrophe.put("rhyme_type", rhyme_type);
        jsonStrophe.put("strophe_type", strophe_type);
        jsonStrophe.put("solid_form", solid_form);
        return jsonStrophe;
    }
}
