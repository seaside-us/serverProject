package plot;
import java.util.ArrayList;

public class SceneSpace {
	private String spname;
	private String modelname;
	private String modelid;
	private String type;
	private int number,istarget,isAction,isDeform,index=0;
	private ArrayList<String> modellist=new ArrayList<String>();
	private ArrayList<String> modelidlist=new ArrayList<String>();
	private ArrayList<Integer> numberlist=new ArrayList<Integer>();
	
	
	public String getSpname() {
		return spname;
	}
	public void setSpname(String spname) {
		this.spname = spname;
	}
	public String getModelname() {
		return modelname;
	}
	public void setModelname(String modelname) {
		this.modelname = modelname;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public void setTarget(int istarget) {
		this.istarget = istarget;
	}
	public int getNumber() {
		return number;
	}
	public int getisTarget() {
		return istarget;
	}
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIsAction() {
		return isAction;
	}
	public void setIsAction(int isAction) {
		this.isAction = isAction;
	}
	public int getIsDeform() {
		return isDeform;
	}
	public void setIsDeform(int isDeform) {
		this.isDeform = isDeform;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void addModellist(String modelName){
		this.modellist.add(modelName);
	}
	public ArrayList<String> getModellist(){
		return modellist;
	}
	public void addModelidlist(String modelid){
		this.modelidlist.add(modelid);
	}
	public ArrayList<String> getModelidlist(){
		return modelidlist;
	}
	public void addNumberlist(int modelNumber){
		this.numberlist.add(modelNumber);
	}
	public ArrayList<Integer> getNumberlist(){
		return numberlist;
	}
}
