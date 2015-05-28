package br.com.abware.jcondo.account;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;

public class PersonData {

	private Person person;

	private Domain domain;

	private Role<? extends Domain> role;
	
	public PersonData(Person person, Domain domain, Role<? extends Domain> role) {
		this.person = person;
		this.domain = domain;
		this.role = role;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Role<? extends Domain> getRole() {
		return role;
	}

	public void setRole(Role<? extends Domain> role) {
		this.role = role;
	}

}
