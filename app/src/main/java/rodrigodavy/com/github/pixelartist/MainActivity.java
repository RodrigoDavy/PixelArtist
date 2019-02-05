package rodrigodavy.com.github.pixelartist;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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
import android.provider.MediaStore.Images.Media;
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

    private static final String SETTINGS_GRID = "grid";
    private static final String URL_ABOUT = "https://github.com/RodrigoDavy/PixelArtist/blob/master/README.md";
    private static final int MY_REQUEST_WRITE_STORAGE = 5;
    private final ArrayList<DrawerMenuItem> listMenuItem = new ArrayList<>();
    private int currentColor;
    private Button colorButtons[];
    private int colors[];
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences settings;
    private boolean grid;

    /**
     * Converts a file to a content uri, by inserting it into the media store.
     * Requires this permission: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     */
    protected static Uri convertFileToContentUri(Context context, File file) throws Exception {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri

        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String uriString = Media.insertImage(cr, imagePath, null, null);
        return Uri.parse(uriString);
    }

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
        fillScreen(ContextCompat.getColor(MainActivity.this, R.color.white));

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
                final View v = findViewById(R.id.color_button_white);

                alertDialog.setTitle(getString(R.string.alert_dialog_title_new));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_new));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                            fillScreen(ContextCompat.getColor(MainActivity.this, R.color.white));
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
                    Toast toast = Toast.makeText(MainActivity.this, R.string.file_no_files_found, Toast.LENGTH_LONG);
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
                Toast toast = Toast.makeText(this, R.string.file_could_not_open, Toast.LENGTH_LONG);
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

        if (ContextCompat.checkSelfPermission(this,
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

        //Uri uri = Uri.fromFile(imageFile);
        try {
            Uri uri = convertFileToContentUri(this, imageFile);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_something_went_wrong), Toast.LENGTH_LONG).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    // can run additional stuff here
                    Toast.makeText(this, R.string.write_permission_granted, Toast.LENGTH_LONG).show();
                } else {
                    // permission denied
                    Toast.makeText(this, R.string.write_permission_unavailable, Toast.LENGTH_LONG).show();
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
                findViewById(R.id.color_button_black),
                findViewById(R.id.color_button_eclipse),
                findViewById(R.id.color_button_grey),
                findViewById(R.id.color_button_silver),
                findViewById(R.id.color_button_white),

                findViewById(R.id.color_button_red),
                findViewById(R.id.color_button_vermilion),
                findViewById(R.id.color_button_orange),
                findViewById(R.id.color_button_amber),
                findViewById(R.id.color_button_yellow),
                findViewById(R.id.color_button_lime),
                findViewById(R.id.color_button_chartreuse),
                findViewById(R.id.color_button_harlequin),
                findViewById(R.id.color_button_green),
                findViewById(R.id.color_button_malachite),
                findViewById(R.id.color_button_mint),
                findViewById(R.id.color_button_turquoise),
                findViewById(R.id.color_button_cyan),
                findViewById(R.id.color_button_sky_blue),
                findViewById(R.id.color_button_azure),
                findViewById(R.id.color_button_sapphire),
                findViewById(R.id.color_button_blue),
                findViewById(R.id.color_button_indigo),
                findViewById(R.id.color_button_purple),
                findViewById(R.id.color_button_lt_purple),
                findViewById(R.id.color_button_magenta),
                findViewById(R.id.color_button_fuchsia),
                findViewById(R.id.color_button_rose),
                findViewById(R.id.color_button_carmine)
        };

        colors = new int[]{
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.eclipse),
                ContextCompat.getColor(this, R.color.grey),
                ContextCompat.getColor(this, R.color.silver),
                ContextCompat.getColor(this, R.color.white),

                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.vermilion),
                ContextCompat.getColor(this, R.color.orange),
                ContextCompat.getColor(this, R.color.amber),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.lime),
                ContextCompat.getColor(this, R.color.chartreuse),
                ContextCompat.getColor(this, R.color.harlequin),
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.malachite),
                ContextCompat.getColor(this, R.color.mint),
                ContextCompat.getColor(this, R.color.turquoise),
                ContextCompat.getColor(this, R.color.cyan),
                ContextCompat.getColor(this, R.color.sky_blue),
                ContextCompat.getColor(this, R.color.azure),
                ContextCompat.getColor(this, R.color.sapphire),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.indigo),
                ContextCompat.getColor(this, R.color.purple),
                ContextCompat.getColor(this, R.color.lt_purple),
                ContextCompat.getColor(this, R.color.magenta),
                ContextCompat.getColor(this, R.color.fuchsia),
                ContextCompat.getColor(this, R.color.rose),
                ContextCompat.getColor(this, R.color.carmine)
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

    //On click method that selects the current color based on the palette button pressed
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
