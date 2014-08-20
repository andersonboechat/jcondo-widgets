package br.com.abware.jcondo.account;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.RoleType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.core.service.RoleService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class AccountBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(AccountBean.class);

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	@EJB(lookup="java:global/jcondo/jcondo-impl/personService")
	private PersonService personService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/flatService")
	private FlatService flatService;
	
	@EJB(lookup="java:global/jcondo/jcondo-impl/roleService")
	private RoleService roleService;

	private PersonDataModel model;

	private Flat flat;
	
	private List<Flat> flats;

	private Person person;
	
	private Person flatPerson;
	
	private List<Person> flatPeople;
	
	private List<Role> flatRoles;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			flats = flatService.getFlats(person);
			flat = flats.get(0);

			if (!CollectionUtils.isEmpty(flats)) {
				flat = flats.get(0);
				setModel(new PersonDataModel(personService.getPeople(flat)));
				//TODO roles = selectedFlat.getRoles();
			} else {
				setModel(new PersonDataModel(new ArrayList<Person>()));
			}

			flatRoles = roleService.getRoles(RoleType.FLAT_ROLE);
		} catch (ApplicationException e) {
			LOGGER.fatal("Falha ao inicializar bean", e);
		}
	}

	public boolean canAddPeople() {
		try {
			return personService.hasPermission(null, Permission.ADD_USER);
		} catch (ApplicationException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return false;
	}	

	public boolean hasPermission(Person person) {
		try {
			return personService.hasPermission(person, Permission.UPDATE_PERSON);
		} catch (ApplicationException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return false;
	}

	public void onSelectFlat(AjaxBehaviorEvent event) {
		try {
			//event.getSource();
			//flat = flats.get(flats.indexOf(null));
			model.setWrappedData(personService.getPeople(flat));
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	public void onSave(AjaxBehaviorEvent event) {
		try {
			boolean isNew = flatPerson.getId() == 0 ? true : false; 
			flatPerson = personService.save(flatPerson);
			
			if (isNew) {
				model.add(flatPerson);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "global.sucess", null);
			}
		} catch (ApplicationException e) {
			LOGGER.error(e.getMessage(), e);
			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getLocalizedMessage(), null));
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, e.getLocalizedMessage(), null);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onCreate() {
		flatPerson = new Person();
	}

	public void onEdit(String userId) {
		try {
			flatPerson = model.getRowData(userId);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onRemove() {
		
	}

	public void onUpload(FileUploadEvent event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			String portraitPath = "/temp/" + Calendar.getInstance().getTimeInMillis() + "." + FilenameUtils.getExtension(uploadedFile.getFileName());
			File portraitFile = new File(portraitPath);
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), portraitFile);
			
			

			personService.setPortrait(portraitFile);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onCancel() {
		
	}

	public PersonDataModel getModel() {
		return model;
	}

	public void setModel(PersonDataModel model) {
		this.model = model;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Person getFlatPerson() {
		return flatPerson;
	}

	public void setFlatPerson(Person flatPerson) {
		this.flatPerson = flatPerson;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<Person> getFlatPeople() {
		return flatPeople;
	}

	public void setFlatPeople(List<Person> flatPeople) {
		this.flatPeople = flatPeople;
	}

	public List<Role> getFlatRoles() {
		return flatRoles;
	}

	public void setFlatRoles(List<Role> flatRoles) {
		this.flatRoles = flatRoles;
	}
}
