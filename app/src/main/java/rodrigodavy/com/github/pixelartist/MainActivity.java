package rodrigodavy.com.github.pixelartist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
                Button b = findViewById(data.getIntExtra("id",0));
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
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        LayoutInflater layoutInflater = this.getLayoutInflater();

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
            case R.id.menu_open:
                alertDialog.setTitle(getString(R.string.menu_open));
                alertDialog.setView(layoutInflater.inflate(R.layout.dialog_save,null));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                EditText editText = alertDialog.findViewById(R.id.dialog_filename_edit_text);

                                String filename;
                                if (editText != null) {
                                    filename = editText.getText() + ".pixel_artist";
                                    openFile(filename);
                                }

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
                alertDialog.setTitle(getString(R.string.menu_save));
                alertDialog.setView(layoutInflater.inflate(R.layout.dialog_save,null));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                EditText editText = alertDialog.findViewById(R.id.dialog_filename_edit_text);
                                String filename = null;
                                if (editText != null) {
                                    filename = editText.getText() + ".pixel_artist";
                                }

                                saveFile(filename);
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
            case R.id.menu_export:
                alertDialog.setTitle(getString(R.string.menu_export));
                alertDialog.setView(layoutInflater.inflate(R.layout.dialog_save,null));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                EditText editText = alertDialog.findViewById(R.id.dialog_filename_edit_text);
                                String filename = null;
                                if (editText != null) {
                                    filename = editText.getText() + ".pixel_artist";
                                }

                                screenShot(findViewById(R.id.paper_linear_layout),filename);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openFile(String fileName) {
        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File openFile = new File(imageFolder,fileName);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile));
            int color;
            String value;

            int x,y;

            if((value = bufferedReader.readLine()) != null) {
                x = Integer.valueOf(value);
            }else{
                throw new IOException();
            }

            if((value = bufferedReader.readLine()) != null) {
                y = Integer.valueOf(value);
            }else{
                throw new IOException();
            }

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);

            for(int i=0;i<x;i++) {
                for(int j=0;j<y;j++) {

                    if((value = bufferedReader.readLine()) != null) {
                        color = Integer.valueOf(value);
                    }else{
                        throw new IOException();
                    }

                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    v.setBackgroundColor(color);
                }
            }

            Toast toast = Toast.makeText(this, R.string.file_opened,Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
            Log.e("MainActivity.openFile","File not found");
            Toast toast = Toast.makeText(this, R.string.file_not_found,Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            Log.e("MainActivity.openFile","Could not openn file");
            Toast toast = Toast.makeText(this, R.string.could_not_open,Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void saveFile(String fileName) {
        if(!isExternalStorageWritable()) {
            Log.e(MainActivity.class.getName(),"External Storage is not writable");
        }

        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File saveFile = new File(imageFolder,fileName);

        try {
            if(!saveFile.exists()) {
                saveFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.append("16\n16\n");

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);
            for(int i=0;i<16;i++) {
                for(int j=0;j<16;j++) {
                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    int color = ((ColorDrawable) v.getBackground()).getColor();
                    fileWriter.append(String.valueOf(color));
                    fileWriter.append("\n");
                }
            }
            fileWriter.flush();
            fileWriter.close();

            Toast toast = Toast.makeText(this, R.string.toast_saved,Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            Log.e("MainActivity.saveFile","File not found");
            Toast toast = Toast.makeText(this, R.string.toast_not_saved,Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void screenShot(View view,String filename) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        if(!isExternalStorageWritable()) {
            Log.e(MainActivity.class.getName(),"External Storage is not writable");
        }

        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(imageFolder,filename);

        FileOutputStream outputStream;

        try {

            if(!imageFile.exists()) {
                imageFile.createNewFile();
            }

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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void initPalette() {
        colorButtons = new Button[] {
                findViewById(R.id.color_button_0),
                findViewById(R.id.color_button_1),
                findViewById(R.id.color_button_2),
                findViewById(R.id.color_button_3),
                findViewById(R.id.color_button_4),
                findViewById(R.id.color_button_5),
                findViewById(R.id.color_button_6),
                findViewById(R.id.color_button_7),
                findViewById(R.id.color_button_8),
                findViewById(R.id.color_button_9),
                findViewById(R.id.color_button_10),
                findViewById(R.id.color_button_11),
                findViewById(R.id.color_button_12),
                findViewById(R.id.color_button_13),
                findViewById(R.id.color_button_14),
                findViewById(R.id.color_button_15)
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
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

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
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

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
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

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
