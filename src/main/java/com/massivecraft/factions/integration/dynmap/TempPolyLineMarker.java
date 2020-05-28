package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TempPolyLineMarker {

    /**
     * @author FactionsUUID Team
     */

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    public String world;

    public List<Point> polyLine = new ArrayList<Point>();

    public int lineColor;
    public double lineOpacity;
    public int lineWeight;

    // -------------------------------------------- //
    // CREATE
    // -------------------------------------------- //

    public static boolean equals(PolyLineMarker marker, List<Point> points) {
        int length = marker.getCornerCount();

        if (points.size() != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (marker.getCornerX(i) != points.get(i).x) {
                return false;
            }
            if (marker.getCornerZ(i) != points.get(i).y) {
                return false;
            }
        }

        return true;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    public PolyLineMarker create(MarkerSet markerset, String markerId) {
        double[] polyX = new double[polyLine.size()];
        double[] polyY = new double[polyLine.size()];
        double[] polyZ = new double[polyLine.size()];
        for (int i = 0; i < polyLine.size(); i++) {
            Point p = polyLine.get(i);
            polyX[i] = p.getX();
            polyY[i] = 64;
            polyZ[i] = p.getY();
        }
        PolyLineMarker poly = markerset.createPolyLineMarker(markerId, "", false, this.world, polyX, polyY, polyZ, false);
        // Poly Line Style
        if (poly != null) {
            poly.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }
        return poly;
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public void update(PolyLineMarker marker) {
        // Corner Locations
        if (!equals(marker, polyLine)) {
            double[] polyX = new double[polyLine.size()];
            double[] polyY = new double[polyLine.size()];
            double[] polyZ = new double[polyLine.size()];
            for (int i = 0; i < polyLine.size(); i++) {
                Point p = polyLine.get(i);
                polyX[i] = p.getX();
                polyY[i] = 64;
                polyZ[i] = p.getY();
            }
            marker.setCornerLocations(polyX, polyY, polyZ);
            marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }

        // Line Style
        if (marker.getLineWeight() != this.lineWeight || marker.getLineOpacity() != this.lineOpacity || marker.getLineColor() != this.lineColor) {
            marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }

    }

}
