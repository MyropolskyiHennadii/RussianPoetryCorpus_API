package ruCorpusAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "metrical_lines")
public class LineOfPoem {

    @Column
    String ending;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_metrical_lines;
    @Column
    private String meter_group;
    @Column
    private int number_of_tonic_feet = 0;
    @Column
    private int irregularity_on_syllable = 0;
    @Column
    private int length = 0;
    @Column
    private int row_key;
    @Column
    private String line;
    @Column
    private String representation;
    @Column
    private String representation_with_spaces;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_verse")
    @JsonBackReference//important to prevent infinite loop of references
    private Poem poem;//foreign key in database

    @OneToOne(targetEntity = GrammaticalAnalysisOfLine.class, mappedBy = "lineOfPoem", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    private GrammaticalAnalysisOfLine grammaticalAnalysisOfLine;

    @OneToOne(targetEntity = Rhyme.class, mappedBy = "lineOfPoem", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    private Rhyme rhyme;

    @OneToOne(targetEntity = Strophe.class, mappedBy = "lineOfPoem", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference//!!! important to prevent infinite loop with json references
    private Strophe strophe;

    public LineOfPoem() {
    }

    public LineOfPoem(String meter_group, int row_key, String line, String representation, String representation_with_spaces, Poem poem) {
        this.meter_group = meter_group;
        this.row_key = row_key;
        this.line = line;
        this.representation = representation;
        this.representation_with_spaces = representation_with_spaces;
        this.poem = poem;
    }

    public int getId_metrical_line() {
        return id_metrical_lines;
    }

    public GrammaticalAnalysisOfLine getGrammaticalAnalysisOfLine() {
        return grammaticalAnalysisOfLine;
    }

    public void setGrammaticalAnalysisOfLine(GrammaticalAnalysisOfLine grammaticalAnalysisOfLine) {
        this.grammaticalAnalysisOfLine = grammaticalAnalysisOfLine;
    }

    public int getRow_key() {
        return row_key;
    }

    public void setRow_key(int row_key) {
        this.row_key = row_key;
    }

    public void setId_metrical_lines(int id_metrical_lines) {
        this.id_metrical_lines = id_metrical_lines;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public String getRepresentation_with_spaces() {
        return representation_with_spaces;
    }

    public void setRepresentation_with_spaces(String representation_with_spaces) {
        this.representation_with_spaces = representation_with_spaces;
    }

    public Poem getPoem() {
        return poem;
    }

    public void setPoem(Poem poem) {
        this.poem = poem;
    }

    public String getMeter_group() {
        return meter_group;
    }

    public void setMeter_group(String meter_group) {
        this.meter_group = meter_group;
    }

    public Rhyme getRhyme() {
        return rhyme;
    }

    public void setRhyme(Rhyme rhyme) {
        this.rhyme = rhyme;
    }

    public Strophe getStrophe() {
        return strophe;
    }

    public void setStrophe(Strophe strophe) {
        this.strophe = strophe;
    }

    public int getNumber_of_tonic_feet() {
        return number_of_tonic_feet;
    }

    public void setNumber_of_tonic_feet(int number_of_tonic_feet) {
        this.number_of_tonic_feet = number_of_tonic_feet;
    }

    public int getIrregularity_on_syllable() {
        return irregularity_on_syllable;
    }

    public void setIrregularity_on_syllable(int irregularity_on_syllable) {
        this.irregularity_on_syllable = irregularity_on_syllable;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getEnding() {
        return ending;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineOfPoem)) return false;
        LineOfPoem that = (LineOfPoem) o;
        return id_metrical_lines == that.id_metrical_lines;
    }

    @Override
    public int hashCode() {
        return id_metrical_lines;
    }

    @Override
    public String toString() {
        return "LineOfPoem{" +
                "id_metrical_line=" + id_metrical_lines +
                ", row_key=" + row_key +
                ", line='" + line + '\'' +
                ", representation='" + representation + '\'' +
                ", representation_with_spaces='" + representation_with_spaces + '\'' +
                ", meter=" + meter_group +
                '}';
    }

    /**
     * composes json-representation for book-exemplar
     */
    public JSONObject composeJsonObject() {
        JSONObject jsonLineOfPoem = new JSONObject();
        jsonLineOfPoem.put("id_metrical_lines", id_metrical_lines);
        jsonLineOfPoem.put("ending", ending);
        jsonLineOfPoem.put("meter_group", meter_group);
        jsonLineOfPoem.put("number_of_tonic_feet", number_of_tonic_feet);
        jsonLineOfPoem.put("irregularity_on_syllable", irregularity_on_syllable);
        jsonLineOfPoem.put("length", length);
        jsonLineOfPoem.put("row_key", row_key);
        jsonLineOfPoem.put("line", line);
        jsonLineOfPoem.put("representation", representation);
        jsonLineOfPoem.put("representation_with_spaces", representation_with_spaces);
        jsonLineOfPoem.put("grammaticalAnalysisOfLine", grammaticalAnalysisOfLine.composeJsonObject());
        jsonLineOfPoem.put("rhyme", rhyme.composeJsonObject());
        jsonLineOfPoem.put("strophe", strophe.composeJsonObject());
        return jsonLineOfPoem;
    }
}
