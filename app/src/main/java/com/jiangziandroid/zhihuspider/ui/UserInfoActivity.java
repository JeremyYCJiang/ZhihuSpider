package com.jiangziandroid.zhihuspider.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.zhihuspider.R;
import com.jiangziandroid.zhihuspider.utils.ParseConstants;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class UserInfoActivity extends SwipeBackActivity {
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int PICK_PHOTO_REQUEST = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;//10MB
    protected Uri mMediaUri;
    @InjectView(R.id.profile_image) de.hdodenhof.circleimageview.CircleImageView mCircleImageView;
    @InjectView(R.id.profile_username) TextView mProfileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.inject(this);
        mProfileTextView.setText(ParseUser.getCurrentUser().getUsername());
        Picasso.with(this).load(ParseUser.getCurrentUser().getString(ParseConstants.KEY_USER_PHOTO_STRING_URI))
                .resize(96, 96).centerCrop().into(mCircleImageView);
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                builder.setTitle(R.string.choose_photo_source_title)
                        .setItems(R.array.photo_source, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item
                                switch (which) {
                                    case 0: //Take picture
                                        // create Intent to take a picture and return control to the calling application
                                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        // create a file to save the image
                                        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                        // set the image file name
                                        if (mMediaUri == null) {
                                            //Display an error
                                            Toast.makeText(UserInfoActivity.this, R.string.external_storage_error,
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            // start the image capture Intent
                                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                                        }
                                        break;
                                    case 1: //Choose picture
                                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        // You will usually specify a broad MIME type (such as image/* or */*),
                                        // resulting in a broad range of content types the user can select from.
                                        choosePhotoIntent.setType("image/*");
                                        startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                                        break;
                                    case 3: //Cancel
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    // Image captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    // invoke the system's media scanner to add your photo to the Media Provider's database,
                    // making it available in the Android Gallery application and to other apps.
                    Intent photoScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    photoScanIntent.setData(data.getData());
                    sendBroadcast(photoScanIntent);
                    mMediaUri = data.getData();
                    Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                    Picasso.with(UserInfoActivity.this).load(mMediaUri).resize(96,96).centerCrop().into(mCircleImageView);
                    ParseUser.getCurrentUser().put(ParseConstants.KEY_USER_PHOTO_STRING_URI, mMediaUri.toString());
                    ParseUser.getCurrentUser().saveInBackground();
                    break;
                case PICK_PHOTO_REQUEST:
                    if (data == null) {
                        Toast.makeText(this, "There was an error, please try again!", Toast.LENGTH_LONG).show();
                    } else {
                        mMediaUri = getCopiedMediaFileUri(data);
                        Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                        Picasso.with(UserInfoActivity.this).load(mMediaUri).resize(96,96).centerCrop().into(mCircleImageView);
                        ParseUser.getCurrentUser().put(ParseConstants.KEY_USER_PHOTO_STRING_URI, mMediaUri.toString());
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                    break;
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            // User cancelled the media capture
            Toast.makeText(this, "User canceled the media capture or pick!", Toast.LENGTH_LONG).show();
        }
        else {
            // Media capture failed, advise user
            Toast.makeText(this, "Media capture or pick failed, pleas try again!", Toast.LENGTH_LONG).show();
        }
    }


    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            //Get the Uri
            //1.Get the external storage directory
            String appName = UserInfoActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            //2.Create our subDirectory
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Toast.makeText(UserInfoActivity.this, "failed to create directory",
                            Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            //3.Create a file name
            //4.Create the file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                    .format(new Date());
            File mediaFile;
            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            } else {
                return null;
            }
            //5.Return the file's Uri
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private Uri getCopiedMediaFileUri(Intent data) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            //Get the Uri
            //1.Get the external storage directory
            String appName = UserInfoActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            //2.Create our subDirectory
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Toast.makeText(UserInfoActivity.this, "failed to create directory",
                            Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            //3.Create a file name
            //4.Create the file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                    .format(new Date());
            File destinationMediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            File sourceMediaFile = new File(getRealPathFromURI(data.getData()));
            Toast.makeText(UserInfoActivity.this, "RealPath: "+getRealPathFromURI(data.getData()),
                    Toast.LENGTH_LONG).show();
            try {
                copyFile(sourceMediaFile, destinationMediaFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //5.Return the file's Uri
            return Uri.fromFile(destinationMediaFile);
        } else {
            return null;
        }
    }


    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source;
        FileChannel destination;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}












//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_info, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
