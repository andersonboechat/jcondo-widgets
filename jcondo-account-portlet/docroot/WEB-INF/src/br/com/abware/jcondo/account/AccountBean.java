package br.com.abware.jcondo.account;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.RoleType;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
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

	/** cache das tabelas carregadas */
	private List<RoleshipDataModel> models;

	/** apartamento selecionado */
	private Flat flat;
	
	/** apartamentos associados ao usuário logado */
	private List<Flat> flats;

	/** usuario logado */
	private Person person;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			flats = flatService.getFlats(person);

			if (!CollectionUtils.isEmpty(flats)) {
				flat = flats.get(0);
				buildModel();
			} else {
				//setModel(new PersonDataModel(new ArrayList<Person>()));
			}

		} catch (ApplicationException e) {
			LOGGER.fatal("Falha ao inicializar bean", e);
		}
	}
	
	private void buildModel() throws ApplicationException {
//		List<Roleship> roleships = new ArrayList<Roleship>();
//
//		for (Person person : personService.getPeople(flat)) {
//			for (Roleship roleship : person.getRoleships()) {
//				if (flat.equals(roleship.getOrganization())) {
//					roleships.add(roleship);
//				}
//			}
//			
//		}
//
//		model = new RoleshipDataModel(flat, roleships);
//		models.add(model);
	}

	public boolean canAddPeople() {
//		try {
//			return personService.hasPermission(null, Permission.ADD_USER);
//		} catch (ApplicationException e) {
//			LOGGER.error(e.getMessage(), e);
//		}

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

	public void onSelectRole(ValueChangeEvent event) {
//		Role role = (Role) event.getNewValue();
//		Roleship data = model.getRowData();
//		Person person = data.getPerson();
//		try {
//			roleService.save(person, role);
//			data.setRole(role);
//		} catch (ApplicationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void onSelectFlat(AjaxBehaviorEvent event) {
//		try {
//			int i;
//
//			if ((i = models.indexOf(new RoleshipDataModel(flat, null))) < 0) {
//				buildModel();
//			} else {
//				model = models.get(i);
//			}
//
//		} catch (Exception e) {
//			LOGGER.error("Unexpected Failure", e);
//			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
//		}
	}
	
	public void onSave(Person person) {
//		try {
//			Person p = personService.register(person);
//			roleship.setPerson(p);
//
//			if (person.getId() == 0) {
//				model.add(roleship);
//				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
//			} else {
//				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "global.sucess", null);
//			}
//		} catch (ApplicationException e) {
//			LOGGER.error(e.getMessage(), e);
//			//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, e.getLocalizedMessage(), null));
//			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, e.getLocalizedMessage(), null);
//		} catch (Exception e) {
//			LOGGER.error("Unexpected Failure", e);
//			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
//		}
	}

	public void onCreate() {

	}

	public void onEdit(String rowKey) {
		try {

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
			
			File temp = File.createTempFile("usr_pic_", FilenameUtils.getExtension(uploadedFile.getFileName()), new File("/temp/"));
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), temp);

			Person person;
			String clientId = null;

			if ("myAccount".equalsIgnoreCase(clientId)) {
				person = this.person;
			} else {

			}			

		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onCancel(Person person) {
		try {
			if (person.getPicture() != null && person.getPicture().getId() == 0) {
				FileUtils.deleteQuietly(new File(new URL(person.getPicture().getPath()).toURI()));
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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

}
