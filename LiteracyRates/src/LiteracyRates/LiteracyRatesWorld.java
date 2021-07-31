package LiteracyRates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class LiteracyRatesWorld extends PApplet{
	/**
	 * 
	 */
	
	UnfoldingMap map;
	Map<String, Float> literacyRatesByCountries;
	List<Feature> countries;
	List<Marker> countriesMarkers;
	public void setup() {
		size(2000, 1500, OPENGL);
		map = new UnfoldingMap(this, 400, 0, 1600, 1500, new Microsoft.RoadProvider());
//		MapUtils.createDefaultEventDispatcher(this, map);

		// Load lifeExpectancy data
		literacyRatesByCountries = loadliteracyRatesByCountries("cross-country-literacy-rates.csv");
		println("Loaded " + literacyRatesByCountries.size() + " data entries");
		
		

		// Load country polygons and adds them as markers
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
//		countries = GeoJSONReader.loadData(this, ARGS_BGCOLOR)
		countriesMarkers = MapUtils.createSimpleMarkers(countries);
//		map.zoomAndPanTo(new Location(52.5f, 13.4f), 10);
		map.addMarkers(countriesMarkers);
		shadeCountries();
		
		// Country markers are shaded according to life expectancy (only once)
		
	}
	private void shadeCountries() {
		for (Marker marker : countriesMarkers) {
			// Find data for country of the current marker
			String countryId = marker.getId();
			if (literacyRatesByCountries.containsKey(countryId)) {
				float lifeExp = literacyRatesByCountries.get(countryId);
				// Encode value as brightness (values range: 40-90)
				int colorLevel = (int) map(lifeExp, 30, 90, 10, 255);
				int color = color(255-colorLevel, 100, colorLevel);
				marker.setColor(color);
				
			}
			else {
				marker.setColor(color(150,150,150));
			}
		}
	}

	private Map<String, Float> loadliteracyRatesByCountries(String string) {
		// TODO Auto-generated method stub
		Map<String,Float> literacyRatesMap=new HashMap<String,Float>();
		String[] rows=loadStrings(string);
		for(String row:rows) {
			String[] columns=row.split(",");
			if(columns.length==4 && columns[2].equals("2015")) {
				literacyRatesMap.put(columns[1], Float.parseFloat(columns[3]));
			}
			else if(columns[2].equals("2003") && Float.parseFloat(columns[3])>96) {
				literacyRatesMap.put(columns[1], Float.parseFloat(columns[3]));
			}
			
		}
		return literacyRatesMap;
	}
	public void draw() {
		map.draw();
		addKey();
	}
	private void addKey() {
		// TODO Auto-generated method stub
		fill(255, 255, 255);
		rect(0, 0, 400, 500);
		fill(0, 0, 0);
		textAlign(CENTER, CENTER);
		textSize(30);
		text("Literacy Rates", 200, 25); 
		
		fill(0, 102, 255);
		ellipse(60,100,20,20);
		fill(0);
		text(">95%",200,100);
		
		fill(102, 0, 204);
		ellipse(60,175,20,20);
		fill(0);
		text("60-80%",200,175);
		
		fill(153, 51, 51);
		ellipse(60,250,20,20);
		fill(0);
		text("40-60%",200,250);
		
		fill(255, 102, 0);
		ellipse(60,325,20,20);
		fill(0);
		text("20-40%",200,325);
		
		fill(200, 200, 200);
		ellipse(60,400,20,20);
		fill(0);
		text("No data",200,400);
	}
	

}
