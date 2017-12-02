package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private Drawable currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button colorButtons[] = {
                (Button) findViewById(R.id.color_button_0),
                (Button) findViewById(R.id.color_button_1),
                (Button) findViewById(R.id.color_button_2),
                (Button) findViewById(R.id.color_button_3),
                (Button) findViewById(R.id.color_button_4),
                (Button) findViewById(R.id.color_button_5),
                (Button) findViewById(R.id.color_button_6),
                (Button) findViewById(R.id.color_button_7),
                (Button) findViewById(R.id.color_button_8),
                (Button) findViewById(R.id.color_button_9),
                (Button) findViewById(R.id.color_button_10),
                (Button) findViewById(R.id.color_button_11),
                (Button) findViewById(R.id.color_button_12),
                (Button) findViewById(R.id.color_button_13),
                (Button) findViewById(R.id.color_button_14),
                (Button) findViewById(R.id.color_button_15)};

        currentColor = colorButtons[0].getBackground();
        getSupportActionBar().setBackgroundDrawable(currentColor);

        for(int n=0;n<colorButtons.length;n++) {

            colorButtons[n].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ColorDrawable c = (ColorDrawable) view.getBackground();
                    Intent i = new Intent(MainActivity.this, ColorSelector.class);
                    i.putExtra("id",view.getId());
                    i.putExtra("color",c.getColor());
                    startActivityForResult(i,1);

                    return false;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Button b = (Button) findViewById(data.getIntExtra("id",0));
                b.setBackgroundColor(data.getIntExtra("color",0));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    public void selectColor(View v) {
        Button b = (Button) v;

        currentColor = b.getBackground();

        getSupportActionBar().setBackgroundDrawable(currentColor);
    }

    public void changeColor(View v) {
        Button b = (Button) v;

        b.setBackground(currentColor);
    }
}
