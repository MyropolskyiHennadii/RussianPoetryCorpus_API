package ruCorpusAPI.model;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "meters_group")
public class MeterGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_meters;
    @Column
    private String foot_model;
    @Column
    private String meter_name;

    public MeterGroup() {
    }

    public MeterGroup(String foot_model, String meter_name) {
        this.foot_model = foot_model;
        this.meter_name = meter_name;
    }

    public int getId_meters() {
        return id_meters;
    }

    public String getFoot_model() {
        return foot_model;
    }

    public String getMeter_name() {
        return meter_name;
    }

    @Override
    public String toString() {
        return "Meter{" +
                "id_meters=" + id_meters +
                ", foot_model='" + foot_model + '\'' +
                ", meter_name='" + meter_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeterGroup)) return false;
        MeterGroup meterGroup = (MeterGroup) o;
        return getId_meters() == meterGroup.getId_meters();
    }

    @Override
    public int hashCode() {
        return getId_meters();
    }

    /**
     * composes json-representation for book-exemplar
     */
    public JSONObject composeJsonObject() {
        JSONObject jsonMetertGroup = new JSONObject();
        jsonMetertGroup.put("id_meters", id_meters);
        jsonMetertGroup.put("foot_model", foot_model);
        jsonMetertGroup.put("meter_name", meter_name);
        return jsonMetertGroup;
    }
}
