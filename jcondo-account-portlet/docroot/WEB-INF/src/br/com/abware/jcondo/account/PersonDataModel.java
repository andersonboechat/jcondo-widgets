package br.com.abware.jcondo.account;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.abware.jcondo.core.Organization;

public class PersonDataModel extends ListDataModel<PersonData> implements SelectableDataModel<PersonData> {

	private Organization organization;
	
	public PersonDataModel(Organization organization, List<PersonData> data) {
		super(data);
		this.organization = organization;
	}
	
	public PersonDataModel(List<PersonData> data) {
		super(data);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PersonData getRowData(String rowKey) {
		List<PersonData> datas = (List<PersonData>) getWrappedData();
		
		for (PersonData data : datas) {
			if (String.valueOf(getRowKey(data)).equals(rowKey)) {
				return data;
			}
		}
		
		return null;
	}

	@Override
	public Object getRowKey(PersonData data) {
		return data.getPerson().getId();
	}
	
	@SuppressWarnings("unchecked")
	public void add(PersonData data) {
		List<PersonData> datas = (List<PersonData>) getWrappedData();
		datas.add(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		try {
			PersonDataModel model = (PersonDataModel) obj;
			if (this.organization.equals(model.getOrganization())) {
				return true;
			}
		} catch (Exception e) {}

		return false;
	}

	public Organization getOrganization() {
		return organization;
	}
}
