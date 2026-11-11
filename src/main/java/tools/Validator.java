package tools;

import java.util.Arrays;

public class Validator {
    private final int[] xRange = {-3, -2, -1, 0, 1, 2, 3, 4, 5};
    private final int[] rRange = {1, 2, 3 , 4, 5};
    public boolean validateX(String xStr){
        double x;
        try {
            if (xStr == null) {
                throw new NullPointerException("Field X is null!");
            }
            String trimmedX = xStr.trim().replace(",",".");
            if (trimmedX.isEmpty()){
                throw new NumberFormatException("Field X is empty or whitespace string!");
            }
            if (trimmedX.length()>15 || !trimmedX.matches("-?\\d+(\\.\\d+)?")){
                throw new NumberFormatException("Field X should be number!");
            }
            x = Double.parseDouble(trimmedX);
            if (x < xRange[0] || x > xRange[xRange.length - 1]){
                throw new IllegalArgumentException("Field X should be in range: " + Arrays.toString(xRange));
            }
            return true;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (NumberFormatException e){
            return false;
        }
        catch (IllegalArgumentException e){
            return  false;
        }
    }

    public boolean validateY(String yStr){
        double y;
        try {
            if (yStr == null) {
                throw new NullPointerException("Field Y is null!");
            }
            String trimmedY = yStr.trim().replace(',','.');
            if (trimmedY.isEmpty()){
                throw new NumberFormatException("Field Y is empty or whitespace string!");
            }
            if (trimmedY.length()>15){
                throw new NumberFormatException("Length of input field of Y should not exceed 15 characters!");
            }
            try {
                y = Double.parseDouble(trimmedY);
            }
            catch (NumberFormatException e){
                throw new NumberFormatException("Field Y should be number!");
            }
            if ((y>-5 && y<5)==false){
                throw new IllegalArgumentException("Field Y should be in range: (-5; 5)");
            }
            return true;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (NumberFormatException e){
            return false;
        }
        catch (IllegalArgumentException e){
            return  false;
        }
    }

    public boolean validateR(String rStr){
        int r;
        try {
            if (rStr == null) {
                throw new NullPointerException("Field R is null!");
            }
            String trimmedR = rStr.trim().replace(",",".");
            if (trimmedR.isEmpty()){
                throw new NumberFormatException("Field R is empty or whitespace string!");
            }
            try {
                r = Integer.parseInt(trimmedR);
            }
            catch (NumberFormatException e){
                throw new NumberFormatException("Field R should be integer!");
            }
            boolean isInRangeR = false;
            for (double elem : rRange){
                if (elem==r){
                    isInRangeR=true;
                    break;
                }
            }
            if (isInRangeR==false){
                throw new IllegalArgumentException("Field R should be in range: " + Arrays.toString(rRange));
            }
            return true;
        }
        catch (NullPointerException e){
            return false;
        }
        catch (NumberFormatException e){
            return false;
        }
        catch (IllegalArgumentException e){
            return  false;
        }
    }
    public boolean validatePageDbNumberStr(String pageDbNumberStr) {
        int pageNumber;
        try {
            if (pageDbNumberStr == null) {
                throw new NullPointerException("Field pageDbNumber is null!");
            }
            String trimmed = pageDbNumberStr.trim().replace(",", ".");
            if (trimmed.isEmpty()) {
                throw new NumberFormatException("Field pageDbNumber is empty or whitespace string!");
            }
            try {
                pageNumber = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Field pageDbNumber should be integer!");
            }
            if (pageNumber < 1) {
                throw new IllegalArgumentException("Field pageDbNumber should be >= 1");
            }

            return true;
        } catch (NullPointerException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validatePageDbSizeStr(String pageDbSizeStr) {
        int pageSize;
        try {
            if (pageDbSizeStr == null) {
                throw new NullPointerException("Field pageDbSize is null!");
            }
            String trimmed = pageDbSizeStr.trim().replace(",", ".");
            if (trimmed.isEmpty()) {
                throw new NumberFormatException("Field pageDbSize is empty or whitespace string!");
            }
            try {
                pageSize = Integer.parseInt(trimmed);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Field pageDbSize should be integer!");
            }
            if (pageSize < 1 || pageSize > 100) {
                throw new IllegalArgumentException("Field pageDbSize should be in range: [1; 100]");
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}