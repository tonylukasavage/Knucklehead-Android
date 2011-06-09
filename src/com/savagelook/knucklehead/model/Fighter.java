package com.savagelook.knucklehead.model;

import org.json.*;

public class Fighter {
	private String name;
	private String nickname;
	private String link;
	private String height;
	private String weight;
	
	public Fighter() {
		init("", "", "", "", "");	
	}
	
	public Fighter(JSONObject json) {
		try {
			String weight = json.getString("wt");
			int pos = weight.indexOf("(");
			if (pos != -1) {
				weight = weight.substring(0, pos).trim();
			}
			
			String height = json.getString("ht");
			pos = height.indexOf("(");
			if (pos != -1) {
				height = height.substring(0, pos).trim();
			}
			if (!height.equals("") && height.indexOf("\"") == -1) {
				height += "\"";
			}
			
			init(json.getString("name"), json.getString("nick"), json.getString("link"), height, weight);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}
	
	public void init(String name, String nickname, String link, String height, String weight) {
		this.name = name;
		this.nickname = nickname;
		this.link = link;
		this.height = height;
		this.weight = weight;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
}
