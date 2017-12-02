package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class ColorSelector extends AppCompatActivity {

    private int red=0;
    private int green=0;
    private int blue=0;

    private int returnId = 0;

    public void apply(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("color",Color.rgb(red,green,blue));
        returnIntent.putExtra("id",returnId);

        if(returnId == 0) {
            setResult(ColorSelector.RESULT_CANCELED,returnIntent);
        }else{
            setResult(ColorSelector.RESULT_OK,returnIntent);
        }

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selector);

        Button old_color_box = (Button) findViewById(R.id.old_color);
        Button new_color_box = (Button) findViewById(R.id.old_color);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                old_color_box.setBackgroundColor(Color.rgb(red,green,blue));
            }else{
                old_color_box.setBackgroundColor(extras.getInt("color"));
                returnId = extras.getInt("id");
            }
        } else {
            old_color_box.setBackgroundColor(savedInstanceState.getInt("color"));
            returnId = savedInstanceState.getInt("id");
        }

        ColorDrawable cd = (ColorDrawable) old_color_box.getBackground();
        int color = cd.getColor();
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);

        new_color_box.setBackground(old_color_box.getBackground());

        SeekBar seekBars[] = {
                findViewById(R.id.seekBarRed),
                findViewById(R.id.seekBarGreen),
                findViewById(R.id.seekBarBlue)};

        for(int n=0;n<seekBars.length;n++) {



            if(seekBars[n] == (SeekBar) findViewById(R.id.seekBarRed)) {
                seekBars[n].setProgress(red);
            }else if(seekBars[n] == (SeekBar) findViewById(R.id.seekBarGreen)) {
                seekBars[n].setProgress(green);
            }else{
                seekBars[n].setProgress(blue);
            }

            seekBars[n].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    int multiplier;

                    if(seekBar == (SeekBar) findViewById(R.id.seekBarRed)) {
                        red = i;
                    }else if(seekBar == (SeekBar) findViewById(R.id.seekBarGreen)) {
                        green = i;
                    }else{
                        blue = i;
                    }

                    Button button = (Button) findViewById(R.id.new_color);
                    button.setBackgroundColor(Color.rgb(red,green,blue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }
}
