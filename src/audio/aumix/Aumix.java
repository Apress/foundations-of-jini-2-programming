
/**
 * Aumix.java
 *
 *
 * Created: Fri Aug 22 10:48:59 2003
 *
 * @author <a href="mailto: jan@newmarch.name">jan newmarch</a>
 * @version
 */

package audio.aumix;

import java.io.*;

public class Aumix {

    private static int volume;

    public static void setOutputVolume(int vol) {
	try {
	    Runtime.getRuntime().exec("aumix -w " + volume + "," + volume);
	    // don't change volume unless aumix succeeds
	    volume = vol;
	} catch(java.io.IOException e) {
	    // ignore
	}
    }

    public static void setInputVolume(int vol) {
	try {
	    Runtime.getRuntime().exec("aumix -i " + volume + "," + volume);
	    // don't change volume unless aumix succeeds
	    volume = vol;
	} catch(java.io.IOException e) {
	    // ignore
	}
    }

    public static int getVolume() {
	return volume;
    }

    public static int getMaxVolume() {
	return (int) 100;
    }

    private static int getVolumeFromDevice() {
        Process aumixProc = null;
        try {
            aumixProc = Runtime.getRuntime().exec("aumix -iq");
        } catch(IOException e) {
            System.err.println(e);
            return 0;
        }

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(
                               aumixProc.getInputStream()));
        String response = null;
        try {
            response = reader.readLine();
        } catch(IOException e) {
            System.err.println(e);
            return 0;
        }

        // format is undocumented, appears to be in aumix 2.7
        // "igain" <left>, <right>, P
        // used to be??
        // "vol" <left> "," <right>

        // System.out.println("volume is " + response);
        // assume "vol "
        response = response.substring(5);
        String vols[] = response.split(",");
        int left = 0, right = 0;
        try {
            right = Integer.parseInt(vols[0].trim());
            left = Integer.parseInt(vols[1].trim());
        } catch(NumberFormatException e) {
            System.err.println("Number error in '" + vols[0] + "' or '" +
                               vols[1] + "'");
	    return 0;
        }
        return (right + left)/2;
   }



}// Aumix
