package br.com.abware.agenda.bean;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import com.sun.faces.util.MessageFactory;

import br.com.abware.jcondo.BeanLocator;
import br.com.abware.jcondo.booking.Room;
import br.com.abware.jcondo.booking.RoomBooking;
import br.com.abware.jcondo.core.Person;

@ManagedBean
@SessionScoped
public class CalendarBean {

	private static final Logger LOGGER = Logger.getLogger(CalendarBean.class);

	private static ScheduleModel model = initModel();

	private static List<Room> rooms = getRooms();

	private Person person;
	
	private Date bookingDate;

	private Integer roomId;

	private Room room;

	private String password;	

	private boolean deal;

	private List<RoomBooking> bookings;

	public CalendarBean() {
		room = rooms.get(0);
		person = getPerson().getCurrentUser();
		bookings = getRoomBooking().getBookingsByLessee(person);
	}

	private static ScheduleModel initModel() {
		ScheduleModel aModel = new DefaultScheduleModel();

		for (RoomBooking booking : getRoomBooking().getBookings()) {
			aModel.addEvent(new DefaultScheduleEvent(booking.getId(), 
													 booking.getDate(), 
													 booking.getDate(), 
													 getRoomStyleClass(booking.getResource())));
		}

		return aModel;
	}

	private static String getRoomStyleClass(Room room) {
		try {
			PropertiesConfiguration pc = new PropertiesConfiguration("application.properties");
			return (String) pc.getProperty("room.style.class." + room.getId());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static List<Room> getRooms() {
		Room room = (Room) BeanLocator.lookupBean(Room.class.getName()); 
		return room.getRooms();
	}

	private static RoomBooking getRoomBooking() {
		RoomBooking booking = (RoomBooking) BeanLocator.lookupBean(RoomBooking.class.getName());
		return booking;
	}
	
	private Person getPerson() {
		return (Person) BeanLocator.lookupBean(Person.class.getName());
	}

	public void onBooking() {
		try {
			if (deal) {
				RoomBooking booking = getRoomBooking();

				booking.book(person, room, bookingDate);

				model.addEvent(new DefaultScheduleEvent(booking.getId(), bookingDate, bookingDate, getRoomStyleClass(room)));

				room = null;
				bookingDate = null;
				deal = false;				
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onDateSelect(DateSelectEvent e) {
		bookingDate = e.getDate();
	}
	
	public void onLoad() {
		
	}
	
	public void onCancel() {
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onCancelBooking(Integer index) {
		try {
			RoomBooking booking = bookings.get(index);
			booking.cancel();
			model.deleteEvent(model.getEvent(booking.getId()));
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}
	
	public boolean bookingExists(String roomId) {
		try {
			return getRoomBooking().exists(room, bookingDate);
		} catch (Exception e) {
			LOGGER.error("Unexpected Failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}

		return false;
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			String clientId = component.getClientId(context);
			FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
			throw new ValidatorException(message);  
		}  
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDeal() {
		return deal;
	}

	public void setDeal(boolean deal) {
		this.deal = deal;
	}

	public List<RoomBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<RoomBooking> bookings) {
		this.bookings = bookings;
	}
}
