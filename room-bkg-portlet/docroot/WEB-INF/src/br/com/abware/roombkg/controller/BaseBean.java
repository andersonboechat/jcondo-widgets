package br.com.abware.roombkg.controller;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.booking.model.Room;

@ManagedBean
@ApplicationScoped
public abstract class BaseBean {

	protected static final Logger LOGGER = Logger.getLogger(BaseBean.class);

	protected static List<Room> rooms;

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	protected static String getRoomStyleClass(Room room) {
		return rb.getString("room.style.class." + room.getId());
	}

	public String getRoomStyleClass(String roomId) {
		Room room = new Room();
		room.setId(Integer.parseInt(roomId));
		return getRoomStyleClass(room);
	}	
	
	public static List<Room> getRooms() {
		return rooms;
	}
	
}
