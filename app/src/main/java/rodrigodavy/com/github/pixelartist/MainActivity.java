package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int currentColor;
    private Button colorButtons[];
    private int colors[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPalette();
        initPixels();
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
            case R.id.menu_grid:
                pixelGrid();
                return true;
            case R.id.menu_save:
                Toast toast = Toast.makeText(this, R.string.toast_save,Toast.LENGTH_LONG);
                toast.show();
                screenShot(findViewById(R.id.paper_linear_layout));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        if(!isExternalStorageWritable()) {
            Log.e(MainActivity.class.getName(),"External Storage is not writable");
        }

        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(imageFolder,"teste.jpg");

        FileOutputStream outputStream = null;

        if(!imageFile.exists()) {
            Log.e(MainActivity.class.getName(),"File was not created");
        }

        try {
            outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (FileNotFoundException e) {
            Log.e(MainActivity.class.getName(),"File not found");
        } catch (IOException e) {
        Log.e(MainActivity.class.getName(),"IOException related to generating bitmap file");
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void initPalette() {
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
    }

    //Initializes the "pixels" (basically sets OnLongClickListerner on them)
    private void initPixels() {
        LinearLayout paper = (LinearLayout) findViewById(R.id.paper_linear_layout);

        for(int i=0;i<paper.getChildCount();i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for(int j=0;j<l.getChildCount();j++) {
                View pixel = l.getChildAt(j);

                //Sets OnLongCLickListener to be able to select current color based on that pixel's color
                pixel.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        selectColor(((ColorDrawable) view.getBackground()).getColor());
                        return false;
                    }
                });
            }
        }
    }

    //Shows or hides the pixels boundaries from the paper_linear_layout
    private void pixelGrid() {
        LinearLayout paper = (LinearLayout) findViewById(R.id.paper_linear_layout);

        for(int i=0;i<paper.getChildCount();i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for(int j=0;j<l.getChildCount();j++) {
                View pixel = l.getChildAt(j);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) pixel.getLayoutParams();

                if(layoutParams.leftMargin>0) {
                    layoutParams.setMargins(0,0,0,0);
                } else {
                    layoutParams.setMargins(1,1,0,0);
                }

                pixel.setLayoutParams(layoutParams);
            }
        }
    }

    //Fills paper_linear_layout with chosen color
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

    //On click method that selects the current color based on the pallete button pressed
    public void selectColor(View v) {
        int i = 0;

        for(Button b: colorButtons) {
            if( v.getId() == b.getId()) {
                break;
            }

            i += 1;
        }

        selectColor(colors[i]);
    }

    //Sets the current color based on the "color" argument
    public void selectColor(int color) {
        currentColor = color;

        findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
    }

    //Onclick method that changes the color of a single "pixel"
    public void changeColor(View v) { v.setBackgroundColor(currentColor); }
}
