package com.dotincorp.touchingdot;

/**
 * Created by wjddk on 2017-02-20.
 */

public class ASCIIcharacter {

    public static char brailleResult(int sum){
        char result ='X';
        switch (sum) {
            case 0b000001:
                result = 'A';
                break;
            case 0b000011:
                result = 'B';
                break;
            case 0b001001:
                result = 'C';
                break;
            case 0b011001:
                result = 'D';
                break;
            case 0b010001:
                result = 'E';
                break;
            case 0b001011:
                result = 'F';
                break;
            case 0b011011:
                result = 'G';
                break;
            case 0b010011:
                result = 'H';
                break;
            case 0b001010:
                result = 'I';
                break;
            case 0b011010:
                result = 'J';
                break;
            case 0b000101:
                result = 'K';
                break;
            case 0b000111:
                result = 'L';
                break;
            case 0b001101:
                result = 'M';
                break;
            case 0b011101:
                result = 'N';
                break;
            case 0b010101:
                result = 'O';
                break;
            case 0b001111:
                result = 'P';
                break;
            case 0b011111:
                result = 'Q';
                break;
            case 0b010111:
                result = 'R';
                break;
            case 0b001110:
                result = 'S';
                break;
            case 0b011110:
                result = 'T';
                break;
            case 0b100101:
                result = 'U';
                break;
            case 0b100111:
                result = 'V';
                break;
            case 0b111010:
                result = 'W';
                break;
            case 0b101101:
                result = 'X';
                break;
            case 0b111101:
                result = 'Y';
                break;
            case 0b110101:
                result = 'Z';
                break;
            case 0b101110:
                result = '!';
                break;
            case 0b010000:
                result = '\"';
                break;
            case 0b111100:
                result= '#';
                break;
            case 0b101011:
                result = '$';
                break;
            case 0b101001:
                result = '%';
                break;
            case 0b101111:
                result = '&';
                break;
            case 0b000100:
                result = '\'';
                break;
            case 0b110111:
                result = '(';
                break;
            case 0b111110:
                result = ')';
                break;
            case 0b100001:
                result = '*';
                break;
            case 0b101100:
                result = '+';
                break;
            case 0b100000:
                result = ',';
                break;
            case 0b100100:
                result = '-';
                break;
            case 0b101000:
                result = '.';
                break;
            case 0b001100:
                result = '/';
                break;
            case 0b110100:
                result = 0;
                break;
            case 0b000010:
                result = 1;
                break;
            case 0b000110:
                result = 2;
                break;
            case 0b010010:
                result = 3;
                break;
            case 0b110010:
                result = 4;
                break;
            case 0b100010:
                result = 5;
                break;
            case 0b010110:
                result = 6;
                break;
            case 0b110110:
                result = 7;
                break;
            case 0b100110:
                result = 8;
                break;
            case 0b010100:
                result = 9;
                break;
            case 0b110001:
                result = ':';
                break;
            case 0b110000:
                result = ';';
                break;
            case 0b100011:
                result = '<';
                break;
            case 0b111111:
                result = '=';
                break;
            case 0b011100:
                result = '>';
                break;
            case 0b111001:
                result = '?';
                break;
            case 0b001000:
                result = '@';
                break;
            case 101010:
                result = '[';
                break;
            case 0b110011:
                result = '\\';
                break;
            case 0b111011:
                result = ']';
                break;
            case 0b011000:
                result = '^';
                break;
            case 0b111000:
                result = '_';
                break;
            default:

                break;
        }
        return result;
    }
}
