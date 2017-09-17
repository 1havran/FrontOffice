package sk.hppa.frontoffice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class BuySell extends Activity {

    String lastFOID = "";
    String lastEmail = "";
    String emailRecipient = "", tnxFOID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);

        final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(BuySell.this);
        emailRecipient = mDbHelper.getMetadataByKey("emailRecipient");
        tnxFOID = mDbHelper.getMetadataByKey("FOID");

        final Button mBtnSend = (Button) findViewById(R.id.btnSend);
        final Button mBtnEmail = (Button) findViewById(R.id.btnEmail);
        final EditText mQuantity1 = (EditText) findViewById(R.id.edtQuantity);
        final EditText mQuantity2 = (EditText) findViewById(R.id.edtQuantity2);
        final EditText mQuantity3 = (EditText) findViewById(R.id.edtQuantity3);
        final EditText mSellerPrice1 = (EditText) findViewById(R.id.edtSellerPrice1);
        final EditText mSellerPrice2 = (EditText) findViewById(R.id.edtSellerPrice2);
        final EditText mSellerPrice3 = (EditText) findViewById(R.id.edtSellerPrice3);
        final EditText mBuyerPrice1 = (EditText) findViewById(R.id.edtBuyerPrice1);
        final EditText mBuyerPrice2 = (EditText) findViewById(R.id.edtBuyerPrice2);
        final EditText mBuyerPrice3 = (EditText) findViewById(R.id.edtBuyerPrice3);
        final EditText mDescription = (EditText) findViewById(R.id.edtDescription);
        final Spinner mSpinnerSeller = (Spinner) findViewById(R.id.spinnerSeller);
        final Spinner mSpinnerBuyer = (Spinner) findViewById(R.id.spinnerBuyer);
        final Spinner mSpinnerGoods1 = (Spinner) findViewById(R.id.spinnerGoods1);
        final Spinner mSpinnerGoods2 = (Spinner) findViewById(R.id.spinnerGoods2);
        final Spinner mSpinnerGoods3 = (Spinner) findViewById(R.id.spinnerGoods3);

        ArrayAdapter<String> adapterCustomers = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getCustomers());
        ArrayAdapter<String> adapterGoods = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getGoods());


        mSpinnerSeller.setAdapter(adapterCustomers);
        mSpinnerBuyer.setAdapter(adapterCustomers);
        mSpinnerGoods1.setAdapter(adapterGoods);
        mSpinnerGoods2.setAdapter(adapterGoods);
        mSpinnerGoods3.setAdapter(adapterGoods);


        mSpinnerGoods1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList GoodsIDs = mDbHelper.getGoodsIDsByName(mSpinnerGoods1.getSelectedItem().toString());
                Double d = mDbHelper.getAvailableGoodsQuantity(GoodsIDs.get(0).toString());
                mQuantity1.setText(d.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinnerGoods2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList GoodsIDs = mDbHelper.getGoodsIDsByName(mSpinnerGoods2.getSelectedItem().toString());
                Double d = mDbHelper.getAvailableGoodsQuantity(GoodsIDs.get(0).toString());
                mQuantity2.setText(d.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSpinnerGoods3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList GoodsIDs = mDbHelper.getGoodsIDsByName(mSpinnerGoods3.getSelectedItem().toString());
                Double d = mDbHelper.getAvailableGoodsQuantity(GoodsIDs.get(0).toString());
                mQuantity3.setText(d.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDescription.setText("");
            }
        });

        mBtnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! lastFOID.equals("") && ! lastEmail.equals("")) {

                    sendEmail(emailRecipient, "New order: " + lastFOID, lastEmail, null, null);
                } else {
                    Toast.makeText(BuySell.this, "No information about last transaction.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {

                    String sellerID, buyerID, description, bodyText;
                    Boolean isDeal1, isDeal2, isDeal3;
                    int subTnx = 0;

                    isDeal1 = ! mSellerPrice1.getText().toString().equals("") && ! mBuyerPrice1.getText().toString().equals("");
                    isDeal2 = ! mSellerPrice2.getText().toString().equals("") && ! mBuyerPrice2.getText().toString().equals("");
                    isDeal3 = ! mSellerPrice3.getText().toString().equals("") && ! mBuyerPrice3.getText().toString().equals("");

                    if (isDeal1 || isDeal2|| isDeal3) {
                        sellerID = mDbHelper.getCustomerIDsByName(mSpinnerSeller.getSelectedItem().toString()).get(0).toString();
                        buyerID = mDbHelper.getCustomerIDsByName(mSpinnerBuyer.getSelectedItem().toString()).get(0).toString();
                        description = mDescription.getText().toString();
                        lastFOID = tnxFOID + System.currentTimeMillis();

                        bodyText = "Front office Transaction ID: " + lastFOID + "\n\n"
                                + "Seller: " + mSpinnerSeller.getSelectedItem().toString() + "\n"
                                + "Buyer: " + mSpinnerBuyer.getSelectedItem().toString() + "\n"
                                + "Description: " + mDescription.getText() + "\n";

                        if (isDeal1) {
                            subTnx += 1;
                            setGoodsDeal(mDbHelper, mSpinnerGoods1, mQuantity1, mSellerPrice1, mBuyerPrice1, sellerID, buyerID, lastFOID + "_" + subTnx, description);
                            bodyText = bodyText + "\n" + getGoodsTemplateMessage(mSpinnerGoods1, mQuantity1, mSellerPrice1, mBuyerPrice1);
                        }
                        if (isDeal2) {
                            subTnx += 1;
                            setGoodsDeal(mDbHelper, mSpinnerGoods2, mQuantity2, mSellerPrice2, mBuyerPrice2, sellerID, buyerID, lastFOID + "_" + subTnx, description);
                            bodyText = bodyText + "\n" + getGoodsTemplateMessage(mSpinnerGoods2, mQuantity2, mSellerPrice2, mBuyerPrice2);
                        }
                        if (isDeal3) {
                            subTnx += 1;
                            setGoodsDeal(mDbHelper, mSpinnerGoods3, mQuantity3, mSellerPrice3, mBuyerPrice3, sellerID, buyerID, lastFOID + "_" + subTnx, description);
                            bodyText = bodyText + "\n" + getGoodsTemplateMessage(mSpinnerGoods3, mQuantity3, mSellerPrice3, mBuyerPrice3);
                        }
                        lastEmail = bodyText;
                        Toast.makeText(BuySell.this, "Done", Toast.LENGTH_SHORT).show();
                        sendEmail(emailRecipient, "New order: " + lastFOID, bodyText, null, null);
                    } else {
                        Toast.makeText(BuySell.this, "Seller and Buyer price is not set for any of the goods.\nTransaction is not sent!", Toast.LENGTH_SHORT).show();
                    }


            }
        });

    }


    private String getGoodsTemplateMessage(Spinner g, EditText q, EditText s, EditText b){
        String result = "Goods: " + g.getSelectedItem().toString() + "\n"
                + "Tons: " + q.getText() + "\n"
                + "Seller Price: " + s.getText() + "\n"
                + "Buyer Price: " + b.getText() + "\n";
        return result;
    }

    private Boolean setGoodsDeal(FrontOfficeDbHelper mDbHelper, Spinner g, EditText q, EditText s, EditText b, String sellerID, String buyerID, String tnxFOID, String description) {
        String goodsID = mDbHelper.getGoodsIDsByName(g.getSelectedItem().toString()).get(0).toString();
        Double d = Double.parseDouble(q.getText().toString());
        mDbHelper.insertTnx(sellerID, buyerID, goodsID, d, "Tons", description, tnxFOID, s.getText().toString(), b.getText().toString());
        d = mDbHelper.getAvailableGoodsQuantity(goodsID) - d;
        mDbHelper.updateQuantityOfGoods(goodsID, d);
        return true;
    }

    public void sendEmail(String recipient, String subject, String bodyText, String pathPic1, String pathPic2 ) {
        try {
            final Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, bodyText);
            if (pathPic1 != null) {
                i.putExtra(Intent.EXTRA_STREAM, pathPic1);
            }
            if (pathPic2 != null) {
                i.putExtra(Intent.EXTRA_STREAM, pathPic2);
            }
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BuySell.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
