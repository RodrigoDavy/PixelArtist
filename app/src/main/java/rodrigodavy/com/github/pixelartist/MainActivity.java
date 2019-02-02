package rodrigodavy.com.github.pixelartist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private int currentColor;
    private Button colorButtons[];
    private int colors[];

    private ActionBarDrawerToggle drawerToggle;
    private final ArrayList<DrawerMenuItem> listMenuItem = new ArrayList<>();

    private SharedPreferences settings;
    private boolean grid;

    private static final String SETTINGS_GRID = "grid";
    private static final String URL_ABOUT = "https://github.com/RodrigoDavy/PixelArtist/blob/master/README.md";

    private static final int MY_REQUEST_WRITE_STORAGE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateDrawerHeader();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        ListView leftDrawer = findViewById(R.id.left_drawer);

        addDrawerItems();

        DrawerMenuItemAdapter adapter = new DrawerMenuItemAdapter(this, listMenuItem);
        leftDrawer.setAdapter(adapter);

        leftDrawer.setOnItemClickListener((adapterView, view, i, l) -> listMenuItem.get(i).execute());

        initPalette();
        initPixels();

        settings = getPreferences(0);
        grid = settings.getBoolean(SETTINGS_GRID, true);

        if (!grid) {
            grid = true;
            pixelGrid();
        }

        openFile(".tmp", false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveFile(".tmp", false);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SETTINGS_GRID, grid);
        editor.apply();
    }

    //Applying changes made in the ColorSelector activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Button b = findViewById(data.getIntExtra("id", 0));
                GradientDrawable gd = (GradientDrawable) b.getBackground();
                int c = data.getIntExtra("color", 0);
                gd.setColor(c);

                colors[data.getIntExtra("position", 0)] = c;

                if (data.getBooleanExtra("currentColor", false)) {
                    currentColor = c;
                    findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
                }
            }
        }
    }

    private void addDrawerItems() {
        DrawerMenuItem drawerNew = new DrawerMenuItem(R.drawable.menu_new, R.string.menu_new) {
            @Override
            public void execute() {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                final View v = findViewById(R.id.color_button_1);

                alertDialog.setTitle(getString(R.string.alert_dialog_title_new));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_new));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                            fillScreen(ContextCompat.getColor(MainActivity.this, R.color.color_1));
                            updateDrawerHeader();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        };

        DrawerMenuItem drawerOpen = new DrawerMenuItem(R.drawable.menu_open, R.string.menu_open) {
            @Override
            public void execute() {
                File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                if ((path != null) && (path.listFiles().length > 0)) {
                    File[] files = path.listFiles();

                    List<CharSequence> list = new ArrayList<>();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.menu_open);

                    for (File file : files) {
                        if (file.getName().contains(".pixel_artist")) {
                            list.add(0, file.getName().replace(".pixel_artist", ""));
                        }
                    }

                    final CharSequence[] charSequences = list.toArray(new CharSequence[0]);

                    builder.setItems(charSequences, (dialogInterface, i) -> {
                        openFile(charSequences[i].toString() + ".pixel_artist", true);
                        updateDrawerHeader();
                    });
                    builder.show();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.no_files_found, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        DrawerMenuItem drawerSave = new DrawerMenuItem(R.drawable.menu_save, R.string.menu_save) {
            @Override
            public void execute() {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                LayoutInflater layoutInflater = MainActivity.this.getLayoutInflater();

                alertDialog.setTitle(getString(R.string.menu_save));
                alertDialog.setView(layoutInflater.inflate(R.layout.dialog_save, null));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                            EditText editText = alertDialog.findViewById(R.id.dialog_filename_edit_text);
                            String filename = null;
                            if (editText != null) {
                                filename = editText.getText() + ".pixel_artist";
                            }
                            saveFile(filename, true);
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        };

        DrawerMenuItem drawerExport = new DrawerMenuItem(R.drawable.menu_export, R.string.menu_export) {
            @Override
            public void execute() {
                String filename;

                Calendar calendar = Calendar.getInstance();

                long unixTime = System.currentTimeMillis() / 1000;
                unixTime %= 1000000;

                filename = "IMG_"
                        + calendar.get(Calendar.YEAR)
                        + calendar.get(Calendar.MONTH)
                        + calendar.get(Calendar.DAY_OF_MONTH) + "_" + unixTime + ".jpg";

                screenShot(findViewById(R.id.paper_linear_layout), filename);
            }
        };

        DrawerMenuItem drawerAbout = new DrawerMenuItem(R.drawable.menu_about, R.string.menu_about) {
            @Override
            public void execute() {
                Uri uri = Uri.parse(URL_ABOUT);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        };

        listMenuItem.add(drawerNew);
        listMenuItem.add(drawerOpen);
        listMenuItem.add(drawerSave);
        listMenuItem.add(drawerExport);
        listMenuItem.add(drawerAbout);
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

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_fill:
                alertDialog.setTitle(getString(R.string.alert_dialog_title_fill));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_fill));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                            fillScreen(currentColor);
                            updateDrawerHeader();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();

                return true;
            case R.id.menu_grid:
                pixelGrid();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openFile(String fileName, boolean showToast) {
        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File openFile = new File(imageFolder, fileName);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile));
            int color;
            String value;

            int x, y;

            if ((value = bufferedReader.readLine()) != null) {
                x = Integer.valueOf(value);
            } else {
                throw new IOException();
            }

            if ((value = bufferedReader.readLine()) != null) {
                y = Integer.valueOf(value);
            } else {
                throw new IOException();
            }

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {

                    if ((value = bufferedReader.readLine()) != null) {
                        color = Integer.valueOf(value);
                    } else {
                        throw new IOException();
                    }

                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    v.setBackgroundColor(color);
                }
            }

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.file_opened, Toast.LENGTH_SHORT);
                toast.show();
            }

        } catch (FileNotFoundException e) {
            Log.e("MainActivity.openFile", "File not found");
            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("MainActivity.openFile", "Could not open file");
            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.could_not_open, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void saveFile(String fileName, boolean showToast) {
        if (isExternalStorageWritable()) {
            Log.e(MainActivity.class.getName(), "External Storage is not writable");
        }

        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File saveFile = new File(imageFolder, fileName);

        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.append("16\n16\n");

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    int color = ((ColorDrawable) v.getBackground()).getColor();
                    fileWriter.append(String.valueOf(color));
                    fileWriter.append("\n");
                }
            }
            fileWriter.flush();
            fileWriter.close();

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("MainActivity.saveFile", "File not found");

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.toast_not_saved, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void screenShot(View view, String filename) {

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_REQUEST_WRITE_STORAGE);

            return;

        }

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        if (isExternalStorageWritable()) {
            Log.e(MainActivity.class.getName(), "External storage is not writable");
        }

        File imageFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        boolean success = true;

        if (!imageFolder.exists()) {
            success = imageFolder.mkdirs();
        }

        if (success) {
            File imageFile = new File(imageFolder, filename);

            FileOutputStream outputStream;

            try {

                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }

                outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                openScreenshot(imageFile);
            } catch (FileNotFoundException e) {
                Log.e(MainActivity.class.getName(), "File not found");
            } catch (IOException e) {
                Log.e(MainActivity.class.getName(), "IOException related to generating bitmap file");
            }
        } else {
            Toast toast = Toast.makeText(this, R.string.toast_could_not_create_app_folder, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // can run additional stuff here
                    Toast.makeText(this, R.string.granted_write_permission, Toast.LENGTH_LONG).show();
                } else {
                    // permission denied
                    Toast.makeText(this, R.string.no_write_permission, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return !Environment.MEDIA_MOUNTED.equals(state);
    }

    private void updateDrawerHeader() {
        View view = findViewById(R.id.paper_linear_layout);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        ImageView header = findViewById(R.id.drawer_header);
        header.setImageBitmap(bitmap);
    }

    private void initPalette() {
        colorButtons = new Button[]{
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

        colors = new int[]{
                ContextCompat.getColor(this, R.color.color_0),
                ContextCompat.getColor(this, R.color.color_1),
                ContextCompat.getColor(this, R.color.color_2),
                ContextCompat.getColor(this, R.color.color_3),
                ContextCompat.getColor(this, R.color.color_4),
                ContextCompat.getColor(this, R.color.color_5),
                ContextCompat.getColor(this, R.color.color_6),
                ContextCompat.getColor(this, R.color.color_7),
                ContextCompat.getColor(this, R.color.color_8),
                ContextCompat.getColor(this, R.color.color_9),
                ContextCompat.getColor(this, R.color.color_10),
                ContextCompat.getColor(this, R.color.color_11),
                ContextCompat.getColor(this, R.color.color_12),
                ContextCompat.getColor(this, R.color.color_13),
                ContextCompat.getColor(this, R.color.color_14),
                ContextCompat.getColor(this, R.color.color_15)
        };

        for (int i = 0; i < colorButtons.length; i++) {

            GradientDrawable cd = (GradientDrawable) colorButtons[i].getBackground();
            cd.setColor(colors[i]);

            colorButtons[i].setOnLongClickListener(view -> {
                int n = 0;

                for (Button b : colorButtons) {
                    if (view.getId() == b.getId()) {
                        break;
                    }

                    n += 1;
                }

                Intent i1 = new Intent(MainActivity.this, ColorSelector.class);
                i1.putExtra("id", view.getId());
                i1.putExtra("position", n);
                i1.putExtra("color", colors[n]);

                if (colors[n] == currentColor) {
                    i1.putExtra("currentColor", true);
                } else {
                    i1.putExtra("currentColor", false);
                }
                startActivityForResult(i1, 1);

                return false;
            });
        }

        selectColor(colorButtons[0]);
    }

    //Initializes the "pixels" (basically sets OnLongClickListener on them)
    private void initPixels() {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                //Sets OnLongCLickListener to be able to select current color based on that pixel's color
                pixel.setOnLongClickListener(view -> {
                    selectColor(((ColorDrawable) view.getBackground()).getColor());
                    return false;
                });
            }
        }
    }

    //Shows or hides the pixels boundaries from the paper_linear_layout
    private void pixelGrid() {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        int x;
        int y;

        if (grid) {
            x = 0;
            y = 0;
        } else {
            x = 1;
            y = 1;
        }

        grid = !grid;

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) pixel.getLayoutParams();

                layoutParams.setMargins(x, y, 0, 0);
                pixel.setLayoutParams(layoutParams);
            }
        }
    }

    //Fills paper_linear_layout with chosen color
    private void fillScreen(int color) {
        LinearLayout paper = findViewById(R.id.paper_linear_layout);

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                pixel.setBackgroundColor(color);
            }
        }
    }

    //On click method that selects the current color based on the pallete button pressed
    public void selectColor(View v) {
        int i = 0;

        for (Button b : colorButtons) {
            if (v.getId() == b.getId()) {
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
    public void changeColor(View v) {
        v.setBackgroundColor(currentColor);
    }
}
