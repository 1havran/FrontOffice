package sk.hppa.frontoffice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class BuySell extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell);

        final FrontOfficeDbHelper mDbHelper = new FrontOfficeDbHelper(BuySell.this);

        ArrayAdapter<String> adapterCustomers = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getCustomers());
        ArrayAdapter<String> adapterGoods = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, mDbHelper.getGoods());

        final EditText eQuantity = (EditText) findViewById(R.id.edtQuantity);
        final EditText eUnit = (EditText) findViewById(R.id.edtUnit);
        final EditText eDescription = (EditText) findViewById(R.id.editTextOptional);
        final Button btnSend = (Button) findViewById(R.id.btnSend);
        final Spinner spinnerSeller = (Spinner) findViewById(R.id.spinnerSeller);
        final Spinner spinnerBuyer = (Spinner) findViewById(R.id.spinnerBuyer);
        final Spinner spinnerGoods = (Spinner) findViewById(R.id.spinnerGoods);

        spinnerSeller.setAdapter(adapterCustomers);
        spinnerBuyer.setAdapter(adapterCustomers);
        spinnerGoods.setAdapter(adapterGoods);

        spinnerGoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String GoodsID = spinnerGoods.getSelectedItem().toString().split(" ")[0];

                    Double d = mDbHelper.getAvailableGoodsQuantity(GoodsID);
                    String s = mDbHelper.getAvailableGoodsUnit(GoodsID);


                    eQuantity.setText(d.toString());


                    eUnit.setText(s);

                    //Toast.makeText(BuySell.this, d.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(BuySell.this, (CharSequence) ex, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                try {

                    String sellerID = spinnerSeller.getSelectedItem().toString().split(" ")[0];
                    String buyerID = spinnerBuyer.getSelectedItem().toString().split(" ")[0];
                    String goodsID = spinnerGoods.getSelectedItem().toString().split(" ")[0];
                    Double d = Double.parseDouble(eQuantity.getText().toString());
                    Double availableQuantity = mDbHelper.getAvailableGoodsQuantity(goodsID) - d;
                    Toast.makeText(BuySell.this, availableQuantity.toString(), Toast.LENGTH_LONG).show();
                    String tnxFOID = "DD" + System.currentTimeMillis();

                    mDbHelper.insertTnx(sellerID, buyerID, goodsID, d,
                            eUnit.getText().toString(), eDescription.getText().toString(), tnxFOID );
                    mDbHelper.updateQuantityOfGoods(goodsID, availableQuantity);

                    String bodyText = "Seller: " + spinnerSeller.getSelectedItem().toString() + "\n"
                            + "Buyer: " + spinnerBuyer.getSelectedItem().toString() + "\n"
                            + "Goods: " + spinnerGoods.getSelectedItem().toString() + "\n"
                            + "Quantity: " + eQuantity.getText().toString() + "\n"
                            + "Unit: " + eUnit.getText().toString() + "\n"
                            + "Description: " + eDescription.getText().toString() + "\n\n";

                    final Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "New Order");
                    i.putExtra(Intent.EXTRA_TEXT, bodyText);
                    //i.putExtra(Intent.EXTRA_TEXT, "Free Text: " + editTextOptional.text;
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                   // Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
