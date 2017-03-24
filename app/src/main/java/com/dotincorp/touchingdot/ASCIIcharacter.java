package com.dotincorp.touchingdot;

/**
 * Created by wjddk on 2017-02-20.
 */

public class ASCIIcharacter {

    public static char brailleResult(int sum){
        char result ='X';
        switch (sum) {
            case 0b000001:
                result = 'a';
                break;
            case 0b000011:
                result = 'b';
                break;
            case 0b001001:
                result = 'c';
                break;
            case 0b011001:
                result = 'd';
                break;
            case 0b010001:
                result = 'e';
                break;
            case 0b001011:
                result = 'f';
                break;
            case 0b011011:
                result = 'g';
                break;
            case 0b010011:
                result = 'h';
                break;
            case 0b001010:
                result = 'i';
                break;
            case 0b011010:
                result = 'j';
                break;
            case 0b000101:
                result = 'k';
                break;
            case 0b000111:
                result = 'l';
                break;
            case 0b001101:
                result = 'm';
                break;
            case 0b011101:
                result = 'n';
                break;
            case 0b010101:
                result = 'o';
                break;
            case 0b001111:
                result = 'p';
                break;
            case 0b011111:
                result = 'q';
                break;
            case 0b010111:
                result = 'r';
                break;
            case 0b001110:
                result = 's';
                break;
            case 0b011110:
                result = 't';
                break;
            case 0b100101:
                result = 'u';
                break;
            case 0b100111:
                result = 'v';
                break;
            case 0b111010:
                result = 'w';
                break;
            case 0b101101:
                result = 'x';
                break;
            case 0b111101:
                result = 'y';
                break;
            case 0b110101:
                result = 'z';
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
