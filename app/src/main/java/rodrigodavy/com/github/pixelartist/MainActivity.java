package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int currentColor;
    private Button colorButtons[];
    private int colors[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorButtons = new Button[] {
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
                (Button) findViewById(R.id.color_button_15)
        };

        colors = new int[] {
                ContextCompat.getColor(this,R.color.color_0),
                ContextCompat.getColor(this,R.color.color_1),
                ContextCompat.getColor(this,R.color.color_2),
                ContextCompat.getColor(this,R.color.color_3),
                ContextCompat.getColor(this,R.color.color_4),
                ContextCompat.getColor(this,R.color.color_5),
                ContextCompat.getColor(this,R.color.color_6),
                ContextCompat.getColor(this,R.color.color_7),
                ContextCompat.getColor(this,R.color.color_8),
                ContextCompat.getColor(this,R.color.color_9),
                ContextCompat.getColor(this,R.color.color_10),
                ContextCompat.getColor(this,R.color.color_11),
                ContextCompat.getColor(this,R.color.color_12),
                ContextCompat.getColor(this,R.color.color_13),
                ContextCompat.getColor(this,R.color.color_14),
                ContextCompat.getColor(this,R.color.color_15)
        };

        for(int i=0;i<colorButtons.length;i++) {

            GradientDrawable cd = (GradientDrawable) colorButtons[i].getBackground();
            cd.setColor(colors[i]);

            colorButtons[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int n = 0;

                    for(Button b: colorButtons) {
                        if( view.getId() == b.getId()) {
                            break;
                        }

                        n += 1;
                    }

                    Intent i = new Intent(MainActivity.this, ColorSelector.class);
                    i.putExtra("id",view.getId());
                    i.putExtra("position",n);
                    i.putExtra("color",colors[n]);

                    if(colors[n]==currentColor) {
                        i.putExtra("currentColor",true);
                    } else {
                        i.putExtra("currentColor",false);
                    }
                    startActivityForResult(i,1);

                    return false;
                }
            });
        }

        selectColor(colorButtons[0]);

        Log.i("OnCreate","TRIGGERED");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Button b = (Button) findViewById(data.getIntExtra("id",0));
                GradientDrawable gd = (GradientDrawable) b.getBackground();
                int c = data.getIntExtra("color",0);
                gd.setColor(c);

                colors[data.getIntExtra("position",0)] = c;

                if(data.getBooleanExtra("currentColor",false)) {
                    currentColor = c;
                    findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        switch (item.getItemId()) {
            case R.id.menu_new:
                final View v = findViewById(R.id.color_button_1);

                alertDialog.setTitle(getString(R.string.alert_dialog_title_new));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_new));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fillScreen(ContextCompat.getColor(MainActivity.this,R.color.color_1));
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            case R.id.menu_fill:
                alertDialog.setTitle(getString(R.string.alert_dialog_title_fill));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_fill));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fillScreen(currentColor);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            case R.id.menu_save:
                Toast toast = Toast.makeText(this, R.string.toast_save,Toast.LENGTH_LONG);
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillScreen(int color) {
        LinearLayout paper = (LinearLayout) findViewById(R.id.paper_linear_layout);

        for(int i=0;i<paper.getChildCount();i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for(int j=0;j<l.getChildCount();j++) {
                View pixel = l.getChildAt(j);

                pixel.setBackgroundColor(color);
            }
        }
    }

    public void selectColor(View v) {
        int i = 0;

        for(Button b: colorButtons) {
            if( v.getId() == b.getId()) {
                break;
            }

            i += 1;
        }

        currentColor = colors[i];

        findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
    }

    public void changeColor(View v) { v.setBackgroundColor(currentColor); }
}
