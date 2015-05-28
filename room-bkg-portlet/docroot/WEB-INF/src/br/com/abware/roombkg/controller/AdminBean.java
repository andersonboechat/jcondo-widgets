package br.com.abware.roombkg.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.liferay.portal.kernel.util.ResourceBundleUtil;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.booking.service.RoomBookingService;
import br.com.abware.jcondo.booking.service.RoomService;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@SessionScoped
public class AdminBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(AdminBean.class);
	
	private static final int ALL_MONTHS = -1;
	
	@EJB(lookup="java:global/jcondo/jcondo-impl/roomBookingService")
	private RoomBookingService roomBookingService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/roomService")
	private RoomService roomService;

	@EJB(lookup="java:global/jcondo/jcondo-impl/flatService")
	private FlatService flatService;

	private List<RoomBooking> bookings;

	private CartesianChartModel barChart;

	private Room room;
	
	private RoomBooking booking;
	
	private Image image;
	
	private List<Flat> flats;
	
	private int year;
	
	private List<Integer> years = new ArrayList<Integer>();
	
	private int month;

	@PostConstruct
	public void init() {
		try {
			Calendar calendar = Calendar.getInstance();
			
			year = calendar.get(Calendar.YEAR);
			month = ALL_MONTHS;
	
			for (int i = 2011; i <= year; i++) {
				years.add(i);
			}
	
				flats = flatService.getFlats();
	
			if (rooms == null) {
				rooms = roomService.getRooms();
			}		
	
			buildChart();		
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void buildChart() throws ApplicationException {
		Calendar calendar = Calendar.getInstance();
		CartesianChartModel model = new CartesianChartModel();
		ChartSeries chartSeries = new ChartSeries();
		List<RoomBooking> bookings = new ArrayList<RoomBooking>();

		for (int i = 0; i < 12; i++) {
			bookings = roomBookingService.getBookings(i, year);

			if (month == i || month == ALL_MONTHS) {
				this.bookings.addAll(bookings);
			}

			calendar.set(year, i, 1);

			chartSeries.set(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()), bookings.size());
		}

		model.addSeries(chartSeries);

		barChart = model;
	}

	public void onSelectMonth(ItemSelectEvent event) {
		try {
			month = event.getItemIndex();
			bookings = roomBookingService.getBookings(month, year);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onSearch() {
		try {		
			month = ALL_MONTHS;
			buildChart();
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}	

	public void onBookingCancel(Integer index) {
		try {
			RoomBooking booking = bookings.get(index.intValue());
			roomBookingService.cancel(booking);
			bookings.remove(booking);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onBookingDischarge(Integer index) {
		try {
			RoomBooking booking = bookings.get(index.intValue());
			roomBookingService.discharge(booking);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String displayBookingsHeader() {
		String monthName;

		if (month == ALL_MONTHS) {
			monthName = rb.getString("all.months"); 
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, month);
			monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()); 
		}

		return ResourceBundleUtil.getString(rb, Locale.getDefault(), "bookings.header", new Object[] {monthName, year}); 
	}

	public CartesianChartModel getBarChart() {
		return barChart;
	}

	public void setBarChart(CartesianChartModel barChart) {
		this.barChart = barChart;
	}

	public List<RoomBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<RoomBooking> bookings) {
		this.bookings = bookings;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public RoomBooking getBooking() {
		return booking;
	}

	public void setBooking(RoomBooking booking) {
		this.booking = booking;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

}
