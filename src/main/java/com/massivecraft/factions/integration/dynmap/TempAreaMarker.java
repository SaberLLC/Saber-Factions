package com.massivecraft.factions.integration.dynmap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

import com.massivecraft.factions.FactionsPlugin;

public class TempAreaMarker
{

	/**
	 * @author FactionsUUID Team
	 */

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public String label;
	public String world;
	public double[] x;
	public double[] z;

	private List<List<Point>> polyLine = new ArrayList<List<Point>>();

	public String description;

	public int lineColor;
	public double lineOpacity;
	public int lineWeight;

	public int fillColor;
	public double fillOpacity;

	public boolean boost;

	// -------------------------------------------- //
	// CREATE
	// -------------------------------------------- //

	public static boolean equals(AreaMarker marker, double[] x, double[] z)
	{
		int length = marker.getCornerCount();

		if (x.length != length)
		{
			return false;
		}
		if (z.length != length)
		{
			return false;
		}

		for (int i = 0; i < length; i++)
		{
			if (marker.getCornerX(i) != x[i])
			{
				return false;
			}
			if (marker.getCornerZ(i) != z[i])
			{
				return false;
			}
		}

		return true;
	}

	public void setPolyLine(List<List<Point>> points)
	{
		polyLine.clear();
		polyLine.addAll(points);
	}

	// -------------------------------------------- //
	// UPDATE
	// -------------------------------------------- //

	public AreaMarker create(MarkerSet markerset, String markerId)
	{
		AreaMarker ret = markerset.createAreaMarker(markerId, this.label, false, this.world, this.x, this.z, false // not persistent
		);
		if (ret == null)
		{
			return null;
		}

		int counter = 0;
		for (List<Point> polyPoints : polyLine)
		{
			counter++;
			double[] polyX = new double[polyPoints.size()];
			double[] polyY = new double[polyPoints.size()];
			double[] polyZ = new double[polyPoints.size()];
			for (int i = 0; i < polyPoints.size(); i++)
			{
				Point p = polyPoints.get(i);
				polyX[i] = p.getX();
				polyY[i] = 64;
				polyZ[i] = p.getY();
			}
			PolyLineMarker poly = markerset.createPolyLineMarker("poly_" + counter + "_" + markerId, "", false, this.world, polyX, polyY, polyZ, false);
			// Poly Line Style
			if (poly != null)
			{
				poly.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
			}
			else
				FactionsPlugin.getInstance().getLogger().info("null");

		}

		// Description
		ret.setDescription(this.description);

		// Line Style
		ret.setLineStyle(0, 0, 0);

		// Fill Style
		ret.setFillStyle(this.fillOpacity, this.fillColor);

		// Boost Flag
		ret.setBoostFlag(this.boost);

		return ret;
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	public void update(AreaMarker marker)
	{
		// Corner Locations
		if (!equals(marker, this.x, this.z))
		{
			marker.setCornerLocations(this.x, this.z);
		}

		// Label
		if (!marker.getLabel().equals(this.label))
		{
			marker.setLabel(this.label);
		}
		if (!marker.getDescription().equals(this.description))
		{
			marker.setDescription(this.description);
		}

		// // Line Style
		// if (marker.getLineWeight() != this.lineWeight || marker.getLineOpacity() != this.lineOpacity || marker.getLineColor() != this.lineColor)
		// {
		// marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
		// }

		MarkerSet markerset = marker.getMarkerSet();
		int counter = 0;
		String markerId = marker.getMarkerID();
		for (List<Point> polyPoints : polyLine)
		{
			counter++;
			PolyLineMarker exists = markerset.findPolyLineMarker("poly_" + counter + "_" + markerId);
			if (exists != null)
			{
				double[] polyX = new double[polyPoints.size()];
				double[] polyY = new double[polyPoints.size()];
				double[] polyZ = new double[polyPoints.size()];
				for (int i = 0; i < polyPoints.size(); i++)
				{
					Point p = polyPoints.get(i);
					polyX[i] = p.getX();
					polyY[i] = 64;
					polyZ[i] = p.getY();
				}
				exists.setCornerLocations(polyX, polyY, polyZ);

				exists.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
				// exists.deleteMarker();
			}
			else
			{

				double[] polyX = new double[polyPoints.size()];
				double[] polyY = new double[polyPoints.size()];
				double[] polyZ = new double[polyPoints.size()];
				for (int i = 0; i < polyPoints.size(); i++)
				{
					Point p = polyPoints.get(i);
					polyX[i] = p.getX();
					polyY[i] = 64;
					polyZ[i] = p.getY();
				}
				PolyLineMarker poly = markerset.createPolyLineMarker("poly_" + counter + "_" + markerId, "", false, this.world, polyX, polyY, polyZ, false);
				// Poly Line Style
				if (poly != null)
				{
					poly.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
				}
				else
					FactionsPlugin.getInstance().getLogger().info("null");
			}

		}

		// Fill Style
		if ((marker.getFillOpacity() != this.fillOpacity) || (marker.getFillColor() != this.fillColor))
		{
			marker.setFillStyle(this.fillOpacity, this.fillColor);
		}
		// Boost Flag
		if (marker.getBoostFlag() != this.boost)
		{
			marker.setBoostFlag(this.boost);
		}
	}

}
