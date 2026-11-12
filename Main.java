import java.math.BigInteger;
import java.util.Scanner;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {
        
    private static final Map<Integer, String> ALPHABET = Map.of(
        16, "0123456789ABCDEF",
        32, "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567",
        36, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ",
        58, "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz",
        62, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
        // using built-in class for base 64, but including for completeness
        // 64, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
        85, "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstu",
        92, "~!#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_abcdefghijklmnopqrstuvwxyz{|}"
    );

    public static void main(String[] args) {
        System.out.println("Enter target string:");
        String input;
        try (Scanner console = new Scanner(System.in)) {
            input = console.nextLine();
        }

        // base 16
        boolean validHex = input.matches("[0-9a-fA-F]+");
        if (validHex) {
            BigInteger output = new BigInteger(input, 16);
            System.out.println("Base-16: " +output);
        }

        // base 32
        boolean valid32 = input.matches("[0-9a-zA-Z]+");
        if (valid32) {
            byte[] decoded = decode(input, 32);
            System.out.println("Base-32: " +new String(decoded, StandardCharsets.UTF_8));
        }

        // base 36
        boolean valid36 = input.matches("[0-9a-zA-Z]+");
        if (valid36) {
            BigInteger output36 = new BigInteger(input, 36);
            byte[] textBytes = output36.toByteArray();
            System.out.println("Base-36: " + new String(textBytes, StandardCharsets.UTF_8));
        }

        // base 58
        boolean valid58 = input.matches("[1-9A-HJ-NP-Za-km-z]+");
        if (valid58) {
            byte[] textBytes = decode(input, 58);
            System.out.println("Base-58: " + new String(textBytes, StandardCharsets.UTF_8));
        }

        // base 62
        boolean valid62 = input.matches("[0-9a-zA-Z]+");
        if (valid62) {
            byte[] textBytes = decode(input, 62);
            System.out.println("Base-62: " + new String(textBytes, StandardCharsets.UTF_8));
        }

        // base 64
        boolean valid64 = input.matches("[0-9a-zA-Z+/=]+");
        if (valid64) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(input);
                System.out.println("Base-64: " +
                        new String(decodedBytes, StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                System.err.println("Not valid Base64: " + e.getMessage());
            }
        }
               
    }
    
    private static byte[] decode(String input, int base) {
        BigInteger value = BigInteger.ZERO;
        BigInteger radix = BigInteger.valueOf(base);
        String alphabet;

        boolean validBase = ALPHABET.containsKey(base);
        if (!validBase) {
            System.err.println("Invalid or unavailable base: " + base);
            return new byte[0];
        }

        alphabet = ALPHABET.get(base);
        try {                                   
            // iterate through each character in the input string and convert it to its corresponding value in the specified base
            // e.g. A becomes 10, B becomes 11
            for (int i = 0; i < input.length(); i++) {
                // check if the input string contains only valid characters for the specified base
                int digit = alphabet.indexOf(input.charAt(i));
                if (digit == -1) {
                    throw new IllegalArgumentException("Invalid character '" + input.charAt(i) + "' for base " + base);
                }
                else {
                    // multiply the current value by the radix (base) and add the new digit
                    // e.g. if the current value is 10 and the new digit is 11, the new value becomes 10 * base + 11
                    value = value.multiply(radix).add(BigInteger.valueOf(digit));
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return new byte[0];
        }

        int leadingZeroes = 0;
        // count the number of leading zeroes in the input string
        // this is done as BigInteger drops leading zeroes
        while (leadingZeroes < input.length() && input.charAt(leadingZeroes) == '0') {
            leadingZeroes++;
        }

        // convert the BigInteger value to a byte array
        // the purpose of this is to represent the decimal value as a sequence of bytes
        byte[] encoded = value.equals(BigInteger.ZERO) ? new byte[0] : value.toByteArray();
        // if the first byte of the encoded array is 0, we need to skip it when copying to the decoded array
        // as the first byte of a BigInteger's byte array is signed 0 if the highest bit is 1 (two's complement)
        int offset = encoded.length > 0 && encoded[0] == 0 ? 1 : 0;
        byte[] decoded = new byte[leadingZeroes + encoded.length - offset];
        // copy the encoded bytes to the decoded array, starting from the offset and skipping any leading zeroes
        System.arraycopy(encoded, offset, decoded, leadingZeroes, encoded.length - offset);
        return decoded;
    }
    
    private static String encode(byte[] input, int base) {
        // 1 is positive
        BigInteger value = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();
        BigInteger radix = BigInteger.valueOf(base);

        boolean validBase = ALPHABET.containsKey(base);
        if (!validBase) {
            System.err.println("Invalid or unavailable base: " + base);
            return new String();
        }

        String alphabet = ALPHABET.get(base);
        // convert the byte array to a base 62 string
        // using compareTo as BigInt does not support >==<
        while (value.compareTo(BigInteger.ZERO) > 0) {
            // divide the value by the radix (base) and get the remainder
            // the remainder is used to get the corresponding character from the alphabet
            // divmod[0] = quotient, divmod[1] = remainder
            BigInteger[] divmod = value.divideAndRemainder(radix);
            // append the character corresponding to the remainder to the stringbuilder
            sb.append(alphabet.charAt(divmod[1].intValue()));
            // update the value to be the quotient for the next iteration
            value = divmod[0];
        }

        // add leading zeroes to the base 62 string if necessary
        for (int i = 0; i < input.length && input[i] == 0; i++) {
            sb.append('0');
        }

        return sb.reverse().toString();
    }
}
