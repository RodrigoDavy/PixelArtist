package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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

        for(Button b: colorButtons) {

            b.setOnLongClickListener(new View.OnLongClickListener() {
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

        currentColor = colorButtons[0].getBackground();
        getSupportActionBar().setBackgroundDrawable(currentColor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Button b = (Button) findViewById(data.getIntExtra("id",0));
                b.setBackgroundColor(data.getIntExtra("color",0));
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
                                fillScreen(v.getBackground());
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

    private void fillScreen(Drawable color) {
        LinearLayout paper = (LinearLayout) findViewById(R.id.paper_linear_layout);

        for(int i=0;i<paper.getChildCount();i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for(int j=0;j<l.getChildCount();j++) {
                View pixel = l.getChildAt(j);
                pixel.setBackground(color);
            }
        }
    }

    public void selectColor(View v) {
        Button b = (Button) v;

        currentColor = b.getBackground();

        getSupportActionBar().setBackgroundDrawable(currentColor);
    }

    public void changeColor(View v) {
        v.setBackground(currentColor);
    }
}
