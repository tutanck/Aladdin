package com.aj.aladdin.tools.components.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.utils.__;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class ImageFragment extends Fragment {

    public static final int PICK_IMAGE_REQUEST = 1;

    private static final String IMG_REF_STR = "IMG_REF_STR";
    private static final String EDITABLE = "EDITABLE";

    private StorageReference imageRef;

    private ImageView imageView;

    private ProgressBarFragment progressBarFragment;


    public static ImageFragment newInstance(
            String imgRefStr
            , boolean editable
    ) {
        Bundle args = new Bundle();
        args.putString(IMG_REF_STR, imgRefStr);
        args.putBoolean(EDITABLE, editable);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child(getArguments().getString(IMG_REF_STR));

        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_image_view, container, false);
        imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_person_profile_large);

        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

        if (getArguments().getBoolean(EDITABLE))
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }
                    }
            );
        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                upload();
            } catch (IOException e) {
                __.showShortToast(getActivity(), "Erreur de récupération de l'image!");
                e.printStackTrace();
            }
    }


    private void upload() {
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        progressBarFragment.show();

        UploadTask uploadTask = imageRef.putBytes(data);

        /* https://firebase.google.com/docs/storage/android/upload-files
        Handle Activity Lifecycle Changes :
        Uploads continue in the background even after activity lifecycle changes (such as presenting a dialog or rotating the screen).
         Any listeners you had attached will also remain attached.
          This could cause unexpected results if they get called after the activity is stopped.
           You can solve this problem by subscribing your listeners with an activity scope to automatically unregister them when the activity stops. */

        uploadTask.addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBarFragment.hide();
                imageView.setImageResource(R.drawable.ic_person_profile_large);
                __.showShortToast(getContext(), "Echec de la mise à jour de l'image de profil.");
            }
        }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBarFragment.hide();
                __.showShortToast(getContext(), "Mise à jour réussie.");
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

}