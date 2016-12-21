package com.wptdxii.playground.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MakeInsuranceTemplateInnerBean implements Serializable {
	private String input_type;
	private String title;
	private String title_key;
	private ArrayList<String> valueCode;
	private ArrayList<String> valueString;
	private String edit_able;
	private String hint;

	public String getInput_type() {
		return input_type;
	}
	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle_key() {
		return title_key;
	}
	public void setTitle_key(String title_key) {
		this.title_key = title_key;
	}
	public ArrayList<String> getValueCode() {
		return valueCode;
	}
	public void setValueCode(ArrayList<String> valueCode) {
		this.valueCode = valueCode;
	}
	public ArrayList<String> getValueString() {
		return valueString;
	}
	public void setValueString(ArrayList<String> valueString) {
		this.valueString = valueString;
	}

	public String getEdit_able() {
		return edit_able;
	}

	public void setEdit_able(String edit_able) {
		this.edit_able = edit_able;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
}
