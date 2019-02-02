package studia.projekt.client.model;

import java.util.Date;

public class DateModel {

	private Date date;
	private final Integer entry_id;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getEntry_id() {
		return entry_id;
	}

	public DateModel(Date date, Integer entry_id) {
		super();
		this.date = date;
		this.entry_id = entry_id;
	}
	
	@Override
	public String toString() {
		return this.date.toString();
	}
}
