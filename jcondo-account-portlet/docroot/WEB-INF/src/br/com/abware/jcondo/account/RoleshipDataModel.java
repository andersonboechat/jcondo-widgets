package br.com.abware.jcondo.account;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.abware.jcondo.core.Organization;
import br.com.abware.jcondo.core.model.Roleship;

public class RoleshipDataModel extends ListDataModel<Roleship> implements SelectableDataModel<Roleship> {

	private Organization organization;	
	
	public RoleshipDataModel(Organization organization, List<Roleship> roleships) {
		super(roleships);
		this.organization = organization;
	}

	public RoleshipDataModel(List<Roleship> roleships) {
		super(roleships);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Roleship getRowData(String rowKey) {
		List<Roleship> datas = (List<Roleship>) getWrappedData();
		
		for (Roleship data : datas) {
			if (String.valueOf(getRowKey(data)).equals(rowKey)) {
				return data;
			}
		}
		
		return null;
	}

	@Override
	public Object getRowKey(Roleship roleship) {
		return roleship.getPerson().getId();
	}
	
	@SuppressWarnings("unchecked")
	public void add(Roleship data) {
		List<Roleship> datas = (List<Roleship>) getWrappedData();
		datas.add(data);
	}	

}
