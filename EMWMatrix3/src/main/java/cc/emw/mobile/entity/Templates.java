package cc.emw.mobile.entity;

import java.util.List;

import cc.emw.mobile.net.ApiEntity.Navigation;
import cc.emw.mobile.net.ApiEntity.Template;

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
