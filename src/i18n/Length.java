package i18n;

import java.io.Serializable;

public class Length implements Serializable {

    public static final String MM = "mm";
    public static final String CM = "cm";
    public static final String M = "m";
    public static final String FOOT = "ft";
    public static final String YARD = "yard";
    public static final String MILE = "mile";

    private String unit;
    private double value;

    Length(double value, String unit) {
	this.value = value;
	this.unit = unit;
    }

    public double getValue() {
	return value;
    }

    public String getUnit() {
	return unit;
    }
}
