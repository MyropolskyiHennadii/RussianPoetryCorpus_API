package ruCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "grammatical_lines")
public class GrammaticalAnalysisOfLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_grammatical_analysis;
    @Column
    private String representation;

    @OneToOne
    @JoinColumn(name = "id_metrical_line")
    @JsonBackReference//important to prevent infinite loop of references
    private LineOfPoem lineOfPoem;

    public GrammaticalAnalysisOfLine() {
    }

    public GrammaticalAnalysisOfLine(LineOfPoem lineOfPoem, String representation) {
        this.representation = representation;
        this.lineOfPoem = lineOfPoem;
    }

    public int getId_grammatical_analysis() {
        return id_grammatical_analysis;
    }

    public String getRepresentation() {
        return representation;
    }

    public LineOfPoem getLineOfPoem() {
        return lineOfPoem;
    }

    @Override
    public String toString() {
        return "GrammaticalAnalysisOfLine{" +
                "id_grammatical_analysis=" + id_grammatical_analysis +
                ", representation='" + representation + '\'' +
                ", lineOfPoem=" + lineOfPoem.getId_metrical_line() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrammaticalAnalysisOfLine)) return false;
        GrammaticalAnalysisOfLine that = (GrammaticalAnalysisOfLine) o;
        return getId_grammatical_analysis() == that.getId_grammatical_analysis();
    }

    @Override
    public int hashCode() {
        return getId_grammatical_analysis();
    }

    /**
     * composes json-representation for book-exemplar
     */
    public JSONObject composeJsonObject() {
        JSONObject jsonGrammarLine = new JSONObject();
        jsonGrammarLine.put("id_grammatical_analysis", id_grammatical_analysis);
        jsonGrammarLine.put("representation", representation);
        return jsonGrammarLine;
    }
}
