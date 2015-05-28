package br.com.abware.jcondo.booking;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.sun.faces.application.ApplicationAssociate;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.service.RoomService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class BookingBean {
	
	@ManagedProperty(name="", value="")
	private CalendarBean roomCalendar;
	
	@EJB(lookup="java:global/jcondo/jcondo-impl/roomService")
	private RoomService roomService;

	public BookingBean() {
		// TODO Auto-generated constructor stub
	}
	
	@PostConstruct
	private void init() throws ApplicationException {

	}

}
