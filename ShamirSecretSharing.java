import com.google.gson.*;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
          
            JsonObject json = JsonParser.parseReader(new FileReader("input.json")).getAsJsonObject();
            JsonObject keys = json.getAsJsonObject("keys");
            int n = keys.get("n").getAsInt();
            int k = keys.get("k").getAsInt(); 

            List<BigInteger> xList = new ArrayList<>();
            List<BigInteger> yList = new ArrayList<>();

           
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                if (key.equals("keys")) continue;

                int x = Integer.parseInt(key);
                JsonObject root = entry.getValue().getAsJsonObject();
                int base = Integer.parseInt(root.get("base").getAsString());
                String value = root.get("value").getAsString();
                BigInteger y = new BigInteger(value, base);

                xList.add(BigInteger.valueOf(x));
                yList.add(y);
                if (xList.size() == k) break; 
            }

          
            BigInteger[][] A = new BigInteger[k][k];
            BigInteger[] Y = new BigInteger[k];

            for (int i = 0; i < k; i++) {
                BigInteger xi = xList.get(i);
                BigInteger power = BigInteger.ONE;
                for (int j = k - 1; j >= 0; j--) {
                    A[i][j] = power;
                    power = power.multiply(xi);
                }
                Y[i] = yList.get(i);
            }

            
            BigInteger[] coeffs = gaussianElimination(A, Y);

         
            System.out.println("Recovered Secret: " + coeffs[k - 1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    static BigInteger[] gaussianElimination(BigInteger[][] A, BigInteger[] Y) {
        int n = A.length;

        for (int i = 0; i < n; i++) {
            
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (A[k][i].abs().compareTo(A[maxRow][i].abs()) > 0) {
                    maxRow = k;
                }
            }

         
            BigInteger[] tempRow = A[i];
            A[i] = A[maxRow];
            A[maxRow] = tempRow;

            BigInteger tempY = Y[i];
            Y[i] = Y[maxRow];
            Y[maxRow] = tempY;

           
            for (int k = i + 1; k < n; k++) {
                BigInteger factor = A[k][i].divide(A[i][i]);
                for (int j = i; j < n; j++) {
                    A[k][j] = A[k][j].subtract(factor.multiply(A[i][j]));
                }
                Y[k] = Y[k].subtract(factor.multiply(Y[i]));
            }
        }

      
        BigInteger[] result = new BigInteger[n];
        for (int i = n - 1; i >= 0; i--) {
            BigInteger sum = Y[i];
            for (int j = i + 1; j < n; j++) {
                sum = sum.subtract(A[i][j].multiply(result[j]));
            }
            result[i] = sum.divide(A[i][i]);
        }

        return result;
    }
}
