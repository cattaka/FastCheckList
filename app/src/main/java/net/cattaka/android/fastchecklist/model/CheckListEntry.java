package net.cattaka.android.fastchecklist.model;

import net.cattaka.util.cathandsgendroid.annotation.DataModel;
import net.cattaka.util.cathandsgendroid.annotation.DataModelAttrs;

import java.util.List;

@DataModel(
		find={"id",":sort+"},
		unique={"title"},
        query={"MaxSortNo:select max(sort) as sort from checkListEntry"}
	)
public class CheckListEntry {
	@DataModelAttrs(primaryKey=true)
	private Long id;
	private String title;
	private Long sort;
	private Long starFlag;
	@DataModelAttrs(ignore=true)
	private List<CheckListItem> items;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<CheckListItem> getItems() {
		return items;
	}
	public void setItems(List<CheckListItem> items) {
		this.items = items;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Long getStarFlag() {
		return starFlag;
	}
	public void setStarFlag(Long starFlag) {
		this.starFlag = starFlag;
	}
}
