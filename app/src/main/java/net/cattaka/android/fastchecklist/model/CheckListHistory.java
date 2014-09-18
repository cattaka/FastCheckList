package net.cattaka.android.fastchecklist.model;

import java.util.Date;

import net.cattaka.util.cathandsgendroid.annotation.DataModel;
import net.cattaka.util.cathandsgendroid.annotation.DataModelAttrs;

@DataModel(
		find={"id:id-","entryId:id-"}
	)
public class CheckListHistory {
	@DataModelAttrs(primaryKey=true)
	private Long id;
	private Long entryId;
	private Date date;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntryId() {
		return entryId;
	}
	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
