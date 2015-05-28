package br.com.abware.accountmgm;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.abware.jcondo.core.Person;

public class PersonDataModel extends ListDataModel<Person> implements SelectableDataModel<Person> {

	public PersonDataModel(List<Person> data) {
		super(data);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Person getRowData(String rowKey) {
		List<Person> people = (List<Person>) getWrappedData();
		
		for (Person person : people) {
			if (person.getId() == Long.parseLong(rowKey)) {
				return person;
			}
		}
		
		return null;
	}

	@Override
	public Object getRowKey(Person person) {
		return person.getId();
	}
	
	@SuppressWarnings("unchecked")
	public void add(Person person) {
		List<Person> people = (List<Person>) getWrappedData();
		people.add(person);
	}

}