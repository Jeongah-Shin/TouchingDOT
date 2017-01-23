package com.dotincorpkr.englidot.Alphabet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dotincorpkr.englidot.R;

/**
 * Created by wjddk on 2017-01-23.
 */

public class Braille_insert extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet_3_braille_insert);
        Button br_submit = (Button) findViewById(R.id.br_submit);





        br_submit.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                CheckBox br1 = (CheckBox) findViewById(R.id.br1);
                CheckBox br2 = (CheckBox) findViewById(R.id.br2);
                CheckBox br3 = (CheckBox) findViewById(R.id.br3);
                CheckBox br4 = (CheckBox) findViewById(R.id.br4);
                CheckBox br5 = (CheckBox) findViewById(R.id.br5);
                CheckBox br6 = (CheckBox) findViewById(R.id.br6);

                int sum = 0;

                if (br1.isChecked()) {
                    sum += 1;
                }

                if (br2.isChecked()) {
                    sum += 2;
                }
                if (br3.isChecked()) {
                    sum += 4;
                }
                if (br4.isChecked()) {
                    sum += 8;
                }
                if (br5.isChecked()) {
                    sum += 16;
                }
                if (br6.isChecked()) {
                    sum += 32;
                }
                String result=null;

                switch (sum) {
                    case 1:
                        result = "A";
                        break;
                    case 3:
                        result = "B";
                        break;
                    case 9:
                        result = "C";
                        break;
                    case 25:
                        result = "D";
                        break;
                    case 17:
                        result = "E";
                        break;
                    case 11:
                        result = "F";
                        break;
                    case 27:
                        result = "G";
                        break;
                    case 19:
                        result = "H";
                        break;
                    case 10:
                        result = "I";
                        break;
                    case 26:
                        result = "J";
                        break;
                    case 5:
                        result = "K";
                        break;
                    case 7:
                        result = "L";
                        break;
                    case 13:
                        result = "M";
                        break;
                    case 29:
                        result = "N";
                        break;
                    case 21:
                        result = "O";
                        break;
                    case 15:
                        result = "P";
                        break;
                    case 31:
                        result = "Q";
                        break;
                    case 23:
                        result = "R";
                        break;
                    case 14:
                        result = "S";
                        break;
                    case 30:
                        result = "T";
                        break;
                    case 37:
                        result = "U";
                        break;
                    case 39:
                        result = "V";
                        break;
                    case 58:
                        result = "W";
                        break;
                    case 45:
                        result = "X";
                        break;
                    case 61:
                        result = "Y";
                        break;
                    case 53:
                        result = "Z";
                        break;
                    default:
                        result="해당 알파벳은 존재하지 않습니다.";

                }
                Toast show=Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                show.show();


            }

        });

    }

}

