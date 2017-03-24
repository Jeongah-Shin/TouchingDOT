package com.dotincorp.touchingdot;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;

import com.dotincorp.touchingdot.Alphabet.AlphabetSongActivity;
import com.dotincorp.touchingdot.Alphabet.AlphabetSwitchingActivity;
import com.dotincorp.touchingdot.Alphabet.AlphabetTestMultipleActivity;
import com.dotincorp.touchingdot.Braille.BrailleEducationActivity;
import com.dotincorp.touchingdot.Braille.BrailleUserActionActivity;
import com.dotincorp.touchingdot.Special_Letter.SpecialLetterSwitchingActivity;

import butterknife.OnClick;

/**
 * Created by wjddk on 2017-03-16.
 */

public class MenuActivity extends Activity {

    public static int menu_item = 0;

    @OnClick(R.id.menu)
        public void menuClicked(Button button){
            final PopupMenu br_edu_popup = new PopupMenu(this, button);

            br_edu_popup.getMenuInflater().inflate(R.menu.br_edu_popup,br_edu_popup.getMenu());

            br_edu_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                Intent brIntent = new Intent(getApplicationContext(), BrailleEducationActivity.class);
                Intent userIntent = new Intent(getApplicationContext(), BrailleUserActionActivity.class);
                Intent songIntent = new Intent(getApplicationContext(), AlphabetSongActivity.class);
                Intent apIntent = new Intent(getApplicationContext(), AlphabetSwitchingActivity.class);
                Intent apTestIntent = new Intent(getApplicationContext(), AlphabetTestMultipleActivity.class);
                Intent specialIntent = new Intent(getApplicationContext(),SpecialLetterSwitchingActivity.class);

                public boolean onMenuItemClick(MenuItem item){
                    switch(item.getItemId()){
                        case R.id.br_learning:
                            startActivity(brIntent);
                            finish();
                            //본인 액티비티에서 본인을 불렀을 때
                            /*if (MenuActivity.this instanceof BrailleEducationActivity) {
                                return false;
                            }*/;
                            break;
                        case R.id.user_action_br:
                            startActivity(userIntent);
                            finish();
                            break;
                        case R.id.song:
                            startActivity(songIntent);
                            finish();
                            break;
                        case R.id.ap_learning:
                            startActivity(apIntent);
                            finish();
                            break;
                        case R.id.ap_test:
                            startActivity(apTestIntent);
                            break;
                        case R.id.operator:
                            startActivity(specialIntent);
                            finish();
                            break;
                        case R.id.special_letter:
                            startActivity(specialIntent);
                            finish();
                            break;
                    }
                    return true;
                }
            });
            br_edu_popup.show();

        }
}
