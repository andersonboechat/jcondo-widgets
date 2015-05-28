package br.com.abware.complaintbook.bean;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.data.PageEvent;

import br.com.abware.jcondo.BeanLocator;
import br.com.abware.jcondo.core.Person;
import br.com.abware.jcondo.crm.Answer;
import br.com.abware.jcondo.crm.Occurrence;
import br.com.abware.jcondo.crm.OccurrenceType;

@ManagedBean
@SessionScoped
public class BookManager {

	private String text;
	
	private Occurrence occurrence;
	
	private List<Occurrence> occurrences;
	
	private Person person;
	
	public BookManager() {
		try {
			person = getPerson().getCurrentUser();
			occurrence = getOccurrence();
			occurrences = occurrence.getOccurrencesByUserId(person.getId());

			if (CollectionUtils.isEmpty(occurrences)) {
				occurrences.add(occurrence);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private Person getPerson() {
		return (Person) BeanLocator.lookupBean(Person.class.getName());
	}
	
	private Occurrence getOccurrence() {
		return (Occurrence) BeanLocator.lookupBean(Person.class.getName());
	}

	public void onNewComplain(ActionEvent event) {
		try {
			occurrence = getOccurrence();
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
			occurrence = getOccurrence();
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
			occurrence.register();
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
				occurrence = this.occurrence.getOccurrenceById(id);
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
			Answer answer = this.occurrence.getOccurrenceById(id).getAnswer();
			
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
