package br.com.abware.jcondo.booking;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class RedirectorBean {

	private String windowState;
	
	public void redirect(String windowState) throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect("/room-bkg-portlet/views/" + windowState + ".xhtml");
		
	}

	public String getWindowState() {
		return windowState;
	}

	public void setWindowState(String windowState) {
		this.windowState = windowState;
	}
}
