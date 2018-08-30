package id.co.ale.rainbowDev;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.proxy.avatar.IAvatarProxy;
import com.ale.infra.proxy.users.IUserProxy;
import com.ale.rainbowsdk.RainbowSdk;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jsibbold.zoomage.ZoomageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import id.co.ale.rainbowDev.Helpers.Util;
import id.co.ale.rainbowDev.R;
import jp.wasabeef.blurry.Blurry;

public class ProfileActivity extends AppCompatActivity {
    private IRainbowContact profile;
    private ImageView contactPhoto;
    private TextView contactName;
    private TextView contactStatus;
    private TextView contactJob;
    private TextView contactCorp;
    private ImageView editContactPhoto;
    private ImageView bannerImage;

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editJob;
    private EditText editCompany;

    RelativeLayout displayLayout;
    LinearLayout editLayout;

    boolean onEditing = false;
    private FloatingActionButton btnEdit;
    private Dialog dialog;
    private ZoomageView dialogImage;
    private ProgressBar uploadPhotoLoading;
    private ImageView qrCodeDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.displayLayout = (RelativeLayout) findViewById(R.id.display_layout);
        this.editLayout = (LinearLayout) findViewById(R.id.edit_layout);
        this.bannerImage = (ImageView) findViewById(R.id.banner_image);
        this.contactName = (TextView) findViewById(R.id.participant_name);
        this.contactPhoto = (ImageView) findViewById(R.id.contact_photo);

        //QR CODE
        this.qrCodeDisplay = (ImageView) findViewById(R.id.qrcode_view);
        QRCodeWriter writer = new QRCodeWriter();
        try {
            String qrContent =
                    RainbowSdk.instance().myProfile().getConnectedUser().getCorporateId()
                            + ":"
                            + RainbowSdk.instance().myProfile().getConnectedUser().getMainEmailAddress();

            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap qrBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            int [] pallette = {
                    Color.parseColor("#3498db"),
                    Color.parseColor("#e74c3c"),
                    Color.parseColor("#8e44ad"),
                    Color.parseColor("#e67e22"),
                    Color.parseColor("#16a085"),
            };

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int idx = (int) x / (200 / pallette.length);
                    if(idx >= pallette.length) idx = pallette.length - 1;
                    int c = pallette [idx];

//                    c = Color.BLACK;

                    qrBmp.setPixel(x, y, bitMatrix.get(x, y) ? c : Color.parseColor("#ffffff"));
                }
            }
            this.qrCodeDisplay.setImageBitmap(qrBmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        //---------

        dialog = new Dialog(this, R.style.dialogImageFull);
        dialog.setContentView(R.layout.image_dialog);
        dialogImage = (ZoomageView) dialog.findViewById(R.id.fullscreen_image);
        dialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        this.contactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bmp = profile.getPhoto();
                if(bmp != null){
                    dialogImage.setImageBitmap(bmp);
                    dialog.show();
                }
            }
        });

        this.editContactPhoto = (ImageView) findViewById(R.id.btn_upload_photo);
        this.editContactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setRequestedSize(300,600)
                        .setMinCropResultSize(300,600)
                        .setCropMenuCropButtonTitle(getString(R.string.save))
                        .setBorderLineColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary))
                        .setAutoZoomEnabled(false)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });
        uploadPhotoLoading = (ProgressBar) findViewById(R.id.upload_photo_loading);

        this.contactStatus = (TextView) findViewById(R.id.contact_status);
        this.contactJob = (TextView) findViewById(R.id.contact_job);
        this.contactCorp = (TextView) findViewById(R.id.contact_corp);

        this.editFirstName = (EditText) findViewById(R.id.edit_first_name);
        this.editLastName = (EditText) findViewById(R.id.edit_last_name);
        this.editJob = (EditText) findViewById(R.id.edit_job);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //-------------
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        this.profile = RainbowSdk.instance().myProfile().getConnectedUser();
        updateProfile();
        this.profile.registerChangeListener(contactListener);

        this.btnEdit = (FloatingActionButton) findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(onEditing){
                    RainbowSdk.instance().myProfile().updateFirstName(editFirstName.getText().toString(), new IUserProxy.IUsersListener() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onFailure(RainbowServiceException e) {}
                    });
                    RainbowSdk.instance().myProfile().updateLastName(editLastName.getText().toString(), new IUserProxy.IUsersListener() {
                        @Override
                        public void onSuccess() { }

                        @Override
                        public void onFailure(RainbowServiceException e) { }
                    });
                    RainbowSdk.instance().myProfile().updateJobTitle(editJob.getText().toString(), new IUserProxy.IUsersListener() {
                        @Override
                        public void onSuccess() { }

                        @Override
                        public void onFailure(RainbowServiceException e) { }
                    });
                }

                onEditing = !onEditing;
                editLayout.setVisibility(onEditing ? View.VISIBLE : View.GONE);
                displayLayout.setVisibility(!onEditing ? View.VISIBLE : View.GONE);
                btnEdit.setImageResource(onEditing ? R.drawable.ic_save : R.drawable.ic_edit);
            }
        });
    }

    private void updateProfile(){
        this.contactName.setText(this.profile.getFirstName()+" "+this.profile.getLastName());
        this.editFirstName.setText(this.profile.getFirstName());
        this.editLastName.setText(this.profile.getLastName());

        Bitmap bmp = this.profile.getPhoto();
        if(bmp != null){
            this.contactPhoto.setImageBitmap(Util.getRoundedCornerBitmap(bmp, bmp.getWidth()));
            Blurry.with(getBaseContext()).from(bmp).into(bannerImage);
        }else{
            String initName = "?";
            try{
                initName = this.profile.getFirstName().substring(0,1)+this.profile.getLastName().substring(0,1);
            }catch (Exception e){}
            this.contactPhoto.setImageDrawable(TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.parseColor("#F26722"))
                    .endConfig()
                    .buildRound(initName, Color.WHITE));
        }

        this.contactStatus.setText(Util.presenceStatus(this.profile.getPresence().getPresence()));

        this.contactJob.setText(this.profile.getJobTitle().length() > 0 ? this.profile.getJobTitle() : "-");
        this.editJob.setText(this.profile.getJobTitle());

        this.contactCorp.setText(this.profile.getCompanyName().length() > 0 ? this.profile.getCompanyName() : "-");
        this.contactCorp.setText(this.profile.getCompanyName());
    }

    private Contact.ContactListener contactListener = new Contact.ContactListener() {
        @Override
        public void contactUpdated(Contact contact) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateProfile();
                }
            });
        }

        @Override
        public void onPresenceChanged(Contact contact, RainbowPresence rainbowPresence) {}

        @Override
        public void onActionInProgress(boolean b) {}
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                String uri = resultUri.toString().replace("file://", "" );
                File filePhoto = new File(uri);

                uploadPhotoLoading.setVisibility(View.VISIBLE);
                editContactPhoto.setVisibility(View.GONE);
                RainbowSdk.instance().myProfile().updatePhoto(filePhoto, new IAvatarProxy.IAvatarListener() {
                    @Override
                    public void onAvatarSuccess(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateProfile();
                                uploadPhotoLoading.setVisibility(View.GONE);
                                editContactPhoto.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onAvatarFailure(RainbowServiceException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadPhotoLoading.setVisibility(View.GONE);
                                editContactPhoto.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        }
    }
}