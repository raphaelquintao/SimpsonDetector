package br.raphael.detector;

public class ObjectList {
	private String titulo;
	private String msg;
	private int icon;
	private String Adress;
	
	public ObjectList(int icon, String title, String msg, String Adress){
		this.titulo = title;
		this.msg = msg;
		this.icon = icon;
		this.Adress = Adress;
		
	}
	public int getIcon(){
		return icon;
	}
	public String getTitle(){
		return titulo;
	}
	public String getMsg(){
		return msg;
	}
	public String getAdress(){
		return Adress;
	}
	public void setTitle(String title){
		this.titulo = title;
	}
	public void setMsg(String msg){
		this.msg = msg;
	}
	
}
