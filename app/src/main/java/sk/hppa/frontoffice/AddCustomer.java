package sk.hppa.frontoffice;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddCustomer extends Activity {


    String pathCustomerPic = null, custFOID = "", emailRecipient = "";;
    static final int PIC_GET_FROM_GALLERY  = 3;
    static final int PIC_GET_FROM_CAMERA = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(AddCustomer.this);
        custFOID = mDbHelper.getMetadataByKey("FOID");
        emailRecipient = mDbHelper.getMetadataByKey("emailRecipient");

        final Spinner spinnerCustomer = (Spinner) findViewById(R.id.spinnerCustomers);
        final Button btnSend = (Button) findViewById(R.id.btnAddCustomer);
        final Button btnEmail = (Button) findViewById(R.id.btnEmail);
        final Button btnGoOcr = (Button) findViewById(R.id.btnGooOcr);
        final EditText eCustomer = (EditText) findViewById(R.id.edtCustomer);
        final ImageView ivCustomerPhoto = (ImageView) findViewById(R.id.ivCustomerPic);

        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getCustomers());
        spinnerCustomer.setAdapter(adapterCustomer);
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList al = mDbHelper.getCustomerIDsByName(spinnerCustomer.getSelectedItem().toString());
                al = mDbHelper.getCustomerByID(al.get(0).toString());
                eCustomer.setText(spinnerCustomer.getSelectedItem().toString());
                ivCustomerPhoto.setImageBitmap(BitmapFactory.decodeFile(al.get(3).toString()));
                btnSend.setText(R.string.lblUpdateCustomer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    String mCustomer = eCustomer.getText().toString()
                            .replaceAll("^\\s+", "")
                            .replaceAll("\\s+", "_");

                    ArrayList al = mDbHelper.getCustomerIDsByName(mCustomer);

                    String bodyText = "Customer: " + mCustomer + "\n";

                    if (al.size() > 0) {
                        //String custID = spinnerCustomer.getSelectedItem().toString().split(" ")[0];
                        //mDbHelper.updateCustomerByID(custID, mCustomer, null);
                    } else {
                        String mFOID = custFOID + System.currentTimeMillis();
                        mDbHelper.insertCustomer(mCustomer, mFOID, pathCustomerPic);
                        sendEmail(emailRecipient, "New customer: " + mFOID, bodyText, pathCustomerPic, null);

                    }
                    Toast.makeText(AddCustomer.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(AddCustomer.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList al = mDbHelper.getCustomerIDsByName(eCustomer.getText().toString());
                String bodyText = "Customer: " + eCustomer.getText().toString() + "\n";

                if (al.size() > 0) {
                    al = mDbHelper.getCustomerByID(al.get(0).toString());
                    sendEmail(emailRecipient, "Customer Update: " + al.get(1).toString(), bodyText, al.get(3).toString(), null);
                } else {
                    sendEmail(emailRecipient, "New Customer: " + custFOID, bodyText, pathCustomerPic, null);
                }

            }

        });

        ivCustomerPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                getImageFromGallery(PIC_GET_FROM_GALLERY);
            }
        });
        ivCustomerPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pathCustomerPic = getImageFromCamera(PIC_GET_FROM_CAMERA);
                return false;
            }
        });


        btnGoOcr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    Bitmap bitmap = ((BitmapDrawable)ivCustomerPhoto.getDrawable()).getBitmap();
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(AddCustomer.this).build();
                    Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                    String s = new String();
                    for (int i = 0; i < textBlocks.size(); i++) {
                        TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                        s = s + " " + textBlock.getValue();
                    }
                    eCustomer.setText(s);
                } catch (Exception ex) {
                    Toast.makeText(AddCustomer.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
            }
        });
        eCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ivCustomerPhoto.setImageBitmap(null);
                btnSend.setText(R.string.lblAddCust);
            }
        });
        eCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eCustomer.setText("");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView ivCustomerPhoto = (ImageView) findViewById(R.id.ivCustomerPic);

        if (requestCode == PIC_GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pathCustomerPic = cursor.getString(columnIndex);
            cursor.close();

            ivCustomerPhoto.setImageBitmap(BitmapFactory.decodeFile(pathCustomerPic));
        }
        if (requestCode == PIC_GET_FROM_CAMERA && resultCode == RESULT_OK) {
            ivCustomerPhoto.setImageBitmap(BitmapFactory.decodeFile(pathCustomerPic));
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
                Uri photoURI = FileProvider.getUriForFile(AddCustomer.this, "sk.hppa.frontoffice.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IntentCode);
            }
        }
        return result;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + custFOID + "_";
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
            Toast.makeText(AddCustomer.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
