package sk.hppa.frontoffice;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

public class AddCustomer extends AppCompatActivity {
    final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(AddCustomer.this);


    static final int REQUEST_IMAGE_CAPTURE = 1;
    TessOCR mTessOCR = new TessOCR(AddCustomer.this, "eng");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);


        final Spinner spinnerCustomer = (Spinner) findViewById(R.id.spinnerCustomers);
        final Button btnSend = (Button) findViewById(R.id.btnAddCustomer);
        final Button btnOcr = (Button) findViewById(R.id.btnOcr);
        final Button btnGoOcr = (Button) findViewById(R.id.btnGooOcr);
        final EditText eCustomer = (EditText) findViewById(R.id.edtCustomer);

        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getCustomers());
        spinnerCustomer.setAdapter(adapterCustomer);
        spinnerCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String CustomerLabel = spinnerCustomer.getSelectedItem().toString().split(" ")[1];
                eCustomer.setText(CustomerLabel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    String mCustomer = eCustomer.getText().toString()
                            .replaceAll(" ", "_");
                    ArrayList al = mDbHelper.getCustomerIDsByName(mCustomer);

                    if (al.size() > 0) {
                        String custID = spinnerCustomer.getSelectedItem().toString().split(" ")[0];
                        mDbHelper.updateCustomerByID(custID, mCustomer, null);
                    } else {
                        String custFOID = "DD" + System.currentTimeMillis();
                        mDbHelper.insertCustomer(mCustomer, custFOID, null);
                    }
                    Toast.makeText(AddCustomer.this, "Done", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(AddCustomer.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        final ImageView ivCustomerPhoto = (ImageView) findViewById(R.id.ivCustomerPic);
        ivCustomerPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnOcr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {
                    Bitmap bitmap = ((BitmapDrawable)ivCustomerPhoto.getDrawable()).getBitmap();
                    doOCR(bitmap);
                } catch (Exception ex) {
                    Toast.makeText(AddCustomer.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bitmap newImage = imageBitmap.copy(Bitmap.Config.ARGB_8888,true);

            ImageView ivCustomerPhoto = (ImageView) findViewById(R.id.ivCustomerPic);
            ivCustomerPhoto.setImageBitmap(newImage);
        }
    }


    private void doOCR (final Bitmap bitmap) {
        final EditText eCustomer = (EditText) findViewById(R.id.edtCustomer);
        String srcText = mTessOCR.getOCRResult(bitmap);
        if (srcText != null && !srcText.equals("")) {
            eCustomer.setText(srcText);
        }
        Toast.makeText(AddCustomer.this, srcText, Toast.LENGTH_LONG).show();

    }
 }
