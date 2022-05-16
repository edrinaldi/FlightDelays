package it.polito.tdp.extflightdelays.db;

public class TestDAO {

	public static void main(String[] args) {

		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();

		System.out.println(dao.loadAllAirlines().size());
//		System.out.println(dao.loadAllAirports().size());
		System.out.println(dao.loadAllFlights().size());
	}

}
