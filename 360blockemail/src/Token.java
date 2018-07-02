public class Token {
	private String preToken, token, postToken, postPostToken;
	public Token(String preToken, String token, String postToken, String postPostToken){
		this.preToken = preToken;
		this.token = token;
		this.postToken = postToken;
		this.postPostToken = postPostToken;
	}
	public Token(){
	
	}
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
