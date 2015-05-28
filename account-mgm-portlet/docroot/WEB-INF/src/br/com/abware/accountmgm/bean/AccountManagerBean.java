package br.com.abware.accountmgm.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.abware.accountmgm.PersonDataModel;
import br.com.abware.jcondo.BeanLocator;
import br.com.abware.jcondo.core.BaseRole;
import br.com.abware.jcondo.core.Flat;
import br.com.abware.jcondo.core.Person;
import br.com.abware.jcondo.core.RolePermission;

public class AccountManagerBean {
	
	private static Logger LOGGER = Logger.getLogger(AccountManagerBean.class);	

	private PersonDataModel model;
	
	private Flat selectedFlat;
	
	private Person person;
	
	private Person[] selectedPeople;
	
	private List<Flat> flats;
	
	private List<BaseRole> roles;
	
	public AccountManagerBean() {
		person = getPerson().getCurrentUser();
		flats = person.getFlats();
		
		if (!CollectionUtils.isEmpty(flats)) {
			selectedFlat = flats.get(0);
			model = new PersonDataModel(selectedFlat.getPeople());
			roles = selectedFlat.getRoles();
		} else {
			model = new PersonDataModel(new ArrayList<Person>());
		}
	}
	
	private Person getPerson() {
		return (Person) BeanLocator.lookupBean(Person.class.getName());
	}
	
	public boolean hasPermission(Person person) {
		Person currentPerson = getPerson().getCurrentUser();
		return currentPerson.isAllowed(RolePermission.UPDATE_PERSON, person);
	}
	
	public void onNewUser() {
		try {
			person = getPerson();
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onDeleteUsers() {
		try {
			Person currentPerson = getPerson().getCurrentUser();
			List<Person> people = (List<Person>) model.getWrappedData();
			
			for (Person person : people) {
				if (currentPerson.isAllowed(RolePermission.DELETE_PERSON, person)) {
					person.remove();
					people.remove(person);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	public void onEditUser(String userId) {
		try {
			person = model.getRowData(userId);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	public void onSaveUser(AjaxBehaviorEvent event) {
		try {
			boolean isNew = person.getId() == 0 ? true : false; 
			person.persist();
			
			if (isNew) {
				model.add(person);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "global.sucess", null);
			}
			
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	public void onCancelUser() {
		try {
			if (person.getId() == 0) {
				FileUtils.deleteQuietly(person.getPortrait());
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	@SuppressWarnings("unused")
	public void onUpload(FileUploadEvent event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			String portraitPath = "/temp/" + Calendar.getInstance().getTimeInMillis() + "." + FilenameUtils.getExtension(uploadedFile.getFileName());
			File portraitFile = new File(portraitPath);
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), portraitFile);

			person.setPortrait(portraitFile);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onSelectFlat(AjaxBehaviorEvent event) {
		try {
			model.setWrappedData(flats.get(0).getPeople());
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public PersonDataModel getModel() {
		return model;
	}

	public void setModel(PersonDataModel model) {
		this.model = model;
	}

	public Flat getSelectedFlat() {
		return selectedFlat;
	}

	public void setSelectedFlat(Flat selectedFlat) {
		this.selectedFlat = selectedFlat;
	}

	public Person[] getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(Person[] selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<BaseRole> getRoles() {
		return roles;
	}

	public void setRoles(List<BaseRole> roles) {
		this.roles = roles;
	}

}
