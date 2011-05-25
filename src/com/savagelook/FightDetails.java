package com.savagelook;

import org.json.*;

public class FightDetails {
	private String result;
	private String opponent;
	private String method;
	private String event;
	private String date;
	private String round;
	private String time;
	
	public FightDetails() {
		init("", "", "", "", "", "", "");
	}
	
	public FightDetails(String result, String opponent, String method, String event,
			String date, String round, String time) {
		init(result, opponent, method, event, date, round, time);	
	}
	
	public FightDetails(JSONObject json) {
		try {
			init(
				json.getString("result"), 
				json.getString("opponent"), 
				json.getString("method"), 
				json.getString("event"),
				json.getString("date"),
				json.getString("round"),
				json.getString("time")
			);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
	}

	private void init(String result, String opponent, String method, String event,
			String date, String round, String time) {
		this.result = result;
		this.opponent = opponent;
		this.method = method;
		this.event = event;
		this.date = date;
		this.round = round;
		this.time = time;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOpponent() {
		return opponent;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRound() {
		return round;
	}

	public void setRound(String round) {
		this.round = round;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
