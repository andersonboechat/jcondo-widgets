package br.com.abware.jcondo.complaint;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.data.PageEvent;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.crm.model.Answer;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.abware.jcondo.crm.model.OccurrenceType;
import br.com.abware.jcondo.crm.service.OccurrenceService;

@ManagedBean
@SessionScoped
public class BookBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(BookBean.class);

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));	

	@EJB(lookup="java:global/jcondo/jcondo-impl/personService")
	private PersonService personService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/occurrenceService")
	private OccurrenceService occurrenceService;	

	@EJB(lookup="java:global/jcondo/jcondo-impl/flatService")
	private FlatService flatService;	

	private String text;
	
	private Occurrence occurrence;
	
	private List<Occurrence> occurrences;
	
	private Person person;
	
	private List<Flat> flats;

	
	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			occurrences = occurrenceService.getOccurrences(person);
			flats = flatService.getFlats(person);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onNewComplain(ActionEvent event) {
		try {
			occurrence = new Occurrence();
			occurrence.setType(OccurrenceType.COMPLAINT);
			occurrence.setPerson(person);

			if (occurrences.get(0).getId() == 0) {
				occurrences.set(0, occurrence);
			} else {
				occurrences.add(occurrence);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onNewSuggestion(ActionEvent event) {
		try {
			occurrence = new Occurrence();
			occurrence.setType(OccurrenceType.SUGGESTION);
			occurrence.setPerson(person);

			if (occurrences.get(0).getId() == 0) {
				occurrences.set(0, occurrence);
			} else {
				occurrences.add(occurrence);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onSave(ActionEvent event) {
		try {
			occurrence.setText(text);
			occurrence.setDate(new Date());
			occurrence = occurrenceService.register(occurrence);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onCancel() {
		try {
			occurrences.remove(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onLoad() {
		
	}

	public void onPaging(PageEvent event) {
		
	}

	public String getOccurrenceFooter(String occurrenceId) {
		try {
			Occurrence occurrence;
			long id = Long.parseLong(occurrenceId);

			if (id == 0) {
				occurrence = occurrences.get(0);
			} else {
				occurrence = occurrenceService.getOccurrenceById(id);
			}

			if (occurrence != null && occurrence.getPerson() != null) {
				Person person = occurrence.getPerson();
				return person.getFullName() + " - " + person.getFlats().get(0).getName();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
		
	}
	
	public String getAnswerFooter(String occurrenceId) {
		try {
			long id = Long.parseLong(occurrenceId);
			Answer answer = occurrenceService.getOccurrenceById(id).getAnswer();
			
			if (answer != null) {
				Person person = answer.getPerson();
				return person.getFullName() + " - " + person.getFlats().get(0).getName(); 
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
}