package cc.emw.mobile.project.entities;

import java.io.Serializable;
import java.util.List;

import cc.emw.mobile.net.ApiEntity.UserFenPai;

public class UserSprint extends cc.emw.mobile.net.ApiEntity.UserSprint implements Serializable {
//	public int ID;
//	public String Name;
//	public int Creator;
//	public String CreateTime;
//	public int Statu;
//	public String Content;
	private List<UserFenPai> Tasks;
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getCreator() {
		return Creator;
	}
	public void setCreator(int creator) {
		Creator = creator;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public int getStatu() {
		return Statu;
	}
	public void setStatu(int statu) {
		Statu = statu;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public List<UserFenPai> getTasks() {
		return Tasks;
	}
	public void setTasks(List<UserFenPai> tasks) {
		Tasks = tasks;
	}
	
	
}
