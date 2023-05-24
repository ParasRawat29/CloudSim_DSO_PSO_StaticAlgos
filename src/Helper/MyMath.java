package Helper;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class MyMath{
	public static BigDecimal power(BigDecimal  x, BigDecimal y)
    {
		BigDecimal temp;
        if(y.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;
        temp = power(x,y.divide(BigDecimal.valueOf(2)));
 
        if (BigDecimal.ZERO.compareTo(y.remainder(BigDecimal.valueOf(2))) ==0)
            return temp.multiply(temp);
        else {
            if (y.compareTo(BigDecimal.ZERO) > 0)
                return temp.multiply(temp).multiply(x);
            else
                return temp.multiply(temp).divide(x);
        }
    }
	
	
	 public static double mapRange(double outputStart , double outputEnd , double inputStart , double inputEnd, double input) {
			inputEnd +=1;
			double output = outputStart + ((outputEnd - outputStart) / (inputEnd - inputStart)) * (input-inputStart);
			return output;
		}
	
	
}