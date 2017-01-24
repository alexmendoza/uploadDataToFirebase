package ventura.mendoza.alex.uploadaudioimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ImageButton mRecordBtn;
    private TextView mRecordLabel;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_Log";
    private StorageReference mStorageAudio;
    private ProgressDialog mProgress;
    //image
    private StorageReference mStorageImage;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    //Selectable image
    private ImageButton mSelectableImage;
    //ImageView
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST=200;
    private static final int CAMERA_REQUEST_CODE = 300;
    private Uri filepath;
    private StorageReference Storage;
    private Button btnSend;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Audio
        mStorageAudio = FirebaseStorage.getInstance().getReference();

        mRecordLabel = (TextView)findViewById(R.id.recordLabel);
        mRecordBtn = (ImageButton) findViewById(R.id.mRecordBtn);

        mProgress = new ProgressDialog(this);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName+= "/recorded_audio.3gp";

        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == event.ACTION_DOWN){
                    startRecording();
                    mRecordLabel.setText("Recording Started...");
                }
                else if(event.getAction() == event.ACTION_UP){
                    stopRecording();
                    mRecordLabel.setText("Recording Stopped...");
                }
                return  false;
            }
        });
        //Img
        mStorageImage = FirebaseStorage.getInstance().getReference();

        mProgressDialog = new ProgressDialog(this);

        /****
         * Abrir Cuadro de Dialogo
         */
        mSelectableImage = (ImageButton)findViewById(R.id.mSelectOriginImage);
        mSelectableImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_selectable_image_origin, null);
                final ImageButton btnImagenGallery = (ImageButton) mView.findViewById(R.id.imgBtnGallery);
                final ImageButton btnImageCamera = (ImageButton)mView.findViewById(R.id.imgBtnCamera);


                btnImageCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Haz seleccionado la camera", Toast.LENGTH_SHORT).show();

                    }
                });

                btnImagenGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Haz seleccionado la galeria",Toast.LENGTH_SHORT).show();
                        showFileImage();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
        //ImagenPreview
        imageView = (ImageView)findViewById(R.id.img);
        Storage= FirebaseStorage.getInstance().getReference();
        btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();gi
            }
        });

    }

    /***
     * Cerrar Cuadro de dialogo
     */

    /*****
     * Audio
     */
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }
    private void uploadAudio() {
        mProgress.setMessage("Uploading audio...");
        mProgress.show();
        StorageReference filepath = mStorageAudio.child("Audio").child("new_audio.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                mRecordLabel.setText("Uploading Finished");
            }
        });


    }
    /****
     * Finish audio
     */

    /****
     *Img
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {


            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadFile()
    {

        if (filepath !=null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Cargando...");
            progressDialog.show();
            StorageReference riversRef = Storage.child("images/profile.jpg");

            riversRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Archivo cargado", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress)+"% Cargando...");
                }
            });
        }
    }

    private void  showFileImage()
    {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "seleccionar imagen"), PICK_IMAGE_REQUEST);
    }
    /****
     * Finish Img
     */







}
