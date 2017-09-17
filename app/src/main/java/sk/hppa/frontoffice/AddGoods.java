package sk.hppa.frontoffice;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddGoods extends Activity {


    static final int DEAL_GET_FROM_GALLERY = 1;
    static final int DEAL_GET_FROM_CAMERA  = 2;
    static final int PIC_GET_FROM_GALLERY  = 3;
    static final int PIC_GET_FROM_CAMERA = 4;


    String emailRecipient = "", goodsFOID = "", pathDealPicture = null, pathDealAgreement = null, bodyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);

        final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(AddGoods.this);
        emailRecipient = mDbHelper.getMetadataByKey("emailRecipient");
        goodsFOID = mDbHelper.getMetadataByKey("FOID");

        final EditText eGoods = (EditText) findViewById(R.id.edtGoodsName);
        final EditText eQuantity = (EditText) findViewById(R.id.edtQuantity);
        final Spinner spinnerGoods = (Spinner) findViewById(R.id.spinnerGoods1);
        final Button btnSend = (Button) findViewById(R.id.btnAddGoods);
        final Button btnGoodsEmail = (Button) findViewById(R.id.btnGoodsEmail);
        final ImageView ivGoodsDeal = (ImageView) findViewById(R.id.ivGoodsDeal);
        final ImageView ivGoodsPicture = (ImageView) findViewById(R.id.ivGoodsPicture);

        final ArrayAdapter<String> adapterGoods = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getGoods());
        spinnerGoods.setAdapter(adapterGoods);
        spinnerGoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ArrayList al = mDbHelper.getGoodsIDsByName(spinnerGoods.getSelectedItem().toString());
                    al = mDbHelper.getGoodsByID(al.get(0).toString());
                    eGoods.setText(al.get(1).toString());
                    eQuantity.setText(al.get(3).toString());
                    ivGoodsDeal.setImageBitmap(BitmapFactory.decodeFile(al.get(4).toString()));
                    ivGoodsPicture.setImageBitmap(BitmapFactory.decodeFile(al.get(5).toString()));
                    btnSend.setText(R.string.lblUpdateGoods);
                } catch (Exception ex) {
                    //
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ivGoodsDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery(DEAL_GET_FROM_GALLERY);
            }
        });
        ivGoodsDeal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pathDealAgreement = getImageFromCamera(DEAL_GET_FROM_CAMERA);
                return false;
            }
        });
        ivGoodsPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery(PIC_GET_FROM_GALLERY);
            }
        });
        ivGoodsPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pathDealPicture = getImageFromCamera(PIC_GET_FROM_CAMERA);
                return false;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    String mGoodsText = eGoods.getText().toString()
                            .replaceAll("^\\s+", "")
                            .replaceAll("\\s+", "_");

                    ArrayList al = mDbHelper.getGoodsIDsByName(mGoodsText);
                    Double d = Double.parseDouble(eQuantity.getText().toString());
                    bodyText = "Goods: " + mGoodsText + "\n"
                            + "Tons: " + eQuantity.getText().toString();

                    if (al.size() > 0) {
                        al = mDbHelper.getGoodsByID(al.get(0).toString());
                        mDbHelper.updateGoods(al.get(0).toString(), al.get(6).toString(), d, "Tons", pathDealAgreement, pathDealPicture);
                        sendEmail(emailRecipient, "Goods update: " + al.get(6).toString(), bodyText, pathDealAgreement, pathDealPicture);
                    } else {
                        String mFOID = goodsFOID + System.currentTimeMillis();
                        mDbHelper.insertGoods(mGoodsText, mFOID, d, "Tons", pathDealAgreement, pathDealPicture);
                        sendEmail(emailRecipient, "New Goods: " + mFOID, bodyText, pathDealAgreement, pathDealPicture);
                    }
                    Toast.makeText(AddGoods.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    //Toast.makeText(MainActivity.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
        btnGoodsEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList al = mDbHelper.getGoodsIDsByName(eGoods.getText().toString());
                bodyText = "Goods: " + eGoods.getText().toString() + "\n"
                        + "Tons: " + eQuantity.getText().toString();
                if (al.size() > 0) {
                    al = mDbHelper.getGoodsByID(al.get(0).toString());
                    sendEmail(emailRecipient, "Goods update: " + al.get(6).toString(), bodyText, pathDealAgreement, pathDealPicture);
                } else {
                    sendEmail(emailRecipient, "New Goods: " + goodsFOID, bodyText, pathDealAgreement, pathDealPicture);
                }

            }
        });
        eGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eGoods.setText("");
            }
        });
        eQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eQuantity.setText("");
            }
        });
        eGoods.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ivGoodsDeal.setImageBitmap(null);
                ivGoodsPicture.setImageBitmap(null);
                btnSend.setText(R.string.lblAddGoods);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView ivGoods;
        String currentPhotoPath;

        if ((requestCode == DEAL_GET_FROM_GALLERY || requestCode == PIC_GET_FROM_GALLERY ) && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            currentPhotoPath = cursor.getString(columnIndex);
            cursor.close();

            if (requestCode == DEAL_GET_FROM_GALLERY) {
                ivGoods = (ImageView) findViewById(R.id.ivGoodsDeal);
                pathDealAgreement = currentPhotoPath;
            } else {
                ivGoods = (ImageView) findViewById(R.id.ivGoodsPicture);
                pathDealPicture = currentPhotoPath;
            }
            ivGoods.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }

        if ((requestCode == DEAL_GET_FROM_CAMERA || requestCode == PIC_GET_FROM_CAMERA) && resultCode == RESULT_OK) {
            if (requestCode == DEAL_GET_FROM_CAMERA) {
                ivGoods = (ImageView) findViewById(R.id.ivGoodsDeal);
                currentPhotoPath = pathDealAgreement;
            } else {
                ivGoods = (ImageView) findViewById(R.id.ivGoodsPicture);
                currentPhotoPath = pathDealPicture;
            }
            ivGoods.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
    }

    public void getImageFromGallery(int IntentCode) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IntentCode);
    }

    public String getImageFromCamera(int IntentCode) {
        String result = "";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                result = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(AddGoods.this, "sk.hppa.frontoffice.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IntentCode);
            }
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + goodsFOID + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public void sendEmail(String recipient, String subject, String bodyText, String pathPic1, String pathPic2 ) {
        try {
            final Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, bodyText);
            ArrayList<Uri> uris = new ArrayList<Uri>();
            if (pathPic1 != null) {
                File f = new File(pathPic1);
                Uri u = Uri.fromFile(f);
                uris.add(u);
            }
            if (pathPic2 != null) {
                uris.add(Uri.fromFile(new File(pathPic2)));
            }
            i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AddGoods.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
