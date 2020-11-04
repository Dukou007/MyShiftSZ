package timezone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeZoneTest {

	public static void main(String[] args) {  
        TimeZone tz = TimeZone.getDefault();  
        System.out.println("tz: " + tz);  
           
        int offset = tz.getRawOffset();  
        System.out.println("raw offset: " + offset);  
           
        int dstSavings = tz.getDSTSavings();  
        System.out.println("dstSavings: " + dstSavings);  
           
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        while(true) {  
            Calendar cal = Calendar.getInstance();  
            String msg = "[" + sdf.format(cal.getTime()) + "] " + cal.getTime();  
            msg += ", offset: " + TimeZone.getDefault().getOffset(cal.getTimeInMillis());  
            System.out.println(msg);  
               
            try {  
                Thread.sleep(60 * 1000);  
            } catch (InterruptedException ex) {  
                ex.printStackTrace();  
            }  
        }  
    }  

}
