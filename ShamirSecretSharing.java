import com.google.gson.*;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecretSharing {

    static class Share {
        int x;
        BigInteger y;

        Share(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    // 256-bit prime (close to 2^256)
    static final BigInteger PRIME = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");

    public static void main(String[] args) {
        try {
            Reader reader = new FileReader("input.json");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject keys = jsonObject.getAsJsonObject("keys");
            int n = keys.get("n").getAsInt();
            int k = keys.get("k").getAsInt();

            List<Share> shares = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (entry.getKey().equals("keys")) continue;

                int x = Integer.parseInt(entry.getKey());
                JsonObject obj = entry.getValue().getAsJsonObject();
                int base = Integer.parseInt(obj.get("base").getAsString());
                String valueStr = obj.get("value").getAsString();

                try {
                    BigInteger y = new BigInteger(valueStr, base);
                    shares.add(new Share(x, y));
                } catch (Exception e) {
                    System.out.println("Invalid value at x = " + x + ". Skipping.");
                }
            }

            if (shares.size() < k) {
                System.out.println("Unable to recover secret. Too many invalid shares.");
                return;
            }

            // Pick first k shares (can be randomized/shuffled for robustness)
            List<Share> selectedShares = shares.subList(0, k);
            BigInteger secret = lagrangeInterpolation(selectedShares, PRIME);
            System.out.println("Recovered secret: " + secret);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lagrange Interpolation to find secret (i.e., constant term of polynomial)
    static BigInteger lagrangeInterpolation(List<Share> shares, BigInteger prime) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < shares.size(); i++) {
            int xi = shares.get(i).x;
            BigInteger yi = shares.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < shares.size(); j++) {
                if (i != j) {
                    int xj = shares.get(j).x;
                    numerator = numerator.multiply(BigInteger.valueOf(-xj)).mod(prime);
                    denominator = denominator.multiply(BigInteger.valueOf(xi - xj)).mod(prime);
                }
            }

            BigInteger term = yi.multiply(numerator).multiply(modInverse(denominator, prime)).mod(prime);
            result = result.add(term).mod(prime);
        }

        return result;
    }

    static BigInteger modInverse(BigInteger a, BigInteger mod) {
        return a.modInverse(mod);
    }
}
