package net.cattaka.android.fastchecklist.model;

import net.cattaka.util.cathandsgendroid.annotation.DataModel;
import net.cattaka.util.cathandsgendroid.annotation.DataModelAttrs;

@DataModel(
		find={"id","entryId:sort+"},
        query={"MaxSort:select max(sort) as sort from checkListItem where entryId=?"}
	)
public class CheckListItem {
	@DataModelAttrs(primaryKey=true)
	private Long id;
	private Long entryId;
	private Long sort;
	private String label;

    public CheckListItem() {
    }

    public CheckListItem(Long id, Long entryId, Long sort, String label) {
        this.id = id;
        this.entryId = entryId;
        this.sort = sort;
        this.label = label;
    }

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
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
}
