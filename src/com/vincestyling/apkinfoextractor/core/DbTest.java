package com.vincestyling.apkinfoextractor.core;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.vincestyling.apkinfoextractor.utils.Utils;

import java.util.List;

public class DbTest {

	public static void main(String[] args) throws Exception {
		ObjectContainer db = Utils.getDatabase();
		try {
			storeFirstPilot(db);
			storeSecondPilot(db);
			retrieveAllPilots(db);
//			retrievePilotByName(db);
//			retrievePilotByExactPoints(db);
//			updatePilot(db);
//			deleteFirstPilotByName(db);
//			deleteSecondPilotByName(db);
		} finally {
			db.close();
		}
	}

	public static void storeFirstPilot(ObjectContainer db) {
		Pilot pilot = new Pilot("Michael Schumacher", 100);
		db.store(pilot);
		System.out.println("Stored : " + pilot + " id : " + db.ext().getObjectInfo(pilot).getInternalID());
	}

	public static void storeSecondPilot(ObjectContainer db) {
		Pilot pilot = new Pilot("Rubens Barrichello", 99);
		db.store(pilot);
		System.out.println("Stored : " + pilot + " id : " + db.ext().getObjectInfo(pilot).getInternalID());
	}

	public static void retrieveAllPilotQBE(ObjectContainer db) {
		Pilot proto = new Pilot(null, 0);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrieveAllPilots(ObjectContainer db) {
		ObjectSet result = db.queryByExample(Pilot.class);
		listResult(result);
	}

	public static void retrievePilotByName(ObjectContainer db) {
		Pilot proto = new Pilot("Michael Schumacher", 0);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void retrievePilotByExactPoints(ObjectContainer db) {
		Pilot proto = new Pilot(null, 100);
		ObjectSet result = db.queryByExample(proto);
		listResult(result);
	}

	public static void listResult(List<?> result) {
		System.out.println(result.size());
		for (Object o : result) {
			System.out.println(o);
		}
	}

	public static void updatePilot(ObjectContainer db) {
		ObjectSet result = db.queryByExample(new Pilot("Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		found.addPoints(11);
		db.store(found);
		System.out.println("Added 11 points for " + found);
		retrieveAllPilots(db);
	}

	public static void deleteFirstPilotByName(ObjectContainer db) {
		ObjectSet result = db.queryByExample(new Pilot("Michael Schumacher", 0));
		Pilot found = (Pilot) result.next();
		db.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(db);
	}

	public static void deleteSecondPilotByName(ObjectContainer db) {
		ObjectSet result = db.queryByExample(new Pilot("Rubens Barrichello", 0));
		Pilot found = (Pilot) result.next();
		db.delete(found);
		System.out.println("Deleted " + found);
		retrieveAllPilots(db);
	}

	public static class Pilot {
		private String name;
		private int points;

		public Pilot() {
		}

		public Pilot(String name, int hours) {
			this.name = name;
			this.points = hours;
		}

		public void addPoints(int points) {
			this.points += points;
		}

		@Override
		public String toString() {
			return "Pilot{" +
					"name='" + name + '\'' +
					", points=" + points +
					'}';
		}
	}

}
