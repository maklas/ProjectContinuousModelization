package ru.maklas.model.utils;

import java.util.regex.Pattern;

public class BitArray {

    private String value;
    private final int length;

    public BitArray(int bitLength) {
        this.length = bitLength;
        fill(false);
    }

    BitArray(String value) {
        this.value = value;
        this.length = value.length();
    }

    public int length() {
        return length;
    }

    public long asLong(){
        return Long.parseLong(value, 2);
    }

    public long maxValue(){
        return ((long)1) << (long)length;
    }

    public boolean get(int position){
        return value.charAt(length - position - 1) == '1';
    }

    public void set(int position, boolean set){
        value = new StringBuilder(value).replace(length - position - 1, length - position, set ? "1" : "0").toString();
    }

    public void setAsLong(long val){
        String l = Long.toBinaryString(val);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (l.length() > i){
                sb.append(l.charAt(l.length() - i - 1));
            } else {
                sb.append(value.charAt(length - 1 - i));
            }
        }
        value = sb.reverse().toString();
    }


    /** Returns new instance with bytes encoded as Gray Code **/
    public BitArray binToGray(){
        return new BitArray(Gray.binarytoGray(value));
    }

    /** Returns new instance with Graay coded bytes decoded as normal binary **/
    public BitArray grayToBin(){
        return new BitArray(Gray.graytoBinary(value));
    }

    public BitArray cpy(){
        return new BitArray(value);
    }

    public BitArray append(BitArray other){
        return new BitArray(value + other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    public String toString(int groups) {
        if (value.length() <= groups){
            return value;
        }

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (int i = value.length() - 1; i >= 0; i--) {
            builder.append(value.charAt(i));
            counter++;
            if (counter == groups && i != 0){
                builder.append(" ");
                counter = 0;
            }
        }
        return builder.reverse().toString();
    }

    public String toStringSmart(int groups) {
        if (length <= groups || length % groups != 0){
            return value;
        }
        return toString(groups);
    }

    public void fill(boolean set) {
        char c = set ? '1' : '0';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(c);
        }
        this.value = sb.toString();
    }

    public static BitArray with(BitArray... arrays){
        StringBuilder sb = new StringBuilder();
        for (BitArray array : arrays) {
            sb.append(array.value);
        }
        return new BitArray(sb.toString());
    }

    private static final Pattern badPattern = Pattern.compile("[^01]");
    public static BitArray fromString(String s){
        if (badPattern.matcher(s).find()) throw new RuntimeException("Bad input. Should only contain zeros and ones");
        return new BitArray(s);
    }
}
