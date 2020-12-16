package ro.ubb;

public class Helpers {
    public static double chooseOperation(double a, double b, String sign){
        switch (sign) {
            case "/":
                return a / b;
            case "*":
                return a * b;
            case "+":
                return a + b;
            case "-":
                return a - b;
        }
        return 0;
    }

    public static String nextBlockType(String type){
        switch (type) {
            case "Y":
                return "U";
            case "U":
                return "V";
            case "V":
                return "Y";
        }
        return "ERROR";
    }

    public static int getSizeOfAmplitude(int coefficient){
        if(coefficient==-1 || coefficient ==1) {
            return 1;
        }
        else if(coefficient==-3 || coefficient ==-2 || coefficient ==2 || coefficient ==3) {
            return 2;
        }
        else if((-7<=coefficient && coefficient<=-4) || (4<=coefficient && coefficient<=7)) {
            return 3;
        }
        else if((-15<=coefficient && coefficient<=-8) || (8<=coefficient && coefficient<=15)) {
            return 4;
        }
        else if((-31<=coefficient && coefficient<=-16) || (16<=coefficient && coefficient<=31)) {
            return 5;
        }
        else if((-63<=coefficient && coefficient<=-32) || (32<=coefficient && coefficient<=63)) {
            return 6;
        }
        else if((-127<=coefficient && coefficient<=-64) || (64<=coefficient && coefficient<=127)) {
            return 7;
        }
        else if((-255<=coefficient && coefficient<=-128) || (128<=coefficient && coefficient<=255)) {
            return 8;
        }
        else if((-511<=coefficient && coefficient<=-256) || (256<=coefficient && coefficient<=511)) {
            return 9;
        }
        else if((-1023<=coefficient && coefficient<=-512) || (512<=coefficient && coefficient<=1023)) {
            return 10;
        }
        return 0;
    }
}
