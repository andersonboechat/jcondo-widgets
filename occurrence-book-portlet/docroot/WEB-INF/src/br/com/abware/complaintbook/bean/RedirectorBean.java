package br.com.abware.complaintbook.bean;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class RedirectorBean {
	
	public void redirect(String windowState) throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect("/ComplaintBook-portlet/views/" + windowState + ".xhtml");
		
	}

}
