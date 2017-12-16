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

    private int oldColor=0;

    private int returnId = 0;
    private int position = -1;
    private boolean currentColor = false;

    public void apply(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position", position);
        returnIntent.putExtra("color",Color.rgb(red,green,blue));
        returnIntent.putExtra("id",returnId);
        returnIntent.putExtra("currentColor",currentColor);

        if(returnId == 0) {
            setResult(ColorSelector.RESULT_CANCELED,returnIntent);
        }else{
            setResult(ColorSelector.RESULT_OK,returnIntent);
        }

        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putInt("id", returnId);
        savedInstanceState.putInt("color", oldColor);
        savedInstanceState.putInt("position", position);
        savedInstanceState.putBoolean("currentColor", currentColor);

        savedInstanceState.putInt("red", red);
        savedInstanceState.putInt("green", green);
        savedInstanceState.putInt("blue", blue);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selector);

        Button old_color_box = (Button) findViewById(R.id.old_color);
        Button new_color_box = (Button) findViewById(R.id.new_color);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                old_color_box.setBackgroundColor(Color.rgb(red,green,blue));
            }else{
                oldColor = extras.getInt("color");
                returnId = extras.getInt("id");
                position = extras.getInt("position");
                currentColor = extras.getBoolean("currentColor");

                red = Color.red(oldColor);
                green = Color.green(oldColor);
                blue = Color.blue(oldColor);
            }
        } else {
            oldColor = savedInstanceState.getInt("color");
            returnId = savedInstanceState.getInt("id");
            position = savedInstanceState.getInt("position");
            currentColor = savedInstanceState.getBoolean("currentColor");

            red = savedInstanceState.getInt("red");
            green = savedInstanceState.getInt("green");
            blue = savedInstanceState.getInt("blue");
        }

        old_color_box.setBackgroundColor(oldColor);

        new_color_box.setBackgroundColor(Color.rgb(red,green,blue));

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
