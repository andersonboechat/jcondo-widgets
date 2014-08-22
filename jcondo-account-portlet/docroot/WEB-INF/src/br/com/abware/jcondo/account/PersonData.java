package br.com.abware.jcondo.account;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;

public class PersonData {

	private Person person;

	private Flat flat;

	private Role role;
	
	public PersonData(Person person, Flat flat, Role role) {
		this.setPerson(person);
		this.setFlat(flat);
		this.setRole(role);
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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
