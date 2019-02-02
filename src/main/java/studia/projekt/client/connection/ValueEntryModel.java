package studia.projekt.client.connection;

import java.util.ArrayList;
import java.util.List;

import studia.projekt.client.model.MeasurementEntry;

public class ValueEntryModel {

	private final String parameter;
	private String value;
	private final String unit;
	private final String reference;

	public ValueEntryModel(String parameter, String value, String unit, String reference) {
		super();
		this.parameter = parameter;
		this.value = value;
		this.unit = unit;
		this.reference = reference;
	}

	public ValueEntryModel(String parameter, String unit, String reference) {
		super();
		this.parameter = parameter;
		this.value = "---";
		this.unit = unit;
		this.reference = reference;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParameter() {
		return parameter;
	}

	public String getUnit() {
		return unit;
	}

	public String getReference() {
		return reference;
	}

	public void fill(MeasurementEntry e) {

	}

	public static List<ValueEntryModel> generateConstantsMale(MeasurementEntry entry) {
		List<ValueEntryModel> entries = new ArrayList<>();

		ValueEntryModel a = new ValueEntryModel("Leukocyty", entry.getLeukocyte().toString(), "tys/ul", "");
		ValueEntryModel b = new ValueEntryModel("Erytrocyty", entry.getErythrocyte().toString(), "mln/ul", "");
		ValueEntryModel c = new ValueEntryModel("Hemoglobina", entry.getHemoglobin().toString(), "g/dl", "");
		ValueEntryModel d = new ValueEntryModel("Hematokryt", entry.getHematocrit().toString(), "%", "");
		ValueEntryModel e = new ValueEntryModel("MCV", entry.getMcv().toString(), "fl", "");
		ValueEntryModel f = new ValueEntryModel("MCH", entry.getMch().toString(), "pg", "");
		ValueEntryModel g = new ValueEntryModel("MCHC", entry.getMchc().toString(), "g/dl", "");
		ValueEntryModel h = new ValueEntryModel("Płytki krwi", entry.getPlatelets().toString(), "tys/ul", "");
		ValueEntryModel i = new ValueEntryModel("Limfocyty", entry.getLymphocyte().toString(), "tys/ul", "");

		entries.add(a);
		entries.add(b);
		entries.add(c);
		entries.add(d);
		entries.add(e);
		entries.add(f);
		entries.add(g);
		entries.add(h);
		entries.add(i);
		return entries;
	}

	public static List<ValueEntryModel> generateConstantsFemale() {
		List<ValueEntryModel> entries = new ArrayList<>();
		ValueEntryModel a = new ValueEntryModel("Leukocyty", "tys/ul", "");
		ValueEntryModel b = new ValueEntryModel("Erytrocyty", "mln/ul", "");
		ValueEntryModel c = new ValueEntryModel("Hemoglobina", "g/dl", "");
		ValueEntryModel d = new ValueEntryModel("Hematokryt", "%", "");
		ValueEntryModel e = new ValueEntryModel("MCV", "fl", "");
		ValueEntryModel f = new ValueEntryModel("MCH", "pg", "");
		ValueEntryModel g = new ValueEntryModel("MCHC", "g/dl", "");
		ValueEntryModel h = new ValueEntryModel("Płytki krwi", "tys/ul", "");
		ValueEntryModel i = new ValueEntryModel("Limfocyty", "tys/ul", "");

		entries.add(a);
		entries.add(b);
		entries.add(c);
		entries.add(d);
		entries.add(e);
		entries.add(f);
		entries.add(g);
		entries.add(h);
		entries.add(i);

		return entries;
	}

}
