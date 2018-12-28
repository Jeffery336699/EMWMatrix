package cc.emw.mobile.chat.bean;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.Template;
import cc.emw.mobile.net.ApiEntity.Navigation;

public class Templates {

	private Template template;
	private List<Navigation> navigations;

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}

	public List<Navigation> getNavigations() {
		return navigations;
	}
	public void setNavigations(List<Navigation> navigations) {
		this.navigations = navigations;
	}

}
