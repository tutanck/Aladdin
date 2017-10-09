package com.aj.aladdin.tools.components.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

    private static final String DEFAULT_DRAWABLE_ID = "DEFAULT_DRAWABLE_ID";
    private static final String IMG_REF_STR = "IMG_REF_STR";
    private static final String EDITABLE = "EDITABLE";

    private StorageReference imageRef;

    private ImageView imageView;

    private Uri downloadUrl = null;

    private ProgressBarFragment progressBarFragment;


    public static ImageFragment newInstance(
            String imgRefStr
            , int defaultDrawableID
            , boolean editable
    ) {
        Bundle args = new Bundle();
        args.putString(IMG_REF_STR, imgRefStr);
        args.putInt(DEFAULT_DRAWABLE_ID, defaultDrawableID);
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

        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

        imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(getArguments().getInt(DEFAULT_DRAWABLE_ID));

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
        progressBarFragment.show();
        //// TODO: 09/10/2017 Optimize: shouldnt always reload img see how to store img loxally
        if (downloadUrl != null) onURLReady();
        else
            imageRef.getDownloadUrl().addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBarFragment.hide();
                }
            }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {// Got the download URL for 'users/me/profile.png'
                    downloadUrl = uri;
                    onURLReady();
                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                upload(bitmap);
            } catch (IOException e) {
                __.showShortToast(getActivity(), "Erreur de récupération de l'image!");
                e.printStackTrace();
            }
    }


    private void upload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);

        progressBarFragment.show();

        uploadTask.addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBarFragment.hide();
                __.showShortToast(getContext(), "Echec de la mise à jour de l'image.");
            }
        }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUrl = taskSnapshot.getDownloadUrl();
                onURLReady();
            }
        });
    }


    private void onURLReady() {

        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                progressBarFragment.hide();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                progressBarFragment.hide();
            }
        });


      //  Glide.with(getContext()).load(downloadUrl).listener(requestListener).into(imageView); // TODO: 09/10/2017 manage errors
        //todo put in glide listener
    }




     /* RequestListener<Uri, GlideDrawable> requestListener = new RequestListener<Uri, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
            progressBarFragment.hide();
            return false; // important to return false so the error placeholder can be placed
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            progressBarFragment.hide();
            return false;
        }
    };*/

}