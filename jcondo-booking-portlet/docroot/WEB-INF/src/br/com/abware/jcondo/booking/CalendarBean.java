package br.com.abware.jcondo.booking;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.validator.ValidatorException;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;

import com.sun.faces.util.MessageFactory;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.booking.service.RoomBookingService;
import br.com.abware.jcondo.booking.service.RoomService;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.ExceptionHandler;

@ManagedBean
@ViewScoped
@Interceptors(value={ExceptionHandler.class})
public class CalendarBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CalendarBean.class);

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private static ScheduleModel model;

	private static List<Room> rooms;

	@EJB(lookup="java:global/jcondo/jcondo-impl/personService")
	private PersonService personService;
	
	@EJB(lookup="java:global/jcondo/jcondo-impl/roomBookingService")
	private RoomBookingService roomBookingService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/roomService")
	private RoomService roomService;
	
	private Person person;
	
	private Date bookingDate;

	private Integer roomId;

	private Room room;

	private String password;	

	private boolean deal;
	
	private StreamedContent agreement;

	private List<RoomBooking> personBookings;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			personBookings = roomBookingService.getBookings(person);
	
			if (model == null) {
				initModel();
			}
	
			if (CollectionUtils.isEmpty(rooms)) {
				rooms = roomService.getRooms();
			}

			if (CollectionUtils.isNotEmpty(rooms)) {
				room = rooms.get(0);	
			}

		} catch (ApplicationException e) {
			LOGGER.fatal("Bean initialization failure", e);
		}
	}

	private void initModel() {
		model = new LazyScheduleModel() {

			private static final long serialVersionUID = 1L;

			@Override
            public void loadEvents(Date start, Date end) {
            	clear();
        		try {
					for (RoomBooking b : roomBookingService.getBookings(start, end)) {
						addEvent(new DefaultScheduleEvent("", 
														  b.getDate(), 
														  b.getDate(), 
														  getRoomStyleClass(b.getResource())));
					}
				} catch (ApplicationException e) {
					e.printStackTrace();
				}
            }   
        };
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

	public String getRoomStyleClass(String roomId) {
		Room room = new Room();
		room.setId(Integer.parseInt(roomId));
		return getRoomStyleClass(room);
	}	

	public void onDateSelect(DateSelectEvent e) {
		bookingDate = e.getDate();
	}
	
	public void onBooking() throws ApplicationException {
		if (deal) {
			RoomBooking booking = roomBookingService.book(person, room, bookingDate);
			personBookings.add(booking);
			model.addEvent(new DefaultScheduleEvent(String.valueOf(booking.getId()), 
													bookingDate, bookingDate, getRoomStyleClass(room)));
			room = null;
			bookingDate = null;
			deal = false;				
		}
	}

	public void onLoad() {
		
	}
	
	public void onCancel() {

	}
	
	public void onCancelBooking(Integer index) throws ApplicationException {
		RoomBooking booking = personBookings.get(index);
		roomBookingService.cancel(booking);
		personBookings.remove(booking);
		model.deleteEvent(model.getEvent(String.valueOf(booking.getId())));
	}
	
	public boolean bookingExists(String roomId) throws ApplicationException {
        Room room = new Room();
        room.setId(Integer.valueOf(roomId));
		return roomBookingService.exists(room, bookingDate);
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			String clientId = component.getClientId(context);
			FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
			throw new ValidatorException(message);
		}  
	}

	public List<Room> getRooms() {
		return rooms;
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

	public StreamedContent getAgreement() {
        try {
	    	FacesContext context = FacesContext.getCurrentInstance();
	
	        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
	        	agreement = new DefaultStreamedContent();
	        } else {
	            String id = context.getExternalContext().getRequestParameterMap().get("id");
	            Room room = new Room();
	            room.setId(Integer.valueOf(id));
	            room = rooms.get(rooms.indexOf(room));
	            String url = "http://" + context.getExternalContext().getRequestServerName() + room.getAgreement();
				agreement = new DefaultStreamedContent(new URL(url).openStream(), "application/pdf");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return agreement;
	}

	public void setAgreement(StreamedContent agreement) {
		this.agreement = agreement;
	}

	public List<RoomBooking> getPersonBookings() {
		return personBookings;
	}

	public void setPersonBookings(List<RoomBooking> bookings) {
		this.personBookings = bookings;
	}

	public ScheduleModel getModel() {
		return model;
	}

	public void setModel(ScheduleModel model) {
		CalendarBean.model = model;
	}
}
