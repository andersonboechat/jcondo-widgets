package br.com.abware.jcondo.booking;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
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
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.validator.ValidatorException;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;

import com.sun.faces.util.MessageFactory;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.booking.service.RoomBookingService;
import br.com.abware.jcondo.booking.service.RoomService;
import br.com.abware.jcondo.core.model.Flat;
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

	private static List<ScheduleModel> models;

	private static List<Room> rooms;

	@EJB(lookup="java:global/jcondo/jcondo-impl/personService")
	private PersonService personService;
	
	@EJB(lookup="java:global/jcondo/jcondo-impl/roomBookingService")
	private RoomBookingService roomBookingService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/roomService")
	private RoomService roomService;
	
	private Person person;
	
	private Flat flat;

	private RoomBooking booking;
	
	private Date bookingDate;

	private Integer roomId;

	private Room room;

	private String password;	

	private boolean deal;

	private List<RoomBooking> personBookings;
	
	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			personBookings = roomBookingService.getBookings(person);
	
			if (CollectionUtils.isEmpty(rooms)) {
				rooms = roomService.getRooms();
			}

			if (CollectionUtils.isNotEmpty(rooms)) {
				room = rooms.get(0);	
			}
			
			models = new ArrayList<ScheduleModel>();
			for (Room r : rooms) {
				models.add(new CalendarModel(r));
			}

		} catch (ApplicationException e) {
			LOGGER.fatal("Bean initialization failure", e);
		}
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
	
	public void onBookingSelect(SelectEvent event) {
		ScheduleEvent e = (ScheduleEvent) event.getObject();
		booking = (RoomBooking) e.getData();
		bookingDate = booking.getDateIn();
	}
	
	public void onBooking(int modelIndex) throws ApplicationException {
		if (deal) {
			RoomBooking booking =  new RoomBooking(person, room, bookingDate, bookingDate);
			booking = roomBookingService.book(booking);
			personBookings.add(booking);

			ScheduleModel model = models.get(modelIndex);
			model.addEvent(new DefaultScheduleEvent(String.valueOf(booking.getId()), 
													bookingDate, bookingDate, getRoomStyleClass(room)));
			bookingDate = null;
			deal = false;				
		}
	}
	
	public void onCancel() {
		try {
			roomBookingService.cancel(booking);
			
			//setMessages(FacesMessage.SEVERITY_WARN, getClientId(":event-dialog-form:cancelBookingBtn"), "register.cancel.success");
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//setMessages(FacesMessage.SEVERITY_WARN, getClientId(":event-dialog-form:cancelBookingBtn"), "register.cancel.success");
		}
	}
	
	public void onBookingCancel(Integer index) throws ApplicationException {
		RoomBooking booking = personBookings.get(index);
		roomBookingService.cancel(booking);
		personBookings.remove(booking);
//		model.deleteEvent(model.getEvent(String.valueOf(booking.getId())));
	}

	public void onFlatSelect(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) event.getSource();
		Integer id = Integer.valueOf((String) selectOneMenu.getValue());
//		Flat f = Flat.getFlat(id);
//
//		if (id != -1) {
//			flat = flats.get(flats.indexOf(f));	
//		} else {
//			flat = f;
//		}
	}	

	public void onResidentSelect(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) event.getSource();
		Integer id = Integer.valueOf((String) selectOneMenu.getValue());

//		if (id != -1) {
//			List<User> users = flat.getUsers();
//			resident = users.get(users.indexOf(UserLocalServiceUtil.createUser(id)));
//		} else {
//			resident = null;
//		}
	}	
	
	public void onTabChange(TabChangeEvent event) {
		TabView tv = (TabView) event.getSource();
		int i = tv.getChildren().indexOf(event.getTab());
		room = ((CalendarModel) models.get(i)).getRoom();
		roomId = room.getId();
	}

    public StreamedContent showAgreement(int roomIndex) {
        try {
	    	FacesContext context = FacesContext.getCurrentInstance();
	
	        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
	            return new DefaultStreamedContent();
	        } else {
	            room = rooms.get(roomIndex);
	            String agreement = "http://" + context.getExternalContext().getRequestServerName() + room.getAgreement();
				return new DefaultStreamedContent(new URL(agreement).openStream(), "application/pdf");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return null;
    }	

	public boolean bookingExists(String roomId) throws ApplicationException {
        Room room = new Room();
        room.setId(Integer.valueOf(roomId));

        RoomBooking booking = new RoomBooking(person, room, bookingDate, bookingDate);
		return roomBookingService.exists(booking);
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			String clientId = component.getClientId(context);
			FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
			throw new ValidatorException(message);
		}  
	}

	public void validatePassword(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof String) {
			String pwd = (String) value;
//			if (!UserHelper.isUserPasswordMatch(UserHelper.getLoggedUser(), pwd)) {
//				String clientId = component.getClientId(context);
//				FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
//				throw new ValidatorException(message);  
//			}
		}
	}

	public boolean isCancelEnable() {
		if (bookingDate != null) {
			Date today = new Date();
			Date deadline = DateUtils.addDays(bookingDate, -7);
			return deadline.after(today);
		}

		return false;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public RoomBooking getBooking() {
		return booking;
	}

	public void setBooking(RoomBooking booking) {
		this.booking = booking;
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

	public List<RoomBooking> getPersonBookings() {
		return personBookings;
	}

	public void setPersonBookings(List<RoomBooking> bookings) {
		this.personBookings = bookings;
	}

	public ScheduleModel getModel(int index) {
		return models.get(index);
	}

	public List<ScheduleModel> getModels() {
		return models;
	}

	public void setModel(List<ScheduleModel> models) {
		CalendarBean.models = models;
	}
	
	public class CalendarModel extends LazyScheduleModel {

		private static final long serialVersionUID = 1L;
		
		private Room room;
		
		public CalendarModel(Room room) {
			super();
			this.room = room;
		}
		
		@Override
	    public void loadEvents(Date start, Date end) {
	    	clear();
			try {
				for (RoomBooking b : roomBookingService.getBookings(room, start, end)) {
    				DefaultScheduleEvent event; 
    				event = new DefaultScheduleEvent(b.getFlat().getName(), 
													 b.getDateIn(), 
													 b.getDateOut(), 
													 CalendarBean.getRoomStyleClass(b.getResource()));
    				event.setData(b);
					addEvent(event);
				}
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
	    }

		public Room getRoom() {
			return room;
		}

	}	
}
