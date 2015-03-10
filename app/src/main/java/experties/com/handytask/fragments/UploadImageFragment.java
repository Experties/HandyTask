package experties.com.handytask.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import experties.com.handytask.R;

/**
 * Created by hetashah on 3/9/15.
 */
public class UploadImageFragment extends DialogFragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PHOTO = 1890;

    private Button btnCameraUpload;
    private Button btnGalleryUpload;

    public interface UploadDialogListener {
        void onSelectImageDialog(byte[] byteArray);
    }

    public UploadImageFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.upload_cust_dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.upload_dialog, parent, false);
        btnCameraUpload = (Button) v.findViewById(R.id.btnCameraUpload);
        btnCameraUpload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        btnGalleryUpload = (Button) v.findViewById(R.id.btnGalleryUpload);
        btnGalleryUpload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        } else if (requestCode == SELECT_PHOTO && resultCode == getActivity().RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                photo = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(photo != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            UploadDialogListener listener = (UploadDialogListener) getActivity();
            listener.onSelectImageDialog(byteArray);
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
