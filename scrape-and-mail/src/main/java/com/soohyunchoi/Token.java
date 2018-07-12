package com.soohyunchoi;


public class Token {
	private String prePreToken, preToken, token, postToken, postPostToken;
	
	//this constructor never used
	public Token (String prePreToken, String preToken, String token, String postToken, String postPostToken){
		this.prePreToken = prePreToken;
		this.preToken = preToken;
		this.token = token;
		this.postToken = postToken;
		this.postPostToken = postPostToken;
	}
	public Token(){ }
	public String getPrePreToken () { return prePreToken; }
	public String getPreToken (){
		return preToken;
	}
	public String getToken (){
		return token;
	}
	public String getPostToken (){
		return postToken;
	}
	public String getPostPostToken (){
		return postPostToken;
	}
	public void setPrePreToken(String prePreToken) { this.prePreToken = prePreToken; }
	public void setPreToken(String preToken){
		this.preToken = preToken;
	}
	public void setToken(String token){
		this.token = token;
	}
	public void setPostToken(String postToken){
		this.postToken = postToken;
	}
	public void setPostPostToken(String postPostToken){
		this.postPostToken = postPostToken;
	}
	
}
