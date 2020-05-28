package com.massivecraft.factions.integration.dynmap;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TempLine {
    private Point p1;
    private Point p2;
    private List<TempLine> connectedLines = new ArrayList<TempLine>();

    TempLine(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public void addAdditionLines(List<TempLine> connectedLines) {
        this.connectedLines = connectedLines;
    }

    public List<TempLine> getConnectedLines() {
        return connectedLines;
    }

    @Override
    public boolean equals(Object o) {
        TempLine line = (TempLine) o;
        if (line.p1.x == this.p1.x && line.p2.x == this.p2.x && line.p1.y == this.p1.y && line.p2.y == this.p2.y) {
            return true;
        }
		return line.p1.x == this.p2.x && line.p2.x == this.p1.x && line.p1.y == this.p2.y && line.p2.y == this.p1.y;
	}

    @Override
    public int hashCode() {
        String test = "" + (p1.x + p2.x);
        test += " " + (p1.y + p2.y);
        return test.hashCode();
    }

}
